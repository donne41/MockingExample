package com.example.shop;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ShoppingCart {

    ArrayList<Product> basket = new ArrayList<>();


    public void addProduct(String productName, BigDecimal price) {
        basket.add(new Product(productName, price));
    }

    public void removeProduct(String productName) {
        var list = getProducts();
        list.stream().filter(p ->
                        p.getProductName().matches(productName))
                .findFirst()
                .ifPresent(p -> list.remove(p));
    }

    public ArrayList<Product> getProducts() {
        return basket;
    }

    public BigDecimal getSumPriceOfAllProducts() {
        var list = getProducts();
        return BigDecimal.valueOf(list.stream().mapToDouble(
                        p -> p.getPrice().doubleValue())
                .sum());

    }

    public void applyDiscount(double discount) {
        double priceChange = (100 - discount) * 0.01;
        getProducts().stream().forEach(p ->
                p.setPrice(BigDecimal.valueOf(p.getPrice().doubleValue() * priceChange)));
    }


}
