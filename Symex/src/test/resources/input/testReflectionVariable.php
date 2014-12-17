<?php 

$a = 'b';
$b = 'y';

echo "{$a}"; // output 'b', the brackets are used to limit the variable
echo "$$a"; // output '$b'
echo $$a;	// output 'y'

?>