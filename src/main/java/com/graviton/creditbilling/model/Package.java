package com.graviton.creditbilling.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Package {
    private String name;
    private int credits;
    private double price;
}
