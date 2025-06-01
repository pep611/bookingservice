package com.example.bookingservice;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;

@Service
public class Consumer {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PassengerService passengerService;

    @KafkaListener(topics = "booking-events", groupId = "booking-group")
    @Transactional
    public void consumeBookingEvents(String message) throws IOException
    {
        ObjectMapper mapper  = new ObjectMapper();
        BookingEvent datum =  mapper.readValue(message, BookingEvent.class);
        logger.info("#### -> Received booking confirmation message: -> {}", datum.getStatus());
        logger.info("#### -> Received booking confirmation message: BookingId: {}, Status: {}", datum.getBookingId(), datum.getStatus());
        // Fetch booking by bookingId
        // BookingModel booking = bookingRepository.findById((long) datum.getBookingId()).orElse(null);
        BookingModel booking = bookingRepository.findByBookingNumber(datum.getBookingId()).orElse(null);
        if (booking != null) {
            booking.setStatus(datum.getStatus());
            logger.info("Saving booking with updated status: " + booking.getStatus());
            bookingRepository.save(booking);
            logger.info("Booking status updated for bookingId: " + datum.getBookingId());

            // Save passenger details
            passengerService.savePassenger(datum.getBookingId(), Instant.now());
            logger.info("Passenger saved for bookingId: " + datum.getBookingId());
        } else {
            logger.warn("Booking not found for bookingId: " + datum.getBookingId());
        }
    }
}
