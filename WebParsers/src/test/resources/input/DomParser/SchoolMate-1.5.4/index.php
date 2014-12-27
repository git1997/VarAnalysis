<?php
 session_start();
 /*
  **********************************
  *       School Mate              *
  * written by: Ray Hauge          *
  * Start Date: 04/29/2004         *
  * End Date: 08/03/04             *
  * Directs system to the          *
  * correct section for display    *
  **********************************
  */

  ////////////////////////////////////////
  //  Database Connection Information   //
  ////////////////////////////////////////
	$dbaddress  = 'localhost';            // location of the database
  $dbuser     = 'SchoolMaster';         // databse username
	$dbpass     = 'schoolmaster';         // databse password
	$dbname     = 'schoolmate';           // name of the database you are using
  ////////////////////////////////////////

  include("Connect.php");

  // Bring up the report cards and stop processing //
  if($_POST['page2']==1337)
  {
   require_once('ReportCards.php');
   die();
  }

  // Start the page //
  require_once("header.php");

  // Make the $page varialbe easy to use //
  $page = $_POST["page"];

  if($page == null)
	$page = 0;

  // Validate and log the user into the system //
  if($_POST["login"] == 1)
  {
   require_once("ValidateLogin.php");
  }

  // Log the use out if they click on Log Out //
  if($_POST["logout"] == 1)
  {
   session_destroy();
   $page=0;
  }

  switch ($page)
  {

   case 0:
		require_once("Login.php");
		break;

   case 1:
		require_once("AdminMain.php");
		break;

   case 2:
		require_once("TeacherMain.php");
		break;

   case 3:
		require_once("SubstituteMain.php");
		break;

   case 4:
		require_once("StudentMain.php");
		break;

   case 5:
		require_once("ParentMain.php");
		break;
  }


  require_once("footer.php");

  mysql_close($dbcnx);
?>
