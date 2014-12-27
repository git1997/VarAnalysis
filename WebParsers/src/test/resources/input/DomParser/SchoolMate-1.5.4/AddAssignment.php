<?php

 print("<h1>Add New Assignment</h1>

  <form name='addassignment' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='700'>
   <tr class='header'>
	<th>Title</th>
	<th>Assigned Task</th>
	<th>Total Points</th>
	<th>Date Assigned</th>
	<th>Date Due</th>
   </tr>
   <tr class='even' valign='top'>
	<td><input type='text' name='title' maxlength='15' size='15' /></td>
	<td><textarea name='task'></textarea></td>
	<td><input type='text' name='total' maxlength='6' size='15' /></td>
	<td><input type='text' name='assigneddate' maxlength='10' size='15' /></td>
	<td><input type='text' name='duedate' maxlength='10' size='15' /></td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='700'>
   <tr>
   <td><input type='button' value='Add Assignment' onClick='document.addassignment.addassignment.value=1;document.addassignment.page2.value=2;document.addassignment.submit();'> <input type='button' value='Cancel' onClick='document.addassignment.page2.value=2;document.addassignment.submit();'></td>
   </tr>
   </table>

  <input type='hidden' name='addassignment' value=''>
  <input type='hidden' name='page2' value='$page2'>
  <input type='hidden' name='logout'>
  <input type='hidden' name='selectclass' value='$_POST[selectclass]' />
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