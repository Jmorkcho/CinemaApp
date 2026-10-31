-- First, check column type
SELECT COLUMN_NAME, DATA_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'projections' AND COLUMN_NAME IN ('start_time', 'end_time');

-- If they are DATE type, convert to DATETIME:
ALTER TABLE dbo.projections 
ALTER COLUMN start_time DATETIME NOT NULL;

ALTER TABLE dbo.projections 
ALTER COLUMN end_time DATETIME NOT NULL;

-- Now add the times
UPDATE dbo.projections
SET start_time = DATEADD(HOUR, 
    CASE (id % 5)
        WHEN 1 THEN 10
        WHEN 2 THEN 13
        WHEN 3 THEN 16
        WHEN 4 THEN 19
        WHEN 0 THEN 21
    END, 
    CAST(CAST(start_time AS DATE) AS DATETIME))
WHERE start_time IS NOT NULL;

UPDATE dbo.projections
SET end_time = DATEADD(HOUR, 2, start_time)
WHERE end_time IS NOT NULL;

-- Verify
SELECT TOP 5 id, CONVERT(VARCHAR(19), start_time, 120) as start_time FROM projections ORDER BY id;