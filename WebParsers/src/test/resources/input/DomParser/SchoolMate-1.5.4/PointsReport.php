<?php

print("
 <h1>Points Report</h1>
 <br>
 <table align='center' width='600' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='classes' action='./index.php' method='POST'>
 <b>Semester: </b> <select name='semester' onChange='document.classes.submit();'>");
 // Get a list of semesters //
  $query = mysql_query("SELECT semesterid, title FROM semesters")
	or die("PointsReport.php: Unable to get a list of semesters for drop-down - ".mysql_error());

  if($_POST['semester']==NULL)
  {
	$q = mysql_query("SELECT semesterid, title FROM semesters WHERE startdate < CURDATE() < enddate");
	$temp = mysql_fetch_row($q);
	$_POST['semester'] = $temp[0];
  }

  while($semester = mysql_fetch_row($query))
  {
   print("<option value='$semester[0]' ".( $_POST['semester']==$semester[0]&&$_POST['semester']!=NULL ? "SELECTED" : "").">$semester[1]</option>");
  }

  print("
	 </select>
 &nbsp;&nbsp;<b>Classes:</b>
 <select name='selectclass' onChange='document.classes.submit();'>");
 if($_POST['semester']!=NULL)
 {
 // Get a list of classes //
 if($_POST['selectclass']==NULL)
 {
  $q = mysql_query("SELECT courseid FROM courses WHERE semesterid = $_POST[semester]");
  $temp = mysql_fetch_row($q);
  $_POST['selectclass']=$temp[0];
 }
 else
 {
  $q = mysql_query("SELECT courseid FROM courses WHERE courseid = $_POST[selectclass] AND semesterid = $_POST[semester]");
  if(mysql_num_rows($q)==0)
  {
  $q = mysql_query("SELECT courseid FROM courses WHERE semesterid = $_POST[semester]");
  $temp = mysql_fetch_row($q);
  $_POST['selectclass']=$temp[0];
  }
 }

 $query = mysql_query("SELECT courseid, coursename FROM courses WHERE semesterid = $_POST[semester]")
   or die("</select>PointsReport.php: Unable to get a list of courses - ".mysql_error());

 while($classes = mysql_fetch_row($query))
 {
  print("<option value='$classes[0]' ".($_POST['selectclass']==$classes[0] ? "SELECTED" : "").">$classes[1]</option>\n");
 }
 }

print("  </select>
  <br><br>
  <table cellspacing='0' width='600' class='dynamiclist'>
  <tr class='header'>
   <th>Student</th>
   <th>Current Points</th>
   <th>Total Points</th>
   <th>Percent</th>
   <th>Grade</th>
  </tr>");

  // Print out the points for each student //
  if($_POST['selectclass']!=NULL)
  {
   $row=0;
   $query = mysql_query("SELECT studentid, currentpoints FROM registrations WHERE courseid = $_POST[selectclass]");
   while($studentinfo = mysql_fetch_row($query))
   {
	$row++;

	// Get Student's Names //
	$q = mysql_query("SELECT fname, lname FROM students WHERE studentid = $studentinfo[0]");
	$name = mysql_fetch_row($q);

	// Get Class's Total Points //
	$q = mysql_query("SELECT totalpoints, aperc, bperc, cperc, dperc, fperc FROM courses WHERE courseid = $_POST[selectclass]");
	$classinfo = mysql_fetch_row($q);

	// Calculate And Display //
	$current = $studentinfo[1];
	$total   = $classinfo[0];

	if($total != 0)
	 $perc    = number_format(100*($current / $total),2);
	else
	 $perc = "0.00";

	if($perc >= $classinfo[1])
	 $grade = "A";
	elseif($perc >= $classinfo[2])
	 $grade = "B";
	elseif($perc >= $classinfo[3])
	 $grade = "C";
	elseif($perc >= $classinfo[4])
	 $grade = "D";
	else
	 $grade = "F";

	print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	<td align='left' style='padding-left: 30px;'>$name[0] $name[1]</td>
	<td>$current</td>
	<td>$total</td>
	<td>$perc&#37;</td>
	<td>$grade</td>");
   }
  }
  else
  {
   print("<tr class='even'><td colspan='5'>There are no students registered for this class.</td></tr>");
  }

 print("  </table>
  <br />
  <input type='button' value=' Back ' onClick='document.classes.page2.value=2;document.classes.submit();'>
  <input type='hidden' name='page2' value='$page2' />
  <input type='hidden' name='logout' />
  <input type='hidden' name='page' value='$page' />
 </form>
 </td>
 </tr>
 </table>
 <table width='520' border=0 cellspacing=0 cellpadding=0 height=1>
  <tr>
   <td valign='top'>
   <empty>
   </td>
  </tr>
 </table>
 ");
?>