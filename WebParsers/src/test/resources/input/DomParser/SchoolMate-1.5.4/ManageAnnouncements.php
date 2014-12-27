<?php

 // Add the new announcement if one is being added //
 if($_POST["addannouncement"] == 1 && $_POST["title"] != "" && $_POST["message"] != "")
 {
   $_POST["date"] = date("Y-m-d");

   $query = mysql_query("INSERT INTO schoolbulletins VALUES('', '$_POST[title]', '$_POST[message]', '$_POST[date]')")
	 or die("ManageAnnouncements.php: Unable to insert new announcement - " . mysql_error());
 }

 // Edit the announcement if one is being edited //
 if($_POST["editannouncement"] == 1 && $_POST["title"] != "" && $_POST["message"] != "" && $_POST["date"] != "")
 {
  $query = mysql_query("UPDATE `schoolbulletins` SET `title`='$_POST[title]', `message`='$_POST[message]', `bulletindate`='".converttodb($_POST["date"])."' WHERE `sbulletinid`='$_POST[announcementid]' LIMIT 1")
	or die("ManageAnnouncements.php: Unable to update the announcement information - ".mysql_error());
 }

 // Delete the announcement(s) that the announcement has requested as well as the classes belonging to those announcements //
 if($_POST["deleteannouncement"] == 1)
 {
  require_once("DeleteFunctions.php");

  $delete = $_POST["delete"];
  for($i=0; $i<sizeof($delete); $i++)
  {
   deleteAnnouncement($delete[$i]);
  }
 }

 print("<script language='JavaScript'>

  // Function to make sure the announcement wants to delete the announcement(s) //
  function validate()
  {
   if( document.announcements.selectannouncement.value > 0 )
   {
	var confirmed = confirm(\"Are you sure you want to delete this announcement?\");

	if( confirmed == true )
	{
	 document.announcements.submit();
	}
   }
   else
   {
	alert('You must select a announcement to delete.');
   }
  }


  // Function to make sure only one checkbox has been selected //
  function checkboxes()
  {
   if( document.announcements.selectannouncement.value == 1 )
   {
	document.announcements.submit();
   }
   else
   {
	if( document.announcements.selectannouncement.value > 1 )
	{
	 alert('You can only edit one announcement at a time.');
	}
	else
	{
	 alert('You must select a announcement to edit.');
	}
   }
  }


  // Function to keep track of how many checkboxes are checked //
  function updateboxes(row)
  {
   row = row + 2;
   if(document.announcements.elements[row].checked)
   {
	document.announcements.selectannouncement.value = Math.round(document.announcements.selectannouncement.value) + 1;
   }
   else
   {
	document.announcements.selectannouncement.value = Math.round(document.announcements.selectannouncement.value) - 1;
   }
  }
 </script>

 <h1>Manage Announcements</h1>
 <br><br>
 <table align='center' width='600' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='announcements' action='./index.php' method='POST'>
  <input type='button' value='Add' onClick='document.announcements.page2.value=18;document.announcements.submit();'>
  <input type='button' value='Edit' onClick='document.announcements.page2.value=19;checkboxes();'>
  <input type='button' value='Delete' onClick='document.announcements.deleteannouncement.value=1;validate();'>
  <br><br>
  <table cellspacing='0' width='600' cellpadding='8' class='dynamiclist'>
   <tr class='header'>
	<td>&nbsp;</td>
	<th>Title</th>
	<th>Message</th>
	<th>Date</th>
   </tr>");

   // Get the total number of announcements to know how many pages to have //
   $query = mysql_query("SELECT COUNT(*) FROM schoolbulletins")
	 or die("ManageAnnouncements.php: Unable to retrieve total number of announcements - ".mysql_error());

   $numrows = mysql_result($query,0);

   $numpages = ceil($numrows / 25);

   if($_POST["onpage"] == "")
   {
	$_POST["onpage"]=1;
   }

   // Get and display the announcements //
   $query = mysql_query("SELECT * FROM schoolbulletins ORDER BY bulletindate DESC")
	 or die("ManageAnnouncements.php: Unable to retrieve the announcements - ".mysql_error());
   $row = 0;
   $actualrow = 0;
   while($announcement = mysql_fetch_row($query))
   {
	$row++;

	if($row > ($_POST["onpage"]*25)-25 && $row <= ($_POST["onpage"]*25))
	{
	 $actualrow++;
	 print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	  <td><input type='checkbox' name='delete[]' value='$announcement[0]' onClick='updateboxes($actualrow);' /></td>
	  <td>$announcement[1]</td>
	  <td class='announcement'>$announcement[2]</td>
	  <td>".convertfromdb($announcement[3])."</td>
	 </tr>");
	}
   }

 print(" </table>
  <br>
  <input type='button' value='Add' onClick='document.announcements.page2.value=18;document.announcements.submit();'>
  <input type='button' value='Edit' onClick='document.announcements.page2.value=19;checkboxes();'>
  <input type='button' value='Delete' onClick='document.announcements.deleteannouncement.value=1;validate();'>
  <br><br>

  <center>Page: ");

  for($i=1; $i<=$numpages; $i++)
  {
   if($i == $_POST["onpage"])
   {
	print("<a href='JavaScript: document.announcements.deleteannouncement.value=0;document.announcements.page2.value=4;document.announcements.onpage.value=$i;document.announcements.submit();' class='selectedpagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
   else
   {
	print("<a href='JavaScript: document.announcements.deleteannouncement.value=0;document.announcements.page2.value=4;document.announcements.onpage.value=$i;document.announcements.submit();' class='pagenum' onMouseover=\"window.status='Go to page $i';return true;\" onMouseout=\"window.status='';return true;\">$i</a>&nbsp;\n");
   }
  }

print("\n</center>
  <input type='hidden' name='deleteannouncement'>
  <input type='hidden' name='selectannouncement'>
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