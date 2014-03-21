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
	<body>
		<!-- Header -->
			<section id="header">
				<header>
					<h1>UV Guardian</h1>
					<p>UV Exposure and Vitamin D Absorption Calculations</p>
				</header>
				<footer>
					<a href="#banner" class="button style2 scrolly scrolly-centered">Input a Path</a>
				</footer>
			</section>
		
		<!-- Banner -->
                    <form action="response.jsp" method="POST">
                        
                        <input type="hidden" name="start_lat"/>
                        <input type="hidden" name="start_lng"/>
                        <input type="hidden" name="end_lat"/>
                        <input type="hidden" name="end_lng"/>
                        
			<section id="banner">
				<header>
					<h2>LOCATIONS</h2>
				</header>
                                <p>
                                Start:
                                <input id="start" type="text" name="start" required />
                                <br>
                                Midpoint:
                                <input id="waypoints" type="text" name="waypoints" value=""/>
                                <br>
                                End:
                                <input id="end" type="text" name="end" required />
                                </p>
				<footer>
                                    <!--<a href="webresources/uvgweb" onclick="calcRoute()">Calculate with Defaults</a>-->
                                    <a href="#calculations" onclick="codeAddress()">Calculate with Defaults</a>
                                    <br>
                                    <br>
                              	    <a href="#logistics" class="button style2 scrolly scrolly-centered">Personalize Calculations</a>
				</footer>
			</section>
		
		<!-- Feature 1 -->
			<article id="logistics" class="container box style1 right">
				<a href="#" class="image full"><img src="images/pic01.jpg" alt="" /></a>
				<div class="inner">
					<header>
						<h2>Logistics</h2>
					</header>
                                        <b>Activity Start Time</b>
                                        <br>
                                        <input type="datetime-local" name="start_time" value="2014-03-19T13:00" />
                                        <br>
                                        <b>Mode of Travel</b>
                                        <br>
                                        <select id="mode" name="mode">
                                          <option value="WALKING" selected>Walking</option>
                                          <option value="BICYCLING">Bicycling</option>
                                          <option value="JOGGING">Jogging</option>
                                          <option value="RUNNING">Running</option>
                                        </select>
                                        <br>
                                        <br>
                                
				<footer>
                              	    <a href="#calculations" class="button scrolly-centered" onclick="codeAddress()" style="background: #52527A;">Calculate</a>
				</footer>
                                
				</div>
			</article>
		
		<!-- Feature 2 -->
			<article id="personalizations" class="container box style1 left">
				<a href="#" class="image full"><img src="images/pic02.jpg" alt="" /></a>
				<div class="inner">
					<header>
						<h2>Personalize</h2>
					</header>
                                    
                                        <img src="images/fitzpatrick_skintypes.png" width="100%" alt="fitzpatrick_skintypes"/>
                                        
                                        <b>Skin Type: </b>
                                        <select id="skin-type" name="skin-type" value="SKIN-3">
                                          <option value="SKIN-1">White</option>
                                          <option value="SKIN-2">Beige</option>
                                          <option value="SKIN-3" selected>Light Brown</option>
                                          <option value="SKIN-4">Medium Brown</option>
                                          <option value="SKIN-5">Dark Brown</option>
                                          <option value="SKIN-6">Black</option>
                                        </select>

                                        <br>
                                        <br>
                                        <b>SPF: </b>
                                        <input id="SPF" type="text" name="SPF" value="0" />
                                        </select>
                                        <br>
                                        <br>
                                        <b>Top Clothing: </b>
                                        <br>
                                        <select id="clothes-top" name="clothes-top">
                                          <option value="SLEEVES-LONG" selected>Wearing Long Sleeves</option>
                                          <option value="SLEEVES-SHORT">Wearing Short Sleeves</option>
                                          <option value="SLEEVES-TANK">Wearing Tank Top</option>
                                          <option value="SLEEVES-SPORTSBRA">Wearing Sports Bra</option>
                                          <option value="SLEEVES-NONE">No Shirt</option>
                                        </select>
                                        <br>
                                        <b>Bottom Clothing: </b>
                                        <br>
                                        <select id="clothes-bottom" name="clothes-bottom">
                                          <option value="PANTS-SHORT" selected>Wearing Shorts</option>
                                          <option value="PANTS-LONG">Wearing Pants</option>
                                          <option value="PANTS-NONE">No Pants</option>
                                        </select>
                                        <br>
                                        <b>Accessories: </b>
                                        <br>
                                        <select id="clothes-hat" name="clothes-hat">
                                          <option value="HAT">Wearing Hat</option>
                                          <option value="HAT-NONE" selected>Not Wearing Hat</option>
                                        </select>
                                        <br>
                                        <br>
                                    
                                    <footer>
                                        <a href="#calculations" class="button scrolly-centered" onclick="codeAddress()" style="background: #c99d44;">Calculate</a>
                                    </footer>
                                    
				</div>
			</article>
		
		<!-- Portfolio -->
			<article id="calculations" class="container box style2">
				<header>
					<h2>
                                            <input type="submit" value="SUBMIT">
                                        </h2>
                                        <!--
					<p>
                                            UV Exposure:
                                            <div id="uvcalc"></div>
                                            % Daily Vitamin D Absorbed:
                                            <div id="vitDcalc"></div>
                                        </p>
                                        -->
				</header>
			</article>
                    </form>
		
		<!-- Portfolio - Directions -->
                <div id="directions-panel"></div>
                <!--
			<article id="directions" class="container box style3">
                                <div class="inner">
                                    <div id="directions-panel"></div>
                                </div>
			</article>
                -->
                
		<!-- Contact - Map -->
                <div hidden id="map-canvas"></div>
                <!--
			<article id="map" class="container box style3">
                                <div class="inner">
                                    <div id="map-canvas"></div>
                                </div>
			</article>
                -->
		
		<section id="footer">
			<div class="copyright">
				<ul class="menu">
					<li>&copy; Untitled. All rights reserved.</li>
					<li>Design: <a href="http://html5up.net/">HTML5 UP</a></li>
				</ul>
			</div>
		</section>

                <!--This is how one could submit to the Web Service in this Web Project-->
                <!--
                <form action="webresources/uvgweb" method="GET">
                    Address: <input id="address" type="text" name="address" />

                    First Name: <input type="text" name="first_name">
                    <br />
                    Last Name: <input type="text" name="last_name" />
                    <input type="submit" value="Submit" />
                </form>
                -->
	</body>
</html>