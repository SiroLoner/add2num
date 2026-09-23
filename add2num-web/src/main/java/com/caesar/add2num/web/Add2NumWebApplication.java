package com.caesar.add2num.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * TASK 2: a small web application that lets a user add two very large numbers and watch the
 * calculation unfold column by column.
 *
 * <p>All arithmetic is delegated to {@code add2num-core}, the deliverable of TASK 1, which this
 * module consumes as a Maven sub-module. Nothing in this package re-implements the addition.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan(basePackages = {"com.caesar.add2num.web", "com.caesar.add2num.core.api"})
public class Add2NumWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(Add2NumWebApplication.class, args);
    }
}
