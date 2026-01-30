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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    LocalDateTime testDate = LocalDateTime.of(2027, 10, 10, 10, 00);


    @ParameterizedTest
    @CsvSource({
            ",,", //all input null
            ", 2026-01-28T11:00, 2026-01-28T12:00", //roomid null
            "Room1, ,2026-01-28T12:00", //Start time null
            "Room1, 2026-01-28T12:00, " //end time null
    })
    void bookRoomThrowExeptionWithNullInput(String roomId, LocalDateTime start, LocalDateTime end) {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom(roomId, start, end);
        });
    }

    @Test
    void bookRoomInThePastThrowException() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(testDate);
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("Room1",
                    timeProvider.getCurrentTime().minusHours(1),
                    timeProvider.getCurrentTime());

        });
        assertThat(exception).hasMessage("Kan inte boka tid i dåtid");
    }

    @Test
    void bookRoomEndTimeIsBeforeStartThrowsException() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(testDate);
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("Room1",
                    timeProvider.getCurrentTime().plusHours(1),
                    timeProvider.getCurrentTime());
        });
        assertThat(exception).hasMessage("Sluttid måste vara efter starttid");
    }

    @Test
    void bookRoomNonExistantRoomThrowsException() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(testDate);
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.bookRoom("FalseRoom",
                    timeProvider.getCurrentTime(),
                    timeProvider.getCurrentTime().plusHours(1));
        });
        assertThat(exception).hasMessage("Rummet existerar inte");
    }

    @Test
    void bookRoomWithUnavaliableRoomReturnsFalse() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(testDate);
        LocalDateTime start = timeProvider.getCurrentTime();
        LocalDateTime end = start.plusHours(1);

        Room mockedRoom = Mockito.mock(Room.class);
        Mockito.when(mockedRoom.isAvailable(Mockito.any(), Mockito.any())).thenReturn(false);
        Mockito.when(roomRepository.findById(Mockito.anyString())).thenReturn(Optional.of(mockedRoom));

        boolean result = bookingSystem.bookRoom("room1", start, end);
        assertThat(result).isFalse();
    }

    @Test
    void bookRoomValidInputReturnsTrue() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(testDate);
        LocalDateTime start = timeProvider.getCurrentTime();
        LocalDateTime end = start.plusHours(1);
        String roomId = "Room1";
        Room room = new Room(roomId, "1");
        Mockito.when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        boolean result = bookingSystem.bookRoom(roomId, start, end);
        assertThat(result).isTrue();
    }

    @Test
    void bookRoomShouldCallForSendBookingConfirmation() {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(testDate);
        LocalDateTime start = timeProvider.getCurrentTime();
        LocalDateTime end = start.plusHours(1);
        Room room = new Room("room", "1");
        //Mockito.when(room.isAvailable(start, end)).thenReturn(true);
        Mockito.when(roomRepository.findById(Mockito.anyString())).thenReturn(Optional.of(room));
        bookingSystem.bookRoom("room", start, end);
        try {
            Mockito.verify(notificationService,
                            Mockito.times(1))
                    .sendBookingConfirmation(Mockito.any(Booking.class));

        } catch (NotificationException e) {
            System.out.println("Notify error");
        }

    }

    @Test
    void bookRoomShouldReturnTrueEvenWhenNotificationExceptionIsThrown() throws NotificationException {
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(testDate);
        LocalDateTime start = timeProvider.getCurrentTime();
        LocalDateTime end = start.plusHours(1);
        Room room = new Room("room", "1");
        Mockito.when(roomRepository.findById(Mockito.anyString())).thenReturn(Optional.of(room));
        Mockito.doThrow(new NotificationException("simulated error"))
                .when(notificationService)
                .sendBookingConfirmation(Mockito.any(Booking.class));

        boolean result = bookingSystem.bookRoom("room", start, end);
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            ",",                    //null input
            "2020-10-10T10:00,",    //null endTime
            ",2020-10-10T10:00",    //null startTime
            "2020-10-10T11:00, 2020-10-10T10:00"   //end before start
    })
    void getAvaliableRoomsShouldThrowExceptionWithInvalidInput(LocalDateTime start, LocalDateTime end) {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.getAvailableRooms(start, end);
        });
    }

    @Test
    void getAvailableRoomsShouldReturnListOfAvailableRooms() {
        Room availableRoom = new Room("room", "1");
        Room occupiedRoom = new Room("room2", "2");
        occupiedRoom.addBooking(new Booking("test", "room2", testDate.plusHours(0), testDate.plusHours(1)));
        Mockito.when(roomRepository.findAll()).thenReturn(List.of(availableRoom, occupiedRoom));
        var result = bookingSystem.getAvailableRooms(testDate, testDate.plusHours(1));

        assertThat(result).isNotEmpty();
        assertThat(result).contains(availableRoom);
        assertThat(result).doesNotContain(occupiedRoom);
    }

    @Test
    void cancelBookingShouldThrowExceptionWithNullInput(){
        assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.cancelBooking(null);
        });
    }

    @Test
    void cancelBookingShouldReturnfalseWhenRoomWithBookingIsEmpty(){
    var result = bookingSystem.cancelBooking("fakeId");
    assertThat(result).isFalse();
    }

    @Test
    void cancelBookingShoudThrowExceptionWhenTryingToCancelAStartedBooking(){
        Mockito.when(timeProvider.getCurrentTime()).thenReturn(testDate);
        LocalDateTime start = timeProvider.getCurrentTime().minusHours(1);
        LocalDateTime end = start.plusHours(1);
        Room room = new Room("room", "1");
        room.addBooking(new Booking("bookingID", "room", start, end));
        Mockito.when(roomRepository.findAll()).thenReturn(List.of(room));

        assertThrows(IllegalStateException.class, () -> {
            bookingSystem.cancelBooking("bookingID");
        });
    }



}