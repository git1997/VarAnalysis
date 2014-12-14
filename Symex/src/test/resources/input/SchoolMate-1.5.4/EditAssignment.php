<?php
 $id = $_POST["delete"];

 // Get the information for the current assignment //
 $query = mysql_query("SELECT * FROM assignments WHERE assignmentid = $id[0]")
   or die("EditAssignment.php: Unable to retrieve the information about the assignment to edit - ".mysql_error());

 $assignment = mysql_fetch_row($query);
 
 print("<h1>Edit Assignment</h1>

  <form name='editassignment' action='./index.php' method='POST'>
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
	<td><input type='text' name='title' maxlength='15' size='15' value='$assignment[2]' /></td>
	<td><textarea name='task'>$assignment[6]</textarea></td>
	<td><input type='text' name='total' maxlength='6' size='15' value='".number_format($assignment[3],0)."' /></td>
	<td><input type='text' name='assigneddate' maxlength='10' size='15' value='".convertfromdb($assignment[4])."' /></td>
	<td><input type='text' name='duedate' maxlength='10' size='15' value='".convertfromdb($assignment[5])."' /></td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='700'>
   <tr>
   <td>
	<input type='button' value='Edit Assignment' onClick='document.editassignment.editassignment.value=1;document.editassignment.page2.value=2;document.editassignment.submit();'>
	<input type='button' value='Cancel' onClick='document.editassignment.page2.value=2;document.editassignment.submit();'>
   </td>
   </tr>
   </table>

  <input type='hidden' name='editassignment'>
  <input type='hidden' name='assignmentid' value='$id[0]'>
  <input type='hidden' name='page2' value='$page2'>
  <input type='hidden' name='selectclass' value='$_POST[selectclass]' />
  <input type='hidden' name='logout'>
  <input type='hidden' name='wastotal' value='$assignment[3]'>
  <input type='hidden' name='wasdate' value='$assignment[5]'>
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