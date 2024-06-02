package dms425.PManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * The class for handling all Property Manager Operations
 */
public class PManager {
    
    /**
     * Action selection handler for the Project Manager Interface
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    public void main(Connection conn, Scanner in) {
        int choice = -1;

        // NEED TO DISPLAY APARTMENT IDs, TENANT IDs, and LEASE IDs FOR TESTING PURPOSES
        do {
            String options = "\nPlease type the number associated with the action you wish to perform:\n[0] - Exit Interface\n[1] - Record Visit Data\n[2] - Record Lease Data\n[3] - Record Move-Out\n[4] - Add Person/Pet to Lease\n[5] - Set Move-Out Date\n[6] - See All Tenant Data\n[7] - See All Lease Data\n[8] - See All Apartment Data";
            System.out.println(options);

            try {
                choice = in.nextInt();

                if(choice == 1) {
                    recordVisit(conn, in);
                } else if(choice == 2) {
                    recordLease(conn, in);
                } else if(choice == 3) {
                    recordMoveout(conn, in);
                } else if(choice == 4) {
                    addToLease(conn, in);
                } else if (choice == 5) {
                    setMoveout(conn, in);
                } else if(choice == 6) {
                    getAllTenData(conn, in);
                } else if(choice == 7) {
                    getAllLeaseData(conn, in);
                } else if(choice == 8) {
                    getAllApartmentData(conn, in);
                }

                if(choice > 8 || choice < -1) {
                    System.out.println("You must enter a number between 0 and 8 inclusively.");
                }

            } catch(InputMismatchException e) {
                System.out.println("You must input either 0, 1, 2, 3, 4, 5, 6, 7, or 8.");
                in.next();
            }

        } while (choice != 0);
        
    }


    /**
     * Record an apartment visit for 
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void recordVisit(Connection conn, Scanner in) {
        int answer = -1;

        do {
            System.out.println("\nPlease indicate the status of the person who visited:\n[1] - Completely New (not in database yet)\n[2] - Already Known (exists in database)");
            try {
                answer = in.nextInt();

                if(answer != 1 && answer != 2) {
                    System.out.println("You need to input either 1 or 2");
                    answer = -1;
                }

            } catch(InputMismatchException e) {
                System.out.println("You need to input a number");
                in.next();
            }

        } while(answer == -1);

        if(answer == 1) { // COMPLETELY NEW
            
            String first = "";
            do {
                System.out.println("\nWhat is the first name of the prospective tenant?");
                in.nextLine();
                first = in.nextLine();

                if (!first.matches("^[a-zA-Z]+$")) {
                    System.out.println("First name must contain only letters.");
                    in.nextLine();
                    first = "";
                }

            } while(first.equals(""));

            String last = "";
            do {
                System.out.println("\nWhat is the last name of the prospective tenant?");
                last = in.nextLine();

                if (!last.matches("^[a-zA-Z]+$")) {
                    System.out.println("Last name must contain only letters.");
                    in.nextLine();
                    last = "";
                }

            } while(last.equals(""));

            int income = -1;
            do {
                System.out.println("\nWhat is the income of the prospective tenant?");

                try {
                    income = in.nextInt();

                } catch(InputMismatchException e) {
                    System.out.println("\nYou must input a number.");
                }
            } while(income == -1);

            int age = -1;
            do {
                System.out.println("\nWhat is the age of the prospective tenant?");

                try {
                    age = in.nextInt();

                    if(age > 100) {
                        System.out.println("\nPlease enter a realistic age.");
                        age = -1;
                    }
                } catch(InputMismatchException e) {
                    System.out.println("\nYou must input a number.");
                }
            } while(age == -1);

            int apt_id = -1;
            do {
                System.out.println("\nWhat is the Apartment ID of the apartment they visited?");

                try {
                    apt_id = in.nextInt();

                    try {
                        PreparedStatement prepApp = conn.prepareStatement("SELECT property.address FROM property, apartment WHERE apt_id = ? and apartment.prop_id = property.prop_id");
                        prepApp.setInt(1, apt_id);

                        ResultSet res = prepApp.executeQuery();
                        if(!res.next()) {
                            System.out.println("The Apartment ID " + apt_id + " does not correlate to a known apartment");
                            apt_id = -1;
                        } else {
                            String address = res.getString(1);
                            System.out.println("\nThe address associated with Apartment ID " + apt_id + " is:\n" + address);
                        }

                    } catch(SQLException e) {
                        System.out.println("There was an issue validating the Apartment ID.");
                    }

                } catch(InputMismatchException e) {
                    System.out.println("You must input a number.");
                }
            } while(apt_id == -1);
            
            try {
                PreparedStatement addPerson = conn.prepareStatement("INSERT INTO person(first_name, last_name, income, age) VALUES(?, ?, ?, ?)");
                addPerson.setString(1, first);
                addPerson.setString(2, last);
                addPerson.setInt(3, income);
                addPerson.setInt(4, age);

                int res = addPerson.executeUpdate();
                if(res == 1) {
                    PreparedStatement getTID = conn.prepareStatement("SELECT t_id FROM person WHERE first_name = ? AND last_name = ? AND income = ? AND age = ?");
                    getTID.setString(1, first);
                    getTID.setString(2, last);
                    getTID.setInt(3, income);
                    getTID.setInt(4, age);

                    ResultSet result = getTID.executeQuery();
                    result.next();
                    int t_id = result.getInt(1);

                    try {
                        PreparedStatement addProsTenant = conn.prepareStatement("INSERT INTO pros_tenant(t_id, apt_visited) VALUES(?, ?)");
                        addProsTenant.setInt(1, t_id);
                        addProsTenant.setInt(2, apt_id);

                        res = addProsTenant.executeUpdate();
                        if(res == 1) {
                            System.out.println("\nThe new prospective tenant's visit was successfully recorded");
                        }
                    } catch(SQLException e) {
                        System.out.println("There was an issue recording the new prospective tenant.");
                    }
                }
            } catch(SQLException e) {
                System.out.println("There was an issue recording the new person.");
            }

        } else if(answer == 2){
            int ten_id = -1;
            do {
                System.out.println("\nWhat is the Tenant ID of the existing person?");

                try {
                    ten_id = in.nextInt();

                    try {
                        PreparedStatement prep = conn.prepareStatement("SELECT t_id FROM pros_tenant WHERE t_id = ?");
                        prep.setInt(1, ten_id);

                        ResultSet res = prep.executeQuery();
                        if(!res.next()) {
                            System.out.println("The Tenant ID " + ten_id + " does not correlate to a known prospective tenant.");
                            ten_id = -1;
                        }

                    } catch(SQLException e) {
                        System.out.println("There was an issue validating the Tenant ID.");
                    }
                } catch(InputMismatchException e) {
                    System.out.println("You must input a number.");
                }
            } while(ten_id == -1);

            int apt_id = -1;
            do {
                System.out.println("\nWhat is the Apartment ID of the apartment they visited?");

                try {
                    apt_id = in.nextInt();

                    try {
                        PreparedStatement prepApp = conn.prepareStatement("SELECT property.address FROM property, apartment WHERE apt_id = ? and apartment.prop_id = property.prop_id");
                        prepApp.setInt(1, apt_id);

                        ResultSet res = prepApp.executeQuery();
                        if(!res.next()) {
                            System.out.println("The Apartment ID " + apt_id + " does not correlate to a known apartment.");
                            apt_id = -1;
                        } else {
                            String address = res.getString(1);
                            System.out.println("\nThe address associated with Apartment ID " + apt_id + " is:\n" + address);
                        }
                    } catch(SQLException e) {
                        System.out.println("There was an issue validating the Apartment ID.");
                    }
                } catch(InputMismatchException e) {
                    System.out.println("You must input a number.");
                }
            } while(apt_id == -1);

            try {
                PreparedStatement updateVisit = conn.prepareStatement("UPDATE pros_tenant SET apt_visited = ? WHERE t_id = ?");
                updateVisit.setInt(1, apt_id);
                updateVisit.setInt(2, ten_id);

                int res = updateVisit.executeUpdate();
                if(res == 1) {
                    System.out.println("\nA visit was recorded for the Tenant ID " + ten_id);
                }

            } catch(SQLException e) {
                System.out.println("There was an issue updating the visit data for the tenant.");
            }
        }
    }


    /**
     * Record a new lease
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void recordLease(Connection conn, Scanner in) {
        int apt_id = -1;

        do {
            System.out.println("\nWhat is the Apartment ID of the apartment you are creating a new lease for?");

            try {
                apt_id = in.nextInt();

                try {
                    PreparedStatement prep = conn.prepareStatement("SELECT apartment.apt_num FROM apartment LEFT JOIN lease ON apartment.apt_id = lease.apt_id WHERE apartment.apt_id = ? AND lease_id IS NULL");
                    prep.setInt(1, apt_id);

                    ResultSet res = prep.executeQuery();
                    if(!res.next()) {
                        System.out.println("Apartment ID " + apt_id + " already has an active lease associated with it\nIf you wish to add someone to the lease, please select that option in the interface.");
                        apt_id = -1;
                    }

                } catch(SQLException e) {
                    System.out.println("There was an issue validating the availability of Apartment ID " + apt_id + ".");
                }

            } catch(InputMismatchException e) {
                System.out.println("You must input a number.");
            }

        } while(apt_id == -1);


        int ten_id = -1;
        do {
            System.out.println("\nWhat is the Tenant ID of the person on the lease?");

            try {
                ten_id = in.nextInt();

                try {
                    PreparedStatement getTID = conn.prepareStatement("SELECT t_id FROM pros_tenant WHERE t_id = ?");
                    getTID.setInt(1, ten_id);

                    ResultSet res = getTID.executeQuery();
                    if(!res.next()) {
                        System.out.println("sThe Tenant ID " + ten_id + " does not correlate to a known prospective tenant.\nA current tenant cannot sign another lease.");
                        ten_id = -1;
                    }
                } catch(SQLException e) {
                    System.out.println("There was an issue validating the Tenant ID.");
                }
            } catch(InputMismatchException e) {
                System.out.println("You must input a number.");
            }
        } while(ten_id == -1);

        try {
            PreparedStatement addData = conn.prepareStatement("INSERT INTO lease(apt_id) VALUES(?)");
            addData.setInt(1, apt_id);

            int result = addData.executeUpdate();
            if(result == 1) {
                PreparedStatement getLease = conn.prepareStatement("SELECT lease_id FROM lease WHERE apt_id = ?");
                getLease.setInt(1, apt_id);

                ResultSet res = getLease.executeQuery();
                res.next();
                int lease_id = res.getInt(1);
                System.out.println("\nThe created lease's Lease ID is: " + lease_id);

                String address = "";
                int apt_num = 0;
                try {
                    PreparedStatement getApartment = conn.prepareStatement("SELECT p.address, a.apt_num FROM apartment a JOIN property p ON a.prop_id = p.prop_id WHERE a.apt_id = ?");
                    getApartment.setInt(1, apt_id);

                    res = getApartment.executeQuery();
                    if(res.next()) {
                        address = res.getString(1);
                        apt_num = res.getInt(2);
                    } else {
                        System.out.println("\nThere was no address or apartment number found for the Apartment ID " + apt_id + ".");
                    }

                    PreparedStatement moveProTenant = conn.prepareStatement("INSERT INTO cur_tenant(t_id, lease_id, address, apt_num) VALUES(?, ?, ?, ?)");
                    moveProTenant.setInt(1, ten_id);
                    moveProTenant.setInt(2, lease_id);
                    moveProTenant.setString(3, address);
                    moveProTenant.setInt(4, apt_num);

                    result = moveProTenant.executeUpdate();
                    if(result == 1) {
                        PreparedStatement delProsTenant = conn.prepareStatement("DELETE pros_tenant WHERE t_id = ?");
                        delProsTenant.setInt(1, ten_id);

                        result = delProsTenant.executeUpdate();
                        if(result == 1) {
                            System.out.println("\nTenant has been successfully assigned to the lease, and updated to a current tenant");
                        }
                    } else {
                        System.out.println("\nCould not change prospective tenant to a current tenant.");
                    }
                } catch(SQLException e) {
                    System.out.println("\nThere was an issue associating the Tenant with the lease.");
                }
            } else {
                System.out.println("There was an issue inserting lease data.");
            }
        } catch(SQLException e) {
            System.out.println("\nThere was an error adding the lease data into the database.");
        }
    }


    /**
     * Record a tenant moveout
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void recordMoveout(Connection conn, Scanner in) {
        int lease_id = -1;

        do {
            System.out.println("\nWhat is the Lease ID that you are recording a moveout for?");

            try {
                lease_id = in.nextInt();

                try {
                    PreparedStatement getLease = conn.prepareStatement("SELECT apt_id FROM lease WHERE lease_id = ?");
                    getLease.setInt(1, lease_id);

                    ResultSet res = getLease.executeQuery();
                    if(!res.next()) {
                        System.out.println("That is not a valid Lease ID.");
                        lease_id = -1;
                    }

                } catch(SQLException e) {
                    System.out.println("\nThere was an issue when validating the Lease ID.");
                }

            } catch(InputMismatchException e) {
                System.out.println("\nYou must input a number.");
            }
        } while(lease_id == -1);

        try {
            PreparedStatement getTenantsAffected = conn.prepareStatement("SELECT t_id FROM cur_tenant WHERE lease_id = ?");
            getTenantsAffected.setInt(1, lease_id);

            ResultSet res = getTenantsAffected.executeQuery();

            PreparedStatement moveTenant = conn.prepareStatement("INSERT INTO pros_tenant(t_id) VALUES(?)");
            PreparedStatement delCurTen = conn.prepareStatement("DELETE FROM cur_tenant WHERE t_id = ?");
            while(res.next()) {
                int ten_id = res.getInt(1);
                moveTenant.setInt(1, ten_id);
                moveTenant.executeUpdate();

                delCurTen.setInt(1, ten_id);
                delCurTen.executeUpdate();
            }
        } catch(SQLException e) {
            System.out.println("There was an issue when transfering current tenants to prospective tenants.");
        }

        try {
            PreparedStatement delLease = conn.prepareStatement("DELETE FROM lease WHERE lease_id = ?");
            delLease.setInt(1, lease_id);

            int res = delLease.executeUpdate();
            if(res == 1) {
                System.out.println("\nMoveout was successfully recorded, and lease data was removed.");
            }
        } catch(SQLException e) {
            System.out.println("\nThere was an issue when attempting to delete the lease data.");
        }
    }


    /**
     * Add either a new person to a lease, or a pet to a lease
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void addToLease(Connection conn, Scanner in) {
        int answer = -1;

        do {
            System.out.println("\nPlease input the number corresponding to what you wish to add:\n[1] - Person\n[2] - Pet");

            try {
                answer = in.nextInt();

                if(answer != 1 && answer != 2) {
                    System.out.println("You must input either 1 or 2.");
                    answer = -1;
                }
            } catch(InputMismatchException e) {
                System.out.println("You must input a number.");
            }

        } while(answer == -1);

        if(answer == 2) {

            String pet = "";
            do {
                System.out.println("\nWhat is the type of pet? (e.g. dog, cat, snake, etc)");
                in.nextLine();
                pet = in.nextLine();

                if (!pet.matches("^[a-zA-Z]+$")) {
                    System.out.println("The type of pet must contain only letters.");
                    in.nextLine();
                    pet = "";
                }

            } while(pet.equals(""));

            String name = "";
            do {
                System.out.println("\nWhat is the name of the pet?");
                name = in.nextLine();

                if (!pet.matches("^[a-zA-Z]+$")) {
                    System.out.println("The pet name must contain only letters.");
                    in.nextLine();
                    name = "";
                }

            } while(name.equals(""));

            int ten_id = -1;
            do {
                System.out.println("\nWhat is the Tenant ID of the person who owns the pet?");

                try {
                    ten_id = in.nextInt();
                } catch(InputMismatchException e) {
                    System.out.println("You must input a number.");
                }

            } while(ten_id == -1);

            boolean valid = false;
            try {
                PreparedStatement checkId = conn.prepareStatement("SELECT first_name FROM person WHERE t_id = ?");
                checkId.setInt(1, ten_id);

                ResultSet res = checkId.executeQuery();
                if(res.next()) {
                    valid = true;
                } else {
                    System.out.println("\nThe Tenant ID you provided does not relate to a current or prospective tenant.");
                }
            } catch(SQLException e) {
                System.out.println("\nThere was an issue validating the Tenant ID.");
            }

            if(valid) {
                try {
                    PreparedStatement addPet = conn.prepareStatement("INSERT INTO pet (pet_name, pet_type, t_id) VALUES (?, ?, ?)");
                    addPet.setString(1, name);
                    addPet.setString(2, pet);
                    addPet.setInt(3, ten_id);

                    int success = addPet.executeUpdate();
                    if(success == 1) {
                        System.out.println("\nPet was successfully added and associated with Tenant ID " + ten_id);
                    }

                } catch(SQLException e) {
                    System.out.println("\nThere was an issue when attempting to add the pet to the database.");
                }
            }


        } else if(answer == 1) {
            int ten_id = -1;
            do {
                System.out.println("\nWhat is the Tenant ID of the person to be added to the lease?");

                try {
                    ten_id = in.nextInt();
                } catch(InputMismatchException e) {
                    System.out.println("You must input a number.");
                }

            } while(ten_id == -1);

            int lease_id = -1;
            do {
                System.out.println("\nWhat is the Lease ID that you want the Tenant ID " + ten_id + " to be added to?");

                try {
                    lease_id = in.nextInt();
                } catch(InputMismatchException e) {
                    System.out.println("You must input a number.");
                }
            } while(lease_id == -1);

            boolean valid = false;
            try {
                PreparedStatement checkId = conn.prepareStatement("SELECT apt_visited FROM pros_tenant WHERE apt_visited IS NOT NULL AND t_id = ?");
                checkId.setInt(1, ten_id);

                ResultSet res = checkId.executeQuery();
                if(res.next()) {
                    valid = true;
                } else {
                    System.out.println("\nThe Tenant ID you provided does not relate to a prospective tenant who is eligible to sign a lease.\n(a current tenant can only have one lease)");
                }
            } catch(SQLException e) {
                System.out.println("\nThere was an issue validating the Tenant ID.");
            }

            if(valid) {
                String address = "";
                int apt_num = 0;

                boolean valAdd = true;
                try {
                    PreparedStatement getApartment = conn.prepareStatement("SELECT p.address, a.apt_num FROM lease l JOIN apartment a ON l.apt_id = a.apt_id JOIN property p ON a.prop_id = p.prop_id WHERE l.lease_id = ?");
                    getApartment.setInt(1,lease_id);

                    ResultSet res = getApartment.executeQuery();
                    if(res.next()) {
                        address = res.getString(1);
                        apt_num = res.getInt(2);
                    } else {
                        System.out.println("\nThere is no address or apt_num associated with the Lease ID " + lease_id + ".");
                        valAdd = false;
                    }
                        
                } catch(SQLException e) {
                    System.out.println("\nThere was an issue retreiving the data associated with the tenant with ID " + ten_id + " from prospective tenants.");
                }

                if (valAdd) {
                    try {
                        PreparedStatement addCurr = conn.prepareStatement("INSERT INTO cur_tenant (t_id, lease_id, address, apt_num) VALUES(?, ?, ?, ?)");
                        addCurr.setInt(1, ten_id);
                        addCurr.setInt(2, lease_id);
                        addCurr.setString(3, address);
                        addCurr.setInt(4, apt_num);

                        int result = addCurr.executeUpdate();
                        if(result == 1) {
                            System.out.println("\nTenant ID " + ten_id + " was successfully added to lease ID " + lease_id);
                        }

                    } catch(SQLException e) {
                        System.out.println("There was an issue adding the tenant with ID " + ten_id + " to the lease.");
                    }

                    try {
                        PreparedStatement dropPros = conn.prepareStatement("DELETE FROM pros_tenant WHERE t_id = ?");
                        dropPros.setInt(1, ten_id);

                        dropPros.executeUpdate();

                    } catch(SQLException e) {
                        System.out.println("\nThere was an issue dropping the tenant with ID " + ten_id + " from the prospective tenants table.");
                    }
                }   
            }
        }
    }


    /**
     * Set a moveout date for a lease
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void setMoveout(Connection conn, Scanner in) {
        int apt_id = -1;
        do {
            try {
                System.out.println("\nWhat is the Apartment ID that you want to set a moveout date?");
                apt_id = in.nextInt();

            } catch(InputMismatchException e) {
                System.out.println("You must enter a number (e.g. 101)");
                in.next();
            }
        } while(apt_id == -1);

       String  moveout = "";
        do {
            try {
                System.out.println("\nWhat is the moveout date you'd like to record (Use the format yyyy-mm-dd)");
                in.nextLine();
                moveout = in.nextLine();

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                format.setLenient(false);

                Date tempParsed = format.parse(moveout);
                java.sql.Date parsed = new java.sql.Date(tempParsed.getTime());

                try {
                    PreparedStatement setMoveout = conn.prepareStatement("UPDATE lease SET moveout = ? WHERE apt_id = ?");
                    setMoveout.setDate(1, parsed);
                    setMoveout.setInt(2, apt_id);
                    setMoveout.executeUpdate();

                    System.out.println("\nMoveout date was successfully set");
                } catch(SQLException e) {
                    System.out.println("\nThere was an issue setting the moveout date");
                }
            } catch(ParseException e) {
                System.out.println("Your entered your date in an incorrect format, it muts be yyyy-mm-dd");
                moveout = "";
                in.nextLine();
            }
        } while(moveout.equals(""));
    }


    /**
     * Get all existing current and prospective tenant data
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void getAllTenData(Connection conn, Scanner in) {
        try {
            PreparedStatement getTenants = conn.prepareStatement("SELECT * FROM person");
            PreparedStatement getCurTenant = conn.prepareStatement("SELECT lease_id FROM cur_tenant WHERE t_id = ?");
            ResultSet res = getTenants.executeQuery();

            while(res.next()) {
                int t_id = res.getInt(1);
                String first = res.getString(2);
                String last = res.getString(3);
                int income = res.getInt(4);
                int age = res.getInt(5);

                getCurTenant.setInt(1, t_id);
                ResultSet subRes = getCurTenant.executeQuery();

                if(subRes.next()) {
                    int lease_id = subRes.getInt(1);
                    System.out.println("Tenant ID: " + t_id + " | Full Name: " + first + " " + last + " | Age: " + age + " | Income: " + income  + " | Lease ID: " + lease_id);
                } else {
                    System.out.println("Tenant ID: " + t_id + " | Full Name: " + first + " " + last + " | Age: " + age + " | Income: " + income);
                }

            }
        } catch(SQLException e) {
            System.out.println("There was an issue retrieving all Tenant data.");
        }
    }


    /**
     * Get all currently existing lease data
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void getAllLeaseData(Connection conn, Scanner in) {
        try {
            PreparedStatement getLeases = conn.prepareStatement("SELECT * FROM lease");
            ResultSet res = getLeases.executeQuery();

            System.out.println("\nHere is all current lease data:");
            while(res.next()) {
                int lease_id = res.getInt(1);
                int apt_id = res.getInt(2);
                Date moveout = res.getDate(3); 

                System.out.println("Lease ID: " + lease_id + " | Apartment ID: " + apt_id + " | Move-out: " + moveout);
            }

        } catch(SQLException e) {
            System.out.println("There was an issue retrieving all lease data.");
        }
    }

    
    /**
     * Get all currently existing apartment data
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void getAllApartmentData(Connection conn, Scanner in) {
        try {
            PreparedStatement getApartments = conn.prepareStatement("SELECT * FROM apartment");
            ResultSet res = getApartments.executeQuery();

            System.out.println("\nHere is all current apartment data:");
            while(res.next()) {
                int apt_id = res.getInt(1);
                int prop_id = res.getInt(2);
                int apt_num = res.getInt(3);
                int rent = res.getInt(4);

                System.out.println("Apartment ID: " + apt_id + " | Property ID: " + prop_id + " | Apartment Number: " + apt_num + " | Monthly Rent: " + rent);
            }

        } catch(SQLException e) {
            System.out.println("There was an issue retrieving all apartment data.");
        }
    }

}
