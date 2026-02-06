package com.example.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    Map<Product, Integer> basket = new HashMap<>();
    BigDecimal priceDiscount = BigDecimal.ONE;


    public void addProduct(String productName, BigDecimal price) {
        if (productName == null || productName.trim().isEmpty())
            throw new IllegalArgumentException("Produktnamnet kan inte vara tomt");
        if (price == null || price.doubleValue() <= 0)
            throw new IllegalArgumentException("Produktpriset kan inte vara null, gratis eller negativt");
        String name = productName.trim().toUpperCase();
        Product newProd = new Product(name, price);
        basket.merge(newProd, 1, Integer::sum);
    }

    public void removeProduct(String productName) {
        if (productName == null || productName.trim().isEmpty())
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
        return Map.copyOf(basket);
    }

    public BigDecimal getSumPriceOfAllProducts() {
        BigDecimal subTotal = basket.entrySet().stream().map(
                        p -> p.getKey()
                                .getPrice()
                                .multiply(BigDecimal.valueOf(p.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        return subTotal.multiply(priceDiscount).setScale(2, RoundingMode.HALF_UP);
    }

    public void applyDiscount(double discount) {
        if (discount < 0 || discount >= 100.000)
            throw new IllegalArgumentException("Rabatt kan inte göra produkterna gratis eller negativt pris");
        BigDecimal tempDiscount = BigDecimal.valueOf((100 - discount) * 0.01);
        if (tempDiscount.compareTo(priceDiscount) < 0)
            priceDiscount = tempDiscount;
    }

    public int getAmountOfProductsInCart() {
        return basket.entrySet().stream().mapToInt(
                        prod -> prod.getValue())
                .sum();
    }


}
