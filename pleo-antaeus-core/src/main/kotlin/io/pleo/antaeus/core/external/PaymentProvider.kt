/*
    This is the payment provider. It is a "mock" of an external service that you can pretend runs on another system.
    With this API you can ask customers to pay an invoice.

    This mock will succeed if the customer has enough money in their balance,
    however the documentation lays out scenarios in which paying an invoice could fail.
 */

package io.pleo.antaeus.core.external

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import java.lang.Exception
import java.net.NetworkInterface

interface PaymentProvider {
    /*
        Charge a customer's account the amount from the invoice.

        Returns:
          `True` when the customer account was successfully charged the given amount.
          `False` when the customer account balance did not allow the charge.

        Throws:
          `CustomerNotFoundException`: when no customer has the given id.
          `CurrencyMismatchException`: when the currency does not match the customer account.
          `NetworkException`: when a network error happens.
     */
    fun  charge(invoice:Invoice,customers:List<Customer>): Boolean {
        var status = true
        val customer = customers.find{it.id == invoice.customerId }
        var networkError = false //TODO actually detect network errors
        if(customer != null){
        if (!invoice.amount.currency.equals(customer.currency)) {
            throw CurrencyMismatchException(invoiceId = invoice.id, customerId = customer.id)
        } else if (networkError) {
            throw NetworkException()
        } else if(invoice.status.equals(InvoiceStatus.PAID)){
            status = false
        }
        else{
            invoice.status.apply { InvoiceStatus.PAID }
            println("Yo this is charge we hit this right?")
        }
        }else
            throw CustomerNotFoundException(invoice.customerId)

        return status
    }
}
