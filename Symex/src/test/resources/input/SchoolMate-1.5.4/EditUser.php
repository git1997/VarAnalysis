<?php
 $id = $_POST["delete"];

 // Get the information for the current user //
 $query = mysql_query("SELECT username, type FROM users WHERE userid = $id[0]")
   or die("EditUser.php: Unable to retrieve the information about the user to edit - ".mysql_error());

 $user = mysql_fetch_row($query);

 print("<script language='JavaScript'>
 <!--
  function validate()
  {
   if(document.edituser.password.value == '' && document.edituser.password2.value == '')
   {
	document.edituser.submit();
   }
   else
   {
	if(document.edituser.password.value == document.edituser.password2.value)
	{
	 document.edituser.submit();
	}
	else
	{
	 alert('Passwords do not match!');
	}
   }
  }
 -->
 </script>

 <h1>Edit User</h1>

  <form name='edituser' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='550'>
   <tr class='header'>
	<th>Username</th>
	<th>Password</th>
	<th>Confirm Password</th>
	<th>Type</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='username' maxlength='15' value='$user[0]' /></td>
	<td><input type='password' name='password' /></td>
	<td><input type='password' name='password2' /></td>
	<td>
	 <select name='type'>");

	$types = array("Admin", "Teacher", "Substitute", "Student", "Parent");

	$text = "";
	for( $i=0; $i<sizeof($types); $i++)
	{
	 if($types[$i] == $user[1])
	 {
	  $text = "<option value='$types[$i]'>$types[$i]</option>\n".$text;
	 }
	 else
	 {
	  $text .= "<option value='$types[$i]'>$types[$i]</option>\n";
	 }
	}

	print($text);

	print("</select>
	</td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='550'>
   <tr>
   <td>
	<input type='button' value='Edit user' onClick='document.edituser.edituser.value=1;document.edituser.page2.value=10;validate();'>
	<input type='button' value='Cancel' onClick='document.edituser.page2.value=10;document.edituser.submit();'>
   </td>
   </tr>
   </table>

  <input type='hidden' name='edituser'>
  <input type='hidden' name='userid' value='$id[0]'>
  <input type='hidden' name='page2' value='$page2'>
  <input type='hidden' name='logout'>
  <input type='hidden' name='page' value='$page'>

 </form>

 <table width='520' border=0 cellspacing=0 cellpadding=0 height=1>
  <tr>
   <td valign='top'>
	&nbsp;
   </td>
  </tr>
 </table>
 ");
?>