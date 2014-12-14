<?php

 $query = mysql_query("SELECT termid,title FROM terms");

 print("<h1>Add New Semester</h1>

  <form name='addsemester' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='600'>
   <tr class='header'>
	<th>Semester Name</th>
	<th>Term</th>
	<th>Start Date</th>
	<th>Midterm Date</th>
	<th>End Date</th>
	<th>Half</th>
   </tr>
   <tr class='even'>
    <td><input type='text' name='title' maxlength='15' /></td>
	<td><select name='term'>");

	// print out the list of terms for the drop-down box //
	while( $terms = mysql_fetch_row($query) )
	{
	 print("<option value='$terms[0]'>$terms[1]</option>\n");
	}

	print("</select>
	</td>
	<td><input type='text' name='startdate' maxlength='10' size='10' /></td>
	<td><input type='text' name='middate' maxlength='10' size='10' /></td>
	<td><input type='text' name='enddate' maxlength='10' size='10' /></td>
	<td>
	 <select name='half'>
	  <option value='1'>First</option>
	  <option value='2'>Second</option>
	 </select>
	</td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='600'>
   <tr>
   <td><input type='button' value='Add Semester' onClick='document.addsemester.addsemester.value=1;document.addsemester.page2.value=5;document.addsemester.submit();'> <input type='button' value='Cancel' onClick='document.addsemester.page2.value=5;document.addsemester.submit();'></td>
   </tr>
   </table>

  <input type='hidden' name='addsemester' value=''>
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