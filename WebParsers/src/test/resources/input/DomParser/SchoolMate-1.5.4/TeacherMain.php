<?php

 if($_SESSION['userid'] == "" || $_SESSION['usertype']!='Teacher')
 {
  die("Invalid User!");
 }

 $page2 = $_POST["page2"];

 print("<script language='JavaScript'>

  function classes()
  {
	document.teacher.page2.value=0;
	document.teacher.submit();
  }

  function settings()
  {
	document.teacher.page2.value=1;
	document.teacher.submit();
  }

  function assignments()
  {
	document.teacher.page2.value=2;
	document.teacher.submit();
  }

  function grades()
  {
   document.teacher.page2.value=3;
   document.teacher.submit();
  }

  function announcements()
  {
	document.teacher.page2.value=9;
	document.teacher.submit();
  }

  function students()
  {
	document.teacher.page2.value=8;
	document.teacher.submit();
  }

  function logoutteacher()
  {
	document.teacher.logout.value=1;
	document.teacher.submit();
  }
 </script>

 <body>");

 include("maketop.php");

 print("
 <tr>
  <td class='b' width=130 valign='top'>
   <br>
   <form name='teacher' action='./index.php' method='POST'>

   <a class='menu' href='javascript: classes();' onMouseover=\"window.status='View Classes'; return true;\" onMouseout=\"window.status=''; return true;\">Classes</a>
   <br><br>");

   if($_POST['selectclass'] != "" && $page2 != 0)
   {
	print("
	 <a class='menu' href='javascript: settings();' onMouseover=\"window.status='Manage Class Settings'; return true;\" onMouseout=\"window.status=''; return true;\">Settings</a>
	 <br><br>
	 <a class='menu' href='javascript: assignments();' onMouseover=\"window.status='Manage Assignments'; return true;\" onMouseout=\"window.status=''; return true;\">Assignments</a>
	 <br><br>
	 <a class='menu' href='javascript: grades();' onMouseover=\"window.status='Manage Grades'; return true;\" onMouseout=\"window.status=''; return true;\">Grades</a>
	 <br><br>
	 <a class='menu' href='javascript: students();' onMouseover=\"window.status='View Student Information'; return true;\" onMouseout=\"window.status=''; return true;\">Students</a>
	 <br><br>
	 <a class='menu' href='javascript: announcements();' onMouseover=\"window.status='View Announcements'; return true;\" onMouseout=\"window.status=''; return true;\">Announcements</a>
	 <br><br>");
   }

print("   <a class='menu' href='javascript: logoutteacher();' onMouseover=\"window.status='Log Out';return true;\" onMouseout=\"window.status='';return true;\">Log Out</a>

   <input type='hidden' name='page2' value='$page2'>
   <input type='hidden' name='logout'>
   <input type='hidden' name='page' value='$page'>
   <input type='hidden' name='selectclass' value='$_POST[selectclass]' />
 </form>
  </td>
  <td class='b' width='10' background='./images/left.gif'><div style='letter-spacing: 1pt;'>&nbsp;</div></td>
  <td class='w' valign='top'>
   <table border=0 cellspacing=0 cellpadding=10 width='100%' height='100%'>
	<tr>
	 <td valign='top'>");

	 switch($page2)
	 {
	  case 0:
			 require_once("ViewCourses.php");
			 break;

	  case 1:
			 require_once("ClassSettings.php");
			 break;

	  case 2:
			 require_once("ManageAssignments.php");
			 break;

	  case 3:
			 require_once("ManageGrades.php");
			 break;

	  case 4:
			 require_once("AddAssignment.php");
			 break;

	  case 5:
			 require_once("EditAssignment.php");
			 break;

	  case 6:
			 require_once("AddGrade.php");
			 break;

	  case 7:
			 require_once("EditGrade.php");
			 break;

	  case 8:
			 require_once("ViewStudents.php");
			 break;

	  case 9:
			 require_once("ViewAnnouncements.php");
			 break;

	  default:
			  print("teacherMain.php: Invalid Page");
			  break;
	 }

print(" 	 </td>
	</tr>
   </table>

  </td>");


?>