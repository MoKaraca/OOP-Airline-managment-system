# Logout Feature Implementation

## Overview
Added a logout feature that allows users and admins to exit the current GUI screen and return to the login screen without closing the entire application.

## Changes Made

### AirlineGUI.java

#### 1. Modified Constructor
- Added a main panel with a logout button at the top-right corner of the window
- The logout button is visible on all screens (Flight Search, Reservations, Admin panels)
- Restructured the GUI layout to include the logout button above the tabbed pane

```java
// Top panel with logout button
JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
JButton btnLogout = new JButton("Logout");
btnLogout.addActionListener(e -> performLogout());
topPanel.add(btnLogout);
mainPanel.add(topPanel, BorderLayout.NORTH);
```

#### 2. Added performLogout() Method
New private method that handles the logout process:
- Shows a confirmation dialog: "Are you sure you want to logout?"
- If user confirms (YES):
  - Creates a new LoginGUI instance with the current database
  - Shows the LoginGUI
  - Disposes of the current AirlineGUI window
- If user cancels (NO):
  - Does nothing, user stays in the current screen

```java
private void performLogout() {
    int confirm = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to logout?", 
        "Confirm Logout", 
        JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        LoginGUI login = new LoginGUI(database);
        login.setVisible(true);
        this.dispose();
    }
}
```

## How to Use

1. **Login** to the system (User Login, User Sign Up, or Admin Login)
2. **Navigate** through the various tabs and perform your tasks
3. **Click the "Logout" button** in the top-right corner of the window
4. **Confirm** the logout action when prompted
5. The AirlineGUI closes and the LoginGUI reappears
6. You can now login as a different user or perform other actions

## Benefits

- ✅ Users can switch accounts without restarting the application
- ✅ No data loss - the database remains loaded and intact
- ✅ Confirmation dialog prevents accidental logouts
- ✅ Works for both regular users and admin users
- ✅ Cleaner user experience with proper session management

## Technical Details

- The logout button uses `FlowLayout.RIGHT` to position it on the right side
- A confirmation dialog prevents accidental logouts
- The database object is passed to the new LoginGUI, maintaining all loaded data
- The current AirlineGUI is disposed, freeing up system resources
- The flight expiration thread is also terminated when the window is disposed
