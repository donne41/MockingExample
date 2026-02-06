package com.example.shop;

import java.math.BigDecimal;
import java.util.Objects;

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

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productName, product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productName);
    }
}
