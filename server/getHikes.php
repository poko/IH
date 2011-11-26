<?php
include 'json_headers.php';
include 'db_open.php';

$result=mysql_query("select * from original_hikes");
$first = true;
//echo $_GET['callback'] . '(';

echo "{\"hikes\":[\n";
while ($row = mysql_fetch_object($result)) {
    if ($first !== true) {
        echo ",\n";
    } 
    echo "{\"hike_id\":\"$row->hike_id\",\"name\":\"" . htmlspecialchars($row->name) . "\",\"desc\":\"" . htmlspecialchars($row->description) . "\",\"date\":\"" . htmlspecialchars($row->date) . "\",\"username\":\"" . htmlspecialchars($row->username) . "\"}";
    $first = false;
}
echo "\n]}";

//echo ')';

include 'db_close.php';
?>
