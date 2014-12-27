<?php

 $query = mysql_query("select schoolname from schoolinfo")
        or die("Unable to retrieve school name: " . mysql_error());

 $schoolname = mysql_result($query,0);

 // Query to change the information if the user changes the info
 if($_POST["infoupdate"] == 1)
 {
  $query = mysql_query("UPDATE schoolinfo SET schoolname = \"".htmlspecialchars($_POST["schoolname"])."\", address = '$_POST[schooladdress]', phonenumber = '$_POST[schoolphone]', sitetext = '$_POST[sitetext]', sitemessage = '$_POST[sitemessage]', numsemesters = '$_POST[numsemesters]', numperiods = '$_POST[numperiods]', apoint = '$_POST[apoint]', bpoint = '$_POST[bpoint]', cpoint = '$_POST[cpoint]', dpoint = '$_POST[dpoint]', fpoint = '$_POST[fpoint]' where schoolname = '$schoolname' LIMIT 1 ");
  $schoolname = htmlspecialchars($_POST["schoolname"]);
 }

 print("<html>
 <head>
 <title>SchoolMate - $schoolname</title>
 <style type=\"text/css\">

 /* BODY */
 body
 {
  background-color: #336699;
 }

 /* LINKS */
  A.footer
  {
    font-family: arial;
    font-size: 10pt;
    font-weight: normal;
    color: silver;
    text-decoration: underline;
  }

  A.footer:hover
  {
    font-family: arial;
    font-size: 10pt;
    font-weight: normal;
    color: black;
	text-decoration: underline;
  }

  A.menu
  {
	font-family: arial;
	font-size: 12pt;
	font-weight: bold;
	color: #e6ca3d;
	text-decoration: none;
  }

  A.menu:hover
  {
	font-family: arial;
    font-size: 12pt;
    font-wight: bold;
	color: #FFFFBB;
    text-decoration: none;
  }

  A.pagenum
  {
   font-family: arial;
   font-size: 10pt;
   font-weight: normal;
   color: #808080;
   text-decoration: none;
  }

  A.pagenum:hover
  {
   font-family: arial;
   font-size: 10pt;
   font-weight: normal;
   color: #ACACAC;
   text-decoration: none;
  }

  A.selectedpagenum
  {
   font-family: arial;
   font-size: 10pt;
   font-weight: normal;
   color: #000000;
   text-decoration: underline;
  }

  A.selectedpagenum:hover
  {
   font-family: arial;
   font-size: 10pt;
   font-weight: normal;
   color: #ACACAC;
   text-decoration: underline;
  }

  A.items
  {
   font-family: arial;
   font-size: 10pt;
   font-weight: normal;
   color: #000000;
   text-decoration: underline;
  }

  A.items:hover
  {
   font-family: arial;
   font-size: 10pt;
   font-weight: normal;
   color: #5F5F5F;
   text-decoration: underline;
  }

  /* TABLES */
  table
  {
	background-color: #FFFFFF;
  }

  table.y
  {
	background-color: #FFFFBB;
  }

  table.dynamiclist
  {
   border-color: #585858;
   border-width: .075EM;
   border-style: solid;
   padding-right: 1px;
   padding-bottom: 1px;
   padding-left: 0px;
   padding-top: 0px;
  }

  td.b
  {
   background-color: #336699;
  }

  td.bv
  {
   background-color: #336699;
   background-repeat: repeat-y;
  }

  td.w
  {
   background-color: #FFFFFF;
  }

  td.announcement
  {
   text-align: justify;
   padding-left: 20px;
   padding-right: 20px;
  }

  .odd
  {
   background-color: #ACACAC;
   text-align: center;
  }

  .even
  {
   background-color: #E0E0E0;
   text-align: center;
  }

  .header
  {
   background-color: #FFFFBB;
  }

 /* HEADERS */
  h1
  {
   	font-family: times;
    font-size: 22pt;
    font-weight: bold;
    color: #000000;
    text-decoration: none;
    text-align: center;
  }

  h2.message
  {
	font-family: times;
    font-size: 16pt;
    font-weight: bold;
    text-decoration: none;
    text-align: center;
  }

 /* CUSTOM CLASSES */
  .messagebox
  {
	background-color: #FFFFBB;
	border-style: solid;
	border-width: .075EM;
	border-color: #e6ca3d;
	padding: 10px 10px 10px 10px;
	min-width: 150px;
	min-height: 150px;
	width: 300;
	height: 200;
	font-family: arial;
	font-size: 10pt;
	text-align: left;
	overflow: none;
  }

  .messageboxcenter
  {
	background-color: #FFFFBB;
	border-style: solid;
	border-width: .075EM;
	border-color: #e6ca3d;
	padding: 10px 10px 10px 10px;
	min-width: 150px;
	min-height: 50px;
	width: 300;
	height: 200;
	font-family: arial;
	text-align: center;
	vertical-align: middle;
	overflow: auto;
  }

  .footer
  {
    font-family: arial;
    font-size: 10pt;
    font-weight: normal;
    color: silver;
  }

  .yellowtext
  {
	font-family: times;
	font-size: 25pt;
	font-weight: normal;
	color: #e6ca3d;
	text-decoration: none;
  }


</style>
</head>
 ");
?>