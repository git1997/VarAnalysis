<?php

 // Add the new term if one is being added //
 if($_POST["addterm"] == 1 && $_POST["startdate"] != "" && $_POST["title"] != "" && $_POST["enddate"] != "")
 {
  $_POST["startdate"] = converttodb($_POST["startdate"]);
  $_POST["enddate"] = converttodb($_POST["enddate"]);
  $query = mysql_query("INSERT INTO terms VALUES('', '$_POST[title]', '$_POST[startdate]', '$_POST[enddate]')")
   or die("ManageTerms.php: Unable to insert new term - " . mysql_error());
 }

 // Edit the term if one is being edited //
 if($_POST["editterm"] == 1 && $_POST["startdate"] != "" && $_POST["title"] != "" && $_POST["enddate"] != "")
 {
  $_POST["startdate"] = converttodb($_POST["startdate"]);
  $_POST["enddate"] = converttodb($_POST["enddate"]);
  $query = mysql_query("UPDATE `terms` SET `title`='$_POST[title]', `startdate`='$_POST[startdate]', `enddate`='$_POST[enddate]' WHERE `termid`='$_POST[termid]' LIMIT 1")
	or die("ManageTerms.php: Unable to update the term information - ".mysql_error());
 }

 // Delete the terms that the user has requested as well as the classes belonging to those terms //
 if($_POST["deleteterm"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   deleteTerm($delete[$i]);
  }
 }

 print("<script language='JavaScript'>
  // Function to make sure the user wants to delete the term(s) //
  function validate()
  {
   if( document.terms.selectterm.value > 0 )
   {
	var confirmed = confirm(\"Deleteing a term will also delete the semesters, classes, bulletins, tardies, attendance, assignments, and registrations that occured during that term. \\n \\nAre you sure?\");

	if( confirmed == true )
	{
	 document.terms.submit();
	}
   }
   else
   {
	alert('You must select a term to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.terms.selectterm.value == 1 )
   {
	document.terms.submit();
   }
   else
   {
	if( document.terms.selectterm.value > 1 )
	{
	 alert('You can only edit one term at a time.');
	}
	else
	{
	 alert('You must select a term to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row + 2;
   if(document.terms.elements[row].checked)
   {
	document.terms.selectterm.value = Math.round(document.terms.selectterm.value) + 1;
   }
   else
   {
	document.terms.selectterm.value = Math.round(document.terms.selectterm.value) - 1;
   }
  }
 </script>

 <h1>Manage Terms</h1>
 <br><br>
 <table align='center' width='450' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='terms' action='./index.php' method='POST'>
  <input type='button' value='Add' onClick='document.terms.page2.value=8;document.terms.submit();'>
  <input type='button' value='Edit' onClick='document.terms.page2.value=12;checkboxes();'>
  <input type='button' value='Delete' onClick='document.terms.deleteterm.value=1;validate();'>
  <br><br>
  <table cellspacing='0' width='450' cellpadding='8' class='dynamiclist'>
   <tr class='header'>
   <td>&nbsp;</td>
   <th>Term Name</th>
   <th>Start Date</th>
   <th>End Date</th>
   </tr>");

   // Get the total number of teachers to know how many pages to have //
   $query = mysql_query("SELECT COUNT(*) FROM terms")
	 or die("ManageTerms.php: Unable to retrieve total number of terms - ".mysql_error());

   $numrows = mysql_result($query,0);

   $numpages = ceil($numrows / 25);

   if($_POST["onpage"] == "")
   {
	$_POST["onpage"]=1;
   }

   // Get and display the terms //
   $query = mysql_query("SELECT termid,title,startdate,enddate FROM terms ORDER BY enddate DESC");
   $row = 0;
   $actualrow = 0;
   while($term = mysql_fetch_row($query))
   {
	$row++;
	if($row > ($_POST["onpage"]*25)-25 && $row <= ($_POST["onpage"]*25))
	{
	 $actualrow++;
	 print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	  <td><input type='checkbox' name='delete[]' value='$term[0]' onClick='updateboxes($actualrow);' /></td>
	  <td>$term[1]</td>
	  <td>".convertfromdb($term[2])."</td>
	  <td>".convertfromdb($term[3])."</td>
	 <tr>");
	}
   }

 print(" </table>
  <br>
  <input type='button' value='Add' onClick='document.terms.page2.value=8;document.terms.submit();'>
  <input type='button' value='Edit' onClick='document.terms.page2.value=12;checkboxes();'>
  <input type='button' value='Delete' onClick='document.terms.deleteterm.value=1;validate();'>

  <br><br>
  <font color='red'>* Deleting a term will also delete the semesters, classes in each semester, and the information for those classes</font>
	<br><br>

  <center>Page: ");

  for($i=1; $i<=$numpages; $i++)
  {
   if($i == $_POST["onpage"])
   {
	print("<a href='JavaScript: document.terms.deleteterm.value=0;document.terms.page2.value=6;document.terms.onpage.value=$i;document.terms.submit();' class='selectedpagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
   else
   {
	print("<a href='JavaScript: document.terms.deleteterm.value=0;document.terms.page2.value=6;document.terms.onpage.value=$i;document.terms.submit();' class='pagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
  }


print("\n</center>
  <input type='hidden' name='deleteterm'>
  <input type='hidden' name='selectterm'>
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