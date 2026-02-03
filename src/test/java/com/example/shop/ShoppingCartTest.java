package com.example.shop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartTest {

    @Mock
    ShoppingCart cart;


    @Test
    void addProductShouldBeSaved() {

        cart.addProduct("milk");

        Mockito.verify(cart,
                        Mockito.times(1))
                .addProduct("milk");
    }
}
