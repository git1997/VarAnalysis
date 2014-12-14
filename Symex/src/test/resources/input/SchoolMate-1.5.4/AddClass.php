<?php
if($_POST['fullyear']!=1)
{
 print("<h1>Add New Class</h1>

  <form name='addclass' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='800'>
   <tr class='header'>
	<th>Class Name</th>
	<th>Teacher</th>
	<th>Semester</th>
	<th>Section Number</th>
	<th>Room Number</th>
	<th>Period Number</th>
	<th>Substitute</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='title' maxlength='20' /></td>
	<td><select name='teacher'>");

	// print out the list of teachers for the drop-down box //
	$query = mysql_query("SELECT teacherid,fname,lname,userid FROM teachers")
	  or die("AddClass.php: Unable to get list of teachers - ".mysql_error());
	while( $teacher = mysql_fetch_row($query) )
	{
	 $q = mysql_query("SELECT type FROM users WHERE userid = '$teacher[3]'");
	 $type = mysql_result($q,0);
	 if($type == "Teacher")
		 print("<option value='$teacher[0]'>$teacher[1] $teacher[2]</option>\n");
	}

	print("</select>
	</td>
	<td><select name='semester'>");

	// print out the list of semesters for the drop-down box //
	$query = mysql_query("SELECT semesterid, title FROM semesters")
	  or die("AddClass.php: Unable to get list of semesters - ".mysql_error());
	while( $semester = mysql_fetch_row($query) )
	{
	 print("<option value='$semester[0]'>$semester[1]</option>\n");
	}

print("</select></td>
	<td><input type='text' name='sectionnum' maxlength='15' size='6' /></td>
	<td><input type='text' name='roomnum' maxlength='4' size='6' /></td>
	<td><input type='text' name='periodnum' maxlength='2' size='6' /></td>
	<td>
	 <select name='substitute'>");

	// print out the list of substitutes for the drop-down box //
	$query = mysql_query("SELECT teacherid,fname,lname,userid FROM teachers")
	  or die("AddClass.php: Unable to get list of substitutes - ".mysql_error());
	while( $teacher = mysql_fetch_row($query) )
	{
	 $q = mysql_query("SELECT type FROM users WHERE userid = '$teacher[3]'");
	 $type = mysql_result($q,0);
	 if($type == "Substitute")
		 print("<option value='$teacher[0]'>$teacher[1] $teacher[2]</option>\n");
	}

print("	 </select>
	</td>
   </tr>
   </table>

   <br />

   <table cellpadding='0' border='0' align='center' width='800'>
   <tr>
   <td>
   <b>Days of the Week:</b>
   <br />
   <input type='checkbox' value='M' name='Days[]' /> Monday<br />
   <input type='checkbox' value='T' name='Days[]' /> Tuesday<br />
   <input type='checkbox' value='W' name='Days[]' /> Wednesday<br />
   <input type='checkbox' value='H' name='Days[]' /> Thursday<br />
   <input type='checkbox' value='F' name='Days[]' /> Friday
   <br /><br />
   <input type='button' value='Add Class' onClick='document.addclass.addclass.value=1;document.addclass.page2.value=0;document.addclass.submit();'>
   <input type='button' value='Full Year' onClick='document.addclass.fullyear.value=1;document.addclass.page2.value=9;document.addclass.submit();'>
   <input type='button' value='Cancel' onClick='document.addclass.page2.value=0;document.addclass.submit();'></td>
   </tr>
   </table>

  <input type='hidden' name='addclass' value='' />
  <input type='hidden' name='fullyear' />
  <input type='hidden' name='page2' value='$page2' />
  <input type='hidden' name='logout' />
  <input type='hidden' name='page' value='$page' />

 </form>

 <table width='520' border=0 cellspacing=0 cellpadding=0 height=1>
  <tr>
   <td valign='top'>
	&nbsp;
   </td>
  </tr>
 </table>
 ");
}
else
{
 print("<h1>Add New Class</h1>

  <form name='addclass' action='./index.php' method='POST'>
  <br><br><br>
  <table cellspacing='0' cellpadding='5' class='dynamiclist' align='center' width='800'>
   <tr class='header'>
	<th>Class Name</th>
	<th>Teacher</th>
	<th>First Semester</th>
	<th>Second Semester</th>
	<th>Section Number</th>
	<th>Room Number</th>
	<th>Period Number</th>
	<th>Substitute</th>
   </tr>
   <tr class='even'>
	<td><input type='text' name='title' maxlength='20' /></td>
	<td><select name='teacher'>");

	// print out the list of teachers for the drop-down box //
	$query = mysql_query("SELECT teacherid,fname,lname,userid FROM teachers")
	  or die("AddClass.php: Unable to get list of teachers - ".mysql_error());
	while( $teacher = mysql_fetch_row($query) )
	{
	 $q = mysql_query("SELECT type FROM users WHERE userid = '$teacher[3]'");
	 $type = mysql_result($q,0);
	 if($type == "Teacher")
		 print("<option value='$teacher[0]'>$teacher[1] $teacher[2]</option>\n");
	}

	print("</select>
	</td>
	<td><select name='semester'>");

	// print out the list of semesters for the drop-down box //
	$query = mysql_query("SELECT semesterid, title FROM semesters")
	  or die("AddClass.php: Unable to get list of semesters - ".mysql_error());
	while( $semester = mysql_fetch_row($query) )
	{
	 print("<option value='$semester[0]'>$semester[1]</option>\n");
	}

print("</select></td>
	<td><select name='semester2'>");

	// print out the list of semesters for the drop-down box //
	$query = mysql_query("SELECT semesterid, title FROM semesters")
	  or die("AddClass.php: Unable to get list of semesters - ".mysql_error());
	while( $semester = mysql_fetch_row($query) )
	{
	 print("<option value='$semester[0]'>$semester[1]</option>\n");
	}

print("</select></td>
	<td><input type='text' name='sectionnum' maxlength='15' size='6' /></td>
	<td><input type='text' name='roomnum' maxlength='4' size='6' /></td>
	<td><input type='text' name='periodnum' maxlength='2' size='6' /></td>
	<td>
	 <select name='substitute'>");

	// print out the list of substitutes for the drop-down box //
	$query = mysql_query("SELECT teacherid,fname,lname,userid FROM teachers")
	  or die("AddClass.php: Unable to get list of substitutes - ".mysql_error());
	while( $teacher = mysql_fetch_row($query) )
	{
	 $q = mysql_query("SELECT type FROM users WHERE userid = '$teacher[3]'");
	 $type = mysql_result($q,0);
	 if($type == "Substitute")
		 print("<option value='$teacher[0]'>$teacher[1] $teacher[2]</option>\n");
	}

print("     </select>
	</td>
   </tr>
   </table>

   <br />

   <table cellpadding='0' border='0' align='center' width='880'>
   <tr>
   <td>
   <b>Days of the Week:</b>
   <br />
   <input type='checkbox' value='M' name='Days[]' /> Monday<br />
   <input type='checkbox' value='T' name='Days[]' /> Tuesday<br />
   <input type='checkbox' value='W' name='Days[]' /> Wednesday<br />
   <input type='checkbox' value='H' name='Days[]' /> Thursday<br />
   <input type='checkbox' value='F' name='Days[]' /> Friday
   <br /><br />
   <input type='button' value='Add Class' onClick='document.addclass.addclass.value=1;document.addclass.page2.value=0;document.addclass.submit();'>
   <input type='button' value='Full Year' onClick='document.addclass.fullyear.value=1;document.addclass.page2.value=9;document.addclass.submit();'>
   <input type='button' value='Cancel' onClick='document.addclass.page2.value=0;document.addclass.submit();'></td>
   </tr>
   </table>

  <input type='hidden' name='addclass' value='' />
  <input type='hidden' name='fullyear' value='$_POST[fullyear]' />
  <input type='hidden' name='page2' value='$page2' />
  <input type='hidden' name='logout' />
  <input type='hidden' name='page' value='$page' />

 </form>

 <table width='520' border=0 cellspacing=0 cellpadding=0 height=1>
  <tr>
   <td valign='top'>
	&nbsp;
   </td>
  </tr>
 </table>
 ");
}
?>