package com.example.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    Map<Product, Integer> basket = new HashMap<>();


    public void addProduct(String productName, BigDecimal price) {
        if (productName.trim().isEmpty())
            throw new IllegalArgumentException("Produktnamnet kan inte vara tomt");
        if (price == null || price.doubleValue() < 0)
            throw new IllegalArgumentException("Produktpriset kan inte vara null eller negativt");
        String name = productName.trim().toUpperCase();
        Product newProd = new Product(name, price);
        basket.merge(newProd, 1, Integer::sum);
    }

    public void removeProduct(String productName) {
        if (productName.trim().isEmpty())
            throw new IllegalArgumentException("Produktnamnet kan inte vara tomt");
        String name = productName.trim().toUpperCase();
        var prod = basket.keySet().stream().filter(product ->
                        product.getProductName().equals(name))
                .findFirst();
        if (prod.isPresent()) {
            if (basket.get(prod.get()) == 1) {
                basket.remove(prod.get());
            } else {
                basket.merge(prod.get(), -1, Integer::sum);
            }
        }
    }

    public Map<Product, Integer> getProducts() {
        return basket;
    }


    public BigDecimal getSumPriceOfAllProducts() {
        return BigDecimal.valueOf(basket.entrySet().stream().mapToDouble(
                        p -> p.getKey().getPrice().doubleValue() * p.getValue())
                .sum()).setScale(2, RoundingMode.HALF_UP);
    }

    public void applyDiscount(double discount) {
        if (discount < 0 || discount >= 100.000)
            throw new IllegalArgumentException("Rabatt kan inte göra produkterna gratis eller negativt pris");
        double priceChange = (100 - discount) * 0.01;
        basket.keySet().stream().forEach(p ->
                p.setPrice(BigDecimal.valueOf(p.getPrice().doubleValue() * priceChange)));
    }

    public int getAmountOfProductsInCart() {
        return basket.entrySet().stream().mapToInt(
                        prod -> prod.getValue())
                .sum();
    }


}
