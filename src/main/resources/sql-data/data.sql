INSERT INTO app_user (DTYPE, firstName, lastName, email, password, instrument)
VALUES
    -- smith@gmail.com / 2222
    ('User', 'John', 'Smith', 'smith@gmail.com', '{noop}2222'),
    -- alex@pja.edu.pl / 2222
    ('Musician','Alex', 'King', 'alex@pja.edu.pl',
     '{MD5}{rT4wBLjXEvWpB8F0MgvLUDt8v36HSZoTWcDiusl7jh4=}fda9321dca602a2bb6c42955a315a3fa', 'TUBA'),
    -- sj@my.com / 2222
    ('Musician', 'Stephen', 'Jones', 'monikwor91@gmail.com', '{noop}2222', 'OBOE');

INSERT INTO userrole (name, description)
VALUES ('ADMIN', 'Adding/updating projects'),
       ('INSPECTOR', 'Inviting musicians to projects'),
       ('MUSICIAN', 'Regular user privileges');

INSERT INTO app_user (DTYPE, firstName, lastName, email, password, birthdate, address, pesel, instrument, bankAccountNumber)
VALUES
    ('Musician', 'Jane', 'Doe', 's26004@pjwstk.edu.pl', '{noop}ss', '1985-12-01', '456 Oak Ave', '44051401458', 'CELLO', '12345678901234567890123456'),
    ('Musician', 'David', 'Lee', 'david.lee@example.com', '{noop}pass456', '1992-08-22', '789 Pine Ln', '56789012345', 'FLUTE', '98765432109876543210987654'),
    ('Musician', 'Emily', 'Chen', 'emily.chen@example.com', 'pass789', '1988-03-10', '1011 Maple Dr', '34567890123', 'TRUMPET', '56789012345678901234567890'),
    ('Musician', 'Michael', 'Brown', 'michael.brown@example.com', 'pass101', '1995-11-28', '1213 Cedar Rd', '78901234567', 'PIANO', '10987654321098765432109876'),
    ('Musician', 'Sarah', 'Jones', 'sarah.jones@example.com', '{noop}pass202', '1983-07-05', '1415 Birch Ct', '10123456789', 'PERCUSSION', '65432109876543210987654321'),
    ('Musician', 'Robert', 'Garcia', 'robert.garcia@example.com', 'pass303', '1998-01-18', '1617 Willow Pl', '23456789012', 'VIOLA', '21098765432109876543210987'),
    ('Musician', 'Anna', 'Kowalska', 'anna.kowalska@example.com', '$2a$10$exampleHash1', '1990-07-15', 'Kraków, ul. Wiślana 10', '90071512345', 'VIOLIN_I', 'PL123456789012345678901234'),
    ('Musician', 'Piotr', 'Nowak', 'piotr.nowak@example.com', '$2a$10$exampleHash2', '1988-03-20', 'Warszawa, al. Niepodległości 25', '88032054321', 'VIOLIN_I', 'PL987654321098765432109876'),
    ('Musician', 'Maria', 'Wójcik', 'maria.wojcik@example.com', '$2a$10$exampleHash3', '1995-11-01', 'Łódź, ul. Piotrkowska 100', '95110167890', 'VIOLIN_I', 'PL555555555555555555555555'),
    ('Musician', 'Jan', 'Lewandowski', 'jan.lewandowski@example.com', '$2a$10$exampleHash4', '1982-05-10', 'Poznań, ul. Stary Rynek 5', '82051023456', 'VIOLIN_I', 'PL112233445566778899001122'),
    ('Musician', 'Katarzyna', 'Kamińska', 'katarzyna.kaminska@example.com', '$2a$10$exampleHash5', '1993-09-22', 'Wrocław, pl. Solny 1', '93092278901', 'VIOLIN_I', 'PL444444444444444444444444'),
    ('Musician', 'Tomasz', 'Zieliński', 'tomasz.zielinski@example.com', '$2a$10$exampleHash6', '1987-12-05', 'Gdańsk, ul. Długi Targ 40', '87120534567', 'VIOLIN_I', 'PL666666666666666666666666'),
    ('Musician', 'Magdalena', 'Szymańska', 'magdalena.szymanska@example.com', '$2a$10$exampleHash7', '1991-06-30', 'Szczecin, al. Niepodległości 1', '91063089012', 'VIOLIN_I', 'PL777777777777777777777777'),
    ('Musician', 'Grzegorz', 'Woźniak', 'grzegorz.wozniak@example.com', '$2a$10$exampleHash8', '1985-01-18', 'Lublin, ul. Krakowskie Przedmieście 15', '85011845678', 'VIOLIN_I', 'PL888888888888888888888888'),
    ('Musician', 'Ewa', 'Jankowska', 'ewa.jankowska@example.com', '$2a$10$exampleHash9', '1997-04-12', 'Bydgoszcz, ul. Długa 20', '97041290123', 'VIOLIN_I', 'PL999999999999999999999999'),
    ('Musician', 'Krzysztof', 'Mazur', 'krzysztof.mazur@example.com', '$2a$10$exampleHash10', '1983-11-28', 'Katowice, ul. 3 Maja 30', '83112856789', 'VIOLIN_I', 'PL000000000000000000000000'),
    ('Musician', 'Sylwia', 'Lis', 'sylwia.lis@example.com', '$2a$10$exampleHash11', '1992-08-05', 'Kraków, ul. Karmelicka 22', '92080523456', 'VIOLIN_II', 'PL222222222222222222222222'),
    ('Musician', 'Adam', 'Dąbrowski', 'adam.dabrowski@example.com', '$2a$10$exampleHash12', '1989-02-14', 'Warszawa, ul. Marszałkowska 100', '89021467890', 'VIOLIN_II', 'PL333333333333333333333333'),
    ('Musician', 'Monika', 'Grabowska', 'monika.grabowska@example.com', '$2a$10$exampleHash13', '1996-12-10', 'Łódź, al. Kościuszki 50', '96121078901', 'VIOLIN_II', 'PL111111111111111111111111'),
    ('Musician', 'Marcin', 'Pawlak', 'marcin.pawlak@example.com', '$2a$10$exampleHash14', '1981-06-25', 'Poznań, ul. Półwiejska 12', '81062534567', 'VIOLIN_II', 'PL445566778899001122334455'),
    ('Musician', 'Joanna', 'Michalska', 'joanna.michalska@example.com', '$2a$10$exampleHash15', '1994-10-08', 'Wrocław, ul. Świdnicka 40', '94100889012', 'VIOLIN_II', 'PL667788990011223344556677'),
    ('Musician', 'Łukasz', 'Wróbel', 'lukasz.wrobel@example.com', '$2a$10$exampleHash16', '1986-09-01', 'Gdańsk, ul. Mariacka 2', '86090145678', 'VIOLIN_II', 'PL889900112233445566778899'),
    ('Musician', 'Natalia', 'Krajewska', 'natalia.krajewska@example.com', '$2a$10$exampleHash17', '1998-01-20', 'Szczecin, pl. Lotników 1', '98012090123', 'VIOLIN_II', 'PL001122334455667788990011'),
    ('Musician', 'Daniel', 'Górski', 'daniel.gorski@example.com', '$2a$10$exampleHash18', '1984-07-03', 'Lublin, ul. Zamojska 30', '84070356789', 'VIOLIN_II', 'PL223344556677889900112233'),
    ('Musician', 'Patrycja', 'Adamczyk', 'patrycja.adamczyk@example.com', '$2a$10$exampleHash19', '1999-03-15', 'Bydgoszcz, ul. Mostowa 5', '99031512340', 'VIOLIN_II', 'PL445566778899001122334455'),
    ('Musician', 'Robert', 'Sikora', 'robert.sikora@example.com', '$2a$10$exampleHash20', '1980-10-12', 'Katowice, al. Korfantego 1', '80101267891', 'VIOLIN_II', 'PL667788990011223344556677'),
    ('Musician', 'Karol', 'Wiśniewski', 'karol.wisniewski@example.com', '$2a$10$exampleHash21', '1992-05-01', 'Kraków, ul. Floriańska 45', '92050178902', 'CELLO', 'PL120000000000000000000001'),
    ('Musician', 'Agata', 'Pawłowska', 'agata.pawlowska@example.com', '$2a$10$exampleHash22', '1997-11-10', 'Warszawa, ul. Krucza 50', '97111023451', 'FLUTE', 'PL340000000000000000000002'),
    ('Musician', 'Michał', 'Olszewski', 'michal.olszewski@example.com', '$2a$10$exampleHash23', '1983-08-22', 'Łódź, ul. Zielona 70', '83082289010', 'PIANO', 'PL560000000000000000000003'),
    ('Musician', 'Zuzanna', 'Dudek', 'zuzanna.dudek@example.com', '$2a$10$exampleHash24', '1999-02-28', 'Poznań, ul. Wrocławska 20', '99022834569', 'CLARINET', 'PL780000000000000000000004'),
    ('Musician', 'Jakub', 'Kowalczyk', 'jakub.kowalczyk@example.com', '$2a$10$exampleHash25', '1986-04-15', 'Wrocław, ul. Oławska 10', '86041590128', 'TRUMPET', 'PL900000000000000000000005');

INSERT INTO app_user_roles (user_id, roles_id)
VALUES (1, 1),
       (2, 2),
       (3, 1),
       (4, 2),
       (5, 3),
       (6, 3),
       (7, 3),
       (8, 3),
       (9, 3),
       (10, 3),
       (11, 3),
       (12, 3),
       (13, 3),
       (14, 3),
       (15, 3),
       (16, 3),
       (17, 3),
       (18, 3),
       (19, 3),
       (20, 3),
       (21, 3),
       (22, 3),
       (23, 3),
       (24, 3),
       (25, 3),
       (26, 3),
       (27, 3),
       (28, 3),
       (29, 3),
       (30, 3),
       (31, 3),
       (32, 3),
       (33, 3),
       (34, 3);

INSERT INTO agreementtemplate (content) VALUES
    ('Work Contract with the Transfer of Rights to Artistic Performance,
concluded in Kraków on ${agreementDate}, between:
[Ordering Party''s name], hereinafter referred to as the "ORDERING PARTY",
and ${musician.fullName}, hereinafter referred to as the "Performer", residing at ${musician.address},
with the following content:

§1

1. The Ordering Party commissions, and the Performer agrees, that as an instrumental musician specializing in ${musician.instrument}, they will personally prepare and perform their part, marked in the score by the composer or arranger as ${musician.instrument}, together with other artists of [Orchestra Name], creating an artistic performance of the following musical works:
${project.programme},
under the direction of maestro ${project.conductor}.
2. The artistic performance of the above-mentioned works will be presented live during the concert on ${project.endDate} at ${project.location}.
3. The performance may be recorded in audio or audio-video format for purposes specified in this agreement, depending on the Ordering Party’s decision.
4. The artistic performance referred to in paragraph 1 shall hereinafter be referred to as the “work” or “works.”
5. The Performer agrees to prepare their designated part of the musical work individually, using the provided sheet music, to the best of their knowledge and ability.
6. The Performer will also follow the instructions of the Ordering Party regarding the entirety of the performance, including collaboration with persons indicated by the Ordering Party, such as the Conductor, Concertmaster, Section Leader, or the Artistic Director of [Orchestra''s name] (or their designee). These instructions may be given in any form, including verbally during rehearsals (the detailed rehearsal schedule will be included in the work plan posted at [Orchestra''s website]).
7. The performance shall be deemed accepted by the Ordering Party upon its execution.

§2

1. The Performer declares they have received all necessary information from the Ordering Party to execute the subject of this Agreement.
2. The Performer confirms they have the skills and knowledge required to deliver the performance to the highest known and available professional standards and guarantees their best preparation.
3. The Performer agrees not to harm the positive image and reputation of the Beethoven Academy Orchestra in connection with the execution of this agreement. This includes proper preparation and adherence to venue regulations during rehearsals and concerts.

§3 – Licensing and Image Rights

1. The Performer grants the Ordering Party an exclusive, unlimited (in territory and time) license to use the rights to the artistic performance co-created with [Orchestra''s name], including but not limited to:
    a. Public performance (live), playback (using any media), broadcasting (via any technique), retransmission, temporary or permanent storage on devices for on-demand access;
    b. Archival recording of the performance, in whole or in part;
    c. Use of fragments for reports and promotional broadcasts;
    d. Radio, TV, and online transmission or retransmission of the performance.
2. The license is granted upon acceptance (recording) of the performance.
3. The Performer authorizes the Ordering Party to use their name, image, and informational materials (e.g., photos, bios) for promotional and informational purposes.
4. The Performer agrees to participate in standard promotional activities such as photo sessions, press conferences, interviews, and use of performance fragments for media coverage.
5. The Performer confirms they are authorized to grant such rights and accepts liability for any third-party claims.
6. All decisions regarding the use of the performance are at the sole discretion of the Ordering Party.
7. The Performer shall not act in any way that would harm the image of the Orchestra.
8. The Performer waives enforcement of personal rights in a way that would limit the Ordering Party’s license.
9. The Performer waives the right to terminate the agreement based on Articles 57, 58, and 68 in conjunction with Article 92 of the Polish Copyright Act.
10. The Performer waives the use of collective rights management organizations.
11. The Performer agrees to include references to the [Orchestra''s name] in any personal promotional materials related to this event on social media.

RECORDINGS
(Repeat of licensing terms from §3, adapted to recording use)
1. The Performer grants the Ordering Party an exclusive, unlimited (in territory and time) license to use the rights to the artistic performance co-created with [Orchestra''s name], including but not limited to:
    a. Public performance (live), playback (using any media), broadcasting (via any technique), retransmission, temporary or permanent storage on devices for on-demand access;
    b. Archival recording of the performance, in whole or in part;
    c. Use of fragments for reports and promotional broadcasts;
    d. Radio, TV, and online transmission or retransmission of the performance.
2. The license is granted upon acceptance (recording) of the performance.
3. All decisions regarding the use of the recording are at the sole discretion of the Ordering Party.

§4 – Compensation

1. The Performer’s total gross fee for the artistic performance and transfer of rights is: ${wage} PLN, which equals ${wageNet} PLN net.
2. Payment will be made via bank transfer to:
    Account Holder: ${musician.fullName}
    Bank Account Number: ${musician.bankAccountNumber}
3. The fee will be paid net of applicable taxes by ${paymentDeadline}, upon issuance of an invoice by the Performer.
4. The Performer authorizes the Ordering Party to prepare the invoice.
5. The Ordering Party will cover the cost of sheet music rental.
6. Lodging costs will be covered under a separate agreement.
7. The Ordering Party will cover meal costs, arranged as needed.

§5 – Penalties

1. For non-performance or improper performance, the Performer may be fined 30% of the gross amount specified in §4.
2. If the performance harms the image or reputation of the Orchestra, especially violations of §2.3, the penalty is 50%.
3. These penalties do not exclude the right to claim full damages.
4. Canceling less than 7 days before the concert date, which is after ${resignationPenaltyDate} results in a 50% penalty.
5. In cases of force majeure, each party bears its own costs.
6. For COVID-19-related cancellations, procedures must be followed, and the Performer waives the right to claim payment.

§6 – Final Provisions

1. The Ordering Party may transfer the rights defined in §3 to third parties.
2. This contract is confidential and considered a trade secret.
3. All amendments require written form.
4. Matters not regulated herein are governed by Polish Civil Code and Copyright Law.
5. Disputes shall be resolved by courts in the jurisdiction of the Ordering Party.
6. The contract is made in two identical copies, one for each party.
Performer: ____________________________
Ordering Party: ________________________



Invoice

Date: ${invoiceData}

Regarding agreement dated: ${agreementDate}

Performer: ${musician.fullName}, pesel:${musician.pesel}

Address: ${musician.address}

Ordering party: [ORDERING PARTY]

Gross invoice amount: ${wage} zł

Costs of obtaining revenue 50%: ${costOfIncome} zł

Tax: 12%: ${tax} zł

Amount to pay: ${wageNet} zł

Please transfer the above amount to my account: ${musician.bankAccountNumber}
Paid by transfer on: __________________
Performer: ____________________________
Ordering Party: ________________________');


INSERT INTO project (name, description, startDate, endDate, agreementTemplate_id)
VALUES
    ('FMF', 'A film music.', '2025-05-21', '2025-05-31', 1),
    ('Summer Symphony', 'A summer concert series featuring classical music with summer theme.', '2025-09-01', '2025-08-31', 1),
    ('Jazz Nights', 'Weekly jazz performances at the local club.', '2025-04-23', '2025-04-28', 1),
    ('Rock Festival', 'A weekend-long rock music festival with various bands.', '2025-09-15', '2025-09-17', 1),
    ('Mazowsze and friends', 'A folk music.', '2025-04-11', '2025-04-15', 1);


INSERT INTO ChatMessage (senderId, receiverId, messageContent, timestamp, read) VALUES
                                                                                    (3, 4, 'Hello, how are you Jane?', '2023-10-26 10:00:00', true),
                                                                                    (4, 3, 'I am doing well, thanks Stephen!', '2023-10-26 10:05:00', true),
                                                                                    (3, 4, 'Rehearsal is at 7pm.', '2023-10-26 10:10:00', true),
                                                                                    (4, 3, 'Got it!', '2023-10-26 10:15:00', true),
                                                                                    (4, 3, 'Anyone seen my music sheets?', '2023-10-26 10:20:00', true),
                                                                                    (3, 4, 'No, sorry. Check the library.', '2023-10-26 10:25:00', true),
                                                                                    (3, 5, 'Hey David!', '2023-10-26 10:25:00', true),
                                                                                    (5, 3, 'Hello Stephen!', '2023-10-26 10:25:00', true);

-- Musician 2 invited to Project 1
INSERT INTO invited_musicians_projects (musician_id, project_id) VALUES (3, 2);
INSERT INTO invited_musicians_projects (musician_id, project_id) VALUES (3, 3);

-- Musician 2 accepted Project 2
INSERT INTO accepted_musicians_projects (musician_id, project_id) VALUES
                                                                      (3, 5),
                                                                      (4, 1),
                                                                      (5, 1),
                                                                      (6, 1),
                                                                      (7, 1),
                                                                      (8, 1),
                                                                      (9, 1),
                                                                      (10, 1),
                                                                      (11, 1),
                                                                      (12, 1),
                                                                      (13, 1),
                                                                      (14, 1),
                                                                      (15, 1),
                                                                      (16, 1),
                                                                      (17, 1),
                                                                      (18, 1),
                                                                      (19, 1),
                                                                      (20, 1),
                                                                      (21, 1),
                                                                      (22, 1),
                                                                      (23, 1),
                                                                      (24, 1),
                                                                      (25, 1),
                                                                      (26, 1),
                                                                      (27, 1),
                                                                      (28, 1),
                                                                      (29, 1),
                                                                      (30, 1),
                                                                      (31, 1),
                                                                      (32, 1),
                                                                      (33, 1),
                                                                      (34, 1),
                                                                      (19, 2);

-- Musician 3 rejected Project 3
INSERT INTO rejected_musicians_projects (musician_id, project_id) VALUES (3, 3);
