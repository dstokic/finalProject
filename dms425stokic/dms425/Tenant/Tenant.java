package dms425.Tenant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The class for handling all Tenant Operations
 */
public class Tenant {
    
    /**
     * Action selection handler for the Tenant Interface
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    public void main(Connection conn, Scanner in) {
        int choice = -1;

        System.out.println("\nFOR TESTING PURPOSES, HERE ARE ALL TENANT IDs (Lease_ID is included if they are a current tenant):");
        getAllTenants(conn);

        do {
            String options = "\nPlease type the number associated with the action you wish to perform:\n[0] - Exit Interface\n[1] - Check Payment Status\n[2] - Make Rental Payment\n[3] - Update Person Data\n[4] - See Current Lease Data\n[5] - Get Tenant ID\n[6] - See/Update Current Amenities";
            System.out.println(options);

            try {
                choice = in.nextInt();

                if(choice == 1) {
                    checkPayment(conn, in);
                } else if(choice == 2) {
                    makePayment(conn, in);
                } else if(choice == 3) {
                    updatePerson(conn, in);
                } else if(choice == 4) {
                    getCurrentLease(conn, in);
                } else if(choice == 5) {
                    getTenantID(conn, in);
                } else if(choice == 6) {
                    seeAmenities(conn, in);
                }

                if(choice > 6 || choice < -1) {
                    System.out.println("You must enter a number between 0 and 6 inclusively.");
                }

            } catch(InputMismatchException e) {
                System.out.println("You must input either 0, 1, 2, 3, 4, 5, or 6.");
                in.next();
            }
        } while (choice != 0);
    }


    /**
     * Allows the user to get their Tenant ID
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void getTenantID(Connection conn, Scanner in) {
        System.out.println("Your tenant ID is important for accessing your personal, lease and payment information.\nYou will need to enter your first and last name, and a confirmation statement to receive it\n(this is essentially a 'forgot your password?' type of thing)");

        String first = "";
        do {
            System.out.println("\nPlease enter your first name");
            first = in.next();

            if(first.matches(".*\\d.*")) {
                System.out.println("Your first name must be only letters.");
                first = "";
                in.next();
            }
        } while(first.equals(""));
        
        String last = "";
        do {
            System.out.println("\nPlease enter your last name");
            last = in.next();

            if(last.matches(".*\\d.*")) {
                System.out.println("Your last name must be only letters.");
                last = "";
                in.next();
            }
        } while(last.equals(""));

        System.out.println("\nPlease type the following statement to confirm you want your Tenant ID:\nI confirm I am " + first + " " + last);
        in.nextLine();
        String statement = in.nextLine();
        String expected = "I confirm I am " + first + " " + last;

        if(statement.equals(expected)) {
            try {
                PreparedStatement getTenantID = conn.prepareStatement("SELECT t_id FROM person WHERE first_name = ? AND last_name = ?");
                getTenantID.setString(1, first);
                getTenantID.setString(2, last);
                ResultSet res = getTenantID.executeQuery();

                if(res.next()) {
                    int id = res.getInt(1);
                    System.out.println("\nYour Tenant ID is: " + id);
                } else {
                    System.out.println("There is no Tenant ID for that person.");
                }

            } catch(SQLException e) {
                System.out.println("There was an issue when attempting to retrieve your Tenant ID.");
            }
        } else {
            System.out.println("We are not able to confirm your identity, and cannot give you your Tenant ID.");
        }
    }


    /**
     * Allows the user to check their payments towards their lease
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void checkPayment(Connection conn, Scanner in) {
        int ten_id = -1;

        do {
            System.out.println("\nPlease enter your Tenant ID to see your payments:");
            try {
                ten_id = in.nextInt();
            } catch(InputMismatchException e) {
                System.out.println("Tenant ID must be a number (e.g. 123).");
                in.next();
            }
        } while(ten_id == -1);

        try {
            PreparedStatement getPay = conn.prepareStatement("Select payments.amount, payments.datetime, payments.apt_id FROM cur_tenant, lease, payments WHERE t_id = ? AND cur_tenant.lease_id = lease.lease_id AND lease.apt_id = payments.apt_id");
            getPay.setInt(1, ten_id);
            ResultSet res = getPay.executeQuery();

            int total = 0;
            System.out.println("\nHere is your payment information:");
            while(res.next()) {
                int amount = res.getInt(1);
                java.sql.Timestamp datetime = res.getTimestamp(2);
                int apt_id = res.getInt(3);

                System.out.println("Apartment ID: " + apt_id + " | Timestamp: " + datetime + " | Amount: " + amount);
                total += amount;
            }

            if(total == 0) {
                System.out.println("There is no payment history for the tenant ID " + ten_id + ".");
            } else {
                System.out.println("Total Amount Paid to Date: " + total);
            }

            int amountDue = 0;
            System.out.println("\nYour total amount due is: ");
            System.out.println("---- Rent");

            PreparedStatement getRent = conn.prepareStatement("SELECT apartment.monthly_rent FROM apartment, lease, cur_tenant WHERE t_id = ? AND cur_tenant.lease_id = lease.lease_id AND apartment.apt_id = lease.apt_id");
            getRent.setInt(1, ten_id);
            ResultSet apt = getRent.executeQuery();

            if(apt.next()) {
                amountDue += apt.getInt(1);
                System.out.println("$" + apt.getInt(1));
            }

            System.out.println("---- Common Ammenity Subscriptions");

            PreparedStatement getAddCharge= conn.prepareStatement("SELECT add_charge FROM cur_tenant WHERE t_id = ?");
            getAddCharge.setInt(1, ten_id);
            ResultSet amen = getAddCharge.executeQuery();

            if(amen.next()) {
                amountDue += amen.getInt(1);
                System.out.println("$" + amen.getInt(1));
            }

            System.out.println("\nTotal Amount Owed: $" + amountDue);
            System.out.println("Total Amount Remaining: $" + (amountDue - total));


        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to get payment data for tenant " + ten_id + ".");
        }
    }


    /**
     * Allows the user to make a payment towards their lease
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void makePayment(Connection conn, Scanner in) {
        int ten_id = -1;

        do {
            System.out.println("\nPlease enter your Tenant ID to initiate a payment:");
            try {
                ten_id = in.nextInt();
            } catch(InputMismatchException e) {
                System.out.println("Tenant ID must be a number (e.g. 123).");
                in.next();
            }
        } while(ten_id == -1);

        int payment = 0;
        do {
            try {
                System.out.println("\nWhat is the amount you are paying?");
                payment = in.nextInt();
            } catch(InputMismatchException e) {
                System.out.println("Your payment must be an integer (e.g 1000).");
                in.next();
            }
        } while(payment == 0);

        int response = -1;
        do {
            System.out.println("\nPlease confirm that you are making a payment of $" + payment);
            System.out.println("[1] - I confirm\n[2] - I do not want to make this payment");
            
            try {
                response = in.nextInt();
            } catch(InputMismatchException e) {
                System.out.println("Your response must be either the number 1 or 2.");
            }

            if(response != 1 && response != 2) {
                System.out.println("Your response can only be either the number 1, to confirm the payment, or 2, to decline the payment.");
            }
        } while(response != 1 && response != 2);

        if(response == 1) {
            try {
                int apt_id = -1;
                try {
                    PreparedStatement getApt = conn.prepareStatement("SELECT lease.apt_id FROM cur_tenant, lease WHERE cur_tenant.t_id = ? AND cur_tenant.lease_id = lease.lease_id");
                    getApt.setInt(1, ten_id);
                    ResultSet apartres = getApt.executeQuery();
                    
                    if(apartres.next()) {
                        apt_id = apartres.getInt(1);
                    } else {
                        System.out.println("There is no apartment associated with this Tenant ID, therefore a payment can't be made.");
                    }
                } catch(SQLException e) {
                    System.out.println("There was an error finding the Apartment ID associated with your Tenant ID.");
                }

                PreparedStatement makePay = conn.prepareStatement("INSERT INTO payments (amount, datetime, apt_id) VALUES (?, CURRENT_TIMESTAMP, ?)");
                makePay.setInt(1, payment);
                makePay.setInt(2, apt_id);
                int count = makePay.executeUpdate();

                if(count == 1) {
                    System.out.println("\nYour payment of $" + payment + " was made successfully");
                } else {
                    System.out.println("There was an issue making your payment.");
                }
            } catch (SQLException e) {
                System.out.println("Something went wrong when trying to get complete the payment for tenant " + ten_id + ".");
            }
        } else {
            System.out.println("The payment was aborted.");
        }
    }


    /**
     * Allows the user to update their first name and last name
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void updatePerson(Connection conn, Scanner in) {
        int ten_id = -1;

        do {
            System.out.println("\nPlease enter your Tenant ID to see and update your person data:");
            try {
                ten_id = in.nextInt();
            } catch(InputMismatchException e) {
                System.out.println("Tenant ID must be a number (e.g. 123).");
                in.next();
            }
        } while(ten_id == -1);

        try {
            PreparedStatement getPerson = conn.prepareStatement("Select first_name, last_name FROM person WHERE t_id = ?");
            getPerson.setInt(1, ten_id);
            ResultSet res = getPerson.executeQuery();

            if(res.next()) {
                System.out.println("\nHere is your current person information:");
                String first = res.getString(1);
                String last = res.getString(2);

                System.out.println("First Name: " + first + " | Last Name: " + last + "\n");

                int choice = -1;
                do {
                    System.out.println("Would you like to change your information?\n[1] - YES\n[2] - NO");

                    try {
                        choice = in.nextInt();
                    } catch(InputMismatchException e) {
                        System.out.println("You must enter either 1 or 2.");
                        in.next();
                    }
                } while(choice == -1);

                if(choice == 2) {
                    System.out.println("Your information has remained the same.");
                } else {
                    String newFirst = "";

                    do {
                        System.out.println("\nPlease enter your new first name (if you don't wish to change it, enter an open and close bracket - e.g. {} )");
                        newFirst = in.next();

                        if(newFirst.matches(".*\\d.*")) {
                            System.out.println("Your new first name must only contain letters.");
                            newFirst = "";
                            in.next();
                        }
                    } while(newFirst.equals(""));
                    
                    String newLast = "";
                    do {
                        System.out.println("\nPlease enter your new last name (if you don't wish to change it, enter an open and close bracket - e.g. {} )");
                        newLast = in.next();

                        if(newLast.matches(".*\\d.*")) {
                            System.out.println("Your new last name must only contain letters.");
                            newLast = "";
                            in.next();
                        }
                    } while(newLast.equals(""));

                    try{
                        if(newFirst.equals("{}") && newLast.equals("{}")) {
                            System.out.println("Your information has remained the same\n");

                        } else if(newFirst.equals("{}")) {
                            System.out.println("Updating your last name!\n");

                            PreparedStatement changeLast = conn.prepareStatement("UPDATE person SET last_name = ? WHERE t_id = ?");
                            changeLast.setString(1, newLast);
                            changeLast.setInt(2, ten_id);

                            changeLast.executeUpdate();
                        } else if(newLast.equals("{}")) {
                            System.out.println("Updating your first name!\n");

                            PreparedStatement changeFirst = conn.prepareStatement("UPDATE person SET first_name = ? WHERE t_id = ?");
                            changeFirst.setString(1, newFirst);
                            changeFirst.setInt(2, ten_id);

                            changeFirst.executeUpdate();
                        } else {
                            System.out.println("Updating your information!\n");

                            PreparedStatement changeBoth = conn.prepareStatement("UPDATE person SET last_name = ?, first_name = ? WHERE t_id = ?");
                            changeBoth.setString(1, newLast);
                            changeBoth.setString(2, newFirst);
                            changeBoth.setInt(3, ten_id);

                            changeBoth.executeUpdate();
                        }
                    } catch(SQLException e) {
                        System.out.println("There was an error updating your personal data.");
                    }
                }
            } else {
                System.out.println("There is no person associated with the tenant ID " + ten_id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to get the person data for tenant " + ten_id + ".");
        }
    }


    /**
     * Allows the user to see their current lease data
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void getCurrentLease(Connection conn, Scanner in) {
        int ten_id = -1;

        do {
            System.out.println("\nPlease enter your Tenant ID to see your current lease data:");
            try {
                ten_id = in.nextInt();
            } catch(InputMismatchException e) {
                System.out.println("Tenant ID must be a number (e.g. 123).");
                in.next();
            }
        } while(ten_id == -1);

        try {
            PreparedStatement getLeaseData = conn.prepareStatement("Select apartment.apt_num, apartment.monthly_rent, apartment.size_sqft, apartment.num_bath, apartment.num_bed, lease.lease_id FROM cur_tenant, lease, apartment WHERE cur_tenant.t_id = ? AND cur_tenant.lease_id = lease.lease_id AND lease.apt_id = apartment.apt_id");
            getLeaseData.setInt(1, ten_id);
            ResultSet res = getLeaseData.executeQuery();

            if(res.next()) {
                System.out.println("\nHere is your current lease information:");
                int apt_num = res.getInt(1);
                int rent = res.getInt(2);
                int sqft = res.getInt(3);
                int bath = res.getInt(4);
                int bed = res.getInt(5);
                int lease_id = res.getInt(6);

                System.out.println("Lease ID: " + lease_id + " | Apartment Number: " + apt_num + " | Monthly Rent: " + rent + " | Total Square Feet: " + sqft + " | # of baths: " + bath + " | # of beds: " + bed );

                while(res.next()) {
                    apt_num = res.getInt(1);
                    rent = res.getInt(2);
                    sqft = res.getInt(3);
                    bath = res.getInt(4);
                    bed = res.getInt(5);

                    System.out.println("Apartment Number: " + apt_num + " | Monthly Rent: " + rent + " | Total Square Feet: " + sqft + " | # of baths: " + bath + " | # of beds: " + bed + "\n");
                }
            } else {
                System.out.println("There is no lease history for " + ten_id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong when trying to get lease data for tenant " + ten_id + ".");
        }
    }


    /**
     * Shows and allows the user to edit the current amenities they are subscribed to
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void seeAmenities(Connection conn, Scanner in) {
        HashMap<Integer, Integer> available = new HashMap<Integer, Integer>();
        try {
            PreparedStatement getAllAmenities = conn.prepareStatement("SELECT * FROM amenity");
            ResultSet res = getAllAmenities.executeQuery();

            System.out.println("\nHere are all the potential amenities:");
            while(res.next()) {
                int am_id = res.getInt(1);
                String am_type = res.getString(2);
                int add_pay = res.getInt(3);
                String addCharge = "";

                if(add_pay == 0) {
                    addCharge = "No";
                } else {
                    addCharge = "Yes";
                }

                System.out.println("Amenity ID: " + am_id + " | Amenity Type: " + am_type + " | Additional Charge? " + addCharge);
            }
        } catch(SQLException e) {
            System.out.println("There was an issue retrieving amenities.");
        }

        int lease_id = 0;
        int apt_id = 0;
        do {
            try {
                System.out.println("\nEnter your Lease ID to see your current amenities");
                lease_id = in.nextInt();

                try {
                    PreparedStatement getApt = conn.prepareStatement("SELECT apt_id FROM lease WHERE lease_id = ?");
                    getApt.setInt(1, lease_id);
                    ResultSet aptRes = getApt.executeQuery();

                    if(aptRes.next()) {
                        apt_id = aptRes.getInt(1);
                    } else {
                        System.out.println("Lease ID " + lease_id + " does not correspond to a valid Lease ID.");
                        lease_id = 0;
                    }
                } catch(SQLException e) {
                    System.out.println("There was an issue validating your Lease ID.");
                }
            } catch(InputMismatchException e) {
                System.out.println("You must input a number.");
                lease_id = 0;
                in.nextLine();
            }
        } while(lease_id == 0);

        try {
            PreparedStatement getCurrAmen = conn.prepareStatement("SELECT am_id FROM priv_amenity WHERE apt_id = ?");
            getCurrAmen.setInt(1, apt_id);
            ResultSet res = getCurrAmen.executeQuery();

            System.out.println("\nHere are your private amenities:");
            while(res.next()) {
                int am_id = res.getInt(1);

                PreparedStatement getPrivAm = conn.prepareStatement("SELECT am_type FROM amenity WHERE am_id = ?");
                getPrivAm.setInt(1, am_id);
                ResultSet desc = getPrivAm.executeQuery();

                if(desc.next()) {
                    System.out.println("Amenity ID: " + am_id + " | Amenity Description: " + desc.getString(1));
                }
            }
        } catch(SQLException e) {
            System.out.println("There was an issue retrieving your current amenities.");
        }

        try {
            PreparedStatement getProp = conn.prepareStatement("SELECT prop_id FROM apartment WHERE apt_id = ?");
            getProp.setInt(1, apt_id);
            ResultSet propRes = getProp.executeQuery();
            propRes.next();
            int prop_id = propRes.getInt(1);

            PreparedStatement getCommon = conn.prepareStatement("SELECT * FROM com_amenity WHERE prop_id = ?");
            getCommon.setInt(1, prop_id);
            ResultSet comAmenitites = getCommon.executeQuery();

            System.out.println("\nHere are the common amenities available in your building:");
            while(comAmenitites.next()) {
                int am_id = comAmenitites.getInt(1);
                int addFee = comAmenitites.getInt(2);

                PreparedStatement getPrivAm = conn.prepareStatement("SELECT am_type FROM amenity WHERE am_id = ?");
                getPrivAm.setInt(1, am_id);
                ResultSet desc = getPrivAm.executeQuery();

                if(desc.next()) {
                    System.out.println("Amenity ID: " + am_id + " | Amenity Description: " + desc.getString(1) + " | Additional Fee: " + addFee);
                    available.put(am_id, addFee);
                }
            }
        } catch(SQLException e) {
            System.out.println("There was an issue retrieving the common amenities available for your property.");
        }

        int response = 0;
        do {
            try {
                System.out.println("\nWould you like to subscribe to one of the available common amenities?\n[1] - Yes\n[2] - No");
                response = in.nextInt();

                if(response != 1 && response != 2) {
                    System.out.println("You must enter either 1 or 2.");
                    response = 0;
                }
            } catch(InputMismatchException e) {
                System.out.println("Your must enter a number.");
                response = 0;
                in.nextLine();
            }
        } while(response == 0);

        if(response == 1) {
            int ten_id = -1;
            do {
                System.out.println("\nPlease enter your Tenant ID to subscribe to a common amenity:");
                try {
                    ten_id = in.nextInt();

                    PreparedStatement checkTen = conn.prepareStatement("SELECT apt_num FROM cur_tenant WHERE t_id = ?");
                    checkTen.setInt(1, ten_id);
                    ResultSet ten = checkTen.executeQuery();

                    if(!ten.next()) {
                        System.out.println("The Tenant ID entered does not correspond to a current tenant eligible for subscribing to common amenities.");
                        ten_id = -1;
                    }
                } catch(InputMismatchException e) {
                    System.out.println("Tenant ID must be a number (e.g. 123).");
                    in.next();
                } catch(SQLException e) {
                    System.out.println("There was an issue validating the Tenant ID " + ten_id + ".");
                }
            } while(ten_id == -1);

            ArrayList<Integer> already = new ArrayList<Integer>();
            try {
                PreparedStatement getAlreadyCommon = conn.prepareStatement("SELECT am_id from priv_amenity WHERE t_id = ?");
                getAlreadyCommon.setInt(1, ten_id);
                ResultSet alreadySub = getAlreadyCommon.executeQuery();

                System.out.println("\nHere are the common amenities you are currently subscribed to:");
                while(alreadySub.next()) {
                    already.add(alreadySub.getInt(1));
                    System.out.println("Amenity ID: " + alreadySub.getInt(1));
                }
            } catch(SQLException e) {
                System.out.println("There was an issue displaying the common amenities you are currently subscribed to.");
            }

            int am_id = 0;
            do {
                try {
                    System.out.println("\nEnter the Amenity ID of the common amenity you would like to subscribe to:");
                    am_id = in.nextInt();

                    if(!available.keySet().contains(am_id)) {
                        System.out.println("The ID you entered does not correspond to an common amenity available in your building.");
                        am_id = 0;
                        in.nextLine();
                    } else if(already.contains(am_id)) {
                        System.out.println("You are already subscribed to Amenity " + am_id + ".");
                        am_id = 0;
                    }
                } catch(InputMismatchException e) {
                    System.out.println("You must input a number.");
                    in.nextLine();
                }
            } while(am_id == 0);

            try {
                PreparedStatement assocComAm = conn.prepareStatement("INSERT INTO priv_amenity(am_id, t_id) VALUES(?, ?)");
                assocComAm.setInt(1, am_id);
                assocComAm.setInt(2, ten_id);
                int res = assocComAm.executeUpdate();

                if(res == 1) {
                    System.out.println("\nSuccessfully subscribed to Amenity ID " + am_id);
                }

                PreparedStatement addCharge = conn.prepareStatement("UPDATE cur_tenant SET add_charge = add_charge + ? WHERE t_id = ?");
                addCharge.setInt(1, available.get(am_id));
                addCharge.setInt(2, ten_id);
                int success = addCharge.executeUpdate();

                if(success > 0) {
                    System.out.println("Additional fee of $" + available.get(am_id) + " added to account");
                }
            } catch(SQLException e) {
                System.out.println("There was an issue when attempting to subscribe to Amenity " + am_id + ".");
            }
        }
    }


    /**
     * FOR TESTING PURPOSES | get all the existing tenants in the database and display all their data, including their Tenant ID and Lease ID
     * @param conn Exisiting database connection
     */
    private void getAllTenants(Connection conn) {
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
}
