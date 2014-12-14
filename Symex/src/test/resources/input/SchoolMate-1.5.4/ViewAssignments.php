<?php

 require_once("DBFunctions.php");

 // Get the coursename //
 $query = mysql_query("SELECT coursename FROM courses WHERE courseid = '$_POST[selectclass]'") or die("ManageAssignments.php: Unable to get the course name - ".mysql_error());
 $coursename = mysql_result($query,0);

 print("
 <h1>View Assignments</h1>
 <br><br>
 <table align='center' width='600' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='assignments' action='./index.php' method='POST'>
  <br><br>
  <table cellspacing='0' width='600' cellpadding='8' class='dynamiclist'>
   <tr class='header'>
	<th colspan='6'><h2>$coursename</th>
   </tr>
   <tr class='header'>
	<th width='120' align='left' style='padding-left: 20px;'>Title</th>
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
	  <td align='left' style='padding-left: 20px;'>$assignment[1]</td>
	  <td style='text-align: left;'>$assignment[5]</td>
	  <td>$assignment[2]</td>
	  <td>$assignment[3]</td>
	  <td>$assignment[4]</td>
	 </tr>");
	}
   }

 print(" </table>
  <br>
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