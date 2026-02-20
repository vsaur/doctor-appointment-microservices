package com.booking_service.controller;

import com.booking_service.DTO.Doctor;
import com.booking_service.DTO.Patient;
import com.booking_service.client.DoctorClient;
import com.booking_service.client.PatientClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "*")
@RestController
public class TestFeignController {

    private final DoctorClient doctorClient;
    private final PatientClient patientClient;


    public TestFeignController(DoctorClient doctorClient, PatientClient patientClient) {
        this.doctorClient = doctorClient;
        this.patientClient = patientClient;
    }

    @GetMapping("/test/doctor")
    public Doctor TestDoctor(@RequestParam long doctorId){
        return doctorClient.getDoctorById(doctorId);
    }
    @GetMapping("/test/patient")
    public Patient TestPatient(@RequestParam long patientId){
        return patientClient.getPatientById(patientId);
    }
}
