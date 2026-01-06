package StaffManagement;
public class Staff {
    private int staffID;
    private String name;
    private String role;   
                public Staff(int staffID, String name, String role) {
                    this.staffID = staffID;
                    this.name = name;
                    this.role = role;
                }
                public int getStaffID() {
                    return staffID;
                }
                public String getName() {
                    return name;
                }
                public String getRole() {
                    return role;
                }
                public void setName(String name) {
                    this.name = name;
                }
                public void setRole(String role) {
                    this.role = role;
                }
                public void setStaffID(int staffID) {
                    this.staffID = staffID;
                }
                @Override
                public String toString() {
                    return staffID+"," + name + "," + role;
                }
}
