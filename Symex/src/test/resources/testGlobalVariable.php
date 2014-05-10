<?php 

$x = "Hello";

function hello() {
	global $x;
	$x = "Hi";
}

hello();

echo "$x";

?>