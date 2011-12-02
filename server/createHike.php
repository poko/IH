<?php
//include 'json_headers.php';

if (version_compare(PHP_VERSION, '5.1.0', '>=')) { date_default_timezone_set('UTC'); }

//$base_upload_dir = "/home/ecoar4/public_html/hikes/uploads/";
$base_upload_dir = "/Users/poko/Development/ecoarttech/IH+/uploads";

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

$hike_name = $_POST["hike_name"];
$hike_desc = $_POST["description"];
$username = $_POST["username"];
$vistas = $_POST["vistas"];
$start_lat = $_POST["start_lat"];
$start_lng = $_POST["start_lng"];
$ip_address = $_SERVER["REMOTE_ADDR"];

// create target folder
$today_dir = date("Y-m-d");
$today_upload_dir = $base_upload_dir . $today_dir;

is_dir($today_upload_dir) || mkdir($today_upload_dir, 0755);

print_r($_FILES);

// create target folder/filename and move it there
//$uploadfile = $today_upload_dir . "/" . date("H.i.s") . "-" . basename($_FILES['picfile']['name']);
//if (move_uploaded_file($_FILES['picfile']['tmp_name'], $uploadfile)) {
    //echo "File is valid, and was successfully uploaded.\n";
//} else {
//    error_log("Couldn't upload file.  Maybe it's too big?");
//}


// create a few thumbnails of the file
//createThumbnail($uploadfile, "S-" . basename($uploadfile), 80, 60);
//createThumbnail($uploadfile, "M-" . basename($uploadfile), 300, 225);

include 'db_open.php';

$query = sprintf("insert into original_hikes (username, name, description, start_lat, start_lng, ip_address) values ('%s', '%s', '%s', '%s', '%s', '%s')",
                  mysql_real_escape_string($username),
                  mysql_real_escape_string($hike_name),
                  mysql_real_escape_string($hike_desc),
                  mysql_real_escape_string($start_lat),
                  mysql_real_escape_string($start_lng),
                  mysql_real_escape_string($ip_address));
$result = mysql_query($query);

if (!$result) {
    $message  = 'Invalid query: ' . mysql_error() . "\n";
    $message .= 'Whole query: ' . $query;
    die($message);
}
//$hike_id = mysql_insert_id();

// save each vista point
$vista_json = json_decode(str_replace('\\', '', $vistas), true);
foreach ($vista_json as $v){
	$query = sprintf("insert into original_vistas (action_id, longitude, latitude, date, note, filename) values ('%s', '%s', '%s', '%s', '%s', '%s')",
                  mysql_real_escape_string($v["action_id"]),
                  mysql_real_escape_string($v["latitude"]),
                  mysql_real_escape_string($v["longitude"]),
                  mysql_real_escape_string($v["date"]),
                  mysql_real_escape_string($v["note"]),
                  mysql_real_escape_string($v["photo"]));
	$result = mysql_query($query);
	if (!$result){
		$message  = 'Invalid query: ' . mysql_error() . "\n";
    	$message .= 'Whole query: ' . $query;
    	die($message);
	}
	$hike_id = mysql_insert_id();
	foreach ($v["points"] as $p){
		$query = sprintf("insert into original_vista_points(hike_id, index, longitude, latitude) values ('%s','%s','%s','%s')",
				mysql_real_escape_string($hike_id),
				mysql_real_escape_string($p["index"]),
				mysql_real_escape_string($p["latitude"]),
				mysql_real_escape_string($p["longitude"])));
		$result = mysql_query($query);
		if (!$result){
			$message  = 'Invalid query: ' . mysql_error() . "\n";
   	 		$message .= 'Whole query: ' . $query;
   	 		die($message);
		}
	}
}

// save the photos

//foreach ($_FILES["photos"]["error"] as $key => $error) {
//    if ($error == UPLOAD_ERR_OK) {
  //      $tmp_name = $_FILES["photos"]["tmp_name"][$key];
    //    $name = $_FILES["photos"]["name"][$key];
//        move_uploaded_file($tmp_name, "$today_upload_dir/". date("H.i.s") ."$name");
//    }
//}

foreach ($_FILES as $file){
	// create target folder/filename and move it there
	$uploadfile = $today_upload_dir . "/" . date("H.i.s") . "-" . basename($file['name']).".jpg";
	echo "up file: ".$uploadfile;
	if (move_uploaded_file($file['tmp_name'], $uploadfile)) {
	    echo "File is valid, and was successfully uploaded.\n";
	} else {
	    error_log("Couldn't upload file.  Maybe it's too big?");
	}
}



// can get last insert id with mysql_insert_id()

include 'db_close.php';

echo "{\"result\":\"success\"}";

?>
