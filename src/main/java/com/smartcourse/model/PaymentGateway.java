package com.smartcourse.model;

import java.util.Random;

public class PaymentGateway {
    // Simulates a real payment gateway with ~80% success rate
    public boolean process(double amount) {
        Random random = new Random();
        return random.nextInt(10) < 8; // 80% success
    }
}
