package com.example.shop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartTest {



    @Test
    void addProductShouldBeSavedToList() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct("milk", BigDecimal.TEN);

        var result = cart.getProducts();

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void removeProductShouldDeleteProductFromList() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct("milk", BigDecimal.TEN);
        cart.removeProduct("milk");

        var result = cart.getProducts();

        assertThat(result.size()).isZero();
    }

    @Test
    void getTotalPriceShouldReturnSumOfProductPrices() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct("milk", BigDecimal.TEN);
        cart.addProduct("milk", BigDecimal.TEN);

        var result = cart.getSumPriceOfAllProducts();

        assertThat(result).isEqualTo(BigDecimal.valueOf(20));
    }

    @Test
    void applyDiscountShouldReturnSumOfNewPrice() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct("milk", BigDecimal.TEN);
        cart.addProduct("bread", BigDecimal.valueOf(20));
        cart.applyDiscount(30.0);

        var result = cart.getSumPriceOfAllProducts();

        assertThat(result).isEqualTo(BigDecimal.valueOf(21).setScale(2));
    }

    @Test
    void addAnotherOfTheSameProductShouldIncreaseIntegerAmount() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct("milk", BigDecimal.TEN);
        cart.addProduct("milk", BigDecimal.TEN);
        cart.addProduct("bread", BigDecimal.valueOf(20));

        var result = cart.getAmountOfProductsInCart();

        assertThat(result).isEqualTo(3);
    }

    @Test
    void addingProductWithNoNameShouldThrowException() {
        ShoppingCart cart = new ShoppingCart();

        var exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    cart.addProduct(" ", BigDecimal.TEN);
                });

        assertThat(exception).hasMessage("Produktnamnet kan inte vara tomt");
    }

}
