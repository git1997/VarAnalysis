<?php 

$a = 1;
$b = &$a;
$b = 2;
echo $a;

?>