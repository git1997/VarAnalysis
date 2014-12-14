<?php
print("
 <h1>Grade Report</h1>
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
  <b>Student:</b><select name='student' onChange='document.classes.submit();'>");

 // Get a list of all the students //
 $query = mysql_query("SELECT studentid,fname,lname FROM students")
   or die("GradeReport.php: Unable to get a list of students - ".mysql_error());

 $text = "";

 while($student = mysql_fetch_row($query))
 {
  // If the student has not been set, then use the first student //
  if($_POST["student"] == "")
  {
   $_POST["student"] = $student[0];
  }

  if($student[0] == $_POST["student"])
  {
	$text = "<option value='$student[0]'>$student[1] $student[2]</option>\n".$text;
  }
  else
  {
	$text .= "<option value='$student[0]'>$student[1] $student[2]</option>\n";
  }
 }

 print($text);
print("  </select>
  <br><br>
  <table cellspacing='0' width='600' class='dynamiclist'>
  <tr class='header'>
   <th>Class</th>
   <th>1st Quarter Grade</th>
   <th>2nd Quarter Grade</th>
   <th>Current Grade</th>
  </tr>");

  // Print out the grades for each class //
  $query = mysql_query("SELECT courseid, q1currpoints, q2currpoints, currentpoints FROM registrations WHERE studentid = '$_POST[student]'")
	or die("GradeReport.php: Unable to get the list of classes this student is registered for - ".mysql_error());

  $row = 0;
  while($classes = mysql_fetch_row($query))
  {
   $row++;
   $q = mysql_query("SELECT coursename, q1points, q1points, totalpoints, aperc, bperc, cperc, dperc, fperc, semesterid FROM courses WHERE courseid = $classes[0]");
   $cinfo = mysql_fetch_row($q);

   if($cinfo[9] != $_POST['semester'])
	 continue;

   // Calculate the 1st Quarter Grade //
   if($cinfo[1]!=0)
	$q1grade = $classes[1] / $cinfo[1];
   else
	$q1grade = 0;

   if($cinfo[1]==0)
	$q1grade = '';
   elseif($q1grade >= ($cinfo[4]/100))
	$q1grade = "A";
   elseif($q1grade >= $cinfo[5]/100)
	$q1grade = "B";
   elseif($q1grade >= $cinfo[6]/100)
	$q1grade = "C";
   elseif($q1grade >= $cinfo[7]/100)
	$q1grade = "D";
   else
	$q1grade = "F";

   // Calculate the 2nd Quarter Grade
   if($cinfo[2]!=0)
	$q2grade = $classes[2] / $cinfo[2];
   else
	$q2grade = 0;

   if($q2grade == 0)
	$q2grade = '';
   elseif($q2grade >= ($cinfo[4]/100))
	$q2grade = "A";
   elseif($q2grade >= $cinfo[5]/100)
	$q2grade = "B";
   elseif($q2grade >= $cinfo[6]/100)
	$q2grade = "C";
   elseif($q2grade >= $cinfo[7]/100)
	$q2grade = "D";
   else
	$q2grade = "F";

   // Calculate the Current Grade //
   if($cinfo[3]!=0)
	$currgrade = $classes[3] / $cinfo[3];
   else
	$currgrade = 0;

   if($cinfo[3] == 0)
	$currgrade = '';
   elseif($currgrade >= ($cinfo[4]/100))
	$currgrade = "A";
   elseif($currgrade >= $cinfo[5]/100)
	$currgrade = "B";
   elseif($currgrade >= $cinfo[6]/100)
	$currgrade = "C";
   elseif($currgrade >= $cinfo[7]/100)
	$currgrade = "D";
   else
	$currgrade = "F";

   print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
   <td align='left' style='padding-left: 30px;'>$cinfo[0]</td>
   <td>$q1grade</td>
   <td>$q2grade</td>
   <td>$currgrade</td>
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