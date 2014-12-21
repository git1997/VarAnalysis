<?php
function hi($t) {
	$x = $y;
}

function hey($y) {
	$x = $y;
}

hi($a);
hi($a);

hey($a);
hey($a);
?>