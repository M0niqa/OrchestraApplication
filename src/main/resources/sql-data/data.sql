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

INSERT INTO App_user (DTYPE, firstName, lastName, email, password, birthdate, address, pesel, instrument, bankAccountNumber)
VALUES
    ('Musician', 'Jane', 'Doe', 's26004@pjwstk.edu.pl', '{noop}ss', '1985-12-01', '456 Oak Ave', '98765432109', 'CELLO', '12345678901234567890123456'),
    ('Musician', 'David', 'Lee', 'david.lee@example.com', '{noop}pass456', '1992-08-22', '789 Pine Ln', '56789012345', 'FLUTE', '98765432109876543210987654'),
    ('Musician', 'Emily', 'Chen', 'emily.chen@example.com', 'pass789', '1988-03-10', '1011 Maple Dr', '34567890123', 'TRUMPET', '56789012345678901234567890'),
    ('Musician', 'Michael', 'Brown', 'michael.brown@example.com', 'pass101', '1995-11-28', '1213 Cedar Rd', '78901234567', 'PIANO', '10987654321098765432109876'),
    ('Musician', 'Sarah', 'Jones', 'sarah.jones@example.com', 'pass202', '1983-07-05', '1415 Birch Ct', '10123456789', 'PERCUSSION', '65432109876543210987654321'),
    ('Musician', 'Robert', 'Garcia', 'robert.garcia@example.com', 'pass303', '1998-01-18', '1617 Willow Pl', '23456789012', 'VIOLA', '21098765432109876543210987');

INSERT INTO App_user_roles (User_id, roles_id)
VALUES (1, 3),
       (2, 2),
       (3, 3),
       (3, 1),
       (4, 3);

INSERT INTO AgreementTemplate (content) VALUES
    ('Work Contract with the transfer of rights to artistic performance, concluded in Kraków on ${agreementDate}, between:
[Ordering Party''s name], hereinafter referred to as the "ORDERING PARTY",
and ${musician.fullName}, hereinafter referred to as the "Performer", residing at ${musician.address},
with the following content:

§1
The Ordering Party commissions, and the Performer agrees, that as an instrumental musician specializing in ${musician.instrument}, they will personally prepare and perform their part, marked in the score by the composer or arranger as ${musician.instrument}, together with other artists of [Orchestra Name], creating an artistic performance of the following musical works:
${project.programme},
under the direction of maestro ${project.conductor}.

The artistic performance of the above-mentioned works will be presented live during the concert on ${project.endDate} at ${project.location}.
The performance may be recorded in audio or audio-video format for purposes specified in this agreement, depending on the Ordering Party’s decision.
The artistic performance referred to in paragraph 1 shall hereinafter be referred to as the “work” or “works.”

The Performer agrees to prepare their designated part of the musical work individually, using the provided sheet music, to the best of their knowledge and ability. The Performer will also follow the instructions of the Ordering Party regarding the entirety of the performance, including collaboration with persons indicated by the Ordering Party, such as the Conductor, Concertmaster, Section Leader, or the Artistic Director of [Orchestra''s name] (or their designee). These instructions may be given in any form, including verbally during rehearsals (the detailed rehearsal schedule will be included in the work plan posted at [Orchestra''s website]).
The performance shall be deemed accepted by the Ordering Party upon its execution.

§2
The Performer declares they have received all necessary information from the Ordering Party to execute the subject of this Agreement.
The Performer confirms they have the skills and knowledge required to deliver the performance to the highest known and available professional standards and guarantees their best preparation.
The Performer agrees not to harm the positive image and reputation of the Beethoven Academy Orchestra in connection with the execution of this agreement. This includes proper preparation and adherence to venue regulations during rehearsals and concerts.

§3 – Licensing and Image Rights
The Performer grants the Ordering Party an exclusive, unlimited (in territory and time) license to use the rights to the artistic performance co-created with [Orchestra''s name] , including but not limited to:
a. Public performance (live), playback (using any media), broadcasting (via any technique), retransmission, temporary or permanent storage on devices for on-demand access;
b. Archival recording of the performance, in whole or in part;
c. Use of fragments for reports and promotional broadcasts;
d. Radio, TV, and online transmission or retransmission of the performance.

The license is granted upon acceptance (recording) of the performance.
The Performer authorizes the Ordering Party to use their name, image, and informational materials (e.g., photos, bios) for promotional and informational purposes.
The Performer agrees to participate in standard promotional activities such as photo sessions, press conferences, interviews, and use of performance fragments for media coverage.
The Performer confirms they are authorized to grant such rights and accepts liability for any third-party claims.
All decisions regarding the use of the performance are at the sole discretion of the Ordering Party.
The Performer shall not act in any way that would harm the image of the Orchestra.
The Performer waives enforcement of personal rights in a way that would limit the Ordering Party’s license.
The Performer waives the right to terminate the agreement based on Articles 57, 58, and 68 in conjunction with Article 92 of the Polish Copyright Act.
The Performer waives the use of collective rights management organizations.
The Performer agrees to include references to the [Orchestra''s name] in any personal promotional materials related to this event on social media.

RECORDINGS
(Repeat of licensing terms from §3, adapted to recording use)

§4 – Compensation
The Performer’s total gross fee for the artistic performance and transfer of rights is: ${wage} PLN, which equals ${wageNet} PLN net.

Payment will be made via bank transfer to:
Account Holder: ${musician.fullName}
Bank Account Number: ${musician.bankAccountNumber}

The fee will be paid net of applicable taxes by ${paymentDeadline}, upon issuance of an invoice by the Performer.
The Performer authorizes the Ordering Party to prepare the invoice.
The Ordering Party will cover the cost of sheet music rental.
Lodging costs will be covered under a separate agreement.
The Ordering Party will cover meal costs, arranged as needed.

§5 – Penalties
For non-performance or improper performance, the Performer may be fined 30% of the gross amount specified in §4.
If the performance harms the image or reputation of the Orchestra, especially violations of §2.3, the penalty is 50%.
These penalties do not exclude the right to claim full damages.
Canceling less than 7 days before the concert date, which is after ${resignationPenaltyDate} results in a 50% penalty.
In cases of force majeure, each party bears its own costs.
For COVID-19-related cancellations, procedures must be followed, and the Performer waives the right to claim payment.

§6 – Final Provisions
The Ordering Party may transfer the rights defined in §3 to third parties.
This contract is confidential and considered a trade secret.
All amendments require written form.
Matters not regulated herein are governed by Polish Civil Code and Copyright Law.
Disputes shall be resolved by courts in the jurisdiction of the Ordering Party.
The contract is made in two identical copies, one for each party.

Performer: ____________________________
Ordering Party: _______________________');


INSERT INTO Project (name, description, startDate, endDate, agreementTemplate_id)
VALUES
    ('FMF', 'A film music.', '2025-05-01', '2025-07-31', 1),
    ('Summer Symphony', 'A summer concert series featuring classical music with summer theme.', '2025-09-01', '2025-08-31', 1),
    ('Jazz Nights', 'Weekly jazz performances at the local club.', '2025-04-23', '2025-04-28', 1),
    ('Rock Festival', 'A weekend-long rock music festival with various bands.', '2025-09-15', '2025-09-17', 1);


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
INSERT INTO invited_musicians_projects (musician_id, project_id) VALUES (3, 1);

-- Musician 2 accepted Project 2
INSERT INTO accepted_musicians_projects (musician_id, project_id) VALUES (3, 2);

-- Musician 2 rejected Project 3
INSERT INTO rejected_musicians_projects (musician_id, project_id) VALUES (3, 3);
