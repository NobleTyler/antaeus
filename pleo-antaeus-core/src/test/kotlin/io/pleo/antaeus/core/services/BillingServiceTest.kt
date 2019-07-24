package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BillingServiceTest {
    //lateinit variables are to be initialized on a per test basis.
    private lateinit var dal : AntaeusDal

    private lateinit var billingService :BillingService

    var invoice = Invoice(id = 1,customerId = 1,amount = Money(131.66.toBigDecimal(),currency = Currency.SEK),status = InvoiceStatus.PENDING)
    var customer = Customer(id = 1,currency = Currency.SEK)
    var invoices = listOf(invoice)
    var customers = listOf(customer)
    @Test
    fun `will throw if billing is not found`() {
                assertThrows<UninitializedPropertyAccessException> {
            billingService.charge(invoice, listOf(Customer(0,Currency.SEK)))
        }
    }
    @Test
    fun `everything works?`(){
        print(invoice.status)
        billingService = BillingService(listOf(invoice), listOf(customer))
        assertTrue(billingService.chargeSignal())
        print(invoice.status)
    }
    @Test
    fun `CurrencyMismatchException test`(){
        val invoice = Invoice(id = 1,customerId = 1,amount = Money(131.66.toBigDecimal(),currency = Currency.SEK),status = InvoiceStatus.PENDING)
        val customers = listOf(Customer(id = 1,currency = Currency.DKK))
        billingService = BillingService(listOf(invoice), listOf(customer))
        assertThrows<CurrencyMismatchException> {
            billingService.charge(invoice,customers)
        }
    }
    @Test
    fun  `CustomerNotFoundException test`(){
        val invoices = listOf(invoice)
        val customers = emptyList<Customer>()
        billingService = BillingService(invoices, customers )
        assertThrows<CustomerNotFoundException> {
            billingService.charge(invoice,customers)
        }
    }
}
