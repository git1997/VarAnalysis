<?php

 // Queries to get the information from the database for the user to change
 $query = mysql_query("SELECT address FROM schoolinfo")
		  or die("ManageSchoolInfo.php: Unable to retrieve School Address " . mysql_error());

 $address = mysql_result($query,0);

 $query = mysql_query("SELECT phonenumber FROM schoolinfo")
		  or die("ManageSchoolInfo.php: Unable to retrieve PhoneNumber " . mysql_error());

 $phone = mysql_result($query,0);

 $query = mysql_query("SELECT numsemesters FROM schoolinfo")
		  or die("ManageSchoolInfo.php: Unable to retrieve NumSemesters " . mysql_error());

 $numsemesters = mysql_result($query,0);

 $query = mysql_query("SELECT numperiods FROM schoolinfo")
		  or die("ManageSchoolInfo.php: Unable to retrieve NumPeriods " . mysql_error());

 $numperiods = mysql_result($query,0);

 $query = mysql_query("SELECT apoint, bpoint, cpoint, dpoint, fpoint FROM schoolinfo")
		  or die("ManageSchoolInfo.php: Unable to retrieve Point System ". mysql_error());

 $points = mysql_fetch_array($query);

 $query = mysql_query("SELECT sitetext, sitemessage FROM schoolinfo")
		  or die("ManageSchoolInfo.php: Unable to retrieve sitetext ". mysql_error());

 $temp        = mysql_fetch_row($query);
 $sitetext    = $temp[0];
 $sitemessage = $temp[1];
 // End GET queries

 print("<table width='100%' border=0 cellpadding=10 cellspacing=0>
<tr>
<td>

 <h1>Manage School Information</h1>
 <br>

 <form name='info' method='POST' action='./index.php'>

 <table border=0 width=500 cellspacing=0 cellpadding='3' align='center' cellpadding=0 class='dynamiclist'>
 <tr class='even'>
  <td align='right'>
   School Name:
  </td>
  <td align='left'>
   <input type='text' value='$schoolname' maxlength='50' name='schoolname' size=40>
  </td>
 </tr>
 <tr class='even'>
  <td align='right'>
   Address:
  </td>
  <td align='left'>
   <input type='text' value='$address' name='schooladdress' maxlength='50' size=40>
  </td>
 </tr>
 <tr class='even'>
  <td align='right'>
   Phone #:
  </td>
  <td align='left'>
   <input type='text' value='$phone' name='schoolphone' maxlength='14'>
  </td>
 </tr>
 <tr class='even'>
  <td align='right'>
   Semesters Per Year:
  </td>
  <td align='left'>
   <input type='text' value='$numsemesters' name='numsemesters' maxlength='3' size=3>
  </td>
 </tr>
 <tr class='even'>
  <td align='right'>
   Periods Per Day:
  </td>
  <td align='left'>
   <input type='text' value='$numperiods' name='numperiods' maxlength='3' size=3>
  </td>
 </tr>
 <tr class='even'>
  <td align='right'>
   Points for A:
  </td>
  <td align='left'>
   <input type='text' value='".number_format($points[0],1)."' name='apoint' maxlength='3' size=3>
  </td>
 </tr>
 <tr class='even'>
  <td align='right'>
   Points for B:
  </td>
  <td align='left'>
   <input type='text' value='".number_format($points[1],1)."' name='bpoint' maxlength='3' size=3>
  </td>
 </tr>
 <tr class='even'>
  <td align='right'>
   Points for C:
  </td>
  <td align='left'>
   <input type='text' value='".number_format($points[2],1)."' name='cpoint' maxlength='3' size=3>
  </td>
 </tr>
 <tr class='even'>
  <td align='right'>
   Points for D:
  </td>
  <td align='left'>
   <input type='text' value='".number_format($points[3],1)."' name='dpoint' maxlength='3' size=3>
  </td>
 </tr>
 <tr class='even'>
  <td align='right'>
   Points for F:
  </td>
  <td align='left'>
   <input type='text' value='".number_format($points[4],1)."' name='fpoint' maxlength='3' size=3>
  </td>
 </tr>
 <tr class='even'>
  <td align='right' valign='top'>
   Text For Login Page:
  </td>
  <td align='left'>
   <textarea name='sitetext' cols=40 rows=10>$sitetext</textarea>
  </td>
 </tr>
 <tr class='even'>
  <td align='right' valign='top'>Today's Message:</td>
  <td align='left'>
   <textarea name='sitemessage' cols=40 rows=10>$sitemessage</textarea>
  </td>
  </tr>
 </table>
<br>
 <table width='500' align='center' cellpadding='0' cellspacing='0' border='0'>
  <tr>
   <td align='center'><input type='button' value=' Update ' onClick='document.info.infoupdate.value=1;document.info.submit();'></td>
  </tr>
 </table>

 <input type='hidden' name='infoupdate' value=''>
 <input type='hidden' name='page2' value='$page2'>
 <input type='hidden' name='logout'>
 <input type='hidden' name='page' value='$page'>
 </form>

 <table width='520' border=0 cellspacing=0 cellpadding=0 height=1>
  <tr>
   <td valign='top'>
   <empty>
   </td>
  </tr>
 </table>

</td>
</tr>
 </table>
 ");
?>