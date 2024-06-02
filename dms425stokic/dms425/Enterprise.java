package dms425;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import dms425.CManager.CManager;
import dms425.FManager.FManager;
import dms425.PManager.PManager;
import dms425.Tenant.Tenant;

public class Enterprise {
    static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

    public static void main(String[] args) {
        Connection conn = null;
        Scanner in = new Scanner(System.in);

        do {
            try {
                System.out.print("Enter Oracle user id: ");
                String user = in.nextLine();

                System.out.print("Enter Oracle password for " + user + ": ");
                String pass = in.nextLine();

                // Open the connection to the SQL database
                conn = DriverManager.getConnection(DB_URL, user, pass);

                int itfc = -1;
                do {
                    String options = "\nPlease type the number associated with the interface you wish to use:\n[0] - Exit Program\n[1] - Property Manager\n[2] - Tenant\n[3] - Company Manager\n[4] - Financial Manager";
                    System.out.println(options);

                    try {
                        itfc = in.nextInt();

                        if(itfc == 0) {
                            System.out.println("\nClosing all connections and exiting program");
                        } else if(itfc == 1) {
                            System.out.println("\n--- Entering the Property Manager interface ---");
                            new PManager().main(conn, in);
                        } else if(itfc == 2) {
                            System.out.println("\n--- Entering the Tenant interface ---");
                            new Tenant().main(conn, in);
                        } else if(itfc == 3) {
                            System.out.println("\n--- Entering the Company Manager interface ---");
                            new CManager().main(conn, in);
                        } else if(itfc == 4) {
                            System.out.println("\n--- Entering the Financial Manager interface ---");
                            new FManager().main(conn, in);
                        }

                    } catch(InputMismatchException e) {
                        System.out.println("You must input either 0, 1, 2, 3, or 4.");
                        in.next();
                    }

                } while (itfc != 0);


                in.close();
                conn.close();
            } catch (SQLException se) {
                System.out.println("[Error]: Connect error. Re-enter login data:");
            }

        } while (conn == null);
    }

}
