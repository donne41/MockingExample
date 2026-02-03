package com.example.shop;

import java.math.BigDecimal;

public class Product {
    String productName;
    BigDecimal price;

    public Product(String productName, BigDecimal price) {
        this.productName = productName;
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }
}
