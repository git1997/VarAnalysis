<?php

 print("<script language='JavaScript'>
 <!--
 function validate()
 {
  if(document.adduser.password.value == document.adduser.password2.value && document.adduser.password.value != '')
  {
   document.adduser.submit();
  }
  else
  {
   alert('Passwords do not match!');
   document.adduser.password.value = '';
   document.adduser.password2.value = '';
   document.adduser.password.select();
  }
 }
 -->
 </script>

 <h1>Add New User</h1>

  <form name='adduser' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='450'>
   <tr class='header'>
	<th>Username</th>
	<th>Password</th>
	<th>Confirm Password</th>
	<th>Type</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='username' maxlength='15' size='15' /></td>
	<td><input type='password' name='password' maxlength='15' size='15' /></td>
	<td><input type='password' name='password2' maxlength='15' size='15' /></td>
	<td>
	 <select name='type'>
	  <option value='Admin'>Admin</option>
	  <option value='Teacher'>Teacher</option>
      <option value='Substitute'>Substitute</option>
	  <option value='Student'>Student</option>
	  <option value='Parent'>Parent</option>
	 </select>
	</td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='450'>
   <tr>
   <td><input type='button' value='Add User' onClick='document.adduser.adduser.value=1;document.adduser.page2.value=10;validate();'> <input type='button' value='Cancel' onClick='document.adduser.page2.value=10;document.adduser.submit();'></td>
   </tr>
   </table>

  <input type='hidden' name='adduser' value=''>
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