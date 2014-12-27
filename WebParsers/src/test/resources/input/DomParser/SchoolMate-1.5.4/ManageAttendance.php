<?php
 // Add the attendance record if the user is adding one //
 if($_POST["addattendance"] == 1)
 {
  require_once("DBFunctions.php");

  // Get the termid from the semester //
  $query = mysql_query('SELECT termid FROM semesters WHERE semesterid = '.$_POST["semester"]);
  $id = mysql_result($query,0);

  // Insert the attendance record //
  $query = mysql_query("INSERT INTO schoolattendance VALUES('', '$_POST[student]', '".converttodb($_POST["attdate"])."', '$_POST[semester]', $id, '$_POST[type]')")
	or die("ManageAttendance.php: Unable to insert new attendance record - ".mysql_error());
 }

 // Delete the selected attendance record //
 if($_POST["deletereg"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   deleteAttendance($delete[$i]);
  }
 }

print("<script language='JavaScript'>

  // Function to make sure the student wants to delete the registration(s) //
  function validate()
  {
   if( document.registration.selectreg.value > 0 )
   {
	var confirmed = confirm(\"Are you sure you want to delete this attendance record?\");

	if( confirmed == true )
	{
	 document.registration.submit();
	}
   }
   else
   {
	alert('You must select an attendance record to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.registration.selectreg.value == 1 )
   {
	document.registration.submit();
   }
   else
   {
	if( document.registration.selectreg.value > 1 )
	{
	 alert('You can only edit one student at a time.');
	}
	else
	{
	 alert('You must select a registration to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row + 1;
   if(document.registration.elements[row].checked)
   {
	document.registration.selectreg.value = Math.round(document.registration.selectreg.value) + 1;
   }
   else
   {
	document.registration.selectreg.value = Math.round(document.registration.selectreg.value) - 1;
   }
   //alert(document.registration.selectreg.value);
  }
 </script>
 <h1>Attendance</h1>
 <br>
 <table align='center' width='400' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='registration' action='./index.php' method='POST'>
 <b>Semester: </b> <select name='semester' onChange='document.registration.addattend.value=0;document.registration.deletereg.value=0;document.registration.submit();'>
 ");
  // Get a list of semesters //
  $query = mysql_query("SELECT semesterid, title FROM semesters")
	or die("Registration.php: Unable to get a list of semesters for drop-down - ".mysql_error());

  if($_POST['semester']==NULL)
  {
	$q = mysql_query("SELECT semesterid, title FROM semesters WHERE startdate < CURDATE() < enddate");
	$temp = mysql_fetch_row($q);
	$_POST['semester'] = $temp[0];

	if($_POST['semester'] == NULL)
	  $_POST['semester'] = '';
  }

  while($semester = mysql_fetch_row($query))
  {
   print("<option value='$semester[0]' ".( $_POST['semester']==$semester[0]&&$_POST['semester']!=NULL ? "SELECTED" : "").">$semester[1]</option>");
  }


  print("
	 </select>
 <b>Student:</b>
 <select name='student' onChange='document.registration.addattend.value=0;document.registration.deletereg.value=0;document.registration.submit();'>");

 // Get a list of all the students //
 $query = mysql_query("SELECT studentid,fname,lname FROM students ORDER BY fname")
   or die("ManageAttendance.php: Unable to get a list of students - ".mysql_error());

 while($student = mysql_fetch_row($query))
 {

  // If the student has not been set, then use the first student //
  if($_POST["student"] == "")
  {
   $_POST["student"] = $student[0];
  }

  print("<option value='$student[0]' ".( $_POST['student']==$student[0]&&$_POST['student']!=NULL ? "SELECTED" : "").">$student[1] $student[2]</option>");

 }

print("  </select>
   <br /><br />
  <table width='400' class='dynamiclist' cellpadding='5' cellspacing='0'>
  <tr class='header' align='center'>
   <th>&nbsp;</th><th>Tardy</th><th>Absent</th>
  </tr>");

  // Get the date restrictions for the current semester //
  if($_POST['semester']!=NULL)
  {
  $query = mysql_query("SELECT startdate, enddate FROM semesters WHERE semesterid = $_POST[semester]");
  $semesterdates = mysql_fetch_row($query);

  // Get the list of registrations for this student //
  $query = mysql_query("SELECT sattendid,sattenddate,type FROM schoolattendance WHERE studentid = '$_POST[student]' AND '$semesterdates[0]' <= sattenddate AND sattenddate <= '$semesterdates[1]' ORDER BY sattenddate ASC")
	or die("ManageAttendance.php: Unable to get a list of registrations - ".mysql_error());

  $row = 0;
  while($reg = mysql_fetch_row($query))
  {
   $row++;

   if($reg[2] == 'tardy')
	 $class[0] = $reg[1];
   else
	 $class[1] = $reg[1];
   print("<tr style='color: red; font-weight: bold;' align='center' class='".( $row%2==0 ? "even" : "odd" )."'>
   <td><input type='checkbox' name='delete[]' value='$reg[0]' onClick='updateboxes($row);' /></td>
   <td>".($class[0]!="" ?convertfromdb($class[0]) : "")."</td>
   <td>".($class[1]!="" ? convertfromdb($class[1]) : "" )."</td>
   </tr>
   ");

   unset($class);
  }

  if($row == 0)
	print("<tr class='even'><td>&nbsp;</td><td>N/A</td><td>N/A</td></tr>");

  $query = mysql_query("SELECT count(*) FROM schoolattendance WHERE type='tardy' AND studentid = '$_POST[student]' AND '$semesterdates[0]' <= sattenddate AND sattenddate <= '$semesterdates[1]'") or die("ManageAttendance.php: Unable to get the total number of tardies - ". mysql_error());
  $tardytotal = mysql_fetch_row($query);

  $query = mysql_query("SELECT count(*) FROM schoolattendance WHERE type='absent' AND studentid = '$_POST[student]' AND '$semesterdates[0]' <= sattenddate AND sattenddate <= '$semesterdates[1]'") or die("ManageAttendance.php: Unable to get the total number of absences - ".mysql_error());
  $absenttotal = mysql_fetch_row($query);
  }

print("
   <tr class='header' align='center'>
	<td><b>Totals:</td><td>$tardytotal[0]</td><td>$absenttotal[0]</td>
   </tr>
  </table>
  <br />
  <input type='button' value='Add' onClick='document.registration.addattend.value=1;document.registration.page2.value=31;document.registration.submit();'> <input type='button' value='Delete' onClick='document.registration.deletereg.value=1;validate();'>

  <input type='hidden' name='addattend' />
  <input type='hidden' name='deletereg' />
  <input type='hidden' name='selectreg' />
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