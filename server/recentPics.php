<?php
include 'db_open.php';

$limit = $_GET['limit'];

if (empty($_GET['limit'])) { $limit = 5; }

$query = sprintf("select * from hike_pics order by creation_dt desc limit %s", 
                mysql_real_escape_string($limit)
         );

$result = mysql_query($query);

while ($row = mysql_fetch_object($result)) {
    echo "<div style=\"float: left; padding: 4px;\">";
    echo "<a href=\"http://ecoarttech.net/hikes/uploads/$row->path/$row->filename\" title=\"". htmlspecialchars($row->username . " " . $row->creation_dt . " " . $row->point_id, ENT_QUOTES) . "\"><img src=\"http://ecoarttech.net/hikes/uploads/$row->path/M-$row->filename\" border=\"0\"></a>\n";
    echo "</div>\n";
}

include 'db_close.php';
?>
