package org.example;

// Custom annotation for author information
public @interface Author {
    String name() default "Unknown";

    String date();
}
