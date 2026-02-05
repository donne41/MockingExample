package com.example.shop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

        assertThat(result).isEqualTo(BigDecimal.valueOf(20).setScale(2));
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
        var exception2 = assertThrows(IllegalArgumentException.class,
                () -> {
                    cart.addProduct(null, BigDecimal.TEN);
                });

        assertThat(exception).hasMessage("Produktnamnet kan inte vara tomt");
        assertThat(exception2).hasMessage("Produktnamnet kan inte vara tomt");
    }

    @ParameterizedTest
    @CsvSource({
            "milk, ,",
            "milk, -10.0",
            "milk, 0"
    })
    void addingProductWithInvalidPriceShouldThrowException(String name, BigDecimal price) {
        ShoppingCart cart = new ShoppingCart();

        var exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    cart.addProduct(name, price);
                });

        assertThat(exception).hasMessage("Produktpriset kan inte vara null, gratis eller negativt");
    }

    @Test
    void removingProductWithInvalidNameShouldThrowException() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct("milk", BigDecimal.TEN);

        var exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    cart.removeProduct(" ");
                });
        assertThat(exception).hasMessage("Produktnamnet kan inte vara tomt");
    }

    @Test
    @DisplayName("Removing a product should decrease its amount by one, unless last one")
    void removingProductShouldDecreaseCountByOne() {
        ShoppingCart cart = new ShoppingCart();
        cart.addProduct("milk", BigDecimal.TEN);
        cart.addProduct("milk", BigDecimal.TEN);
        cart.addProduct("milk", BigDecimal.TEN);
        cart.removeProduct("milk");

        var productCount = cart.getAmountOfProductsInCart();

        assertThat(productCount).isEqualTo(2);
    }

    @ParameterizedTest
    @CsvSource({
            "100", "110", "10000", "-1", "-1000", "-0.000001", "100.000001"
    })
    void discountShouldNotMakeProductFreeOrNegative(double discount) {
        ShoppingCart cart = new ShoppingCart();

        var exception = assertThrows(IllegalArgumentException.class,
                () -> cart.applyDiscount(discount));

        assertThat(exception).hasMessage("Rabatt kan inte göra produkterna gratis eller negativt pris");
    }
}
