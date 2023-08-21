# AppointmentScheduler
![](https://github.com/johncrescenzi/AppointmentScheduler/blob/master/login.gif)

Scheduling Application README

Purpose of Application

This application serves as a GUI-based scheduling desktop program, aiming to provide an intuitive interface for scheduling tasks and managing appointments.

Author Information
John Crescenzi
jcresc2@wgu.edu
Application Version: 1.0
Date: 08/20/2023

IDE and Java Module Information

IDE: IntelliJ IDEA 2023.2.1 (Community Edition)
Build #IC-231.9011.34, built on May 16, 2023
Runtime version: 17.0.6+10-b829.9 aarch64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
Javafx: javafx-sdk-17.0.8

Dependencies and Libraries

JavaFX: Integrated with the IDE, ensure you have the appropriate library set up in your project settings.
MySQL Connector: mysql-connector-j-8.1. Ensure that this library is added to your project's classpath for database connectivity.

Additional Report Feature

In the provided code, two of the requested reports are already present:

A report displaying the total number of customer appointments by type and month. This is generated with the generateAppointmentTotalsReport() method.
A report displaying customers by country. This is generated with the generateCustomerByCountryReport() method.

How to Run the Program

Launch the application.

Upon initialization, you'll encounter a login screen.
To proceed, input a valid username and password, which should be present in a MySQL database.
Please note that this application is designed to work with Java 17. Compatibility with other JVMs hasn't been tested.

Additional Information

When setting up your project in IntelliJ, ensure that the module paths are correctly set to include the source directory and the required libraries.
If any issues arise related to dependencies, cross-check with the module configuration provided above to ensure everything is in order.
Thank you for choosing our Scheduling Application. We hope it enhances your scheduling and management tasks. For any queries or feedback, please reach out to our support team
