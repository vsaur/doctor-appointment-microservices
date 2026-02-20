package com.booking_service.client;

import com.booking_service.DTO.Doctor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "doctor-services", url = "${DOCTOR_SERVICE_URL}")
public interface DoctorClient {

    @GetMapping("/api/v1/doctor/getdoctorbyid")
    Doctor getDoctorById(@RequestParam("doctorId") Long doctorId);
}
