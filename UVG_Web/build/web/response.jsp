<%-- 
    Document   : response
    Created on : Mar 10, 2014, 4:22:41 PM
    Author     : Owner
--%>

<%@ page import="navigation.shared.LatLong"
         import="navigation.shared.Routes"
         import="navigation.shared.Person"
         import="navigation.shared.VitaminD"
         import="java.io.IOException"
         import="java.net.URISyntaxException"
         import="navigation.server.FastRTParser"
         import="java.util.Calendar"
         import="java.text.SimpleDateFormat"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%!
    LatLong start_loc = new LatLong();
    String start_lat_str;
    String start_lng_str;
    double start_lat_dou;
    double start_lng_dou;
    LatLong end_loc = new LatLong();
    String end_lat_str;
    String end_lng_str;
    double end_lat_dou;
    double end_lng_dou;

    double sun_dist;
    double sun_dur;
    double shade_dist;
    double shade_dur;
    double assumed_pace;
    double total_dist;

    Calendar cal = Calendar.getInstance();
    String pace_str;
    double pace;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    Routes myRoute = new Routes();
    VitaminD vitD = new VitaminD();
    Person person = new Person();
    
    double bodyExposure;
    double totalEnergyofUVRadiation;

%>

<%
    start_lat_dou = Double.parseDouble(request.getParameter("start_lat"));
    start_lng_dou = Double.parseDouble(request.getParameter("start_lng"));
    end_lat_dou = Double.parseDouble(request.getParameter("end_lat"));
    end_lng_dou = Double.parseDouble(request.getParameter("end_lng"));
    start_loc.setLatitude(start_lat_dou);
    start_loc.setLongitude(start_lng_dou);
    end_loc.setLatitude(end_lat_dou);
    end_loc.setLongitude(end_lng_dou);
    
    cal.setTime(sdf.parse(request.getParameter("start_time")));
    pace_str = request.getParameter("mode");
    if (pace_str.equals("WALKING")) {
        pace = 1.34112; // 1 mile / 20 minutes = 1.34112 meters/second
    } else if (pace_str.equals("JOGGING")) {
        pace = 2.68224; // 1 mile / 10 minutes = 2.68224 meters/second
    } else if (pace_str.equals("BICYCLING")) {
        pace = 4.3; // 15.5 kilometers / 1 hour = 4.3 meters/second
    } else if (pace_str.equals("RUNNING")) {
        pace = 3.83177143; // 1 mile / 7 minutes = 1.34112 meters/second
    }
    myRoute.initialize(start_loc, end_loc, cal, pace);
    
    sun_dur = myRoute.getDurationInSun();
    sun_dist = myRoute.getDistanceInSun();
    shade_dur = myRoute.getDurationInShadow();
    shade_dist = myRoute.getDistanceInShadow();
    assumed_pace = myRoute.getPace();
    total_dist = myRoute.getDistanceInMeters();
    
    person.setSPF(request.getParameter("SPF"));
    person.setSkinType(request.getParameter("skin-type"));
    person.clothesToBodyExposure(request.getParameter("clothes-hat"),
                                 request.getParameter("clothes-top"),
                                 request.getParameter("clothes-bottom"));
    bodyExposure = person.getBodyExposure();
    totalEnergyofUVRadiation = myRoute.computeExposure(bodyExposure);
    try {
        vitD.getVitaminDandSunburnTime(start_loc, person, request.getParameter("start_time"));
    } catch (URISyntaxException uriSE) {
        System.out.println("getVitaminDandSunburnTime: URISyntaxException");
    } catch (IOException ioE) {
        System.out.println("getVitaminDandSunburnTime: IOException");
    }    
%>

<!DOCTYPE HTML>
<!--
	Overflow 1.1 by HTML5 UP
	html5up.net | @n33co
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
-->

<html>
	<head>
		<title>UV Guardian</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<link href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600,300italic" rel="stylesheet" type="text/css" />
		<!--[if lte IE 8]><script src="css/ie/html5shiv.js"></script><![endif]-->
		<script src="js/jquery.min.js"></script>
		<script src="js/jquery.poptrox.min.js"></script>
		<script src="js/skel.min.js"></script>
		<script src="js/init.js"></script>
                <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA1DHX1i07T04TWKwc6x0LfmNURsY6shss&sensor=false"></script>
                <script src="js/GoogleMapsConnect.js"></script>
		<noscript>
			<link rel="stylesheet" href="css/skel-noscript.css" />
			<link rel="stylesheet" href="css/style.css" />
			<link rel="stylesheet" href="css/UVGadded.css" />
		</noscript>
		<!--[if lte IE 8]><link rel="stylesheet" href="css/ie/v8.css" /><![endif]-->
        </head>
	<body onload="calcRoute()">
            <input hidden id="start" value="<%= request.getParameter("start") %>" />
            <input hidden id="end" value="<%= request.getParameter("end") %>" />
            <input hidden id="mode" value="<%= request.getParameter("mode") %>" />
            <input hidden id="waypoints" value="<%= request.getParameter("waypoints") %>" />
		<!-- Header -->
			<section id="header">
                            <div id="response_section_header">
				<header id="response_header">
					<h1>UV Guardian Response Page</h1>
					<p>UV Exposure and Vitamin D Absorption Calculations</p>
				</header>
                            </div>
			</section>
		
		<!-- Portfolio -->
			<article id="calculations" class="container box style2">
				<header>
					<h2>Calculations</h2>
                                        <!--<a href="#calculations" onclick="calcRoute()">Calculate!</a>-->
					<p>
                                            <br>
                                            <strong><u>UV Exposure:</u></strong>
                                            <br>
                                            <br>
                                            <strong>Total Energy of UV Radiation: </strong><%= totalEnergyofUVRadiation %> Joules
                                            <br>
                                            <strong>Distance in Sun: </strong><%= sun_dist %> km
                                            <br>
                                            <strong>Distance in Shade: </strong><%= shade_dist %> km
                                            <br>
                                            <strong>Time in Sun: </strong><%= sun_dur %> sec
                                            <br>
                                            <strong>Time in Shade: </strong><%= shade_dur %> sec
                                            <br>
                                            <strong>Assumed Speed: </strong><%= assumed_pace %> m/sec
                                            <br>
                                            <strong>Total Distance: </strong><%= total_dist %> m
                                            <br>
                                            <br>
                                            <strong><u>Vitamin D Results:</u></strong>
                                            <div id="vitDcalc"></div>
                                            <br>
                                            <strong>Required Time for Sufficient Vitamin D Synthesis: </strong><%= vitD.getVitaminDTime() %> (hh:mm)
                                            <br>
                                            <strong>Time until Sunburnt: </strong><%= vitD.getSunburnTime() %> (hh:mm)
                                            <br>
                                        </p>
				</header>
			</article>
		
		<!-- Portfolio - Directions -->
			<article id="directions" class="container box style3">
                                <div class="inner">
                                    <div id="directions-panel"></div>
                                </div>
			</article>
                
		<!-- Contact - Map -->
			<article id="map" class="container box style3">
                                <div class="inner">
                                    <div id="map-canvas"></div>
                                </div>
			</article>
                
                <!--<section id="header">-->
                    <form action="index.jsp" method="POST">
                        <input type="submit" class="button style2 scrolly scrolly-centered" value="Go Back" />
                    </form>
                <!--</section>-->
		
		<section id="footer">
			<div class="copyright">
				<ul class="menu">
					<li>&copy; Untitled. All rights reserved.</li>
					<li>Design: <a href="http://html5up.net/">HTML5 UP</a></li>
				</ul>
			</div>
		</section>
	</body>
</html>
