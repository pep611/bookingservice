package com.example.bookingservice;

public class BookingSearchResponse {

    private String searchResult;
    private Double price;

    public BookingSearchResponse() {}

    public BookingSearchResponse(String searchResult, Double price) {
        this.searchResult = searchResult;
        this.price = price;
    }

    public String getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(String searchResult) {
        this.searchResult = searchResult;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
