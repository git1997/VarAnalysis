<?php

 require_once("DBFunctions.php");

 // Get the coursename //
 $query = mysql_query("SELECT coursename FROM courses WHERE courseid = '$_POST[selectclass]'") or die("ManageAssignments.php: Unable to get the course name - ".mysql_error());
 $coursename = mysql_result($query,0);

 ###############
 #     ADD     #
 ###############

 // Add the new assignment if one is being added //
 if($_POST["addassignment"] == 1)
 {
  if($_POST["title"] != "" && $_POST["total"] != "" && $_POST["assigneddate"] != "" && $_POST["duedate"] != "")
  {
   // Convert the dates into the db's format //
   $_POST['assigneddate'] = converttodb($_POST['assigneddate']);
   $_POST['duedate'] = converttodb($_POST['duedate']);

   // Get the semesterid and termid for this Assignment by using the courseid //
   $query = mysql_query('SELECT semesterid,termid FROM courses WHERE courseid = '.$_POST["selectclass"]);
   $ids = mysql_fetch_row($query);

   // If all is good, insert the new assignment into the database //
   $query = mysql_query("INSERT INTO assignments VALUES('', '$_POST[selectclass]', '$ids[0]', '$ids[1]', '$_POST[title]', '$_POST[total]', '$_POST[assigneddate]', '$_POST[duedate]', '$_POST[task]')")
	 or die("ManageAssignments.php: Unable to insert new assignment - " . mysql_error());

   // Add the points for this assignment to the total points for the class //
   $query      = mysql_query("SELECT midtermdate FROM semesters WHERE startdate < CURDATE() AND CURDATE() < enddate");
   $dates      = mysql_fetch_row($query);
   $middate    = strtotime($dates[0]);
   $assigndate = strtotime($_POST['assigneddate']);

   if($assigndate < $middate)
	 $query = mysql_query("UPDATE courses SET q1points = (q1points + $_POST[total]), totalpoints = (totalpoints + $_POST[total]) WHERE courseid = $_POST[selectclass]");
   else
	 $query = mysql_query("UPDATE courses SET q2points = (q2points + $_POST[total]), totalpoints = (totalpoints + $_POST[total]) WHERE courseid = $_POST[selectclass]");
  }
 }

 ################
 #     EDIT     #
 ################

 // Edit the assignment if one is being edited //
 if($_POST["editassignment"] == 1 )
 {
  require_once("DBFunctions.php");
  $query = mysql_query("UPDATE `assignments` SET title = '$_POST[title]', assignmentinformation = '$_POST[task]', totalpoints = '$_POST[total]', assigneddate = '".converttodb($_POST[assigneddate])."', duedate = '".converttodb($_POST[duedate])."' WHERE `assignmentid`='$_POST[assignmentid]' LIMIT 1")
	or die("ManageAssignments.php: Unable to update the assignment information - ".mysql_error());

  // Update the amount of points the student has accumulated for this class //
  $query = mysql_query("SELECT semesterid FROM courses WHERE courseid = $_POST[selectclass]");
  $id = mysql_fetch_row($query);

  $query = mysql_query("SELECT midtermdate FROM semesters WHERE semesterid = $id[0]");
  $dates = mysql_fetch_row($query);

  $middate    = strtotime($dates[0]);
  $duedate    = strtotime($_POST['duedate']);
  $wasdate    = strtotime($_POST['wasdate']);

  if($duedate < $middate)
  {
   if($wasdate < $middate)
	$query = mysql_query("UPDATE courses SET q1points=(q1points + $_POST[total] - $_POST[wastotal]), totalpoints=(totalpoints + $_POST[total] - $_POST[wastotal]) WHERE courseid = $_POST[selectclass]");
   else
	$query = mysql_query("UPDATE courses SET q2points=(q2points - $_POST[wastotal]), q1points=(q1points + $_POST[total]), totalpoints=(totalpoints + $_POST[total] - $_POST[wastotal]) WHERE courseid = $_POST[selectclass]");
  }
  else
  {
   if($wasdate < $middate)
	$query = mysql_query("UPDATE courses SET q1points=(q1points - $_POST[wastotal]), q2points=(q2points + $_POST[total]), totalpoints=(totalpoints + $_POST[total] - $_POST[wastotal]) WHERE courseid = $_POST[selectclass]");
   else
	$query = mysql_query("UPDATE courses SET q2points=(q2points + $_POST[total] - $_POST[wastotal]), totalpoints=(totalpoints + $_POST[total] - $_POST[wastotal]) WHERE courseid = $_POST[selectclass]");
  }
 }

 ##################
 #     DELETE     #
 ##################

 // Delete the assignment(s) that the assignment has requested as well as the classes belonging to those assignments //
 if($_POST["deleteassignment"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   // Make sure to update the total points for the class before deleting //
   $q = mysql_query("SELECT totalpoints, duedate FROM assignments WHERE assignmentid = $delete[$i]");
   $total = mysql_fetch_row($q);

   $q = mysql_query("SELECT semesterid FROM courses WHERE courseid = $_POST[selectclass]");
   $id = mysql_fetch_row($q);

   $q = mysql_query("SELECT midtermdate FROM semesters WHERE semesterid = $id[0]");
   $middate = mysql_fetch_row($q);

   $duedate = strtotime($total[1]);
   $middate = strtotime($middate[0]);

   if($duedate < $middate)
	$q = mysql_query("UPDATE courses SET totalpoints = (totalpoints - $total[0]), q1points = (q1points - $total[0]) WHERE courseid = $_POST[selectclass]");
   else
	$q = mysql_query("UPDATE courses SET totalpoints = (totalpoints - $total[0]), q2points = (q2points - $total[0]) WHERE courseid = $_POST[selectclass]");

   deleteAssignments($delete[$i]);
  }
 }

 print("<script language='JavaScript'>

  // Function to make sure the assignment wants to delete the assignment(s) //
  function validate()
  {
   if( document.assignments.selectassignment.value > 0 )
   {
	var confirmed = confirm(\"Are you sure you want to delete this assignment?\");

	if( confirmed == true )
	{
	 document.assignments.submit();
	}
   }
   else
   {
	alert('You must select a assignment to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.assignments.selectassignment.value == 1 )
   {
	document.assignments.submit();
   }
   else
   {
	if( document.assignments.selectassignment.value > 1 )
	{
	 alert('You can only edit one assignment at a time.');
	}
	else
	{
	 alert('You must select a assignment to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row + 2;
   if(document.assignments.elements[row].checked)
   {
	document.assignments.selectassignment.value = Math.round(document.assignments.selectassignment.value) + 1;
   }
   else
   {
	document.assignments.selectassignment.value = Math.round(document.assignments.selectassignment.value) - 1;
   }
  }
 </script>

 <h1>Manage Assignments</h1>
 <br><br>
 <table align='center' width='600' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='assignments' action='./index.php' method='POST'>
  <input type='button' value='Add' onClick='document.assignments.page2.value=4;document.assignments.submit();'>
  <input type='button' value='Edit' onClick='document.assignments.page2.value=5;checkboxes();'>
  <input type='button' value='Delete' onClick='document.assignments.deleteassignment.value=1;validate();'>
  <br><br>
  <table cellspacing='0' width='600' cellpadding='8' class='dynamiclist'>
   <tr class='header'>
	<th colspan='6'><h2>$coursename</th>
   </tr>
   <tr class='header'>
	<td>&nbsp;</td>
	<th>Title</th>
	<th>Assigned Task</th>
	<th>Possible Points</th>
	<th>Date Assigned</th>
	<th>Date Due</th>
   </tr>");

   // Get the total number of assignments to know how many pages to have //
   $query = mysql_query("SELECT COUNT(*) FROM assignments")
	 or die("ManageAssignments.php: Unable to retrieve total number of assignments - ".mysql_error());

   $numrows = mysql_result($query,0);

   $numpages = ceil($numrows / 25);

   if($_POST["onpage"] == "")
   {
	$_POST["onpage"]=1;
   }

   // Get and display the assignments //
   $query = mysql_query("SELECT assignmentid, title, totalpoints, assigneddate, duedate, assignmentinformation FROM assignments WHERE courseid = $_POST[selectclass] ORDER BY assigneddate DESC")
			or die("ManageAssignments.php: Unable to get a list of assignments - ".mysql_error());
   $row = 0;
   $actualrow = 0;
   while($assignment = mysql_fetch_row($query))
   {
	$row++;

	if($row > ($_POST["onpage"]*25)-25 && $row <= ($_POST["onpage"]*25))
	{
	 $actualrow++;

	 $assignment[2] = number_format($assignment[2],0);
	 $assignment[3] = convertfromdb($assignment[3]);
	 $assignment[4] = convertfromdb($assignment[4]);

	 print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	  <td><input type='checkbox' name='delete[]' value='$assignment[0]' onClick='updateboxes($actualrow);' /></td>
	  <td>$assignment[1]</td>
	  <td style='text-align: left;'>$assignment[5]</td>
	  <td>$assignment[2]</td>
	  <td>$assignment[3]</td>
	  <td>$assignment[4]</td>
	 </tr>");
	}
   }

 print(" </table>
  <br>
  <input type='button' value='Add' onClick='document.assignments.page2.value=4;document.assignments.submit();'>
  <input type='button' value='Edit' onClick='document.assignments.page2.value=5;checkboxes();'>
  <input type='button' value='Delete' onClick='document.assignments.deleteassignment.value=1;validate();'>
  <br><br>

  <center>Page: ");

  for($i=1; $i<=$numpages; $i++)
  {
   if($i == $_POST["onpage"])
   {
	print("<a href='JavaScript: document.assignments.deleteassignment.value=0;document.assignments.page2.value=2;document.assignments.onpage.value=$i;document.assignments.submit();' class='selectedpagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
   else
   {
	print("<a href='JavaScript: document.assignments.deleteassignment.value=0;document.assignments.page2.value=2;document.assignments.onpage.value=$i;document.assignments.submit();' class='pagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
  }

print("\n</center>
  <input type='hidden' name='deleteassignment'>
  <input type='hidden' name='selectassignment'>
  <input type='hidden' name='page2' value='$page2'>
  <input type='hidden' name='onpage' value='$_POST[onpage]'>
  <input type='hidden' name='logout'>
  <input type='hidden' name='selectclass' value='$_POST[selectclass]' />
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