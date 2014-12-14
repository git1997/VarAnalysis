<?php

 if($_SESSION['userid'] == "" || $_SESSION['usertype'] != 'Admin')
 {
  die("Invalid User!");
 }
 $page2 = $_POST["page2"];

 print("<script language='JavaScript'>
  function logoutAdmin()
  {
	document.admin.logout.value=1;
	document.admin.submit();
  }

  function classes()
  {
	document.admin.page2.value=0;
	document.admin.submit();
  }

  function schoolInfo()
  {
	document.admin.page2.value=1;
	document.admin.submit();
  }

  function students()
  {
	document.admin.page2.value=2;
	document.admin.submit();
  }

  function teachers()
  {
	document.admin.page2.value=3;
	document.admin.submit();
  }

  function announcements()
  {
	document.admin.page2.value=4;
	document.admin.submit();
  }

  function semesters()
  {
	document.admin.page2.value=5;
	document.admin.submit();
  }

  function terms()
  {
	document.admin.page2.value=6;
	document.admin.submit();
  }

  function users()
  {
	document.admin.page2.value=10;
	document.admin.submit();
  }

  function parents()
  {
	document.admin.page2.value=22;
	document.admin.submit();
  }

  function register()
  {
	document.admin.page2.value=26;
	document.admin.submit();
  }

  function attendance()
  {
	document.admin.page2.value=30;
	document.admin.submit();
  }
 </script>

 <body>");

 include("maketop.php");

 print("
 <tr>
  <td class='b' width=130 valign='top'>
   <br>
   <form name='admin' action='./index.php' method='POST'>

   <a class='menu' href='javascript: schoolInfo();' onMouseover=\"window.status='Manage School Information'; return true;\" onMouseout=\"window.status=''; return true;\">School</a>
   <br><br>
   <a class='menu' href='javascript: terms();' onMouseover=\"window.status='Manage Terms'; return true;\" onMouseout=\"window.status=''; return true;\">Terms</a>
   <br><br>
   <a class='menu' href='javascript: semesters();' onMouseover=\"window.status='Manage Semesters'; return true;\" onMouseout=\"window.status=''; return true;\">Semesters</a>
   <br><br>
   <a class='menu' href='javascript: classes();' onMouseover=\"window.status='Manage Classes'; return true;\" onMouseout=\"window.status=''; return true;\">Classes</a>
   <br><br>
   <a class='menu' href='javascript: users();' onMouseover=\"window.status='Manage Users'; return true;\" onMouseout=\"window.status=''; return true;\">Users</a>
   <br><br>
   <a class='menu' href='javascript: teachers();' onMouseover=\"window.status='Manage Teachers'; return true;\" onMouseout=\"window.status=''; return true;\">Teachers</a>
   <br><br>
   <a class='menu' href='javascript: students();' onMouseover=\"window.status='Manage Students'; return true;\" onMouseout=\"window.status=''; return true;\">Students</a>
   <br><br>
   <a class='menu' href='javascript: register();' onMouseover=\"window.status='Register Students for Classes'; return true;\" onMouseout=\"window.status=''; return true;\">Registration</a>
   <br><br>
   <a class='menu' href='javascript: attendance();' onMouseover=\"window.status='Keep Attendance'; return true;\" onMouseout=\"window.status=''; return true;\">Attendance</a>
   <br><br>
   <a class='menu' href='javascript: parents();' onMouseover=\"window.status='Manage Parents'; return true;\" onMouseout=\"window.status=''; return true;\">Parents</a>
   <br><br>
   <a class='menu' href='javascript: announcements();' onMouseover=\"window.status='Manage Announcements'; return true;\" onMouseout=\"window.status=''; return true;\">Announcements</a>
   <br><br>
   <a class='menu' href='javascript: logoutAdmin();' onMouseover=\"window.status='Log Out';return true;\" onMouseout=\"window.status='';return true;\">Log Out</a>

   <input type='hidden' name='page2' value='$page2'>
   <input type='hidden' name='logout'>
   <input type='hidden' name='page' value='$page'>
 </form>
  </td>
  <td class='b' width=10 background='./images/left.gif'><div style='letter-spacing: 1pt;'>&nbsp;</div></td>
  <td class='w' valign='top'>
   <table border=0 cellspacing=0 cellpadding=10 width='100%' height='100%'>
	<tr>
	 <td valign='top'>");

	 switch($page2)
	 {

	  case 0:
			 require_once("ManageClasses.php");
			 break;

	  case 1:
			 require_once("ManageSchoolInfo.php");
			 break;

	  case 2:
			 require_once("ManageStudents.php");
			 break;

	  case 3:
			 require_once("ManageTeachers.php");
			 break;

	  case 4:
			 require_once("ManageAnnouncements.php");
			 break;

	  case 5:
			 require_once("ManageSemesters.php");
			 break;

	  case 6:
			 require_once("ManageTerms.php");
			 break;

	  case 7:
			 require_once("AddSemester.php");
			 break;

	  case 8:
			 require_once("AddTerm.php");
			 break;

	  case 9:
			 require_once("AddClass.php");
			 break;

	  case 10:
			 require_once("ManageUsers.php");
			 break;

	  case 11:
			 require_once("EditClass.php");
			 break;

	  case 12:
			 require_once("EditTerm.php");
			 break;

	  case 13:
			 require_once("EditSemester.php");
			 break;

	  case 14:
			 require_once("AddUser.php");
			 break;

	  case 15:
			 require_once("EditUser.php");
			 break;

	  case 16:
			 require_once("AddTeacher.php");
			 break;

	  case 17:
			 require_once("EditTeacher.php");
			 break;

	  case 18:
			 require_once("AddAnnouncements.php");
			 break;

	  case 19:
			 require_once("EditAnnouncements.php");
			 break;

	  case 20:
			 require_once("AddStudent.php");
			 break;

	  case 21:
			 require_once("EditStudent.php");
			 break;

	  case 22:
			 require_once("ManageParents.php");
			 break;

	  case 23:
			 require_once("AddParent.php");
			 break;

	  case 24:
			 require_once("EditParent.php");
			 break;

	  case 25:
			 require_once("VisualizeClasses.php");
			 break;

	  case 26:
			 require_once("Registration.php");
			 break;

	  case 27:
			 require_once("DeficiencyReport.php");
			 break;

	  case 28:
			 require_once("GradeReport.php");
			 break;

	  case 29:
			 require_once("VisualizeRegistration.php");
			 break;

	  case 30:
			 require_once("ManageAttendance.php");
			 break;

	  case 31:
			 require_once("AddAttendance.php");
			 break;

	  case 32:
			  require_once("PointsReport.php");
			  break;

	  default:
			  print("AdminMain.php: Invalid Page");
			  break;
	 }

print(" 	 </td>
	</tr>
   </table>

  </td>");


?>