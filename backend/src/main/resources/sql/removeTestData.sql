-- remove test data, if existing
DELETE
FROM owner
WHERE id < 0;
DELETE
FROM horse
WHERE id < 0;