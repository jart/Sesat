<?xml version="1.0" encoding="UTF-8"?>
<jsp:root version="2.0"
        xmlns:jsp="http://java.sun.com/JSP/Page"
        xmlns:c="http://java.sun.com/jsp/jstl/core"
        xmlns:search="urn:jsptld:/WEB-INF/SearchPortal.tld"><!-- XXX a little awkward since this never exists in the skin -->
<!-- 
 * Copyright (2008) Schibsted S�k AS
 *   This file is part of SESAT.
 *
 *   SESAT is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   SESAT is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with SESAT.  If not, see <http://www.gnu.org/licenses/>.
 *
    Author     : mick
    Version    : $Id$
-->
<c:set var="link_action"><search:boomerang url="/search/" param="category:search;subcategory:bottom"/></c:set>
<form name="sf" id="sf" action="${link_action}">
    <c:set var="img_inputButton"><search:findResource url="/images/searchbar/search.gif"/></c:set>
    <input type="text" id="inputBox" name="q" size="60" value="${DataModel.query.xmlEscaped}"/>
    <input type="image" id="inputButton" src="${img_inputButton}"/>
    <div class="clearEl"><jsp:text><![CDATA[&nbsp;]]></jsp:text></div>
</form>
</jsp:root>