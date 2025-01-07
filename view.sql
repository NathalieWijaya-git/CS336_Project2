DROP VIEW IF EXISTS project_2_view CASCADE;
CREATE VIEW project_2_view AS
SELECT ait.applicant_id, action_taken, purchaser_type, msamd, applicant_income_000s, loan_amount_000s, county_code, loan_type, tract_to_msamd_income, loan_purpose, property_type, owner_occupancy, lien_status, rate_spread
FROM loan_information_table lit 
INNER JOIN applicant_information_table ait
ON lit.applicant_id = ait.applicant_id
INNER JOIN property_location_table plt
ON ait.property_location_id = plt.property_location_id
WHERE action_taken = 1 AND (purchaser_type = 0 OR purchaser_type = 1 OR purchaser_type = 2 OR purchaser_type = 3 OR purchaser_type = 4 OR purchaser_type = 8);