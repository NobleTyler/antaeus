## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will pay those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

### Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── pleo-antaeus-app
|
|       Packages containing the main() application. 
|       This is where all the dependencies are instantiated.
|
├── pleo-antaeus-core
|
|       This is where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|
|       Module interfacing with the database. Contains the models, mappings and access layer.
|
├── pleo-antaeus-models
|
|       Definition of models used throughout the application.
|
├── pleo-antaeus-rest
|
|        Entry point for REST API. This is where the routes are defined.
└──
```

## Instructions
Fork this repo with your solution. We want to see your progression through commits (don’t commit the entire solution in 1 step) and don't forget to create a README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

Happy hacking 😁!

## How to run
```
./docker-start.sh
```

## Libraries currently in use
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library

## Design Decisions
### What we liked:
Great commits and git history,
The documentation with thoughts about the design,
The tests with mocks

### What we needed more of:
We realise that this implementation may have taken a lot of time to be made given its complexity however there was a misunderstanding about the point of the challenge. In particular the role of the paymentProvider, which you implement even though it is a third party code. There was also confusion of the role of the 3rd party code and our code.
A simplified use of the billing service would have resulted to a less confusing implementation. The billing service was used in a complicated way over CLI which may have problems working in production with the invoices being billed on last day of the month instead of first day yet they are marked as pending on the 15th of every month.
We would have loved to see you showcase injecting dependencies, coroutines, scheduling and more documentation.
We would have loved to see more justification with CLI interactions (REST not safe?)
Documenting about any future improvements.
Justification or pitfalls of using the list of customer in memory, the use of CLI which is not usable in a production deployment

### Admin Interaction
Admin interaction is done through the console in this iteration. 
While alternatives like using restful routes or creating a front end could be used the CLI was both quick to implement and decidedly more secure.
The console is likely only seen by administrators and it us unlikely somebody accidentally charge by going to a Restful-URL (I'm looking at you chrome autocomplete) or Front-End interface.
As well there are modes for easily shutting down the server, as well as testing charges and performing Normal operations where users are charged on a per month basis.

Please note that when running via CLI normal mode is assumed.
>TLDR; I used a CLI for safety reasons and ease of development.
### Payment Provider
Payment provider is an abstract class which is the parent of BillingTest. The purpose of which serves to deal with error handling for customer/invoice
interactions. As any abstract class it provides a template for things to look out for in charges and can later be used to possibly implement refunds or different types of customer/invoice interactions.
>TLDR: It's an abstract class, implements charge and throws exceptions. Very bare-bones.
### Billing Service
BillingService was something I originally wanted to keep inline with other services by inheriting the DAL(Data Access Layer) 
. However this is already covered in the main so I later found that it was best to take in the two required services into the BillingService.
The billing service itself is used mainly in normal operation to iterate over the list of invoices and call a charge function on each customer.
It has added benefits of catching thrown exceptions as well and increased error reporting. 
>TLDR; Billing Service inherits from Payment Provider. 
### Testing 
Testing was all done in Junit. Originally I had implemented testing such that it wouldn't need mocks and would just take lists.
Then I found a bug and changed it back to using the DAL. The only untestable error I found was NetworkExceptions.
>TLDR; Originally built around not using the DAL, then I learned to use mockk.

# Total Time
#### 11 hours 26 minutes

> Specific hours can be checked in WorkTime.csv
