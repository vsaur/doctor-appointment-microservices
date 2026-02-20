package com.doctor_services.repository;

import com.doctor_services.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {



    List<Doctor> findBySpecialization (String specialization);
    List<Doctor> findByName(String name);
}
