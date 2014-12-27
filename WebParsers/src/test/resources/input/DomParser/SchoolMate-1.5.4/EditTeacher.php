<?php
 $id = $_POST["delete"];

 // Get the information for the current teacher //
 $query = mysql_query("SELECT userid, fname, lname FROM teachers WHERE teacherid = $id[0]")
   or die("EditTeacher.php: Unable to retrieve the information about the teacher to edit - ".mysql_error());

 $teacher = mysql_fetch_row($query);

 print("<h1>Edit Teacher</h1>

  <form name='editteacher' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='550'>
   <tr class='header'>
	<th>First Name</th>
	<th>Last Name</th>
	<th>Username</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='fname' maxlength='15' value='$teacher[1]' /></td>
	<td><input type='test' name='lname' maxlength='15' value='$teacher[2]' /></td>
	<td>
	 <select name='username'>");

	// print out the list of teachers for the drop-down box //
	 $query = mysql_query("SELECT userid FROM teachers WHERE teacherid = $id[0]")
	   or die("AddTeacher: Unable to get the current teacher's userid - ".mysql_error());
	 $currentuserid = mysql_result($query,0);

	 $query = mysql_query("SELECT u.userid,u.username FROM users u LEFT JOIN teachers t ON u.userid = t.userid WHERE t.userid IS NULL AND (u.type='Teacher' OR u.type='Substitute') OR u.userid = $currentuserid")
	   or die("EditTeacher: Unable to retrieve the list of users - ".mysql_error());

	$text = "";
	while( $user = mysql_fetch_row($query) )
	{
	 if($teacher[0] == $user[0])
	 {
	  $text = "<option value='$user[0]'>$user[1]</option>\n".$text;
	 }
	 else
	 {
	  $text .= "<option value='$user[0]'>$user[1]</option>\n";
	 }
	}

	print($text);

print("     </select>
	</td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='550'>
   <tr>
   <td>
	<input type='button' value='Edit teacher' onClick='document.editteacher.editteacher.value=1;document.editteacher.page2.value=3;document.editteacher.submit();'>
	<input type='button' value='Cancel' onClick='document.editteacher.page2.value=3;document.editteacher.submit();'>
   </td>
   </tr>
   </table>

  <input type='hidden' name='editteacher'>
  <input type='hidden' name='teacherid' value='$id[0]'>
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