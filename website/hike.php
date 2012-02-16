<?php
include '../server/json_headers.php';
include '../server/db_open.php';

$hike_id = $_GET["id"];

$query = sprintf("SELECT * from hikes where hike_id = '%s'", mysql_real_escape_string($hike_id));
$result=mysql_query($query);
if (!$result) {
    $message  = 'Invalid query: ' . mysql_error() . "\n";
    $message .= 'Whole query: ' . $query;
    die($message);
}

$hike = mysql_fetch_object($result);

// get vistas
$query = sprintf("SELECT * from vistas INNER JOIN vista_actions where hike_id = '%s' AND vistas.action_id = vista_actions.action_id", mysql_real_escape_string($hike_id));
$result=mysql_query($query);
$vista_json = array();
while ($row = mysql_fetch_object($result)) {
	$vista_json[] = $row;
}
$hike->vistas = $vista_json;


//print_r($hike);

echo "<html>";
echo "<span class=\"big_16pt\"><strong>".$hike->name."<br>".$hike->description."<br>pioneered by ".$hike->username.", ".$hike->date."</strong></span>";
for ($i = 0; $i < sizeof($hike->vistas); $i++){
	$v = $hike->vistas[$i];
	echo $i + 1 .". ".$v->verbiage;
	$type = $v->action_type;
	if ($type == 'text'){
		echo "TEXTED TO FRIEND: ".$v->note;
	}
	else if ($type == 'note'){
		echo "FIELD NOTE: ".$v->note;
	}
	else if ($type == 'photo'){
		echo "<img src=\"".$v->photo."\"/>";
	}
}

echo "</html>";
include '../server/db_close.php';
?>
