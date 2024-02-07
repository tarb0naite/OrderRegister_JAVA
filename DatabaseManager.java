package org.example;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.example.CustomCalendar.processResultSet;


public class DatabaseManager {

    private static final String JDBC_URL = "jdbc:sqlite:orders.db";

    public static void createTable(){
        try{
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(JDBC_URL)){
                String createTableSQL = "CREATE TABLE IF NOT EXISTS orders (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "first_name TEXT," +
                        "last_name TEXT," +
                        "phone_number TEXT," +
                        "occasion TEXT," +
                        "pastry_type TEXT," +
                        "taste TEXT," +
                        "pickup_date TEXT," +
                        "pickup_time TEXT," +
                        "comment TEXT)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)){
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDoneOrdersTable() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
                String createDoneOrdersTableSQL = "CREATE TABLE IF NOT EXISTS doneOrders (" +
                        "id INTEGER PRIMARY KEY, "+
                        "first_name TEXT," +
                        "last_name TEXT," +
                        "phone_number TEXT," +
                        "occasion TEXT," +
                        "pastry_type TEXT," +
                        "taste TEXT," +
                        "pickup_date TEXT," +
                        "pickup_time TEXT," +
                        "comment TEXT" +
                        "status TEXT)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(createDoneOrdersTableSQL)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void insertOrder(String firstName, String lastName, String phoneNumber, String occasion,
                                   String pastryType, String taste, String pickupDate, String pickupTime, String comment) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
                String insertOrderSQL = "INSERT INTO orders (first_name, last_name, " +
                        "phone_number, occasion, pastry_type, taste, pickup_date, pickup_time, comment)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setString(3, phoneNumber);
                    preparedStatement.setString(4, occasion);
                    preparedStatement.setString(5, pastryType);
                    preparedStatement.setString(6, taste);
                    preparedStatement.setString(7, pickupDate);
                    preparedStatement.setString(8, pickupTime);
                    preparedStatement.setString(9, comment);
                    preparedStatement.executeUpdate();

                    // Get the generated order ID
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int orderID = generatedKeys.getInt(1);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void insertDoneOrder(int orderID, String orderInfo) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
                String insertDoneOrderSQL = "INSERT INTO doneOrders (id, first_name, last_name, " +
                        "phone_number, occasion, pastry_type, taste, pickup_date, pickup_time, comment)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertDoneOrderSQL)) {
                    preparedStatement.setInt(1, orderID);
                    // Parse and set other order information from orderInfo string

                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String fetchOrderInfoFromDatabase(String orderID) {
        try {

            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
                String fetchOrderSQL = "SELECT * FROM orders WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(fetchOrderSQL)) {
                    preparedStatement.setString(1, orderID);


                    return processResultSet(preparedStatement.executeQuery());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching order information";
        }
    }



    public static List<String> fetchAllOrderIDs(){
        List<String> orderIDs = new ArrayList<>();
        try{
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(JDBC_URL)){
                String fetchOderIDsSQL = "SELECT id FROM orders";
                try(PreparedStatement preparedStatement = connection.prepareStatement(fetchOderIDsSQL)){
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()){
                        orderIDs.add(String.valueOf(resultSet.getInt("id")));
                    }
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return orderIDs;
    }


    public static void deleteOrder(String orderID){
        try {
            Class.forName("org.sqlite.JDBC");
            try(Connection connection = DriverManager.getConnection(JDBC_URL)){
                String deleteOrderSQL = "DELETE FROM orders WHERE id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteOrderSQL)){
                    preparedStatement.setInt(1, Integer.parseInt(orderID));
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<LocalDate> getDatesWithOrders(){
        List<LocalDate> datesWithOrders = new ArrayList<>();

        try{
            Class.forName("org.sqlite.JDBC");
            try(Connection connection = DriverManager.getConnection(JDBC_URL);
                Statement statement = connection.createStatement()) {

                String query = "SELECT DISTINCT pickup_date FROM orders";
                ResultSet resultSet = statement.executeQuery(query);

                while(resultSet.next()) {
                    LocalDate orderDate = LocalDate.parse(resultSet.getString("pickup_date"));
                    datesWithOrders.add(orderDate);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }

            return  datesWithOrders;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<String> getOrdersForDay(String selectedDate) {
        List<String> ordersForDay = new ArrayList<>();

        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
                String getOrdersForDaySQL = "SELECT * FROM orders WHERE pickup_date = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(getOrdersForDaySQL)) {
                    preparedStatement.setString(1, selectedDate);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        String orderInfo = processResultSet(resultSet);
                        ordersForDay.add(orderInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ordersForDay;
    }



}
