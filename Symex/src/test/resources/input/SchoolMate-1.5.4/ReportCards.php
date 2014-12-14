<?php
// Make sure only admins who are logged in can veiw this page //
if($_SESSION['userid'] == "" || $_SESSION['usertype']!="Admin")
{
 die("Invalid User!");
}
else
{
 $q = mysql_query("SELECT type FROM users WHERE userid = $_SESSION[userid]");
 $type = mysql_fetch_row($q);

 // If the user is not an admin... exit //
 if($type[0]!="Admin")
 {
  mysql_close($dbcnx);
  die();
 }
}

// Get the name of the term we're displaying info for //
$query = mysql_query("SELECT title FROM terms WHERE termid = $_POST[term]");
$term  = mysql_fetch_row($query);

// Get a list of semesterids for this term //

// Get a list of the semesters we'll be using //
$query = mysql_query("SELECT semesterid FROM semesters WHERE termid = $_POST[term] ORDER BY startdate ASC");
while($id = mysql_fetch_row($query))
  $semester[] = $id[0];


###############
#  START PDF  #
###############

//Create & Open PDF-Object
$pdf = pdf_new();
pdf_open_file($pdf);
pdf_set_info($pdf, "Author","Bob Nijman");
pdf_set_info($pdf, "Title","www.nijman.de");
pdf_set_info($pdf, "Creator", "bob@nijman.de");
pdf_set_info($pdf, "Subject", "pdf-stuff");

###########################################
# CREATE A SEPARATE PAGE FOR EACH STUDENT #
###########################################
$query = mysql_query("SELECT studentid, fname, mi, lname FROM students ORDER BY UPPER(lname)");
while($student = mysql_fetch_row($query))
{
pdf_begin_page($pdf, 595, 842);

$TimesHeader 	= pdf_findfont($pdf, "Times-Bold", "host");
$body           = pdf_findfont($pdf, "Helvetica", "host");
$bodybold       = pdf_findfont($pdf, "Helvetica-Bold", "host");

// Outline rectangle
pdf_setlinewidth($pdf, 0.5); //make the border of the rectangle a bit wider
pdf_rect($pdf, 50, 200, 495, 500); //draw the rectangle
pdf_stroke($pdf); //stroke the path with the current color(not yet :-)) and line width

##############
#  TOP TEXT  #
##############

// Title //
pdf_setfont($pdf, $TimesHeader, 30);
pdf_show_xy($pdf, "Ray High School", 50,760);

// Student's Name //
pdf_setfont($pdf, $body, 12);
pdf_show_xy($pdf, "Student: $student[1] $student[2]. $student[3]", 50,725);

// Line Under Student's Name //
pdf_moveto($pdf, 95, 722);
pdf_lineto($pdf, 250, 722);
pdf_stroke($pdf);

// Show the term reports are for //
pdf_setfont($pdf, $body, 12);
pdf_show_xy($pdf, "Term: $term[0]", 300,725);

// Line Under Term Name //
pdf_moveto($pdf, 335, 722);
pdf_lineto($pdf, 400, 722);
pdf_stroke($pdf);


###############
#  DRAW GRID  #
###############

// Top Title line of box
pdf_moveto($pdf, 255, 685);
pdf_lineto($pdf, 490, 685);
pdf_stroke($pdf);

// Class Name Title
pdf_setfont($pdf, $bodybold, 12);
pdf_show_xy($pdf, "Class Name", 115, 680);

// Line seperator
pdf_moveto($pdf, 255, 700);
pdf_lineto($pdf, 255, 200);
pdf_stroke($pdf);

// Semester1 Title
pdf_setfont($pdf, $bodybold, 12);
pdf_show_xy($pdf, "Semester 1", 285, 688);

// Line seperator
pdf_moveto($pdf, 380, 700);
pdf_lineto($pdf, 380, 200);
pdf_stroke($pdf);

// Line seperator
pdf_moveto($pdf, 295, 685);
pdf_lineto($pdf, 295, 200);
pdf_stroke($pdf);

// Line seperator
pdf_moveto($pdf, 338, 685);
pdf_lineto($pdf, 338, 200);
pdf_stroke($pdf);

// Semester1 Subtext
pdf_setfont($pdf, $bodybold, 10);
pdf_show_xy($pdf, "Q 1", 267, 675);

pdf_setfont($pdf, $bodybold, 10);
pdf_show_xy($pdf, "Q 2", 307, 675);

pdf_setfont($pdf, $bodybold, 10);
pdf_show_xy($pdf, "Total", 347, 675);

// Semester2 Title
pdf_setfont($pdf, $bodybold, 12);
pdf_show_xy($pdf, "Semester 2", 401, 688);

// Line seperator
pdf_moveto($pdf, 490, 700);
pdf_lineto($pdf, 490, 200);
pdf_stroke($pdf);

// Total Title
pdf_setfont($pdf, $bodybold, 12);
pdf_show_xy($pdf, "Overall", 497, 680);

// Line Seperator
pdf_moveto($pdf, 420, 685);
pdf_lineto($pdf, 420, 200);
pdf_stroke($pdf);

// Line Seperator
pdf_moveto($pdf, 455, 685);
pdf_lineto($pdf, 455, 200);
pdf_stroke($pdf);

// Semester2 Subtext
pdf_setfont($pdf, $bodybold, 10);
pdf_show_xy($pdf, "Q 3", 393, 675);

pdf_setfont($pdf, $bodybold, 10);
pdf_show_xy($pdf, "Q 4", 430, 675);

pdf_setfont($pdf, $bodybold, 10);
pdf_show_xy($pdf, "Total", 462, 675);

// Bottom Title Line
pdf_moveto($pdf, 50, 670);
pdf_lineto($pdf, 545, 670);
pdf_stroke($pdf);


###################################################################
#                           FILL GRID                             #
# Start at 655px                                                  #
# A seperator line is drawn 5px below the text, skipping the last #
# Each new line of text goes 20px down from the previous text     #
###################################################################
$start = 655;
$line  = 5;
$next  = 20;

// Get the list of classes the student is registered for //
$q = mysql_query("SELECT courseid FROM registrations WHERE studentid = $student[0] ORDER BY courseid ASC");
while($cid = mysql_fetch_row($q))
{
 // Clear some variables for the next loop //
 $clause = "";
 $s1 = NULL;
 $s2 = NULL;

 // Build the AND clause to make sure we weed out the classes that are old //
 for($i=0; $i<count($semester); $i++)
 {
  if($i==0)
	$clause.=" AND (semesterid = $semester[$i]";
  else
	$clause.=" OR semesterid = $semester[$i]";
 }

 $clause.=")";

 // Make sure we have the right classes //
 $sql = mysql_query("SELECT coursename, q1points, q2points, totalpoints, aperc, bperc, cperc, dperc, fperc, secondcourseid, semesterid FROM courses WHERE courseid = $cid[0] $clause");
 while($class = @mysql_fetch_row($sql))
 {
  // Go to the next class if we've already used the matching class //
  for($j=0; $j<count($ignore); $j++)
  {
   if($cid[0] == $ignore[$j])
	continue 2;
  }

  // Find out if we're on the first semester or second semseter //
  if($semester[0] == $class[10])
   $order = 0;
  else
   $order = 1;

  // Get the info for the current class //
  $q1 = mysql_query("SELECT q1currpoints, q2currpoints, currentpoints FROM registrations WHERE studentid = $student[0] AND courseid = $cid[0]");

  if($order == 0)
   $s1 = mysql_fetch_row($q1);
  else
   $s2 = mysql_fetch_row($q1);

  // Keep track of the IDs to ignore if we haven't used this pair yet //
  if($class[9]!="")
  {
   $ignore[] = $class[9];

   // Get the class info for the second semester of this class //
   $q2 = mysql_query("SELECT q1points, q2points, totalpoints, aperc, bperc, cperc, dperc FROM courses WHERE courseid = $class[9]");
   $secondinfo = mysql_fetch_row($q2);


   // Get the info for the second semester of this class //
   $q1 = mysql_query("SELECT q1currpoints, q2currpoints, currentpoints FROM registrations WHERE studentid = $student[0] AND courseid = $class[9]");

   if($order == 0)
	$s2 = mysql_fetch_row($q1);
   else
	$s1 = mysql_fetch_row($q1);
  }

  if($s1 != NULL)
  {
  // Calculate the grade for Q1 //
  if($class[1] != 0)
   $q1perc = $s1[0] / $class[1];
  else
   $q1perc = 0;

  $q1perc *= 100;

  if($q1perc == 0)
   $q1grade = '';
  elseif($q1perc >= $class[4])
   $q1grade = 'A';
  elseif($q1perc >= $class[5])
   $q1grade = 'B';
  elseif($q1perc >= $class[6])
   $q1grade = 'C';
  elseif($q1perc >= $class[7])
   $q1grade = 'D';
  else
   $q1grade = 'F';

  // Calculate the grade for Q2 //
  if($class[2] != 0)
   $q2perc = $s1[1] / $class[2];
  else
   $q2perc = 0;

  $q2perc *= 100;

  if($q2perc == 0)
   $q2grade = '';
  elseif($q2perc >= $class[4])
   $q2grade = 'A';
  elseif($q2perc >= $class[5])
   $q2grade = 'B';
  elseif($q2perc >= $class[6])
   $q2grade = 'C';
  elseif($q2perc >= $class[7])
   $q2grade = 'D';
  else
   $q2grade = 'F';

  // Calculate the grade for S1 //
  if($class[3] != 0)
   $s1perc = $s1[2] / $class[3];
  else
   $s1perc = 0;

  $s1perc *= 100;

  if($s1perc == 0)
   $s1grade = '';
  elseif($s1perc >= $class[4])
   $s1grade = 'A';
  elseif($s1perc >= $class[5])
   $s1grade = 'B';
  elseif($s1perc >= $class[6])
   $s1grade = 'C';
  elseif($s1perc >= $class[7])
   $s1grade = 'D';
  else
   $s1grade = 'F';
  }

  // Make sure the information is used for the class if the class only exists in the second semester //
  if($secondinfo[0]==0 && $secondinfo[1]==0 && $secondinfo[2]==0 && $secondinfo[3]==0 && $secondinfo[4]==0 && $secondinfo[5]==0 && $secondinfo[6]==0)
  {
   $secondinfo[0]=$class[1];
   $secondinfo[1]=$class[2];
   $secondinfo[2]=$class[3];
   $secondinfo[3]=$class[4];
   $secondinfo[4]=$class[5];
   $secondinfo[5]=$class[6];
   $secondinfo[6]=$class[7];
   $change = 1;
  }

  ### SECOND SEMESTER ###
  if($s2 != NULL)
  {
  // Calculate the grade for Q3 //
  if($secondinfo[0] != 0)
   $q3perc = $s2[0] / $secondinfo[0];
  else
   $q3perc = 0;

  $q3perc *= 100;

  if($q3perc == 0 || $secondinfo[3] == 0.000)
   $q3grade = '';
  elseif($q3perc >= $secondinfo[3])
   $q3grade = 'A';
  elseif($q3perc >= $secondinfo[4])
   $q3grade = 'B';
  elseif($q3perc >= $secondinfo[5])
   $q3grade = 'C';
  elseif($q3perc >= $secondinfo[6])
   $q3grade = 'D';
  else
   $q3grade = 'F';

  // Calculate the grade for Q4 //
  if($secondinfo[1] != 0)
   $q4perc = $s2[1] / $secondinfo[1];
  else
   $q4perc = 0;

  $q4perc *= 100;

  if($q4perc == 0)
	$q4grade = '';
  elseif($q4perc >= $secondinfo[3])
   $q4grade = 'A';
  elseif($q4perc >= $secondinfo[4])
   $q4grade = 'B';
  elseif($q4perc >= $secondinfo[5])
   $q4grade = 'C';
  elseif($q4perc >= $secondinfo[6])
   $q4grade = 'D';
  else
   $q4grade = 'F';

  // Calculate the grade for S2 //
  if($secondinfo[2] != 0)
   $s2perc = $s2[2] / $secondinfo[2];
  else
   $s2perc = 0;

  $s2perc *= 100;

  if($s2perc == 0)
   $s2grade = '';
  elseif($s2perc >= $secondinfo[3])
   $s2grade = 'A';
  elseif($s2perc >= $secondinfo[4])
   $s2grade = 'B';
  elseif($s2perc >= $secondinfo[5])
   $s2grade = 'C';
  elseif($s2perc >= $secondinfo[6])
   $s2grade = 'D';
  else
   $s2grade = 'F';
  }

  // Add up everything for the total grade //

  if($change)
  {
   $class[1]=0;
   $class[2]=0;
   $class[3]=0;
  }

  $totalstudentpoints = $s1[2] + $s2[2];
  $totalclasspoints   = $class[3] + $secondinfo[2];

  if($totalclasspoints != 0)
   $tperc = $totalstudentpoints / $totalclasspoints;
  else
   $tperc = 0;

  $tperc *= 100;

  if($tperc == 0)
   $tgrade = '';
  elseif($tperc >= $class[4])
   $tgrade = 'A';
  elseif($tperc >= $class[5])
   $tgrade = 'B';
  elseif($tperc >= $class[6])
   $tgrade = 'C';
  elseif($tperc >= $class[7])
   $tgrade = 'D';
  else
   $tgrade = 'F';

  ### PRINT THE TEXT ###

  // Text for the current line //
  pdf_setfont($pdf, $body, 10);
  pdf_show_xy($pdf, "$class[0]", 55, $start);

  // Q1 Grade //
  pdf_setfont($pdf, $body, 10);
  pdf_show_xy($pdf, "$q1grade", 270, $start);

  // Q2 Grade //
  pdf_setfont($pdf, $body, 10);
  pdf_show_xy($pdf, "$q2grade", 310, $start);

  // S1 Grade //
  pdf_setfont($pdf, $body, 10);
  pdf_show_xy($pdf, "$s1grade", 355, $start);

  // Q3 Grade //
  pdf_setfont($pdf, $body, 10);
  pdf_show_xy($pdf, "$q3grade", 397, $start);

  // Q4 Grade //
  pdf_setfont($pdf, $body, 10);
  pdf_show_xy($pdf, "$q4grade", 430, $start);

  // S2 Grade //
  pdf_setfont($pdf, $body, 10);
  pdf_show_xy($pdf, "$s2grade", 465, $start);

  // Total Grade //
  pdf_setfont($pdf, $body, 10);
  pdf_show_xy($pdf, "$tgrade", 510, $start);

  // Line under text //
  pdf_moveto($pdf, 50, $start-$line);
  pdf_lineto($pdf, 545, $start-$line);
  pdf_stroke($pdf);

  $start -= $next;
 }
}

################
#  ATTENDANCE  #
################
// Get the dates for comparisons //
$full = 0;
$half = 0;

$q = mysql_query("SELECT startdate, enddate FROM terms WHERE termid = $_POST[term]");
$termdates = mysql_fetch_row($q);

$q = mysql_query("SELECT sattenddate, type FROM schoolattendance WHERE studentid = $student[0]");
while($attendance = mysql_fetch_row($q))
{
 $tstart = strtotime($termdates[0]);
 $tend   = strtotime($termdates[1]);
 $attend = strtotime($attendance[0]);

 if($tstart < $attend && $attend < $tend)
 {
  if($attendance[1]=='absent')
   $full++;
  else
   $half++;
 }
}

// Attendance text //
pdf_setfont($pdf, $bodybold, 10);
pdf_show_xy($pdf, "Attendance:", 50, 100);

// Full Days Text //
pdf_setfont($pdf, $body, 10);
pdf_show_xy($pdf, "Full Days", 150, 100);

// Full Days Text Above Line //
pdf_setfont($pdf, $body, 10);
pdf_show_xy($pdf, "$full", 125, 100);

// Full Days Line //
pdf_moveto($pdf, 110, 99);
pdf_lineto($pdf, 145, 99);
pdf_stroke($pdf);

// Half Days Text //
pdf_setfont($pdf, $body, 10);
pdf_show_xy($pdf, "Half Days", 240, 100);

// Half Days Text Above Line //
pdf_setfont($pdf, $body, 10);
pdf_show_xy($pdf, "$half", 215, 100);

// Half Days Line //
pdf_moveto($pdf, 200, 99);
pdf_lineto($pdf, 235, 99);
pdf_stroke($pdf);

pdf_end_page($pdf);
}

//close it up
pdf_close($pdf);

$data = pdf_get_buffer($pdf);

// Close up the db connection //
mysql_close($dbcnx);

header('Content-type: application/pdf');
header('Content-disposition: attachment; filename=ReportCard.pdf');
header('Content-length: ' . strlen($data));
echo $data;
?>