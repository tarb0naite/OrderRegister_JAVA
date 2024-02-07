package org.example;

import com.toedter.calendar.JDateChooser;
import org.sqlite.JDBC;

import javax.swing.*;
import javax.swing.border.Border;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collections;
import java.util.List;


class CustomDay extends JPanel {
    private final String fullDate;
    private final boolean isSelected;
    private final Pair<Integer, Boolean> orderInfo;

    public CustomDay(LocalDate date, String selectedDate, boolean hasOrder) {
        fullDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        isSelected = fullDate.equals(selectedDate);
        orderInfo = new Pair<>(date.getDayOfMonth(), hasOrder);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(70, 70));
        setBackground(isSelected ? Color.WHITE : Color.decode("#F28500"));

        String day = date.isEqual(LocalDate.now()) ? "Today" : date.format(DateTimeFormatter.ofPattern("EEE"));
        String dayNumber = date.format(DateTimeFormatter.ofPattern("d"));

        JLabel dayLabel = new JLabel(day);
        dayLabel.setForeground(isSelected ? Color.ORANGE : Color.WHITE);
        dayLabel.setFont(new Font("Arial", Font.BOLD, 12));

        add(dayLabel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel dayNumberLabel = new JLabel(dayNumber);
        dayNumberLabel.setForeground(isSelected ? Color.ORANGE : Color.WHITE);
        dayNumberLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        add(dayNumberLabel);


        if (hasOrder){
            JLabel dotLabel = new JLabel("☻");
            dotLabel.setForeground(Color.RED);
            dotLabel.setPreferredSize(new Dimension(30, 30));
            add(dotLabel);
        }

        setBorder(new RoundedCornerBorder(12));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(isSelected ? Color.WHITE : Color.decode("#FF9F00"));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(isSelected ? Color.WHITE : Color.decode("#F28500"));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (hasOrder){
                    showOrdersForDay(fullDate);
                }
            }
        });
    }

    private void showOrdersForDay(String selectedDate) {
        ((CustomCalendar) SwingUtilities.getRoot(this)).filterAndDisplayCards(selectedDate);
    }


    public String getFullDate() {
        return fullDate;
    }


    private class RoundedCornerBorder implements Border {
        private final int radius;

        private RoundedCornerBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.BLACK);
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius+1, radius+1, radius+2, radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
@FunctionalInterface
interface ButtonClickListener {
    void onClick();
}

class RoundButton extends JButton {
    public RoundButton(String label, ButtonClickListener clickListener) {
        super(label);

        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width, size.height);
        setPreferredSize(size);
        setBackground(Color.decode("#F28500"));


        addActionListener(e -> clickListener.onClick());
    }

    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.decode("#F28500"));
        } else {
            g.setColor(getBackground());
        }

        int diameter = Math.min(getWidth(), getHeight());
        g.fillOval(0, 0, diameter - 1, diameter - 1);

        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        int diameter = Math.min(getWidth(), getHeight());
        g.drawOval(0, 0, diameter - 1, diameter - 1);
    }

    Shape shape;

    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            int diameter = Math.min(getWidth(), getHeight());
            shape = new Ellipse2D.Float(0, 0, diameter, diameter);
        }

        return shape.contains(x, y);
    }
}

class CustomCalendar extends JFrame {
    private final String selectedDate;

    private JComboBox<String> filterComboBox;




    public CustomCalendar(String selectedDate) {
        this.selectedDate = selectedDate;
        initUI();
    }

    void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 750);

        LocalDate currentDate = LocalDate.now();

        ImageIcon calendarIcon = new ImageIcon("src/main/java/icons/calendar-days.png");
        Image scaledCalendarIcon = calendarIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledCalendarIcon);


        JButton roundButton = new JButton();
        roundButton.setIcon(scaledIcon);
        roundButton.setBackground(Color.decode("#F28500"));
        roundButton.setPreferredSize(new Dimension(30, 30));
        roundButton.setFocusPainted(false);
        roundButton.setBorderPainted(false);
        roundButton.setContentAreaFilled(false);

        JTextField searchBar = new JTextField();
        searchBar.setPreferredSize(new Dimension(150, 30));

        ImageIcon searchIcon = new ImageIcon("src/main/java/icons/search-interface-symbol.png");
        Image scaledSearchIcon = searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledSearch = new ImageIcon(scaledSearchIcon);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setPreferredSize(new Dimension(200, 30));
        searchPanel.setBackground(Color.decode("#F28500"));

        JLabel searchIconLabel = new JLabel(scaledSearch);
        searchIconLabel.setPreferredSize(new Dimension(20, 20));

        searchPanel.add(searchIconLabel, BorderLayout.WEST);
        searchPanel.add(searchBar, BorderLayout.CENTER);

        JTextField searchBarText = new JTextField("Search...");
        searchBarText.setPreferredSize(new Dimension(150, 30));

        searchBarText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBarText.getText().equals("Search...")){
                    searchBarText.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchBarText.getText().isEmpty()){
                    searchBarText.setText("Search...");
                }
            }
        });


        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(Color.decode("#F28500"));
        topPanel.setPreferredSize(new Dimension(600, 40));
        topPanel.add(searchPanel);
        topPanel.add(roundButton);

       ButtonClickListener orderAction = () -> {
          OrderScreen orderScreen = new OrderScreen();
          orderScreen.initUI();
          orderScreen.setVisible(true);
       };

        RoundButton circleButton = new RoundButton("Add", orderAction);
        circleButton.addActionListener(e -> {
            System.out.println("Circle Button Clicked");
            OrderScreen orderScreen = new OrderScreen();
            orderScreen.initUI();
            orderScreen.setVisible(true);
        });

        JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        daysPanel.setBackground(Color.decode("#F28500"));

        List<LocalDate> datesWithOrders = DatabaseManager.getDatesWithOrders(); // Replace this with your actual method to get dates with orders


        for (int i = 0; i < 7; i++) {
            LocalDate date = currentDate.plusDays(i);
            boolean hasOrder = datesWithOrders.contains(date);
            CustomDay customDay = new CustomDay(date, selectedDate, hasOrder);
            customDay.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Selected date: " + customDay.getFullDate());
                }
            });
            daysPanel.add(customDay);
        }

        JPanel gapPanel = new JPanel();
        gapPanel.setPreferredSize(new Dimension(10, 20));

        String[] filterOptions = {"All", "Closest", "This Week", "Expired"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setPreferredSize(new Dimension(150, 30));
        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFilter = (String) filterComboBox.getSelectedItem();
                applyFilter(selectedFilter);
            }
        });


        setLayout(new FlowLayout(FlowLayout.CENTER));
        getContentPane().setBackground(Color.WHITE);
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(gapPanel, BorderLayout.NORTH);
        getContentPane().add(daysPanel, BorderLayout.CENTER);
        getContentPane().add(filterComboBox, BorderLayout.CENTER);
        getContentPane().add(refresh, BorderLayout.CENTER);

        List<String> orderIDs = DatabaseManager.fetchAllOrderIDs();

        for (String orderID : orderIDs){
            JPanel cardPanel = createCardPanel(orderID);
            getContentPane().add(cardPanel, BorderLayout.CENTER);
        }
        getContentPane().add(circleButton, BorderLayout.SOUTH);

        setVisible(true);

        refresh.setBackground(Color.decode("#FF9F00"));
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshUI();
            }
        });

    }

    ImageIcon refreshIcon = new ImageIcon("src/main/java/icons/rotate.png");
    Image scaledRefreshIcon = refreshIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
    ImageIcon scaledRefresh = new ImageIcon(scaledRefreshIcon);
    JButton refresh = new JButton(scaledRefresh);


    private JPanel createCardPanel(String orderID) {
        JPanel cardPanel = new JPanel();
        cardPanel.setPreferredSize(new Dimension(550, 100));
        cardPanel.setBackground(Color.decode("#FF9F00"));

        JLabel cardLabel = new JLabel("Order information");
        cardPanel.add(cardLabel);

        ImageIcon dotsIcon = new ImageIcon("src/main/java/icons/dots.png");
        Image scaledDotsIcon = dotsIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledDots = new ImageIcon(scaledDotsIcon);

        JLabel dotsLabel = new JLabel(scaledDots);
        cardPanel.setLayout(new BorderLayout());

        dotsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopupMenu(dotsLabel, e.getX(), e.getY(), orderID);
            }
        });

        cardPanel.add(dotsLabel, BorderLayout.LINE_END);

        String orderInfo = DatabaseManager.fetchOrderInfoFromDatabase(orderID);
        JLabel orderInfoLabel = new JLabel(orderInfo);
        cardPanel.add(orderInfoLabel, BorderLayout.CENTER);

        cardPanel.putClientProperty("orderID", orderID);

        return cardPanel;
    }


    static String processResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return
                    "<html>Client's full name " + resultSet.getString("first_name") +
                            "" + resultSet.getString("last_name") +
                            "<br>Pastry Type: " + resultSet.getString("pastry_type") +
                            "<br>Pastry taste: " + resultSet.getString("taste") +
                            "<br>Pick up: " + resultSet.getString("pickup_date") + " " +resultSet.getString("pickup_time");

        } else {
            return "Order not found";
        }
    }

    private void applyFilter(String selectedFilter) {
        switch (selectedFilter) {
            case "Today":

                break;
            case "This Week":

                break;
            case "Custom":

                break;
            default:

                displayAllOrders();
                break;
        }
    }

    private void showPopupMenu(Component invoker, int x, int y, String orderId){
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem markAsDoneItem = new JMenuItem("Mark as done");
        JMenuItem editItem = new JMenuItem("Edit");
        JMenuItem deleteItem = new JMenuItem("Delete");

        markAsDoneItem.addActionListener(e -> markAsDoneAction(orderId));
        editItem.addActionListener(e -> editAction(orderId));
        deleteItem.addActionListener(e -> deleteAction(orderId));

        popupMenu.add(markAsDoneItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        popupMenu.show(invoker, x, y);
    }
    private void markAsDoneAction(String orderID) {

        DatabaseManager.createDoneOrdersTable();
        SwingUtilities.invokeLater(() -> {
            new CustomCalendar(LocalDate.now().toString());
        });
        String orderInfo = DatabaseManager.fetchOrderInfoFromDatabase(orderID);

        DatabaseManager.insertDoneOrder(Integer.parseInt(orderID), orderInfo);

        DatabaseManager.deleteOrder(orderID);

        refreshUI();

        System.out.println("Mark as Done action for order ID: " + orderID);
    }


    private void refreshUI() {
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getBackground().equals(Color.decode("#FF9F00"))) {
                getContentPane().remove(component);
            }
        }

        List<String> orderIDs = DatabaseManager.fetchAllOrderIDs();
        for (String orderID : orderIDs) {
            JPanel cardPanel = createCardPanel(orderID);
            getContentPane().add(cardPanel, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }




    private void editAction(String orderID) {

        System.out.println("Edit action for order ID: " + orderID);
    }


    private void deleteAction(String orderID) {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this order?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (dialogResult == JOptionPane.YES_OPTION){
            DatabaseManager.deleteOrder(orderID);
        }

        System.out.println("Delete action for order ID: " + orderID);

    }

    public void filterAndDisplayCards(String selectedDate) {
        List<String> ordersForDay = DatabaseManager.getOrdersForDay(selectedDate);

        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getClientProperty("orderID") != null) {
                String orderID = (String) ((JPanel) component).getClientProperty("orderID");

                if (ordersForDay.contains(orderID)) {
                    component.setVisible(true);
                } else {
                    component.setVisible(false);
                }
            }
        }

        revalidate();
        repaint();
    }



    private void displayAllOrders() {
        List<Order> allOrders = getAllOrdersFromDatabase();
        Collections.sort(allOrders, new OrderComparator());
        displayOrders(allOrders);
    }

    private List<Order> getAllOrdersFromDatabase() {
        List<String> orderIDs = DatabaseManager.fetchAllOrderIDs();
        List<Order> orders = new ArrayList<>();
        for (String orderID : orderIDs) {
            String orderInfo = DatabaseManager.fetchOrderInfoFromDatabase(orderID);
            Order order = new Order(orderID, orderInfo);
            orders.add(order);
        }
        return orders;
    }


    private void displayOrders(List<Order> orders) {

        removeExistingOrderPanels();

        for (Order order : orders) {
            JPanel cardPanel = createCardPanel(String.valueOf(order));
            getContentPane().add(cardPanel, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }

    private void removeExistingOrderPanels() {
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getClientProperty("orderID") != null) {
                getContentPane().remove(component);
            }
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomCalendar(LocalDate.now().toString());
        });
    }


}
