-- liquibase formatted sql
-- changeset helpdesk:update_db_helpdesk-2.1.2-2.1.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Table helpdesk_question_answer
--

ALTER TABLE helpdesk_question_answer ADD COLUMN id_order int DEFAULT 0 NOT NULL;
