package com.patient_service.repository;

import com.patient_service.entity.patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<patient, Long> {
}
