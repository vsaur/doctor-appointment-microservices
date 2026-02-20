package com.booking_service.controller;

import com.booking_service.DTO.*;
import com.booking_service.client.DoctorClient;
import com.booking_service.client.PatientClient;
import com.booking_service.client.PaymentClient;
import com.booking_service.entity.BookingConfirmationDetails;
import com.booking_service.exception.slotAlreadyBookedException;
import com.booking_service.repository.BookingConfirmationRepository;
import org.springframework.web.bind.annotation.*;




import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final DoctorClient doctorClient;
    private final PatientClient patientClient;
    private final BookingConfirmationRepository bookingConfirmationRepository;
    private final PaymentClient paymentClient;


    public BookingController(DoctorClient doctorClient, PatientClient patientClient, BookingConfirmationRepository bookingConfirmationRepository, PaymentClient paymentClient) {
        this.doctorClient = doctorClient;
        this.patientClient = patientClient;
        this.bookingConfirmationRepository = bookingConfirmationRepository;

        this.paymentClient = paymentClient;
    }

    @PostMapping("/create")
    public StripeResponse createBooking(@RequestParam long doctorId,
                                        @RequestParam long patientId,
                                        @RequestParam LocalDate date,
                                        @RequestParam LocalTime time){
        Patient patient = patientClient.getPatientById(patientId);
        Doctor doctor = doctorClient.getDoctorById(doctorId);

        boolean available = false;

        List<DoctorAppointmentSchedule> schedules = doctor.getSchedules();
        if(schedules != null){
            for(DoctorAppointmentSchedule s : schedules){
                if(date.equals(s.getDate())){
                    List<TimeSlots> slots = s.getTimeSlots();
                    if(slots != null){
                        for(TimeSlots ts : slots){
                            if(time.equals(ts.getTime())){
                                available = true;
                                break;
                            }
                        }
                    }
                }
                if(available) break;
            }
        }
        if(!available){
            throw new RuntimeException("slot not available for this doctor");
        }
        if(bookingConfirmationRepository.existsByDoctorIdAndDateAndTime(doctorId,date,time)){
            throw new slotAlreadyBookedException("This slot is already booked for this doctor");
        }
        BookingConfirmationDetails b = new BookingConfirmationDetails();
        b.setDoctorId(doctorId);
        b.setPatienId(patientId);
        b.setDoctorName(doctor.getName());
        b.setPatientName(patient.getName());
        b.setAddress(doctor.getAddress());
        b.setDate(date);
        b.setTime(time);
        b.setStatus(false);

        BookingConfirmationDetails saved = bookingConfirmationRepository.save(b);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setName(saved.getPatientName());
        productRequest.setAmount(80000L);
        productRequest.setCurrency("INR");
        productRequest.setQuantity(1L);
        productRequest.setBookingId(saved.getId());

        StripeResponse stripeResponse = paymentClient.checkout(productRequest);

        return stripeResponse;

    }

    @GetMapping("/by-id")
    public BookingConfirmationDetails getBooking(@RequestParam Long bookingId){
        return bookingConfirmationRepository.findById(bookingId).
                orElseThrow(() -> new RuntimeException("Booking not found: "+bookingId));
    }

    @PutMapping("/status")
    public BookingConfirmationDetails updateBookingStatus(@RequestParam Long bookingId,
                                                          @RequestParam boolean status){
        BookingConfirmationDetails booking = bookingConfirmationRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        booking.setStatus(status);

        return bookingConfirmationRepository.save(booking);
    }

    @PostMapping("/confirm")
    public String confirmBooking(@RequestParam Long bookingId,
                                 @RequestParam String sessionId) {
        // for now just print sessionId to confirm it is reaching booking-service
        System.out.println("Confirm called. bookingId=" + bookingId + " sessionId=" + sessionId);

        BookingConfirmationDetails booking = bookingConfirmationRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(true);
        bookingConfirmationRepository.save(booking);

        return "booking confirmed";
    }

}
