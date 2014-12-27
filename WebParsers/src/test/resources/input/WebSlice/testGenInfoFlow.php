<?php
	function hey() {
		$z = "test";
		return "<input name=input3 value=$z>";
	}
	if ($C)
		echo hey();
	else
		echo hey();
	$x = 1;
	$y = 2;
?>

<form name="form1" action="">
	<input name="input1" value="<?php echo $_GET['input1']; ?>"/>
	<input name="input1" value="<?php echo $y; ?>"/>
</form>