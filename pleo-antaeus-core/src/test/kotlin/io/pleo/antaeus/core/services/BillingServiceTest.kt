package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BillingServiceTest {
    private lateinit var billingService :BillingService

    var invoice = Invoice(id = 1,customerId = 1,amount = Money(131.66.toBigDecimal(),currency = Currency.SEK),status = InvoiceStatus.PENDING)
    var customer = Customer(id = 1,currency = Currency.SEK)
    lateinit  var invoiceService : InvoiceService
    lateinit  var customerService : CustomerService
    @Test
    fun `billing not found test`() {
        assertThrows<UninitializedPropertyAccessException> {
            billingService.charge(invoice, listOf(Customer(0,Currency.SEK)))
        }
    }
    @Test
    fun `already paid test`(){
        invoice = Invoice(id = 1,customerId = 1,amount = Money(131.66.toBigDecimal(),currency = Currency.SEK),status = InvoiceStatus.PAID)
        val dal = mockk<AntaeusDal>{
            every { fetchInvoices() } returns listOf(invoice)
            every { fetchCustomers() } returns listOf(customer)
        }
        customerService= CustomerService(dal)
        invoiceService=InvoiceService(dal)
        billingService = BillingService(invoiceService, customerService)
        assertTrue(billingService.charge(invoice,customerService.fetchAll()))
        }
        @Test
    fun `proper conditions test`(){
        val dal = mockk<AntaeusDal>{
        every { fetchInvoices() } returns listOf(invoice)
        every { fetchCustomers() } returns listOf(customer)
    }
        customerService= CustomerService(dal)
        invoiceService=InvoiceService(dal)
        billingService = BillingService(invoiceService, customerService)
        assertTrue(billingService.charge(invoice,customerService.fetchAll()))
    }
    @Test
    fun `CurrencyMismatchException test`(){
        val invoice = Invoice(id = 1,customerId = 1,amount = Money(131.66.toBigDecimal(),currency = Currency.DKK),status = InvoiceStatus.PENDING)
        val dal = mockk<AntaeusDal>{
            every { fetchInvoices() } returns listOf(invoice)
            every { fetchCustomers() } returns listOf(customer)
        }
        customerService= CustomerService(dal)
        invoiceService=InvoiceService(dal)
        billingService = BillingService(invoiceService, customerService)
        assertThrows<CurrencyMismatchException> {
            billingService.charge(invoice,customerService.fetchAll())
        }
    }
    @Test
    fun  `CustomerNotFoundException test`(){
        val dal = mockk<AntaeusDal>{
            every { fetchInvoices() } returns listOf(invoice)
            every { fetchCustomers() } returns emptyList()
        }
        customerService= CustomerService(dal)
        invoiceService=InvoiceService(dal)
        billingService = BillingService(invoiceService, customerService )

        assertThrows<CustomerNotFoundException> {
            billingService.charge(invoice,customerService.fetchAll())
        }
    }
        @Test
        fun  `No invoices test`(){
            val dal = mockk<AntaeusDal>{
                every { fetchInvoices() } returns emptyList()
                every { fetchCustomers() } returns emptyList()
            }
            customerService= CustomerService(dal)
            invoiceService=InvoiceService(dal)
            billingService = BillingService(invoiceService, customerService )

            assertThrows<CustomerNotFoundException> {
                billingService.charge(invoice,customerService.fetchAll())
            }
    }

}
