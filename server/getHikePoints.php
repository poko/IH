<?php
include 'json_headers.php';
include 'db_open.php';

$query = sprintf("select * from hikes where hike_id='%s'", mysql_real_escape_string($_GET["hike_id"]));

$hike_result = mysql_query($query);
$hike_row = mysql_fetch_object($hike_result);

$result=mysql_query("select point_id, name, X(coords) as lat, Y(coords) as lng, is_poi from hike_points where hike_id='" . $_GET["hike_id"] . "' order by sequence asc");
$first = true;

echo $_GET['callback'] . '(';

echo "{\"hike\":\n";
echo "{\"name\":\"" . htmlspecialchars($hike_row->name) . "\",\n";
echo "\"points\":[\n";

while ($row = mysql_fetch_object($result)) {
    if ($first !== true) {
        echo ",\n";
    } 
    echo "{\"point_id\":\"$row->point_id\",\"name\":\"$row->name\",\"lat\":$row->lat,\"lng\":$row->lng,\"is_poi\":" . ( ($row->is_poi == 1) ? 'true' : 'false') . "}";
    $first = false;
}
echo "\n]}\n}";

echo ')';

include 'db_close.php';
?>
