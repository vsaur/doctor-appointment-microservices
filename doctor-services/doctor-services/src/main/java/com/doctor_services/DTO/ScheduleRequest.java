package com.doctor_services.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ScheduleRequest {

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<LocalTime> getTimes() {
        return times;
    }

    public void setTimes(List<LocalTime> times) {
        this.times = times;
    }

    private LocalDate date;
    private List<LocalTime> times;
}
