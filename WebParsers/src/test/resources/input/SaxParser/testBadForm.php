<?php 
	$x = $_GET['input2'];
	$y = $_GET['a'];
	function hello() {
		
	}
	hello();
	$result = mysql_query("SELECT field1, field2 FROM table");
	$row = mysql_fetch_array($result);
	echo $row[0] . $row['field1'] . $row[1] . $row['field2'];
	$x = array();
	$x[1] = 1;
	if ($c)
		$x = 2;
	else
		$x = 3;
	echo $x;
?>
<html>

<script>
	function hi() {
		x.y = y.x;
		document.getElementById('id1').value =1;
		document.form1.input2.value = document.form1.input2.value;
		x = 1;
		if (c)
			x = 2;
		else
			x = 3;
		alert(x);
	}
	hi();
</script>

<a href="testWebSlice.php?a=1"></a>
<div id="id1"></div>
<form name="form1" action="">
	<input name="input1" VALUE="1"/>
	<input name="input2" value="<?php echo $x; ?>"/>
</form>

</html>