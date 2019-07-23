package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BillingServiceTest {
    private var dal = mockk<AntaeusDal> {
        every { fetchCustomer(404) } returns null
    }
    private val customerService = CustomerService(dal = dal)
    private val invoiceService = InvoiceService(dal=dal)
    private var billingService =BillingService(invoiceService,customerService)
    //TODO figure out why the DAL is unable to fetch anything
    @Test
    fun `will throw if billing is not found`() {
        assertThrows<CustomerNotFoundException> {
            billingService.chargeSignal()
        }
    }
    @Test
    fun `CurrencyMismatchException test`(){
        assertThrows<CurrencyMismatchException> {
            val invoice = Invoice(id = 1,customerId = 1,amount = Money(131.66.toBigDecimal(),currency = Currency.SEK),status = InvoiceStatus.PENDING)
            billingService.charge(invoice,customers = customerService.fetchAll())
        }
    }
    @Test
    fun  `CustomerNotFoundException test`(){
        assertThrows<CustomerNotFoundException> {
        }
    }
}