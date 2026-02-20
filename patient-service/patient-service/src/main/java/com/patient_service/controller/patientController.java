package com.patient_service.controller;

import com.patient_service.entity.patient;
import com.patient_service.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/patient")
public class patientController {

    private final PatientRepository patientRepository;

    public patientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @PostMapping("/create")
    public patient createPatient(@RequestBody patient patient){
        return patientRepository.save(patient);
    }

    @GetMapping("/getpatientbyid")
    public patient getPatientId(@RequestParam ("id") Long id){
        return patientRepository.findById(id).
                orElseThrow(() -> new RuntimeException("patiernt not found"+id));
    }

}
