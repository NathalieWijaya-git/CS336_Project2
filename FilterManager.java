package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class FilterManager {

    private Connection conn;

    public String msamdValue;
    public Double minRatio, maxRatio;
    public String countyNames;
    public String loanTypeValue;
    public Double minNum, maxNum;
    public String loanPurposeName;
    public String propertyTypeName;
    public String ownerOccupancyName;
    public Set<Integer> uniqueChoiceTracker = new LinkedHashSet<>();

    //Constructor
    public FilterManager(Connection conn) {
        this.conn = conn;
    }

    //Getter for uniqueChoiceTracker
    public void getUniqueChoiceTracker(Set<Integer> uniqueChoiceTracker) {
        this.uniqueChoiceTracker = uniqueChoiceTracker;
    }

    //Setter for uniqueChoiceTracker
    public Set<Integer> setUniqueChoiceTracker() {
        return uniqueChoiceTracker;
    }

    //Getter for msamdValue
    public void getMsamdValue(String msamdValue) {
        this.msamdValue = msamdValue;
    }

    //Setter for msamdValue
    public String setMsamdValue() {
        return msamdValue;
    }

    //Getter for minRatio
    public void getMinRatio(Double minRatio) {
        this.minRatio = minRatio;
    }

    //Setter for minRatio
    public Double setMinRatio() {
        return minRatio;
    }

    //Getter for maxRatio
    public void getMaxRatio(Double maxRatio) {
        this.maxRatio = maxRatio;
    }

    //Setter for maxRatio
    public Double setMaxRatio() {
        return maxRatio;
    }

    //Getter for countyNames
    public void getCountyNames(String countyNames) {
        this.countyNames = countyNames;
    }

    //Setter for countyNames
    public String setCountyNames() {
        return countyNames;
    }

    //Getter for loanTypeValue
    public void getLoanTypeValue(String loanTypeValue) {
        this.loanTypeValue = loanTypeValue;
    }

    //Setter for loanTypeValue
    public String setLoanTypeValue() {
        return loanTypeValue;
    }

    //Getter for minNum
    public void getMinNum(Double minNum) {
        this.minNum = minNum;
    }

    //Setter for minNum
    public Double setMinNum() {
        return minNum;
    }

    //Getter for maxNum
    public void getMaxNum(Double maxNum) {
        this.maxNum = maxNum;
    }

    //Setter for maxNum
    public Double setMaxNum() {
        return maxNum;
    }

    //Getter for loanPurposeName
    public void getLoanPurposeName(String loanPurposeName) {
        this.loanPurposeName = loanPurposeName;
    }

    //Setter for loanPurposeName
    public String setLoanPurposeName() {
        return loanPurposeName;
    }

    //Getter for propertyTypeName
    public void getPropertyTypeName(String propertyTypeName) {
        this.propertyTypeName = propertyTypeName;
    }

    // Setter for propertyTypeName
    public String setPropertyTypeName() {
        return propertyTypeName;
    }

    // Getter for ownerOccupancyName
    public void getOwnerOccupancyName(String ownerOccupancyName) {
        this.ownerOccupancyName = ownerOccupancyName;
    }
    
    // Setter for ownerOccupancyName
    public String setOwnerOccupancyName() {
        return ownerOccupancyName;
    }

    //MSAMD Filter
    public List<Map<String, Object>> filterByMsamd(String msamdValue) {   
        // Split the input string into separate MSAMD values
        List<String> msamdArray = Arrays.asList(msamdValue.split("\\s*,\\s*"));
    
        //Check if MSAMD input is empty or null
        if (msamdValue == null || msamdValue.isEmpty()) {
            System.out.println("No MSAMD(s) provided for filtering.");
            return null; 
        }
    
        //Build SQL query with placeholders
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM project_2_view p2v JOIN msamd_table mt ON p2v.msamd = mt.msamd WHERE (mt.msamd_name IN (");
    
        for (int i = 0; i < msamdArray.size(); i++) {
            queryBuilder.append("?");
            if (i < msamdArray.size() - 1) {
                queryBuilder.append(", "); //Add comma except for the last parameter
            }
        } queryBuilder.append(") OR CAST(mt.msamd AS TEXT) IN (");
    
        for (int i = 0; i < msamdArray.size(); i++) {
            queryBuilder.append("?");
            if (i < msamdArray.size() - 1) {
                queryBuilder.append(", "); //Add comma except for the last parameter
            }
        } queryBuilder.append("));");
    
        List<Map<String, Object>> results = new ArrayList<>();
        int rowCount = 0; //Initialize row count
        int totalLoanAmount = 0; //Initialize total loan amount
    
        //Convert SQL query to String
        String msamdFilterQuery = queryBuilder.toString();
    
        try (PreparedStatement stmt = conn.prepareStatement(msamdFilterQuery)) {
            //Set the values for msamd_name placeholders
            for (int i = 0; i < msamdArray.size(); i++) {
                stmt.setString(i + 1, msamdArray.get(i)); //For msamd_name
            }
            //Set the values for msamd code placeholders
            for (int i = 0; i < msamdArray.size(); i++) {
                stmt.setString(msamdArray.size() + i + 1, msamdArray.get(i)); //For msamd code
            }
    
            //Execute the query and get the results
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No results found for the selected MSAMD.");
                } else {
                    // Process the result set and add to the list
                    do {
                        Map<String, Object> row = new HashMap<>();
                        int loanAmount = rs.getInt("loan_amount_000s");
                        
                        row.put("applicant_id", rs.getInt("applicant_id"));
                        row.put("action_taken", rs.getInt("action_taken"));
                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                        row.put("msamd", rs.getString("msamd"));
                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                        row.put("loan_amount_000s", loanAmount);
                        row.put("county_code", rs.getString("county_code"));
                        row.put("loan_type", rs.getInt("loan_type"));
                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                        row.put("property_type", rs.getInt("property_type"));
                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                        row.put("lien_status", rs.getInt("lien_status"));
                        row.put("rate_spread", rs.getDouble("rate_spread"));
    
                        // Add the row to the results list
                        results.add(row);

                        //Update row count and total loan amount
                        rowCount++;
                        totalLoanAmount += loanAmount;
    
                        //System.out.println("MSAMD Name: " + rs.getString("msamd_name") +", Lien Status: " + rs.getInt("lien_status") + ", Rate Spread: " + rs.getDouble("rate_spread"));
                    } while (rs.next());
                }
            }
            //Display the number of rows and the sum of loan amounts
            System.out.println("Number of rows matching the filters: " + rowCount);
            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
        } catch (SQLException e) {
            // Handle SQL exceptions and provide error messages
            System.err.println("Error executing MSAMD filter query: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        getMsamdValue(msamdValue);
        return results;   
    } 
    
    public List<Map<String, Object>> filterByIncomeToDebtRatio(Double minRatio, Double maxRatio) {
        // Ensure minRatio and maxRatio are valid before building the query
        if (minRatio == null && maxRatio == null) {
            System.out.println("No income-to-debt ratio range provided for filtering.");
            return new ArrayList<>(); // Return empty list if no valid ratio provided
        }

        //Build SQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT *, COALESCE((applicant_income_000s / NULLIF(loan_amount_000s, 0)), 0) FROM project_2_view WHERE ");

        //SQL query placeholder
        int paramIndex = 1;

        if(minRatio != null && maxRatio != null) { //Both min and max ratio are provided
            queryBuilder.append("(CAST(applicant_income_000s AS NUMERIC) / loan_amount_000s) >= ? AND (applicant_income_000s / loan_amount_000s) <= ?");
        } else if(minRatio != null && maxRatio == null) { //Only min ratio is provided
            queryBuilder.append("(CAST(applicant_income_000s AS NUMERIC) / loan_amount_000s) >= ?");
        } else if(minRatio == null && maxRatio != null) { //Only max ratio is provided
            queryBuilder.append("(CAST(applicant_income_000s AS NUMERIC) / loan_amount_000s) <= ?");
        } queryBuilder.append(";");

        List<Map<String, Object>> results = new ArrayList<>();
        int rowCount = 0; // Initialize row count
        int totalLoanAmount = 0; // Initialize total loan amount

        //Converts SQL query to String
        String incomeToDebtRatioFilter = queryBuilder.toString();

        try(PreparedStatement stmt = conn.prepareStatement(incomeToDebtRatioFilter)) {
            //Set the min ratio value with placeholders (if provided)
            if(minRatio != null) {
                stmt.setDouble(paramIndex++, minRatio);
            }

            //Set the max ratio with placeholders (if provided)
            if(maxRatio != null) {
                stmt.setDouble(paramIndex++, maxRatio);
            }

            //Execute the query
            try(ResultSet rs = stmt.executeQuery()) {
                if(!rs.next()) {
                    System.out.println("No results found for the given income-to-debt ratio.");
                } else {
                    //System.out.println("Income-to-Debt Ratio Results:");
                    do {
                        //Fetch values from the result set
                        //double applicantIncome = rs.getDouble("applicant_income_000s");
                        int loanAmount = rs.getInt("loan_amount_000s");

                        //Calculate ratio
                        //double calculatedRatio = loanAmount != 0 ? applicantIncome/loanAmount : 0;

                        //Create a row map
                        Map<String, Object> row = new HashMap<>();
                        row.put("applicant_id", rs.getInt("applicant_id"));
                        row.put("action_taken", rs.getInt("action_taken"));
                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                        row.put("msamd", rs.getString("msamd"));
                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                        row.put("loan_amount_000s", loanAmount);
                        row.put("county_code", rs.getString("county_code"));
                        row.put("loan_type", rs.getInt("loan_type"));
                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                        row.put("property_type", rs.getInt("property_type"));
                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                        row.put("lien_status", rs.getInt("lien_status"));
                        row.put("rate_spread", rs.getDouble("rate_spread"));

                        //Add the row to the results list
                        results.add(row);

                        //Update row count and total loan amount
                        rowCount++;
                        totalLoanAmount += loanAmount;

                        //System.out.println("Applicant Income: " + applicantIncome + ", Loan Amount: " + loanAmount + ", Ratio: " + calculatedRatio + ", Lien Status: " + lienStatus + ", Rate Spread: " + rateSpread);
                    } while(rs.next());
                }
            }
            //Display the number of rows and the sum of loan amounts
            System.out.println("Number of rows matching the filters: " + rowCount);
            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
        } catch (SQLException e) {
            //Handle SQL exceptions and provide error messages
            System.err.println("Error executing Income to Debt Ratio filter query: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        getMinRatio(minRatio);
        getMaxRatio(minRatio);
        return results;
    }

    public List<Map<String, Object>> filterByCounty(String countyNames) {
        //Split the input string into separate county names
        List<String> countyArray = Arrays.asList(countyNames.split("\\s*,\\s*"));

        //Check if counties list is empty or null
        if(countyNames == null || countyNames.isEmpty()) {
            System.out.println("No county name(s) provided for filtering.");
            return null; 
        }

        // Print the selected county name(s)
        if (!countyArray.isEmpty()) {
            System.out.println("The county name(s) that you selected: " + String.join(", ", countyArray));
        } else {
            System.out.println("No county name(s) selected.");
        }

        //Build SQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM project_2_view p2v JOIN county_table ct ON p2v.county_code = ct.county_code WHERE ct.county_name IN (");

        //Append the correct number of question marks based on the county names list size
        for(int i = 0; i < countyArray.size(); i++) {
            queryBuilder.append("?");
            if(i < countyArray.size() - 1) {
                queryBuilder.append(", "); //Add comma except for the last parameter
            }
        } queryBuilder.append(");");

        List<Map<String, Object>> results = new ArrayList<>();
        int rowCount = 0; // Initialize row count
        int totalLoanAmount = 0; // Initialize total loan amount

        //Convert SQL query to a String
        String countyNamesFilterQuery = queryBuilder.toString();

        try(PreparedStatement stmt = conn.prepareStatement(countyNamesFilterQuery)) {
            //Set each county parameter in the PreparedStatement
            for(int i = 0; i < countyArray.size(); i++) {
                stmt.setString(i + 1, countyArray.get(i)); // Set each county in the prepared statement
            }

            //Execute the query
            try(ResultSet rs = stmt.executeQuery()) {
                //If no results are found
                if(!rs.next()) {
                    System.out.println("No results found for the given county name(s).");
                } else {
                    //System.out.println("County Filter Results:");
                    //Process the result set
                    do {
                        // Process the result set and add to the list
                        Map<String, Object> row = new HashMap<>();
                        int loanAmount = rs.getInt("loan_amount_000s");

                        row.put("applicant_id", rs.getInt("applicant_id"));
                        row.put("action_taken", rs.getInt("action_taken"));
                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                        row.put("msamd", rs.getString("msamd"));
                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                        row.put("loan_amount_000s", loanAmount);
                        row.put("county_code", rs.getString("county_code"));
                        row.put("loan_type", rs.getInt("loan_type"));
                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                        row.put("property_type", rs.getInt("property_type"));
                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                        row.put("lien_status", rs.getInt("lien_status"));
                        row.put("rate_spread", rs.getDouble("rate_spread"));

                        // Add the row to the results list
                        results.add(row);

                        //Update row count and total loan amount
                        rowCount++;
                        totalLoanAmount += loanAmount;

                        //Output the results
                        //System.out.println("County Name: " + rs.getString("county_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                    } while(rs.next()); // Continue until all rows are processed
                }
            }
            //Display the number of rows and the sum of loan amounts
            System.out.println("Number of rows matching the filters: " + rowCount);
            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
        } catch (SQLException e) {
            //Handle SQL exceptions and provide error messages
            System.err.println("Error executing County filter query: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        getCountyNames(countyNames);
        return results;
    }

    public List<Map<String, Object>> filterByLoanType(String loanTypeValue) {       
        //Split the input string into separate loan type values
        List<String> loanTypeArray = Arrays.asList(loanTypeValue.split("\\s*,\\s*"));

        //Check if loan type list is empty or null
        if(loanTypeValue == null || loanTypeValue.isEmpty()) {
            System.out.println("No loan type(s) provided for filtering.");
            return null; 
        }

        // Print the selected loan type(s)
        if (!loanTypeArray.isEmpty()) {
            System.out.println("The loan type(s) that you selected: " + String.join(", ", loanTypeArray));
        } else {
            System.out.println("No loan type(s) selected.");
        }

        //Build SQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM project_2_view p2v JOIN loan_type_table ltt ON p2v.loan_type = ltt.loan_type WHERE ltt.loan_type_name IN (");

        //Append the correct number of question marks based on the loan type list size
        for(int i = 0; i < loanTypeArray.size(); i++) {
            queryBuilder.append("?");
            if(i < loanTypeArray.size() - 1) {
                queryBuilder.append(", "); //Add comma except for the last parameter
            }
        } queryBuilder.append(");");

        List<Map<String, Object>> results = new ArrayList<>();
        int rowCount = 0; // Initialize row count
        int totalLoanAmount = 0; // Initialize total loan amount

        //Converts SQL query to String
        String loanTypeFilterQuery = queryBuilder.toString();

        try (PreparedStatement stmt = conn.prepareStatement(loanTypeFilterQuery)) {
            //Set placeholder values
            for (int i = 0; i < loanTypeArray.size(); i++) {
                stmt.setString(i + 1, loanTypeArray.get(i));
            }

            //Execute query 
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No results found for the selected loan type.");
                } else {
                    //System.out.println("Loan Type Results:");
                    do {
                        // Process the result set and add to the list
                        Map<String, Object> row = new HashMap<>();
                        int loanAmount = rs.getInt("loan_amount_000s");

                        row.put("applicant_id", rs.getInt("applicant_id"));
                        row.put("action_taken", rs.getInt("action_taken"));
                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                        row.put("msamd", rs.getString("msamd"));
                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                        row.put("loan_amount_000s", loanAmount);
                        row.put("county_code", rs.getString("county_code"));
                        row.put("loan_type", rs.getInt("loan_type"));
                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                        row.put("property_type", rs.getInt("property_type"));
                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                        row.put("lien_status", rs.getInt("lien_status"));
                        row.put("rate_spread", rs.getDouble("rate_spread"));

                        // Add the row to the results list
                        results.add(row);

                        //Update row count and total loan amount
                        rowCount++;
                        totalLoanAmount += loanAmount;

                        //System.out.println("Loan Type Name: " + rs.getString("loan_type_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                    } while (rs.next());
                }
            }
            //Display the number of rows and the sum of loan amounts
            System.out.println("Number of rows matching the filters: " + rowCount);
            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
        } catch (SQLException e) {
            //Handle SQL exceptions and provide error messages
            System.err.println("Error executing Loan Type filter query: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        getLoanTypeValue(loanTypeValue);
        return results;   
    } 

    public List<Map<String, Object>> filterByTractToMsamdIncome(Double minNum, Double maxNum) {
        //Build SQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM project_2_view WHERE ");

        //SQL query placeholder
        int paramIndex = 1;

        if(minNum != null && maxNum != null) { //Both min and max numericals are provided
            queryBuilder.append("tract_to_msamd_income >= ? AND tract_to_msamd_income <= ?");
        } else if(minNum != null && maxNum == null) { //Only min numerical is provided
            queryBuilder.append("tract_to_msamd_income >= ?");
        } else if(minNum == null && maxNum != null) { //Only max numerical is provided
            queryBuilder.append("tract_to_msamd_income <= ?");
        } queryBuilder.append(";");

        List<Map<String, Object>> results = new ArrayList<>();
        int rowCount = 0; // Initialize row count
        int totalLoanAmount = 0; // Initialize total loan amount

        //Converts SQL query to String
        String tractToMsamdIncomeFilter = queryBuilder.toString();

        try(PreparedStatement stmt = conn.prepareStatement(tractToMsamdIncomeFilter)) {
            //Set the min numerical value with placeholders (if provided)
            if(minNum != null) {
                stmt.setDouble(paramIndex++, minNum);
            }

            //Set the max numerical value with placeholders (if provided)
            if(maxNum != null) {
                stmt.setDouble(paramIndex++, maxNum);
            }

            //Execute the query
            try(ResultSet rs = stmt.executeQuery()) {
                if(!rs.next()) {
                    System.out.println("No results found for the given tract-to-msamd income.");
                } else {
                    //System.out.println("Tract-to-Msamd Income Results:");
                    do {
                        // Process the result set and add to the list
                        Map<String, Object> row = new HashMap<>();
                        int loanAmount = rs.getInt("loan_amount_000s");

                        row.put("applicant_id", rs.getInt("applicant_id"));
                        row.put("action_taken", rs.getInt("action_taken"));
                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                        row.put("msamd", rs.getString("msamd"));
                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                        row.put("loan_amount_000s", loanAmount);
                        row.put("county_code", rs.getString("county_code"));
                        row.put("loan_type", rs.getInt("loan_type"));
                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                        row.put("property_type", rs.getInt("property_type"));
                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                        row.put("lien_status", rs.getInt("lien_status"));
                        row.put("rate_spread", rs.getDouble("rate_spread"));

                        // Add the row to the results list
                        results.add(row);

                        //Update row count and total loan amount
                        rowCount++;
                        totalLoanAmount += loanAmount;

                        //System.out.println("Tract to MSAMD Income: " + rs.getDouble("tract_to_msamd_income") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                    } while(rs.next());
                }
            }
            //Display the number of rows and the sum of loan amounts
            System.out.println("Number of rows matching the filters: " + rowCount);
            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
        } catch (SQLException e) {
            // Handle SQL exceptions and provide error messages
            System.err.println("Error executing Tract to MSAMD Income filter query: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        return results;
    }

    public List<Map<String, Object>> filterByLoanPurpose(String loanPurposeName) {
        //Split the input string into separate loan purpose names
        List<String> loanPurposeArray = Arrays.asList(loanPurposeName.split("\\s*,\\s*"));

        //Check if loan purpose list is empty or null
        if(loanPurposeName == null || loanPurposeName.isEmpty()) {
            System.out.println("No loan purpose(s) provided for filtering.");
            return null; 
        }

        // Print the selected loan purpose(s)
        if (!loanPurposeArray.isEmpty()) {
            System.out.println("The loan purpose(s) that you selected: " + String.join(", ", loanPurposeArray));
        } else {
            System.out.println("No loan purpose(s) selected.");
        }

        //Build SQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM project_2_view p2v JOIN loan_purpose_table lpt ON p2v.loan_purpose = lpt.loan_purpose WHERE lpt.loan_purpose_name IN (");

        //Append the correct number of question marks based on the loan purpose list size
        for(int i = 0; i < loanPurposeArray.size(); i++) {
            queryBuilder.append("?");
            if(i < loanPurposeArray.size() - 1) {
                queryBuilder.append(", "); //Add comma except for the last parameter
            }
        } queryBuilder.append(");");

        List<Map<String, Object>> results = new ArrayList<>();
        int rowCount = 0; // Initialize row count
        int totalLoanAmount = 0; // Initialize total loan amount

        //Convert SQL query to a String
        String loanPurposeFilterQuery = queryBuilder.toString();

        try(PreparedStatement stmt = conn.prepareStatement(loanPurposeFilterQuery)) {
            //Set each loan purpose parameter in the PreparedStatement
            for(int i = 0; i < loanPurposeArray.size(); i++) {
                stmt.setString(i + 1, loanPurposeArray.get(i)); //Set each loan purpose in the prepared statement
            }

            //Execute the query
            try(ResultSet rs = stmt.executeQuery()) {
                //If no results are found
                if(!rs.next()) {
                    System.out.println("No results found for the given loan purpose(s).");
                } else {
                    //System.out.println("Loan Purpose Filter Results:");
                    //Process the result set
                    do {

                        // Process the result set and add to the list
                        Map<String, Object> row = new HashMap<>();
                        int loanAmount = rs.getInt("loan_amount_000s");

                        row.put("applicant_id", rs.getInt("applicant_id"));
                        row.put("action_taken", rs.getInt("action_taken"));
                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                        row.put("msamd", rs.getString("msamd"));
                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                        row.put("loan_amount_000s", loanAmount);
                        row.put("county_code", rs.getString("county_code"));
                        row.put("loan_type", rs.getInt("loan_type"));
                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                        row.put("property_type", rs.getInt("property_type"));
                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                        row.put("lien_status", rs.getInt("lien_status"));
                        row.put("rate_spread", rs.getDouble("rate_spread"));

                        // Add the row to the results list
                        results.add(row);

                        //Update row count and total loan amount
                        rowCount++;
                        totalLoanAmount += loanAmount;

                        //Output the results
                        //System.out.println("Loan Purpose Name: " + rs.getString("loan_purpose_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                    } while(rs.next()); //Continue until all rows are processed
                }
            }
            //Display the number of rows and the sum of loan amounts
            System.out.println("Number of rows matching the filters: " + rowCount);
            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
        } catch (SQLException e) {
            //Handle SQL exceptions and provide error messages
            System.err.println("Error executing Loan Purpose filter query: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        getMinNum(minNum);
        getMaxNum(maxNum);
        return results;
    }

    public List<Map<String, Object>> filterByPropertyType(String propertyTypeName) {
        //Split the input string into separate property type names
        List<String> propertyTypeArray = Arrays.asList(propertyTypeName.split("\\s*,\\s*"));

        //Check if property type list is empty or null
        if(propertyTypeName == null || propertyTypeName.isEmpty()) {
            System.out.println("No property type(s) provided for filtering.");
            return null; 
        }

        // Print the selected property type(s)
        if (!propertyTypeArray.isEmpty()) {
            System.out.println("The property type(s) that you selected: " + String.join(", ", propertyTypeArray));
        } else {
            System.out.println("No property type(s) selected.");
        }

        //Build SQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM project_2_view p2v JOIN property_type_table ptt ON p2v.property_type = ptt.property_type WHERE ptt.property_type_name IN (");

        //Append the correct number of question marks based on the property type list size
        for(int i = 0; i < propertyTypeArray.size(); i++) {
            queryBuilder.append("?");
            if(i < propertyTypeArray.size() - 1) {
                queryBuilder.append(", "); //Add comma except for the last parameter
            }
        } queryBuilder.append(");");

        List<Map<String, Object>> results = new ArrayList<>();
        int rowCount = 0; // Initialize row count
        int totalLoanAmount = 0; // Initialize total loan amount

        //Convert SQL query to a String
        String propertyTypeFilterQuery = queryBuilder.toString();

        try(PreparedStatement stmt = conn.prepareStatement(propertyTypeFilterQuery)) {
            //Set each property tpe parameter in the PreparedStatement
            for(int i = 0; i < propertyTypeArray.size(); i++) {
                stmt.setString(i + 1, propertyTypeArray.get(i)); //Set each property in the prepared statement
            }

            //Execute the query
            try(ResultSet rs = stmt.executeQuery()) {
                //If no results are found
                if(!rs.next()) {
                    System.out.println("No results found for the given property type(s).");
                } else {
                    //System.out.println("Property Type Filter Results:");
                    //Process the result set
                    do {
                        // Process the result set and add to the list
                        Map<String, Object> row = new HashMap<>();
                        int loanAmount = rs.getInt("loan_amount_000s");

                        row.put("applicant_id", rs.getInt("applicant_id"));
                        row.put("action_taken", rs.getInt("action_taken"));
                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                        row.put("msamd", rs.getString("msamd"));
                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                        row.put("loan_amount_000s", loanAmount);
                        row.put("county_code", rs.getString("county_code"));
                        row.put("loan_type", rs.getInt("loan_type"));
                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                        row.put("property_type", rs.getInt("property_type"));
                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                        row.put("lien_status", rs.getInt("lien_status"));
                        row.put("rate_spread", rs.getDouble("rate_spread"));

                        // Add the row to the results list
                        results.add(row);

                        //Update row count and total loan amount
                        rowCount++;
                        totalLoanAmount += loanAmount;

                        //Output the results
                        //System.out.println("Property Type Name: " + rs.getString("property_type_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                    } while(rs.next()); //Continue until all rows are processed
                }
            }
            //Display the number of rows and the sum of loan amounts
            System.out.println("Number of rows matching the filters: " + rowCount);
            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
        } catch (SQLException e) {
            //Handle SQL exceptions and provide error messages
            System.err.println("Error executing Property Type filter query: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        getPropertyTypeName(propertyTypeName);
        return results;
    }

    public List<Map<String, Object>> filterByOwnerOccupancy(String ownerOccupancyName) {
        //Split the input string into separate Owner Occupancy numbers
        List<String> ownerOccupancyArray = Arrays.asList(ownerOccupancyName.split("\\s*,\\s*"));

        //Check if owner occupancy list is empty or null
        if(ownerOccupancyName == null || ownerOccupancyName.isEmpty()) {
            System.out.println("No county name(s) provided for filtering.");
            return null; 
        }

        // Print the selected owner occupancy(ies)
        if (!ownerOccupancyArray.isEmpty()) {
            System.out.println("The owner occupancy(ies) that you selected: " + String.join(", ", ownerOccupancyArray));
        } else {
            System.out.println("No owner occupancy(ies) selected.");
        }

        //Build SQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM project_2_view p2v JOIN owner_occupancy_table oot ON p2v.owner_occupancy = oot.owner_occupancy WHERE oot.owner_occupancy_name IN (");

        //Append the correct number of question marks based on the owner occupancy list size
        for(int i = 0; i < ownerOccupancyArray.size(); i++) {
            queryBuilder.append("?");
            if(i < ownerOccupancyArray.size() - 1) {
                queryBuilder.append(", "); //Add comma except for the last parameter
            }
        } queryBuilder.append(");");

        List<Map<String, Object>> results = new ArrayList<>();
        int rowCount = 0; //Initialize row count
        int totalLoanAmount = 0; //Initialize total loan amount

        //Convert SQL query to a String
        String ownerOccupancyFilterQuery = queryBuilder.toString();

        try(PreparedStatement stmt = conn.prepareStatement(ownerOccupancyFilterQuery)) {
            //Set each owner occupancy parameter in the PreparedStatement
            for(int i = 0; i < ownerOccupancyArray.size(); i++) {
                stmt.setString(i + 1, ownerOccupancyArray.get(i)); //Set each owner occupancy in the prepared statement
            }

            //Execute the query
            try(ResultSet rs = stmt.executeQuery()) {
                //If no results are found
                if(!rs.next()) {
                    System.out.println("No results found for the given owner occupancy(ies).");
                } else {
                    //System.out.println("Owner Occupancy Filter Results:");
                    //Process the result set
                    do {
                        // Process the result set and add to the list
                        Map<String, Object> row = new HashMap<>();
                        int loanAmount = rs.getInt("loan_amount_000s");

                        row.put("applicant_id", rs.getInt("applicant_id"));
                        row.put("action_taken", rs.getInt("action_taken"));
                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                        row.put("msamd", rs.getString("msamd"));
                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                        row.put("loan_amount_000s", loanAmount);
                        row.put("county_code", rs.getString("county_code"));
                        row.put("loan_type", rs.getInt("loan_type"));
                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                        row.put("property_type", rs.getInt("property_type"));
                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                        row.put("lien_status", rs.getInt("lien_status"));
                        row.put("rate_spread", rs.getDouble("rate_spread"));

                        // Add the row to the results list
                        results.add(row);

                        //Update row count and total loan amount
                        rowCount++;
                        totalLoanAmount += loanAmount;

                        // Output the results
                        //System.out.println("Owner Occupancy Name: " + rs.getString("owner_occupancy_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                    } while(rs.next()); //Continue until all rows are processed
                }
            }  
            //Display the number of rows and the sum of loan amounts
            System.out.println("Number of rows matching the filters: " + rowCount);
            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);       
        } catch (SQLException e) {
            //Handle SQL exceptions and provide error messages
            System.err.println("Error executing Owner Occupancy filter query: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        getOwnerOccupancyName(ownerOccupancyName);
        return results;
    }

    public  List<Map<String, Object>> addFilter(List<Map<String, Object>> filteredResults, Set<Integer> uniqueChoiceTracker) {
        Scanner myScanner = new Scanner(System.in);
        List<Map<String, Object>> results = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        int rowCount = 0;
        int totalLoanAmount = 0; 

        System.out.println("List of currently applied filters: " + uniqueChoiceTracker);
        System.out.println("Here are a list of available filters:");
        System.out.println("1: MSAMD"); //Method written by Nathalie Wijaya (error)
        System.out.println("2: Income to Debt Ratio"); //Method written by Nathalie  Wijaya (done)
        System.out.println("3: County"); //Method written by Nathalie Wijaya (error)
        System.out.println("4: Loan Type"); //Method written by Olivia Kamau (done)
        System.out.println("5: Tract to MSAMD Income"); //Method written by Nathalie Wijaya (done)
        System.out.println("6: Loan Purpose"); //Method written by Nathalie Wijaya (done)
        System.out.println("7: Property Type"); //Method written by Nathalie Wijaya (done)
        System.out.println("8: Owner Occupancy"); //Method written by Nathalie Wijaya (done)
        System.out.println("9: Exit");
        System.out.println("Please select an option: ");
        int filterChoice = myScanner.nextInt();

        //Check if filter choice is empty or null
        if(filterChoice <= 0 || filterChoice > 9 ) {
            System.out.println("Invalid input. Please enter a number between 1 and 9.");
            filterChoice = myScanner.nextInt(); 
        } else { //Switch cases for each filter option
            System.out.println("You selected filter number: " + filterChoice + ".");
            //Create temp_result table to store initially filtered results
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "@EdwardWijaya04")) {

                //Create the empty temp table that will store the filtered results
                String createTempTableQuery = "CREATE TEMP TABLE temp_results (applicant_id INTEGER, action_taken INTEGER, purchaser_type INTEGER, msamd TEXT, applicant_income_000s INTEGER, loan_amount_000s INTEGER, county_code TEXT, loan_type INTEGER, tract_to_msamd_income INTEGER, loan_purpose INTEGER, property_type INTEGER, owner_occupancy INTEGER, lien_status INTEGER, rate_spread NUMERIC(10,5))";

                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(createTempTableQuery);
                    System.out.println("Temporary table created.");
                } catch (SQLException e) {
                    System.err.println("Error creating temporary table: " + e.getMessage());
                }

                //Insert values into temp_filtered_results
                String insertQuery = "INSERT INTO temp_results (applicant_id, action_taken, purchaser_type, msamd, applicant_income_000s, loan_amount_000s, county_code, loan_type, tract_to_msamd_income, loan_purpose, property_type, owner_occupancy, lien_status, rate_spread) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    for (Map<String, Object> row : filteredResults) {
                        stmt.setObject(1, (Integer) row.get("applicant_id"));
                        stmt.setObject(2, (Integer) row.get("action_taken"));
                        stmt.setObject(3, (Integer) row.get("purchaser_type"));
                        stmt.setObject(4, (String) row.get("msamd"));
                        stmt.setObject(5, (Integer) row.get("applicant_income_000s"));
                        stmt.setObject(6, (Integer) row.get("loan_amount_000s"));
                        stmt.setObject(7, (String) row.get("county_code"));
                        stmt.setObject(8, (Integer) row.get("loan_type"));
                        stmt.setObject(9, (Integer) row.get("tract_to_msamd_income"));
                        stmt.setObject(10, (Integer) row.get("loan_purpose"));
                        stmt.setObject(11, (Integer) row.get("property_type"));
                        stmt.setObject(12, (Integer) row.get("owner_occupancy"));
                        stmt.setObject(13, (Integer) row.get("lien_status"));
                        stmt.setObject(14, (Double) row.get("rate_spread"));
                        stmt.addBatch();
                    }

                    stmt.executeBatch();
                    System.out.println("Filtered results inserted into temporary table.");
                } catch (SQLException e) {
                    System.err.println("Error inserting filtered results: " + e.getMessage());
                }

                switch(filterChoice) {
                    //Populate the temp table with the results from the first filter option
                    case 1: //MSAMD filter
                        uniqueChoiceTracker.add(1);
                        //Displays the options of MSAMDs available
                        String msamdDisplay = "SELECT *, COALESCE(msamd_name, CAST(msamd AS TEXT)) AS msamd_display FROM msamd_table;";
                        try (PreparedStatement stmt = conn.prepareStatement(msamdDisplay)) {
                            try (ResultSet rs = stmt.executeQuery()) {
                                if (!rs.next()) {
                                    System.out.println("No data found in the table.");
                                } else {
                                    do {
                                        String msamdDisplayValue = rs.getString("msamd_display");
                                        System.out.println("MSAMD Name/Code: " + msamdDisplayValue);
                                    } while (rs.next());
                                }
                            }
                        } catch (SQLException e) {
                            // Handle any SQLException that occurs during the execution of the query
                            System.out.println("Error executing query: " + e.getMessage());
                            e.printStackTrace();
                        }
                        
                        //Get user's option input
                        while(true) {
                            //The MSAMD number that the user will input
                            String msamdInput = " ";

                            System.out.println("Please input MSAMD(s), comma separated if multiple values: ");
                            myScanner.nextLine();
                            msamdInput = myScanner.nextLine();

                            //Split the input string into separate MSAMD values
                            List<String> msamdArray = Arrays.asList(msamdInput.split("\\s*,\\s*"));

                            // Check if MSAMD input is empty or null
                            if (msamdInput == null || msamdInput.isEmpty()) {
                                System.out.println("No MSAMD(s) provided for filtering.");
                                return null; 
                            }
                        
                            //Build SQL query with placeholders
                            queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN msamd_table mt ON tr.msamd = mt.msamd WHERE (mt.msamd_name IN (");
                        
                            for (int i = 0; i < msamdArray.size(); i++) {
                                queryBuilder.append("?");
                                if (i < msamdArray.size() - 1) {
                                    queryBuilder.append(", "); // Add comma except for the last parameter
                                }
                            } queryBuilder.append(") OR CAST(mt.msamd AS TEXT) IN (");
                        
                            for (int i = 0; i < msamdArray.size(); i++) {
                                queryBuilder.append("?");
                                if (i < msamdArray.size() - 1) {
                                    queryBuilder.append(", "); // Add comma except for the last parameter
                                }
                            } queryBuilder.append("));");
                        
                            //List<Map<String, Object>> results = new ArrayList<>();
                            rowCount = 0; // Initialize row count
                            totalLoanAmount = 0; // Initialize total loan amount
                        
                            //Convert SQL query to String
                            String msamdFilterQuery = queryBuilder.toString();
                        
                            try (PreparedStatement stmt = conn.prepareStatement(msamdFilterQuery)) {
                                // Set the values for msamd_name placeholders
                                for (int i = 0; i < msamdArray.size(); i++) {
                                    stmt.setString(i + 1, msamdArray.get(i)); // For msamd_name
                                }
                                // Set the values for msamd code placeholders
                                for (int i = 0; i < msamdArray.size(); i++) {
                                    stmt.setString(msamdArray.size() + i + 1, msamdArray.get(i)); // For msamd code
                                }
                        
                                //Execute the query and get the results
                                try (ResultSet rs = stmt.executeQuery()) {
                                    if (!rs.next()) {
                                        System.out.println("No results found for the selected MSAMD.");
                                    } else {
                                        // Process the result set and add to the list
                                        do {
                                            Map<String, Object> row = new HashMap<>();
                                            int loanAmount = rs.getInt("loan_amount_000s");
                                            
                                            row.put("applicant_id", rs.getInt("applicant_id"));
                                            row.put("action_taken", rs.getInt("action_taken"));
                                            row.put("purchaser_type", rs.getInt("purchaser_type"));
                                            row.put("msamd", rs.getString("msamd"));
                                            row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                            row.put("loan_amount_000s", loanAmount);
                                            row.put("county_code", rs.getString("county_code"));
                                            row.put("loan_type", rs.getInt("loan_type"));
                                            row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                            row.put("loan_purpose", rs.getInt("loan_purpose"));
                                            row.put("property_type", rs.getInt("property_type"));
                                            row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                            row.put("lien_status", rs.getInt("lien_status"));
                                            row.put("rate_spread", rs.getDouble("rate_spread"));
                        
                                            // Add the row to the results list
                                            results.add(row);

                                            //Update row count and total loan amount
                                            rowCount++;
                                            totalLoanAmount += loanAmount;
                        
                                            //System.out.println("MSAMD Name: " + rs.getString("msamd_name") +", Lien Status: " + rs.getInt("lien_status") + ", Rate Spread: " + rs.getDouble("rate_spread"));
                                        } while (rs.next());
                                    }
                                }
                                //Display the number of rows and the sum of loan amounts
                                System.out.println("Number of rows matching the filters: " + rowCount);
                                System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                            } catch (SQLException e) {
                                // Handle SQL exceptions and provide error messages
                                System.err.println("Error executing MSAMD filter query: " + e.getMessage());
                                e.printStackTrace();
                                return new ArrayList<>();
                            }                           
                            break;
                        }
                        break;
                    case 2: //Income to debt ratio filter
                        uniqueChoiceTracker.add(2);
                        System.out.println("Please enter the minimum income-to-debt ratio desired, enter 0 if none: ");
                        Double minRatio = myScanner.nextDouble();
                        System.out.println("Please enter the maximum income-to-debt ratio desired, enter 0 if none: ");
                        Double maxRatio = myScanner.nextDouble();
                        
                        //Ensure minRatio and maxRatio are valid before building the query
                        if (minRatio <= 0 && maxRatio <= 0) {
                            System.out.println("No income-to-debt ratio range provided for filtering.");
                            return new ArrayList<>(); // Return empty list if no valid ratio provided
                        }

                        //Build SQL query
                        queryBuilder = new StringBuilder("SELECT *, COALESCE((applicant_income_000s / NULLIF(loan_amount_000s, 0)), 0) FROM temp_results WHERE ");

                        //SQL query placeholder
                        int paramIndex = 1;

                        if ((minRatio != null && minRatio > 0) && (maxRatio != null && maxRatio > 0)) { 
                            queryBuilder.append("(CAST(applicant_income_000s AS NUMERIC) / loan_amount_000s) >= ? AND (applicant_income_000s / loan_amount_000s) <= ?");
                        } else if (minRatio != null && minRatio > 0) { 
                            queryBuilder.append("(CAST(applicant_income_000s AS NUMERIC) / loan_amount_000s) >= ?");
                        } else if (maxRatio != null && maxRatio > 0) { 
                            queryBuilder.append("(CAST(applicant_income_000s AS NUMERIC) / loan_amount_000s) <= ?");
                        } else { 
                            // Fallback in case no valid ratios are provided
                            System.out.println("No valid income-to-debt ratio provided for filtering.");
                            return new ArrayList<>();
                        }

                        //List<Map<String, Object>> results = new ArrayList<>();
                        rowCount = 0; // Initialize row count
                        totalLoanAmount = 0; // Initialize total loan amount

                        //Converts SQL query to String
                        String incomeToDebtRatioFilter = queryBuilder.toString();

                        try(PreparedStatement stmt = conn.prepareStatement(incomeToDebtRatioFilter)) {
                            //Set the min ratio value with placeholders (if provided)
                            if(minRatio != null) {
                                stmt.setDouble(paramIndex++, minRatio);
                            }

                            //Set the max ratio with placeholders (if provided)
                            if(maxRatio != null) {
                                stmt.setDouble(paramIndex++, maxRatio);
                            }

                            //Execute the query
                            try(ResultSet rs = stmt.executeQuery()) {
                                if(!rs.next()) {
                                    System.out.println("No results found for the given income-to-debt ratio.");
                                } else {
                                    //System.out.println("Income-to-Debt Ratio Results:");
                                    do {
                                        //Fetch values from the result set
                                        int loanAmount = rs.getInt("loan_amount_000s");

                                        //Create a row map
                                        Map<String, Object> row = new HashMap<>();
                                        row.put("applicant_id", rs.getInt("applicant_id"));
                                        row.put("action_taken", rs.getInt("action_taken"));
                                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                                        row.put("msamd", rs.getString("msamd"));
                                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                        row.put("loan_amount_000s", loanAmount);
                                        row.put("county_code", rs.getString("county_code"));
                                        row.put("loan_type", rs.getInt("loan_type"));
                                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                                        row.put("property_type", rs.getInt("property_type"));
                                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                        row.put("lien_status", rs.getInt("lien_status"));
                                        row.put("rate_spread", rs.getDouble("rate_spread"));

                                        //Add the row to the results list
                                        results.add(row);

                                        //Update row count and total loan amount
                                        rowCount++;
                                        totalLoanAmount += loanAmount;

                                        //System.out.println("Applicant Income: " + applicantIncome + ", Loan Amount: " + loanAmount + ", Ratio: " + calculatedRatio + ", Lien Status: " + lienStatus + ", Rate Spread: " + rateSpread);
                                    } while(rs.next());
                                }
                            }
                            //Display the number of rows and the sum of loan amounts
                            System.out.println("Number of rows matching the filters: " + rowCount);
                            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                        } catch (SQLException e) {
                            //Handle SQL exceptions and provide error messages
                            System.err.println("Error executing Income to Debt Ratio filter query: " + e.getMessage());
                            e.printStackTrace();
                            return new ArrayList<>();
                        }
                        break;
                    case 3: //County filter
                        uniqueChoiceTracker.add(3);
                        //The county names that the user will select
                        String countyInput;

                        //Displays the options of counties available
                        String countyDisplay = "SELECT * FROM county_table;";
                        try(PreparedStatement stmt = conn.prepareStatement(countyDisplay)) {
                            try(ResultSet rs = stmt.executeQuery()) {
                                if(!rs.next()) {
                                    System.out.println("No data found in the table.");
                                } else {
                                    //System.out.println("County Code\tCounty Name");
                                    do{
                                        //String county = rs.getString("county_code");
                                        String countyName = rs.getString("county_name");

                                        System.out.println(/*county + "\t\t" +*/ "County Name: " + countyName);
                                    } while(rs.next());
                                }	
                            }
                        } catch(SQLException e) {
                            System.out.println("Error retrieving county names.");
                        }

                        //Ask user to input a valid county name
                        while(true) {
                            System.out.println("Please input county names(s), comma separated if multiple values: ");
                            myScanner.nextLine();
                            countyInput = myScanner.nextLine();
                            //filteredResults = myFilter.filterByCounty(countyInput);
                            //myMortgage.calculateRate(filteredResults);
                            break;
                        }

                        //Filtering step:

                        //Split the input string into separate county names
                        List<String> countyArray = Arrays.asList(countyInput.split("\\s*,\\s*"));

                        //Check if counties list is empty or null
                        if(countyInput == null || countyInput.isEmpty()) {
                            System.out.println("No county name(s) provided for filtering.");
                            return null; 
                        }

                        // Print the selected county name(s)
                        if (!countyArray.isEmpty()) {
                            System.out.println("The county name(s) that you selected: " + String.join(", ", countyArray));
                        } else {
                            System.out.println("No county name(s) selected.");
                        }

                        //Build SQL query
                        queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN county_table ct ON tr.county_code = ct.county_code WHERE ct.county_name IN (");

                        //Append the correct number of question marks based on the county names list size
                        for(int i = 0; i < countyArray.size(); i++) {
                            queryBuilder.append("?");
                            if(i < countyArray.size() - 1) {
                                queryBuilder.append(", "); //Add comma except for the last parameter
                            }
                        } queryBuilder.append(");");

                        //List<Map<String, Object>> results = new ArrayList<>();
                        rowCount = 0; // Initialize row count
                        totalLoanAmount = 0; // Initialize total loan amount

                        //Convert SQL query to a String
                        String countyNamesFilterQuery = queryBuilder.toString();

                        try(PreparedStatement stmt = conn.prepareStatement(countyNamesFilterQuery)) {
                            //Set each county parameter in the PreparedStatement
                            for(int i = 0; i < countyArray.size(); i++) {
                                stmt.setString(i + 1, countyArray.get(i)); // Set each county in the prepared statement
                            }

                            //Execute the query
                            try(ResultSet rs = stmt.executeQuery()) {
                                //If no results are found
                                if(!rs.next()) {
                                    System.out.println("No results found for the given county name(s).");
                                } else {
                                    //System.out.println("County Filter Results:");
                                    //Process the result set
                                    do {
                                        // Process the result set and add to the list
                                        Map<String, Object> row = new HashMap<>();
                                        int loanAmount = rs.getInt("loan_amount_000s");

                                        row.put("applicant_id", rs.getInt("applicant_id"));
                                        row.put("action_taken", rs.getInt("action_taken"));
                                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                                        row.put("msamd", rs.getString("msamd"));
                                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                        row.put("loan_amount_000s", loanAmount);
                                        row.put("county_code", rs.getString("county_code"));
                                        row.put("loan_type", rs.getInt("loan_type"));
                                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                                        row.put("property_type", rs.getInt("property_type"));
                                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                        row.put("lien_status", rs.getInt("lien_status"));
                                        row.put("rate_spread", rs.getDouble("rate_spread"));

                                        // Add the row to the results list
                                        results.add(row);

                                        //Update row count and total loan amount
                                        rowCount++;
                                        totalLoanAmount += loanAmount;

                                        //Output the results
                                        //System.out.println("County Name: " + rs.getString("county_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                    } while(rs.next()); // Continue until all rows are processed
                                }
                            }
                            //Display the number of rows and the sum of loan amounts
                            System.out.println("Number of rows matching the filters: " + rowCount);
                            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                        } catch (SQLException e) {
                            //Handle SQL exceptions and provide error messages
                            System.err.println("Error executing County filter query: " + e.getMessage());
                            e.printStackTrace();
                            return new ArrayList<>();
                        }
                        break;
                    case 4: //Loan Type filter
                        uniqueChoiceTracker.add(4);
                        //The loan type numbers that the user will select
                        String loanTypeInput;

                        //Displays the options of loan purposes available
                        String loanTypeDisplay = "SELECT * FROM loan_type_table;";
                        try(PreparedStatement stmt = conn.prepareStatement(loanTypeDisplay)) {
                            try(ResultSet rs = stmt.executeQuery()) {
                                if(!rs.next()) {
                                    System.out.println("No data found in the table.");
                                } else {
                                    //System.out.println("Loan Type");
                                    do{
                                        //String loanType = rs.getString("loan_type");
                                        String loanTypeName = rs.getString("loan_type_name");

                                        System.out.println(/*loanType + "\t\t" +*/ "Loan Type: " + loanTypeName);
                                    } while(rs.next());
                                }	
                            }
                        } catch(SQLException e) {
                            System.out.println("Error retrieving loan types.");
                        }

                        //Ask user to input a valid loan type number
                        while(true) {
                            System.out.println("Please input loan type(s), comma separated if multiple values: ");
                            myScanner.nextLine();
                            loanTypeInput = myScanner.nextLine();
                            //filteredResults = myFilter.filterByLoanType(loanTypeInput);
                            //myMortgage.calculateRate(filteredResults);
                            break;
                        }


                        //Split the input string into separate loan type values
                        List<String> loanTypeArray = Arrays.asList(loanTypeInput.split("\\s*,\\s*"));

                        //Check if loan type list is empty or null
                        if(loanTypeInput == null || loanTypeInput.isEmpty()) {
                            System.out.println("No loan type(s) provided for filtering.");
                            return null; 
                        }

                        // Print the selected loan type(s)
                        if (!loanTypeArray.isEmpty()) {
                            System.out.println("The loan type(s) that you selected: " + String.join(", ", loanTypeArray));
                        } else {
                            System.out.println("No loan type(s) selected.");
                        }

                        //Build SQL query
                        queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN loan_type_table ltt ON tr.loan_type = ltt.loan_type WHERE ltt.loan_type_name IN (");

                        //Append the correct number of question marks based on the loan type list size
                        for(int i = 0; i < loanTypeArray.size(); i++) {
                            queryBuilder.append("?");
                            if(i < loanTypeArray.size() - 1) {
                                queryBuilder.append(", "); //Add comma except for the last parameter
                            }
                        } queryBuilder.append(");");

                        //List<Map<String, Object>> results = new ArrayList<>();
                        rowCount = 0; // Initialize row count
                        totalLoanAmount = 0; // Initialize total loan amount

                        //Converts SQL query to String
                        String loanTypeFilterQuery = queryBuilder.toString();

                        try (PreparedStatement stmt = conn.prepareStatement(loanTypeFilterQuery)) {
                            //Set placeholder values
                            for (int i = 0; i < loanTypeArray.size(); i++) {
                                stmt.setString(i + 1, loanTypeArray.get(i));
                            }

                            //Execute query 
                            try (ResultSet rs = stmt.executeQuery()) {
                                if (!rs.next()) {
                                    System.out.println("No results found for the selected loan type.");
                                } else {
                                    //System.out.println("Loan Type Results:");
                                    do {
                                        // Process the result set and add to the list
                                        Map<String, Object> row = new HashMap<>();
                                        int loanAmount = rs.getInt("loan_amount_000s");

                                        row.put("applicant_id", rs.getInt("applicant_id"));
                                        row.put("action_taken", rs.getInt("action_taken"));
                                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                                        row.put("msamd", rs.getString("msamd"));
                                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                        row.put("loan_amount_000s", loanAmount);
                                        row.put("county_code", rs.getString("county_code"));
                                        row.put("loan_type", rs.getInt("loan_type"));
                                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                                        row.put("property_type", rs.getInt("property_type"));
                                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                        row.put("lien_status", rs.getInt("lien_status"));
                                        row.put("rate_spread", rs.getDouble("rate_spread"));

                                        // Add the row to the results list
                                        results.add(row);

                                        //Update row count and total loan amount
                                        rowCount++;
                                        totalLoanAmount += loanAmount;

                                        //System.out.println("Loan Type Name: " + rs.getString("loan_type_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                    } while (rs.next());
                                }
                            }
                            //Display the number of rows and the sum of loan amounts
                            System.out.println("Number of rows matching the filters: " + rowCount);
                            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                        } catch (SQLException e) {
                            //Handle SQL exceptions and provide error messages
                            System.err.println("Error executing Loan Type filter query: " + e.getMessage());
                            e.printStackTrace();
                            return new ArrayList<>();
                        }
                        break;
                    case 5: //Tract to MSAMD Income filter
                        uniqueChoiceTracker.add(5);
                        System.out.println("Please enter the minimum tract-to-msamd income desired, enter 0 if none: ");
                        Double minNum = myScanner.nextDouble();
                        System.out.println("Please enter the maximum tract-to-msamd income desired, enter 0 if none: ");
                        Double maxNum = myScanner.nextDouble();
                        //filteredResults = myFilter.filterByTractToMsamdIncome(minNum, maxNum);

                        //Build SQL query
                        queryBuilder = new StringBuilder("SELECT * FROM temp_results WHERE ");

                        //SQL query placeholder
                        int paramIndex2 = 1;

                        if(minNum != null && maxNum != null) { //Both min and max numericals are provided
                            queryBuilder.append("tract_to_msamd_income >= ? AND tract_to_msamd_income <= ?");
                        } else if(minNum != null && maxNum == null) { //Only min numerical is provided
                            queryBuilder.append("tract_to_msamd_income >= ?");
                        } else if(minNum != 0 && maxNum != null) { //Only max numerical is provided
                            queryBuilder.append("tract_to_msamd_income <= ?");
                        } queryBuilder.append(";");

                        //List<Map<String, Object>> results = new ArrayList<>(); 
                        rowCount = 0; // Initialize row count
                        totalLoanAmount = 0; // Initialize total loan amount

                        //Converts SQL query to String
                        String tractToMsamdIncomeFilter = queryBuilder.toString();

                        try(PreparedStatement stmt = conn.prepareStatement(tractToMsamdIncomeFilter)) {
                            //Set the min numerical value with placeholders (if provided)
                            if(minNum != null) {
                                stmt.setDouble(paramIndex2++, minNum);
                            }

                            //Set the max numerical value with placeholders (if provided)
                            if(maxNum != null) {
                                stmt.setDouble(paramIndex2++, maxNum);
                            }

                            //Execute the query
                            try(ResultSet rs = stmt.executeQuery()) {
                                if(!rs.next()) {
                                    System.out.println("No results found for the given tract-to-msamd income.");
                                } else {
                                    //System.out.println("Tract-to-Msamd Income Results:");
                                    do {
                                        // Process the result set and add to the list
                                        Map<String, Object> row = new HashMap<>();
                                        int loanAmount = rs.getInt("loan_amount_000s");

                                        row.put("applicant_id", rs.getInt("applicant_id"));
                                        row.put("action_taken", rs.getInt("action_taken"));
                                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                                        row.put("msamd", rs.getString("msamd"));
                                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                        row.put("loan_amount_000s", loanAmount);
                                        row.put("county_code", rs.getString("county_code"));
                                        row.put("loan_type", rs.getInt("loan_type"));
                                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                                        row.put("property_type", rs.getInt("property_type"));
                                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                        row.put("lien_status", rs.getInt("lien_status"));
                                        row.put("rate_spread", rs.getDouble("rate_spread"));

                                        // Add the row to the results list
                                        results.add(row);

                                        //Update row count and total loan amount
                                        rowCount++;
                                        totalLoanAmount += loanAmount;

                                        //System.out.println("Tract to MSAMD Income: " + rs.getDouble("tract_to_msamd_income") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                    } while(rs.next());
                                }
                            }
                            //Display the number of rows and the sum of loan amounts
                            System.out.println("Number of rows matching the filters: " + rowCount);
                            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                        } catch (SQLException e) {
                            // Handle SQL exceptions and provide error messages
                            System.err.println("Error executing Tract to MSAMD Income filter query: " + e.getMessage());
                            e.printStackTrace();
                            return new ArrayList<>();
                        }
                        break;
                    case 6: // Loan Purpose filter
                        uniqueChoiceTracker.add(6);
                        //The loan purpose numbers that the user will select
                        String loanPurposeInput;

                        //Displays the options of loan purposes available
                        String loanPurposeDisplay = "SELECT * FROM loan_purpose_table;";
                        try(PreparedStatement stmt = conn.prepareStatement(loanPurposeDisplay)) {
                            try(ResultSet rs = stmt.executeQuery()) {
                                if(!rs.next()) {
                                    System.out.println("No data found in the table.");
                                } else {
                                    //System.out.println("Loan Purpose");
                                    do{
                                        //String loanPurpose = rs.getString("loan_purpose");
                                        String loanPurposeName = rs.getString("loan_purpose_name");

                                        System.out.println(/*loanPurpose + "\t\t" +*/ "Loan Purpose: " + loanPurposeName);
                                    } while(rs.next());
                                }	
                            } 
                        } catch(SQLException e) {
                            System.out.println("Error retrieving loan purpose names.");

                        }

                        //Ask user to input a valid loan purpose number
                        while(true) {
                            System.out.println("Please input loan purpose(s), comma separated if multiple values: ");
                            myScanner.nextLine();
                            loanPurposeInput = myScanner.nextLine();
                            //filteredResults = myFilter.filterByLoanPurpose(loanPurposeInput);
                            //myMortgage.calculateRate(filteredResults);
                            break;
                        }

                        //Split the input string into separate loan purpose names
                        List<String> loanPurposeArray = Arrays.asList(loanPurposeInput.split("\\s*,\\s*"));

                        //Check if loan purpose list is empty or null
                        if(loanPurposeInput == null || loanPurposeInput.isEmpty()) {
                            System.out.println("No loan purpose(s) provided for filtering.");
                            return null; 
                        }

                        // Print the selected loan purpose(s)
                        if (!loanPurposeArray.isEmpty()) {
                            System.out.println("The loan purpose(s) that you selected: " + String.join(", ", loanPurposeArray));
                        } else {
                            System.out.println("No loan purpose(s) selected.");
                        }

                        //Build SQL query
                        queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN loan_purpose_table lpt ON tr.loan_purpose = lpt.loan_purpose WHERE lpt.loan_purpose_name IN (");

                        //Append the correct number of question marks based on the loan purpose list size
                        for(int i = 0; i < loanPurposeArray.size(); i++) {
                            queryBuilder.append("?");
                            if(i < loanPurposeArray.size() - 1) {
                                queryBuilder.append(", "); //Add comma except for the last parameter
                            }
                        } queryBuilder.append(");");

                        //List<Map<String, Object>> results = new ArrayList<>();
                        rowCount = 0; // Initialize row count
                        totalLoanAmount = 0; // Initialize total loan amount

                        //Convert SQL query to a String
                        String loanPurposeFilterQuery = queryBuilder.toString();

                        try(PreparedStatement stmt = conn.prepareStatement(loanPurposeFilterQuery)) {
                            //Set each loan purpose parameter in the PreparedStatement
                            for(int i = 0; i < loanPurposeArray.size(); i++) {
                                stmt.setString(i + 1, loanPurposeArray.get(i)); //Set each loan purpose in the prepared statement
                            }

                            //Execute the query
                            try(ResultSet rs = stmt.executeQuery()) {
                                //If no results are found
                                if(!rs.next()) {
                                    System.out.println("No results found for the given loan purpose(s).");
                                } else {
                                    //System.out.println("Loan Purpose Filter Results:");
                                    //Process the result set
                                    do {

                                        // Process the result set and add to the list
                                        Map<String, Object> row = new HashMap<>();
                                        int loanAmount = rs.getInt("loan_amount_000s");

                                        row.put("applicant_id", rs.getInt("applicant_id"));
                                        row.put("action_taken", rs.getInt("action_taken"));
                                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                                        row.put("msamd", rs.getString("msamd"));
                                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                        row.put("loan_amount_000s", loanAmount);
                                        row.put("county_code", rs.getString("county_code"));
                                        row.put("loan_type", rs.getInt("loan_type"));
                                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                                        row.put("property_type", rs.getInt("property_type"));
                                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                        row.put("lien_status", rs.getInt("lien_status"));
                                        row.put("rate_spread", rs.getDouble("rate_spread"));

                                        // Add the row to the results list
                                        results.add(row);

                                        //Update row count and total loan amount
                                        rowCount++;
                                        totalLoanAmount += loanAmount;

                                        //Output the results
                                        //System.out.println("Loan Purpose Name: " + rs.getString("loan_purpose_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                    } while(rs.next()); //Continue until all rows are processed
                                }
                            }
                            //Display the number of rows and the sum of loan amounts
                            System.out.println("Number of rows matching the filters: " + rowCount);
                            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                        } catch (SQLException e) {
                            //Handle SQL exceptions and provide error messages
                            System.err.println("Error executing Loan Purpose filter query: " + e.getMessage());
                            e.printStackTrace();
                            return new ArrayList<>();
                        }
                        break;
                    case 7: //Property Type filter
                        uniqueChoiceTracker.add(7);
                        //The property type numbers that the user will select
                        String propertyTypeInput;

                        //Displays the options of property types available
                        String propertyTypeDisplay = "SELECT * FROM property_type_table;";
                        try(PreparedStatement stmt = conn.prepareStatement(propertyTypeDisplay)) {
                            try(ResultSet rs = stmt.executeQuery()) {
                                if(!rs.next()) {
                                    System.out.println("No data found in the table.");
                                } else {
                                    //System.out.println("Property Type");
                                    do{
                                        //String propertyType = rs.getString("property_type");
                                        String propertyTypeName = rs.getString("property_type_name");

                                        System.out.println(/*propertyType + "\t\t" +*/ "Property Type: " + propertyTypeName);
                                    } while(rs.next());
                                }	
                            }
                        } catch(SQLException e) {
                            System.out.println("Error retrieving property type names.");
                        }

                        //Ask user to input a valid property type number
                        while(true) {
                            System.out.println("Please input property type(s), comma separated if multiple values: ");
                            myScanner.nextLine();
                            propertyTypeInput = myScanner.nextLine();
                            //filteredResults = myFilter.filterByPropertyType(propertyTypeInput);
                            //myMortgage.calculateRate(filteredResults);
                            break;
                        }


                        //Split the input string into separate property type names
                        List<String> propertyTypeArray = Arrays.asList(propertyTypeInput.split("\\s*,\\s*"));

                        //Check if property type list is empty or null
                        if(propertyTypeInput == null || propertyTypeInput.isEmpty()) {
                            System.out.println("No property type(s) provided for filtering.");
                            return null; 
                        }

                        // Print the selected property type(s)
                        if (!propertyTypeArray.isEmpty()) {
                            System.out.println("The property type(s) that you selected: " + String.join(", ", propertyTypeArray));
                        } else {
                            System.out.println("No property type(s) selected.");
                        }

                        //Build SQL query
                        queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN property_type_table ptt ON tr.property_type = ptt.property_type WHERE ptt.property_type_name IN (");

                        //Append the correct number of question marks based on the property type list size
                        for(int i = 0; i < propertyTypeArray.size(); i++) {
                            queryBuilder.append("?");
                            if(i < propertyTypeArray.size() - 1) {
                                queryBuilder.append(", "); //Add comma except for the last parameter
                            }
                        } queryBuilder.append(");");

                        //List<Map<String, Object>> results = new ArrayList<>();
                        rowCount = 0; // Initialize row count
                        totalLoanAmount = 0; // Initialize total loan amount

                        //Convert SQL query to a String
                        String propertyTypeFilterQuery = queryBuilder.toString();

                        try(PreparedStatement stmt = conn.prepareStatement(propertyTypeFilterQuery)) {
                            //Set each property tpe parameter in the PreparedStatement
                            for(int i = 0; i < propertyTypeArray.size(); i++) {
                                stmt.setString(i + 1, propertyTypeArray.get(i)); //Set each property in the prepared statement
                            }

                            //Execute the query
                            try(ResultSet rs = stmt.executeQuery()) {
                                //If no results are found
                                if(!rs.next()) {
                                    System.out.println("No results found for the given property type(s).");
                                } else {
                                    //System.out.println("Property Type Filter Results:");
                                    //Process the result set
                                    do {
                                        // Process the result set and add to the list
                                        Map<String, Object> row = new HashMap<>();
                                        int loanAmount = rs.getInt("loan_amount_000s");

                                        row.put("applicant_id", rs.getInt("applicant_id"));
                                        row.put("action_taken", rs.getInt("action_taken"));
                                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                                        row.put("msamd", rs.getString("msamd"));
                                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                        row.put("loan_amount_000s", loanAmount);
                                        row.put("county_code", rs.getString("county_code"));
                                        row.put("loan_type", rs.getInt("loan_type"));
                                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                                        row.put("property_type", rs.getInt("property_type"));
                                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                        row.put("lien_status", rs.getInt("lien_status"));
                                        row.put("rate_spread", rs.getDouble("rate_spread"));

                                        // Add the row to the results list
                                        results.add(row);

                                        //Update row count and total loan amount
                                        rowCount++;
                                        totalLoanAmount += loanAmount;

                                        //Output the results
                                        //System.out.println("Property Type Name: " + rs.getString("property_type_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                    } while(rs.next()); //Continue until all rows are processed
                                }
                            }
                            //Display the number of rows and the sum of loan amounts
                            System.out.println("Number of rows matching the filters: " + rowCount);
                            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                        } catch (SQLException e) {
                            //Handle SQL exceptions and provide error messages
                            System.err.println("Error executing Property Type filter query: " + e.getMessage());
                            e.printStackTrace();
                            return new ArrayList<>();
                        }
                        break;
                    case 8: //Owner Occupancy filter
                        uniqueChoiceTracker.add(8);
                        //The Owner Occupancy numbers that the user will select
                        String ownerOccupancyInput;

                        //Displays the options of owner occupancies available
                        String ownerOccupancyDisplay = "SELECT * FROM owner_occupancy_table;";
                        try(PreparedStatement stmt = conn.prepareStatement(ownerOccupancyDisplay)) {
                            try(ResultSet rs = stmt.executeQuery()) {
                                if(!rs.next()) {
                                    System.out.println("No data found in the table.");
                                } else {
                                    //System.out.println("Owner Occupancy");
                                    do{
                                        //String ownerOccupancy = rs.getString("owner_occupancy");
                                        String ownerOccupancyName = rs.getString("owner_occupancy_name");

                                        System.out.println(/*ownerOccupancy + "\t\t" +*/ "Owner Occupancy: " + ownerOccupancyName);
                                    } while(rs.next());
                                }	
                            }
                        } catch(SQLException e) {
                            System.out.println("Error retrieving owner occupany names.");
                        }
                        //Ask user to input a valid owner occupancy number
                        while(true) {
                            System.out.println("Please input owner occupancy(ies), comma separated if multiple values: ");
                            myScanner.nextLine();
                            ownerOccupancyInput = myScanner.nextLine();
                            //filteredResults = myFilter.filterByOwnerOccupancy(ownerOccupancyInput);
                            //myMortgage.calculateRate(filteredResults);
                            break;
                        }

                        //Split the input string into separate Owner Occupancy numbers
                        List<String> ownerOccupancyArray = Arrays.asList(ownerOccupancyInput.split("\\s*,\\s*"));

                        //Check if counties list is empty or null
                        if(ownerOccupancyInput == null || ownerOccupancyInput.isEmpty()) {
                            System.out.println("No county name(s) provided for filtering.");
                            return null; 
                        }

                        // Print the selected owner occupancy(ies)
                        if (!ownerOccupancyArray.isEmpty()) {
                            System.out.println("The owner occupancy(ies) that you selected: " + String.join(", ", ownerOccupancyArray));
                        } else {
                            System.out.println("No owner occupancy(ies) selected.");
                        }

                        //Build SQL query
                        queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN owner_occupancy_table oot ON tr.owner_occupancy = oot.owner_occupancy WHERE oot.owner_occupancy_name IN (");

                        //Append the correct number of question marks based on the owner occupancy list size
                        for(int i = 0; i < ownerOccupancyArray.size(); i++) {
                            queryBuilder.append("?");
                            if(i < ownerOccupancyArray.size() - 1) {
                                queryBuilder.append(", "); //Add comma except for the last parameter
                            }
                        } queryBuilder.append(");");

                        //List<Map<String, Object>> results = new ArrayList<>();
                        rowCount = 0; //Initialize row count
                        totalLoanAmount = 0; //Initialize total loan amount

                        //Convert SQL query to a String
                        String ownerOccupancyFilterQuery = queryBuilder.toString();

                        try(PreparedStatement stmt = conn.prepareStatement(ownerOccupancyFilterQuery)) {
                            //Set each owner occupancy parameter in the PreparedStatement
                            for(int i = 0; i < ownerOccupancyArray.size(); i++) {
                                stmt.setString(i + 1, ownerOccupancyArray.get(i)); //Set each owner occupancy in the prepared statement
                            }

                            //Execute the query
                            try(ResultSet rs = stmt.executeQuery()) {
                                //If no results are found
                                if(!rs.next()) {
                                    System.out.println("No results found for the given owner occupancy(ies).");
                                } else {
                                    //System.out.println("Owner Occupancy Filter Results:");
                                    //Process the result set
                                    do {
                                        // Process the result set and add to the list
                                        Map<String, Object> row = new HashMap<>();
                                        int loanAmount = rs.getInt("loan_amount_000s");

                                        row.put("applicant_id", rs.getInt("applicant_id"));
                                        row.put("action_taken", rs.getInt("action_taken"));
                                        row.put("purchaser_type", rs.getInt("purchaser_type"));
                                        row.put("msamd", rs.getString("msamd"));
                                        row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                        row.put("loan_amount_000s", loanAmount);
                                        row.put("county_code", rs.getString("county_code"));
                                        row.put("loan_type", rs.getInt("loan_type"));
                                        row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                        row.put("loan_purpose", rs.getInt("loan_purpose"));
                                        row.put("property_type", rs.getInt("property_type"));
                                        row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                        row.put("lien_status", rs.getInt("lien_status"));
                                        row.put("rate_spread", rs.getDouble("rate_spread"));

                                        // Add the row to the results list
                                        results.add(row);

                                        //Update row count and total loan amount
                                        rowCount++;
                                        totalLoanAmount += loanAmount;

                                        // Output the results
                                        //System.out.println("Owner Occupancy Name: " + rs.getString("owner_occupancy_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                    } while(rs.next()); //Continue until all rows are processed
                                }
                            }  
                            //Display the number of rows and the sum of loan amounts
                            System.out.println("Number of rows matching the filters: " + rowCount);
                            System.out.println("Total loan amount matching the filters: " + totalLoanAmount);       
                        } catch (SQLException e) {
                            //Handle SQL exceptions and provide error messages
                            System.err.println("Error executing Owner Occupancy filter query: " + e.getMessage());
                            e.printStackTrace();
                            return new ArrayList<>();
                        }
                        break;
                    case 9:
                        System.out.println("Exiting the system.");                       
                        try{ 
                            conn.close();
                        } catch(SQLException e) {
                            System.out.println("Closing the program.");
                            System.exit(0);
                        }
                        break;
                    default:
                            System.out.println("Invalid input. Please enter a number between 1 and 9.");
                }
            } catch(SQLException e) {
                System.out.println("Fail to make connection!" + e.getMessage());
            }                  
        }
        getUniqueChoiceTracker(uniqueChoiceTracker);
        return results;
    } //Method close

    public List<Map<String, Object>> deleteFilter(List<Map<String, Object>> filteredResults, Set<Integer> uniqueChoiceTracker) {
        Scanner myScanner = new Scanner(System.in);
        List<Map<String, Object>> results = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();

        int rowCount = 0;
        int totalLoanAmount = 0; 

        System.out.println("You have chosen to delete a filter.");
        System.out.println("List of currently applied filters: " + uniqueChoiceTracker);
        System.out.println("Please select a filter to delete:");
        System.out.println("1: MSAMD"); //Method written by Nathalie Wijaya (done)
        System.out.println("2: Income to Debt Ratio"); //Method written by Nathalie  Wijaya (done)
        System.out.println("3: County"); //Method written by Nathalie Wijaya (done)
        System.out.println("4: Loan Type"); //Method written by Olivia Kamau (done)
        System.out.println("5: Tract to MSAMD Income"); //Method written by Nathalie Wijaya (done)
        System.out.println("6: Loan Purpose"); //Method written by Nathalie Wijaya (done)
        System.out.println("7: Property Type"); //Method written by Nathalie Wijaya (done)
        System.out.println("8: Owner Occupancy"); //Method written by Nathalie Wijaya (done)
        System.out.println("9: Exit");
        System.out.println("Please select an option: ");
        int filterChoice = myScanner.nextInt();

        //Remove deleted filter choice from hashset
        try {
            if(uniqueChoiceTracker.contains(filterChoice) && filterChoice != 9) { //Checks to make sure that choice enters belongs to the hashset
                uniqueChoiceTracker.remove(filterChoice);
                System.out.println("The filter choice has been removed.");  
            } else if (filterChoice == 9) { // Checks whether user chose to exit program
                System.out.println("You have chosen to exit the program.");
                System.out.println("Exiting program.");
                System.exit(0);
            } else {
                System.out.println("Invalid choice, please re-enter a valid integer choice: ");
                filterChoice = myScanner.nextInt();
            }
        } catch(InputMismatchException e) {
            System.out.println("Invalid input. Please re-enter a valid integer choice: ");
            filterChoice = myScanner.nextInt();
        }

        //Exit delete filter method if the hashset is empty
        if(uniqueChoiceTracker.isEmpty()){
            System.out.println("No filters currently chosen. Exiting program.");
            System.exit(0);
        }

        //Get the first and last elements in the hashset & store them in variables
        Iterator<Integer> iterator = uniqueChoiceTracker.iterator();
            int firstHashSetElement= iterator.next();   //Gets the first element in the hashset
            int lastHashSetElement = 0;
        for (int x : uniqueChoiceTracker) {
            lastHashSetElement = x; //Gets the last element in the hashset
        }

        //Perform filtering operations again after removing the deleted filter option selected
        //Insert the add filters into their respective if statements
        try 
        {
            //1. MSAMD filter 
            if(uniqueChoiceTracker.contains(1)){
                //Runs query from project_2_view if it is the first element in the hashset
                if(firstHashSetElement == 1) {
                    //Call the method from pv2
                    filterByMsamd(setMsamdValue());

                    //Create the temp table for it (to apply filters for other methods)                   
                    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "@EdwardWijaya04")) {

                        //Create the empty temp table that will store the filtered results
                        String createTempTableQuery = "CREATE TEMP TABLE temp_results (applicant_id INTEGER, action_taken INTEGER, purchaser_type INTEGER, msamd TEXT, applicant_income_000s INTEGER, loan_amount_000s INTEGER, county_code TEXT, loan_type INTEGER, tract_to_msamd_income INTEGER, loan_purpose INTEGER, property_type INTEGER, owner_occupancy INTEGER, lien_status INTEGER, rate_spread NUMERIC(10,5))";
        
                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate(createTempTableQuery);
                            System.out.println("Temporary table created.");
                        } catch (SQLException e) {
                            System.err.println("Error creating temporary table: " + e.getMessage());
                        }
        
                        //Insert values into temp_filtered_results
                        String insertQuery = "INSERT INTO temp_results (applicant_id, action_taken, purchaser_type, msamd, applicant_income_000s, loan_amount_000s, county_code, loan_type, tract_to_msamd_income, loan_purpose, property_type, owner_occupancy, lien_status, rate_spread) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
                        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                            for (Map<String, Object> row : filteredResults) {
                                stmt.setObject(1, (Integer) row.get("applicant_id"));
                                stmt.setObject(2, (Integer) row.get("action_taken"));
                                stmt.setObject(3, (Integer) row.get("purchaser_type"));
                                stmt.setObject(4, (String) row.get("msamd"));
                                stmt.setObject(5, (Integer) row.get("applicant_income_000s"));
                                stmt.setObject(6, (Integer) row.get("loan_amount_000s"));
                                stmt.setObject(7, (String) row.get("county_code"));
                                stmt.setObject(8, (Integer) row.get("loan_type"));
                                stmt.setObject(9, (Integer) row.get("tract_to_msamd_income"));
                                stmt.setObject(10, (Integer) row.get("loan_purpose"));
                                stmt.setObject(11, (Integer) row.get("property_type"));
                                stmt.setObject(12, (Integer) row.get("owner_occupancy"));
                                stmt.setObject(13, (Integer) row.get("lien_status"));
                                stmt.setObject(14, (Double) row.get("rate_spread"));
                                stmt.addBatch();
                            }
                            stmt.executeBatch();
                            System.out.println("Filtered results inserted into temporary table.");
                        } catch (SQLException e) {
                            System.err.println("Error inserting filtered results: " + e.getMessage());
                        }
                    } catch (SQLException e) {
                        System.err.println("Error inserting filtered results: " + e.getMessage());
                    }
                } //If-statement for first hashset element ends
                //Runs query from temp_results table
                else {  
                    String msamdInput = setMsamdValue();

                    //Split the input string into separate MSAMD values
                    List<String> msamdArray = Arrays.asList(msamdInput.split("\\s*,\\s*"));

                    //Build SQL query with placeholders
                    queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN msamd_table mt ON tr.msamd = mt.msamd WHERE (mt.msamd_name IN (");
                
                    for (int i = 0; i < msamdArray.size(); i++) {
                        queryBuilder.append("?");
                        if (i < msamdArray.size() - 1) {
                            queryBuilder.append(", "); // Add comma except for the last parameter
                        }
                    } queryBuilder.append(") OR CAST(mt.msamd AS TEXT) IN (");
                
                    for (int i = 0; i < msamdArray.size(); i++) {
                        queryBuilder.append("?");
                        if (i < msamdArray.size() - 1) {
                            queryBuilder.append(", "); // Add comma except for the last parameter
                        }
                    } queryBuilder.append("));");
                
                    //List<Map<String, Object>> results = new ArrayList<>();
                    rowCount = 0; // Initialize row count
                    totalLoanAmount = 0; // Initialize total loan amount
                
                    //Convert SQL query to String
                    String msamdFilterQuery = queryBuilder.toString();
                
                    try (PreparedStatement stmt = conn.prepareStatement(msamdFilterQuery)) {
                        // Set the values for msamd_name placeholders
                        for (int i = 0; i < msamdArray.size(); i++) {
                            stmt.setString(i + 1, msamdArray.get(i)); // For msamd_name
                        }
                        // Set the values for msamd code placeholders
                        for (int i = 0; i < msamdArray.size(); i++) {
                            stmt.setString(msamdArray.size() + i + 1, msamdArray.get(i)); // For msamd code
                        }
                
                        //Execute the query and get the results
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (!rs.next()) {
                                System.out.println("No results found for the selected MSAMD.");
                            } else {
                                // Process the result set and add to the list
                                do {
                                    Map<String, Object> row = new HashMap<>();
                                    int loanAmount = rs.getInt("loan_amount_000s");
                                    
                                    row.put("applicant_id", rs.getInt("applicant_id"));
                                    row.put("action_taken", rs.getInt("action_taken"));
                                    row.put("purchaser_type", rs.getInt("purchaser_type"));
                                    row.put("msamd", rs.getString("msamd"));
                                    row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                    row.put("loan_amount_000s", loanAmount);
                                    row.put("county_code", rs.getString("county_code"));
                                    row.put("loan_type", rs.getInt("loan_type"));
                                    row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                    row.put("loan_purpose", rs.getInt("loan_purpose"));
                                    row.put("property_type", rs.getInt("property_type"));
                                    row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                    row.put("lien_status", rs.getInt("lien_status"));
                                    row.put("rate_spread", rs.getDouble("rate_spread"));
                
                                    // Add the row to the results list
                                    results.add(row);

                                    //Update row count and total loan amount
                                    rowCount++;
                                    totalLoanAmount += loanAmount;
                
                                    //System.out.println("MSAMD Name: " + rs.getString("msamd_name") +", Lien Status: " + rs.getInt("lien_status") + ", Rate Spread: " + rs.getDouble("rate_spread"));
                                } while (rs.next());
                            }
                        }
                        //Display the number of rows and the sum of loan amounts
                        System.out.println("Number of rows matching the filters: " + rowCount);
                        System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                    } catch (SQLException e) {
                        // Handle SQL exceptions and provide error messages
                        System.err.println("Error executing MSAMD filter query: " + e.getMessage());
                        e.printStackTrace();
                        return new ArrayList<>();
                    }                           
                }//Else statement for remaining hashset elements
            } //1. MSAMD delete-if ends
            
            //2. Income to Debt Ratio filter
            if(uniqueChoiceTracker.contains(2)) {

                //Runs query from p2v view if it is the first element in the hashset
                if(firstHashSetElement == 2) {
                    //Call the method from pv2
                    filterByIncomeToDebtRatio(setMinRatio(), setMaxRatio());

                    //Create the temp table for it (to apply filters for other methods)
                    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "@EdwardWijaya04")) {

                        //Create the empty temp table that will store the filtered results
                        String createTempTableQuery = "CREATE TEMP TABLE temp_results (applicant_id INTEGER, action_taken INTEGER, purchaser_type INTEGER, msamd TEXT, applicant_income_000s INTEGER, loan_amount_000s INTEGER, county_code TEXT, loan_type INTEGER, tract_to_msamd_income INTEGER, loan_purpose INTEGER, property_type INTEGER, owner_occupancy INTEGER, lien_status INTEGER, rate_spread NUMERIC(10,5))";
        
                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate(createTempTableQuery);
                            System.out.println("Temporary table created.");
                        } catch (SQLException e) {
                            System.err.println("Error creating temporary table: " + e.getMessage());
                        }
        
                        //Insert values into temp_results
                        String insertQuery = "INSERT INTO temp_results (applicant_id, action_taken, purchaser_type, msamd, applicant_income_000s, loan_amount_000s, county_code, loan_type, tract_to_msamd_income, loan_purpose, property_type, owner_occupancy, lien_status, rate_spread) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
                        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                            for (Map<String, Object> row : filteredResults) {
                                stmt.setObject(1, (Integer) row.get("applicant_id"));
                                stmt.setObject(2, (Integer) row.get("action_taken"));
                                stmt.setObject(3, (Integer) row.get("purchaser_type"));
                                stmt.setObject(4, (String) row.get("msamd"));
                                stmt.setObject(5, (Integer) row.get("applicant_income_000s"));
                                stmt.setObject(6, (Integer) row.get("loan_amount_000s"));
                                stmt.setObject(7, (String) row.get("county_code"));
                                stmt.setObject(8, (Integer) row.get("loan_type"));
                                stmt.setObject(9, (Integer) row.get("tract_to_msamd_income"));
                                stmt.setObject(10, (Integer) row.get("loan_purpose"));
                                stmt.setObject(11, (Integer) row.get("property_type"));
                                stmt.setObject(12, (Integer) row.get("owner_occupancy"));
                                stmt.setObject(13, (Integer) row.get("lien_status"));
                                stmt.setObject(14, (Double) row.get("rate_spread"));
                                stmt.addBatch();
                            }
        
                            stmt.executeBatch();
                            System.out.println("Filtered results inserted into temporary table.");
                        } catch (SQLException e) {
                            System.err.println("Error inserting filtered results: " + e.getMessage());
                        }
                    } catch (SQLException e) {
                        System.err.println("Error inserting filtered results: " + e.getMessage());
                    }
                } //If statement for first hash set element ends
                //Runs query from temp_results table (if it's not the first element in the hashset)
                else {
                    Double minRatio = setMinRatio();
                    Double maxRatio = setMaxRatio();

                    //Build SQL query
                    queryBuilder = new StringBuilder("SELECT *, COALESCE((applicant_income_000s / NULLIF(loan_amount_000s, 0)), 0) FROM temp_results WHERE ");

                    //SQL query placeholder
                    int paramIndex = 1;

                    if ((minRatio != null && minRatio > 0) && (maxRatio != null && maxRatio > 0)) { 
                        queryBuilder.append("(CAST(applicant_income_000s AS NUMERIC) / loan_amount_000s) >= ? AND (applicant_income_000s / loan_amount_000s) <= ?");
                    } else if (minRatio != null && minRatio > 0) { 
                        queryBuilder.append("(CAST(applicant_income_000s AS NUMERIC) / loan_amount_000s) >= ?");
                    } else if (maxRatio != null && maxRatio > 0) { 
                        queryBuilder.append("(CAST(applicant_income_000s AS NUMERIC) / loan_amount_000s) <= ?");
                    } else { 
                        // Fallback in case no valid ratios are provided
                        System.out.println("No valid income-to-debt ratio provided for filtering.");
                        return new ArrayList<>();
                    }

                    //List<Map<String, Object>> results = new ArrayList<>();
                    rowCount = 0; // Initialize row count
                    totalLoanAmount = 0; // Initialize total loan amount

                    //Converts SQL query to String
                    String incomeToDebtRatioFilter = queryBuilder.toString();

                    try(PreparedStatement stmt = conn.prepareStatement(incomeToDebtRatioFilter)) {
                        //Set the min ratio value with placeholders (if provided)
                        if(minRatio != null) {
                            stmt.setDouble(paramIndex++, minRatio);
                        }

                        //Set the max ratio with placeholders (if provided)
                        if(maxRatio != null) {
                            stmt.setDouble(paramIndex++, maxRatio);
                        }

                        //Execute the query
                        try(ResultSet rs = stmt.executeQuery()) {
                            if(!rs.next()) {
                                System.out.println("No results found for the given income-to-debt ratio.");
                            } else {
                                //System.out.println("Income-to-Debt Ratio Results:");
                                do {
                                    //Fetch values from the result set
                                    int loanAmount = rs.getInt("loan_amount_000s");

                                    //Create a row map
                                    Map<String, Object> row = new HashMap<>();
                                    row.put("applicant_id", rs.getInt("applicant_id"));
                                    row.put("action_taken", rs.getInt("action_taken"));
                                    row.put("purchaser_type", rs.getInt("purchaser_type"));
                                    row.put("msamd", rs.getString("msamd"));
                                    row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                    row.put("loan_amount_000s", loanAmount);
                                    row.put("county_code", rs.getString("county_code"));
                                    row.put("loan_type", rs.getInt("loan_type"));
                                    row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                    row.put("loan_purpose", rs.getInt("loan_purpose"));
                                    row.put("property_type", rs.getInt("property_type"));
                                    row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                    row.put("lien_status", rs.getInt("lien_status"));
                                    row.put("rate_spread", rs.getDouble("rate_spread"));

                                    //Add the row to the results list
                                    results.add(row);

                                    //Update row count and total loan amount
                                    rowCount++;
                                    totalLoanAmount += loanAmount;

                                    //System.out.println("Applicant Income: " + applicantIncome + ", Loan Amount: " + loanAmount + ", Ratio: " + calculatedRatio + ", Lien Status: " + lienStatus + ", Rate Spread: " + rateSpread);
                                } while(rs.next());
                            }
                        }
                        //Display the number of rows and the sum of loan amounts
                        System.out.println("Number of rows matching the filters: " + rowCount);
                        System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                    } catch (SQLException e) {
                        //Handle SQL exceptions and provide error messages
                        System.err.println("Error executing Income to Debt Ratio filter query: " + e.getMessage());
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                } //Else statement for remaining hash set elements ends
            }   // 2. Income to Debt Ratio delete-if ends
            // 3. County filter
            if(uniqueChoiceTracker.contains(3)) {

                //Runs query from p2v view if it's the first element in the hashset
                if(firstHashSetElement == 3) {
                        
                    //Call the method from pv2
                    filterByCounty(setCountyNames());

                    //Create the temp table for it (to apply filters for other methods)
                    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "@EdwardWijaya04")) {

                        //Create the empty temp table that will store the filtered results
                        String createTempTableQuery = "CREATE TEMP TABLE temp_results (applicant_id INTEGER, action_taken INTEGER, purchaser_type INTEGER, msamd TEXT, applicant_income_000s INTEGER, loan_amount_000s INTEGER, county_code TEXT, loan_type INTEGER, tract_to_msamd_income INTEGER, loan_purpose INTEGER, property_type INTEGER, owner_occupancy INTEGER, lien_status INTEGER, rate_spread NUMERIC(10,5))";
        
                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate(createTempTableQuery);
                            System.out.println("Temporary table created.");
                        } catch (SQLException e) {
                            System.err.println("Error creating temporary table: " + e.getMessage());
                        }
        
                        //Insert values into temp_filtered_results
                        String insertQuery = "INSERT INTO temp_results (applicant_id, action_taken, purchaser_type, msamd, applicant_income_000s, loan_amount_000s, county_code, loan_type, tract_to_msamd_income, loan_purpose, property_type, owner_occupancy, lien_status, rate_spread) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
                        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                            for (Map<String, Object> row : filteredResults) {
                                stmt.setObject(1, (Integer) row.get("applicant_id"));
                                stmt.setObject(2, (Integer) row.get("action_taken"));
                                stmt.setObject(3, (Integer) row.get("purchaser_type"));
                                stmt.setObject(4, (String) row.get("msamd"));
                                stmt.setObject(5, (Integer) row.get("applicant_income_000s"));
                                stmt.setObject(6, (Integer) row.get("loan_amount_000s"));
                                stmt.setObject(7, (String) row.get("county_code"));
                                stmt.setObject(8, (Integer) row.get("loan_type"));
                                stmt.setObject(9, (Integer) row.get("tract_to_msamd_income"));
                                stmt.setObject(10, (Integer) row.get("loan_purpose"));
                                stmt.setObject(11, (Integer) row.get("property_type"));
                                stmt.setObject(12, (Integer) row.get("owner_occupancy"));
                                stmt.setObject(13, (Integer) row.get("lien_status"));
                                stmt.setObject(14, (Double) row.get("rate_spread"));
                                stmt.addBatch();
                            }
        
                            stmt.executeBatch();
                            System.out.println("Filtered results inserted into temporary table.");
                        } catch (SQLException e) {
                            System.err.println("Error inserting filtered results: " + e.getMessage());
                        }
                    } catch (SQLException e) {
                        System.err.println("Error inserting filtered results: " + e.getMessage());
                    }
                }   //If statement for first hashset element ends
                //Runs query from temp_results table if it's not the first element in the hashset
                else {

                    String countyInput = setCountyNames();

                    //Split the input string into separate county names
                    List<String> countyArray = Arrays.asList(countyInput.split("\\s*,\\s*"));

                    //Build SQL query
                    queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN county_table ct ON tr.county_code = ct.county_code WHERE ct.county_name IN (");

                    //Append the correct number of question marks based on the county names list size
                    for(int i = 0; i < countyArray.size(); i++) {
                        queryBuilder.append("?");
                        if(i < countyArray.size() - 1) {
                            queryBuilder.append(", "); //Add comma except for the last parameter
                        }
                    } queryBuilder.append(");");

                    //List<Map<String, Object>> results = new ArrayList<>();
                    rowCount = 0; // Initialize row count
                    totalLoanAmount = 0; // Initialize total loan amount

                    //Convert SQL query to a String
                    String countyNamesFilterQuery = queryBuilder.toString();

                    try(PreparedStatement stmt = conn.prepareStatement(countyNamesFilterQuery)) {
                        //Set each county parameter in the PreparedStatement
                        for(int i = 0; i < countyArray.size(); i++) {
                            stmt.setString(i + 1, countyArray.get(i)); // Set each county in the prepared statement
                        }

                        //Execute the query
                        try(ResultSet rs = stmt.executeQuery()) {
                            //If no results are found
                            if(!rs.next()) {
                                System.out.println("No results found for the given county name(s).");
                            } else {
                                //System.out.println("County Filter Results:");
                                //Process the result set
                                do {
                                    // Process the result set and add to the list
                                    Map<String, Object> row = new HashMap<>();
                                    int loanAmount = rs.getInt("loan_amount_000s");

                                    row.put("applicant_id", rs.getInt("applicant_id"));
                                    row.put("action_taken", rs.getInt("action_taken"));
                                    row.put("purchaser_type", rs.getInt("purchaser_type"));
                                    row.put("msamd", rs.getString("msamd"));
                                    row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                    row.put("loan_amount_000s", loanAmount);
                                    row.put("county_code", rs.getString("county_code"));
                                    row.put("loan_type", rs.getInt("loan_type"));
                                    row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                    row.put("loan_purpose", rs.getInt("loan_purpose"));
                                    row.put("property_type", rs.getInt("property_type"));
                                    row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                    row.put("lien_status", rs.getInt("lien_status"));
                                    row.put("rate_spread", rs.getDouble("rate_spread"));

                                    // Add the row to the results list
                                    results.add(row);

                                    //Update row count and total loan amount
                                    rowCount++;
                                    totalLoanAmount += loanAmount;

                                    //Output the results
                                    //System.out.println("County Name: " + rs.getString("county_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                } while(rs.next()); // Continue until all rows are processed
                            }
                        }
                        //Display the number of rows and the sum of loan amounts
                        System.out.println("Number of rows matching the filters: " + rowCount);
                        System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                    } catch (SQLException e) {
                        //Handle SQL exceptions and provide error messages
                        System.err.println("Error executing County filter query: " + e.getMessage());
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                }   //Else statement for other hashset elements ends
            }   // 3. County delete-if ends
            // 4. Loan Type filter
            if(uniqueChoiceTracker.contains(4)) {

                //Runs query from p2v view if it is the first element in the hashset
                if(firstHashSetElement == 4) {

                    //Call the method from pv2
                    filterByLoanType(setLoanTypeValue());

                    //Create the temp table for it (to apply filters for other methods)
                    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "@EdwardWijaya04")) {

                        //Create the empty temp table that will store the filtered results
                        String createTempTableQuery = "CREATE TEMP TABLE temp_results (applicant_id INTEGER, action_taken INTEGER, purchaser_type INTEGER, msamd TEXT, applicant_income_000s INTEGER, loan_amount_000s INTEGER, county_code TEXT, loan_type INTEGER, tract_to_msamd_income INTEGER, loan_purpose INTEGER, property_type INTEGER, owner_occupancy INTEGER, lien_status INTEGER, rate_spread NUMERIC(10,5))";
        
                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate(createTempTableQuery);
                            System.out.println("Temporary table created.");
                        } catch (SQLException e) {
                            System.err.println("Error creating temporary table: " + e.getMessage());
                        }
        
                        //Insert values into temp_filtered_results
                        String insertQuery = "INSERT INTO temp_results (applicant_id, action_taken, purchaser_type, msamd, applicant_income_000s, loan_amount_000s, county_code, loan_type, tract_to_msamd_income, loan_purpose, property_type, owner_occupancy, lien_status, rate_spread) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
                        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                            for (Map<String, Object> row : filteredResults) {
                                stmt.setObject(1, (Integer) row.get("applicant_id"));
                                stmt.setObject(2, (Integer) row.get("action_taken"));
                                stmt.setObject(3, (Integer) row.get("purchaser_type"));
                                stmt.setObject(4, (String) row.get("msamd"));
                                stmt.setObject(5, (Integer) row.get("applicant_income_000s"));
                                stmt.setObject(6, (Integer) row.get("loan_amount_000s"));
                                stmt.setObject(7, (String) row.get("county_code"));
                                stmt.setObject(8, (Integer) row.get("loan_type"));
                                stmt.setObject(9, (Integer) row.get("tract_to_msamd_income"));
                                stmt.setObject(10, (Integer) row.get("loan_purpose"));
                                stmt.setObject(11, (Integer) row.get("property_type"));
                                stmt.setObject(12, (Integer) row.get("owner_occupancy"));
                                stmt.setObject(13, (Integer) row.get("lien_status"));
                                stmt.setObject(14, (Double) row.get("rate_spread"));
                                stmt.addBatch();
                            }
        
                            stmt.executeBatch();
                            System.out.println("Filtered results inserted into temporary table.");
                        } catch (SQLException e) {
                            System.err.println("Error inserting filtered results: " + e.getMessage());
                        }
                    } catch (SQLException e) {
                        System.err.println("Error inserting filtered results: " + e.getMessage());
                    }
                }   //If statement for first hashset element ends

                //Runs query from temp_results table if it's not the first element in the hashset
                else {
                    String loanTypeInput = setLoanTypeValue();

                    //Split the input string into separate loan type values
                    List<String> loanTypeArray = Arrays.asList(loanTypeInput.split("\\s*,\\s*"));

                    //Build SQL query
                    queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN loan_type_table ltt ON tr.loan_type = ltt.loan_type WHERE ltt.loan_type_name IN (");

                    //Append the correct number of question marks based on the loan type list size
                    for(int i = 0; i < loanTypeArray.size(); i++) {
                        queryBuilder.append("?");
                        if(i < loanTypeArray.size() - 1) {
                            queryBuilder.append(", "); //Add comma except for the last parameter
                        }
                    } queryBuilder.append(");");

                    //List<Map<String, Object>> results = new ArrayList<>();
                    rowCount = 0; // Initialize row count
                    totalLoanAmount = 0; // Initialize total loan amount

                    //Converts SQL query to String
                    String loanTypeFilterQuery = queryBuilder.toString();

                    try (PreparedStatement stmt = conn.prepareStatement(loanTypeFilterQuery)) {
                        //Set placeholder values
                        for (int i = 0; i < loanTypeArray.size(); i++) {
                            stmt.setString(i + 1, loanTypeArray.get(i));
                        }

                        //Execute query 
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (!rs.next()) {
                                System.out.println("No results found for the selected loan type.");
                            } else {
                                //System.out.println("Loan Type Results:");
                                do {
                                    // Process the result set and add to the list
                                    Map<String, Object> row = new HashMap<>();
                                    int loanAmount = rs.getInt("loan_amount_000s");

                                    row.put("applicant_id", rs.getInt("applicant_id"));
                                    row.put("action_taken", rs.getInt("action_taken"));
                                    row.put("purchaser_type", rs.getInt("purchaser_type"));
                                    row.put("msamd", rs.getString("msamd"));
                                    row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                    row.put("loan_amount_000s", loanAmount);
                                    row.put("county_code", rs.getString("county_code"));
                                    row.put("loan_type", rs.getInt("loan_type"));
                                    row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                    row.put("loan_purpose", rs.getInt("loan_purpose"));
                                    row.put("property_type", rs.getInt("property_type"));
                                    row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                    row.put("lien_status", rs.getInt("lien_status"));
                                    row.put("rate_spread", rs.getDouble("rate_spread"));

                                    // Add the row to the results list
                                    results.add(row);

                                    //Update row count and total loan amount
                                    rowCount++;
                                    totalLoanAmount += loanAmount;

                                    //System.out.println("Loan Type Name: " + rs.getString("loan_type_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                } while (rs.next());
                            }
                        }
                        //Display the number of rows and the sum of loan amounts
                        System.out.println("Number of rows matching the filters: " + rowCount);
                        System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                    } catch (SQLException e) {
                        //Handle SQL exceptions and provide error messages
                        System.err.println("Error executing Loan Type filter query: " + e.getMessage());
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                }   //Else statement for other hashset elements ends
            }   // 4. Loan Type delete-if ends
            // 5. Tract to MSAMD Income filter
            if(uniqueChoiceTracker.contains(5)) {

                //Runs query from p2v view if it's the first element in the hashset
                if(firstHashSetElement == 5) {

                    //Call the method from pv2
                    filterByTractToMsamdIncome(setMinNum(), setMaxNum());

                    //Create the temp table for it (to apply filters for other methods)
                    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "@EdwardWijaya04")) {

                        //Create the empty temp table that will store the filtered results
                        String createTempTableQuery = "CREATE TEMP TABLE temp_results (applicant_id INTEGER, action_taken INTEGER, purchaser_type INTEGER, msamd TEXT, applicant_income_000s INTEGER, loan_amount_000s INTEGER, county_code TEXT, loan_type INTEGER, tract_to_msamd_income INTEGER, loan_purpose INTEGER, property_type INTEGER, owner_occupancy INTEGER, lien_status INTEGER, rate_spread NUMERIC(10,5))";
        
                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate(createTempTableQuery);
                            System.out.println("Temporary table created.");
                        } catch (SQLException e) {
                            System.err.println("Error creating temporary table: " + e.getMessage());
                        }
        
                        //Insert values into temp_filtered_results
                        String insertQuery = "INSERT INTO temp_results (applicant_id, action_taken, purchaser_type, msamd, applicant_income_000s, loan_amount_000s, county_code, loan_type, tract_to_msamd_income, loan_purpose, property_type, owner_occupancy, lien_status, rate_spread) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
                        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                            for (Map<String, Object> row : filteredResults) {
                                stmt.setObject(1, (Integer) row.get("applicant_id"));
                                stmt.setObject(2, (Integer) row.get("action_taken"));
                                stmt.setObject(3, (Integer) row.get("purchaser_type"));
                                stmt.setObject(4, (String) row.get("msamd"));
                                stmt.setObject(5, (Integer) row.get("applicant_income_000s"));
                                stmt.setObject(6, (Integer) row.get("loan_amount_000s"));
                                stmt.setObject(7, (String) row.get("county_code"));
                                stmt.setObject(8, (Integer) row.get("loan_type"));
                                stmt.setObject(9, (Integer) row.get("tract_to_msamd_income"));
                                stmt.setObject(10, (Integer) row.get("loan_purpose"));
                                stmt.setObject(11, (Integer) row.get("property_type"));
                                stmt.setObject(12, (Integer) row.get("owner_occupancy"));
                                stmt.setObject(13, (Integer) row.get("lien_status"));
                                stmt.setObject(14, (Double) row.get("rate_spread"));
                                stmt.addBatch();
                            }
        
                            stmt.executeBatch();
                            System.out.println("Filtered results inserted into temporary table.");
                        } catch (SQLException e) {
                            System.err.println("Error inserting filtered results: " + e.getMessage());
                        }
                    } catch (SQLException e) {
                        System.err.println("Error inserting filtered results: " + e.getMessage());
                    }
                }   // If statement for first hashset element ends
                //Runs query from temp_results table if it's not the first element in the hashset
                else {
                    Double minNum = setMinNum();
                    Double maxNum = setMaxNum();

                    //Build SQL query
                    queryBuilder = new StringBuilder("SELECT * FROM temp_results WHERE ");

                    //SQL query placeholder
                    int paramIndex2 = 1;

                    if(minNum != null && maxNum != null) { //Both min and max numericals are provided
                        queryBuilder.append("tract_to_msamd_income >= ? AND tract_to_msamd_income <= ?");
                    } else if(minNum != null && maxNum == null) { //Only min numerical is provided
                        queryBuilder.append("tract_to_msamd_income >= ?");
                    } else if(minNum != 0 && maxNum != null) { //Only max numerical is provided
                        queryBuilder.append("tract_to_msamd_income <= ?");
                    } queryBuilder.append(";");

                    //List<Map<String, Object>> results = new ArrayList<>(); 
                    rowCount = 0; // Initialize row count
                    totalLoanAmount = 0; // Initialize total loan amount

                    //Converts SQL query to String
                    String tractToMsamdIncomeFilter = queryBuilder.toString();

                    try(PreparedStatement stmt = conn.prepareStatement(tractToMsamdIncomeFilter)) {
                        //Set the min numerical value with placeholders (if provided)
                        if(minNum != null) {
                            stmt.setDouble(paramIndex2++, minNum);
                        }

                        //Set the max numerical value with placeholders (if provided)
                        if(maxNum != null) {
                            stmt.setDouble(paramIndex2++, maxNum);
                        }

                        //Execute the query
                        try(ResultSet rs = stmt.executeQuery()) {
                            if(!rs.next()) {
                                System.out.println("No results found for the given tract-to-msamd income.");
                            } else {
                                //System.out.println("Tract-to-Msamd Income Results:");
                                do {
                                    // Process the result set and add to the list
                                    Map<String, Object> row = new HashMap<>();
                                    int loanAmount = rs.getInt("loan_amount_000s");

                                    row.put("applicant_id", rs.getInt("applicant_id"));
                                    row.put("action_taken", rs.getInt("action_taken"));
                                    row.put("purchaser_type", rs.getInt("purchaser_type"));
                                    row.put("msamd", rs.getString("msamd"));
                                    row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                    row.put("loan_amount_000s", loanAmount);
                                    row.put("county_code", rs.getString("county_code"));
                                    row.put("loan_type", rs.getInt("loan_type"));
                                    row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                    row.put("loan_purpose", rs.getInt("loan_purpose"));
                                    row.put("property_type", rs.getInt("property_type"));
                                    row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                    row.put("lien_status", rs.getInt("lien_status"));
                                    row.put("rate_spread", rs.getDouble("rate_spread"));

                                    // Add the row to the results list
                                    results.add(row);

                                    //Update row count and total loan amount
                                    rowCount++;
                                    totalLoanAmount += loanAmount;

                                    //System.out.println("Tract to MSAMD Income: " + rs.getDouble("tract_to_msamd_income") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                } while(rs.next());
                            }
                        }
                        //Display the number of rows and the sum of loan amounts
                        System.out.println("Number of rows matching the filters: " + rowCount);
                        System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                    } catch (SQLException e) {
                        // Handle SQL exceptions and provide error messages
                        System.err.println("Error executing Tract to MSAMD Income filter query: " + e.getMessage());
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                } // Else statement for other hashset elements ends
            }   // 5. Tract to MSAMD Income delete-if ends
            // 6. Loan Purpose filter
            if(uniqueChoiceTracker.contains(6)) {

                //Runs query from p2v view if it's the first element in the hashset
                if(firstHashSetElement == 6) {

                    //Call the method from pv2
                    filterByLoanPurpose(setLoanPurposeName());

                    //Create the temp table for it (to apply filters for other methods)
                    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "@EdwardWijaya04")) {

                        //Create the empty temp table that will store the filtered results
                        String createTempTableQuery = "CREATE TEMP TABLE temp_results (applicant_id INTEGER, action_taken INTEGER, purchaser_type INTEGER, msamd TEXT, applicant_income_000s INTEGER, loan_amount_000s INTEGER, county_code TEXT, loan_type INTEGER, tract_to_msamd_income INTEGER, loan_purpose INTEGER, property_type INTEGER, owner_occupancy INTEGER, lien_status INTEGER, rate_spread NUMERIC(10,5))";
        
                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate(createTempTableQuery);
                            System.out.println("Temporary table created.");
                        } catch (SQLException e) {
                            System.err.println("Error creating temporary table: " + e.getMessage());
                        }
        
                        //Insert values into temp_filtered_results
                        String insertQuery = "INSERT INTO temp_results (applicant_id, action_taken, purchaser_type, msamd, applicant_income_000s, loan_amount_000s, county_code, loan_type, tract_to_msamd_income, loan_purpose, property_type, owner_occupancy, lien_status, rate_spread) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
                        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                            for (Map<String, Object> row : filteredResults) {
                                stmt.setObject(1, (Integer) row.get("applicant_id"));
                                stmt.setObject(2, (Integer) row.get("action_taken"));
                                stmt.setObject(3, (Integer) row.get("purchaser_type"));
                                stmt.setObject(4, (String) row.get("msamd"));
                                stmt.setObject(5, (Integer) row.get("applicant_income_000s"));
                                stmt.setObject(6, (Integer) row.get("loan_amount_000s"));
                                stmt.setObject(7, (String) row.get("county_code"));
                                stmt.setObject(8, (Integer) row.get("loan_type"));
                                stmt.setObject(9, (Integer) row.get("tract_to_msamd_income"));
                                stmt.setObject(10, (Integer) row.get("loan_purpose"));
                                stmt.setObject(11, (Integer) row.get("property_type"));
                                stmt.setObject(12, (Integer) row.get("owner_occupancy"));
                                stmt.setObject(13, (Integer) row.get("lien_status"));
                                stmt.setObject(14, (Double) row.get("rate_spread"));
                                stmt.addBatch();
                            }
        
                            stmt.executeBatch();
                            System.out.println("Filtered results inserted into temporary table.");
                        } catch (SQLException e) {
                            System.err.println("Error inserting filtered results: " + e.getMessage());
                        }
                    } catch (SQLException e) {
                        System.err.println("Error inserting filtered results: " + e.getMessage());
                    }
                }   // If statement for first hashset element ends
                //Runs query from temp_results table if it's not the first element in the hashset
                else {

                    //The loan purpose numbers that the user will select
                    String loanPurposeInput = setLoanPurposeName();

                    //Split the input string into separate loan purpose names
                    List<String> loanPurposeArray = Arrays.asList(loanPurposeInput.split("\\s*,\\s*"));

                    //Build SQL query
                    queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN loan_purpose_table lpt ON tr.loan_purpose = lpt.loan_purpose WHERE lpt.loan_purpose_name IN (");

                    //Append the correct number of question marks based on the loan purpose list size
                    for(int i = 0; i < loanPurposeArray.size(); i++) {
                        queryBuilder.append("?");
                        if(i < loanPurposeArray.size() - 1) {
                            queryBuilder.append(", "); //Add comma except for the last parameter
                        }
                    } queryBuilder.append(");");

                    //List<Map<String, Object>> results = new ArrayList<>();
                    rowCount = 0; // Initialize row count
                    totalLoanAmount = 0; // Initialize total loan amount

                    //Convert SQL query to a String
                    String loanPurposeFilterQuery = queryBuilder.toString();

                    try(PreparedStatement stmt = conn.prepareStatement(loanPurposeFilterQuery)) {
                        //Set each loan purpose parameter in the PreparedStatement
                        for(int i = 0; i < loanPurposeArray.size(); i++) {
                            stmt.setString(i + 1, loanPurposeArray.get(i)); //Set each loan purpose in the prepared statement
                        }

                        //Execute the query
                        try(ResultSet rs = stmt.executeQuery()) {
                            //If no results are found
                            if(!rs.next()) {
                                System.out.println("No results found for the given loan purpose(s).");
                            } else {
                                //System.out.println("Loan Purpose Filter Results:");
                                //Process the result set
                                do {

                                    // Process the result set and add to the list
                                    Map<String, Object> row = new HashMap<>();
                                    int loanAmount = rs.getInt("loan_amount_000s");

                                    row.put("applicant_id", rs.getInt("applicant_id"));
                                    row.put("action_taken", rs.getInt("action_taken"));
                                    row.put("purchaser_type", rs.getInt("purchaser_type"));
                                    row.put("msamd", rs.getString("msamd"));
                                    row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                    row.put("loan_amount_000s", loanAmount);
                                    row.put("county_code", rs.getString("county_code"));
                                    row.put("loan_type", rs.getInt("loan_type"));
                                    row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                    row.put("loan_purpose", rs.getInt("loan_purpose"));
                                    row.put("property_type", rs.getInt("property_type"));
                                    row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                    row.put("lien_status", rs.getInt("lien_status"));
                                    row.put("rate_spread", rs.getDouble("rate_spread"));

                                    // Add the row to the results list
                                    results.add(row);

                                    //Update row count and total loan amount
                                    rowCount++;
                                    totalLoanAmount += loanAmount;

                                    //Output the results
                                    //System.out.println("Loan Purpose Name: " + rs.getString("loan_purpose_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                } while(rs.next()); //Continue until all rows are processed
                            }
                        }
                        //Display the number of rows and the sum of loan amounts
                        System.out.println("Number of rows matching the filters: " + rowCount);
                        System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                    } catch (SQLException e) {
                        //Handle SQL exceptions and provide error messages
                        System.err.println("Error executing Loan Purpose filter query: " + e.getMessage());
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                } // Else statement for other hashset elements ends            
            } // 6. Loan Purpose delete-if ends
            // 7. Property Type filter
            if(uniqueChoiceTracker.contains(7)) {

                //Runs query from p2v view if it's the first element in the hashset
                if(firstHashSetElement == 7) {

                    //Call the method from pv2
                    filterByPropertyType(setPropertyTypeName());

                    //Create the temp table for it (to apply filters for other methods)
                    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "@EdwardWijaya04")) {

                        //Create the empty temp table that will store the filtered results
                        String createTempTableQuery = "CREATE TEMP TABLE temp_results (applicant_id INTEGER, action_taken INTEGER, purchaser_type INTEGER, msamd TEXT, applicant_income_000s INTEGER, loan_amount_000s INTEGER, county_code TEXT, loan_type INTEGER, tract_to_msamd_income INTEGER, loan_purpose INTEGER, property_type INTEGER, owner_occupancy INTEGER, lien_status INTEGER, rate_spread NUMERIC(10,5))";
        
                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate(createTempTableQuery);
                            System.out.println("Temporary table created.");
                        } catch (SQLException e) {
                            System.err.println("Error creating temporary table: " + e.getMessage());
                        }
        
                        //Insert values into temp_filtered_results
                        String insertQuery = "INSERT INTO temp_results (applicant_id, action_taken, purchaser_type, msamd, applicant_income_000s, loan_amount_000s, county_code, loan_type, tract_to_msamd_income, loan_purpose, property_type, owner_occupancy, lien_status, rate_spread) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
                        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                            for (Map<String, Object> row : filteredResults) {
                                stmt.setObject(1, (Integer) row.get("applicant_id"));
                                stmt.setObject(2, (Integer) row.get("action_taken"));
                                stmt.setObject(3, (Integer) row.get("purchaser_type"));
                                stmt.setObject(4, (String) row.get("msamd"));
                                stmt.setObject(5, (Integer) row.get("applicant_income_000s"));
                                stmt.setObject(6, (Integer) row.get("loan_amount_000s"));
                                stmt.setObject(7, (String) row.get("county_code"));
                                stmt.setObject(8, (Integer) row.get("loan_type"));
                                stmt.setObject(9, (Integer) row.get("tract_to_msamd_income"));
                                stmt.setObject(10, (Integer) row.get("loan_purpose"));
                                stmt.setObject(11, (Integer) row.get("property_type"));
                                stmt.setObject(12, (Integer) row.get("owner_occupancy"));
                                stmt.setObject(13, (Integer) row.get("lien_status"));
                                stmt.setObject(14, (Double) row.get("rate_spread"));
                                stmt.addBatch();
                            }
        
                            stmt.executeBatch();
                            System.out.println("Filtered results inserted into temporary table.");
                        } catch (SQLException e) {
                            System.err.println("Error inserting filtered results: " + e.getMessage());
                        }
                    } catch (SQLException e) {
                        System.err.println("Error inserting filtered results: " + e.getMessage());
                    }
                }   // If statement for first hashset element ends
                //Runs query from temp_results table if it's not the first element in the hashset
                else {
                    //The property type numbers that the user will select
                    String propertyTypeInput = setPropertyTypeName();

                    //Split the input string into separate property type names
                    List<String> propertyTypeArray = Arrays.asList(propertyTypeInput.split("\\s*,\\s*"));

                    //Build SQL query
                    queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN property_type_table ptt ON tr.property_type = ptt.property_type WHERE ptt.property_type_name IN (");

                    //Append the correct number of question marks based on the property type list size
                    for(int i = 0; i < propertyTypeArray.size(); i++) {
                        queryBuilder.append("?");
                        if(i < propertyTypeArray.size() - 1) {
                            queryBuilder.append(", "); //Add comma except for the last parameter
                        }
                    } queryBuilder.append(");");

                    //List<Map<String, Object>> results = new ArrayList<>();
                    rowCount = 0; // Initialize row count
                    totalLoanAmount = 0; // Initialize total loan amount

                    //Convert SQL query to a String
                    String propertyTypeFilterQuery = queryBuilder.toString();

                    try(PreparedStatement stmt = conn.prepareStatement(propertyTypeFilterQuery)) {
                        //Set each property tpe parameter in the PreparedStatement
                        for(int i = 0; i < propertyTypeArray.size(); i++) {
                            stmt.setString(i + 1, propertyTypeArray.get(i)); //Set each property in the prepared statement
                        }

                        //Execute the query
                        try(ResultSet rs = stmt.executeQuery()) {
                            //If no results are found
                            if(!rs.next()) {
                                System.out.println("No results found for the given property type(s).");
                            } else {
                                //System.out.println("Property Type Filter Results:");
                                //Process the result set
                                do {
                                    // Process the result set and add to the list
                                    Map<String, Object> row = new HashMap<>();
                                    int loanAmount = rs.getInt("loan_amount_000s");

                                    row.put("applicant_id", rs.getInt("applicant_id"));
                                    row.put("action_taken", rs.getInt("action_taken"));
                                    row.put("purchaser_type", rs.getInt("purchaser_type"));
                                    row.put("msamd", rs.getString("msamd"));
                                    row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                    row.put("loan_amount_000s", loanAmount);
                                    row.put("county_code", rs.getString("county_code"));
                                    row.put("loan_type", rs.getInt("loan_type"));
                                    row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                    row.put("loan_purpose", rs.getInt("loan_purpose"));
                                    row.put("property_type", rs.getInt("property_type"));
                                    row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                    row.put("lien_status", rs.getInt("lien_status"));
                                    row.put("rate_spread", rs.getDouble("rate_spread"));

                                    // Add the row to the results list
                                    results.add(row);

                                    //Update row count and total loan amount
                                    rowCount++;
                                    totalLoanAmount += loanAmount;

                                    //Output the results
                                    //System.out.println("Property Type Name: " + rs.getString("property_type_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                } while(rs.next()); //Continue until all rows are processed
                            }
                        }
                        //Display the number of rows and the sum of loan amounts
                        System.out.println("Number of rows matching the filters: " + rowCount);
                        System.out.println("Total loan amount matching the filters: " + totalLoanAmount);
                    } catch (SQLException e) {
                        //Handle SQL exceptions and provide error messages
                        System.err.println("Error executing Property Type filter query: " + e.getMessage());
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                } // Else statement for other hashset elements ends
            } // 7. Property Type delete-if ends
            //8. Owner Occupancy filter
            if(uniqueChoiceTracker.contains(8)) {

                //Runs query form p2v view if it's the first element in the hashset
                if(firstHashSetElement == 8) {

                    //Call the method from pv2
                    filterByOwnerOccupancy(setOwnerOccupancyName());

                    //Create the temp table for it (to apply filters for other methods)
                    try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project1db", "postgres", "@EdwardWijaya04")) {

                        //Create the empty temp table that will store the filtered results
                        String createTempTableQuery = "CREATE TEMP TABLE temp_results (applicant_id INTEGER, action_taken INTEGER, purchaser_type INTEGER, msamd TEXT, applicant_income_000s INTEGER, loan_amount_000s INTEGER, county_code TEXT, loan_type INTEGER, tract_to_msamd_income INTEGER, loan_purpose INTEGER, property_type INTEGER, owner_occupancy INTEGER, lien_status INTEGER, rate_spread NUMERIC(10,5))";
        
                        try (Statement stmt = conn.createStatement()) {
                            stmt.executeUpdate(createTempTableQuery);
                            System.out.println("Temporary table created.");
                        } catch (SQLException e) {
                            System.err.println("Error creating temporary table: " + e.getMessage());
                        }
        
                        //Insert values into temp_filtered_results
                        String insertQuery = "INSERT INTO temp_results (applicant_id, action_taken, purchaser_type, msamd, applicant_income_000s, loan_amount_000s, county_code, loan_type, tract_to_msamd_income, loan_purpose, property_type, owner_occupancy, lien_status, rate_spread) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
                        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                            for (Map<String, Object> row : filteredResults) {
                                stmt.setObject(1, (Integer) row.get("applicant_id"));
                                stmt.setObject(2, (Integer) row.get("action_taken"));
                                stmt.setObject(3, (Integer) row.get("purchaser_type"));
                                stmt.setObject(4, (String) row.get("msamd"));
                                stmt.setObject(5, (Integer) row.get("applicant_income_000s"));
                                stmt.setObject(6, (Integer) row.get("loan_amount_000s"));
                                stmt.setObject(7, (String) row.get("county_code"));
                                stmt.setObject(8, (Integer) row.get("loan_type"));
                                stmt.setObject(9, (Integer) row.get("tract_to_msamd_income"));
                                stmt.setObject(10, (Integer) row.get("loan_purpose"));
                                stmt.setObject(11, (Integer) row.get("property_type"));
                                stmt.setObject(12, (Integer) row.get("owner_occupancy"));
                                stmt.setObject(13, (Integer) row.get("lien_status"));
                                stmt.setObject(14, (Double) row.get("rate_spread"));
                                stmt.addBatch();
                            }
        
                            stmt.executeBatch();
                            System.out.println("Filtered results inserted into temporary table.");
                        } catch (SQLException e) {
                            System.err.println("Error inserting filtered results: " + e.getMessage());
                        }
                    } catch (SQLException e) {
                        System.err.println("Error inserting filtered results: " + e.getMessage());
                    }
                }   // If statement for first hashset element ends
                //Runs query from temp_results table if it's not the first element in the hashset
                else {

                    //The Owner Occupancy numbers that the user will select
                    String ownerOccupancyInput = setOwnerOccupancyName();

                    //Split the input string into separate Owner Occupancy numbers
                    List<String> ownerOccupancyArray = Arrays.asList(ownerOccupancyInput.split("\\s*,\\s*"));

                    //Build SQL query
                    queryBuilder = new StringBuilder("SELECT * FROM temp_results tr JOIN owner_occupancy_table oot ON tr.owner_occupancy = oot.owner_occupancy WHERE oot.owner_occupancy_name IN (");

                    //Append the correct number of question marks based on the owner occupancy list size
                    for(int i = 0; i < ownerOccupancyArray.size(); i++) {
                        queryBuilder.append("?");
                        if(i < ownerOccupancyArray.size() - 1) {
                            queryBuilder.append(", "); //Add comma except for the last parameter
                        }
                    } queryBuilder.append(");");

                    //List<Map<String, Object>> results = new ArrayList<>();
                    rowCount = 0; //Initialize row count
                    totalLoanAmount = 0; //Initialize total loan amount

                    //Convert SQL query to a String
                    String ownerOccupancyFilterQuery = queryBuilder.toString();

                    try(PreparedStatement stmt = conn.prepareStatement(ownerOccupancyFilterQuery)) {
                        //Set each owner occupancy parameter in the PreparedStatement
                        for(int i = 0; i < ownerOccupancyArray.size(); i++) {
                            stmt.setString(i + 1, ownerOccupancyArray.get(i)); //Set each owner occupancy in the prepared statement
                        }

                        //Execute the query
                        try(ResultSet rs = stmt.executeQuery()) {
                            //If no results are found
                            if(!rs.next()) {
                                System.out.println("No results found for the given owner occupancy(ies).");
                            } else {
                                //System.out.println("Owner Occupancy Filter Results:");
                                //Process the result set
                                do {
                                    // Process the result set and add to the list
                                    Map<String, Object> row = new HashMap<>();
                                    int loanAmount = rs.getInt("loan_amount_000s");

                                    row.put("applicant_id", rs.getInt("applicant_id"));
                                    row.put("action_taken", rs.getInt("action_taken"));
                                    row.put("purchaser_type", rs.getInt("purchaser_type"));
                                    row.put("msamd", rs.getString("msamd"));
                                    row.put("applicant_income_000s", rs.getInt("applicant_income_000s"));
                                    row.put("loan_amount_000s", loanAmount);
                                    row.put("county_code", rs.getString("county_code"));
                                    row.put("loan_type", rs.getInt("loan_type"));
                                    row.put("tract_to_msamd_income", rs.getInt("tract_to_msamd_income"));
                                    row.put("loan_purpose", rs.getInt("loan_purpose"));
                                    row.put("property_type", rs.getInt("property_type"));
                                    row.put("owner_occupancy", rs.getInt("owner_occupancy"));
                                    row.put("lien_status", rs.getInt("lien_status"));
                                    row.put("rate_spread", rs.getDouble("rate_spread"));

                                    // Add the row to the results list
                                    results.add(row);

                                    //Update row count and total loan amount
                                    rowCount++;
                                    totalLoanAmount += loanAmount;

                                    // Output the results
                                    //System.out.println("Owner Occupancy Name: " + rs.getString("owner_occupancy_name") + ", Lien Status: " + rs.getString("lien_status") + ", Rate Spread: " + rs.getString("rate_spread"));
                                } while(rs.next()); //Continue until all rows are processed
                            }
                        }  
                        //Display the number of rows and the sum of loan amounts
                        System.out.println("Number of rows matching the filters: " + rowCount);
                        System.out.println("Total loan amount matching the filters: " + totalLoanAmount);       
                    } catch (SQLException e) {
                        //Handle SQL exceptions and provide error messages
                        System.err.println("Error executing Owner Occupancy filter query: " + e.getMessage());
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                } // Else statement for other hashset elements ends
            } // 8. Owner Occupancy delete-if ends
        } catch(Exception e) {
            System.out.println("Error deleting filter: " + e.getMessage());
        }   // End of catch block for delete-if's      
        return filteredResults; //return type for the deleteFilter method
    }   // Delete filter method ends
}