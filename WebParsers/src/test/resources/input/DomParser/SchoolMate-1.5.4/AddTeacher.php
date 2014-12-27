<?php

 print("<h1>Add New Teacher</h1>

  <form name='addteacher' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='450'>
   <tr class='header'>
	<th>First Name</th>
	<th>Last Name</th>
	<th>Username</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='fname' maxlength='15' size='15' /></td>
	<td><input type='text' name='lname' maxlength='15' size='15' /></td>
	<td>
	 <select name='username'>");
	 // Get the list of usernames for the user to choose from //
	 $query = mysql_query("SELECT u.userid,u.username FROM users u LEFT JOIN teachers t ON u.userid = t.userid WHERE t.userid IS NULL AND (u.type='Teacher' OR u.type='Substitute')")
	   or die("AddTeacher: Unable to retrieve the list of users - ".mysql_error());

	 while($users = mysql_fetch_row($query))
	 {
	  print("<option value='$users[0]'>$users[1]</option>\n");
	 }
print("	 </select>
	</td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='450'>
   <tr>
   <td><input type='button' value='Add Teacher' onClick='document.addteacher.addteacher.value=1;document.addteacher.page2.value=3;document.addteacher.submit();'> <input type='button' value='Cancel' onClick='document.addteacher.page2.value=3;document.addteacher.submit();'></td>
   </tr>
   </table>

  <input type='hidden' name='addteacher' value=''>
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