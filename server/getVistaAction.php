<?php
include 'json_headers.php';
include 'db_open.php';
$amt = $_GET['amount'];
$first=true;
$result = mysql_query("SELECT action_id, action_type, action_type FROM `vista_actions` order by rand() limit $amt");

echo "{\"vista_actions\":[";
while ($row = mysql_fetch_object($result)) {
    if ($first !== true) {
        echo ",\n";
    } 
    echo "{\"action_id\":\"$row->action_id\",\"action_type\":\"$row->action_type\",\"verbiage\":\"" . htmlspecialchars($row->action_type) . "\"}";
    $first = false;
}
     echo "";
echo "]}";


include 'db_close.php';
?>
