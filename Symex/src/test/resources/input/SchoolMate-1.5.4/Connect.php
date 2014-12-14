<?php
///////////////////////////////
//   CONNECT TO DATABASE     //
// allows system to connect  //
// to the database it uses   //
///////////////////////////////



 $dbcnx = mysql_connect($dbaddress,$dbuser,$dbpass)
                                         or die("Could not connect: " . mysql_error());


 mysql_select_db($dbname, $dbcnx) or die ('Unable to select the database: ' . mysql_error());

 require_once("DBFunctions.php");
 
?>