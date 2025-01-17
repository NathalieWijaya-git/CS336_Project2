package com.example.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;
import java.util.Scanner;


public class MortgageProcessor {

    private Connection conn;

    //Constructor
    public MortgageProcessor(Connection conn) {
        this.conn = conn;
    }

    public void calculateRate(List<Map<String, Object>> filteredResults) {
        if (filteredResults == null || filteredResults.isEmpty()) {
            System.out.println("No results to process for rate calculation.");
            return;
        }

        int totalLoanAmount = 0; 
        double weightedRateSum = 0;
        final double baseRate = 2.33;

        for(Map<String, Object> result : filteredResults) {

            Integer loanAmountObj = (Integer) result.get("loan_amount_000s"); // Get loan amount
            Integer lienStatus = (Integer) result.get("lien_status"); // Get lien status
            Double rateSpreadObj = (Double) result.get("rate_spread"); // Get rate spread

            //Default to 0.0 if loanAmount is null
            int loanAmount = (loanAmountObj != null) ? loanAmountObj : 0;
        
            //Default to 0.0 if rate spread is null
            double rateSpread = (rateSpreadObj != null) ? rateSpreadObj : 0.0;

            if(rateSpread == 0) { //If rate spread is null
                if(lienStatus == 1) { 
                    rateSpread = 1.5; //Assign 1.5 if lien status is 1
                } else if(lienStatus == 2) {
                    rateSpread = 3.5; //Assign 3.5 if lien status is 2
                }
            }

            double rate = baseRate + rateSpread;

            weightedRateSum += rate*loanAmount; //The sum of rate, each calculated by multiplying it to its corresponding loan amount
            totalLoanAmount += loanAmount; //Sums the total loan amount 
        }

        if(totalLoanAmount > 0) {
            double expectedRate = weightedRateSum/totalLoanAmount;
            System.out.println("The expected rate is: " + expectedRate);
            return;
        } else {
            System.out.println("No valid loan amounts.");
            return;
        }
    }

    public void updatePurchaserType(List<Map<String, Object>> filteredResults) {
        if (filteredResults == null || filteredResults.isEmpty()) {
            System.out.println("No filtered results to update.");
            return;
        }

        //Start a connection and set the transaction isolation level to SERIALIZABLE
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "1234")) {
            //Set the transaction isolation level to SERIALIZABLE
            conn.setAutoCommit(false);  //Disable auto-commit
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);  //Set isolation level

            //Create a temporary table with applicant_id
            String createTableQuery = "CREATE TEMP TABLE temp_filtered_results (applicant_id INTEGER)";

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTableQuery);
                System.out.println("Temporary table created.");
            } catch (SQLException e) {
                System.err.println("Error creating temporary table: " + e.getMessage());
            }

            //Insert values into temp_filtered_results
            String insertQuery = "INSERT INTO temp_filtered_results (applicant_id) VALUES (?)";

            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                for (Map<String, Object> row : filteredResults) {
                    stmt.setObject(1, (Integer) row.get("applicant_id"));
                    stmt.addBatch();
                }

                stmt.executeBatch();
                System.out.println("Filtered results inserted into temporary table.");
            } catch (SQLException e) {
                System.err.println("Error inserting filtered results: " + e.getMessage());
            }

            //Update project_2_view by joining with temp_filtered_results
            String updateQuery = "UPDATE project_2_view p2v SET purchaser_type = 5 FROM temp_filtered_results tfr WHERE p2v.applicant_id = tfr.applicant_id"; //Update purchaser_type to 5

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                int rowsAffected = stmt.executeUpdate();
                System.out.println("Purchaser type updated for " + rowsAffected + " rows.");
            } catch (SQLException e) {
                System.err.println("Error updating project_2_view: " + e.getMessage());
            }

            //Commit the transaction
            conn.commit();
            System.out.println("Transaction committed.");
        } catch (SQLException e) {
            //Rollback in case of error
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Error with transaction: " + e.getMessage());
        } finally {
            //Explicitly drop the temporary table
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "1234")) {
                String dropTableQuery = "DROP TABLE IF EXISTS temp_filtered_results";
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(dropTableQuery);
                    System.out.println("Temporary table dropped.");
                }
            } catch (SQLException e) {
                System.err.println("Error dropping temporary table: " + e.getMessage());
            }
            
            //Reset auto-commit to true after transaction
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "1234")) {
                conn.setAutoCommit(true);  // Ensure auto-commit is reset to true if needed
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public void createMortgage() {
        Scanner myScanner = new Scanner(System.in);

        //Gather user input for new mortgage application
        System.out.println("Please provide the following details for a new mortgage application.");

        System.out.println("Applicant Income: ");
        int applicantIncome = myScanner.nextInt();

        System.out.println("Loan Amount: ");
        int loanAmount = myScanner.nextInt();

        //Displays the options of MSAMDs available
        String msamdDisplay = "SELECT *, COALESCE(msamd_name, CAST(msamd AS TEXT)) AS msamd_display FROM msamd_table;";
        try (PreparedStatement stmt = conn.prepareStatement(msamdDisplay)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No data found in the table.");
                } else {
                    //Loop through all the result rows and display the available MSAMD options
                    System.out.println("Available MSAMD Names/Codes:");
                    do {
                        String msamdDisplayValue = rs.getString("msamd_display");
                        System.out.println("MSAMD Name/Code: " + msamdDisplayValue);
                    } while (rs.next());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Please enter the MSAMD Name/Code based on the options available: ");
        myScanner.nextLine();
        String msamdName = myScanner.nextLine();
        if (msamdName.trim().isEmpty()) msamdName = null;

        //Displays the options of sex available
        String sexDisplay = "SELECT * FROM sex_table;";
        try (PreparedStatement stmt = conn.prepareStatement(sexDisplay)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No data found in the table.");
                } else {
                    //Loop through all the result rows and display the available sex options
                    System.out.println("Available Sex:");
                    do {
                        String sexDisplayValue = rs.getString("sex_name");
                        System.out.println("Sex: " + sexDisplayValue);
                    } while (rs.next());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Please indicate applicant's sex based on the options available: ");
        String applicantSexName = myScanner.nextLine();
        if (applicantSexName.trim().isEmpty()) applicantSexName = null;

        //Displays the options of loan types available
        String loanTypeDisplay = "SELECT * FROM loan_type_table;";
        try (PreparedStatement stmt = conn.prepareStatement(loanTypeDisplay)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No data found in the table.");
                } else {
                    //Loop through all the result rows and display the available loan type options
                    System.out.println("Available Loan Types:");
                    do {
                        String loanTypeDisplayValue = rs.getString("loan_type_name");
                        System.out.println("Loan Type: " + loanTypeDisplayValue);
                    } while (rs.next());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Please enter the desired loan type based on the options available: ");
        String loanTypeName = myScanner.nextLine();
        if (loanTypeName.trim().isEmpty()) loanTypeName = null;

        //Displays the options of loan types available
        String applicantEthnicityDisplay = "SELECT * FROM ethnicity_table;";
        try (PreparedStatement stmt = conn.prepareStatement(applicantEthnicityDisplay)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No data found in the table.");
                } else {
                    //Loop through all the result rows and display the available applicant ethnicity options
                    System.out.println("Available Applicant Ethnicities:");
                    do {
                        String applicantEthnicityDisplayValue = rs.getString("ethnicity_name");
                        System.out.println("Applicant Ethnicity: " + applicantEthnicityDisplayValue);
                    } while (rs.next());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Please indicate applicant's ethnicity based on the options available: ");
        String applicantEthnicityName = myScanner.nextLine();
        if (applicantEthnicityName.trim().isEmpty()) applicantEthnicityName = null;

        //SQL query to insert data into the preliminary table
        String insertPreliminaryQuery = "INSERT INTO preliminary (applicant_income_000s, loan_amount_000s, msamd_name, applicant_sex_name, loan_type_name, applicant_ethnicity_name) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertPreliminaryQuery)) {
            stmt.setInt(1, applicantIncome); //Set applicant income
            stmt.setInt(2, loanAmount); //Set loan amount
            stmt.setString(3, msamdName); //Set MSAMD
            stmt.setString(4, applicantSexName); //Set applicant sex
            stmt.setString(5, loanTypeName); //Set loan type
            stmt.setString(6, applicantEthnicityName); //Set ethnicity
        
            stmt.executeUpdate(); //Execute the insertion
            System.out.println("Data inserted into preliminary table.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } 
}
