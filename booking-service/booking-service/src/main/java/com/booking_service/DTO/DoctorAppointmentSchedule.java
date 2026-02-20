package com.booking_service.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DoctorAppointmentSchedule {

    private long id;
    private LocalDate date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<TimeSlots> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlots> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }



    private List<TimeSlots> timeSlots;
}
