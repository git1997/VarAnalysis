<?php
 $id = $_POST["delete"];

 // Get the information for the current class //
 $query = mysql_query("SELECT coursename, teacherid, semesterid, sectionnum, roomnum, periodnum, dotw, substituteid FROM courses WHERE courseid = $id[0]")
   or die("EditClass.php: Unable to retrieve the information about the class to edit - ".mysql_error());

 $class = mysql_fetch_row($query);

 print("<h1>Edit Class</h1>

  <form name='editclass' action='./index.php' method='POST'>
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
	<td><input type='text' name='title' maxlength='20' value='$class[0]' /></td>
	<td><select name='teacher'>");

	// print out the list of teachers for the drop-down box //
	$query = mysql_query("SELECT teacherid,fname,lname FROM teachers")
	  or die("EditClass.php: Unable to get list of teachers - ".mysql_error());

	$text = "";
	while( $teacher = mysql_fetch_row($query) )
	{
	 if($teacher[0] == $class[1])
	 {
	  $text = "<option value='$teacher[0]'>$teacher[1] $teacher[2]</option>\n".$text;
	 }
	 else
	 {
	  $text .= "<option value='$teacher[0]'>$teacher[1] $teacher[2]</option>\n";
	 }
	}

	print($text);

	print("</select>
	</td>
	<td><select name='semester'>");

	// print out the list of semesters for the drop-down box //
	$query = mysql_query("SELECT semesterid, title FROM semesters")
	  or die("EditClass.php: Unable to get list of semesters - ".mysql_error());

	$text = "";
	while( $semester = mysql_fetch_row($query) )
	{
	 if($semester[0] == $class[2])
	 {
	  $text = "<option value='$semester[0]'>$semester[1]</option>\n".$text;
	 }
	 else
	 {
	  $text .= "<option value='$semester[0]'>$semester[1]</option>\n";
	 }
	}

	print($text);

print("</select></td>
	<td><input type='text' name='sectionnum' maxlength='15' size='6' value='$class[3]' /></td>
	<td><input type='text' name='roomnum' maxlength='4' size='6' value='$class[4]' /></td>
	<td><input type='text' name='periodnum' maxlength='2' size='6' value='$class[5]' /></td>
	<td>
	 <select name='substitute'>");

	// print out the list of substitutes for the drop-down box //
	$query = mysql_query("SELECT teacherid,fname,lname,userid FROM teachers")
	  or die("EditClass.php: Unable to get list of substitutes - ".mysql_error());

	$text = "";
	while( $teacher = mysql_fetch_row($query) )
	{
	 $q = mysql_query("SELECT type FROM users WHERE userid = '$teacher[3]'");
	 $type = mysql_result($q,0);
	 if($type == "Substitute")
	 {
	  if($teacher[0] == $class[7])
	  {
	   $text = "<option value='$teacher[0]'>$teacher[1] $teacher[2]</option>\n".$text;
	  }
	  else
	  {
	   $text .= "<option value='$teacher[0]'>$teacher[1] $teacher[2]</option>\n";
	  }
	 }
	}

print($text);

print("	 </select>
	</td>
   </tr>
   </table>

   <br>

   <table cellpadding='0' border='0' align='center' width='800'>
   <tr>
   <td>
   <b>Days of the Week:</b>
   <br />
   <input type='checkbox' value='M' name='Days[]'");
	if(preg_match("/M/", $class[6]))
	{
	 print("CHECKED");
	}
print(   " /> Monday<br />
   <input type='checkbox' value='T' name='Days[]'");
	if(preg_match("/T/", $class[6]))
	{
	 print("CHECKED");
	}
print(	" /> Tuesday<br />
   <input type='checkbox' value='W' name='Days[]'");
	if(preg_match("/W/", $class[6]))
	{
	 print("CHECKED");
	}
print(    " /> Wednesday<br />
   <input type='checkbox' value='H' name='Days[]'");
	if(preg_match("/H/", $class[6]))
	{
	 print("CHECKED");
	}
print(    " /> Thursday<br />
   <input type='checkbox' value='F' name='Days[]'");
	if(preg_match("/F/", $class[6]))
	{
	 print("CHECKED");
	}
print(    " /> Friday
   <br /><br />
	<input type='button' value='Edit Class' onClick='document.editclass.editclass.value=1;document.editclass.page2.value=0;document.editclass.submit();'>
	<input type='button' value='Cancel' onClick='document.editclass.page2.value=0;document.editclass.submit();'>
   </td>
   </tr>
   </table>

  <input type='hidden' name='editclass' value=''>
  <input type='hidden' name='courseid' value='$id[0]'>
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