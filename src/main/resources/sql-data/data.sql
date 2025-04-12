INSERT INTO App_user (DTYPE, firstName, lastName, email, password, instrument)
VALUES
    -- smith@gmail.com / 2222
    ('User', 'John', 'Smith', 'smith@gmail.com', '{noop}2222', 'FRENCH_HORN'),
    -- alex@pja.edu.pl / 2222
    ('Musician','Alex', 'King', 'alex@pja.edu.pl',
     '{MD5}{rT4wBLjXEvWpB8F0MgvLUDt8v36HSZoTWcDiusl7jh4=}fda9321dca602a2bb6c42955a315a3fa', 'TUBA'),
    -- sj@my.com / 2222
    ('Musician', 'Stephen', 'Jones', 'monikwor91@gmail.com', '{noop}2222', 'OBOE');

INSERT INTO UserRole (name, description)
VALUES ('ADMIN', 'Adding/updating projects'),
       ('INSPECTOR', 'Inviting musicians to projects'),
       ('MUSICIAN', 'Regular user privileges');

INSERT INTO App_user_roles (User_id, roles_id)
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (3, 2);


--
-- INSERT INTO Musician (firstName, lastName, email, password, birthdate, address, pesel, taxOffice, instrument, bankAccountNumber)
-- VALUES
--     ('Jane', 'Doe', 'jane.doe@example.com', '{noop}ss', '1985-12-01', '456 Oak Ave', '98765432109', '202', 'CELLO', '12345678901234567890123456'),
--     ('David', 'Lee', 'david.lee@example.com', 'pass456', '1992-08-22', '789 Pine Ln', '56789012345', '303', 'FLUTE', '98765432109876543210987654'),
--     ('Emily', 'Chen', 'emily.chen@example.com', 'pass789', '1988-03-10', '1011 Maple Dr', '34567890123', '404', 'TRUMPET', '56789012345678901234567890'),
--     ('Michael', 'Brown', 'michael.brown@example.com', 'pass101', '1995-11-28', '1213 Cedar Rd', '78901234567', '505', 'PIANO', '10987654321098765432109876'),
--     ('Sarah', 'Jones', 'sarah.jones@example.com', 'pass202', '1983-07-05', '1415 Birch Ct', '10123456789', '606', 'PERCUSSION', '65432109876543210987654321'),
--     ('Robert', 'Garcia', 'robert.garcia@example.com', 'pass303', '1998-01-18', '1617 Willow Pl', '23456789012', '707', 'VIOLA', '21098765432109876543210987'),
--     ('Alice', 'Johnson', 'alice.johnson@example.com', 'passA123', '1987-09-20', '202 Elm St', '11122233344', '808', 'VIOLIN', '87654321098765432109876543'),
--     ('Bob', 'Williams', 'bob.williams@example.com', 'passB456', '1993-04-12', '303 Maple Ave', '55566677788', '909', 'CELLO', '32109876543210987654321098'),
--     ('Carol', 'Davis', 'carol.davis@example.com', 'passC789', '1980-11-03', '404 Oak Ln', '99900011122', '1010', 'FLUTE', '45678901234567890123456789'),
--     ('Dan', 'Rodriguez', 'dan.rodriguez@example.com', 'passD101', '1996-06-25', '505 Pine Dr', '33344455566', '1111', 'TRUMPET', '98765432101234567890123456'),
--     ('Eve', 'Martinez', 'eve.martinez@example.com', 'passE202', '1982-02-18', '606 Cedar Rd', '77788899900', '1212', 'PIANO', '56789012349876543210987654'),
--     ('Frank', 'Wilson', 'frank.wilson@example.com', 'passF303', '1999-10-09', '707 Birch Ct', '11223344556', '1313', 'VIOLIN', '10987654326543210987654321'),
--     ('Grace', 'Anderson', 'grace.anderson@example.com', 'passG404', '1986-08-30', '808 Willow Pl', '66778899001', '1414', 'VIOLA', '65432109872109876543210987'),
--     ('Henry', 'Thomas', 'henry.thomas@example.com', 'passH505', '1991-03-21', '909 Redwood Ln', '22334455667', '1515', 'OBOE', '21098765438765432109876543'),
--     ('Ivy', 'Jackson', 'ivy.jackson@example.com', 'passI606', '1984-07-14', '1010 Spruce Ave', '88990011223', '1616', 'CLARINET', '87654321093210987654321098'),
--     ('Jack', 'White', 'jack.white@example.com', 'passJ707', '1997-12-05', '1111 Sycamore Dr', '44556677889', '1717', 'BASSOON', '32109876549876543210987654'),
--     ('Kelly', 'Harris', 'kelly.harris@example.com', 'passK808', '1981-05-28', '1212 Walnut Rd', '00112233445', '1818', 'TROMBONE', '45678901231234567890123456'),
--     ('Leo', 'Martin', 'leo.martin@example.com', 'passL909', '1994-01-19', '1313 Chestnut Ct', '66778899006', '1919', 'TUBA', '98765432106543210987654321'),
--     ('Mia', 'Thompson', 'mia.thompson@example.com', 'passM101', '1989-09-11', '1414 Ash Pl', '11223344566', '2020', 'VIOLIN', '56789012342109876543210987'),
--     ('Noah', 'Garcia', 'noah.garcia@example.com', 'passN202', '1998-04-02', '1515 Maple Ln', '22334455688', '2121', 'VIOLIN', '10987654328765432109876543'),
--     ('Olivia', 'Perez', 'olivia.perez@example.com', 'passO303', '1983-11-24', '1616 Pine Ave', '33445566778', '2222', 'VIOLA', '65432109873210987654321098');

INSERT INTO App_user (DTYPE, firstName, lastName, email, password, birthdate, address, pesel, taxOffice, instrument, bankAccountNumber)
VALUES
    ('Musician', 'Jane', 'Doe', 'jane.doe@example.com', '{noop}ss', '1985-12-01', '456 Oak Ave', '98765432109', '202', 'CELLO', '12345678901234567890123456'),
    ('Musician', 'David', 'Lee', 'david.lee@example.com', 'pass456', '1992-08-22', '789 Pine Ln', '56789012345', '303', 'FLUTE', '98765432109876543210987654'),
    ('Musician', 'Emily', 'Chen', 'emily.chen@example.com', 'pass789', '1988-03-10', '1011 Maple Dr', '34567890123', '404', 'TRUMPET', '56789012345678901234567890'),
    ('Musician', 'Michael', 'Brown', 'michael.brown@example.com', 'pass101', '1995-11-28', '1213 Cedar Rd', '78901234567', '505', 'PIANO', '10987654321098765432109876'),
    ('Musician', 'Sarah', 'Jones', 'sarah.jones@example.com', 'pass202', '1983-07-05', '1415 Birch Ct', '10123456789', '606', 'PERCUSSION', '65432109876543210987654321'),
    ('Musician', 'Robert', 'Garcia', 'robert.garcia@example.com', 'pass303', '1998-01-18', '1617 Willow Pl', '23456789012', '707', 'VIOLA', '21098765432109876543210987');


INSERT INTO AgreementTemplate (content) VALUES
    ('AGREEMENT\n\nThis agreement is made on ${current.date} between:\n1. The Orchestra Management (hereinafter "Orchestra")\nand\n2. ${musician.fullName}, residing at ${musician.address} (hereinafter "Musician")\n\nRegarding participation in the project: ${project.name}\nProject Dates: ${project.startDate} to ${project.endDate}\nLocation: ${project.location}\n\nWage: ${project.wage} PLN\n\nMusician PESEL (for records): ${musician.pesel}\nPayment will be made to Bank Account: ${musician.bankAccountNumber}\n\n[... Add many more standard clauses here ...]\n\nSigned:\n\n_____________________\nOrchestra Representative\n\n_____________________\nMusician (Acceptance via system)');


INSERT INTO Project (name, description, startDate, endDate, status, agreementTemplate_id)
VALUES
    ('Summer Symphony', 'A summer concert series featuring classical music.', '2024-07-01', '2024-07-31', 'ACTIVE', 1),
    ('Jazz Nights', 'Weekly jazz performances at the local club.', '2024-08-05', '2024-08-26', 'ACTIVE', 1),
    ('Rock Festival', 'A weekend-long rock music festival with various bands.', '2024-09-15', '2024-09-17', 'ACTIVE', 1);


INSERT INTO ChatMessage (senderId, receiverId, messageContent, timestamp) VALUES
    (3, 4, 'Hello, how are you?', '2023-10-26 10:00:00'),
    (4, 3, 'I am doing well, thanks!', '2023-10-26 10:05:00'),
    (3, 4, 'Rehearsal is at 7pm.', '2023-10-26 10:10:00'),
    (4, 3, 'Got it!', '2023-10-26 10:15:00'),
    (4, 3, 'Anyone seen my music sheets?', '2023-10-26 10:20:00'),
    (3, 4, 'No, sorry. Check the library.', '2023-10-26 10:25:00');
