<?php
 $id = $_POST["delete"];

 // Get the information for the current student //
 $query = mysql_query("SELECT userid, fname, mi, lname FROM students WHERE studentid = $id[0]")
   or die("EditStudent.php: Unable to retrieve the information about the student to edit - ".mysql_error());

 $student = mysql_fetch_row($query);

 print("<h1>Edit Student</h1>

  <form name='editstudent' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='525'>
   <tr class='header'>
	<th>First Name</th>
	<th>Middle Initial</th>
	<th>Last Name</th>
	<th>Username</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='fname' maxlength='15' value='$student[1]' /></td>
	<td><input type='text' name='mi' maxlength='2' size='2' value='$student[2]' /></td>
	<td><input type='test' name='lname' maxlength='15' value='$student[3]' /></td>
	<td>
	 <select name='username'>");

	// print out the list of students for the drop-down box //
	 $query = mysql_query("SELECT userid FROM students WHERE studentid = $id[0]")
	   or die("EditStudent: Unable to get the current student's userid - ".mysql_error());
	 $currentuserid = mysql_result($query,0);

	 $query = mysql_query("SELECT u.userid,u.username FROM users u LEFT JOIN students s ON u.userid = s.userid WHERE s.userid IS NULL AND (u.type='Student') OR u.userid = $currentuserid")
	   or die("EditStudent: Unable to retrieve the list of users - ".mysql_error());

	$text = "";
	while( $user = mysql_fetch_row($query) )
	{
	 if($student[0] == $user[0])
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

   <table cellpadding='0' border='0' align='center' width='525'>
   <tr>
   <td>
	<input type='button' value='Edit Student' onClick='document.editstudent.editstudent.value=1;document.editstudent.page2.value=2;document.editstudent.submit();'>
	<input type='button' value='Cancel' onClick='document.editstudent.page2.value=2;document.editstudent.submit();'>
   </td>
   </tr>
   </table>

  <input type='hidden' name='editstudent'>
  <input type='hidden' name='studentid' value='$id[0]'>
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