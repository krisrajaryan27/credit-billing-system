package com.graviton.creditbilling.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Transaction {
    private String customerName;
    private String type; // "purchase" or "usage"
    private String detail;
    private boolean success;

    @Override
    public String toString() {
        return customerName + "," + type + "," + detail + "," + (success ? "Success" : "Denied");
    }
}
