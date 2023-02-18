--
-- Structure for table helpdesk_faq
--
DROP TABLE IF EXISTS helpdesk_faq;
CREATE TABLE helpdesk_faq (
  id_faq int NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT '' NOT NULL,
  description long varchar NOT NULL,
  role_key varchar(50) DEFAULT '' NOT NULL,
  workgroup_key varchar(50) DEFAULT '' NOT NULL,
  PRIMARY KEY  (id_faq)
);


--
-- Structure for table helpdesk_subject
--
DROP TABLE IF EXISTS helpdesk_subject;
CREATE TABLE helpdesk_subject (
  id_subject int NOT NULL AUTO_INCREMENT,
  subject varchar(50) DEFAULT '' NOT NULL,
  id_parent int NOT NULL,
  id_order int NOT NULL,
  PRIMARY KEY  (id_subject),
  CONSTRAINT fk_helpdesk_subject_parent FOREIGN KEY (id_parent) REFERENCES helpdesk_subject(id_subject)
);


--
-- Structure for table helpdesk_theme
--
DROP TABLE IF EXISTS helpdesk_theme;
CREATE TABLE helpdesk_theme (
  id_theme int NOT NULL AUTO_INCREMENT,
  theme varchar(50) DEFAULT '' NOT NULL,
  id_mailing_list varchar(50) DEFAULT '' NOT NULL,
  id_parent int NOT NULL,
  id_order int NOT NULL,
  PRIMARY KEY  (id_theme),
  CONSTRAINT fk_helpdesk_theme_parent FOREIGN KEY (id_parent) REFERENCES helpdesk_theme(id_theme)
);


--
-- Structure for table helpdesk_ln_faq_subject
--
DROP TABLE IF EXISTS helpdesk_ln_faq_subject;
CREATE TABLE helpdesk_ln_faq_subject (
  id_faq int NOT NULL,
  id_subject int NOT NULL,
  PRIMARY KEY  (id_faq, id_subject),
  CONSTRAINT fk_helpdesk_faq_subject_faq FOREIGN KEY (id_faq) REFERENCES helpdesk_faq(id_faq),
  CONSTRAINT fk_helpdesk_faq_subject_topic FOREIGN KEY (id_subject) REFERENCES helpdesk_subject(id_subject)
);


--
-- Structure for table helpdesk_ln_faq_theme
--
DROP TABLE IF EXISTS helpdesk_ln_faq_theme;
CREATE TABLE helpdesk_ln_faq_theme (
  id_faq int NOT NULL,
  id_theme int NOT NULL,
  PRIMARY KEY  (id_faq, id_theme),
  CONSTRAINT fk_helpdesk_faq_theme_faq FOREIGN KEY (id_faq) REFERENCES helpdesk_faq(id_faq),
  CONSTRAINT fk_helpdesk_faq_theme_theme FOREIGN KEY (id_theme) REFERENCES helpdesk_theme(id_theme)
);


--
-- Structure for table helpdesk_question_answer
--
DROP TABLE IF EXISTS helpdesk_question_answer;
CREATE TABLE helpdesk_question_answer (
  id_question_answer int NOT NULL AUTO_INCREMENT,
  question long varchar NOT NULL,
  answer long varchar NOT NULL,
  id_subject int NOT NULL,
  status int DEFAULT 0 NOT NULL,
  creation_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
  id_order int DEFAULT 0 NOT NULL,
  PRIMARY KEY  (id_question_answer),
  CONSTRAINT fk_helpdesk_question_answer FOREIGN KEY (id_subject) REFERENCES helpdesk_subject(id_subject)
);


--
-- Structure for table helpdesk_visitor_question
--
DROP TABLE IF EXISTS helpdesk_visitor_question;
CREATE TABLE helpdesk_visitor_question (
  id_visitor_question int NOT NULL AUTO_INCREMENT,
  last_name varchar(50) DEFAULT '' NOT NULL,
  first_name varchar(50) DEFAULT '' NOT NULL,
  email varchar(80) DEFAULT NULL,
  question long varchar NOT NULL,
  answer long varchar NOT NULL,
  date_visitor_question date NOT NULL,
  id_user int NOT NULL,
  id_theme int NOT NULL, 
  PRIMARY KEY  (id_visitor_question),
  CONSTRAINT fk_helpdesk_visitor_question FOREIGN KEY (id_theme) REFERENCES helpdesk_theme(id_theme)
);