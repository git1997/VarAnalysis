<?php 

function hi($a, &$b) {
	global $t;
	echo $t;
	echo $a;
	echo $b;
	$t = 1;
	$a = 2;
	$b = 3;
	return $a;
}

$t = 1;

$x = 1;
$y = 2;
echo hi($x, $y);
echo $x . $y;

$x = 3;
$y = 4;
echo hi($x, $y);
echo $x . $y;

echo $t;
?>