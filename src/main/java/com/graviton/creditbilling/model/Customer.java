package com.graviton.creditbilling.model;

import lombok.Data;

@Data
public class Customer {
    private String name;
    private int totalCredits;

    public Customer(String name) {
        this.name = name;
        this.totalCredits = 0;
    }

    public void addCredits(int credits) {
        totalCredits += credits;
    }

    public boolean useCredits(int credits) {
        if (totalCredits >= credits) {
            totalCredits -= credits;
            return true;
        }
        return false;
    }
}
