drop table musicscore;

CREATE TABLE musicscore (
                             id BIGSERIAL PRIMARY KEY,
                             fileName VARCHAR(255) NOT NULL,
                             fileType VARCHAR(100) NOT NULL,
                             filePath VARCHAR(512) NOT NULL,
                             project_id BIGINT NOT NULL,
                             CONSTRAINT fk_music_score_project
                                 FOREIGN KEY (project_id)
                                     REFERENCES project(id)
                                     ON DELETE CASCADE
)