-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE
FROM horse;

INSERT INTO horse (id, name, description, date_of_birth, sex)
VALUES (1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE');
INSERT INTO horse (id, name, description, date_of_birth, sex, mother_id)
VALUES (69, 'HORS', 'hors goes brrr', '2012-12-13', 'FEMALE', 1);
