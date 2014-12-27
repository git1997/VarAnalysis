<?php
 $id = $_POST["delete"];

 // Get the information for the current announcement //
 $query = mysql_query("SELECT * FROM schoolbulletins WHERE sbulletinid = $id[0]")
   or die("EditAnnouncement.php: Unable to retrieve the information about the announcement to edit - ".mysql_error());

 $announcement = mysql_fetch_row($query);

 print("<h1>Edit Announcement</h1>

  <form name='editannouncement' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='600'>
   <tr class='header'>
	<th>Title</th>
	<th>Message</th>
	<th>Date</th>
   </tr>
   <tr class='even' valign='top'>
	<td><input type='text' name='title' maxlength='15' value='$announcement[1]' /></td>
	<td><textarea name='message' rows='5' cols='30'>$announcement[2]</textarea></td>
	<td><input type='text' name='date' value='".convertfromdb($announcement[3])."' /></td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='600'>
   <tr>
   <td>
	<input type='button' value='Edit Announcement' onClick='document.editannouncement.editannouncement.value=1;document.editannouncement.page2.value=4;document.editannouncement.submit();'>
	<input type='button' value='Cancel' onClick='document.editannouncement.page2.value=4;document.editannouncement.submit();'>
   </td>
   </tr>
   </table>

  <input type='hidden' name='editannouncement'>
  <input type='hidden' name='announcementid' value='$id[0]'>
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