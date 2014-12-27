<?php
 print("
 <body onLoad='document.login.username.focus();'>
 ");

 require_once("maketop.php");

 $query = mysql_query("select sitemessage from schoolinfo");

 $message = mysql_result($query,0);

 $query = mysql_query("select sitetext from schoolinfo");
 $text  = mysql_result($query,0);

 print("
  <tr>
  <td class='b' width=130 valign='top'>
   <br>
  </td>
  <td class='bv' width=10 background='./images/left.gif'>&nbsp;</td>
  <td class='w' valign='top'>
   <table border=0 cellspacing=0 cellpadding=25 width='100%' height='100%'>
	<tr>
	 <td valign='top'>
	  <br>
	  <table width='150' border=0 align='center' cellspacing=0 cellpadding=5>
	  <tr>

	   <td width='50%' align='left' valign='top'>
		<div class='messagebox'>
		  $text
		</div>
	   </td>

	 <td width='50%' align='right' valign='top'>
		<div class='messagebox'>
		 <br>
		 <form action='./index.php' method='post' name='login'>");

		 if($loginerror == 1)
		 {
		  print("<font color='red'><center>Invalid username or password!</center></font>");
		 }

print(" <table width='100%' height='85%' border=0 cellspacing=0 cellpadding=0 align='center' class='y'>
		  <tr>
		   <td align='right' height=50 valign='middle'><b>Username:&nbsp;</b></td>
		   <td><input type=text name='username' maxlength=15 width=18></td>
		  </tr>
		  <tr>
		   <td align='right' height=45 valign='middle'><b>Password:&nbsp;</b></td>
		   <td><input type=password name='password' maxlength=15 width=18></td>
		  </tr>
		  <tr>
		   <td>&nbsp;</td>
		   <td align='center' height=45><input type=submit value='Login' onClick='document.login.login.value=1;'></td>

		  </tr>
		 </table>
	   <input type='hidden' name='page' value='$page'>
	   <input type='hidden' name='login'>
	  </form>
	 </div>
	</td>

   </tr>
   <tr>

   <td width='50%' align='right' valign='top'>
	<div class='messageboxcenter'>
	 <table border=0 cellpadding=0 cellspacing=0 class='y'>
	 <tr>
	  <td align='center' valign='middle'>
		<br>
		<img src='./images/school.jpg' style='padding-left: 30px; padding-top: 20px;' width='233' height='111' align='center' valign='middle' />
	  </td>
	 </tr>
	 </table>
	</div>
   </td>

	<td width='50%' align='left' valign='top'>
	 <div class='messageboxcenter'>
	  <h2 class='message'>Today's Message</h2> <br>
	   $message
	 </div>
	</td>

   </tr>
  </table>
 </td>

  </tr>
 </table>
</td>");
?>