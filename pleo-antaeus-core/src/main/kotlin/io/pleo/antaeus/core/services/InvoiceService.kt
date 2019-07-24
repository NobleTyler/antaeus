/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice

class InvoiceService(private val dal: AntaeusDal):PaymentProvider {
    fun fetchAll(): List<Invoice> {
        return dal.fetchInvoices()
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }
    fun markPaid(invoice: Invoice){
        dal.markInvoicePaid(invoice)
    }
    fun markAllPaid(invoices: List<Invoice>){
        invoices.forEach {
            dal.markInvoicePaid(it)
        }
    }
    fun markAllPending(invoices: List<Invoice>){
        invoices.forEach {
            dal.markInvoicePending(it)
        }

    }
}
