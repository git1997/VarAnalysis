<?php
 $id = $_POST["delete"];

 // Get the information for the current semester //
 $query = mysql_query("SELECT title, startdate, midtermdate, enddate, type FROM semesters WHERE semesterid = $id[0]")
   or die("EditSemester.php: Unable to retrieve the information about the semester to edit - ".mysql_error());

 $semester = mysql_fetch_row($query);

 $query = mysql_query("SELECT termid,title FROM terms");

 print("<h1>Edit Semester</h1>

  <form name='editsemester' action='./index.php' method='POST'>
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
	<td><input type='text' name='title' maxlength='15' value='$semester[0]' /></td>
	<td><select name='term'>");

	// print out the list of terms for the drop-down box //
	while( $terms = mysql_fetch_row($query) )
	{
	 print("<option value='$terms[0]'>$terms[1]</option>\n");
	}

	print("</select>
	</td>
	<td><input type='text' maxlength='10' name='startdate' size='8' value='".convertfromdb($semester[1])."' /></td>
	<td><input type='text' maxlength='10' name='middate' size='8' value='".convertfromdb($semester[2])."' /></td>
	<td><input type='text' maxlength='10' name='enddate' size='8' value='".convertfromdb($semester[3])."' /></td>
	<td>
	 <select name='half'>
	  <option value='1' ".($semester[4]==1 ? "SELECTED" : "").">First</option>
	  <option value='2' ".($semester[4]==2 ? "SELECTED" : "").">Second</option>
	 </select>
	</td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='600'>
   <tr>
   <td>
	<input type='button' value='Edit Semester' onClick='document.editsemester.editsemester.value=1;document.editsemester.page2.value=5;document.editsemester.submit();'>
	<input type='button' value='Cancel' onClick='document.editsemester.page2.value=5;document.editsemester.submit();'>
   </td>
   </tr>
   </table>

  <input type='hidden' name='editsemester'>
  <input type='hidden' name='semesterid' value='$id[0]'>
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