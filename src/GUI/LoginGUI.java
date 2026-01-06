package GUI;

import StaffManagement.Staff;
import java.awt.*;
import java.util.Map;
import javax.swing.*;
import reservation_ticketing.Passenger;
import service_management.Database;
import service_management.FileOp;

public class LoginGUI extends JFrame {
    private Database database;

    public LoginGUI(Database database) {
        this.database = database;
        setTitle("Airline System - Login");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel(new BorderLayout());
        JPanel center = new JPanel(new GridLayout(3, 1, 8, 8));

        JButton btnUserLogin = new JButton("User Login");
        JButton btnUserSignup = new JButton("User Sign Up");
        JButton btnAdminLogin = new JButton("Admin Login");

        center.add(btnUserLogin);
        center.add(btnUserSignup);
        center.add(btnAdminLogin);

        p.add(new JLabel("Choose login type:"), BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);

        add(p);

        btnUserSignup.addActionListener(e -> doUserSignUp());

        btnUserLogin.addActionListener(e -> doUserLogin());

        btnAdminLogin.addActionListener(e -> doAdminLogin());
    }

    private void doUserSignUp() {
        JTextField tfId = new JTextField();
        JTextField tfName = new JTextField();
        JTextField tfSurname = new JTextField();
        JTextField tfContact = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0,2));
        panel.add(new JLabel("ID (numeric):")); panel.add(tfId);
        panel.add(new JLabel("Name:")); panel.add(tfName);
        panel.add(new JLabel("Surname:")); panel.add(tfSurname);
        panel.add(new JLabel("Contact number:")); panel.add(tfContact);

        int res = JOptionPane.showConfirmDialog(this, panel, "Sign Up", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        long id; long contact;
        try {
            id = Long.parseLong(tfId.getText().trim());
            contact = Long.parseLong(tfContact.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "ID and contact must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String name = tfName.getText().trim();
        String surname = tfSurname.getText().trim();

        Passenger p = new Passenger(id, name, surname, contact);
        database.getPassengers().put(p.getPassengerId(), p);
        FileOp.saveFile("src/passengers.csv", database.getPassengers().values(), false, true, "passengerId,name,surname,contactNumber");

        JOptionPane.showMessageDialog(this, "Sign up successful. Launching user interface.", "Success", JOptionPane.INFORMATION_MESSAGE);
        // open main GUI without admin panel (pass the new passenger as logged-in user)
        AirlineGUI gui = new AirlineGUI(database, false, p);
        gui.setVisible(true);
        
        // Start the flight expiration checker thread
        Thread flightExpirationThread = new Thread(gui);
        flightExpirationThread.setDaemon(false);
        flightExpirationThread.start();
        
        this.dispose();
    }

    private void doUserLogin() {
        String idStr = JOptionPane.showInputDialog(this, "Enter your Passenger ID:", "User Login", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null || idStr.trim().isEmpty()) return;
        try {
            long id = Long.parseLong(idStr.trim());
            if (database.getPassengers().containsKey(id)) {
                Passenger p = database.getPassengers().get(id);
                AirlineGUI gui = new AirlineGUI(database, false, p);
                gui.setVisible(true);
                
                // Start the flight expiration checker thread
                Thread flightExpirationThread = new Thread(gui);
                flightExpirationThread.setDaemon(false);
                flightExpirationThread.start();
                
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Passenger ID not found. Please sign up first.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doAdminLogin() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Staff ID (numeric):", "Admin Login", JOptionPane.PLAIN_MESSAGE);
        if (idStr == null || idStr.trim().isEmpty()) return;
        try {
            long id = Long.parseLong(idStr.trim());
            Map<Long, Staff> staffs = database.getStaffMembers();
            if (staffs == null || !staffs.containsKey(id)) {
                JOptionPane.showMessageDialog(this, "Staff ID not found.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Staff s = staffs.get(id);
            if (s == null || s.getRole() == null || !s.getRole().equalsIgnoreCase("admin")) {
                JOptionPane.showMessageDialog(this, "Staff is not an admin. Access denied.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // successful admin login
            AirlineGUI gui = new AirlineGUI(database, true, null);
            gui.setVisible(true);
            
            // Start the flight expiration checker thread
            Thread flightExpirationThread = new Thread(gui);
            flightExpirationThread.setDaemon(false);
            flightExpirationThread.start();
            
            this.dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Staff ID must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
