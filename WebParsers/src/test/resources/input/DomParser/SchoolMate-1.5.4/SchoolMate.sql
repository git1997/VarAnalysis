##################################
# MySQL Databases For SchoolMate #
##################################

DROP DATABASE IF EXISTS schoolmate;

CREATE DATABASE schoolmate;

USE schoolmate;

#
# Structure for table adminstaff : 
#

DROP TABLE IF EXISTS adminstaff;

CREATE TABLE adminstaff (
  adminid int(11) NOT NULL auto_increment,
  userid int(11) NOT NULL default '0',
  fname varchar(20) NOT NULL default '',
  lname varchar(15) NOT NULL default '',
  PRIMARY KEY  (adminid),
  UNIQUE KEY UserID (userid)
) TYPE=MyISAM;

#
# Structure for table assignments : 
#

DROP TABLE IF EXISTS assignments;

CREATE TABLE assignments (
  assignmentid int(11) NOT NULL auto_increment,
  courseid int(11) NOT NULL default '0',
  semesterid int(11) NOT NULL default '0',
  termid int(11) NOT NULL default '0',
  title varchar(15) NOT NULL default '',
  totalpoints double(6,2) NOT NULL default '0.00',
  assigneddate date NOT NULL default '0000-00-00',
  duedate date NOT NULL default '0000-00-00',
  assignmentinformation text,
  PRIMARY KEY  (assignmentid)
) TYPE=MyISAM;

#
# Structure for table courses : 
#

DROP TABLE IF EXISTS courses;

CREATE TABLE courses (
  courseid int(11) NOT NULL auto_increment,
  semesterid int(11) NOT NULL default '0',
  termid int(11) NOT NULL default '0',
  coursename varchar(20) NOT NULL default '',
  teacherid int(11) NOT NULL default '0',
  sectionnum varchar(15) NOT NULL default '0',
  roomnum varchar(5) NOT NULL default '',
  periodnum char(3) NOT NULL default '',
  q1points double(6,2) NOT NULL default '0.00',
  q2points double(6,2) NOT NULL default '0.00',
  totalpoints double(6,2) NOT NULL default '0.00',
  aperc double(6,3) NOT NULL default '0.000',
  bperc double(6,3) NOT NULL default '0.000',
  cperc double(6,3) NOT NULL default '0.000',
  dperc double(6,3) NOT NULL default '0.000',
  fperc double(6,3) NOT NULL default '0.000',
  dotw varchar(5) default NULL,
  substituteid int(11) default NULL,
  secondcourseid int(11) default NULL,
  PRIMARY KEY  (courseid)
) TYPE=MyISAM;

#
# Structure for table grades : 
#

DROP TABLE IF EXISTS grades;

CREATE TABLE grades (
  gradeid int(11) NOT NULL auto_increment,
  assignmentid int(11) NOT NULL default '0',
  courseid int(11) NOT NULL default '0',
  semesterid int(11) NOT NULL default '0',
  termid int(11) NOT NULL default '0',
  studentid int(11) NOT NULL default '0',
  points double(6,2) default '0.00',
  comment text,
  submitdate date default '0000-00-00',
  islate int(1) default '0',
  PRIMARY KEY  (gradeid)
) TYPE=MyISAM;

#
# Structure for table parent_student_match : 
#

DROP TABLE IF EXISTS parent_student_match;

CREATE TABLE parent_student_match (
  matchid int(11) NOT NULL auto_increment,
  parentid int(11) NOT NULL default '0',
  studentid int(11) NOT NULL default '0',
  PRIMARY KEY  (matchid)
) TYPE=MyISAM;

#
# Structure for table parents : 
#

DROP TABLE IF EXISTS parents;

CREATE TABLE parents (
  parentid int(11) NOT NULL auto_increment,
  userid int(11) NOT NULL default '0',
  fname varchar(15) default NULL,
  lname varchar(15) default NULL,
  PRIMARY KEY  (parentid)
) TYPE=MyISAM;

#
# Structure for table registrations : 
#

DROP TABLE IF EXISTS registrations;

CREATE TABLE registrations (
  regid int(11) NOT NULL auto_increment,
  courseid int(11) NOT NULL default '0',
  studentid int(11) NOT NULL default '0',
  semesterid int(11) NOT NULL default '0',
  termid int(11) NOT NULL default '0',
  q1currpoints double(6,2) NOT NULL default '0.00',
  q2currpoints double(6,2) NOT NULL default '0.00',
  currentpoints double(6,2) NOT NULL default '0.00',
  PRIMARY KEY  (regid)
) TYPE=MyISAM;

#
# Structure for table schoolattendance : 
#

DROP TABLE IF EXISTS schoolattendance;

CREATE TABLE schoolattendance (
  sattendid int(11) NOT NULL auto_increment,
  studentid int(11) NOT NULL default '0',
  sattenddate date NOT NULL default '0000-00-00',
  semesterid int(11) NOT NULL default '0',
  termid int(11) NOT NULL default '0',
  type enum('tardy','absent') default NULL,
  PRIMARY KEY  (sattendid)
) TYPE=MyISAM;

#
# Structure for table schoolbulletins : 
#

DROP TABLE IF EXISTS schoolbulletins;

CREATE TABLE schoolbulletins (
  sbulletinid int(11) NOT NULL auto_increment,
  title varchar(15) NOT NULL default '',
  message text NOT NULL,
  bulletindate date NOT NULL default '0000-00-00',
  PRIMARY KEY  (sbulletinid)
) TYPE=MyISAM;

#
# Structure for table schoolinfo : 
#

DROP TABLE IF EXISTS schoolinfo;

CREATE TABLE schoolinfo (
  schoolname varchar(50) NOT NULL default '',
  address varchar(50) default NULL,
  phonenumber varchar(14) default NULL,
  sitetext text,
  sitemessage text,
  currenttermid int(11) default NULL,
  numsemesters int(3) NOT NULL default '0',
  numperiods int(3) NOT NULL default '0',
  apoint double(6,3) NOT NULL default '0.000',
  bpoint double(6,3) NOT NULL default '0.000',
  cpoint double(6,3) NOT NULL default '0.000',
  dpoint double(6,3) NOT NULL default '0.000',
  fpoint double(6,3) NOT NULL default '0.000',
  PRIMARY KEY  (schoolname)
) TYPE=MyISAM;

#
# Data for table schoolinfo;
#

INSERT INTO schoolinfo VALUES ('School Name','1,
Street','52365895','','This is the Message of the day:-
\r\n\r\nWe think our fathers fools, so wise do we grow,
no doubt our wisest sons would think us
so.',NULL,0,0,0.000,0.000,0.000,0.000,0.000);


#
# Structure for table semesters : 
#

DROP TABLE IF EXISTS semesters;

CREATE TABLE semesters (
  semesterid int(11) NOT NULL auto_increment,
  termid varchar(15) NOT NULL default '',
  title varchar(15) NOT NULL default '',
  startdate date NOT NULL default '0000-00-00',
  midtermdate date NOT NULL default '0000-00-00',
  enddate date NOT NULL default '0000-00-00',
  type enum('1','2') default NULL,
  PRIMARY KEY  (semesterid)
) TYPE=MyISAM;

#
# Structure for table students : 
#

DROP TABLE IF EXISTS students;

CREATE TABLE students (
  studentid int(11) NOT NULL auto_increment,
  userid int(11) NOT NULL default '0',
  fname varchar(15) NOT NULL default '',
  mi char(1) NOT NULL default '',
  lname varchar(15) NOT NULL default '',
  PRIMARY KEY  (studentid),
  UNIQUE KEY UserID (userid)
) TYPE=MyISAM;

#
# Structure for table teachers : 
#

DROP TABLE IF EXISTS teachers;

CREATE TABLE teachers (
  teacherid int(11) NOT NULL auto_increment,
  userid int(11) NOT NULL default '0',
  fname varchar(15) NOT NULL default '',
  lname varchar(15) NOT NULL default '',
  PRIMARY KEY  (teacherid),
  UNIQUE KEY UserID (userid)
) TYPE=MyISAM;

#
# Structure for table terms : 
#

DROP TABLE IF EXISTS terms;

CREATE TABLE terms (
  termid int(11) NOT NULL auto_increment,
  title varchar(15) NOT NULL default '',
  startdate date NOT NULL default '0000-00-00',
  enddate date NOT NULL default '0000-00-00',
  PRIMARY KEY  (termid)
) TYPE=MyISAM;

#
# Structure for table users : 
#

DROP TABLE IF EXISTS users;

CREATE TABLE users (
  userid int(11) NOT NULL auto_increment,
  username varchar(15) NOT NULL default '',
  password varchar(32) NOT NULL default '',
  type enum('Admin','Teacher','Substitute','Student','Parent') NOT NULL default 'Admin',
  PRIMARY KEY  (userid),
  UNIQUE KEY username (username)
) TYPE=MyISAM;

#
# Data for table users  (LIMIT 0,500)
#

INSERT INTO users (userid, username, password, type) VALUES 
  (1,'test','098f6bcd4621d373cade4e832627b4f6','Admin');

COMMIT;

