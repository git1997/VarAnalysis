<?php
print("
 <h1>Students</h1>
 <br>
 <table align='center' width='600' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='classes' action='./index.php' method='POST'>");

 // Get a list of all the students in this class //
 $query = mysql_query("SELECT studentid FROM registrations WHERE courseid = $_POST[selectclass]")
   or die("ViewStudents.php: Unable to get a list of students - ".mysql_error());

 while($info = mysql_fetch_row($query))
 {
  $ids[] = $info[0];

  $q = mysql_query("SELECT fname,lname FROM students WHERE studentid = $info[0]");
  $temp = mysql_fetch_row($q);
  $fnames[] = $temp[0];
  $lnames[] = $temp[1];
 }

print("
  <br><br>
  <table cellspacing='0' width='600' class='dynamiclist'>
  <tr class='header'>
   <th>Student</th>
   <th>Current Points</th>
   <th>Possible Points</th>
   <th>Percent</th>
   <th>Grade</th>
  </tr>");

  // Print out the grades for each class //
  $query =    mysql_query("SELECT totalpoints, aperc, bperc, cperc, dperc, fperc FROM courses WHERE courseid = $_POST[selectclass]")
	or die("ViewStudents.php: Unable to get the list of classes this student is registered for - ".mysql_error());

  $classes = mysql_fetch_row($query);

  $row = 0;
  for($i=0; $i<count($ids); $i++)   //while($classes = mysql_fetch_row($query))
  {
   $row++;
   $q = mysql_query("SELECT currentpoints FROM registrations WHERE studentid = '$ids[$i]' AND courseid = $_POST[selectclass]");
   $cinfo = mysql_fetch_row($q);

   // Calculate the Current Grade //
   if($classes[0] != 0)
	$currperc = $cinfo[0] / $classes[0];
   else
	$currperc = 0;

   if($currperc >= ($classes[1]/100))
   {
	$currgrade = "A";
   }
   elseif($currperc >= $classes[2]/100)
   {
	$currgrade = "B";
   }
   elseif($currperc >= $classes[3]/100)
   {
	$currgrade = "C";
   }
   elseif($currperc >= $classes[4]/100)
   {
	$currgrade = "D";
   }
   else
   {
	$currgrade = "F";
   }
   print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
   <td align='left'>$fnames[$i] $lnames[$i]</td>
   <td>$cinfo[0]</td>
   <td>$classes[0]</td>
   <td>".(number_format($currperc*100,2))."&#37;</td>
   <td>$currgrade</td>
   ");
  }

print("  </table>
  <br />
  <input type='button' value=' Back ' onClick='document.classes.page2.value=2;document.classes.submit();'>
  <input type='hidden' name='selectclass' value='$_POST[selectclass]' />
  <input type='hidden' name='page2' value='$page2' />
  <input type='hidden' name='logout' />
  <input type='hidden' name='page' value='$page' />
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