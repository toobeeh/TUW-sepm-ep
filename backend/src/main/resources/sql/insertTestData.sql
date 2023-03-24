-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

-- remove old test data, if existing
DELETE
FROM owner
WHERE id < 0;
DELETE
FROM horse
WHERE id < 0;

-- insert owners
INSERT INTO owner (id, first_name, last_name, email)
VALUES (-66, 'Obi-Wan', 'Kenobi', 'execute@order.sixtysix'),
       (-69, 'Wendeez', 'Nuts', 'wendys@nuts.com'),
       (-70, 'Kek', 'Owner', 'imakek@lol.xyz'),
       (-71, 'Ra-Ra-Rasputin', 'LoverOfDaRussianKween', 'rasputin@og.com'),
       (-72, 'Giga', 'Chad', 'chad@chad.com'),
       (-73, 'Andreas', 'Kieling', 'andreas@kieling.de'),
       (-74, 'Beren', 'Erchamion', 'gimmethesilmaril@tragic.story'),
       (-75, 'Luthien', 'Tinuviel', 'so@much.drama'),
       (-76, 'Peter', 'Purgathofer', 'denki@little-aurora.at'),
       (-77, 'Peter', 'Lustig', 'always@happy.at');

-- insert horses
-- hint: family tree generation may look weird (duplicate ancestors) in different branches
-- this is because of the entangled family tree of the spanish habsburger.
-- to get a better visual impression, here is a graphical representation:
-- https://sciencev1.orf.at/static2.orf.at/science/storyimg/storypart_272630.gif
INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, father_id, mother_id)
VALUES (-1, 'Ferdinand of Aragon', NULL, '1452-03-10', 'MALE', -66, NULL, NULL),
       (-2, 'Isabella of Castile', 'la Católica', '1451-04-22', 'FEMALE', -69, NULL, NULL),
       (-3, 'Maximilian I', 'Holy Roman Emperor', '1459-03-22', 'MALE', -70, NULL, NULL),
       (-4, 'Mary of Burgundy', NULL, '1457-02-13', 'FEMALE', -71, NULL, NULL),
       (-5, 'Manuel I', 'King of Portugal - Emanuel the Fortunate :-)', '1469-05-31', 'MALE', -72, NULL, NULL),
       (-6, 'Mary of Aragon', NULL, '1516-02-18', 'FEMALE', -73, -1, -2),
       (-7, 'Joanna I', 'Queen of Castile and Aragon - Joanna the Mad >:(', '1479-09-06', 'FEMALE', -74, -1, -2),
       (-8, 'Philip I', 'The Fair', '1478-07-22', 'MALE', -75, -3, -4),
       (-9, 'John III', 'King of Portugal', '1502-06-07', 'MALE', -76, -5, -6),
       (-10, 'Isabella of Portugal', NULL, '1503-10-24', 'FEMALE', NULL, -5, -6),
       (-11, 'Catherine', NULL, '1507-01-14', 'FEMALE', -66, -8, -7),
       (-12, 'Charles I', 'Holy Roman Emperor (Charles V)', '1500-02-24', 'MALE', -66, -8, -7),
       (-13, 'Ferdinand I', 'Holy Roman Emperor', '1503-03-20', 'MALE', -70, -8, -7),
       (-14, 'Anna of Hungary', NULL, '1503-07-23', 'FEMALE', NULL, NULL, NULL),
       (-15, 'Mary of Portugal', NULL, '1527-10-15', 'FEMALE', NULL, -9, -11),
       (-16, 'Philip II', NULL, '1527-05-21', 'MALE', NULL, -12, -10),
       (-17, 'Maria', NULL, '1528-06-21', 'FEMALE', NULL, -12, -10),
       (-18, 'Maximilian II', 'Holy Roman Emperor', '1527-07-31', 'MALE', NULL, -13, -14),
       (-19, 'Archduke Charles II', 'of Austria', '1540-06-03', 'MALE', NULL, -13, -14),
       (-20, 'Anna', NULL, '1528-07-07', 'FEMALE', NULL, -13, -14),
       (-21, 'Mary', NULL, '1551-03-21', 'FEMALE', NULL, NULL, -20),
       (-22, 'Anna of Austria', NULL, '1549-09-02', 'FEMALE', NULL, -18, -17),
       (-23, 'Charles', 'Don Carlos - serious health issues due to incest', '1545-07-08', 'MALE', NULL, -16, -15),
       (-24, 'Philip III', NULL, '1578-04-12', 'MALE', NULL, -16, NULL),
       (-25, 'Margaret of Austria', NULL, '1584-12-25', 'FEMALE', NULL, -19, -21),
       (-26, 'Ferdinand II', 'Holy Roman Emperor', '1578-07-09', 'MALE', NULL, -19, -21),
       (-27, 'Philip IV', NULL, '1605-04-08', 'MALE', NULL, -24, -25),
       (-28, 'Maria Anna of Austria', NULL, '1606-08-18', 'FEMALE', NULL, -24, -25),
       (-29, 'Ferdinand III', 'Holy Roman Emperor', '1608-07-13', 'MALE', NULL, -26, NULL),
       (-30, 'Mariana of Austria', NULL, '1624-12-24', 'FEMALE', NULL, -26, -28),
       (-31, 'Charles II', 'Last Habsburg ruler of Spain', '1661-09-06', 'MALE', NULL, -27, -30);

