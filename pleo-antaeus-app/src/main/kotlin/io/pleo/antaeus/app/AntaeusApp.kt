/*
    Defines the main() entry point of the app.
    Configures the database and sets up the REST web service.
 */

@file:JvmName("AntaeusApp")

package io.pleo.antaeus.app

import getPaymentProvider
import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.data.CustomerTable
import io.pleo.antaeus.data.InvoiceTable
import io.pleo.antaeus.rest.AntaeusRest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import setupInitialData
import java.sql.Connection
import java.time.LocalDate
import kotlin.system.exitProcess


fun main() {
    // The tables to create in the database.
    val tables = arrayOf(InvoiceTable, CustomerTable)

    // Connect to the database and create the needed tables. Drop any existing data.
    val db = Database
            .connect("jdbc:sqlite:/tmp/data.db", "org.sqlite.JDBC")
            .also {
                TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
                transaction(it) {
                    addLogger(StdOutSqlLogger)
                    // Drop all existing tables to ensure a clean slate on each run
                    SchemaUtils.drop(*tables)
                    // Create all tables
                    SchemaUtils.create(*tables)
                }
            }

    // Set up data access layer.
    val dal = AntaeusDal(db = db)

    // Insert example data in the database.
    setupInitialData(dal = dal)

    // Get third parties
    //  val paymentProvider = getPaymentProvider()

    // Create core services
    val invoiceService = InvoiceService(dal = dal)
    val customerService = CustomerService(dal = dal)

    // This is _your_ billing service to be included where you see fit
    val billingService = BillingService(invoiceService,customerService)

    // Create REST web service
    AntaeusRest(
            invoiceService = invoiceService,
            customerService = customerService
    ).run()
    userCom(billingService,customerService,invoiceService)
}
//Function is used to trigger events
fun userCom(billingService:BillingService, customerService:CustomerService,invoiceService: InvoiceService){
    print("Alpha mode initialized. \n Please enter a command \n (N)-Normal Mode (T)-test (A)abort:" )
    val uinput: String = readLine()!!.toString().toUpperCase()
    if(uinput == "T"){
        billingService.chargeSignal()
    }else if(uinput =="A"){
        println("Admin has shutdown the server.")
        exitProcess(0)
    }else if(uinput == "N"){
        val billingService = BillingService(invoiceService,customerService)
        normalMode(billingService,customerService, invoiceService)
    }
    else{
        println("I'm sorry that command is unavailable at the moment.")
        userCom(billingService,customerService,invoiceService)
    }
}
//Function iterates through every day and bills on last day
fun normalMode(billingService:BillingService, customerService:CustomerService,invoiceService: InvoiceService){
    var day = LocalDate.now()
    var year = true
    while(year) {
        println("Today is $day. ")
        if (day.dayOfMonth == day.lengthOfMonth()) {
            billingService.chargeSignal()
        }
        day = day.plusDays(1)
        Thread.sleep(1000)
        if(day.dayOfMonth.equals(15)){
            invoiceService.markAllPending(invoiceService.fetchAll())
        }
        if(day.dayOfYear.equals(day.lengthOfYear())){
            year = false
            userCom(billingService,customerService, invoiceService)
        }
    }

}
