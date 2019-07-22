package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BillingServiceTest {
    private val dal = mockk<AntaeusDal> {
        every { fetchCustomer(404) } returns null
    }

    private val billingService = BillingService(dal = dal)

    @Test
    fun `will throw if billing is not found`() {
        assertThrows<CustomerNotFoundException> {
            billingService.chargeSignal()
        }
    }
}