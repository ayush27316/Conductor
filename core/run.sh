#!/bin/bash

# Go up one directory
cd ..

# Run Maven clean and compile
mvn clean compile

# Go into the core directory
cd core

# Run Spring Boot application
mvn spring-boot:run
