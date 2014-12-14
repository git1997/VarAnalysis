<?php
 // Function to convert the dates into database format //
 function converttodb($indate)
 {
	if($indate == "")
	{
	 $ret_date = "0000-00-00";
	}
	else
	{
	 $date = $indate;
	 list($month, $day, $year) = split('[/.-]', $date);

	 if(strlen($year) == 2 || $year == NULL)
	 {
	  $cur_year = date("Y");
	  $year = $cur_year[0] . $cur_year[1] . $year;
	 }

	 if(strlen($month) < 2 && $month < 10)
	 {
	  $month = "0".$month;
	 }

	 if(strlen($day) < 2 && $day < 10)
	 {
	  $day = "0".$day;
	 }

	 $ret_date = $year."-".$month."-".$day;
	}

	return $ret_date;
 }

 // Convert the database date format into something a bit more readable //
 function convertfromdb($indate)
 {
  list($year, $month, $day) = split('[/.-]', $indate);
  $ret_date = $month."/".$day."/".$year;

  if($ret_date == "00/00/0000")
  {
   $ret_date = "";
  }
  return $ret_date;
 }
?>