package com.example.bookingservice;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

@Service
public class ClientService {

    @LoadBalanced
    private final RestTemplate restTemplate = new RestTemplate();

    private final String inventoryServiceUrl = "http://localhost:8082/api/inventory/search";
    private final String bookingServiceUrl = "http://localhost:8081/api/price";

    public String sendSourceAndDestination(String source, String destination) {
        String uri = UriComponentsBuilder.fromHttpUrl(inventoryServiceUrl)
                .queryParam("source", source)
                .queryParam("destination", destination)
                .toUriString();

        // Assuming the other service returns a String response
        return restTemplate.getForObject(uri, String.class);
    }

    public Double fetchPrice(String source, String destination) {
        String uri = UriComponentsBuilder.fromHttpUrl(bookingServiceUrl)
                .queryParam("source", source)
                .queryParam("destination", destination)
                .toUriString();
        Double[] prices = restTemplate.getForObject(uri, Double[].class);
        return (prices != null && prices.length > 0) ? prices[0] : null;
    }

    public BookingSearchResponse aggregateSearchAndPrice(String source, String destination) {
        String searchResult = sendSourceAndDestination(source, destination);
        Double price = fetchPrice(source, destination);
        return new BookingSearchResponse(searchResult, price);
    }

    public boolean isSeatsAvailable(String source, String destination, int requestedSeats) {
        String uri = "http://localhost:8082/" + "/api/inventory/seats";
        SeatCheckRequest request = new SeatCheckRequest();
        request.setSource(source);
        request.setDestination(destination);
        request.setRequestedSeats(requestedSeats);

        SeatAvailabilityResponse[] responseArr = restTemplate.postForObject(uri, request, SeatAvailabilityResponse[].class);
        SeatAvailabilityResponse response = (responseArr != null && responseArr.length > 0) ? responseArr[0] : null;
        return response != null && requestedSeats <= response.getAvailableSeats();
    }
}
