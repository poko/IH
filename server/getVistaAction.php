<?php
include 'json_headers.php';
include 'db_open.php';
$amt = $_GET['amount'];
$first=true;
$result = mysql_query("SELECT action_id, action_type, verbiage FROM `vista_actions` order by rand() limit $amt");

echo "{\"vista_actions\":[";
while ($row = mysql_fetch_object($result)) {
    if ($first !== true) {
        echo ",\n";
    } 
    echo json_encode($row);
    $first = false;
}
     echo "";
echo "]}";


include 'db_close.php';
?>
