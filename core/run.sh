#!/bin/bash

# Clear screen
clear

# Check if -d flag is provided
if [ "$1" == "-d" ]; then
  echo "Deleting contents of ./data..."
  rm -rf ./data/*
fi

# Go up one directory
cd ..

# Run Maven clean and compile
mvn clean compile

# Go into the core directory
cd core

# Run Spring Boot application
mvn spring-boot:run
