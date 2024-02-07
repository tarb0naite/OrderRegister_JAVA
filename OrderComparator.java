package org.example;

import org.example.Order;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order o1, Order o2) {
        if (o1.getPickupDate() == null && o2.getPickupDate() == null) {
            return 0;
        } else if (o1.getPickupDate() == null) {
            return -1; // Treat null pickup date as less than non-null pickup date
        } else if (o2.getPickupDate() == null) {
            return 1; // Treat non-null pickup date as greater than null pickup date
        } else {
            return o1.getPickupDate().compareTo(o2.getPickupDate());
        }
    }
}
