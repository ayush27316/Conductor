/*
*   ____                _            _
*  / ___|___  _ __   __| | ___ _   _| |_ ___  _ __
* | |   / _ \| '_ \ / _` |/ __| | | | __/ _ \| '__|
* | |__| (_) | | | | (_| | (__| |_| | || (_) | |
*  \____\___/|_| |_|\__,_|\___|\__,_|\__\___/|_|
*
* @Author: Ayush Srivastava
* @Version 1.0.0
* */
package com.conductor.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.conductor.core",
        "com.conductor.adapters"
})
public class ConductorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConductorApplication.class, args);
    }
}
