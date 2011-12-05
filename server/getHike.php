<?php
include 'json_headers.php';
include 'db_open.php';

$hike_id = $_GET["hike_id"];

$query = sprintf("SELECT * from original_hikes where hike_id = '%s'", mysql_real_escape_string($hike_id));
$result=mysql_query($query);
if (!$result) {
    $message  = 'Invalid query: ' . mysql_error() . "\n";
    $message .= 'Whole query: ' . $query;
    die($message);
}

$hike = mysql_fetch_object($result);

// get points
$query = sprintf("SELECT * from original_hike_points where hike_id = '%s'", mysql_real_escape_string($hike_id));
$result=mysql_query($query);
$points_json = array();
while ($row = mysql_fetch_object($result)) {
	$points_json[] = $row;
}
$hike->points = $points_json;

// get vistas
$query = sprintf("SELECT * from original_vistas where hike_id = '%s'", mysql_real_escape_string($hike_id));
$result=mysql_query($query);
$vista_json = array();
while ($row = mysql_fetch_object($result)) {
	$vista_json[] = $row;
}
$hike->vistas = $vista_json;

// set vista actions
$vista_length = count($vista_json);
$query = "SELECT action_id, verbiage, action_type from vista_actions order by rand() limit $vista_length";
$result=mysql_query($query);
for ($i=0; $i<mysql_num_rows($result); $i++) {
	$row = mysql_fetch_object($result);
	$vista_json[$i]->new_vista_action = $row;
}

$hike_resp = array('hike'=> $hike);
echo json_encode($hike_resp);
include 'db_close.php';
?>
