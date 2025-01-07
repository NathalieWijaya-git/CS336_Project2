package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		
		String url = "jdbc:postgresql://127.0.0.1:5432/project1db";
        String username = "postgres";
        String password = "@EdwardWijaya04";

		try(Connection conn = DriverManager.getConnection(url, username, password)) {

			int filterInput = 0;
			int secondMenuOption = 0;
			FilterManager myFilter = new FilterManager(conn);
			MortgageProcessor myMortgage = new MortgageProcessor(conn);
			Set<Integer> uniqueChoiceTracker = new LinkedHashSet<>();
			Scanner myScanner = new Scanner(System.in);

			List<Map<String, Object>> filteredResults = new ArrayList<>();

			if(conn != null) {
				System.out.println("Connected to the database!");

				// Loop until a valid filter is input
				while(true) {
					System.out.println("Welcome to the MBS System!"); //Welcome message
					System.out.println("Here are a list of available filters:");
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
					try {
						filterInput = myScanner.nextInt();  // Read the user's input
		
						// Check if input is within the valid range
						if(filterInput >= 1 && filterInput <= 9) {
							System.out.println("You selected filter number: " + filterInput + ".");
							//Program will execute codes based on filter input
							switch(filterInput) {
								//MSAMD Filter
								case 1:
									//The MSAMD number that the user will select
									uniqueChoiceTracker.add(1);
									String msamdInput;

									//Displays the options of MSAMDs available
									String msamdDisplay = "SELECT *, COALESCE(msamd_name, CAST(msamd AS TEXT)) AS msamd_display FROM msamd_table;";
									try(PreparedStatement stmt = conn.prepareStatement(msamdDisplay)) {
										try(ResultSet rs = stmt.executeQuery()) {
											if(!rs.next()) {
												System.out.println("No data found in the table.");
											} else {
												//System.out.println("MSAMD");
												do{
													//msamdInput = rs.getString("msamd");
													String msamdDisplayValue = rs.getString("msamd_display");

													System.out.println(/*msamdInput + "\t" +*/ "MSAMD Name/Code: " + msamdDisplayValue);
												} while(rs.next());
											}	
										}
									}

									//Ask user to input a valid MSAMD number
									while(true) {
										System.out.println("Please input MSAMD(s), comma separated if multiple values: ");
										myScanner.nextLine();
										msamdInput = myScanner.nextLine();
										filteredResults = myFilter.filterByMsamd(msamdInput);
										//myMortgage.calculateRate(filteredResults);
										break;
									}
									break;
								//Income to Debt Ratio Filter
								case 2:
									uniqueChoiceTracker.add(2);
									System.out.println("Please enter the minimum income-to-debt ratio desired, enter 0 if none: ");
									double minRatio = myScanner.nextDouble();
									System.out.println("Please enter the maximum income-to-debt ratio desired, enter 0 if none: ");
									double maxRatio = myScanner.nextDouble();
									filteredResults = myFilter.filterByIncomeToDebtRatio(minRatio, maxRatio);
									//myMortgage.calculateRate(filteredResults);
									break;
								//County Filter
								case 3:
									//The county names that the user will select
									String countyInput;
									uniqueChoiceTracker.add(3);
				
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
									}

									//Ask user to input a valid county name
									while(true) {
										System.out.println("Please input county names(s), comma separated if multiple values: ");
										myScanner.nextLine();
										countyInput = myScanner.nextLine();
										filteredResults = myFilter.filterByCounty(countyInput);
										//myMortgage.calculateRate(filteredResults);
										break;
									}
									break;
								//Loan Type Filter
								case 4:
									//The loan type numbers that the user will select
									String loanTypeInput;
									uniqueChoiceTracker.add(4);

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
									}

									//Ask user to input a valid loan type number
									while(true) {
										System.out.println("Please input loan type(s), comma separated if multiple values: ");
										myScanner.nextLine();
										loanTypeInput = myScanner.nextLine();
										filteredResults = myFilter.filterByLoanType(loanTypeInput);
										//myMortgage.calculateRate(filteredResults);
										break;
									}
									break;
								//Tract to MSAMD Income Filter
								case 5:	
									uniqueChoiceTracker.add(5);
									System.out.println("Please enter the minimum tract-to-msamd income desired, enter 0 if none: ");
									double minNum = myScanner.nextDouble();
									System.out.println("Please enter the maximum tract-to-msamd income desired, enter 0 if none: ");
									double maxNum = myScanner.nextDouble();
									filteredResults = myFilter.filterByTractToMsamdIncome(minNum, maxNum);
									//myMortgage.calculateRate(filteredResults);
									break;
								//Loan Purpose Filter
								case 6:
									//The loan purpose numbers that the user will select
									String loanPurposeInput;
									uniqueChoiceTracker.add(6);

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
									}

									//Ask user to input a valid loan purpose number
									while(true) {
										System.out.println("Please input loan purpose(s), comma separated if multiple values: ");
										myScanner.nextLine();
										loanPurposeInput = myScanner.nextLine();
										filteredResults = myFilter.filterByLoanPurpose(loanPurposeInput);
										//myMortgage.calculateRate(filteredResults);
										break;
									}
									break;
								//Property Type Filter
								case 7:
									//The property type numbers that the user will select
									String propertyTypeInput;
									uniqueChoiceTracker.add(7);

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
									}

									//Ask user to input a valid property type number
									while(true) {
										System.out.println("Please input property type(s), comma separated if multiple values: ");
										myScanner.nextLine();
										propertyTypeInput = myScanner.nextLine();
										filteredResults = myFilter.filterByPropertyType(propertyTypeInput);
										//myMortgage.calculateRate(filteredResults);
										break;
									}
									break;
								//Owner Occupancy Filter
								case 8:
									//The Owner Occupancy numbers that the user will select
									String ownerOccupancyInput;
									uniqueChoiceTracker.add(8);

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
									}
									//Ask user to input a valid owner occupancy number
									while(true) {
										System.out.println("Please input owner occupancy(ies), comma separated if multiple values: ");
										myScanner.nextLine();
										ownerOccupancyInput = myScanner.nextLine();
										filteredResults = myFilter.filterByOwnerOccupancy(ownerOccupancyInput);
										//myMortgage.calculateRate(filteredResults);
										break;
									}
									break;
								case 9:
									System.out.println("Exiting system.");
									break;
							}

							boolean nextMenuChoice = true;

							//Loop will continue as long as nextMenuChoice is true
							while(nextMenuChoice == true) {

								System.out.println("1: Add Filter\n2: Delete Filter\n3: Calculate Rate\n4: Create New Mortgage\n5: Exit");
								System.out.println("Please select one of the options below: ");

								if (myScanner.hasNextInt()) {
									secondMenuOption = myScanner.nextInt();
								} else {
									System.out.println("Invalid input or no input available.");
								}

								if(secondMenuOption == 1) { //Add filter
									myFilter.addFilter(filteredResults, uniqueChoiceTracker); //Return back to the next menu choice loop
									nextMenuChoice = true;
								} else if(secondMenuOption == 2) { //Delete filter
									Set<Integer> nextUniqueChoiceTracker = myFilter.setUniqueChoiceTracker();
									myFilter.deleteFilter(filteredResults, nextUniqueChoiceTracker); //Return back to the next menu choice loop
									nextMenuChoice = true;
								} else if(secondMenuOption == 3) { //Calculate rate
									myMortgage.calculateRate(filteredResults);
									System.out.println("Would you like to accept or reject? \n 1. Accept\n 2. Reject");
									int decision = myScanner.nextInt();

									if (decision == 1) { //If accept...
										myMortgage.updatePurchaserType(filteredResults); //Change purchaser type to 5
										System.out.println("Accepted! Exiting program.");
										System.exit(0); //Exit program immediately
									} else if (decision == 2) { //If reject...
										System.out.println("Returning to main menu..."); //Return to main loop
										nextMenuChoice = false; //Exit next menu loop
									}
								} else if(secondMenuOption == 4) {
									myMortgage.createMortgage();
									System.exit(0);
								} else if(secondMenuOption == 5) { //Exit program
									System.out.println("Returning to main menu...");
									nextMenuChoice = false; //Return to main loop
								} 
							} break;
						} else {
							System.out.println("Invalid input. Please enter a number between 1 and 9.");
						}
					} catch (java.util.InputMismatchException e) {
						// Handle invalid input
						System.out.println("Invalid input. Please enter a valid integer.");
						myScanner.next();  // Consume the invalid input to avoid infinite loop
					}
				} 
			} else {
				System.out.println("Failed to make connection!");
			}
		} catch (SQLException e) {
			System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}