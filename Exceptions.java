package org.example;

public class Exceptions {

    public static class InvalidOrderInformationException extends Exception {
        public InvalidOrderInformationException() {
            super("Invalid order information. Please fill in all the required fields.");

        }
    }

    public class DatabaseConnectionException extends RuntimeException {
        public DatabaseConnectionException(Throwable cause) {
            super("Error connecting to the database.", cause);
        }
    }

    public static class InvalidPhoneNumberException extends Exception {
        public InvalidPhoneNumberException() {
            super("Invalid phone number. Only numbers are allowed.");
        }
    }



    public static class ShortPhoneNumberException extends Exception {
        public ShortPhoneNumberException() {
            super("The phone number is too short. Must be 9 digits");
        }
    }

    public static class LongPhoneNumberException extends Exception {
        public LongPhoneNumberException() {
            super("The phone number is too long. Must be 9 digits");
        }
    }


}
