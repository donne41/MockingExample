package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class BookingSystemTest {
    @Mock
    TimeProvider timeProvider;
    @Mock
    RoomRepository roomRepository;
    @Mock
    NotificationService notificationService;

    @InjectMocks
    BookingSystem bookingSystem;


    @Test
    void bookRoomThrowExeptionWithNullTimeOrId(){
        assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("", null, null);
        });
        //TODO Make parametirized test so all branches are tested
    }

    @Test
    void bookRoomInThePastThrowException(){
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("1",
                    timeProvider.getCurrentTime().minusHours(1),
                    timeProvider.getCurrentTime().plusMinutes(1));
        });
    }

}