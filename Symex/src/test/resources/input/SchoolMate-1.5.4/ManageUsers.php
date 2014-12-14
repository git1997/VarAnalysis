<?php

 // Add the new user if one is being added //
 if($_POST["adduser"] == 1 && $_POST["password"] != "" && $_POST["type"] != "")
 {
  $query = mysql_query("SELECT username FROM users")
	or die("ManageUsers.php: Uanable to get list of users - ".mysql_error());

  // Make sure that the supplied username does not already exist in the database //
  while( $userlist = mysql_fetch_row($query) )
  {
   if($_POST["username"] == $userlist[0])
   {
	die("<br><br><h1 align='center'><font color='red'>Username already exists!</font></h1>
	<br>
	<form name='uhoh' action='./index.php' method='POST'>
	<center><input type='button' value='&nbsp;Back&nbsp;' onClick='document.uhoh.page2.value=10;document.uhoh.submit();'></center>
	<input type='hidden' name='page2' value='$page2'>
	<input type='hidden' name='logout'>
	<input type='hidden' name='page' value='$page'>
	</form>");
   }
  }

  // If all is good, insert the new user into the database //
  $query = mysql_query("INSERT INTO users VALUES('', '$_POST[username]', '".md5($_POST[password])."', '$_POST[type]')")
	or die("ManageUsers.php: Unable to insert new user - " . mysql_error());
 }

 // Edit the user if one is being edited //
 if($_POST["edituser"] == 1 && $_POST["password"] != "" && $_POST["type"] != "")
 {
  if($_POST["password"] != "")
  {
   $query = mysql_query("UPDATE `users` SET `username`='$_POST[username]', `password`='".md5($_POST["password"])."', `type`='$_POST[type]' WHERE `userid`='$_POST[userid]' LIMIT 1")
	 or die("ManageUsers.php: Unable to update the user information (password) - ".mysql_error());
  }
  else
  {
   $query = mysql_query("UPDATE `users` SET `username`='$_POST[username]', `type`='$_POST[type]' WHERE `userid`='$_POST[userid]' LIMIT 1")
	 or die("ManageUsers.php: Unable to update the user information (no password) - ".mysql_error());
  }
 }

 // Delete the user(s) that the user has requested as well as the classes belonging to those users //
 if($_POST["deleteuser"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   deleteUser($delete[$i]);
  }
 }

 print("<script language='JavaScript'>

  // Function to make sure the user wants to delete the user(s) //
  function validate()
  {
   if( document.users.selectuser.value > 0 )
   {
	var confirmed = confirm(\"Deleting a user will also delete that student/teacher/parent from the database.\\n\\nAre you sure you want to delete this user?\");

	if( confirmed == true )
	{
	 document.users.submit();
	}
   }
   else
   {
	alert('You must select a user to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.users.selectuser.value == 1 )
   {
	document.users.submit();
   }
   else
   {
	if( document.users.selectuser.value > 1 )
	{
	 alert('You can only edit one user at a time.');
	}
	else
	{
	 alert('You must select a user to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row + 2;
   if(document.users.elements[row].checked)
   {
	document.users.selectuser.value = Math.round(document.users.selectuser.value) + 1;
   }
   else
   {
	document.users.selectuser.value = Math.round(document.users.selectuser.value) - 1;
   }
  }
 </script>

 <h1>Manage Users</h1>
 <br><br>
 <table align='center' width='250' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='users' action='./index.php' method='POST'>
  <input type='button' value='Add' onClick='document.users.page2.value=14;document.users.submit();'>
  <input type='button' value='Edit' onClick='document.users.page2.value=15;checkboxes();'>
  <input type='button' value='Delete' onClick='document.users.deleteuser.value=1;validate();'>
  <br><br>
  <table cellspacing='0' width='250' cellpadding='8' class='dynamiclist'>
   <tr class='header'>
	<td>&nbsp;</td>
	<th>Username</th>
	<th>Type</th>
   </tr>");

   // Get the total number of users to know how many pages to have //
   $query = mysql_query("SELECT COUNT(*) FROM users")
	 or die("ManageUsers.php: Unable to retrieve total number of users - ".mysql_error());

   $numrows = mysql_result($query,0);

   $numpages = ceil($numrows / 25);

   if($_POST["onpage"] == "")
   {
	$_POST["onpage"]=1;
   }

   // Get and display the users //
   $query = mysql_query("SELECT userid,username,type FROM users")
	 or die("ManageUsers.php: Unable to retrieve user information - ".mysql_error());

   $row = 0;
   $actualrow = 0;

   while($user = mysql_fetch_row($query) )
   {
	$row++;
	if($row > ($_POST["onpage"]*25)-25 && $row <= ($_POST["onpage"]*25))
	{
	 $actualrow++;
	 print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	  <td><input type='checkbox' name='delete[]' value='$user[0]' onClick='updateboxes($actualrow);' /></td>
	  <td>$user[1]</td>
	  <td>$user[2]</td>
	 <tr>");
	}
   }

 print(" </table>
  <br>
  <input type='button' value='Add' onClick='document.users.page2.value=14;document.users.submit();'>
  <input type='button' value='Edit' onClick='document.users.page2.value=15;checkboxes();'>
  <input type='button' value='Delete' onClick='document.users.deleteuser.value=1;validate();'>

  <br><br>

  <center>Page: ");

  for($i=1; $i<=$numpages; $i++)
  {
   if($i == $_POST["onpage"])
   {
	print("<a href='JavaScript: document.users.deleteuser.value=0;document.users.page2.value=10;document.users.onpage.value=$i;document.users.submit();' class='selectedpagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
   else
   {
	print("<a href='JavaScript: document.users.deleteuser.value=0;document.users.page2.value=10;document.users.onpage.value=$i;document.users.submit();' class='pagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
  }


print("\n</center>
  <input type='hidden' name='deleteuser'>
  <input type='hidden' name='selectuser'>
  <input type='hidden' name='onpage' value='$_POST[onpage]'>
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