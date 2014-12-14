<?php
print("
 <h1>Deficiency Report</h1>
 <table align='center' width='400' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='classes' action='./index.php' method='POST'>

 <table border='0' width='400'>
 <tr>
 <td>
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

  while($semester = mysql_fetch_row($query))
  {
   print("<option value='$semester[0]' ".( $_POST['semester']==$semester[0]&&$_POST['semester']!=NULL ? "SELECTED" : "").">$semester[1]</option>");
  }

  print("
   <option value='-1' ".($_POST['semester']==-1 ? "SELECTED" : "").">All</option>
	 </select>
<br>
 </td>
 </tr>
 </table>

  <table cellspacing='0' width='400' class='dynamiclist'>
  <tr class='header' align='left'>
   <th style='padding-left: 10px;'>Student</th>
   <th>Class</th>
   <th>Percentage</th>
  </tr>");

 // Get the list of defficient students //
 $query = mysql_query("SELECT studentid, courseid FROM registrations ORDER BY courseid")
   or die("DeficiencyReport: Unable to get a list of deficient students - ".mysql_error());

 $row=0;
 while($deficient = mysql_fetch_row($query))
 {
  // Make sure we're in the right semester //
  $q = mysql_query("SELECT semesterid FROM courses WHERE courseid = $deficient[1]");
  $semesterid = mysql_fetch_row($q);

  // If dispalying all semesters, make sure $semesterid always equals $_POST['semester'] //
  if($_POST['semester']==-1)
    $semesterid[0]=-1;

  if($semesterid[0] != $_POST['semester'])
	continue;

  $q = mysql_query("SELECT fname,lname FROM students WHERE studentid = $deficient[0]");
  $student = mysql_fetch_row($q);

  $q = mysql_query("SELECT coursename, totalpoints, cperc FROM courses WHERE courseid = $deficient[1]");
  $class = mysql_fetch_row($q);

  $q = mysql_query("SELECT currentpoints FROM registrations WHERE studentid = $deficient[0] AND courseid = $deficient[1]");
  $currentpoints = mysql_fetch_row($q);

  if($class[1] >  0)
   $percentage = number_format($currentpoints[0]/$class[1],2);
  else
   $percentage = "0.00";

  $percentage *= 100;

  if($percentage >= $class[2] )
	continue;
  $row++;
  print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
   <td align='left' style='padding-left: 10px;'>$student[0] $student[1]</td>
   <td align='left'>$class[0]</td>
   <td align='left'>$percentage&#37;</td>
  </tr>
  ");
 }

print("  </table>
  <br />
  <input type='button' value=' Back ' onClick='document.classes.page2.value=2;document.classes.submit();'>
  <input type='hidden' name='page2' value='$page2'>
  <input type='hidden' name='logout'>
  <input type='hidden' name='page' value='$page'>
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