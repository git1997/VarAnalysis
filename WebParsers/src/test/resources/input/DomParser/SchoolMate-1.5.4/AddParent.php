<?php

 print("<h1>Add New Parent</h1>

  <form name='addparent' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='500'>
   <tr class='header'>
	<th>First Name</th>
	<th>Last Name</th>
	<th>Student Name</th>
	<th>Username</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='fname' maxlength='15' size='15' /></td>
	<td><input type='text' name='lname' maxlength='15' size='15' /></td>
	<td>
	 <select name='student'>");
	 // Get the list of students for the user to choose from //
	 $query = mysql_query("SELECT s.studentid,s.fname,s.lname FROM students s")
	   or die("AddParent: Unable to retrieve the list of users - ".mysql_error());

	 while($students = mysql_fetch_row($query))
	 {
	  print("<option value='$students[0]'>$students[1] $students[2]</option>\n");
	 }
print("  </select>
	</td>
	<td>
	 <select name='username'>");
	 // Get the list of usernames for the user to choose from //
	 $query = mysql_query("SELECT u.userid,u.username FROM users u WHERE u.type='parent'")
	   or die("AddParent: Unable to retrieve the list of users - ".mysql_error());

	 while($users = mysql_fetch_row($query))
	 {
	  print("<option value='$users[0]'>$users[1]</option>\n");
	 }
print("	 </select>
	</td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='500'>
   <tr>
   <td><input type='button' value='Add Parent' onClick='document.addparent.addparent.value=1;document.addparent.page2.value=22;document.addparent.submit();'> <input type='button' value='Cancel' onClick='document.addparent.page2.value=22;document.addparent.submit();'></td>
   </tr>
   </table>

  <input type='hidden' name='addparent' value=''>
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