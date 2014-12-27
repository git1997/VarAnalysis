<?php
 $id = $_POST['delete'];

 $query = mysql_query("SELECT submitdate, points, comment, islate, gradeid FROM grades WHERE studentid = '$id[0]' AND assignmentid = '$_POST[assignment]'")
   or die("EditGrade.php: Unable to retrieve the information about the grade - ".mysql_error());

 $grade = mysql_fetch_row($query);

 print("<h1>Edit Grade</h1>

  <form name='editgrade' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='500'>
   <tr class='header'>
	<th>Student Name</th>
	<th>Date Submitted</th>
	<th>Earned Points</th>
	<th>Comment</th>
	<th>Late</th>
   </tr>
   <tr class='even' valign='top'>
	<td>");

	$query = mysql_query('SELECT fname, lname FROM students WHERE studentid =\''.$id[0].'\'')
			 or die('EditGrade.php: Unable to get the student\'s name - '.mysql_error());

	$student = mysql_fetch_row($query);

	print("$student[0] $student[1]");

print("	</td>
	<td><input type='text' name='gradedate' maxlength='10' size='10' value='".( convertfromdb($grade[0]) != "//" ? convertfromdb($grade[0]) : "")."' /></td>
	<td><input type='text' name='points' maxlength='5' size='5' value='".number_format($grade[1],1)."' /></td>
	<td><textarea cols='20' rows='3' name='comment'>$grade[2]</textarea></td>
	<td><input type='checkbox' name='late' value='1'".($grade[3]==1?"CHECKED":"")." /></td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='500'>
   <tr>
   <td><input type='button' value='Edit Grade' onClick='document.editgrade.editgrade.value=1;document.editgrade.page2.value=3;document.editgrade.submit();'> <input type='button' value='Cancel' onClick='document.editgrade.page2.value=3;document.editgrade.submit();'></td>
   </tr>
   </table>

  <input type='hidden' name='editgrade' value=''>
  <input type='hidden' name='gradeid' value='$grade[4]' />
  <input type='hidden' name='wasgrade' value='".number_format($grade[1],1)."' />
  <input type='hidden' name='wasdate' value='".( convertfromdb($grade[0]) != "//" ? convertfromdb($grade[0]) : "")."' />
  <input type='hidden' name='student' value='$id[0]' />
  <input type='hidden' name='assignment' value='$_POST[assignment]' />
  <input type='hidden' name='selectclass' value='$_POST[selectclass]' />
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