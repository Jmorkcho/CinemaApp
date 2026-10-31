-- UPDATE_PROJECTIONS_TO_2028.sql
-- Simple approach: Update existing projections from 2025 to 2028
-- This shifts all dates forward 3 years to ensure they're in the future

-- Update all existing projections: 2025-12-XX → 2028-12-XX
-- This keeps the same month/day/time structure but 3 years in the future
UPDATE dbo.projections
SET start_time = DATEADD(YEAR, 3, start_time),
    end_time = DATEADD(YEAR, 3, end_time);

PRINT 'All projections updated to 2028 dates!';
PRINT 'Total rows affected: ' + CAST(@@ROWCOUNT AS VARCHAR(10));
PRINT 'Sample dates are now in December 2028.';
PRINT 'Refresh your browser to see the updated schedule.';
