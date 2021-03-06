<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

	<head>
		<title>indeterminate hikes + | ecoarttech</title>
        <meta http-equiv="Pragma" content="no-cache" />
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<meta name="keywords" content="digital and networked environmental art, eco art, green cultural studies, eco criticism, conceptual art, video art, digital video, new media and digital art technologies, New York, NYC, Maine, USA" content="ECOARTTECH draws on ideas in digital art, critical theory, ecology, eco-art, environmental literature, environmental philosophy, ecocriticism, and environmentalist activism.">
		<link rel="SHORTCUT ICON" href="http://www.ecoarttech.net/images/favicon.ico" />
<link href="../../eco_art11.css" rel="stylesheet" type="text/css" />
        <script src="../Scripts/AC_RunActiveContent.js" type="text/javascript"></script>
		<script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?key=AIzaSyA-hCqc1MrMdUWQd60F08eTdGi8BI0TUv0&sensor=false"></script>
		<script type="text/javascript">
			function initialize(){
				title = document.getElementById("map_canvas").title;
    			//alert(latLngStr);
    			titleSplit = title.split("|");
    			// start point
    			latLngStr = titleSplit[0];
    			latLng = latLngStr.split(",");
    			m = map(latLng[0], latLng[1]);
    			vistasSplit = titleSplit[1].split(";");
    			var fullBounds = new google.maps.LatLngBounds();
    			// add all vista markers
    			for (j = 0; j < vistasSplit.length; j++){
    				vLatLng = vistasSplit[j].split(",");
    				addMarker(vLatLng[0], vLatLng[1], m, fullBounds);
    			}
    			// add points
    			pointsSplit = titleSplit[2].split(";");
    			var pointsCoords = [];
    			for (k = 0; k < pointsSplit.length; k++){
    				pLatLng = pointsSplit[k].split(",");
    				pLat = pLatLng[0]/1000000;
    				pLng = pLatLng[1]/1000000;
    				if (pLat != 0 && pLng != 0){
    					point = new google.maps.LatLng(pLat, pLng);
    					pointsCoords.push(point);
    					fullBounds.extend(point);
    				}
    			}
    			var hikePath = new google.maps.Polyline({
					path: pointsCoords,
					strokeColor: "#339BF7",
					strokeOpacity: 1.0,
					strokeWeight: 3
				});

				hikePath.setMap(m);
				m.fitBounds(fullBounds);
			}
			
      		function map(lat, long) {
				var latLng = new google.maps.LatLng(lat, long);
        		var myOptions = {
        			center: latLng,
          			zoom: 16,
          			disableDefaultUI: true,
          			mapTypeId: google.maps.MapTypeId.ROADMAP
        		};
        		var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
        		var image = new google.maps.MarkerImage('vista.png',new google.maps.Size(17, 17),
      						// The origin for this image is 0,0.
      						new google.maps.Point(0,0),
      						// The anchor for this image is the base of the flagpole at 0,32.
      						new google.maps.Point(8, 8));
        		var marker = new google.maps.Marker({
       				position: latLng, 
      				map: map,
      				icon: image
    			});
    			return map;
			}
      
      		function addMarker(lat, long, map, bounds){
				var latLng = new google.maps.LatLng(lat, long);
				if (lat != 0 && long != 0)
					bounds.extend(latLng);
        		var image = new google.maps.MarkerImage('vista.png',new google.maps.Size(17, 17),
      						// The origin for this image is 0,0.
      						new google.maps.Point(0,0),
      						// The anchor for this image is the base of the flagpole at 0,32.
      						new google.maps.Point(8, 8));
       			var marker = new google.maps.Marker({
       				position: latLng, 
      				map: map,
      				icon: image,
    			});
      	}
    </script>
<style type="text/css">
<!--
body {
	background-color: #666666;
}
.style1 {color: #0066FF}
.style8 {font-size: 9px; color: black;}
-->
</style></head>

	<body onload="initialize()">
		<table width="980" border="0" cellspacing="2" cellpadding="2" align="center">
	  <tr>
				<td align="left" valign="top" bgcolor="#CCCCCC"><a href="http://www.ecoarttech.net"><img src="../../images/feature_work_ban_img.gif" width="980" height="64" border="0" /></a><br />
				  <table width="980" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <th width="95" align="left" valign="top" bgcolor="#FFFFFF" scope="col"><table width="95" border="0" cellspacing="4" cellpadding="2">
                        <tr>
                          <th align="left" valign="top" scope="col"><a href="http://www.ecoarttech.net"><img src="../../images/wmv_player.jpg" alt="" width="95" height="58" border="0" /></a><br />
                            <a href="../index.html"><br />
                            </a> <a href="http://www.ecoarttech.net/indeterminatehikes">IH+<br />
                            </a><a href="../../about.html">about</a><br />
                            <a href="../../works.html">works</a><br />
                            <a href="../../events.html">news/events</a><br />
                            <br />
                            <a href="http://www.vimeo.com/ecoarttech" target="_blank">videos</a><br />
                            <a href="http://www.ecoarttech.net/blog">research blog</a><br />
                            <br />
                            <a href="../../downloads/index.html">press images</a><br />
                            <a href="../../contact.html">contact</a><br />
                            <br />
                            <a href="http://ecoarttech.us4.list-manage1.com/subscribe?u=99a487b9dc5ad90e627613744&amp;id=914c29f4db" target="_blank"><img src="../../images/mailing_list.gif" alt="ecoarttech mailing list" width="92" height="32" border="0" /></a> <br />
                            <br />
                            <img src="../images/nav_line.gif" alt="" width="90" height="1" /></th>
                        </tr>
                        <tr>
                          <th align="left" valign="top" scope="row"><table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                              <th align="left" valign="top" bgcolor="#CCCCCC" scope="col"><img src="../../images/facebook.gif" alt="" width="16" height="16" border="0" /> <a href="http://www.facebook.com/ecoarttech" target="_blank"> ecoarttech facebook group</a><br />
                              </th>
                            </tr>
                          </table>
                          </th>
                        </tr>
                      </table>
                        <p><span class="small"><br />
                      <br />
                      <br />
                      <br />
                      </span></p></th>
                      <td width="885" align="left" valign="top" scope="col"><br />
<span class="bigHighlite"><strong>indeterminate hikes+</strong></span><strong><br />
                            <span class="bodystylehighlitePROMPT">ecoarttech's wilderness-actualizing app</span></strong><br />
                        <br />
                        <table width="640" border="0" cellspacing="4" cellpadding="2">
                          <tr>
                            <td align="left" valign="top"><?php
include '../scripts/db_open.php';
$hike_id = $_GET["id"];

$query = sprintf("SELECT * from hikes where hike_id = '%s'", mysql_real_escape_string($hike_id));
$result=mysql_query($query);
if (!$result) {
    $message  = 'Invalid query: ' . mysql_error() . "\n";
    $message .= 'Whole query: ' . $query;
    die($message);
}

$hike = mysql_fetch_object($result);

// get vistas
if ($hike->companion)
	$query = sprintf("SELECT * from vistas INNER JOIN companion_vista_actions where hike_id = '%s' AND vistas.action_id = vista_actions.action_id", mysql_real_escape_string($hike_id));
else
	$query = sprintf("SELECT * from vistas INNER JOIN vista_actions where hike_id = '%s' AND vistas.action_id = vista_actions.action_id", mysql_real_escape_string($hike_id));
$result=mysql_query($query);
$vista_json = array();
while ($row = mysql_fetch_object($result)) {
	$vista_json[] = $row;
}
$hike->vistas = $vista_json;

// get points
$query = sprintf("SELECT * from hike_points where hike_id = '%s'", mysql_real_escape_string($hike_id));
$result=mysql_query($query);
$points = array();
while ($row = mysql_fetch_object($result)) {
	$points[] = $row;
}
$hike->points = $points;

function vistaLocs($vistas){
	$res = "";
	foreach ($vistas as $v){
		$res .= $v->latitude.",".$v->longitude.";";
	}
	return (string)$res;
}

function hikePoints($points){
	$res = "";
	foreach ($points as $p){
		$res .= $p->latitude.",".$p->longitude.";";
	}
	return (string)$res;
}

echo "<span class=\"body12point\"><strong>Hike Name: </strong>".$hike->name."<br><strong>Description: </strong>".$hike->description."<br><strong>Pioneered by: </strong>".$hike->username.", ".date("Y.m.d", strtotime($hike->date))."</span><br/>
<img src=\"../../images/line_600.gif\"><br><br>";
echo "<div id=\"map_canvas\" title=\"".$hike->start_lat.",".$hike->start_lng."|".vistaLocs($hike->vistas)."|".hikePoints($hike->points)."\" style=\"width:500px; height:320px; border:1px\"></div>";
for ($i = 0; $i < sizeof($hike->vistas); $i++){
	$v = $hike->vistas[$i];
	echo "<br/><span class=\"bodystylehighlite\">Scenic Vista #".($i + 1).". ".$v->verbiage."</span>";
	$type = $v->action_type;
	if ($type == 'text'){
		echo "<br/><span class=\"bodystylehighlitePROMPT\">TEXTED TO A FRIEND: ".$v->note."</span></br>";
	}
	else if ($type == 'note'){
		echo "<br/><span class=\"bodystylehighlitePROMPT\">MADE A FIELD NOTE: ".$v->note."</span></br>";
	}
	else if ($type == 'photo'){
		echo "<br/><img src=\"http://www.ecoarttech.net/ih_plus/uploads/".$v->photo."\"/></br>";
	}
}

include '../scripts/db_close.php';
?><br /><br />
                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                  <td align="left" valign="top" bgcolor="#FFFFFF"><a href="http://www.ecoarttech.net/indeterminatehikes"><strong>IH+ is a wilderness-actualizing app by ecoarttech... more &gt;&gt;</strong></a></td>
                                </tr>
                              </table>
                            <a href="http://www.ecoarttech.net/indeterminatehikes"></a></td>
                          </tr>
                        </table>

                        <table width="100%" border="0" cellspacing="4" cellpadding="2">
                          <tr>
                            <td align="left" valign="top" class="small" scope="col"><img src="images/nav_line.gif" alt="" width="840" height="1" /><br />
                              <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/"><br />
                                IH+ is a project by ecoarttech (leila nadir &amp; cary peppermint)<br />
                                <br />
                                software development:<br />
                                polina&nbsp;koronkevich<br />
                                <br />
                                created with funding from:<br />
                                new york state council on the arts<br />
                                new york foundation for the arts<br />
                                whitney museum of american art<br />
                                university of rochester<br />
                                <br />
                                (cc) 2011 ecoarttech<br />
                                attribution - noncommercial<br />
                                share alike<br />
                                <br />
                                <img alt="Creative Commons License" style="border-width:0" src="http://creativecommons.org/images/public/somerights20.png"/><br />
                            </a><br/>
                              This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/">Creative Commons Attribution-Noncommercial-No Derivative Works 3.0 Unported License</a><br />
                            </td>
                          </tr>
                        </table>                        
                        </span></td>
                    </tr>
                  </table>	    </td>
	  </tr>
		</table>

</body>

</html>

