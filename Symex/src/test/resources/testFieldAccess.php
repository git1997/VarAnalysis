<?php 

class A {
    public $foo = 1;
    public $bar = 2;
}  

$a = new A;

$b = new A;

$foo = "bar";

echo $a->foo . ' ' . $b->bar . ' ' . $b->$foo;


?>