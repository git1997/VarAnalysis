<?php
 $id = $_POST["delete"];

 // Get the information for the current parent //
 $query = mysql_query("SELECT userid, fname, lname FROM parents WHERE parentid = $id[0]")
   or die("EditParent.php: Unable to retrieve the information about the parent to edit - ".mysql_error());

 $parent = mysql_fetch_row($query);

 print("<h1>Edit Parent</h1>

  <form name='editparent' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='550'>
   <tr class='header'>
	<th>First Name</th>
	<th>Last Name</th>
	<th>Student Name</th>
	<th>Username</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='fname' maxlength='15' value='$parent[1]' /></td>
	<td><input type='test' name='lname' maxlength='15' value='$parent[2]' /></td>
	<td>
	 <select name='student'>");
	 // Get the list of students for the user to choose from //
	 $query = mysql_query("SELECT s.studentid,s.fname,s.lname FROM students s LEFT JOIN parent_student_match p ON s.studentid = p.studentid WHERE p.studentid IS NULL OR (p.parentid=$id[0] AND p.studentid='$_POST[studentid]')")
	   or die("AddParent: Unable to retrieve the list of users - ".mysql_error());

	$text1 = "";
	while( $students = mysql_fetch_row($query) )
	{
	 if($students[0] == $_POST["studentid"])
	 {
	  $text1 = "<option value='$students[0]'>$students[1] $students[2]</option>\n".$text;
	 }
	 else
	 {
	  $text1 .= "<option value='$students[0]'>$students[1] $students[2]</option>\n";
	 }
	}

	print($text1);

print("  </select>
	</td>
	<td>
	 <select name='username'>");

	// print out the list of parents for the drop-down box //
	 $query = mysql_query("SELECT userid FROM parents WHERE parentid = $id[0]")
	   or die("Addparent: Unable to get the current parent's userid - ".mysql_error());
	 $currentuserid = mysql_result($query,0);

	 $query = mysql_query("SELECT u.userid,u.username FROM users u LEFT JOIN parents p ON u.userid = p.userid WHERE p.userid IS NULL AND (u.type='parent') OR u.userid = $currentuserid")
	   or die("Editparent: Unable to retrieve the list of users - ".mysql_error());

	$text = "";
	while( $user = mysql_fetch_row($query) )
	{
	 if($parent[0] == $user[0])
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
	<input type='button' value='Edit parent' onClick='document.editparent.editparent.value=1;document.editparent.page2.value=22;document.editparent.submit();'>
	<input type='button' value='Cancel' onClick='document.editparent.page2.value=22;document.editparent.submit();'>
   </td>
   </tr>
   </table>

  <input type='hidden' name='editparent'>
  <input type='hidden' name='parentid' value='$id[0]'>
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