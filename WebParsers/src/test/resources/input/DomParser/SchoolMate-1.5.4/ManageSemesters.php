<?php

 // Add the new semester if one is being added //
 if($_POST["addsemester"] == 1 && $_POST["term"] != "" && $_POST["title"] != "" && $_POST["startdate"] != "" && $_POST["middate"] != "" && $_POST["enddate"] != "")
 {
  $_POST["startdate"] = converttodb($_POST["startdate"]);
  $_POST["middate"] = converttodb($_POST["middate"]);
  $_POST["enddate"] = converttodb($_POST["enddate"]);
  $query = mysql_query("INSERT INTO semesters VALUES('', $_POST[term], '$_POST[title]', '$_POST[startdate]', '$_POST[middate]', '$_POST[enddate]', '$_POST[half]')")
   or die("ManageSemesters.php: Unable to insert new semester - " . mysql_error());
 }

 // Edit the semester if one is being edited //
 if($_POST["editsemester"] == 1 && $_POST['term'] != "" && $_POST["title"] != "" && $_POST["startdate"] != "" && $_POST["middate"] != "" && $_POST["enddate"] != "")
 {
  $_POST["startdate"] = converttodb($_POST["startdate"]);
  $_POST["middate"] = converttodb($_POST["middate"]);
  $_POST["enddate"] = converttodb($_POST["enddate"]);
  $query = mysql_query("UPDATE `semesters` SET `title`='$_POST[title]', `startdate`='$_POST[startdate]', `midtermdate`='$_POST[middate]', `enddate`='$_POST[enddate]', `type`='$_POST[half]' WHERE `semesterid`='$_POST[semesterid]' LIMIT 1")
	or die("ManageSemesters.php: Unable to update the semester information - ".mysql_error());
 }

 // Delete the semesters that the user has requested as well as the classes belonging to those semesters //
 if($_POST["deletesemester"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   deleteSemester($delete[$i]);
  }
 }

 print("<script language='JavaScript'>

  // Function to make sure the user wants to delete the semester(s) //
  function validate()
  {
   if( document.semesters.selectsemester.value > 0 )
   {
	var confirmed = confirm(\"Deleteing a semester will also delete the classes, bulletins, tardies, attendance, assignments, and registrations that occured during that semester. \\n \\nAre you sure?\");

	if( confirmed == true )
	{
	 document.semesters.submit();
	}
   }
   else
   {
	alert('You must select a semester to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.semesters.selectsemester.value == 1 )
   {
	document.semesters.submit();
   }
   else
   {
	if( document.semesters.selectsemester.value > 1 )
	{
	 alert('You can only edit one semester at a time.');
	}
	else
	{
	 alert('You must select a semester to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row + 2;
   if(document.semesters.elements[row].checked)
   {
	document.semesters.selectsemester.value = Math.round(document.semesters.selectsemester.value) + 1;
   }
   else
   {
	document.semesters.selectsemester.value = Math.round(document.semesters.selectsemester.value) - 1;
   }
  }
 </script>

 <h1>Manage Semesters</h1>
 <br><br>
 <table align='center' width='600' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='semesters' action='./index.php' method='POST'>
  <input type='button' value='Add' onClick='document.semesters.page2.value=7;document.semesters.submit();'>
  <input type='button' value='Edit' onClick='document.semesters.page2.value=13;checkboxes();'>
  <input type='button' value='Delete' onClick='document.semesters.deletesemester.value=1;validate();'>
  <br><br>
  <table cellspacing='0' width='600' cellpadding='8' class='dynamiclist'>
   <tr class='header'>
	<td>&nbsp;</td>
	<th>Semester Name</th>
	<th>Term</th>
	<th>Start Date</th>
	<th>Midterm Date</th>
	<th>End Date</th>
	<th>Half</th>
   </tr>");

   // Get the total number of semesters to know how many pages to have //
   $query = mysql_query("SELECT COUNT(*) FROM semesters")
	 or die("ManageSemesters.php: Unable to retrieve total number of semesters - ".mysql_error());

   $numrows = mysql_result($query,0);

   $numpages = ceil($numrows / 25);

   if($_POST["onpage"] == "")
   {
	$_POST["onpage"]=1;
   }

   // Get and display the semesters //
   $query = mysql_query("SELECT semesterid,termid,title,startdate,midtermdate,enddate,type FROM semesters ORDER BY enddate DESC");
   $row = 0;
   $actualrow = 0;
   while($smstr = mysql_fetch_row($query))
   {
	$query2 = mysql_query("SELECT title FROM terms WHERE termid='$smstr[1]'");
	$term = mysql_result($query2,0);
	$row++;

	if($row > ($_POST["onpage"]*25)-25 && $row <= ($_POST["onpage"]*25))
	{
	 $actualrow++;
	 print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	  <td><input type='checkbox' name='delete[]' value='$smstr[0]' onClick='updateboxes($actualrow);' /></td>
	  <td>$smstr[2]</td>
	  <td>$term</td>
	  <td>".convertfromdb($smstr[3])."</td>
	  <td>".convertfromdb($smstr[4])."</td>
	  <td>".convertfromdb($smstr[5])."</td>
	  <td>".($smstr[6]==1 ? "First" : "Second")."</td>
	 </tr>");
	}
   }

 print(" </table>
  <br>
  <input type='button' value='Add' onClick='document.semesters.page2.value=7;document.semesters.submit();'>
  <input type='button' value='Edit' onClick='document.semesters.page2.value=13;checkboxes();'>
  <input type='button' value='Delete' onClick='document.semesters.deletesemester.value=1;validate();'>
  <br><br>
  <font color='red'>* Deleting a semester will also delete the classes in that semester and the information for those classes</font>
	<br><br>

  <center>Page: ");

  for($i=1; $i<=$numpages; $i++)
  {
   if($i == $_POST["onpage"])
   {
	print("<a href='JavaScript: document.semesters.deletesemester.value=0;document.semesters.page2.value=5;document.semesters.onpage.value=$i;document.semesters.submit();' class='selectedpagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
   else
   {
	print("<a href='JavaScript: document.semesters.deletesemester.value=0;document.semesters.page2.value=5;document.semesters.onpage.value=$i;document.semesters.submit();' class='pagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
  }


print("\n</center>
  <input type='hidden' name='deletesemester'>
  <input type='hidden' name='selectsemester'>
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