<?php

// Test if with 3 values;
$y = 1;
if ($bar)
	$y = 2;
else
	$y = 3;

echo $y;

// Test if with 2 values;

$z1 = 1;
if ($bar)
	$z1 = 2;

echo $z1;

if ($bar)
	$z2 = 2;
else 
	$z2 = 3;

echo $z2;


// Test nested if
if ($foo) {
	if ($bar)
		$x = "Hey";
	else 
		$x = "Hi";
}
else
	$x = "Hello";

echo $x;

?>