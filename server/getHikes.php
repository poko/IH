<?php
include 'json_headers.php';
include 'db_open.php';

$result=mysql_query("select * from hikes where active=1 order by location, sequence");
$first = true;
echo $_GET['callback'] . '(';

echo "{\"hikes\":[\n";
while ($row = mysql_fetch_object($result)) {
    if ($first !== true) {
        echo ",\n";
    } 
    echo "{\"hike_id\":\"$row->hike_id\",\"name\":\"" . htmlspecialchars($row->name) . "\",\"location\":\"" . htmlspecialchars($row->location) . "\"}";
    $first = false;
}
echo "\n]}";

echo ')';

include 'db_close.php';
?>
