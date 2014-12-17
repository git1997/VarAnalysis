<?php
$colors = array("red", "green", "blue", "yellow");

$fruits = array("b" => "apple", 2 => "orange", '3' => "kiwi");
$fruits[] = "blueberries";

foreach ($colors as $value) {
    echo "$value ";
}

foreach ($colors as $key => $value) {
	echo "$key ";
}

foreach ($fruits as $key => $value) {
	echo "$key:$value ";
}
?>