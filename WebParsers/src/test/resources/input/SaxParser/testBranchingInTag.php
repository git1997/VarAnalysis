<?php 

	if ($C)
		$x = "val1='1' val2='2'><style>";
	else 
		$x = "val3='3'><div>";

?>

<html>
<head>
<script>
	JavaScript code
</script>
</head>
<body>
	Some text here
	<input name="input1" value='0' <?php echo $x; ?>
	<input name="input2" />
	Some text there
</body>

</html>

