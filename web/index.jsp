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
                <script
                    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA1DHX1i07T04TWKwc6x0LfmNURsY6shss&sensor=false">
                </script>
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
			<section id="banner">
				<header>
					<h2>LOCATIONS</h2>
				</header>
                                    <p>
                                    Start:
                                    <input id="start" type="text" name="start" />
                                    <br>
                                    Midpoint:
                                    <input id="waypoints" type="text" name="waypoints" />
                                    <br>
                                    End:
                                    <input id="end" type="text" name="end" />
                                    </p>
				<footer>
                                    <!--<a href="#logistics" class="button style2 scrolly">Calculate with Defaults</a>-->
                                    <a href="#calculations" onclick="calcRoute()">Calculate with Defaults</a>
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
                                        <i>Use format (mm/dd hh:mm)</i>
                                        <br>
                                        <input id="start_time" type="text" name="start_time" />
                                        <br>
                                        OR
                                        <br>
                                        <select id="start_time" onchange="calcRoute();">
                                          <option value="3 AM">3 AM</option>
                                          <option value="6 AM">6 AM</option>
                                          <option value="9 AM">9 AM</option>
                                          <option value="12 NOON">12 NOON</option>
                                          <option value="3 PM">3 PM</option>
                                          <option value="6 PM">6 PM</option>
                                          <option value="9 PM">9 PM</option>
                                          <option value="12 MIDNIGHT">12 MIDNIGHT</option>
                                        </select>
                                        <br>
                                        <br>
                                        <b>Mode of Travel</b>
                                        <br>
                                        <select id="mode" onchange="calcRoute();">
                                          <option value="WALKING">Walking</option>
                                          <option value="BICYCLING">Bicycling</option>
                                          <option value="JOGGING">Jogging</option>
                                          <option value="RUNNING">Running</option>
                                        </select>
                                        <br>
                                        <br>
				<footer>
                              	    <a href="#calculations" class="button scrolly-centered" onclick="calcRoute()" style="background: #52527A;">Calculate</a>
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
                                        <select id="skin-type" onchange="calcRoute();">
                                          <option value="SKIN-1">White</option>
                                          <option value="SKIN-2">Beige</option>
                                          <option value="SKIN-3">Light Brown</option>
                                          <option value="SKIN-4">Medium Brown</option>
                                          <option value="SKIN-5">Dark Brown</option>
                                          <option value="SKIN-6">Black</option>
                                        </select>

                                        <br>
                                        <br>
                                        <b>SPF: </b>
                                        <input id="SPF" type="text" name="SPF" />
                                        <!--<select id="SPF" onchange="calcRoute();">
                                          <option value="SUNSCREEN15">Sunscreen SPF 15</option>
                                          <option value="SUNSCREEN30">Sunscreen SPF 30</option>
                                          <option value="SUNSCREEN45">Sunscreen SPF 45</option>
                                          <option value="SUNSCREEN50">Sunscreen SPF 50</option>
                                          <option value="SUNSCREEN80">Sunscreen SPF 80</option>
                                        </select>-->
                                        <br>
                                        <br>
                                        <b>Top Clothing: </b>
                                        <br>
                                        <select id="clothes-top" onchange="calcRoute();">
                                          <option value="SLEEVES-LONG">Wearing Long Sleeves</option>
                                          <option value="SLEEVES-SHORT">Wearing Short Sleeves</option>
                                          <option value="SLEEVES-TANK">Wearing Tank Top</option>
                                          <option value="SLEEVES-NONE">No Shirt</option>
                                        </select>
                                        <br>
                                        <b>Bottom Clothing: </b>
                                        <br>
                                        <select id="clothes-bottom" onchange="calcRoute();">
                                          <option value="PANTS-SHORT">Wearing Shorts</option>
                                          <option value="PANTS-LONG">Wearing Pants</option>
                                          <option value="PANTS-NONE">No Pants</option>
                                        </select>
                                        <br>
                                        <b>Accessories: </b>
                                        <br>
                                        <select id="clothes-hat" onchange="calcRoute();">
                                          <option value="HAT">Wearing Hat</option>
                                          <option value="HAT-NONE">Not Wearing Hat</option>
                                        </select>
                                        <br>
                                        <br>
                                    <footer>
                                        <a href="#calculations" class="button scrolly-centered" onclick="calcRoute()" style="background: #c99d44;">Calculate</a>
                                    </footer>
				</div>
			</article>
		
		<!-- Portfolio -->
			<article id="calculations" class="container box style2">
				<header>
					<h2>Calculations</h2>
					<p>
                                            UV Exposure:
                                            <div id="uvcalc"></div>
                                            <br>
                                            % Daily Vitamin D Absorbed:
                                            <div id="vitDcalc"></div>
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
		
		<!-- Generic -->
		<!--
			<article class="container box style3">
				<header>
					<h2>Generic Box</h2>
					<p>Just a generic box. Nothing to see here.</p>
				</header>
				<section>
					<header>
						<h3>Paragraph</h3>
						<p>This is a byline</p>
					</header>
					<p>Phasellus nisl nisl, varius id <sup>porttitor sed pellentesque</sup> ac orci. Pellentesque 
					habitant <strong>strong</strong> tristique <b>bold</b> et netus <i>italic</i> malesuada <em>emphasized</em> ac turpis egestas. Morbi 
					leo suscipit ut. Praesent <sub>id turpis vitae</sub> turpis pretium ultricies. Vestibulum sit 
					amet risus elit.</p>
				</section>
				<section>
					<header>
						<h3>Blockquote</h3>
					</header>
					<blockquote>Fringilla nisl. Donec accumsan interdum nisi, quis tincidunt felis sagittis eget.
					tempus euismod. Vestibulum ante ipsum primis in faucibus.</blockquote>
				</section>
				<section>
					<header>
						<h3>Divider</h3>
					</header>
					<p>Donec consectetur <a href="#">vestibulum dolor et pulvinar</a>. Etiam vel felis enim, at viverra 
					ligula. Ut porttitor sagittis lorem, quis eleifend nisi ornare vel. Praesent nec orci 
					facilisis leo magna. Cras sit amet urna eros, id egestas urna. Quisque aliquam 
					tempus euismod. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices 
					posuere cubilia.</p>
					<hr />
					<p>Donec consectetur vestibulum dolor et pulvinar. Etiam vel felis enim, at viverra 
					ligula. Ut porttitor sagittis lorem, quis eleifend nisi ornare vel. Praesent nec orci 
					facilisis leo magna. Cras sit amet urna eros, id egestas urna. Quisque aliquam 
					tempus euismod. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices 
					posuere cubilia.</p>
				</section>
				<section>
					<header>
						<h3>Unordered List</h3>
					</header>
					<ul class="default">
						<li>Donec consectetur vestibulum dolor et pulvinar. Etiam vel felis enim, at viverra ligula. Ut porttitor sagittis lorem, quis eleifend nisi ornare vel.</li>
						<li>Donec consectetur vestibulum dolor et pulvinar. Etiam vel felis enim, at viverra ligula. Ut porttitor sagittis lorem, quis eleifend nisi ornare vel.</li>
						<li>Donec consectetur vestibulum dolor et pulvinar. Etiam vel felis enim, at viverra ligula. Ut porttitor sagittis lorem, quis eleifend nisi ornare vel.</li>
						<li>Donec consectetur vestibulum dolor et pulvinar. Etiam vel felis enim, at viverra ligula. Ut porttitor sagittis lorem, quis eleifend nisi ornare vel.</li>
					</ul>
				</section>
				<section>
					<header>
						<h3>Ordered List</h3>
					</header>
					<ol class="default">
						<li>Donec consectetur vestibulum dolor et pulvinar. Etiam vel felis enim, at viverra ligula. Ut porttitor sagittis lorem, quis eleifend nisi ornare vel.</li>
						<li>Donec consectetur vestibulum dolor et pulvinar. Etiam vel felis enim, at viverra ligula. Ut porttitor sagittis lorem, quis eleifend nisi ornare vel.</li>
						<li>Donec consectetur vestibulum dolor et pulvinar. Etiam vel felis enim, at viverra ligula. Ut porttitor sagittis lorem, quis eleifend nisi ornare vel.</li>
						<li>Donec consectetur vestibulum dolor et pulvinar. Etiam vel felis enim, at viverra ligula. Ut porttitor sagittis lorem, quis eleifend nisi ornare vel.</li>
					</ol>
				</section>
				<section>
					<header>
						<h3>Table</h3>
					</header>
					<div class="table-wrapper">
						<table class="default">
							<thead>
								<tr>
									<th>ID</th>
									<th>Name</th>
									<th>Description</th>
									<th>Price</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>45815</td>
									<td>Something</td>
									<td>Ut porttitor sagittis lorem quis nisi ornare.</td>
									<td>29.99</td>
								</tr>
								<tr>
									<td>24524</td>
									<td>Nothing</td>
									<td>Ut porttitor sagittis lorem quis nisi ornare.</td>
									<td>19.99</td>
								</tr>
								<tr>
									<td>45815</td>
									<td>Something</td>
									<td>Ut porttitor sagittis lorem quis nisi ornare.</td>
									<td>29.99</td>
								</tr>
								<tr>
									<td>24524</td>
									<td>Nothing</td>
									<td>Ut porttitor sagittis lorem quis nisi ornare.</td>
									<td>19.99</td>
								</tr>
							</tbody>
							<tfoot>
								<tr>
									<td colspan="3"></td>
									<td>100.00</td>
								</tr>
							</tfoot>
						</table>
					</div>
				</section>
				<section>
					<header>
						<h3>Form</h3>
					</header>
					<form method="post" action="#">
						<div class="row">
							<div class="6u">
								<input class="text" type="text" name="name" id="name" value="" placeholder="John Doe" />
							</div>
							<div class="6u">
								<input class="text" type="text" name="email" id="email" value="" placeholder="johndoe@domain.tld" />
							</div>
						</div>
						<div class="row">
							<div class="12u">
								<select name="department" id="department">
									<option value="">Choose a department</option>
									<option value="1">Manufacturing</option>
									<option value="2">Administration</option>
									<option value="3">Support</option>
								</select>
							</div>
						</div>
						<div class="row">
							<div class="12u">
								<input class="text" type="text" name="subject" id="subject" value="" placeholder="Enter your subject" />
							</div>
						</div>
						<div class="row">
							<div class="12u">
								<textarea name="message" id="message" placeholder="Enter your message"></textarea>
							</div>
						</div>
						<div class="row">
							<div class="12u">
								<ul class="actions">
									<li><a href="#" class="button form">Submit</a></li>
									<li><a href="#" class="button style3 form-reset">Clear Form</a></li>
								</ul>
							</div>
						</div>
					</form>
				</section>
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

	</body>
</html>