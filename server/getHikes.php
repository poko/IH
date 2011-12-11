<?php
include 'json_headers.php';
include 'db_open.php';

$hike_id = $_GET["hike_id"];

$query = "SELECT hike_id, name, description, date, username FROM original_hikes WHERE hike_id = $hike_id OR original_hike_id = $hike_id";
$result=mysql_query($query);
$first = true;
echo "{\"hikes\":[\n";
while ($hike = mysql_fetch_object($result)) {
    if ($first !== true) {
        echo ",\n";
    } 
    // get vistas & actions
	$query = sprintf("SELECT hike_id, name, username, description, date, start_lat, start_lng, original, original_hike_id from original_vistas INNER JOIN vista_actions WHERE original_vistas.action_id = vista_actions.action_id AND hike_id = '%s'",
					mysql_real_escape_string($hike->hike_id));
	$res=mysql_query($query);
	$vista_json = array();
	while ($row = mysql_fetch_object($res)) {
		$vista_json[] = $row;
	}
	$hike->vistas = $vista_json;
    echo json_encode($hike);
    $first = false;
}
echo "\n]}";


include 'db_close.php';
?>
