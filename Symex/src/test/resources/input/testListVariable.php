<?php 

$info = array('coffee', 'brown', 'caffeine');

// Listing all the variables
list($drink, $color, $power) = $info;
echo "$drink is $color and $power makes it special. ";

// Listing some of them
list($drink, , $power) = $info;
echo "$drink has $power.";

?>