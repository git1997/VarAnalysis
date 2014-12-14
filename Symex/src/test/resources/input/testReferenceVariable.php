<?php
function foo(&$var)
{
    $var = 'ab';
}

$a=5;
foo($a);
echo $a;
?>
