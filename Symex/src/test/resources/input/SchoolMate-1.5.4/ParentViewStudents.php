<?php
$query = mysql_query("SELECT parentid, fname, lname FROM parents WHERE userid = $_SESSION[userid]");
$parent = mysql_fetch_row($query);
$parentid = $parent[0];

print("
 <h1>Students of $parent[1] $parent[2]</h1>
 <br>
 <table align='center' width='300' cellspacing='0' cellpadding='0' border='0'>
 <tr>
 <td>
 <form name='classes' action='./index.php' method='POST'>");

print("
  <br>
  <table cellspacing='0' width='300' align='center' class='dynamiclist'>
  <tr class='header'>
   <th>Student Name</th>
  </tr>");
 // Get a list of all the students in this class //
 $query = mysql_query("SELECT studentid FROM parent_student_match WHERE parentid = $parentid")
   or die("ViewStudents.php: Unable to get a list of students - ".mysql_error());

 $row = 0;

 while($info = mysql_fetch_row($query))
 {
  $row++;
  $ids[] = $info[0];

  $q = mysql_query("SELECT fname,lname, studentid FROM students WHERE studentid = $info[0]");
  $temp = mysql_fetch_row($q);
  print("<tr class='".( $row%2==0 ? "even" : "odd" )."'>
  <td><a class='items' href=\"javascript:document.classes.student.value=$temp[2];document.classes.page2.value=5;document.classes.submit();\">$temp[0] $temp[1]</a></td>
  </tr>");
 }
print("  </table>
  <br />
  <input type='hidden' name='student' value='' />
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