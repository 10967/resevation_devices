package com.example.backend.service;

import com.example.backend.dto.ReservationDto;
import com.example.backend.entity.Device;
import com.example.backend.entity.Reservation;
import com.example.backend.entity.User;
import com.example.backend.exceptions.ResourceNotFoundException;
import com.example.backend.repository.DeviceRepository;
import com.example.backend.repository.ReservationRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeviceRepository deviceRepository;

    private User user;
    private Device device;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        device = new Device();
        device.setId(1L);

        reservation = new Reservation(user, device, LocalDateTime.now(), LocalDateTime.now().plusHours(2), "wait");
        reservation.setId(1L);
    }

    @Test
    void getAllReservations_shouldReturnList() {
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<ReservationDto> result = reservationService.getAllReservations();

        assertEquals(1, result.size());
        verify(reservationRepository).findAll();
    }

    @Test
    void getReservationById_shouldReturnDto() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        ReservationDto result = reservationService.getReservationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAndUpdateReservation_shouldUpdateStatus() throws ResourceNotFoundException {
        reservation.setStatus("wait");
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(reservation);

        ReservationDto result = reservationService.getAndUpdateReservation(1L);

        assertEquals("reserved", result.getStatus());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void createReservation_shouldSaveReservation() {
        ReservationDto dto = new ReservationDto();
        dto.setUserId(1L);
        dto.setDeviceId(1L);
        dto.setStartDate(LocalDateTime.now());
        dto.setEndDate(LocalDateTime.now().plusHours(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        when(reservationRepository.existsByUserAndDeviceAndStartDateBeforeAndEndDateAfter(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.save(any())).thenReturn(reservation);

        ReservationDto result = reservationService.createReservation(dto);

        assertNotNull(result);
        verify(reservationRepository).save(any());
    }

    @Test
    void createReservation_duplicate_shouldThrow() {
        ReservationDto dto = new ReservationDto();
        dto.setUserId(1L);
        dto.setDeviceId(1L);
        dto.setStartDate(LocalDateTime.now());
        dto.setEndDate(LocalDateTime.now().plusHours(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        when(reservationRepository.existsByUserAndDeviceAndStartDateBeforeAndEndDateAfter(any(), any(), any(), any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> reservationService.createReservation(dto));
    }

    @Test
    void updateReservation_shouldUpdate() {
        ReservationDto dto = new ReservationDto();
        dto.setUserId(1L);
        dto.setDeviceId(1L);
        dto.setStartDate(LocalDateTime.now());
        dto.setEndDate(LocalDateTime.now().plusHours(1));

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        when(reservationRepository.existsByUserAndDeviceAndStartDateBeforeAndEndDateAfter(any(), any(), any(), any())).thenReturn(false);
        when(reservationRepository.save(any())).thenReturn(reservation);

        ReservationDto result = reservationService.updateReservation(1L, dto);

        assertNotNull(result);
        verify(reservationRepository).save(any());
    }

    @Test
    void deleteReservation_shouldCallRepository() {
        reservationService.deleteReservation(1L);
        verify(reservationRepository).deleteById(1L);
    }
}