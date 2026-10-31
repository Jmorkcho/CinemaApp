-- Check if there are any existing records
SELECT COUNT(*) FROM dbo.tickets;

-- If empty or minimal data, drop and recreate:
DROP TABLE dbo.tickets;

CREATE TABLE dbo.tickets (
    id INT PRIMARY KEY IDENTITY(1,1),
    owner_id INT NOT NULL,
    projection_id INT NOT NULL,
    seat INT NOT NULL,
    purchased_at DATETIME NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES dbo.users(id),
    FOREIGN KEY (projection_id) REFERENCES dbo.projections(id)
);

-- Verify
EXEC sp_help 'dbo.tickets';