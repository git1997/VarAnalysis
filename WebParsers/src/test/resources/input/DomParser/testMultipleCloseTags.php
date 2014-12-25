<?php 

	if ($C)
		$x = "</div></style>Some text here</a>";
	else 
		$x = "</div>Some text there</style></a>";
?>

<html>
 <head>
  <title>PHP Test</title>
 </head>
 <body>
 	<a><style><div>
 <?php 
 	echo $x;
 ?> 
 <footer>Footer</footer>
 </body>
</html>