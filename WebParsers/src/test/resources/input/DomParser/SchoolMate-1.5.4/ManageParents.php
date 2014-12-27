<?php

 // Add the new parent if one is being added //
 if($_POST["addparent"] == 1)
 {
  if($_POST["username"] != "" && $_POST["fname"] != "" && $_POST["lname"] != "" && $_POST["student"] != "")
  {
   $query = mysql_query("SELECT userid FROM parents")
	 or die("ManageParents.php: Uanable to get list of users - ".mysql_error());

   // Make sure that the supplied username does not already exist in the database //
   while( $userlist = mysql_fetch_row($query) )
   {
	$q = mysql_query("SELECT studentid FROM parent_student_match WHERE parentid = $_POST[username]");
	while($student = mysql_fetch_row($q))
	{
	 if($_POST["username"] == $userlist[0] && $_POST['student']==$student[0])
	 {
	  die("<br><br><h1 align='center'><font color='red'>That user is already assigned to a parent!</font></h1>
	  <br>
	  <form name='uhoh' action='./index.php' method='POST'>
	  <center><input type='button' value='&nbsp;Back&nbsp;' onClick='document.uhoh.page2.value=22;document.uhoh.submit();'></center>
	  <input type='hidden' name='page2' value='$page2'>
	  <input type='hidden' name='logout'>
	  <input type='hidden' name='page' value='$page'>
	  </form>");
	 }
	}
   }

   // If all is good, insert the new parent into the database //
   $query = mysql_query("INSERT INTO parents VALUES('', '$_POST[username]', '$_POST[fname]', '$_POST[lname]')")
	 or die("ManageParents.php: Unable to insert new parent - " . mysql_error());

   // Get the parentid we just entered //
   $query = mysql_query("SELECT parentid FROM parents WHERE userid='$_POST[username]'")
	 or die("Unable to get the newly entered parentid - ".mysql_error());

   $parentid = mysql_result($query,0);

   // Also insert the "registered" grouping of parent and student //
   $query = mysql_query("INSERT INTO parent_student_match VALUES('', '$parentid', '$_POST[student]')")
	 or die("ManageParents.php: Unable to insert the parent to student match - ".mysql_error());
  }
 }

 // Edit the parent if one is being edited //
 if($_POST["editparent"] == 1 && $_POST["username"] != "" && $_POST["fname"] != "" && $_POST["lname"] != "" && $_POST["student"] != "")
 {
  $query = mysql_query("UPDATE `parents` SET `userid`='$_POST[username]', `fname`='$_POST[fname]', `lname`='$_POST[lname]' WHERE `parentid`='$_POST[parentid]' LIMIT 1")
	or die("ManageParents.php: Unable to update the parent information - ".mysql_error());
 }

 // Delete the parent(s) that the parent has requested as well as the classes belonging to those parents //
 if($_POST["deleteparent"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   deleteParent($delete[$i]);
  }
 }

 print("<script language='JavaScript'>

  // Function to make sure the parent wants to delete the parent(s) //
  function validate()
  {
   if( document.parents.selectparent.value > 0 )
   {
	var confirmed = confirm(\"Are you sure you want to delete this parent?\");

	if( confirmed == true )
	{
	 document.parents.submit();
	}
   }
   else
   {
	alert('You must select a parent to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.parents.selectparent.value == 1 )
   {
	document.parents.submit();
   }
   else
   {
	if( document.parents.selectparent.value > 1 )
	{
	 alert('You can only edit one parent at a time.');
	}
	else
	{
	 alert('You must select a parent to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row + 2;

   if(document.parents.elements[row].checked)
   {
	document.parents.selectparent.value = Math.round(document.parents.selectparent.value) + 1;
   }
   else
   {
	document.parents.selectparent.value = Math.round(document.parents.selectparent.value) - 1;
   }
  }
 </script>

 <h1>Manage Parents</h1>
 <br><br>
 <table align='center' width='500' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='parents' action='./index.php' method='POST'>
  <input type='button' value='Add' onClick='document.parents.page2.value=23;document.parents.submit();'>
  <input type='button' value='Edit' onClick='document.parents.page2.value=24;checkboxes();'>
  <input type='button' value='Delete' onClick='document.parents.deleteparent.value=1;validate();'>
  <br><br>
  <table cellspacing='0' width='500' cellpadding='8' class='dynamiclist'>
   <tr class='header'>
	<td>&nbsp;</td>
	<th>First Name</th>
	<th>Last Name</th>
	<th>Student Name</th>
	<th>Username</th>
   </tr>");

   // Get the total number of parents to know how many pages to have //
   $query = mysql_query("SELECT COUNT(*) FROM parents")
	 or die("ManageParents.php: Unable to retrieve total number of parents - ".mysql_error());

   $numrows = mysql_result($query,0);

   $numpages = ceil($numrows / 25);

   if($_POST["onpage"] == "")
   {
	$_POST["onpage"]=1;
   }

   // Get and display the parents //
   $query = mysql_query("SELECT p.parentid,p.fname,p.lname,u.username FROM parents p, users u WHERE p.userid = u.userid")
			or die("ManageParents.php: Unable to get a list of parents - ".mysql_error());
   $row = 0;
   $actualrow = 0;
   while($parent = mysql_fetch_row($query))
   {
	$row++;

	$q = mysql_query("SELECT s.fname, s.lname, s.studentid FROM students s, parent_student_match m WHERE m.studentid = s.studentid AND m.parentid = '$parent[0]'")
		 or die("ManageParents.php: Unable to get a list of parents with the matching students - ".mysql_error());
	$student = mysql_fetch_row($q);

	if($row > ($_POST["onpage"]*25)-25 && $row <= ($_POST["onpage"]*25))
	{
	 $actualrow++;
	 print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	  <td><input type='checkbox' name='delete[]' value='$parent[0]' onClick='updateboxes($actualrow);document.parents.studentid.value=".($student[2]==NULL ? "-1" : "$student[2]").";' /></td>
	  <td>$parent[1]</td>
	  <td>$parent[2]</td>
	  <td>$student[0] $student[1]</td>
	  <td>$parent[3]</td>
	 </tr>");
	}
   }

 print(" </table>
  <br>
  <input type='button' value='Add' onClick='document.parents.page2.value=23;document.parents.submit();'>
  <input type='button' value='Edit' onClick='document.parents.page2.value=24;checkboxes();'>
  <input type='button' value='Delete' onClick='document.parents.deleteparent.value=1;validate();'>
  <br><br>

  <center>Page: ");

  for($i=1; $i<=$numpages; $i++)
  {
   if($i == $_POST["onpage"])
   {
	print("<a href='JavaScript: document.parents.deleteparent.value=0;document.parents.page2.value=3;document.parents.onpage.value=$i;document.parents.submit();' class='selectedpagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
   else
   {
	print("<a href='JavaScript: document.parents.deleteparent.value=0;document.parents.page2.value=3;document.parents.onpage.value=$i;document.parents.submit();' class='pagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
  }

print("\n</center>
  <input type='hidden' name='deleteparent'>
  <input type='hidden' name='selectparent'>
  <input type='hidden' name='studentid'>
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