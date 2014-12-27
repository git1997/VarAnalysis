<?php
 ################
 #    INSERT    #
 ################

 // Add the registration if the user is adding one //
 if($_POST["addreg"] == 1)
 {
  // Get the period and day for this new class for validation //
  $query = mysql_query("SELECT periodnum, dotw FROM courses WHERE courseid = '$_POST[class]'")
	or die("Registration.php: Unable to get the information about the new class to add for validation - ".mysql_error());

  $cinfo = mysql_fetch_row($query);
  $cinfo_days = preg_split('//', $cinfo[1], -1, PREG_SPLIT_NO_EMPTY);

  // Make sure this new registration doesn't interfere with any others //
  $query = mysql_query("SELECT courseid FROM registrations WHERE studentid = '$_POST[student]'")
	or die("Registration.php: Unable to get a list of registrations for validation - ".mysql_error());

  while($classes = mysql_fetch_row($query))
  {
   $q = mysql_query("SELECT periodnum, dotw FROM courses WHERE courseid = '$classes[0]' AND semesterid = $_POST[semester]");
   $curregs = mysql_fetch_row($q);

   for($i=0; $i<count($cinfo_days); $i++)
   {
	if(preg_match("/$cinfo_days[$i]/",$curregs[1]) && $cinfo[0] == $curregs[0] && $curregs)
	{
	 $insert = 0;
	 break 2;
	}
	else
	{
	 $insert = 1;
	}
  }
 }

 if($curregs[0] == "")
 {
  $insert=1;
 }

  if($insert)
  {
   // Get the termid from the semesterid //
   $query = mysql_query('SELECT termid FROM semesters WHERE semesterid = '.$_POST["semester"]);
   $id = @mysql_result($query,0);

   $query = mysql_query("INSERT INTO registrations VALUES('','$_POST[class]','$_POST[student]', '$_POST[semester]', '$id[0]', '','','')")
	 or die("Registration.php: Unable to add the new registration - ".mysql_error());

   // Check to see if the class is a full year //
   $query = mysql_query("SELECT secondcourseid FROM courses WHERE courseid = $_POST[class]");
   $id = mysql_fetch_row($query);

   if($id[0]!=NULL)
	$query = mysql_query("INSERT INTO registrations VALUES('','$id[0]','$_POST[student]','','','')");

  }
  else
  {
   print("<font color='red'>*That class interferes with another registration for this student</font>");
  }
 }

 ##################
 #     DELETE     #
 ##################

 // Delete the selected registration //
 if($_POST["deletereg"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   deleteRegistration($delete[$i]);
  }
 }

print("<script language='JavaScript'>

  // Function to make sure the student wants to delete the registration(s) //
  function validate()
  {
   if( document.registration.selectreg.value > 0 )
   {
	var confirmed = confirm(\"Are you sure you want to delete this registration?\");

	if( confirmed == true )
	{
	 document.registration.submit();
	}
   }
   else
   {
	alert('You must select a registration to delete.');
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
   row = row + 4;
   if(document.registration.elements[row].checked)
   {
	document.registration.selectreg.value = Math.round(document.registration.selectreg.value) + 1;
   }
   else
   {
	document.registration.selectreg.value = Math.round(document.registration.selectreg.value) - 1;
   }
  }
 </script>
 <h1>Registration</h1>
 <br>
 <table align='center' width='400' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='registration' action='./index.php' method='POST'>
 <b>Student:</b>
 <select name='student' onChange='document.registration.addreg.value=0;document.registration.deletereg.value=0;document.registration.submit();'>");

 // Get a list of all the students //
 $query = mysql_query("SELECT studentid,fname,lname FROM students ORDER BY fname ASC")
   or die("Registration.php: Unable to get a list of students - ".mysql_error());

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
&nbsp;&nbsp;<b>Semester: </b> <select name='semester' onChange='document.registration.addreg.value=0;document.registration.deletereg.value=0;document.registration.submit();'>
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
   <br /><br />
   <input type='button' value='Show in Grid' onClick='document.registration.addreg.value=0;document.registration.deletereg.value=0;document.registration.page2.value=29;document.registration.submit();' />
   <br /><br />
  <table width='400' class='dynamiclist' cellpadding='5' cellspacing='0'>
  <tr class='header'>
   <th colspan='3'><span style='font-size: 14pt;'><b>Add Class</b></span></th>
  </tr>
  <tr class='header'>
   <td align='center' colspan='3'>
	<select name='class'>");

 // Get a list of all the classes in the selected semester //
 if($_POST['semester']!=NULL)
 {
 $query = mysql_query("SELECT courseid, coursename FROM courses WHERE semesterid = $_POST[semester]")
   or die("</select>Registration.php: Unable to get a list of courses - ".mysql_error());

 while($classes = mysql_fetch_row($query))
 {
  print("<option value='$classes[0]'>$classes[1]</option>\n");
 }
 }
print("	</select>
   <input type='button' value='Add' onClick='document.registration.addreg.value=1;document.registration.submit();' />
  </td>
  </tr>
  <tr class='header'>
   <th colspan='2'>Class Name</th><th>Period Number</th>
  </tr>");

  // Get the list of registrations for this student in the current semester //
  $query = mysql_query("SELECT regid,courseid FROM registrations WHERE studentid = '$_POST[student]'")
	or die("Registration.php: Unable to get a list of registrations - ".mysql_error());

  $row = 0;
  while($reg = mysql_fetch_row($query))
  {
   $q = mysql_query("SELECT coursename, periodnum, semesterid FROM courses WHERE courseid = '$reg[1]'");
   $class = mysql_fetch_row($q);

   if($class[2] != $_POST['semester'])
	 continue;

   $row++;
   print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
   <td><input type='checkbox' name='delete[]' value='$reg[0]' onClick='updateboxes($row);' /></td>
   <td align='left'>$class[0]</td>
   <td>$class[1]</td>
   </tr>
   ");
  }


print("  </table>
  <br />
  <input type='button' value='Delete' onClick='document.registration.deletereg.value=1;validate();'>
  <input type='hidden' name='addreg' />
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