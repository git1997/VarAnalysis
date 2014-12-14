<?php
 // Edit the grade when it is to be edited //
 if($_POST['editgrade'] == 1)
 {
  require_once("DBFunctions.php");

  // If a grade has already been inserted, then just update it.	//
  // Otherwise add a new grade 									//
  if($_POST['gradeid']!='')
  {
  $query = mysql_query("UPDATE grades SET submitdate = '".converttodb($_POST['gradedate'])."', points = '$_POST[points]', comment = '$_POST[comment]', islate = '".($_POST['late']==1 ? "1" : "0")."' WHERE gradeid = $_POST[gradeid]")
	or die("ManageGrades.php: Unable to update the grade - ".mysql_error());
  }
  else
  {
   // Get the semester and term ids from the class to be inserted //
   $query = mysql_query('SELECT semesterid,termid FROM courses WHERE courseid = '.$_POST["selectclass"]);
   $ids = mysql_fetch_row($query);

   $query = mysql_query("INSERT INTO grades VALUES('', '$_POST[assignment]', '$_POST[selectclass]', '$ids[0]', '$ids[1]', '$_POST[student]', '$_POST[points]', '$_POST[comment]', '".converttodb($_POST['gradedate'])."', '".($_POST['late']==1 ? "1" : "0")."')")
	or die("ManageGrades.php: Unable to insert the new grade - ".mysql_error());
  }

  // Update the amount of points the student has accumulated for this class //
  $query = mysql_query("SELECT semesterid FROM courses WHERE courseid = $_POST[selectclass]");
  $id = mysql_fetch_row($query);

  $query = mysql_query("SELECT midtermdate FROM semesters WHERE semesterid = $id[0]");
  $dates = mysql_fetch_row($query);

  $middate    = strtotime($dates[0]);
  $submitdate = strtotime($_POST['gradedate']);
  $wasdate    = strtotime($_POST['wasdate']);

  if($submitdate < $middate)
  {
   if($wasdate < $middate)
	$query = mysql_query("UPDATE registrations SET q1currpoints=(q1currpoints + $_POST[points] - $_POST[wasgrade]), currentpoints=(currentpoints + $_POST[points] - $_POST[wasgrade]) WHERE courseid = $_POST[selectclass] AND studentid = $_POST[student]");
   else
	$query = mysql_query("UPDATE registrations SET q2currpoints=(q2currpoints - $_POST[wasgrade]), q1currpoints=(q1currpoints + $_POST[points]), currentpoints=(currentpoints + $_POST[points] - $_POST[wasgrade]) WHERE courseid = $_POST[selectclass] AND studentid = $_POST[student]");
  }
  else
  {
   if($wasdate < $middate)
	$query = mysql_query("UPDATE registrations SET q1currpoints=(q1currpoints - $_POST[wasgrade]), q2currpoints=(q2currpoints + $_POST[points]), currentpoints=(currentpoints + $_POST[points] - $_POST[wasgrade]) WHERE courseid = $_POST[selectclass] AND studentid = $_POST[student]");
   else
	$query = mysql_query("UPDATE registrations SET q2currpoints=(q2currpoints + $_POST[points] - $_POST[wasgrade]), currentpoints=(currentpoints + $_POST[points] - $_POST[wasgrade]) WHERE courseid = $_POST[selectclass] AND studentid = $_POST[student]");
  }
 }

 // Delete the selected grades //
 if($_POST["deletegrade"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   $query = mysql_query("SELECT gradeid, points, submitdate, studentid FROM grades WHERE studentid = $delete[$i] AND assignmentid = $_POST[assignment]");
   $id = mysql_fetch_row($query);
   deleteGrade($id[0]);

   // Subtract the amount off from the current and possible points //
   $query = mysql_query("SELECT midtermdate FROM semesters WHERE startdate < CURDATE() AND CURDATE() < enddate");
   $dates = mysql_fetch_row($query);

   $middate    = strtotime($dates[0]);
   $submitdate = strtotime($id[2]);

   if($submitdate < $middate)
   {
	$query = mysql_query("UPDATE registrations SET q1currpoints=(q1currpoints - $id[1]), currentpoints=(currentpoints - $id[1]) WHERE studentid = $id[3] AND courseid = $_POST[selectclass]");
   }
   else
   {
	$query = mysql_query("UPDATE registrations SET q2currpoints=(q2currpoints - $id[1]), currentpoints=(currentpoints - $id[1]) WHERE studentid = $id[3] AND courseid = $_POST[selectclass]");
   }
  }
 }

print("<script language='JavaScript'>

  // Function to make sure the student wants to delete the grade(s) //
  function validate()
  {
   if( document.grades.selectgrade.value > 0 )
   {
	var confirmed = confirm(\"Are you sure you want to delete this grade?\");

	if( confirmed == true )
	{
	 document.grades.submit();
	}
   }
   else
   {
	alert('You must select a grade to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.grades.selectgrade.value == 1 )
   {
	document.grades.submit();
   }
   else
   {
	if( document.grades.selectgrade.value > 1 )
	{
	 alert('You can only edit one grade at a time.');
	}
	else
	{
	 alert('You must select a grade to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row;
   if(document.grades.elements[row].checked)
   {
	document.grades.selectgrade.value = Math.round(document.grades.selectgrade.value) + 1;
   }
   else
   {
	document.grades.selectgrade.value = Math.round(document.grades.selectgrade.value) - 1;
   }
  }
 </script>
 <h1>Grades</h1>
 <br>
 <table align='center' width='595' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='grades' action='./index.php' method='POST'>
 <b>Assignment:</b>
 <select name='assignment' onChange='document.grades.addgrade.value=0;document.grades.deletegrade.value=0;document.grades.submit();'>");

 $query = mysql_query("SELECT * FROM assignments WHERE courseid = '$_POST[selectclass]'");

 // Stop displaying information if the query returned a null set //
  if(mysql_fetch_row($query) == NULL)
  {
   die("</select>
	 <br /><br />
  <table width='595' class='dynamiclist' cellpadding='5' cellspacing='0'>
  <tr class='header'>
   <th>&nbsp;</th>
   <th>Student Name</th>
   <th>Date Submitted</th>
   <th>Earned Points</th>
   <th>Possible Points</th>
   <th>Grade</th>
   <th>Late</th>
  </tr>
  <tr class='even'>
   <td colspan='10' align='center'>No assignments have been defined for this class.</td>
  </tr>
  </table>");
  }

 // Get a list of all the assignments //
 $query = mysql_query("SELECT assignmentid,title FROM assignments WHERE courseid = '$_POST[selectclass]'")
   or die("ManageGrades.php: Unable to get a list of assignments - ".mysql_error());

 $text = "";

 while($assignment = mysql_fetch_row($query))
 {

  // If the assignment has not been set, then use the first student //
  if($_POST["assignment"] == "")
  {
   $_POST["assignment"] = $assignment[0];
  }
  print("<option value='$assignment[0]' ".( $_POST['assignment']==$assignment[0]&&$_POST['assignment']!=NULL ? "SELECTED" : "").">$assignment[1]</option>");
 }

print("  </select>
  <br /><br />
  <table width='595' class='dynamiclist' cellpadding='5' cellspacing='0'>
  <tr class='header'>
   <th>&nbsp;</th>
   <th>Student Name</th>
   <th>Date Submitted</th>
   <th>Earned Points</th>
   <th>Possible Points</th>
   <th>Grade</th>
   <th>Late</th>
  </tr>");

  // Get the list of grades and students for this assignment //
  $query = mysql_query("SELECT DISTINCT s.studentid, s.fname, s.lname FROM students s, registrations r WHERE s.studentid = r.studentid AND r.courseid = $_POST[selectclass] ORDER BY UPPER(s.lname) ASC")
	or die("ManageGrades.php: Unable to get the list of students for this class - ".mysql_error());

  require_once("DBFunctions.php");
  $row = 0;
  while($student = mysql_fetch_row($query))
  {

   $q = mysql_query("SELECT gradeid, points, comment, submitdate, islate, studentid, comment FROM grades WHERE assignmentid = '$_POST[assignment]' AND courseid = '$_POST[selectclass]' AND studentid = '$student[0]'")
	or die("ManageGrades.php: Unable to get a list of gradess - ".mysql_error());

   $grade = mysql_fetch_row($q);

   $q = mysql_query("SELECT title,totalpoints FROM assignments WHERE assignmentid = '$_POST[assignment]'");
   $assignment = mysql_fetch_row($q);

   $row++;
   print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
   <td><input type='checkbox' name='delete[]' value='$student[0]' onClick='updateboxes($row);' /></td>
   <td>$student[1] $student[2]</td>
   <td>".( convertfromdb($grade[3]) != "//" ? convertfromdb($grade[3]) : "")."</td>
   <td>$grade[1]</td>
   <td>$assignment[1]</td>
	<td>");

   // Calculate and display the letter grade //
	$q = mysql_query("SELECT aperc,bperc,cperc,dperc,fperc FROM courses WHERE courseid = $_POST[selectclass]") or die("ManageGrades.php: Unable to get the grade percentages - ".mysql_error());
	$percs = mysql_fetch_row($q);

	if($assignment[1]==0)
	  $assignment[1] = 1;
	$letter = $grade[1]/$assignment[1];
	$letter = $letter * 100;

	if($assignment[1]==0)
	  $letter = -1;
	if($grade[4] != NULL)
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
	<td>");

	if($grade[4] == 1)
	   print("Yes");
	elseif($grade[4] != NULL)
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
  <input type='button' value='Edit' onClick='document.grades.page2.value=7;checkboxes();'>
  <input type='button' value='Delete' onClick='document.grades.deletegrade.value=1;validate();'>

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