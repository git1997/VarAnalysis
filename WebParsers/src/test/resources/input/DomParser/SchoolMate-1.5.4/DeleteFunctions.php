<?php
##############
#   GRADES   #
##############
// Function to delete grades //
function deleteGrade($gradeid)
{
 $query = mysql_query("DELETE FROM grades WHERE gradeid = '$gradeid' LIMIT 1")
   or die("DeleteFunctions.php: Unable to delete selected grade(s) - ".mysql_error());
}

##################
#   ATTENDANCE   #
##################
// Function to delete absences/tardies //
function deleteAttendance($attendid)
{
 $query = mysql_query("DELETE FROM schoolattendance WHERE sattendid = $attendid LIMIT 1")
   or die("DeleteFunctions.php: Unable to delete selected Attendanc(es) - ".mysql_error());
}

###############
#   PARENTS   #
###############
// Function to delete Parents //
function deleteParent($parentid)
{
 $query = mysql_query("DELETE FROM parents WHERE parentid = $parentid LIMIT 1")
   or die("DeleteFunctions.php: Unable to delete selected Parent(s) - ".mysql_error());

 $query = mysql_query("DELETE FROM parent_student_match WHERE parentid = $parentid")
   or die("DeleteFunctions.php: Unable to delete the parents in the student/parent match - ".mysql_error());
}

#####################
#   ANNOUNCEMENTS   #
#####################
// Function to delete Announcements //
function deleteAnnouncement($announcementid)
{
 $query = mysql_query("DELETE FROM schoolbulletins WHERE sbulletinid = $announcementid LIMIT 1")
   or die("DeleteFunctions.php: Unable to delete selected Announcement(s) - ".mysql_error());
}

################
#   TEACHERS   #
################
// Function to delete Teachers //
function deleteTeacher($teacherid)
{
 $query = mysql_query("DELETE FROM teachers WHERE teacherid = $teacherid LIMIT 1")
   or die("DeleteFunctions.php: Unable to delete selected Teacher(s) - ".mysql_error());
}

################
#   STUDENTS   #
################
// Function to delete Students //
function deleteStudent($studentid)
{
 $query = mysql_query("DELETE FROM students WHERE studentid = $studentid LIMIT 1")
   or die("DeleteFunctions.php: Unable to delete selected Student(s) - ".mysql_error());

 // Delete the registrations for the student //
 mysql_query("DELETE FROM registrations WHERE studentid = $studentid");

 // Delete the grades for the student //
 mysql_query("DELETE FROM grades WHERE studentid = $studentid");

 // Delete the parent-student link //
 mysql_query("DELETE FROM parent_student_match WHERE studentid = $studentid");
}

##############
#   ADMINS   #
##############
// Function to delete Admins //
function deleteAdmin($adminid)
{
 $query = mysql_query("DELETE FROM adminstaff WHERE adminid = $adminid LIMIT 1")
   or die("DeleteFunctions.php: Unable to delete selected Admin(s) - ".mysql_error());
}


#############
#   USERS   #
#############
// Function to delete Users //
function deleteUser($userid)
{
 $query = mysql_query("SELECT type FROM users WHERE userid = $userid");
 $type = @mysql_result($query,0);

 $query = mysql_query("DELETE FROM users WHERE userid = $userid LIMIT 1")
   or die("DeleteFunction.php: Unable to delete selected User(s) - ".mysql_error());

 switch($type)
 {
  case "Teacher":
				// Get the list of teachers/substitutes that will also be deleted and delete them //
				$query = mysql_query("SELECT teacherid FROM teachers WHERE userid = $userid")
				 or die("DeleteFunctions.php: Unable to get list of teachers to delete for deleteUser() - " . mysql_error());

				while( $teacherid = mysql_fetch_row($query) )
				{
				 deleteTeacher($teacherid[0]);
				}
  break;

  case "Student":
				// Get the list of students that will also be deleted and delete them //
				$query = mysql_query("SELECT studentid FROM students WHERE userid = $userid")
				 or die("DeleteFunctions.php: Unable to get list of students to delete for deleteUser() - " . mysql_error());

				while( $studentid = @mysql_result($query,0) )
				{
				 deleteStudent($studentid);
				}
  break;

  case "Parent":
			 // Get the list of parents that will also be deleted and delete them //
			 $query = mysql_query("SELECT parentid FROM parents WHERE userid = $userid")
				 or die("DeleteFunctions.php: Unable to get list of parents to delete for deleteUser() - " . mysql_error());

			 while( $parentid = @mysql_result($query,0) )
			 {
				deleteParent($parentid);
			 }
 	break;

  case "Admin":
			 // Get the list of administration staff that will also be deleted and delete them //
			 $query = mysql_query("SELECT adminid FROM adminstaff WHERE userid = $userid")
			 or die("DeleteFunctions.php: Unable to get list of admins to delete for deleteUser() - " . mysql_error());

			 while( $adminid = @mysql_result($query,0) )
			 {
			deleteAdmin($adminid);
			 }
  break;
 }
}

#####################
#   REGISTRATIONS   #
#####################
// Function to delete Registrations //
function deleteRegistration($regid)
{
 // First get some info for later use //
 $query = mysql_query("SELECT courseid FROM registrations WHERE regid = $regid");
 $courseid = mysql_fetch_row($query);
 $courseid = $courseid[0];

 $query = mysql_query("SELECT studentid FROM registrations WHERE regid = $regid");
 $studentid = mysql_fetch_row($query);
 $studentid = $studentid[0];

 // Delete the registration //
 $query = mysql_query("DELETE FROM registrations WHERE regid = $regid LIMIT 1")
	or die("DeleteFunctions.php: Unable to delete selected Registration(s) - " . mysql_error());

 // Get a list of the grades //
 mysql_query("DELETE FROM grades WHERE courseid = $courseid AND studentid = $studentid");
}


###################
#   ASSIGNMENTS   #
###################
// Function to delete Assignments //
function deleteAssignments($assignid)
{

 // First get info for later use //
 $q1 = mysql_query("SELECT courseid FROM assignments WHERE assignmentid = $assignid");
 $courseid = mysql_fetch_row($q1);
 $courseid = $courseid[0];

 // Delete the assignment //
 $query = mysql_query("DELETE FROM assignments WHERE assignmentid = $assignid LIMIT 1")
	or die("DeleteFunctions.php: Unable to delete selected CourseBulletin(s) - " . mysql_error());

 // Get the list of grades for that assignment //
 mysql_query("DELETE FROM grades WHERE assignmentid = $assignid");
}

###############
#   COURSES   #
###############
// Function to Delete the selected Course(s) and their associated information //
function deleteCourse($courseid)
{
 $query = mysql_query('DELETE FROM courses WHERE courseid = '.$courseid);
 $query = mysql_query('DELETE FROM registrations WHERE courseid = '.$couseid);
 $query = mysql_query('DELETE FROM assignments WHERE courseid = '.$courseid);
 $query = mysql_query('DELETE FROM grades WHERE courseid = '.$courseid);
}


#################
#   SEMESTERS   #
#################
// Function to delete semesters and their associated information //
function deleteSemester($semesterid)
{
 $query = mysql_query('DELETE FROM semesters WHERE semesterid = '.$semesterid);
 $query = mysql_query('DELETE FROM courses WHERE semesterid = '.$semesterid);
 $query = mysql_query('DELETE FROM grades WHERE semesterid = '.$semesterid);
 $query = mysql_query('DELETE FROM assignments WHERE semesterid = '.$semesterid);
 $query = mysql_query('DELETE FROM schoolattendance WHERE semesterid = '.$semesterid);
 $query = mysql_query('DELETE FROM registrations WHERE semesterid = '.$semesterid);
}

#############
#   TERMS   #
#############
// Function to delete terms and their associated information //
function deleteTerm($termid)
{
 $query = mysql_query('DELETE FROM terms WHERE termid = '.$termid);
 $query = mysql_query('DELETE FROM semesters WHERE termid = '.$termid);
 $query = mysql_query('DELETE FROM courses WHERE termid = '.$termid);
 $query = mysql_query('DELETE FROM grades WHERE termid = '.$termid);
 $query = mysql_query('DELETE FROM assignments WHERE termid = '.$termid);
 $query = mysql_query('DELETE FROM schoolattendance WHERE termid = '.$termid);
 $query = mysql_query('DELETE FROM registrations WHERE termid = '.$termid);
}
?>
