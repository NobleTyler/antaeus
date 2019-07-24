package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice
import mu.KotlinLogging
import java.io.File

class BillingService(private val invoiceService:InvoiceService, private val customerService: CustomerService):PaymentProvider {
    private val logger = KotlinLogging.logger("billingService")
    private var invoices = invoiceService.fetchAll()
    private   var customers= customerService.fetchAll()

 //Works by charging each invoice in a for each loop and doing error handling beyond the parent class
    fun chargeSignal():Boolean{
        var status = true
        //This is required for normal mode inorder to refresh
        invoices = invoiceService.fetchAll()
        customers = customerService.fetchAll()

        invoices.forEach{
            try {
                if(charge(it,customers))
                    invoiceService.markPaid(it)
            }
            catch (e :CurrencyMismatchException){
                status = false
                logger.error { "Currency's where mismatched on ${it.id}" }
            }catch (e : CustomerNotFoundException){
                status = false
                logger.error { "Customer was not found on ${it.id}" }
            }catch (e :NetworkException){
                status = false
                logger.error { "Network unstable on transaction ${it.id}" }
            }catch (e :Exception){
                status = false
                logger.error { "${e.cause}" }
            }
        }
        if(status)
            println("Charge complete")
        else
            println("Not all charges complete!")

        return status
    }

}