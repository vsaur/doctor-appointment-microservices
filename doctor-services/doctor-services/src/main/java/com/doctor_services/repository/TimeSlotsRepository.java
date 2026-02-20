package com.doctor_services.repository;

import com.doctor_services.entity.TimeSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeSlotsRepository extends JpaRepository<TimeSlots, Long> {

    List<TimeSlots> findByDoctorAppointmentScheduleId(Long scheduleId);
}
