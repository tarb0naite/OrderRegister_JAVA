package org.example;

import java.time.LocalDate;

public class Order  implements Comparable<Order> {
    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String occasion;
    private String pastryType;
    private String taste;
    private LocalDate pickupDate;
    private String pickupTime;
    private String comment;


    public Order(int id, String firstName, String lastName, String phoneNumber, String occasion, String pastryType, String taste, LocalDate pickupDate, String pickupTime, String comment) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.occasion = occasion;
        this.pastryType = pastryType;
        this.taste = taste;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.comment = comment;
    }

    public Order(String orderID, String orderInfo) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    @Override
    public int compareTo(Order other) {
        if (this.pickupDate == null && other.pickupDate == null) {
            return 0;
        } else if (this.pickupDate == null) {
            return -1; // Treat null pickup date as less than non-null pickup date
        } else if (other.pickupDate == null) {
            return 1; // Treat non-null pickup date as greater than null pickup date
        } else {
            return this.pickupDate.compareTo(other.pickupDate);
        }
    }
}
