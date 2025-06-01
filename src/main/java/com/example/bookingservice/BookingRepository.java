package com.example.bookingservice;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookingModel, Long> {
     Optional<BookingModel> findByBookingNumber(Integer bookingNumber);
}
