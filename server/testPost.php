<?php
echo "<pre>". print_r($_SERVER) . "</pre>";
echo "<br /><br />";

echo print_r($_POST);
echo "<br/>";
echo "<pre>".print_r($_REQUEST)."</pre>";

echo print_r($_FILES);

$data = file_get_contents('php://input');
print "DATA: <pre>";
var_dump($data);
var_dump($_POST);
print "</pre>";

echo print_r($_GET);

?>
