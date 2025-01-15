CREATE
    EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS current;

CREATE TABLE current.statuses
(
    status_id   serial PRIMARY KEY,
    status_name varchar(15) NOT NULL UNIQUE
);

INSERT INTO current.statuses (status_name)
VALUES ('Lead'),
       ('In work'),
       ('Consultation'),
       ('Save for later'),
       ('Student'),
       ('Archive');

CREATE TABLE current.branches
(
    branch_id      serial PRIMARY KEY,
    branch_name    varchar(255) NOT NULL,
    branch_address varchar(255) NOT NULL
);

INSERT INTO current.branches (branch_name, branch_address)
VALUES ('Rehovot', 'Rehovot,Herzl st.1-25'),
       ('Haifa', 'Haifa,Herzl st.1-25'),
       ('Tel Aviv', 'Tel Aviv,Herzl st.1-25');

CREATE TABLE current.courses
(
    course_id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_name         varchar(255) NOT NULL,
    course_abbreviation varchar(8)   NOT NULL
);

INSERT INTO current.courses (course_name, course_abbreviation)
VALUES ('Full stack development', 'FSD'),
       ('Quality assurance', 'QA'),
       ('Base Programming', 'BP');

CREATE TABLE current.contacts
(
    contact_id       uuid PRIMARY KEY default uuid_generate_v4(),
    contact_name     varchar(255) NOT NULL,
    phone            varchar(15)  NOT NULL,
    email            varchar(255) NOT NULL,
    status_id        int          NOT NULL,
    FOREIGN KEY (status_id) REFERENCES current.statuses (status_id),
    branch_id        int          NOT NULL,
    FOREIGN KEY (branch_id) REFERENCES current.branches (branch_id),
    target_course_id uuid         NOT NULL,
    FOREIGN KEY (target_course_id) REFERENCES current.courses (course_id),
    comment          varchar(255)
);

CREATE TABLE current.students
(
    student_id       uuid PRIMARY KEY default uuid_generate_v4(),
    contact_name     varchar(255) NOT NULL,
    phone            varchar(15)  NOT NULL,
    email            varchar(255) NOT NULL,
    status_id        int          NOT NULL,
    FOREIGN KEY (status_id) REFERENCES current.statuses (status_id),
    branch_id        int          NOT NULL,
    FOREIGN KEY (branch_id) REFERENCES current.branches (branch_id),
    target_course_id uuid         NOT NULL,
    FOREIGN KEY (target_course_id) REFERENCES current.courses (course_id),
    comment          varchar(255),
    full_payment     decimal(7,2)  NOT NULL,
    documents_done   boolean      NOT NULL
);

CREATE TABLE current.payment_types
(
    payment_type_id   serial PRIMARY KEY,
    payment_type_name varchar(255) NOT NULL
);

INSERT INTO current.payment_types (payment_type_name)
VALUES ('Credit card'),
       ('Cash'),
       ('Cheque'),
       ('Online transfer');

CREATE TABLE current.payment_information
(
    payment_id      uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id      uuid,
    FOREIGN KEY (student_id) REFERENCES current.students (student_id),
    payment_date    date NOT NULL,
    payment_type_id int  NOT NULL,
    FOREIGN KEY (payment_type_id) REFERENCES current.payment_types (payment_type_id),
    payment_amount  decimal(7,2)  NOT NULL,
    payment_details varchar(255)
);

CREATE TABLE current.groups
(
    group_id         uuid PRIMARY KEY default uuid_generate_v4(),
    group_name       varchar(25)  NOT NULL,
    start_date       date         NOT NULL,
    finish_date      date         NOT NULL,
    course_id        uuid         NOT NULL,
    FOREIGN KEY (course_id) REFERENCES current.courses (course_id),
    slack_link       varchar(255) NOT NULL,
    whats_app_link   varchar(255) NOT NULL,
    skype_link       varchar(255) NOT NULL,
    deactivate_after boolean      NOT NULL
);

CREATE TABLE current.students_by_group
(
    student_id uuid    NOT NULL,
    FOREIGN KEY (student_id) REFERENCES current.students (student_id),
    group_id   uuid    NOT NULL,
    FOREIGN KEY (group_id) REFERENCES current.groups (group_id),
    PRIMARY KEY (student_id, group_id),
    is_active  boolean NOT NULL
);

CREATE TABLE current.lecturers
(
    lecturer_id   uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    lecturer_name varchar(255) NOT NULL,
    phone         varchar(15)  NOT NULL,
    email         varchar(255) NOT NULL,
    branch_id     int          NOT NULL,
    FOREIGN KEY (branch_id) REFERENCES current.branches (branch_id),
    comment       varchar(255)
);

CREATE TABLE current.lecturers_by_group
(
    lecturer_id   uuid    NOT NULL,
    FOREIGN KEY (lecturer_id) REFERENCES current.lecturers (lecturer_id),
    group_id      uuid    NOT NULL,
    FOREIGN KEY (group_id) REFERENCES current.groups (group_id),
    PRIMARY KEY (lecturer_id, group_id),
    is_webinarist boolean NOT NULL,
    is_active     boolean NOT NULL
);

CREATE TABLE current.weekdays
(
    weekday_id   serial PRIMARY KEY,
    weekday_name varchar(10) NOT NULL
);

INSERT INTO current.weekdays (weekday_name)
VALUES ('Monday'),
       ('Tuesday'),
       ('Wednesday'),
       ('Thursday'),
       ('Friday'),
       ('Saturday'),
       ('Sunday');

CREATE TABLE current.webinars_by_weekday
(
    group_id   uuid NOT NULL,
    FOREIGN KEY (group_id) REFERENCES current.groups (group_id),
    weekday_id int  NOT NULL,
    FOREIGN KEY (weekday_id) REFERENCES current.weekdays (weekday_id),
    PRIMARY KEY (group_id, weekday_id)
);

CREATE TABLE current.lessons_by_weekday
(
    group_id   uuid NOT NULL,
    FOREIGN KEY (group_id) REFERENCES current.groups (group_id),
    weekday_id int  NOT NULL,
    FOREIGN KEY (weekday_id) REFERENCES current.weekdays (weekday_id),
    PRIMARY KEY (group_id, weekday_id)
);

CREATE SCHEMA archive;

CREATE TABLE archive.contacts
(
    contact_id            uuid PRIMARY KEY default uuid_generate_v4(),
    contact_name          varchar(255) NOT NULL,
    phone                 varchar(15)  NOT NULL,
    email                 varchar(255) NOT NULL,
    status_id             int          NOT NULL,
    FOREIGN KEY (status_id) REFERENCES current.statuses (status_id),
    branch_id             int          NOT NULL,
    FOREIGN KEY (branch_id) REFERENCES current.branches (branch_id),
    target_course_id      uuid         NOT NULL,
    FOREIGN KEY (target_course_id) REFERENCES current.courses (course_id),
    comment               varchar(255),
    reason_of_archivation varchar(100) NOT NULL,
    archivation_date      date         NOT NULL
);

CREATE TABLE archive.students
(
    student_id            uuid PRIMARY KEY default uuid_generate_v4(),
    contact_name          varchar(255) NOT NULL,
    phone                 varchar(15)  NOT NULL,
    email                 varchar(255) NOT NULL,
    status_id             int          NOT NULL,
    FOREIGN KEY (status_id) REFERENCES current.statuses (status_id),
    branch_id             int          NOT NULL,
    FOREIGN KEY (branch_id) REFERENCES current.branches (branch_id),
    target_course_id      uuid         NOT NULL,
    FOREIGN KEY (target_course_id) REFERENCES current.courses (course_id),
    comment               varchar(255),
    full_payment          decimal(7,2)  NOT NULL,
    documents_done        boolean      NOT NULL,
    reason_of_archivation varchar(100) NOT NULL,
    archivation_date      date         NOT NULL
);

CREATE TABLE archive.payment_information
(
    payment_id      uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id      uuid,
    FOREIGN KEY (student_id) REFERENCES archive.students (student_id),
    payment_date    date NOT NULL,
    payment_type_id int  NOT NULL,
    FOREIGN KEY (payment_type_id) REFERENCES current.payment_types (payment_type_id),
    payment_amount  decimal(7,2)  NOT NULL,
    payment_details varchar(255)
);

CREATE TABLE archive.groups
(
    group_id         uuid PRIMARY KEY default uuid_generate_v4(),
    group_name       varchar(25)  NOT NULL,
    start_date       date         NOT NULL,
    finish_date      date         NOT NULL,
    course_id        uuid         NOT NULL,
    FOREIGN KEY (course_id) REFERENCES current.courses (course_id),
    slack_link       varchar(255) NOT NULL,
    whats_app_link   varchar(255) NOT NULL,
    skype_link       varchar(255) NOT NULL,
    deactivate_after boolean      NOT NULL,
    archivation_date date         NOT NULL
);

CREATE TABLE archive.students_by_group
(
    student_id       uuid    NOT NULL,
    group_id         uuid    NOT NULL,
    PRIMARY KEY (student_id, group_id),
    is_active        boolean NOT NULL,
    archivation_date date    NOT NULL
);

CREATE TABLE archive.lecturers
(
    lecturer_id           uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    lecturer_name         varchar(255) NOT NULL,
    phone                 varchar(15)  NOT NULL,
    email                 varchar(255) NOT NULL,
    branch_id             int          NOT NULL,
    FOREIGN KEY (branch_id) REFERENCES current.branches (branch_id),
    comment               varchar(255),
    reason_of_archivation varchar(100) NOT NULL,
    archivation_date      date         NOT NULL
);

CREATE TABLE archive.lecturers_by_group
(
    lecturer_id      uuid    NOT NULL,
    group_id         uuid    NOT NULL,
    PRIMARY KEY (lecturer_id, group_id),
    is_webinarist    boolean NOT NULL,
    is_active        boolean NOT NULL
);