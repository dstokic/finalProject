package data_gen;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class data_gen {

    /**
     * This class is not actually used in the program, it's just to show the code used for generating apartment data
     * This code is being used in the CManager Class
     * @param conn
     */
    private void main(Connection conn) {
        int prop_id = 0; // GOTTEN FROM THE NEWLY CREATED PROPERTY IN THE CMANAGER CLASS
        int numberApt = 0; // GOTTEN FROM THE USER IN THE CMANAGER CLASS

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
