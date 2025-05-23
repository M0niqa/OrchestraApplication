DROP TABLE IF EXISTS app_user_roles CASCADE;
DROP TABLE IF EXISTS userrole CASCADE;
DROP TABLE IF EXISTS chatmessage CASCADE;
DROP TABLE IF EXISTS musicscore CASCADE;
DROP TABLE IF EXISTS project CASCADE;
DROP TABLE IF EXISTS agreementtemplate CASCADE;
DROP TABLE IF EXISTS app_user CASCADE;
DROP TABLE IF EXISTS survey CASCADE;

CREATE TABLE agreementtemplate (
                                   id BIGSERIAL PRIMARY KEY,
                                   content TEXT
);

CREATE TABLE project (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         description TEXT,
                         startDate DATE,
                         endDate DATE,
                         location VARCHAR(255),
                         conductor VARCHAR(255),
                         programme TEXT,
                         agreementtemplate_id BIGINT,
                         survey_id BIGINT,
                         invitationDeadline TIMESTAMP,
                         FOREIGN KEY (agreementtemplate_id) REFERENCES agreementtemplate(id) ON DELETE SET NULL
);

CREATE TABLE survey (
                        id BIGSERIAL PRIMARY KEY,
                        project_id BIGINT NOT NULL UNIQUE,
                        closed BOOLEAN NOT NULL,
                        FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

ALTER TABLE project
    ADD CONSTRAINT fk_project_survey_id
        FOREIGN KEY (survey_id) REFERENCES survey(id)
            ON DELETE SET NULL;

CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          dtype VARCHAR(255),
                          firstname VARCHAR(255),
                          lastname VARCHAR(255),
                          email VARCHAR(255) UNIQUE,
                          password VARCHAR(255),
                          birthdate DATE,
                          address VARCHAR(255),
                          taxoffice VARCHAR(255),
                          bankaccountnumber VARCHAR(255),
                          pesel VARCHAR(11) UNIQUE,
                          instrument VARCHAR(255)
);

CREATE TABLE musicscore (
                            id BIGSERIAL PRIMARY KEY,
                            filename VARCHAR(255) NOT NULL,
                            filetype VARCHAR(100) NOT NULL,
                            filepath VARCHAR(512) NOT NULL,
                            project_id BIGINT NOT NULL,
                            CONSTRAINT fk_music_score_project
                                FOREIGN KEY (project_id)
                                    REFERENCES project(id)
                                    ON DELETE CASCADE
);

CREATE TABLE chatmessage (
                             id BIGSERIAL PRIMARY KEY,
                             senderid BIGINT,
                             receiverid BIGINT,
                             messagecontent TEXT,
                             timestamp TIMESTAMP,
                             read BOOLEAN DEFAULT FALSE
);

CREATE TABLE userrole (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) UNIQUE,
                          description VARCHAR(255)
);

CREATE TABLE app_user_roles (
                                user_id BIGINT,
                                roles_id BIGINT,
                                FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                                FOREIGN KEY (roles_id) REFERENCES userrole(id) ON DELETE CASCADE,
                                PRIMARY KEY (user_id, roles_id)
);

CREATE TABLE musicianagreement (
                                   id BIGSERIAL PRIMARY KEY,
                                   fileName VARCHAR(255),
                                   fileType VARCHAR(255),
                                   filePath VARCHAR(255),
                                   createdAt TIMESTAMP,
                                   user_id BIGINT NOT NULL,
                                   project_id BIGINT NOT NULL,

                                   FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                                   FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE TABLE token (
                       id BIGSERIAL PRIMARY KEY,
                       token VARCHAR(255),
                       email VARCHAR(255),
                       expiryDate TIMESTAMP WITHOUT TIME ZONE
);

