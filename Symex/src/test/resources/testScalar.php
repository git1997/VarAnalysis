<?php 

$type1 = 01;
$type2 = 123;
$type3 = 123.4;
$type4 = "abc";
$type5 = 'abc';
$type6 = "abc$x'";
$type7 = ABC;
$type8 = __ABC__;

$x = "abc\"def\nghi";
$y = 'abc\"def\tghi"';

echo $type1 . " " . $type2 . " " . $type3 . " " . $type4 . " " . $type5 . " " . $type6 . " " . $type7 . " " . $type8;

echo $x . " " . $y;

?>