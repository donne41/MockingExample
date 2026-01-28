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

import static org.assertj.core.api.Assertions.assertThat;
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
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(2026,01,28,10,00));
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("Room1",
                    timeProvider.getCurrentTime().minusHours(1),
                    timeProvider.getCurrentTime());

        });
        assertThat(exception).hasMessage("Kan inte boka tid i dåtid");
    }

    @Test
    void bookRoomEndTimeIsBeforeStartThrowsException(){
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(2026,01,28,10,00));
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("Room1",
                    timeProvider.getCurrentTime().plusHours(1),
                    timeProvider.getCurrentTime());
        });
        assertThat(exception).hasMessage("Sluttid måste vara efter starttid");
    }

    @Test
    void bookRoomNonExistantRoomThrowsException(){
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.of(2026,01,28,10,00));
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("FalseRoom",
                    timeProvider.getCurrentTime(),
                    timeProvider.getCurrentTime().plusHours(1));
        });
        assertThat(exception).hasMessage("Rummet existerar inte");
    }



}