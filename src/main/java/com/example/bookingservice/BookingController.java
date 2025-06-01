package com.example.bookingservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;

@RestController
public class BookingController {

    private final ClientService clientService;
    private final BookingRepository bookingRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Producer producer;

    @Autowired
    public BookingController(ClientService clientService,
                             BookingRepository bookingRepository,
                             KafkaTemplate<String, String> kafkaTemplate,
                             Producer producer) {
        this.clientService = clientService;
        this.bookingRepository = bookingRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.producer = producer;
    }

    @GetMapping("/api/booking/search")
    public String searchBuses(@RequestParam String source, @RequestParam String destination) {
        return clientService.sendSourceAndDestination(source, destination);
    }

    @GetMapping("/api/booking/price")
    public Double getPrice(@RequestParam String source, @RequestParam String destination) {
        return clientService.fetchPrice(source, destination);
    }

    @GetMapping("/api/booking/aggregate")
    public BookingSearchResponse aggregate(
            @RequestParam String source,
            @RequestParam String destination) {
        return clientService.aggregateSearchAndPrice(source, destination);
    }

    @PostMapping("/api/booking/checkseats")
    public boolean checkSeats(@RequestBody SeatCheckRequest request) throws JsonProcessingException {
        boolean available = clientService.isSeatsAvailable(
                request.getSource(),
                request.getDestination(),
                request.getRequestedSeats()
        );
        if (available) {
            BookingModel booking = new BookingModel();
            booking.setSource(request.getSource());
            booking.setDestination(request.getDestination());
            booking.setNoOfSeats(request.getRequestedSeats());
            booking.setStatus("Pending");
            booking.setBookingDate(java.time.Instant.now());
            booking.setBusNumber("225");// Assuming you have a method to get current date and time
            // Set other fields as needed
            bookingRepository.save(booking);

            PaymentEvent event = new PaymentEvent();
            event.setBookingId(booking.getBookingNumber());
            event.setBusNumber("225");
            event.setNoOfSeats(request.getRequestedSeats());
            try {
                producer.pubDomainEvent(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return available;
    }
}
