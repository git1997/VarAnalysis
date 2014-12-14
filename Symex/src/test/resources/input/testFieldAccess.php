<?php 

class A {
    public $foo = 1;
    public $bar = 2;
}  

$a = new A;

$a->foo = 3;

$b = new A;

$foo = "bar";

echo $a->foo . ' ' . $b->bar . ' ' . $b->$foo;

if ($c)
	$a->bar = 4;
else
	$a->bar = 5;
echo $a->bar;


?>