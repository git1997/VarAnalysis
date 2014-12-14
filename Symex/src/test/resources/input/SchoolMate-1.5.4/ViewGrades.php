<?php

if($_POST['student']==NULL)
{
 $query = mysql_query("SELECT studentid FROM students WHERE userid = $_SESSION[userid]");
 $studentid = mysql_fetch_row($query);
 $studentid = $studentid[0];
}
else
 $studentid = $_POST['student'];

print("
 <h1>View Grades</h1>
 <br>
 <table align='center' width='600' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='grades' action='./index.php' method='POST'>
  <br />
  <table width='595' class='dynamiclist' cellpadding='5' cellspacing='0'>
  <tr class='header'>
   <th>Assignment Name</th>
   <th>Date Submitted</th>
   <th>Earned Points</th>
   <th>Possible Points</th>
   <th>Grade</th>
   <th>Comment</th>
   <th>Late</th>
  </tr>");

  // Get the list of grades and students for this assignment //
  $query = mysql_query("SELECT assignmentid, title FROM assignments WHERE courseid = $_POST[selectclass] ORDER BY duedate DESC")
	or die("ViewGrades.php: Unable to get the list of assignments for this class - ".mysql_error());

  require_once("DBFunctions.php");
  $row = 0;
  while($assignment = mysql_fetch_row($query))
  {

   $q = mysql_query("SELECT gradeid, points, submitdate, islate, comment FROM grades WHERE studentid = '$studentid' AND courseid = '$_POST[selectclass]' AND assignmentid = '$assignment[0]'")
	or die("ManageGrades.php: Unable to get a list of gradess - ".mysql_error());

   $grade = mysql_fetch_row($q);

   $q = mysql_query("SELECT title,totalpoints FROM assignments WHERE assignmentid = '$assignment[0]'");
   $assignmentinfo = mysql_fetch_row($q);

   $row++;
   print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
   <td>$assignment[1]</td>
   <td>".( convertfromdb($grade[2]) != "//" ? convertfromdb($grade[2]) : "")."</td>
   <td>$grade[1]</td>
   <td>$assignmentinfo[1]</td>
	<td>");

   // Calculate and display the letter grade //
	$q = mysql_query("SELECT aperc,bperc,cperc,dperc,fperc FROM courses WHERE courseid = $_POST[selectclass]") or die("ManageGrades.php: Unable to get the grade percentages - ".mysql_error());
	$percs = mysql_fetch_row($q);

	if($assignmentinfo[1]==0)
	  $assignmentinfo[1] = 1;
	$letter = $grade[1]/$assignmentinfo[1];
	$letter = $letter * 100;

	if($assignmentinfo[1]==0)
	  $letter = -1;
	if($grade[3] != NULL)
	{
	 if($letter == -1)
	   print("Total Not Found");
	 elseif($letter >= $percs[0])
	   print("A");
	 elseif($letter >= $percs[1])
	   print("B");
	 elseif($letter >= $percs[2])
	   print("C");
	 elseif($letter >= $percs[3])
	   print("D");
	 else
	   print("F");
	}

print("</td>
	<td align='left'>$grade[4]</td>
	<td>");

	if($grade[3] == 1)
	   print("Yes");
	elseif($grade[3] != NULL)
	   print("No");

print("
	</td>
   </tr>
   ");
  }

  if($row==0)
	 print("<tr class='even'><td colspan='9'><center>There are currently no students registered for this class.</center></td></tr>");


print("  </table>
  <br />

  <input type='hidden' name='addgrade' />
  <input type='hidden' name='deletegrade' />
  <input type='hidden' name='selectclass' value='$_POST[selectclass]' />
  <input type='hidden' name='selectgrade' />
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