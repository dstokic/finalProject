package dms425.FManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The class for handling all Financial Manager Operations
 */
public class FManager {

    /**
     * Action selection handler for the Finanical Manager Interface
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    public void main(Connection conn, Scanner in) {
        int choice = -1;

        System.out.println("\nFOR TESTING PURPOSES, HERE ARE ALL PROPERTY IDs AND ADDRESSES:");
        getAllProperties(conn);
        // NEED TO SEE PROPERTY IDs AND ADDRESSES FOR TESTING PURPOSES
        do {
            String options = "\nPlease type the number associated with the action you wish to perform:\n[0] - Exit Interface\n[1] - Get Property Data for a Single Property\n[2] - Get Property Data for a Subset of Properties\n[3] - Get Property Data for all Properties";
            System.out.println(options);

            try {
                choice = in.nextInt();

                if(choice == 1) {
                    singleProperty(conn, in);
                } else if(choice == 2) {
                    multiProperty(conn, in);
                } else if(choice == 3) {
                    allProperties(conn);
                }

                if(choice > 3 || choice < -1) {
                    System.out.println("You must enter a number between 0 and 3 inclusively.");
                }

            } catch(InputMismatchException e) {
                System.out.println("You must input either 0, 1, 2, or 3.");
                in.next();
            }

        } while (choice != 0);

    }


    /**
     * Get financial and general data for a single property using a user-input Property ID
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void singleProperty(Connection conn, Scanner in) {
        
        int prop_id = -1;
        do {
            System.out.println("\nPlease enter the ID of the property you want to see:");
            try {
                prop_id = in.nextInt();
            } catch(InputMismatchException e) {
                System.out.println("Property ID must be a number (e.g. 123)");
                in.next();
            }
        } while(prop_id == -1);

        try {
            PreparedStatement getOneProp = conn.prepareStatement("Select * FROM property WHERE prop_id = ?");
            getOneProp.setInt(1, prop_id);
            ResultSet res = getOneProp.executeQuery();

            if(res.next()) {
                int id = res.getInt(1);
                String add = res.getString(2);

                System.out.println("\nHere is the data for property " + prop_id);
                System.out.println("id: " + id + " | address: " + add + "\n");
                try {
                    PreparedStatement getPropEarn = conn.prepareStatement("SELECT SUM(p.amount) AS total_payments FROM property pr JOIN apartment a ON pr.prop_id = a.prop_id JOIN payments p ON a.apt_id = p.apt_id WHERE pr.prop_id = ?");
                    getPropEarn.setInt(1,  prop_id);
                    ResultSet subRes = getPropEarn.executeQuery();

                    if(subRes.next()) {
                        System.out.println("Earnings to date for Property " + prop_id + ": $" + subRes.getInt(1));
                    }
                } catch(SQLException e) {
                    System.out.println("There was an issue finding the earning to date for property " + prop_id);
                }
            } else {
                System.out.println("Property " + prop_id + " does not exist, or does not yet have any data in the database.\n");
            }

        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to get property data for property " + prop_id);
        }
    }


    /**
     * Get financial and general data for a subset of properties using a user-input state abbreviation (e.g. NY)
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void multiProperty(Connection conn, Scanner in) {

        String state = "";
        do {
            System.out.println("\nPlease enter the state abbreviation of the properties you want to see (e.g. NJ):");
            try {
                state = in.next();

                if(state.matches(".*\\d.*")) {
                    System.out.println("State must only contain letters (e.g. NJ)");
                    state = "";
                    in.nextLine();
                } else if(state.length() != 2) {
                    System.out.println("State must be only 2 letters long (e.g. NJ)");
                    state = "";
                    in.nextLine();
                }

            } catch(InputMismatchException e) {
                System.out.println("State must be a String (e.g. NJ)");
                in.nextLine();
            }
        } while(state.equals(""));

        try {
            state.toUpperCase();
            PreparedStatement getProps = conn.prepareStatement("Select * FROM property WHERE address LIKE ?");
            getProps.setString(1, "%, " + state + "%");
            ResultSet res = getProps.executeQuery();

            int totalEarnings = 0;
            while(res.next()) {
                int id = res.getInt(1);
                String add = res.getString(2);

                System.out.println("\nid: " + id + " | address: " + add);

                try {
                    PreparedStatement getApt_id = conn.prepareStatement("SELECT apt_id FROM apartment WHERE prop_id = ?");
                    getApt_id.setInt(1, id);
                    ResultSet tempId = getApt_id.executeQuery();

                    while(tempId.next()){
                        int apt_id = tempId.getInt(1);
                        PreparedStatement getEarnings = conn.prepareStatement("SELECT SUM(amount) FROM payments WHERE apt_id = ?");
                        getEarnings.setInt(1, apt_id);
                        ResultSet subRes = getEarnings.executeQuery();
                        
                        if(subRes.next()) {
                            totalEarnings += subRes.getInt(1);
                        }

                        System.out.println("Earnings for Apartment ID " + apt_id + ": $" + subRes.getInt(1));
                    }

                } catch(SQLException e) {
                    System.out.println("There was an issue attempting to get the earnings for properties in " + state);
                }
            }
            System.out.println("\nAll earnings to date for properties located in " + state + ": $" + totalEarnings);

        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to get property data for properties in " + state);
        }
    }


    /**
     * Get financial and general data for all exisiting properties
     * @param conn Exisiting database connection
     */
    private void allProperties(Connection conn) {

        try {
            PreparedStatement getAllProps = conn.prepareStatement("SELECT * FROM property");
            ResultSet res = getAllProps.executeQuery();

            while(res.next()) {
                int id = res.getInt(1);
                String add = res.getString(2);

                System.out.println("\nid: " + id + " | address: " + add);

                PreparedStatement getAptsEarn = conn.prepareStatement("SELECT apt_id FROM apartment WHERE prop_id = ?");
                getAptsEarn.setInt(1, id);
                ResultSet subRes = getAptsEarn.executeQuery();

                int totalProp = 0;
                while(subRes.next()) {
                    int apt_id = subRes.getInt(1);
                    PreparedStatement singleAptEarn = conn.prepareStatement("SELECT sum(amount) FROM payments WHERE apt_id = ?");
                    singleAptEarn.setInt(1, apt_id);
                    ResultSet amount = singleAptEarn.executeQuery();
                    amount.next();

                    totalProp += amount.getInt(1);
                    System.out.println("Earnings for Apartment ID " + apt_id + ": $" + amount.getInt(1));
                }
                System.out.println("Total Earnings for Property ID " + id + ": $" + totalProp);
            }
            System.out.println();

        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to fetch all property data");
        }

        try {
            PreparedStatement getAllEarnings = conn.prepareStatement("SELECT SUM(amount) FROM payments");
            ResultSet res = getAllEarnings.executeQuery();

            if(res.next()) {
                System.out.println("Earnings to date across ALL properties: $" + res.getInt(1));
            }
        } catch(SQLException e) {
            System.out.println("There was an issue attempting to get the earnings to date for all properties");
        }
    }

    private void getAllProperties(Connection conn) {
        try {
            PreparedStatement getApartments = conn.prepareStatement("SELECT * FROM property");
            ResultSet res = getApartments.executeQuery();

            while(res.next()) {
                int prop_id = res.getInt(1);
                String address = res.getString(2);

                System.out.println("Property ID: " + prop_id + " | Address: " + address);
            }

        } catch(SQLException e) {
            System.out.println("There was an issue retrieving all apartment data.");
        }
    }
}
