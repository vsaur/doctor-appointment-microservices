package com.booking_service.DTO;

import java.time.LocalTime;

public class TimeSlots {
    private long id;
    private LocalTime time;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
}
