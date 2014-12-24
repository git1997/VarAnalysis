<?php 

	if ($C)
		$x = "ab' val1='1' val2='2'><style>";
	else 
		$x = "cd' val3='3'><div>";

	if ($D)
		$y = "bb";
	else
		$y = "cc";
	
	if ($E)
		$z = "dd'";
	else
		$z = "ee'";
?>

<html>
<head>
<script>
	JavaScript code
</script>
</head>
<body>
	Some text here
	<input name="input1" value=' <?php echo $x; ?>
	<input name="input2" value='aa<?php echo $y . $z; ?>/>
	Some text there
</body>

</html>

