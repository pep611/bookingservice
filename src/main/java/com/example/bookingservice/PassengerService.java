package com.example.bookingservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@Component
public class PassengerService {

    @Autowired
    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }
    public void savePassenger(int bookingId, Instant dateOfPayment) {
        PassengerModel passenger = new PassengerModel();
        passenger.setBookingNumber(bookingId);
        passenger.setDateOfPayment(dateOfPayment);
        passengerRepository.save(passenger);
    }
}
