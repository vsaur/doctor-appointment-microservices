package com.booking_service.repository;

import com.booking_service.entity.BookingConfirmationDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface BookingConfirmationRepository
        extends JpaRepository<BookingConfirmationDetails, Long> {

    boolean existsByDoctorIdAndDateAndTime(long doctorId, LocalDate date, LocalTime time);
}
