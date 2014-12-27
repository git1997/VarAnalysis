<?php

 if($_SESSION['userid'] == "")
 {
  die("Invalid User!");
 }

 $page2 = $_POST["page2"];

 print("<script language='JavaScript'>

  function classes()
  {
	document.student.page2.value=0;
	document.student.submit();
  }

  function settings()
  {
	document.student.page2.value=1;
	document.student.submit();
  }

  function assignments()
  {
	document.student.page2.value=2;
	document.student.submit();
  }

  function grades()
  {
   document.student.page2.value=3;
   document.student.submit();
  }

  function announcements()
  {
	document.student.page2.value=4;
	document.student.submit();
  }

  function logoutstudent()
  {
	document.student.logout.value=1;
	document.student.submit();
  }
 </script>

 <body>");

 include("maketop.php");

 print("
 <tr>
  <td class='b' width=130 valign='top'>
   <br>
   <form name='student' action='./index.php' method='POST'>

   <a class='menu' href='javascript: classes();' onMouseover=\"window.status='View Classes'; return true;\" onMouseout=\"window.status=''; return true;\">Classes</a>
   <br><br>");

   if($_POST['selectclass'] != "" && $page2 != 0)
   {
	print("
	 <a class='menu' href='javascript: settings();' onMouseover=\"window.status='View Class Settings'; return true;\" onMouseout=\"window.status=''; return true;\">Settings</a>
	 <br><br>
	 <a class='menu' href='javascript: assignments();' onMouseover=\"window.status='View Assignments'; return true;\" onMouseout=\"window.status=''; return true;\">Assignments</a>
	 <br><br>
	 <a class='menu' href='javascript: grades();' onMouseover=\"window.status='View Grades'; return true;\" onMouseout=\"window.status=''; return true;\">Grades</a>
	 <br><br>
	 <a class='menu' href='javascript: announcements();' onMouseover=\"window.status='View Announcements'; return true;\" onMouseout=\"window.status=''; return true;\">Announcements</a>
	 <br><br>");
   }

print("   <a class='menu' href='javascript: logoutstudent();' onMouseover=\"window.status='Log Out';return true;\" onMouseout=\"window.status='';return true;\">Log Out</a>

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
			 require_once("StudentViewCourses.php");
			 break;

	  case 1:
			 require_once("ViewClassSettings.php");
			 break;

	  case 2:
			 require_once("ViewAssignments.php");
			 break;

	  case 3:
			 require_once("ViewGrades.php");
			 break;

	  case 4:
			 require_once("ViewAnnouncements.php");
			 break;

	  default:
			  print("StudentMain.php: Invalid Page");
			  break;
	 }

print("      </td>
	</tr>
   </table>

  </td>");


?>