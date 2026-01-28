package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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


    @ParameterizedTest
    @CsvSource({
            ",,", //all input null
            ", 2026-01-28T11:00, 2026-01-28T12:00", //roomid null
            "Room1, ,2026-01-28T12:00", //Start time null
            "Room1, 2026-01-28T12:00, " //end time null
    })
    void bookRoomThrowExeptionWithNullInput(String roomId, LocalDateTime start, LocalDateTime end){
        assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom(roomId, start, end);
        });
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