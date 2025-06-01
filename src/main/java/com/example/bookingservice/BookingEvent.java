package com.example.bookingservice;

import java.io.Serializable;

public class BookingEvent implements Serializable{

    private int bookingId;
    private String status;

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
