<?php
 $id = $_POST["delete"];

 // Get the information for the current term //
 $query = mysql_query("SELECT title, startdate, enddate FROM terms WHERE termid = $id[0]")
   or die("EditTerm.php: Unable to retrieve the information about the term to edit - ".mysql_error());

 $term = mysql_fetch_row($query);

 print("<h1>Edit Term</h1>

  <form name='editterm' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='450'>
   <tr class='header'>
	<th>Term Name</th>
	<th>Start Date</th>
	<th>End Date</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='title' maxlength='15' value='$term[0]' /></td>
	<td><input type='text' name='startdate' value='".convertfromdb($term[1])."' /></td>
	<td><input type='text' name='enddate' value='".convertfromdb($term[2])."' /></td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='450'>
   <tr>
   <td>
	<input type='button' value='Edit Term' onClick='document.editterm.editterm.value=1;document.editterm.page2.value=6;document.editterm.submit();'>
	<input type='button' value='Cancel' onClick='document.editterm.page2.value=6;document.editterm.submit();'>
   </td>
   </tr>
   </table>

  <input type='hidden' name='editterm'>
  <input type='hidden' name='termid' value='$id[0]'>
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