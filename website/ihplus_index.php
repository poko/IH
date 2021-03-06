<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

	<head>
		<title>indeterminate hikes + | experience ecoarttech's wilderness actualizing app</title>
        <meta http-equiv="Pragma" content="no-cache" />
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<meta name="keywords" content="digital and networked environmental art, eco art, green cultural studies, eco criticism, conceptual art, video art, digital video, new media and digital art technologies, New York, NYC, Maine, USA" content="ECOARTTECH draws on ideas in digital art, critical theory, ecology, eco-art, environmental literature, environmental philosophy, ecocriticism, and environmentalist activism.">
		<link href="eco_art11.css" rel="stylesheet" type="text/css" media="all" />
        <link rel="SHORTCUT ICON" href="http://www.ecoarttech.net/images/favicon.ico" />
<link href="../eco_art11.css" rel="stylesheet" type="text/css" />
        <script src="Scripts/AC_RunActiveContent.js" type="text/javascript"></script>
    <script type="text/javascript"
      src="http://maps.googleapis.com/maps/api/js?key=AIzaSyA-hCqc1MrMdUWQd60F08eTdGi8BI0TUv0&sensor=false">
    </script>
    <script type="text/javascript">
    	function initAll(){
    		for (i=0; i < 4; i++){
    			title = document.getElementById("map_canvas_"+i).title;
    			//alert(latLngStr);
    			titleSplit = title.split("|");
    			// start point
    			latLngStr = titleSplit[0];
    			latLng = latLngStr.split(",");
    			m = initialize(latLng[0], latLng[1], i);
    			// rest of vistsas
    			vistasSplit = titleSplit[1].split(";");
    			var fullBounds = new google.maps.LatLngBounds();
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
				//m.fitBounds(fullBounds);
    		}
    	}
      function initialize(lat, long, id) {
		var latLng = new google.maps.LatLng(lat, long);
        var myOptions = {
          center: latLng,
          zoom: 15,
          disableDefaultUI: true,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        var map = new google.maps.Map(document.getElementById("map_canvas_"+id),
            myOptions);
        var image = new google.maps.MarkerImage('images/vista.png',new google.maps.Size(17, 17),
      		// The origin for this image is 0,0.
      		new google.maps.Point(0,0),
      		// The anchor for this image is the base of the flagpole at 0,32.
      		new google.maps.Point(8, 8));
        var marker = new google.maps.Marker({
       		position: latLng, 
      		map: map,
      		icon: image,
    	});
    	return map;
      }
      
      function addMarker(lat, long, map, bounds){
		var latLng = new google.maps.LatLng(lat, long);
		if (lat != 0 && long != 0)
			bounds.extend(latLng);
        var image = new google.maps.MarkerImage('images/vista.png',new google.maps.Size(17, 17),
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

	<body onload="initAll()">
		<table width="980" border="0" cellspacing="2" cellpadding="2" align="center">
	  <tr>
				<td align="left" valign="top" bgcolor="#CCCCCC"><a href="http://www.ecoarttech.net"><img src="../images/feature_work_ban_img.gif" width="980" height="64" border="0" /></a><br />
				  <table width="980" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <th width="95" align="left" valign="top" bgcolor="#FFFFFF" scope="col"><table width="95" border="0" cellspacing="4" cellpadding="2">
                        <tr>
                          <th align="left" valign="top" scope="col"><a href="http://www.ecoarttech.net"><img src="../images/wmv_player.jpg" alt="" width="95" height="58" border="0" /></a><br />
                            <a href="index.html"><br />
                            </a> <a href="../about.html">about</a><br />
                            <a href="../works.html">works</a><br />
                            <a href="../events.html">news/events</a><br />
                            <br />
                            <a href="http://www.vimeo.com/ecoarttech" target="_blank">videos</a><br />
                            <a href="http://www.ecoarttech.net/blog">research blog</a><br />
                            <br />
                            <a href="../downloads/index.html">press images</a><br />
                            <a href="../contact.html">contact</a><br />
                            <br />
                            <a href="http://ecoarttech.us4.list-manage1.com/subscribe?u=99a487b9dc5ad90e627613744&amp;id=914c29f4db" target="_blank"><img src="../images/mailing_list.gif" alt="ecoarttech mailing list" width="92" height="32" border="0" /></a> <br />
                            <br />
                            <img src="images/nav_line.gif" alt="" width="90" height="1" /></th>
                        </tr>
                        <tr>
                          <th align="left" valign="top" scope="row"><table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                              <th align="left" valign="top" bgcolor="#CCCCCC" scope="col"><img src="../images/facebook.gif" alt="" width="16" height="16" border="0" /> <a href="http://www.facebook.com/ecoarttech" target="_blank"> ecoarttech facebook group</a><br />
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
                      <td width="885" align="left" valign="top" scope="col">
                        <br />
                        <table width="872" border="0" cellspacing="2" cellpadding="2">
                        <tr>
                          <td width="58%" align="left" valign="top"><span class="bigHighlite"><strong>indeterminate hikes+</strong></span><strong><br />
                            <span class="bodystylehighlitePROMPT">ecoarttech's wilderness-actualizing app</span><br />
                            <br />
                         <iframe src="http://player.vimeo.com/video/35096318?title=0&amp;byline=0&amp;portrait=0" width="495" height="278" frameborder="0" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>
                          </strong>

                          <strong>IH+ is an Android app that transforms everyday landscapes into sites of bio-cultural diversity and wild happenings</strong>.</strong> Generally devices of rapid communication and consumerism, smartphones are re-appropriated by IH+ as tools of environmental imagination and meditative wonder, renewing awareness of intertwining biological, cultural, and media ecologies and slowing us down at the same time. The app works by importing the rhetoric of wilderness into virtually any place accessible by Google Maps and encouraging its users to treat these locales as spaces worthy of the attention accorded to sublime landscapes, such as canyons and gorges. <br />
                          <br />
                 
                          <a href="https://market.android.com/details?id=net.ecoarttech.ihplus&amp;feature=search_result#?t=W251bGwsMSwxLDEsIm5ldC5lY29hcnR0ZWNoLmlocGx1cyJd" target="_blank"><img src="images/android_download.gif" alt="indeterminate hikes android market download" width="166" height="57" border="0" /><br />
                          <br />
                          </a><span class="style1"><strong>Apple iOS version coming June 2012.</strong></span><br />
                          <br />
                          <strong>IH+ can be experienced in two ways: <br />
(1) as a participatory public event  led by ecoarttech-guides along with a basecamp installation (e.g. at festivals/exhibitions)<br />
(2) as a self-guided excursion taken by a hiker equipped with an IH+-enabled smartphone. </strong></p>
                            <p>&nbsp;</p>
                            <br />
                            <br /></td>
                          <td width="42%" align="left" valign="top"></p>
                            <table width="100%" border="0" cellspacing="4" cellpadding="2">
                              <tr>
                                <td align="left" valign="top"><span class="bigHighlite">recent hikes</span><br />
                                </td>
                              </tr>
                            </table>
                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                              <tr>
                                <td align="left" valign="top" >
<?php
include '../ih_plus/scripts/db_open.php';

$query = "SELECT hike_id, name, description, date, username, start_lat, start_lng FROM hikes order by hike_id desc limit 4";
$result=mysql_query($query);
$hikes = array();
while ($hike = mysql_fetch_object($result)) {
    // get vistas & actions
	$query = sprintf("SELECT id, hike_id, vistas.action_id, longitude, latitude, photo, action_type from vistas INNER JOIN vista_actions WHERE vistas.action_id = vista_actions.action_id AND hike_id = '%s'",
					mysql_real_escape_string($hike->hike_id));
	$res=mysql_query($query);
	$vistas = array();
	while ($row = mysql_fetch_object($res)) {
		$vistas[] = $row;
	}
	// get points
	$query = sprintf("SELECT * from hike_points where hike_id = '%s'", mysql_real_escape_string($hike->hike_id));
	$points_res=mysql_query($query);
	$points = array();
	while ($row = mysql_fetch_object($points_res)) {
		$points[] = $row;
	}
	$hike->points = $points;
	// grab a random image from the hike (if it exists)
	$hike->sample_photo = "sample_photo.jpg";
	foreach ($vistas as $v){
		if ($v->action_type == 'photo' && file_exists("../ih_plus/uploads/".$v->photo)){
			$hike->sample_photo = $v->photo;
			break;
		}	
	}
	$hike->vistas = $vistas;
    array_push($hikes, $hike);
}

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

//foreach ($hikes as $h){
for ($i=0; $i < sizeof($hikes); $i++){
$h = $hikes[$i];
echo "<table width=\"100%\" border=\"0\" cellspacing=\"4\" cellpadding=\"2\">";
	echo "<tr>";
		echo "<td align=\"left\" valign=\"top\"><a href=\"http://www.ecoarttech.net/ih_plus/web/hike.php?id=".$h->hike_id."\">";
			echo "<div id=\"map_canvas_".$i."\" title=\"".$h->start_lat.",".$h->start_lng."|".vistaLocs($h->vistas)."|".hikePoints($h->points)."\" style=\"width:355px; height:123px; border:1px\"/></td>";
	echo "</tr>";
	echo "<tr>";
		echo "<td align=\"left\" valign=\"top\" class=\"bodystyle\">";
			echo "<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\">";
				echo "<tr>";
					echo "<td width=\"27%\" align=\"left\" valign=\"top\"><a href=\"http://www.ecoarttech.net/ih_plus/web/hike.php?id=".$h->hike_id."\">";
					echo "<img src=\"http://www.ecoarttech.net/ih_plus/uploads/".$h->sample_photo."\" alt=\"hike image\" width=\"120\" height=\"90\" border=\"0\" /></a></td>";
					echo "<td width=\"73%\" align=\"left\" valign=\"top\" class=\"bodystyle\"><strong>".$h->name." -<br />";
					echo "Pioneered by ".$h->username."</strong>.<br />";
					echo $h->description."<br />";
					echo "<a href=\"http://www.ecoarttech.net/ih_plus/web/hike.php?id=".$h->hike_id."\">More&gt;</a></td>";
				echo "</tr>";
			echo "</table>";
		echo "</td>";
	echo "</tr>";
echo "</table>";
echo "<img src=\"images/line_img.jpg\" width=\"355\" height=\"1\" alt=\"line\" /><br />";
}

include '../ih_plus/scripts/db_close.php';
?>
                              </tr>
                          </table>
                            <br />
                            <br />
                            <br />
                            <br />
                          <br /></td>
                          </tr>
                      </table>
                        <table width="747" border="0" cellspacing="2" cellpadding="2">
                        <tr>
                          <td width="66%"><p>&nbsp;</p></td>
                          </tr>
                      </table>
                      <p><br />
                     </p>
                      <table width="100%" border="0" cellspacing="4" cellpadding="2">
                        <tr>
                          <td align="left" valign="top" scope="col"><span class="small"><img src="images/nav_line.gif" alt="" width="840" height="1" /><br />
                           <br />
                              a project by ecoarttech (leila nadir &amp; cary peppermint)<br />
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
                              <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/"><img alt="Creative Commons License" style="border-width:0" src="http://creativecommons.org/images/public/somerights20.png"/><br />
                          </a><br/>
                            This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/">Creative Commons Attribution-Noncommercial-No Derivative Works 3.0 Unported License</a><br />
                          </span></td>
                        </tr>
                      </table></td>
                    </tr>
                  </table>	    </td>
	  </tr>
		</table>

</body>

</html>
