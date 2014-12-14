<?php 

$cars = array("Volvo", "BMW", "Toyota");
$cars['1'] = 'Lexus'; 
echo "I like " . $cars[0] . ", " . $cars[1] . " and " . $cars[2] . ".";

$ben = "Ben";

$age = array("Peter"=>"35", "Joe"=>"43");
$age[$ben] = 37;
echo "Peter is " . $age['Peter'] . " years old. Ben is " . $age[$ben] . " years old.";

if ($c)
	$age[1] = 'a';
else 
	$age[1] = 'b';
echo $age[1];
?>