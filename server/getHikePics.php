<?php
include 'json_headers.php';
include 'db_open.php';

$point_id = $_GET['point_id'];
$start_date = $_GET['start_date']; 
$limit = $_GET['limit'];
$direction = $_GET['direction'];
$direction_operator = "<";

if ($direction == "rev") {
    $direction_operator = ">";
}

if (empty($_GET['limit'])) {
    $limit = 5;
}

if (empty($_GET['point_id'])) {
    $point_id = 0;
}

$query = null;

if (empty($_GET['start_date'])) {
$query = sprintf("select * from hike_pics where point_id='%s' and creation_dt < now() order by creation_dt desc limit %s", 
                mysql_real_escape_string($point_id),
                mysql_real_escape_string($limit)
         );
} else {
$query = sprintf("select * from hike_pics where point_id='%s' and creation_dt %s TIMESTAMP('%s') order by creation_dt desc limit %s",
                mysql_real_escape_string($point_id),
                $direction_operator,
                mysql_real_escape_string($start_date),
                mysql_real_escape_string($limit)
         );
}

$result = mysql_query($query);

echo $_GET['callback'] . '(';

echo "{\"picResults\":\n";
echo "{\"point_id\":\"$point_id\",\n";
//echo "\"query\":\"$query\",\n";
echo "\"pics\":[\n";
$first = true;
while ($row = mysql_fetch_object($result)) {
    if ($first !== true) {
        echo ",\n";
    } 
    echo "{\"filename\":\"$row->filename\",\"path\":\"$row->path\",\"username\":\"$row->username\",\"creation_dt\":\"$row->creation_dt\"}";
    $first = false;
}
echo "\n]}\n}";

echo ')';

include 'db_close.php';
?>
