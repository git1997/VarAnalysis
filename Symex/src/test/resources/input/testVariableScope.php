<?php 

$x = 1;

$y = 2;

function hi() {
	$x = 3;
	global $y;
	echo $x . $y . $z;
	$y = 4;
}

hi();
echo $x . $y;
?>