<?php
include 'json_headers.php';
include 'db_open.php';

$hike_id = $_GET["hike_id"];

$query = "SELECT hike_id, name, description, date, username, companion FROM hikes WHERE hike_id = $hike_id OR original_hike_id = $hike_id";
$result=mysql_query($query);
$first = true;
echo "{\"hikes\":[\n";
while ($hike = mysql_fetch_object($result)) {
    if ($first !== true) {
        echo ",\n";
    } 
    // get vistas & actions
    // determine which vista actions table to select from
    $table_name = 'vista_actions';
    if ($hike->companion == 'true'){
    	$table_name = 'companion_vista_actions';
    }
	$query = sprintf("SELECT id, hike_id, vistas.action_id, longitude, latitude, date, note, photo, verbiage, action_type from vistas INNER JOIN %s WHERE vistas.action_id = %s.action_id AND hike_id = '%s'",
					$table_name, $table_name,
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
