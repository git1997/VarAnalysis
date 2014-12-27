<?php

 // Add the new teacher if one is being added //
 if($_POST["addteacher"] == 1)
 {
  if($_POST["username"] != "" && $_POST["fname"] != "" && $_POST["lname"] != "")
  {
   $query = mysql_query("SELECT userid FROM teachers")
	 or die("ManageTeachers.php: Uanable to get list of users - ".mysql_error());

   // Make sure that the supplied username does not already exist in the database //
   while( $userlist = mysql_fetch_row($query) )
   {
	if($_POST["username"] == $userlist[0])
	{
	 die("<br><br><h1 align='center'><font color='red'>That user is already assigned to a teacher!</font></h1>
	 <br>
	 <form name='uhoh' action='./index.php' method='POST'>
	 <center><input type='button' value='&nbsp;Back&nbsp;' onClick='document.uhoh.page2.value=3;document.uhoh.submit();'></center>
	 <input type='hidden' name='page2' value='$page2'>
	 <input type='hidden' name='logout'>
	 <input type='hidden' name='page' value='$page'>
	 </form>");
	}
   }

   // If all is good, insert the new teacher into the database //
   $query = mysql_query("INSERT INTO teachers VALUES('', '$_POST[username]', '$_POST[fname]', '$_POST[lname]')")
	 or die("ManageTeachers.php: Unable to insert new teacher - " . mysql_error());
  }
 }

 // Edit the teacher if one is being edited //
 if($_POST["editteacher"] == 1 && $_POST["fname"] != "" && $_POST["lname"] != "")
 {
  $query = mysql_query("UPDATE `teachers` SET `userid`='$_POST[username]', `fname`='$_POST[fname]', `lname`='$_POST[lname]' WHERE `teacherid`='$_POST[teacherid]' LIMIT 1")
	or die("Manageteachers.php: Unable to update the teacher information - ".mysql_error());
 }

 // Delete the teacher(s) that the teacher has requested as well as the classes belonging to those teachers //
 if($_POST["deleteteacher"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   deleteTeacher($delete[$i]);
  }
 }

 print("<script language='JavaScript'>

  // Function to make sure the teacher wants to delete the teacher(s) //
  function validate()
  {
   if( document.teachers.selectteacher.value > 0 )
   {
	var confirmed = confirm(\"Are you sure you want to delete this teacher?\");

	if( confirmed == true )
	{
	 document.teachers.submit();
	}
   }
   else
   {
	alert('You must select a teacher to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.teachers.selectteacher.value == 1 )
   {
	document.teachers.submit();
   }
   else
   {
	if( document.teachers.selectteacher.value > 1 )
	{
	 alert('You can only edit one teacher at a time.');
	}
	else
	{
	 alert('You must select a teacher to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row + 2;
   if(document.teachers.elements[row].checked)
   {
	document.teachers.selectteacher.value = Math.round(document.teachers.selectteacher.value) + 1;
   }
   else
   {
	document.teachers.selectteacher.value = Math.round(document.teachers.selectteacher.value) - 1;
   }
  }
 </script>

 <h1>Manage Teachers</h1>
 <br><br>
 <table align='center' width='400' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='teachers' action='./index.php' method='POST'>
  <input type='button' value='Add' onClick='document.teachers.page2.value=16;document.teachers.submit();'>
  <input type='button' value='Edit' onClick='document.teachers.page2.value=17;checkboxes();'>
  <input type='button' value='Delete' onClick='document.teachers.deleteteacher.value=1;validate();'>
  <br><br>
  <table cellspacing='0' width='400' cellpadding='8' class='dynamiclist'>
   <tr class='header'>
	<td>&nbsp;</td>
	<th>First Name</th>
	<th>Last Name</th>
	<th>Username</th>
   </tr>");

   // Get the total number of teachers to know how many pages to have //
   $query = mysql_query("SELECT COUNT(*) FROM teachers")
	 or die("ManageTeachers.php: Unable to retrieve total number of teachers - ".mysql_error());

   $numrows = mysql_result($query,0);

   $numpages = ceil($numrows / 25);

   if($_POST["onpage"] == "")
   {
	$_POST["onpage"]=1;
   }

   // Get and display the teachers //
   $query = mysql_query("SELECT t.teacherid,t.fname,t.lname,u.username FROM teachers t, users u WHERE t.userid = u.userid");
   $row = 0;
   $actualrow = 0;
   while($teacher = mysql_fetch_row($query))
   {
	$row++;

	if($row > ($_POST["onpage"]*25)-25 && $row <= ($_POST["onpage"]*25))
	{
	 $actualrow++;
	 print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	  <td><input type='checkbox' name='delete[]' value='$teacher[0]' onClick='updateboxes($actualrow);' /></td>
	  <td>$teacher[1]</td>
	  <td>$teacher[2]</td>
	  <td>$teacher[3]</td>
	 </tr>");
	}
   }

 print(" </table>
  <br>
  <input type='button' value='Add' onClick='document.teachers.page2.value=16;document.teachers.submit();'>
  <input type='button' value='Edit' onClick='document.teachers.page2.value=17;checkboxes();'>
  <input type='button' value='Delete' onClick='document.teachers.deleteteacher.value=1;validate();'>
  <br><br>

  <center>Page: ");

  for($i=1; $i<=$numpages; $i++)
  {
   if($i == $_POST["onpage"])
   {
	print("<a href='JavaScript: document.teachers.deleteteacher.value=0;document.teachers.page2.value=3;document.teachers.onpage.value=$i;document.teachers.submit();' class='selectedpagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
   else
   {
	print("<a href='JavaScript: document.teachers.deleteteacher.value=0;document.teachers.page2.value=3;document.teachers.onpage.value=$i;document.teachers.submit();' class='pagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
  }

print("\n</center>
  <input type='hidden' name='deleteteacher'>
  <input type='hidden' name='selectteacher'>
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