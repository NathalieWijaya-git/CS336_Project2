-- Drop existing trigger if it exists
DROP TRIGGER IF EXISTS project_2_view_update ON project_2_view;

-- Drop existing function if it exists
DROP FUNCTION IF EXISTS update_project_2_view;

-- Create the trigger function
CREATE OR REPLACE FUNCTION update_project_2_view()
RETURNS TRIGGER AS $$
BEGIN
    -- Update purchaser_type in the loan_information_table for the matching applicant_id
    UPDATE loan_information_table
    SET purchaser_type = NEW.purchaser_type
    WHERE loan_information_table.applicant_id = NEW.applicant_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger
CREATE TRIGGER project_2_view_update
INSTEAD OF UPDATE ON project_2_view
FOR EACH ROW
EXECUTE FUNCTION update_project_2_view();