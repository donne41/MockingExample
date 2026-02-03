package com.example.shop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartTest {



    @Test
    void addProductShouldBeSavedToList() {
        ShoppingCart cart = new ShoppingCart();

        cart.addProduct("milk");

        var result = cart.getProducts();
        assertThat(result).contains("milk");
    }

    @Test
    void removeProductShouldDeleteProductFromList() {
        ShoppingCart cart = new ShoppingCart();

        cart.addProduct("milk");
        cart.removeProduct("milk");

        var result = cart.getProducts();
        assertThat(result).doesNotContain("milk");
    }
}
