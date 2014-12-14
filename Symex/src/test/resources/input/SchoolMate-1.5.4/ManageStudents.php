<?php

 // Add the new student if one is being added //
 if($_POST["addstudent"] == 1)
 {
  if($_POST["username"] != "" && $_POST["fname"] != "" && $_POST["mi"] != "" && $_POST["lname"] != "")
  {
   $query = mysql_query("SELECT userid FROM students")
	 or die("ManageStudents.php: Uanable to get list of users - ".mysql_error());

   // Make sure that the supplied username does not already exist in the database //
   while( $userlist = mysql_fetch_row($query) )
   {
	if($_POST["username"] == $userlist[0])
	{
	 die("<br><br><h1 align='center'><font color='red'>That user is already assigned to a student!</font></h1>
	 <br>
	 <form name='uhoh' action='./index.php' method='POST'>
	 <center><input type='button' value='&nbsp;Back&nbsp;' onClick='document.uhoh.page2.value=2;document.uhoh.submit();'></center>
	 <input type='hidden' name='page2' value='$page2'>
	 <input type='hidden' name='logout'>
	 <input type='hidden' name='page' value='$page'>
	 </form>");
	}
   }

   // If all is good, insert the new student into the database //
   $query = mysql_query("INSERT INTO students VALUES('', '$_POST[username]', '$_POST[fname]', '$_POST[mi]', '$_POST[lname]')")
	 or die("ManageStudents.php: Unable to insert new student - " . mysql_error());
  }
 }

 // Edit the student if one is being edited //
 if($_POST["editstudent"] == 1 && $_POST["username"] != "" && $_POST["fname"] != "" && $_POST["mi"] != "" && $_POST["lname"] != "")
 {
  $query = mysql_query("UPDATE `students` SET `userid`='$_POST[username]', `fname`='$_POST[fname]', `mi`='$_POST[mi]', `lname`='$_POST[lname]' WHERE `studentid`='$_POST[studentid]' LIMIT 1")
	or die("ManageStudents.php: Unable to update the student information - ".mysql_error());
 }

 // Delete the student(s) that the student has requested as well as the classes belonging to those students //
 if($_POST["deletestudent"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   deleteStudent($delete[$i]);
  }
 }

 print("<script language='JavaScript'>

  // Function to make sure the student wants to delete the student(s) //
  function validate()
  {
   if( document.students.selectstudent.value > 0 )
   {
	var confirmed = confirm(\"Are you sure you want to delete this student?\");

	if( confirmed == true )
	{
	 document.students.submit();
	}
   }
   else
   {
	alert('You must select a student to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.students.selectstudent.value == 1 )
   {
	document.students.submit();
   }
   else
   {
	if( document.students.selectstudent.value > 1 )
	{
	 alert('You can only edit one student at a time.');
	}
	else
	{
	 alert('You must select a student to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row + 2;
   if(document.students.elements[row].checked)
   {
	document.students.selectstudent.value = Math.round(document.students.selectstudent.value) + 1;
   }
   else
   {
	document.students.selectstudent.value = Math.round(document.students.selectstudent.value) - 1;
   }
  }
 </script>

 <h1>Manage Students</h1>
 <br><br>
 <table align='center' width='425' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='students' action='./index.php' method='POST'>
  <input type='button' value='Add' onClick='document.students.page2.value=20;document.students.submit();'>
  <input type='button' value='Edit' onClick='document.students.page2.value=21;checkboxes();'>
  <input type='button' value='Delete' onClick='document.students.deletestudent.value=1;validate();'>
  <br><br>
  <table cellspacing='0' width='425' cellpadding='8' class='dynamiclist'>
   <tr class='header'>
	<td>&nbsp;</td>
	<th>First Name</th>
	<th>Middle Initial</th>
	<th>Last Name</th>
	<th>Username</th>
   </tr>");

   // Get the total number of students to know how many pages to have //
   $query = mysql_query("SELECT COUNT(*) FROM students")
	 or die("Managestudents.php: Unable to retrieve total number of students - ".mysql_error());

   $numrows = mysql_result($query,0);

   $numpages = ceil($numrows / 25);

   if($_POST["onpage"] == "")
   {
	$_POST["onpage"]=1;
   }

   // Get and display the students //
   $query = mysql_query("SELECT s.studentid,s.fname,s.mi,s.lname,u.username FROM students s, users u WHERE s.userid = u.userid");
   $row = 0;
   $actualrow = 0;
   while($student = mysql_fetch_row($query))
   {
	$row++;

	if($row > ($_POST["onpage"]*25)-25 && $row <= ($_POST["onpage"]*25))
	{
	 $actualrow++;
	 print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	  <td><input type='checkbox' name='delete[]' value='$student[0]' onClick='updateboxes($actualrow);' /></td>
	  <td>$student[1]</td>
	  <td>$student[2]</td>
	  <td>$student[3]</td>
	  <td>$student[4]</td>
	 </tr>");
	}
   }

 print(" </table>
  <br>
  <b>Reports:</b>
  <select name='report' onChange='document.students.page2.value=document.students.report.value;document.students.deletestudent.value=0;document.students.submit();'>
   <option>Choose a Report...</option>
   <option value='27'>Deficiency Report</option>
   <option value='28'>Grade Report</option>
   <option value='32'>Points Report</option>
   <option value='1337'>Report Cards</option>
  </select>
 &nbsp;&nbsp;&nbsp;&nbsp;<b>Term: </b> <select name='term' onChange='document.students.submit();'>");
 // Get a list of terms //
  $query = mysql_query("SELECT termid, title FROM terms")
	or die("ManageStudents.php: Unable to get a list of terms for drop-down - ".mysql_error());

  if($_POST['term']==NULL)
  {
	$q = mysql_query("SELECT termid, title FROM terms WHERE startdate < CURDATE() < enddate");
	$temp = mysql_fetch_row($q);
	$_POST['term'] = $temp[0];
  }

  while($term = mysql_fetch_row($query))
  {
   print("<option value='$term[0]' ".( $_POST['term']==$term[0]&&$_POST['term']!=NULL ? "SELECTED" : "").">$term[1]</option>");
  }

  print("
	 </select>
  <br /><br />
  <input type='button' value='Add' onClick='document.students.page2.value=20;document.students.submit();'>
  <input type='button' value='Edit' onClick='document.students.page2.value=21;checkboxes();'>
  <input type='button' value='Delete' onClick='document.students.deletestudent.value=1;validate();'>
  <br><br>

  <center>Page: ");

  for($i=1; $i<=$numpages; $i++)
  {
   if($i == $_POST["onpage"])
   {
	print("<a href='JavaScript: document.students.deletestudent.value=0;document.students.page2.value=2;document.students.onpage.value=$i;document.students.submit();' class='selectedpagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
   else
   {
	print("<a href='JavaScript: document.students.deletestudent.value=0;document.students.page2.value=2;document.students.onpage.value=$i;document.students.submit();' class='pagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
  }

print("\n</center>
  <input type='hidden' name='deletestudent'>
  <input type='hidden' name='selectstudent'>
  <input type='hidden' name='page2' value='$page2'>
  <input type='hidden' name='onpage' value='$_POST[onpage]'>
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