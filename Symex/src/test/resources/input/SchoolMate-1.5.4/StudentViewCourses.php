<?php
 $query = mysql_query("SELECT studentid, fname, lname FROM students WHERE userid = $_SESSION[userid]") or die("ViewCourses.php: Unable to get the studentid - ".mysql_error());
 $studentid = mysql_fetch_row($query);

 print(" <h1>$studentid[1] $studentid[2]'s Classes</h1>
 <br><br>
 <table align='center' width='300' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='classes' action='./index.php' method='POST'>
 <b>Semester: </b> <select name='semester' onChange='document.classes.submit();'>
");
  // Get a list of semesters //
  $query = mysql_query("SELECT semesterid, title FROM semesters ORDER BY startdate DESC")
	or die("ViewCourses.php: Unable to get a list of semesters for drop-down - ".mysql_error());

  if($_POST['semester']==NULL)
  {
	$q = mysql_query("SELECT semesterid FROM semesters WHERE startdate < CURDATE() < enddate");
	$temp = mysql_fetch_row($q);
	$_POST['semester'] = $temp[0];
  }

  $count=0;

  while($semester = mysql_fetch_row($query))
  {
   print("<option value='$semester[0]' ".( $_POST['semester']==$semester[0]&&$_POST['semester']!=NULL ? "SELECTED" : "").">$semester[1]</option>");
  }


  print("
	 </select>
	 <br><br>
  </td>
 </tr>
 <tr>
 <td>
  <table cellspacing='0' width='300' cellpadding='5' class='dynamiclist' align='center'>
   <tr class='header'>
	<th>Class Name</th>
   </tr>
   ");
 // Get the classes //
 if($_POST['semester']!=NULL)
 {
 $query = mysql_query("SELECT DISTINCT courseid FROM registrations WHERE studentid = $studentid[0]")
   or die("ViewCourses.php: Unable to get a list of classes - ".mysql_error());

 $row = 0;
 while( $class = mysql_fetch_row($query) )
 {
  $row++;
  $q = mysql_query("SELECT DISTINCT coursename FROM courses WHERE courseid = $class[0] AND semesterid = $_POST[semester]");
  while($info = mysql_fetch_row($q))
  {
   print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
   <td><a class='items' href=\"JavaScript:document.classes.selectclass.value=$class[0];document.classes.page2.value=1;document.classes.submit();\" onMouseover=\"window.status='View Information For $info[0]';return true;\" onMouseout=\"window.status='';return true;\">$info[0]</a></td>
   </tr>");
  }
 }
 }
 print("</table>
   <input type='hidden' name='page2' value='$page2' />
   <input type='hidden' name='logout' />
   <input type='hidden' name='page' value='$page' />
   <input type='hidden' name='selectclass' />
   <input type='hidden' name='studentid' value='$studentid[0]' />
 </td>
 </tr>
 </table>
 </form>

  <table width='520' border=0 cellspacing=0 cellpadding=0 height=1>
  <tr>
   <td valign='top'>
   <empty>
   </td>
  </tr>
 </table>");
?>