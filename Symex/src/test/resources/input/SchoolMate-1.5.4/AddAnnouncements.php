<?php

 print("<h1>Add New Announcement</h1>

  <form name='addannouncement' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='600'>
   <tr class='header'>
	<th>Title</th>
	<th>Message</th>
   </tr>
   <tr class='even' valign='top'>
	<td><input type='text' name='title' maxlength='15' size='15' /></td>
	<td><textarea name='message' rows=5 cols=30></textarea></td>

   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='600'>
   <tr>
   <td><input type='button' value='Add announcement' onClick='document.addannouncement.addannouncement.value=1;document.addannouncement.page2.value=4;document.addannouncement.submit();'> <input type='button' value='Cancel' onClick='document.addannouncement.page2.value=4;document.addannouncement.submit();'></td>
   </tr>
   </table>

  <input type='hidden' name='addannouncement' value=''>
  <input type='hidden' name='page2' value='$page2'>
  <input type='hidden' name='date'>
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