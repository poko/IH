<?php
include 'json_headers.php';
include 'db_open.php';

$hike_id = $_GET["hike_id"];

//$result=mysql_query("select * from original_hikes");
$query = sprtintf("SELECT * from original_hikes where hike_id = '%s'", mysql_real_escape_string($hike_id));
$result=mysql_query($query);

$row = mysql_fetch_object($result);
$hike_resp = array('hike'=> $row);
echo $hike_resp;

//$first = true;
//echo "{\"hikes\":[\n";
//while ($row = mysql_fetch_object($result)) {
//    if ($first !== true) {
//        echo ",\n";
//    } 
//    echo "{\"hike_id\":\"$row->hike_id\",\"name\":\"" . htmlspecialchars($row->name) . "\",\"desc\":\"" . htmlspecialchars($row->description) . "\",\"date\":\"" . htmlspecialchars($row->date) . "\",\"username\":\"" . htmlspecialchars($row->username) . "\"}";
//    $first = false;
//}
//echo "\n]}";


include 'db_close.php';
?>
