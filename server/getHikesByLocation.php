<?php
include 'json_headers.php';
include 'db_open.php';

$user_lat = $_GET["latitude"];
$user_lng = $_GET["longitude"];

$query = "SELECT hike_id, name, description, date, username, companion, ( 3959 * acos( cos( radians($user_lat) ) * cos( radians( start_lat ) ) * cos( radians( start_lng ) - radians($user_lng) ) + sin( radians($user_lat) ) * sin( radians( start_lat ) ) ) ) AS distance FROM hikes WHERE original=true HAVING distance < 25 ORDER BY distance LIMIT 0 , 20";
$result=mysql_query($query);
$first = true;

echo "{\"hikes\":[\n";
while ($row = mysql_fetch_object($result)) {
    if ($first !== true) {
        echo ",\n";
    } 
    echo "{\"hike_id\":\"$row->hike_id\",\"name\":\"" . htmlspecialchars($row->name) . "\",\"description\":\"" . htmlspecialchars($row->description) . "\",\"date\":\"" . htmlspecialchars($row->date) . "\",\"username\":\"" . htmlspecialchars($row->username) . "\",\"companion\":\"" . htmlspecialchars($row->companion) . "\"}";
    $first = false;
}
echo "\n]}";


include 'db_close.php';
?>
