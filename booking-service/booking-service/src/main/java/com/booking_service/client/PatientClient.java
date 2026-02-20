package com.booking_service.client;

import com.booking_service.DTO.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "patient-service", url = "${PATIENT_SERVICE_URL}")
public interface PatientClient {

    @GetMapping("/api/v1/patient/getpatientbyid")
    Patient getPatientById(@RequestParam("id") long id);
}
