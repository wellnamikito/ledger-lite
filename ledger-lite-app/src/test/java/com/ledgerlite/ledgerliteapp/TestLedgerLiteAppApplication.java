package com.ledgerlite.ledgerliteapp;

import org.springframework.boot.SpringApplication;

public class TestLedgerLiteAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(LedgerLiteAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
