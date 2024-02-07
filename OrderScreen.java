package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class OrderScreen extends JFrame {

    private JComboBox<String> pastryTypeComboBox;
    private JComboBox<String> tasteComboBox;
    private List<Pair<String, String>> pastryTypes = new ArrayList<>();
    private Map<String, List<String>> tasteMap = new HashMap<>();
    private JDateChooser dateChooser;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneNumberField;
    private JTextField occasionField;



    void initUI() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        DatabaseManager.createTable();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setSize(600, 750);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel label = new JLabel("Orders information");

        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel dateLabel = new JLabel("Select date:");
        dateLabel.setBackground(Color.decode("#FF9F00"));
        dateLabel.setPreferredSize(new Dimension(300, 20));
        dateLabel.setOpaque(true);
        mainPanel.add(dateLabel, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(dateChooser, gbc);

        SpinnerDateModel spinnerDateModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(spinnerDateModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        JFormattedTextField timeTextField = ((JSpinner.DefaultEditor) timeSpinner.getEditor()).getTextField();
        timeTextField.setColumns(8);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel timeLabel = new JLabel("Select time:");
        timeLabel.setBackground(Color.decode("#FF9F00"));
        timeLabel.setPreferredSize(new Dimension(300, 20));
        timeLabel.setOpaque(true);
        mainPanel.add(timeLabel, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(timeSpinner, gbc);

        firstNameField = new JTextField();
        firstNameField.setPreferredSize(new Dimension(150, 30));
        gbc.gridy++;
        JLabel firstNameLabel = new JLabel("First name:");
        firstNameLabel.setBackground(Color.decode("#FF9F00"));
        firstNameLabel.setPreferredSize(new Dimension(300, 20));
        firstNameLabel.setOpaque(true);
        mainPanel.add(firstNameLabel, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(firstNameField, gbc);

        lastNameField = new JTextField();
        lastNameField.setPreferredSize(new Dimension(150, 30));
        gbc.gridy++;
        JLabel lastNameLabel = new JLabel("Last name:");
        lastNameLabel.setBackground(Color.decode("#FF9F00"));
        lastNameLabel.setPreferredSize(new Dimension(300, 20));
        lastNameLabel.setOpaque(true);
        mainPanel.add(lastNameLabel, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(lastNameField, gbc);

        phoneNumberField = new JTextField();
        phoneNumberField.setPreferredSize(new Dimension(150, 30));
        gbc.gridy++;
        JLabel phoneNumberLabel = new JLabel("Phone number:");
        phoneNumberLabel.setBackground(Color.decode("#FF9F00"));
        phoneNumberLabel.setPreferredSize(new Dimension(300, 20));
        phoneNumberLabel.setOpaque(true);
        mainPanel.add(phoneNumberLabel, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(phoneNumberField, gbc);

        occasionField = new JTextField();
        occasionField.setPreferredSize(new Dimension(150, 30));
        gbc.gridy++;
        JLabel occasionLabel = new JLabel("Occasion:");
        occasionLabel.setBackground(Color.decode("#FF9F00"));
        occasionLabel.setPreferredSize(new Dimension(300, 20));
        occasionLabel.setOpaque(true);
        mainPanel.add(occasionLabel, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(occasionField, gbc);

        String filePath = "src/main/java/org/example/data/data.json";

        List<Pair<String, String>> pastryTypes = new ArrayList<>();
        List<Pair<String, String>> tastes = new ArrayList<>();

        try (FileReader fileReader = new FileReader(filePath)) {
            JsonObject jsonData = JsonParser.parseReader(fileReader).getAsJsonObject();
            JsonArray bakedGoodsArray = jsonData.getAsJsonArray("baked_goods");

            for (int i = 0; i < bakedGoodsArray.size(); i++) {
                JsonObject bakedGood = bakedGoodsArray.get(i).getAsJsonObject();
                String bakedName = bakedGood.get("baked_name").getAsString();
                pastryTypes.add(new Pair<>(bakedName, bakedName));

                List<String> tastesForBakedGood = new ArrayList<>();

                JsonArray typesArray = bakedGood.getAsJsonArray("types");

                for (int j = 0; j < typesArray.size(); j++) {
                    JsonObject type = typesArray.get(j).getAsJsonObject();
                    String typeName = type.get("name").getAsString();
                    String uniqueID = typeName;
                    tastesForBakedGood.add(uniqueID);
                    tastes.add(new Pair<>(bakedName, uniqueID));
                }

                tasteMap.put(bakedName, tastesForBakedGood);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading pastry types and tastes from JSON file.", e);
        }

        pastryTypeComboBox = new JComboBox<>();
        tasteComboBox = new JComboBox<>();

        for (Pair<String, String> pair : pastryTypes) {
            pastryTypeComboBox.addItem(pair.getSecond());
        }


        pastryTypeComboBox.addActionListener(e -> {
            String selectedPastryType = (String) pastryTypeComboBox.getSelectedItem();
            List<String> tastesForSelectedPastry = tasteMap.get(selectedPastryType);
            tasteComboBox.removeAllItems();
            for (String taste : tastesForSelectedPastry){
                tasteComboBox.addItem(taste);
            }
        });


        gbc.gridy++;
        JLabel pastryLabel = new JLabel("Pastry type:");
        pastryLabel.setBackground(Color.decode("#FF9F00"));
        pastryLabel.setPreferredSize(new Dimension(300, 20));
        pastryLabel.setOpaque(true);
        mainPanel.add(pastryLabel, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(pastryTypeComboBox, gbc);

        gbc.gridy++;
        JLabel tasteLabel = new JLabel("Taste:");
        tasteLabel.setBackground(Color.decode("#FF9F00"));
        tasteLabel.setPreferredSize(new Dimension(300, 20));
        tasteLabel.setOpaque(true);
        mainPanel.add(tasteLabel, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(tasteComboBox, gbc);


        dateChooser.addPropertyChangeListener("date", evt -> checkSelectedDate());

        JTextField commentField = new JTextField();
        commentField.setPreferredSize(new Dimension(150, 30));

        gbc.gridy++;
        JLabel commentLabel = new JLabel("Comment:");
        commentLabel.setBackground(Color.decode("#FF9F00"));
        commentLabel.setPreferredSize(new Dimension(300, 20));
        commentLabel.setOpaque(true);
        mainPanel.add(commentLabel, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        mainPanel.add(commentField, gbc);


        gbc.gridy++;
        gbc.gridx = 0;
        JButton submitButton = new JButton("Submit");
        submitButton.setBackground(Color.decode("#F28500"));
        submitButton.setPreferredSize(new Dimension(300, 30));
        submitButton.addActionListener(e ->  {
                try {
                    String firstName = firstNameField.getText();
                    String lastName = lastNameField.getText();
                    String phoneNumber = phoneNumberField.getText();
                    String occasion = occasionField.getText();
                    String pastryType = (String) pastryTypeComboBox.getSelectedItem();
                    String pastryTaste = (String) tasteComboBox.getSelectedItem();
                    String comment = commentField.getText();

                    String selectedDate = dateFormat.format(dateChooser.getDate());
                    String selectedTime = timeFormat.format(timeSpinner.getValue());

                    if (validateFields()) {
                        String message = "Submitted Information:\n" +
                                "First Name: " + firstName + "\n" +
                                "Last Name: " + lastName + "\n" +
                                "Phone Number: " + phoneNumber + "\n" +
                                "Occasion: " + occasion + "\n" +
                                "Pastry Type: " + pastryType + "\n" +
                                "Taste: " + pastryTaste + "\n" +
                                "Pickup date: " + selectedDate + "\n" +
                                "Pickup time: " + selectedTime + "\n" +
                                "Comment: " + comment;
                        JOptionPane.showMessageDialog(OrderScreen.this, message, "Submission", JOptionPane.INFORMATION_MESSAGE);

                        DatabaseManager.insertOrder(firstName, lastName, phoneNumber, occasion,
                                pastryType, pastryTaste, selectedDate, selectedTime, comment);

                        JOptionPane.showMessageDialog(OrderScreen.this, "Order submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }

                    validatePhoneNumber();
                    validatePhoneNumberLength();

                } catch (Exceptions.InvalidPhoneNumberException ex) {
                    JOptionPane.showMessageDialog(OrderScreen.this, ex.getMessage(), "Invalid Phone Number", JOptionPane.WARNING_MESSAGE);
                } catch (Exceptions.ShortPhoneNumberException ex) {
                    JOptionPane.showMessageDialog(OrderScreen.this, ex.getMessage(), "Short Phone Number", JOptionPane.WARNING_MESSAGE);
                } catch (Exceptions.LongPhoneNumberException ex) {
                    JOptionPane.showMessageDialog(OrderScreen.this, ex.getMessage(), "Long Phone Number", JOptionPane.WARNING_MESSAGE);
                } catch (Exceptions.DatabaseConnectionException ex) {
                    JOptionPane.showMessageDialog(OrderScreen.this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(OrderScreen.this, "Error processing the order.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });


        mainPanel.add(submitButton, gbc);


        getContentPane().add(mainPanel);
    }


    private void validatePhoneNumber() throws Exceptions.InvalidPhoneNumberException {
        String phoneNumber = phoneNumberField.getText();
        if (!phoneNumber.matches("\\d+")) {
            throw new Exceptions.InvalidPhoneNumberException();
        }
    }

    private void validatePhoneNumberLength() throws Exceptions.ShortPhoneNumberException, Exceptions.LongPhoneNumberException {
        String phoneNumber = phoneNumberField.getText();
        if (phoneNumber.length() < 9) {
            throw new Exceptions.ShortPhoneNumberException();
        } else if (phoneNumber.length() > 9) {
            throw new Exceptions.LongPhoneNumberException();
        }
    }

    private boolean validateFields() throws Exceptions.InvalidOrderInformationException {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || phoneNumberField.getText().isEmpty() ||
                occasionField.getText().isEmpty() || pastryTypeComboBox.getSelectedItem() == null || tasteComboBox.getSelectedItem() == null) {
            throw new Exceptions.InvalidOrderInformationException();
        }

        return checkSelectedDate();
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OrderScreen orderScreen = new OrderScreen();
            orderScreen.initUI();
            orderScreen.setVisible(true);
        });
    }


    private boolean checkSelectedDate() {
        Date currentDate = new Date();
        Date selectedDate = dateChooser.getDate();

        if (selectedDate != null) {
            long timeDiff = selectedDate.getTime() - currentDate.getTime();
            long daysDiff = timeDiff / (24 * 60 * 60 * 1000);

            if (daysDiff < 2) {
                JOptionPane.showMessageDialog(OrderScreen.this, "Please select a date at least 2 days from today.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                dateChooser.setDate(null); // Reset the dateChooser
                return false;
            }
        }

        return true;
    }


}