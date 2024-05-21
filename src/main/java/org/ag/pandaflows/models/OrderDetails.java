package org.ag.pandaflows.models;

import lombok.Data;

@Data
public class OrderDetails {
    private int quantity;
    private double price;
    private String productName;
    private String productDescription;
    private String productCategory;

}
