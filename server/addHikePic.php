<?php
//include 'json_headers.php';

if (version_compare(PHP_VERSION, '5.1.0', '>=')) { date_default_timezone_set('UTC'); }

$base_upload_dir = "/home/ecoar4/public_html/hikes/uploads/";
//$base_upload_dir = "/Users/joe/projects/peppermint/hikes/uploads/";

function createThumbnail($inFile, $outFile, $maxWidth, $maxHeight) {
    $pathinfo = pathinfo($inFile);

    $srcImg = null;
    switch(strtolower($pathinfo['extension'])) {
        case 'gif'  : $srcImg = imagecreatefromgif($inFile); break;
        case 'png'  : $srcImg = imagecreatefrompng($inFile); break;
        case 'jpg'  : 
        case 'jpeg' : $srcImg = imagecreatefromjpeg($inFile); break;
        default: die("can't open this type of image");
    }

/*
// Constraints 
$max_width = 100; 
$max_height = 100; 
list($width, $height) = getimagesize($img_path); 
$ratioh = $max_height/$height; 
$ratiow = $max_width/$width; 
$ratio = min($ratioh, $ratiow); 
// New dimensions 
$width = intval($ratio*$width); 
$height = intval($ratio*$height); 
*/

    $origWidth = imagesx($srcImg);
    $origHeight = imagesy($srcImg);

    $ratioH = $maxHeight/$origHeight;
    $ratioW = $maxWidth/$origWidth;

    $ratio = min($ratioH, $ratioW);

    $newWidth = intval($ratio * $origWidth);
    $newHeight = intval($ratio * $origHeight);

//    $ratio = $origHeight/ $origWidth; 
//    $newHeight = $maxWidth * $ratio;

    $thumbImg = imagecreatetruecolor($newWidth, $newHeight);

    imagecopyresampled($thumbImg, $srcImg, 0, 0, 0, 0, $newWidth, $newHeight, $origWidth, $origHeight);

    imagejpeg($thumbImg, $pathinfo['dirname'] . "/" . $outFile, 95);
}


$hike_id = $_POST["hike_id"];
$point_id = $_POST["point_id"];
$lat = $_POST["lat"];
$lng = $_POST["lng"];
$username = $_POST["username"];
$ip_address = $_SERVER["REMOTE_ADDR"];

// create target folder
$today_dir = date("Y-m-d");
$today_upload_dir = $base_upload_dir . $today_dir;

is_dir($today_upload_dir) || mkdir($today_upload_dir, 0755);

// create target folder/filename and move it there
$uploadfile = $today_upload_dir . "/" . date("H.i.s") . "-" . basename($_FILES['picfile']['name']);
if (move_uploaded_file($_FILES['picfile']['tmp_name'], $uploadfile)) {
    //echo "File is valid, and was successfully uploaded.\n";
} else {
    error_log("Couldn't upload file.  Maybe it's too big?");
}


// create a few thumbnails of the file
createThumbnail($uploadfile, "S-" . basename($uploadfile), 80, 60);
createThumbnail($uploadfile, "M-" . basename($uploadfile), 300, 225);

include 'db_open.php';

$query = sprintf("insert into hike_pics (filename, path, coords, point_id, ip_address, username) values ('%s', '%s', GeomFromText('POINT(%s %s)'), '%s', INET_ATON('%s'), '%s')",
                  mysql_real_escape_string(basename($uploadfile)),
                  mysql_real_escape_string($today_dir),
                  mysql_real_escape_string($lat),
                  mysql_real_escape_string($lng),
                  mysql_real_escape_string($point_id),
                  mysql_real_escape_string($ip_address),
                  mysql_real_escape_string($username));
$result = mysql_query($query);

if (!$result) {
    $message  = 'Invalid query: ' . mysql_error() . "\n";
    $message .= 'Whole query: ' . $query;
    die($message);
}
// can get last insert id with mysql_insert_id()

include 'db_close.php';

echo "<textarea>";
echo "{\"uploadResult\":\"success\"}";
echo "</textarea>";

?>
