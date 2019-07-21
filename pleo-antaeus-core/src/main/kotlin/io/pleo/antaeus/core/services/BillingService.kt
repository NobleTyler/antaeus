package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import java.io.File

class BillingService(private val dal: AntaeusDal):PaymentProvider {
   // TODO - Add code e.g. here
   var invSer:InvoiceService = InvoiceService(dal)
   var cusSer:CustomerService= CustomerService(dal)

   var invoices = invSer.fetchAll().toList()
    var customers = cusSer.fetchAll().toList()

    fun chargeSignal():Boolean{
       var status = true
        invoices.forEach{
            File("PaymentFailures.txt").bufferedWriter()
        File("PaymentSucess.txt").bufferedWriter()
        val success = StringBuilder()
        val failed = StringBuilder()
        failed.append("List of failed billings below:")
        success.append("List of successful billings below")
        try {
            println("Charge complete")
            success.append(it.id,it.customerId)
        }catch (e :Exception){
            println("Charge incomplete!")
            //failed.append(invoice.id,e.cause)
            status = false
        }
       }
        return status
    }



}