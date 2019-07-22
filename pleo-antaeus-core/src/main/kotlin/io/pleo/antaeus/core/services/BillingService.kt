package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import mu.KLogger
import mu.KotlinLogging
import java.io.File

class BillingService(private val dal: AntaeusDal):PaymentProvider {
    private val logger = KotlinLogging.logger("billingService")
    private val invSer = InvoiceService(dal)
    private val cusSer =CustomerService(dal)

    var invoices = invSer.fetchAll().toList()
    var customers = cusSer.fetchAll().toList()

    fun chargeSignal():Boolean{
        var status = true
        var success = StringBuilder()
        var failed = StringBuilder()

        File("PaymentFailures.txt").bufferedWriter()
        File("PaymentSucess.txt").bufferedWriter()
        failed.append("List of failed billings below:")
        success.append("List of successful billings below")
        invoices.forEach{
            try {
                var custHold = it.customerId
                var custCur = it.amount.currency
                var custID= customers.indexOfFirst { it.id == custHold && it.currency ==custCur }
                if(custID != -1)
                    success.append(it.id,it.customerId)
                else {
                    failed.append(it.id, it.customerId)
                    status = false
                }
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


        return status
    }



}