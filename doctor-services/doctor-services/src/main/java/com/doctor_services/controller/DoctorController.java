package com.doctor_services.controller;

import com.doctor_services.DTO.ScheduleRequest;
import com.doctor_services.entity.Doctor;
import com.doctor_services.entity.DoctorAppointmentSchedule;
import com.doctor_services.entity.TimeSlots;
import com.doctor_services.repository.DoctorRepository;
import com.doctor_services.repository.TimeSlotsRepository;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final TimeSlotsRepository timeSlotsRepository;

    public DoctorController(DoctorRepository doctorRepository, TimeSlotsRepository timeSlotsRepository) {
        this.doctorRepository = doctorRepository;
        this.timeSlotsRepository = timeSlotsRepository;
    }

     @PostMapping("/create")
     public Doctor createDoctor(@RequestBody Doctor doctor){
        return doctorRepository.save(doctor);
     }

     @GetMapping("/getdoctorbyid")
     public Doctor getDoctorById(@RequestParam("doctorId") Long doctorId){
        return doctorRepository.findById(doctorId).
                orElseThrow(() -> new RuntimeException("doctor not found"));
     }

     @GetMapping("/getbyspecialization")
     public List<Doctor> getBySpecialization(@RequestParam("specialization") String specialization){
        return doctorRepository.findBySpecialization(specialization);

     }

     @GetMapping("/getbyname")
     public List<Doctor> getByName(@RequestParam("name") String name){

        return doctorRepository.findByName(name);
     }

     @PostMapping("/{doctorId}/schedule")
     public DoctorAppointmentSchedule addSchedule(@PathVariable Long doctorId,
                                                  @RequestBody ScheduleRequest request){
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("doctor not found"));

        DoctorAppointmentSchedule schedule = new DoctorAppointmentSchedule();
        schedule.setDate(request.getDate());
        schedule.setDoctor(doctor);

        List<TimeSlots> slots = new ArrayList<>();
        for(var t : request.getTimes()){
            TimeSlots ts = new TimeSlots();
            ts.setTime(t);
            ts.setDoctorAppointmentSchedule(schedule);
            slots.add(ts);
        }
        schedule.setTimeSlots(slots);

        doctor.getSchedules().add(schedule);

        doctorRepository.save(doctor);

        return schedule;
     }

     @GetMapping("/{scheduleId}/slots")
     public List<TimeSlots> getSlotsByScheduleId(@PathVariable long scheduleId ){
        return timeSlotsRepository.findByDoctorAppointmentScheduleId(scheduleId);
     }
}
