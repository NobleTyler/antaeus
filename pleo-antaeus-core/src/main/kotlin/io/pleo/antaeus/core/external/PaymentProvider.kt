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
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

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

        if(customer != null){
            if (invoice.amount.currency != customer.currency) {
                throw CurrencyMismatchException(invoiceId = invoice.id, customerId = customer.id)
            }
            else if(invoice.status==(InvoiceStatus.PAID)){
                //TODO should this do something?
            }
            else if(!networkFind()){
                status = networkFind()
                throw NetworkException()
            }
        }
        else
            throw CustomerNotFoundException(invoice.customerId)

        return status
    }
    /*
    This method returns true if it is able to ping google. Used in network checking
     */
    fun networkFind():Boolean {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
                .uri(URI.create("https://google.com"))
                .build()
        var status= false
        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            println(response.statusCode())
            if(response.statusCode().toString().contains("unstable")){
                status = false
                throw NetworkException()
            }else{
                status = true
            }
        }catch (e:Exception){
            status = false
            throw NetworkException()
        }
        return status

    }
}
