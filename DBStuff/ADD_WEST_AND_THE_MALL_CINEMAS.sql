-- Adds the West Mall and The Mall cinemas (names must match the ones used in
-- WestMallView / TheMallView) with 3 halls each and a week of projections.
-- cinemas, halls and projections have no IDENTITY id in this database, so ids are assigned explicitly.
-- Safe to run more than once.
SET NOCOUNT ON;

INSERT INTO dbo.cinemas (id, name, city)
SELECT (SELECT ISNULL(MAX(id), 0) FROM dbo.cinemas) + ROW_NUMBER() OVER (ORDER BY v.name), v.name, v.city
FROM (VALUES ('KinoWestMall', 'Sofia'), ('KinoTheMall', 'Sofia')) AS v(name, city)
WHERE NOT EXISTS (SELECT 1 FROM dbo.cinemas c WHERE c.name = v.name);

INSERT INTO dbo.halls (id, number, capacity, cinema_id)
SELECT (SELECT ISNULL(MAX(id), 0) FROM dbo.halls) + ROW_NUMBER() OVER (ORDER BY c.id, h.number),
       h.number, h.capacity, c.id
FROM (VALUES (1, 100), (2, 150), (3, 200)) AS h(number, capacity)
CROSS JOIN dbo.cinemas c
WHERE c.name IN ('KinoWestMall', 'KinoTheMall')
  AND NOT EXISTS (SELECT 1 FROM dbo.halls x WHERE x.cinema_id = c.id AND x.number = h.number);

;WITH movie_list AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) - 1 AS rn FROM dbo.movies
),
movie_count AS (
    SELECT COUNT(*) AS cnt FROM dbo.movies
),
days AS (
    SELECT n FROM (VALUES (0), (1), (2), (3), (4), (5), (6)) AS d(n)
),
slots AS (
    SELECT idx, hr FROM (VALUES (0, 10), (1, 13), (2, 16), (3, 19), (4, 21)) AS s(idx, hr)
),
new_projections AS (
    SELECT
        h.id AS hall_id,
        DATEADD(HOUR, s.hr, DATEADD(DAY, d.n, CAST('2028-12-23' AS DATETIME))) AS start_time,
        (h.id * 3 + d.n * 5 + s.idx * 7) % mc.cnt AS movie_rn
    FROM dbo.halls h
    JOIN dbo.cinemas c ON c.id = h.cinema_id
    CROSS JOIN days d
    CROSS JOIN slots s
    CROSS JOIN movie_count mc
    WHERE c.name IN ('KinoWestMall', 'KinoTheMall')
)
INSERT INTO dbo.projections (id, movie_id, hall_id, start_time, end_time)
SELECT (SELECT ISNULL(MAX(id), 0) FROM dbo.projections) + ROW_NUMBER() OVER (ORDER BY np.hall_id, np.start_time),
       m.id, np.hall_id, np.start_time, DATEADD(HOUR, 2, np.start_time)
FROM new_projections np
JOIN movie_list m ON m.rn = np.movie_rn
WHERE NOT EXISTS (
    SELECT 1 FROM dbo.projections p
    WHERE p.hall_id = np.hall_id AND p.start_time = np.start_time
);

SELECT c.name, COUNT(DISTINCT h.id) AS halls, COUNT(p.id) AS projections
FROM dbo.cinemas c
LEFT JOIN dbo.halls h ON h.cinema_id = c.id
LEFT JOIN dbo.projections p ON p.hall_id = h.id
GROUP BY c.name;
