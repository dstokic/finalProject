package dms425.CManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.HashMap;

/**
 * The class for handling all Company Manager Operations
 */
public class CManager {
    
    /**
     * Action selection handler for the Company Manager Interface
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    public void main(Connection conn, Scanner in) {
        int choice = -1;

        System.out.println("\nFOR TESTING PURPOSES, HERE ARE ALL THE CURRENTLY EXISTING PROPERTIES:");
        getAllProperties(conn);

        do {
            String options = "\nPlease type the number associated with the action you wish to perform:\n[0] - Exit Interface\n[1] - Add New Property\n[2] - Add Apartment to an Existing Property";
            System.out.println(options);

            try {
                choice = in.nextInt();

                if(choice == 1) {
                    addProperty(conn, in);
                } else if(choice == 2) {
                    editProperty(conn, in);
                }

                if(choice > 2 || choice < -1) {
                    System.out.println("You must enter a number between 0 and 2 inclusively.");
                }

            } catch(InputMismatchException e) {
                System.out.println("You must input 0, 1, or 2.");
                in.next();
            }

        } while (choice != 0);
        
    }


    /**
     * Allows the Company Manager to create a completely new property and add new apartments to the property
     * This function has both an automatic generation option and a way to manually enter all information.
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void addProperty(Connection conn, Scanner in) {

        int choice = -1;
        do {
            System.out.println("\nInput the number relating to the operation you wish to perform:\n[1] - Manually Input Property Data\n[2] - Generate Property Data");
            
            try {
                choice = in.nextInt();

                if(choice != 1 && choice != 2) {
                    System.out.println("You must enter either the number 1 or 2");
                }
            } catch(InputMismatchException e) {
                System.out.println("You must enter a number");
                in.next();
            }

        } while(choice != 1 && choice != 2);

        if(choice == 1) { // MANUALLY ENTER THE DATA
            int exit = -1;

            System.out.println("\nPlease enter the property street address:");
            in.nextLine();
            String street = in.nextLine();

            System.out.println("\nPlease enter the property town:");
            String town = in.nextLine();

            System.out.println("\nPlease enter the property state abbreviation (e.g. NJ)");
            String state = in.nextLine();

            System.out.println("\nPlease enter the property zip code");
            String zip = in.nextLine();

            String address = street + ", " + town + ", " + state + " " + zip;
            System.out.println("\nFULL ADDRESS: " + address);

            int res = 0;
            int prop_id = -1;
            try {
                PreparedStatement addProp = conn.prepareStatement("INSERT INTO property(address) VALUES(?)");
                addProp.setString(1, address);

                res = addProp.executeUpdate();

                PreparedStatement getPropId = conn.prepareStatement("SELECT prop_id FROM property WHERE address = ?");
                getPropId.setString(1, address);

                ResultSet result = getPropId.executeQuery();
                result.next();
                prop_id = result.getInt(1);
            } catch(SQLException e) {
                System.out.println("There was an issue creating the new property");
            }

            do {

                do {
                    System.out.println("\nPlease input the number associated with the action you wish to complete:\n[1] - Add Apartment\n[2] - Exit Apartment Creation");

                    try {
                        exit = in.nextInt();

                        if(exit != 1 && exit != 2) {
                            System.out.println("You must enter either 1 or 2.");
                            exit = -1;
                            in.next();
                        }
                    } catch(InputMismatchException e) {
                        System.out.println("You must input a number");
                        in.next();
                    }

                } while(exit == -1);

                if(exit != 2) {
                    int sqft = 0;
                    do {
                        System.out.println("\nWhat is the square footage of the apartment?");

                        try {
                            sqft = in.nextInt();

                            if(sqft < 0) {
                                System.out.println("Your input must be a positive number");
                                sqft = 0;
                            }
                        } catch(InputMismatchException e) {
                            System.out.println("Your input must be a number");
                        }
                    } while(sqft == 0);

                    int numBath = 0;
                    do {
                        System.out.println("\nHow many baths does the apartment have?");

                        try {
                            numBath = in.nextInt();

                            if(numBath < 0) {
                                System.out.println("Your input must be a positive number");
                                numBath = 0;
                            }
                        } catch(InputMismatchException e) {
                            System.out.println("Your input must be a number");
                        }
                    } while(numBath == 0);

                    int numBed = 0;
                    do {
                        System.out.println("\nHow many beds does the apartment have?");

                        try {
                            numBed = in.nextInt();

                            if(numBed < 0) {
                                System.out.println("Your input must be a positive number");
                                numBed = 0;
                            }
                        } catch(InputMismatchException e) {
                            System.out.println("Your input must be a number");
                        }
                    } while(numBed == 0);

                    int apt_num = 0;
                    do {
                        System.out.println("\nWhat is the apartment number?");

                        try {
                            apt_num = in.nextInt();

                            if(apt_num < 0) {
                                System.out.println("Your input must be a positive number");
                                apt_num = 0;
                            }
                        } catch(InputMismatchException e) {
                            System.out.println("Your input must be a number");
                        }
                    } while(apt_num == 0);

                    int rent = 0;
                    do {
                        System.out.println("\nWhat is the monthly rent payment?");

                        try {
                            rent = in.nextInt();

                            if(rent < 0) {
                                System.out.println("Your input must be a positive number");
                                rent = 0;
                            }
                        } catch(InputMismatchException e) {
                            System.out.println("Your input must be a number");
                        }
                    } while(rent == 0);

                    int apres = 0;
                    try {
                        PreparedStatement addApt = conn.prepareStatement("INSERT INTO apartment(apt_num, sec_deposit, monthly_rent, size_sqft, num_bath, num_bed, prop_id) VALUES(?, ?, ?, ?, ?, ?, ?)");
                        addApt.setInt(1, apt_num);
                        addApt.setInt(2, rent);
                        addApt.setInt(3, rent);
                        addApt.setInt(4, sqft);
                        addApt.setInt(5, numBath);
                        addApt.setInt(6, numBed);
                        addApt.setInt(7, prop_id);

                        apres = addApt.executeUpdate();
                        if(apres == 1) {
                            System.out.println("The new apartment was added successfully");
                        }
                    } catch(SQLException e) {
                        System.out.println("There was an issue when adding the apartment to the database");
                    }

                    if(apres == 1) {
                        int apt_id = 0;
                        try {
                            PreparedStatement getAptID = conn.prepareStatement("SELECT apt_id FROM apartment WHERE apt_num = ? AND size_sqft = ? AND prop_id = ?");
                            getAptID.setInt(1, apt_num);
                            getAptID.setInt(2, sqft);
                            getAptID.setInt(3, prop_id);

                            ResultSet ressetApt = getAptID.executeQuery();
                            ressetApt.next();
                            apt_id = ressetApt.getInt(1);

                        } catch(SQLException e) {
                            System.out.println("There was an issue getting the newly created apartment's Apartment ID");
                        }

                        try {
                            PreparedStatement getPrivAmen = conn.prepareStatement("SELECT am_id, am_type FROM amenity WHERE add_pay = 0");
                            ResultSet resu = getPrivAmen.executeQuery();

                            System.out.println("\nHere are the private amenity options:");
                            int max = 0;
                            while(resu.next()) {
                                int am_id = resu.getInt(1);
                                max = am_id;
                                String am_type = resu.getString(2);

                                System.out.println("Amenity ID: " + am_id + " | Amenity Type: " + am_type);
                            }

                            int done = -1;
                            ArrayList<Integer> alreadyAdded = new ArrayList<Integer>();
                            do {
                                System.out.println("\nEnter the Amenity ID (shown above) of the amenity associated with the apartment (enter 0 if there are none/no more)");
                                try {
                                    done = in.nextInt();

                                    if(done > max) {
                                        System.out.println("\nEnter a number correlating to one of the Amenity IDs displayed above, or 0 to stop adding amenities");
                                    } else if(!alreadyAdded.contains(done)){
                                        try {
                                            PreparedStatement addAmen = conn.prepareStatement("INSERT INTO priv_amenity(am_id, apt_id) VALUES(?, ?)");
                                            addAmen.setInt(1, done);
                                            addAmen.setInt(2, apt_id);

                                            int resAm = addAmen.executeUpdate();
                                            if(resAm == 1) {
                                                alreadyAdded.add(done);
                                                System.out.println("\nSuccessfully added the amenity to the apartment");
                                            }
                                        } catch(SQLException e) {
                                            System.out.println("There was an issue associating the amenity with the apartment");
                                        }
                                    } else {
                                        System.out.println("That amenity has already been added to apartment");
                                    }

                                } catch(InputMismatchException e) {
                                    System.out.println("You must input a number");
                                }
                            } while(done != 0);

                        } catch(SQLException e) {
                            System.out.println("There was an issue generating the available private amenitites");
                        }

                        exit = -1;
                    }
                }
            } while((exit == -1 || exit == 1) && res == 1);

        } else if (choice == 2) {
            System.out.println("\nPlease enter the property street address:");
            in.nextLine();
            String street = in.nextLine();

            System.out.println("\nPlease enter the property town:");
            String town = in.nextLine();

            System.out.println("\nPlease enter the property state abbreviation (e.g. NJ)");
            String state = in.nextLine();

            System.out.println("\nPlease enter the property zip code");
            String zip = in.nextLine();

            String address = street + ", " + town + ", " + state + " " + zip;
            System.out.println("\nFULL ADDRESS: " + address);

            int res = 0;
            int prop_id = -1;
            try {
                PreparedStatement addProp = conn.prepareStatement("INSERT INTO property(address) VALUES(?)");
                addProp.setString(1, address);

                res = addProp.executeUpdate();

                PreparedStatement getPropId = conn.prepareStatement("SELECT prop_id FROM property WHERE address = ?");
                getPropId.setString(1, address);

                ResultSet result = getPropId.executeQuery();
                result.next();
                prop_id = result.getInt(1);
            } catch(SQLException e) {
                System.out.println("There was an issue creating the new property");
            }

            if(res == 1) {
                int numberApt = -1;
                do {
                    try {
                        System.out.println("\nWhat is the number of apartments you want to be generated for this property? (Maximum is 20)");
                        numberApt = in.nextInt();

                        if(numberApt > 20) {
                            System.out.println("You can generate a maximum of 20 apartments for a single property");
                            numberApt = -1;
                            in.next();
                        }
                    } catch(InputMismatchException e) {
                        System.out.println("You must input a number");
                        in.next();
                    }
                } while(numberApt == -1);


                ArrayList<Integer> privAmen = new ArrayList<Integer>();
                try {
                    PreparedStatement getPrivAmen = conn.prepareStatement("SELECT am_id FROM amenity WHERE add_pay = 0");
                    ResultSet resu = getPrivAmen.executeQuery();

                    while(resu.next()) {
                        privAmen.add(resu.getInt(1));
                    }

                } catch(SQLException e) {
                    System.out.println("There was an issue generating the available private amenitites");
                }

                try {
                    PreparedStatement assocAmen = conn.prepareStatement("INSERT INTO priv_amenity(am_id, apt_id) VALUES(?, ?)");
                    PreparedStatement addApp = conn.prepareStatement("INSERT INTO apartment(apt_num, sec_deposit, monthly_rent, size_sqft, num_bath, num_bed, prop_id) VALUES(?, ?, ?, ?, ?, ?, ?)");
                
                    for(int i = 0; i < numberApt; i++) {
                        int sqft = (int)(Math.random() * (2001)) + 500;
                        int numBath = (int)(Math.random() * 3) + 1;
                        int numBed = (int)(Math.random() * 4);
                        int sec_rent = (int) (Math.random() * 2301) + 750;
                        int apt_num = (int) (Math.random() * 951) + 49;

                        addApp.setInt(1, apt_num);
                        addApp.setInt(2, sec_rent);
                        addApp.setInt(3, sec_rent);
                        addApp.setInt(4, sqft);
                        addApp.setInt(5, numBath);
                        addApp.setInt(6, numBed);
                        addApp.setInt(7, prop_id);

                        int resu = addApp.executeUpdate();

                        PreparedStatement getAptId = conn.prepareStatement("SELECT apt_id FROM apartment WHERE apt_num = ? AND size_sqft = ? AND prop_id = ?");
                        getAptId.setInt(1, apt_num);
                        getAptId.setInt(2, sqft);
                        getAptId.setInt(3, prop_id);

                        ResultSet result = getAptId.executeQuery();
                        result.next();
                        int apt_id = result.getInt(1);

                        if(resu == 1) {
                            int numAmen = (int)(Math.random() * 5); // Generate a random number from 0 - 4

                            for(int j = 0; j < numAmen; j++) {
                                int curr = privAmen.get(j);
                                assocAmen.setInt(1, curr);
                                assocAmen.setInt(2, apt_id);

                                assocAmen.executeUpdate();
                            }

                            System.out.println("SQFT: " + sqft + " | NUM_BATH: " + numBath + " | NUM_BED: " + numBed + " | RENT: " + sec_rent + " | APT_NUM: "  + apt_num + " | NUM_AMENITY: " + numAmen);
                        }

                    }

                } catch(SQLException e) {
                    System.out.println("There was an issue inserting the new apartments");
                }

            }
        }
    }


    /**
     * Allows the Company Manager to add apartments to an already existing property
     * There is no automatic generation option for this function
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     */
    private void editProperty(Connection conn, Scanner in) {

        int prop_id = 0;
        do {
            try {
                System.out.println("\nPlease enter the Property ID of the property you wish to add apartments to:");
                prop_id = in.nextInt();

                PreparedStatement checkId = conn.prepareStatement("SELECT address FROM property WHERE prop_id = ?");
                checkId.setInt(1, prop_id);

                ResultSet res = checkId.executeQuery();
                if(!res.next()) {
                    System.out.println("The Property ID you entered does not correspond to an existing Property ID");
                    prop_id = 0;
                }

            } catch(InputMismatchException e) {
                System.out.println("You must input a number");
                in.nextLine();
            } catch(SQLException e) {
                System.out.println("There was an issue validating the input Property ID");
            }
            
        } while(prop_id == 0);


        int done = -1;
        do {
            try {
                System.out.println("\nPlease input the number associated with the action you wish to perform\n[0] - Done Adding Apartments\n[1] - Add Another Apartment");
                done = in.nextInt();

                if(done != 0 && done != 1 && done != -1) {
                    System.out.println("You need to enter either 0 or 1.");
                    done = -1;
                }

                if(done == 1) {
                    int apt_id = makeApartment(conn, in, prop_id);
                    
                    System.out.println("\nHere is the Apartment ID for the newly created apartment: " + apt_id);

                    int amen = -1;
                    do {
                        try {
                            System.out.println("\nWould you like to add private amenities to this apartment\n[0] - No\n[1] - Yes");
                            amen = in.nextInt();

                            if(amen != 0 && amen != 1 && amen != -1) {
                                System.out.println("You may only enter either 0 or 1");
                                amen = -1;
                            }
                        } catch(InputMismatchException e) {
                            System.out.println("You must input a number");
                        }
                    } while(amen == -1);

                    if(amen == 1) {
                        HashMap<Integer, String> priv = new HashMap<Integer, String>();
                        try {
                            PreparedStatement getPrivAmen = conn.prepareStatement("SELECT am_id, am_type FROM amenity WHERE add_pay = 0");
                            ResultSet resu = getPrivAmen.executeQuery();

                            System.out.println("\nHere are the available private amenities:");
                            while(resu.next()) {
                                int am_id = resu.getInt(1);
                                String am_type = resu.getString(2);
                                priv.put(am_id, am_type);

                                System.out.println("Amenity ID: " + am_id + " | Amenity Description: " + am_type);
                            }
                        } catch(SQLException e) {
                            System.out.println("There was an issue when trying to display available private amenities.");
                        }

                        ArrayList<Integer> alreadyDone = new ArrayList<>();
                        int cont = -1;
                        do {
                            System.out.println("\nInsert the Amenity ID of the private amenity you wish to add to the apartment (Enter '0' to stop)");
                            try {
                                cont = in.nextInt();

                                if(cont != 0 && !alreadyDone.contains(cont)) {
                                    alreadyDone.add(cont);
                                    PreparedStatement insertPrivAmen = conn.prepareStatement("INSERT INTO priv_amenity(am_id, apt_id) VALUES(?, ?)");
                                    insertPrivAmen.setInt(1, cont);
                                    insertPrivAmen.setInt(2, apt_id);

                                    int success = insertPrivAmen.executeUpdate();
                                    if(success == 1) {
                                        System.out.println("Private amenity " + priv.get(cont) + " successfully added to Apartment ID " + apt_id);
                                    }
                                } else if(alreadyDone.contains(cont)) {
                                    System.out.println("You have already added that amenity to the apartment.");
                                }
                            } catch(InputMismatchException e) {
                                System.out.println("You must input a number.");
                                in.nextLine();
                            } catch(SQLException e) {
                                System.out.println("There was an issue associating that Amenity ID with the created Apartment.");
                            }

                        } while(cont != 0);
                    }
                }
                
            } catch(InputMismatchException e) {
                System.out.println("You must input a number.");
                done = -1;
                in.nextLine();
            }
        } while(done != 0);
    }


    /**
     * Prompts the user for all the input necessary for adding an apartment.
     * @param conn Exisiting database connection
     * @param in Scanner for reading user input
     * @param prop_id Property ID given by the user
     * @return the newly generated apt_id
     */
    private int makeApartment(Connection conn, Scanner in, int prop_id) {   
        int sqft = 0;
        do {
            System.out.println("\nWhat is the square footage of the apartment?");

            try {
                sqft = in.nextInt();

                if(sqft < 0) {
                    System.out.println("Your input must be a positive number");
                    sqft = 0;
                }
            } catch(InputMismatchException e) {
                System.out.println("Your input must be a number");
            }
        } while(sqft == 0);

        int numBath = 0;
        do {
            System.out.println("\nHow many baths does the apartment have?");

            try {
                numBath = in.nextInt();

                if(numBath < 0) {
                    System.out.println("Your input must be a positive number");
                    numBath = 0;
                }
            } catch(InputMismatchException e) {
                System.out.println("Your input must be a number");
            }
        } while(numBath == 0);

        int numBed = 0;
        do {
            System.out.println("\nHow many beds does the apartment have?");

            try {
                numBed = in.nextInt();

                if(numBed < 0) {
                    System.out.println("Your input must be a positive number");
                    numBed = 0;
                }
            } catch(InputMismatchException e) {
                System.out.println("Your input must be a number");
            }
        } while(numBed == 0);

        int apt_num = 0;
        do {
            System.out.println("\nWhat is the apartment number?");

            try {
                apt_num = in.nextInt();

                if(apt_num < 0) {
                    System.out.println("Your input must be a positive number");
                    apt_num = 0;
                }
            } catch(InputMismatchException e) {
                System.out.println("Your input must be a number");
            }
        } while(apt_num == 0);

        int rent = 0;
        do {
            System.out.println("\nWhat is the monthly rent payment?");

            try {
                rent = in.nextInt();

                if(rent < 0) {
                    System.out.println("Your input must be a positive number");
                    rent = 0;
                }
            } catch(InputMismatchException e) {
                System.out.println("Your input must be a number");
            }
        } while(rent == 0);

        int apres = 0;
        try {
            PreparedStatement addApt = conn.prepareStatement("INSERT INTO apartment(apt_num, sec_deposit, monthly_rent, size_sqft, num_bath, num_bed, prop_id) VALUES(?, ?, ?, ?, ?, ?, ?)");
            addApt.setInt(1, apt_num);
            addApt.setInt(2, rent);
            addApt.setInt(3, rent);
            addApt.setInt(4, sqft);
            addApt.setInt(5, numBath);
            addApt.setInt(6, numBed);
            addApt.setInt(7, prop_id);

            apres = addApt.executeUpdate();
            if(apres == 1) {
                System.out.println("The new apartment was added successfully");
            }
        } catch(SQLException e) {
            System.out.println("There was an issue when adding the apartment to the database");
        }

        int apt_id = -1;
        if(apres == 1) {
            try {
                PreparedStatement getAptID = conn.prepareStatement("SELECT apt_id FROM apartment WHERE apt_num = ? AND size_sqft = ? AND prop_id = ?");
                getAptID.setInt(1, apt_num);
                getAptID.setInt(2, sqft);
                getAptID.setInt(3, prop_id);

                ResultSet ressetApt = getAptID.executeQuery();
                ressetApt.next();
                apt_id = ressetApt.getInt(1);

            } catch(SQLException e) {
                System.out.println("There was an issue getting the newly created apartment's Apartment ID");
            }
        }

        return apt_id;
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
