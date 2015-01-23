<?php 

function hi() {
	if ($c) {
		echo "Hello";
		return;
	}
	echo "Hi";
}

function hello() {
	if ($c) {
		echo "Hello2";
		return;
	}
	echo "Hi2";
	return;
}

hi();

hello();
?>