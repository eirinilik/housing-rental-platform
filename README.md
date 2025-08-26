
Housing Rental Platform

This project is a web-based housing rental platform built with Spring Boot. It allows owners to list properties and tenants to submit rental applications. The application includes user management with different roles and secure access to various functionalities.

Key Features

    User Management: Users can register with roles as either TENANT or OWNER. New roles require approval from an administrator.

    User Roles:

        ROLE_ADMIN: Administrator. Can approve role requests, manage all users, and approve or delete any property.

        ROLE_OWNER: Owner. Can list new properties, view their own properties, manage applications submitted for their properties, and delete them.

        ROLE_TENANT: Tenant. Can view available properties and submit applications for them.

    Property Management: Properties are only visible after being approved by an administrator.

    Rental Applications: Tenants can submit applications for properties, which owners can then approve or reject.

Technologies

    Backend: Java, Spring Boot, Spring Security, Spring Data JPA

    Database: PostgreSQL

    Frontend: Thymeleaf, Bootstrap, JQuery

    Build Tool: Maven

Setup Instructions

    Clone the project:
    

git clone https://github.com/eirinilik/housing-rental-platform.git

Database Configuration:
Update the src/main/resources/application.properties file with your PostgreSQL database credentials.

Run the application:
You can run the application from the terminal using Maven:
Bash

    ./mvnw spring-boot:run

    The application will start at http://localhost:9090.

Initial Data

Upon the first startup of the application, the following users and one approved property are automatically created for testing purposes:

    Admin:

        Username: Admin

        Email: admin@gmail.com

        Password: 123

    Owner:

        Username: Dimitris Spurou

        Email: dimitris@gmail.com

        Password: 123

    Tenant:

        Username: Afroditi Mamoy

        Email: afroditi@gmail.com

        Password: 123
