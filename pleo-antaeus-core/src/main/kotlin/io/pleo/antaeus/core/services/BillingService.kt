package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import mu.KLogger
import mu.KotlinLogging
import java.io.File

class BillingService(private val invoiceService: InvoiceService,private val customerService: CustomerService):PaymentProvider {
    private val logger = KotlinLogging.logger("billingService")

    var customers = customerService.fetchAll().toList()
    var invoices = invoiceService.fetchAll().toList()
    fun chargeSignal():Boolean{
        var status = true
        var success = StringBuilder()
        var failed = StringBuilder()

        var pf= File("PaymentFailures.txt").bufferedWriter()
        var ps =File("PaymentSuccess.txt").bufferedWriter()
        failed.append("List of failed billings below:")
        success.append("List of successful billings below")
        invoices.forEach{
            try {
                charge(it,customers)
                success.append(it.id,it.customerId)
            }
            catch (e :CurrencyMismatchException){
                failed.append(it.id,it.customerId)
                status = false
                logger.error { "Currency's where mismatched on ${it.id}" }
            }catch (e : CustomerNotFoundException){
                failed.append(it.id,it.customerId)
                status = false
                logger.error { "Customer was not found on ${it.id}" }
            }catch (e :NetworkException){
                failed.append(it.id,it.customerId)
                status = false
                logger.error { "Network unstable on transaction ${it.id}" }
            }catch (e :Exception){
                failed.append(it.id,it.customerId)
                status = false
                logger.error { "${e.cause}" }
            }
        }
        if(status)
            println("Charge complete")
        else
            println("Not all charges complete! \n Please see error files to find which transactions failed.")

        pf.write(failed.toString())
        ps.write(success.toString())
        return status
    }

}