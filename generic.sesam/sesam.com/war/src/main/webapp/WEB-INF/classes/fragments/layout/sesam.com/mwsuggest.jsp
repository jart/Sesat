<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:search="urn:jsptld:/WEB-INF/SearchPortal.tld"><!-- XXX a little awkward since this never exists in the skin -->
<!--
 * Copyright (2007-2012) Schibsted ASA
 *   This file is part of Possom.
 *
 *   Possom is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Possom is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Possom.  If not, see <http://www.gnu.org/licenses/>.
 *
    Version    : $Id$
-->
<!-- MediaWiki Suggest definitions -->
<script type='text/javascript'><jsp:text><![CDATA[
var wgMWSuggestTemplate = "/search/?q=\"{searchTerms}\"&c=suggest";
var wgMWSuggestMessages = ["]]></jsp:text><search:text key="mwsuggest_with_suggestions"/><jsp:text><![CDATA[", "]]></jsp:text><search:text key="mwsuggest_no_suggestions"/><jsp:text><![CDATA["];
// these pairs of inputs/forms will be autoloaded at startup
var os_autoload_inputs = new Array('inputBox');//,'inputBox');
var os_autoload_forms = new Array('sf');//,'sf-bottom');

// search_box_id -> Results object
var os_map = {};
// cached data, url -> json_text
var os_cache = {};
// global variables for suggest_keypress
var os_cur_keypressed = 0;
var os_last_keypress = 0;
var os_keypressed_count = 0;
// type: Timer
var os_timer = null;
// tie mousedown/up events
var os_mouse_pressed = false;
var os_mouse_num = -1;
// if true, the last change was made by mouse (and not keyboard)
var os_mouse_moved = false;
// delay between keypress and suggestion (in ms)
var os_search_timeout = 250;
// if we stopped the service
var os_is_stopped = false;
// max lines to show in suggest table
var os_max_lines_per_suggest = 99;
// number of steps to animate expansion/contraction of container width
var os_animation_steps = 6;
// num of pixels of smallest step
var os_animation_min_step = 2;
// delay between steps (in ms)
var os_animation_delay = 30;
// max width of container in percent of normal size (1 == 100%)
var os_container_max_width = 2;
// currently active animation timer
var os_animation_timer = null;

var resultTableHtmlPrefix = null;
var resultTableHtmlSuffix = null;
var os_suggest_result_padding = 4;
]]></jsp:text></script>
<c:set var="mwsuggestjs_link"><search:findResource url="/javascript/external/mwsuggest.js"/></c:set>
<script type='text/javascript' src="${mwsuggestjs_link}"><![CDATA[&nbsp;]]></script>
<c:set var="mwsuggestcss_link"><search:findResource url="/css/external/mwsuggest.css"/></c:set>
<link rel="stylesheet" type="text/css" href="${mwsuggestcss_link}" media="screen"><![CDATA[&nbsp;]]></link>
</jsp:root>
