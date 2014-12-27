<?php


 print("<h1>View Announcements</h1>
 <br><br>
 <table align='center' width='600' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='announcements' action='./index.php' method='POST'>
  <table cellspacing='0' width='600' cellpadding='8' class='dynamiclist'>
   <tr class='header'>
	<th>Title</th>
	<th>Message</th>
	<th>Date</th>
   </tr>");

   // Get the total number of announcements to know how many pages to have //
   $query = mysql_query("SELECT COUNT(*) FROM schoolbulletins")
	 or die("ViewAnnouncements.php: Unable to retrieve total number of announcements - ".mysql_error());

   $numrows = mysql_result($query,0);

   $numpages = ceil($numrows / 25);

   if($_POST["onpage"] == "")
   {
	$_POST["onpage"]=1;
   }

   // Get and display the announcements //
   $query = mysql_query("SELECT * FROM schoolbulletins ORDER BY bulletindate DESC")
	 or die("ViewAnnouncements.php: Unable to retrieve the announcements - ".mysql_error());
   $row = 0;
   $actualrow = 0;
   while($announcement = mysql_fetch_row($query))
   {
	$row++;

	if($row > ($_POST["onpage"]*25)-25 && $row <= ($_POST["onpage"]*25))
	{
	 $actualrow++;
	 print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
	  <td><b>$announcement[1]</b></td>
	  <td class='announcement'>$announcement[2]</td>
	  <td>".convertfromdb($announcement[3])."</td>
	 </tr>");
	}
   }

 print(" </table>
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