<?php

 print("<h1>Add New Student</h1>

  <form name='addstudent' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='500'>
   <tr class='header'>
	<th>First Name</th>
	<th>Middle Initial</th>
	<th>Last Name</th>
	<th>Username</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='fname' maxlength='15' size='15' /></td>
	<td><input type='text' name='mi' maxlength='2' size='2' /></td>
	<td><input type='text' name='lname' maxlength='15' size='15' /></td>
	<td>
	 <select name='username'>");
	 // Get the list of usernames for the user to choose from //
	 $query = mysql_query("SELECT u.userid,u.username FROM users u LEFT JOIN students s ON u.userid = s.userid WHERE s.userid IS NULL AND (u.type='student')")
	   or die("AddStudent: Unable to retrieve the list of users - ".mysql_error());

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
   <td><input type='button' value='Add Student' onClick='document.addstudent.addstudent.value=1;document.addstudent.page2.value=2;document.addstudent.submit();'> <input type='button' value='Cancel' onClick='document.addstudent.page2.value=2;document.addstudent.submit();'></td>
   </tr>
   </table>

  <input type='hidden' name='addstudent' value=''>
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