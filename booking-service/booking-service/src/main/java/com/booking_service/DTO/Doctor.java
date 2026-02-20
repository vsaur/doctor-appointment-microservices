package com.booking_service.DTO;

import java.util.List;

public class Doctor {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<DoctorAppointmentSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<DoctorAppointmentSchedule> schedules) {
        this.schedules = schedules;
    }

    private String name;
    private String specialization;
    private String qualification;
    private String address;
    private String contact;
    private List<DoctorAppointmentSchedule> schedules;
}
