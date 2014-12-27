<?php

 ###############
 #     ADD     #
 ###############

 // Add the new class if one is being added //
 if($_POST["addclass"] == 1 && $_POST["title"] != "" && $_POST["teacher"] != "" && $_POST["semester"] != "" && $_POST["roomnum"] != "" && $_POST["periodnum"] != "")
 {
  if($_POST['fullyear']!=1)
  { $dotw = '';
  // Combine the days of the week to be entered into the database //
  $days = $_POST["Days"];
  for($i=0; $i<sizeof($days); $i++)
  {
   $dotw .= $days[$i];
  }

  // Get the termid for insertion of the class //
  $query = mysql_query('SELECT termid FROM semesters WHERE semesterid = '.$_POST["semester"]);
  $termid = @mysql_result($query,0);

  $query = mysql_query("INSERT INTO courses VALUES('', '$_POST[semester]', '$termid', '$_POST[title]', '$_POST[teacher]', '$_POST[sectionnum]', '$_POST[roomnum]', '$_POST[periodnum]','','','','','','','','','$dotw','$_POST[substitute]','')")
	or die("ManageClasses.php: Unable to insert new class - " . mysql_error());
  }
  else
  {
   // Make sure they haven't selected the same id for both semesters //
   if ($_POST['semester']!=$_POST['semester2'])
   {
   // Combine the days of the week to be entered into the database //
   $days = $_POST["Days"];
   for($i=0; $i<sizeof($days); $i++)
   {
	$dotw .= $days[$i];
   }

   // Insert the class for the first semester //
   $query = mysql_query("INSERT INTO courses VALUES('', '$_POST[semester]', '$termid', '$_POST[title]', '$_POST[teacher]', '$_POST[sectionnum]', '$_POST[roomnum]', '$_POST[periodnum]','','','','','','','','','$dotw','$_POST[substitute]', '')")
	or die("ManageClasses.php: Unable to insert new class - " . mysql_error());

   // Get it's ID //
   $course1 = mysql_insert_id();

   // Insert the class for the second semester //
   $query = mysql_query("INSERT INTO courses VALUES('', '$_POST[semester2]', '$termid', '$_POST[title]', '$_POST[teacher]', '$_POST[sectionnum]', '$_POST[roomnum]', '$_POST[periodnum]','','','','','','','','','$dotw','$_POST[substitute]', '')")
	or die("ManageClasses.php: Unable to insert new class - " . mysql_error());

   // Get it's ID //
   $course2 = mysql_insert_id();

   // Update the classes so that they'll know about each other //
   $query = mysql_query("UPDATE courses SET secondcourseid = $course2 WHERE courseid = $course1");
   $query = mysql_query("UPDATE courses SET secondcourseid = $course1 WHERE courseid = $course2");
   }
  }
 }

 ##############
 #    EDIT    #
 ##############

 // Edit the class if one is being edited //
 if($_POST["editclass"] == 1 && $_POST["title"] != "" && $_POST["teacher"] != "" && $_POST["semester"] != "" && $_POST["roomnum"] != "" && $_POST["periodnum"] != "")
 {
  // Combine the days of the week to be entered into the database //
  $days = $_POST["Days"];
  for($i=0; $i<sizeof($days); $i++)
  {
   $dotw .= $days[$i];
  }

  $query = mysql_query("UPDATE `courses` SET `coursename`='$_POST[title]', `teacherid`='$_POST[teacher]', `semesterid`='$_POST[semester]', `sectionnum`='$_POST[sectionnum]', `roomnum`='$_POST[roomnum]', `periodnum`='$_POST[periodnum]', `dotw`='$dotw', `substituteid`='$_POST[substitute]' WHERE `courseid`='$_POST[courseid]' LIMIT 1")
	or die("ManageClasses.php: Unable to update the class information - ".mysql_error());
 }

 ##############
 #   DELETE   #
 ##############

 // Delete the classess that the user has requested as well as the classes belonging to those classess //
 if($_POST["deleteclass"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   $query = mysql_query("SELECT secondcourseid FROM courses WHERE courseid = $delete[$i]");
   $secondclass = mysql_fetch_row($query);

   deleteCourse($delete[$i]);
   deleteCourse($secondclass[0]);
  }
 }

 print("<script language='JavaScript'>
  // Function to make sure the user wants to delete the class(es) //
  function validate()
  {
   if( document.classes.selectclass.value > 0 )
   {
	var confirmed = confirm(\"Deleteing a class will also delete the class bulletins, tardies, attendance, assignments, and registrations that occured during that class. \\n \\nAre you sure?\");

	if( confirmed == true )
	{
	 document.classes.submit();
	}
   }
   else
   {
	alert('You must select a class to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.classes.selectclass.value == 1 )
   {
	document.classes.submit();
   }
   else
   {
	if( document.classes.selectclass.value > 1 )
	{
	 alert('You can only edit one class at a time.');
	}
	else
	{
	 alert(document.classes.selectclass.value);
	 alert('You must select a class to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row + 2;
   if(document.classes.elements[row].checked)
   {
	document.classes.selectclass.value = Math.round(document.classes.selectclass.value) - 1;
   }
   else
   {
	document.classes.selectclass.value = Math.round(document.classes.selectclass.value) + 1;
   }
  }
 </script>

 <h1>Manage Classes</h1>
 <br>
 <form name='classes' action='./index.php' method='POST'>

 <table align='center' width='900' cellspacing='0' cellpadding='0' border='0'>
 <b>Semester: </b> <select name='semester' onChange='document.classes.submit();'>
");
  // Get a list of semesters //
  $query = mysql_query("SELECT semesterid, title FROM semesters ORDER BY startdate DESC")
	or die("ViewCourses.php: Unable to get a list of semesters for drop-down - ".mysql_error());

  if($_POST['semester']==NULL)
  {
	$q = mysql_query("SELECT semesterid FROM semesters WHERE startdate < CURDATE() < enddate ORDER BY startdate");
	$temp = mysql_fetch_row($q);
	$_POST['semester'] = $temp[0];
  }

  $count=0;
  $all = "";

  while($semester = mysql_fetch_row($query))
  {
   if($count==0)
	$all = " $semester[0]";
   else
	$all .= " OR semesterid = $semester[0]";

   print("<option value='$semester[0]' ".( $_POST['semester']==$semester[0]&&$_POST['semester']!=NULL ? "SELECTED" : "").">$semester[1]</option>");

   $count++;
  }

  print("
   <option value='-1' ".($_POST['semester']==-1 ? "SELECTED" : "").">All</option>");

  if($_POST['semester']==-1)
	$_POST['semester']=$all;

print("	 </select>
	 <br><br>
   </td>
  </tr>
 <tr>
 <td>
  <input type='button' value='Add' onClick=\"document.classes.page2.value='9';document.classes.submit();\">
  <input type='button' value='Edit' onClick='document.classes.page2.value=11;checkboxes();'>
  <input type='button' value='Delete' onClick='document.classes.deleteclass.value=1;validate();'>
  <input type='button' value='Show in Grid' onClick='document.classes.page2.value=25;document.classes.submit();' />
  <br><br>
  <table cellspacing='0' width='900' cellpadding='5' class='dynamiclist'>
   <tr class='header'>
	<td>&nbsp;</td>
	<th>Class Name</th>
	<th>Teacher</th>
	<th>Semester</th>
	<th>Section Number</th>
	<th>Room Number</th>
	<th>Period Number</th>
	<th>Days</th>
	<th>Substitute</th>
   </tr>");

   // Get the total number of teachers to know how many pages to have //
   if($_POST['semester']!="")
   {
   $query = mysql_query("SELECT COUNT(*) FROM courses WHERE semesterid = $_POST[semester]")
	 or die("$_POST[semester] - ManageClasses.php: Unable to retrieve total number of classes - ".mysql_error());

   $numrows = mysql_result($query,0);


   $numpages = ceil($numrows / 25);

   if($_POST["onpage"] == "")
   {
	$_POST["onpage"]=1;
   }

   // Get and display the classes //

   $query = mysql_query("SELECT c.courseid, c.semesterid, c.coursename, c.sectionnum, c.roomnum, c.periodnum, c.teacherid, c.dotw, c.substituteid  FROM courses c WHERE c.semesterid=$_POST[semester]")
	 or die(mysql_error());
   $row = 0;
   while($class = mysql_fetch_row($query))
   {
	$row++;

	if($row > ($_POST["onpage"]*25)-25 && $row <= ($_POST["onpage"]*25))
	{
	 $actualrow++;

	$q = mysql_query("SELECT s.title FROM semesters s WHERE s.semesterid = $class[1] LIMIT 1")
	  or die("ManageClasses.php: Unable to get the semester title for the current course being displayed - ".mysql_error());
	$semester = mysql_fetch_row($q);

	$q = mysql_query("SELECT t.fname, t.lname FROM teachers t WHERE t.teacherid = $class[6] LIMIT 1")
	  or die("ManageClasses.php: Unable to get the teacher name for the current course being displayed - ".mysql_error());
	$teacher = mysql_fetch_row($q);

	$q = mysql_query("SELECT t.fname, t.lname FROM teachers t where t.teacherid = '$class[8]' LIMIT 1")
	  or die("ManageClasses.php: Unable to get the substitute name for the current corse being displayed - ".mysql_error());
	$sub = mysql_fetch_row($q);

	 print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	  <td><input type='checkbox' name='delete[]' value='$class[0]' onClick='updateboxes($actualrow);' /></td>
	  <td>$class[2]</td>
	  <td>$teacher[0] $teacher[1]</td>
	  <td>$semester[0]</td>
	  <td>$class[3]</td>
	  <td>$class[4]</td>
	  <td>$class[5]</td>
	  <td>$class[7]</td>
	  <td>$sub[0] $sub[1]</td>
	 </tr>");
	}
   }
  }
  else
  {
	$numpages = 1;
	$_POST['onpage']=1;
  }

 print(" </table>
  <br>
  <input type='button' value='Add' onClick='document.classes.page2.value=9;document.classes.submit();'>
  <input type='button' value='Edit' onClick='document.classes.page2.value=11;checkboxes();'>
  <input type='button' value='Delete' onClick='document.classes.deleteclass.value=1;validate();'>
  <input type='button' value='Show in Grid' onClick='document.classes.page2.value=25;document.classes.submit();' />

  <br><br>
  <font color='red'>* Deleting a class will also delete the information for that class</font>
  <br><br>

  <center>Page: ");

  for($i=1; $i<=$numpages; $i++)
  {
   if($i == $_POST["onpage"])
   {
	print("<a href='JavaScript: document.classes.deleteclass.value=0;document.classes.page2.value=0;document.classes.onpage.value=$i;document.classes.submit();' class='selectedpagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
   else
   {
	print("<a href='JavaScript: document.classes.deleteclass.value=0;document.classes.page2.value=0;document.classes.onpage.value=$i;document.classes.submit();' class='pagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
  }

print("\n</center>
  <input type='hidden' name='deleteclass'>
  <input type='hidden' name='selectclass'>
  <input type='hidden' name='page2' value='$page2'>
  <input type='hidden' name='onpage' value='$_POST[onpage]'>
  <input type='hidden' name='logout'>
  <input type='hidden' name='page' value='$page'>
 </form>
 </td>
 </tr>
 </table>
 <table width='520' border=0 cellspacing=0 cellpadding=0 height=1>
  <tr>
   <td valign='top'>
   <empty>
   </td>
  </tr>
 </table>
 ");
?>