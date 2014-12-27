<?php

$query = mysql_query("SELECT fname, lname FROM students WHERE studentid = '$_POST[student]'")
  or die("VisualizeRegistration: Unable to get the student's name - ".mysql_error());

$name  = mysql_fetch_row($query);

$query = mysql_query("SELECT title FROM semesters WHERE semesterid = $_POST[semester]");
$title = mysql_fetch_row($query);
print("
 <h1>$name[0] $name[1]'s Schedule</h1>
 <table align='center' width='600' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='classes' action='./index.php' method='POST'>
  <b>Semester:</b> $title[0]
  <table cellspacing='0' width='600' cellpadding='5' border='1' align='center' bordercolor='black' cellspacing='0' cellpadding='5'>
  <tr class='header'>
   <th width='10'>Period</th>
   <th>Monday</th>
   <th>Tuesday</th>
   <th>Wednesday</th>
   <th>Thursday</th>
   <th>Friday</th>
  </tr>
   ");
 // Get the total number of periods, so we know how big to make the table //
 $query = mysql_query("SELECT numperiods FROM schoolinfo")
   or die("VisualizeRegistration.php: Unable to get number of periods - ".mysql_error());

 $numperiods = mysql_result($query,0);

 for($i=1; $i<=$numperiods; $i++)
 {
  // Clear re-used variables for the new row //
  $tablerow = "";
  $monday = "<td align='left'>";
  $tuesday = "<td align='left'>";
  $wednesday = "<td align='left'>";
  $thursday = "<td align='left'>";
  $friday = "<td align='left'>";

  print("<tr class='".( $i%2==0 ? "even" : "odd" )."' valign='top'>
  <td>$i</td>");

  // Get the list of courses the student is registered for //
  $sql = mysql_query("SELECT courseid FROM registrations WHERE studentid = '$_POST[student]'")
	or die("VisualizeRegistration.php: Unable to get a list of CourseIDs for the student's registration - ".mysql_error());

  while($id = mysql_fetch_row($sql))
  {
  // Get the information about the classes for the current period //
  $query = mysql_query("SELECT coursename, teacherid, sectionnum, roomnum, periodnum, dotw FROM courses WHERE periodnum = '$i' AND courseid = '$id[0]' AND semesterid = '$_POST[semester]'")
	or die("VisualizeRegistration.php: Unable to get class information - ".mysql_error());

  while( $class = mysql_fetch_row($query) )
  {
	$days = preg_split('//', $class[5], -1, PREG_SPLIT_NO_EMPTY);

	for($j=0; $j<count($days); $j++)

	switch($days[$j])
	{
	 case 'M':
		   $q = mysql_query("SELECT fname, lname FROM teachers WHERE teacherid = $class[1]");
		   $teacher = mysql_fetch_row($q);

		   $monday .= "<b>$class[0]</b><br />
		   Section: $class[2]<br />
		   Room: $class[3]<br />
		   Teacher: $teacher[0] $teacher[1]";

		   if($monday != "<td>")
			 $monday .= "<br /><br />";
		   else
			 $monday .= "<br />";
		   break;
	 case 'T':
		   $q = mysql_query("SELECT fname, lname FROM teachers WHERE teacherid = $class[1]");
		   $teacher = mysql_fetch_row($q);

		   $tuesday .= "<b>$class[0]</b><br />
		   Section: $class[2]<br />
		   Room: $class[3]<br />
		   Teacher: $teacher[0] $teacher[1]";
		   if($tuesday != "<td>")
			 $tuesday .= "<br /><br />";
		   else
			 $tuesday .= "<br />";

		   break;
	 case 'W':
		   $q = mysql_query("SELECT fname, lname FROM teachers WHERE teacherid = $class[1]");
		   $teacher = mysql_fetch_row($q);

		   $wednesday .= "<b>$class[0]</b><br />
		   Section: $class[2]<br />
		   Room: $class[3]<br />
		   Teacher: $teacher[0] $teacher[1]";
		   if($wednesday != "<td>")
			 $wednesday .= "<br /><br />";
		   else
			 $wednesday .= "<br />";
		   break;
	 case 'H':
		   $q = mysql_query("SELECT fname, lname FROM teachers WHERE teacherid = $class[1]");
		   $teacher = mysql_fetch_row($q);

		   $thursday .= "<b>$class[0]</b><br />
		   Section: $class[2]<br />
		   Room: $class[3]<br />
		   Teacher: $teacher[0] $teacher[1]";
		   if($thursday != "<td>")
			 $thursday .= "<br /><br />";
		   else
			 $thursday .= "<br />";
		   break;
	 case 'F':
		   $q = mysql_query("SELECT fname, lname FROM teachers WHERE teacherid = $class[1]");
		   $teacher = mysql_fetch_row($q);

		   $friday .= "<b>$class[0]</b><br />
		   Section: $class[2]<br />
		   Room: $class[3]<br />
		   Teacher: $teacher[0] $teacher[1]";
		   if($friday != "<td>")
			 $friday .= "<br /><br />";
		   else
			 $friday .= "<br />";
		   break;
	}
   }
  }

  $tablerow = $monday . "&nbsp;</td>" . $tuesday . "&nbsp;</td>" . $wednesday . "&nbsp;</td>" . $thursday . "&nbsp;</td>" . $friday . "&nbsp;</td>";

  print($tablerow);

  print("</tr>");
 }

print("  </table>
<table border='0' width='600' align='center'>
<tr>
<td>
<p align='left'>
  <input type='button' value=' Back ' onClick='document.classes.page2.value=26;document.classes.submit();'>
  <input type='hidden' name='page2' value='$page2'>
  <input type='hidden' name='logout'>
  <input type='hidden' name='page' value='$page'>
</p>
</td>
</tr>
</table>
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