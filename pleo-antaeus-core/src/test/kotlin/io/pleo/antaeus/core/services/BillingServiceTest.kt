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
    /*private val dal = mockk<AntaeusDal> {
        every { fetchCustomer(404) } returns null
    }*/
    private var dal = mockk<AntaeusDal>{}
    private var invoice = Invoice(id = 1,customerId = 1,amount = Money(131.66.toBigDecimal(),currency = Currency.DKK),status = InvoiceStatus.PENDING)
    lateinit var billingService : BillingService
    lateinit var customerService: CustomerService
    lateinit var invoiceService: InvoiceService

    @Test
    fun `will throw if billing is not found`() {
        assertThrows<CustomerNotFoundException> {

            billingService.chargeSignal()
        }
    }
    @Test
    fun `CurrencyMismatchException test`(){
        assertThrows<CurrencyMismatchException> {

            billingService.charge(invoice,customers = billingService.customers )
        }
    }
    @Test
    fun  `CustomerNotFoundException test`(){
        assertThrows<CustomerNotFoundException> {
            invoice= Invoice(id = 1,customerId = -1,amount = Money(131.66.toBigDecimal(),currency = Currency.DKK),status = InvoiceStatus.PENDING)
            billingService.charge(invoice,customers = billingService.customers )
        }
    }
}