/* Copyright (2006-2012) Schibsted ASA
 * This file is part of Possom.
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
 * SearchTabMainTag.java
 *
 * Created on May 26, 2006, 3:17 PM
 */
package no.sesat.search.view.taglib;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import no.sesat.search.datamodel.DataModel;
import no.sesat.search.view.config.SearchTab.Layout;

import org.apache.log4j.Logger;


/** Import's the SearchTab's main template from the appropriate layout.
 * Will use the "front" template if the layout defines it and the query is empty.
 *
 * The template may be either a velocity template or a JavaServer page.
 * If the extension is not specified it defaults to ".vm".
 *
 * A relative path is relative to templates/pages/
 *
 *
 * @version $Id$
 */

public final class SearchTabMainTag extends AbstractVelocityTemplateTag {


    // Constants -----------------------------------------------------

    private static final Logger LOG = Logger.getLogger(SearchTabMainTag.class);
    private static final String MISSING = "Missing_SearchTabMain_Template";

    private static final String PAGES_DIRECTORY = "/pages/";
    private static final String RFC1123="EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(RFC1123);


    // Attributes ----------------------------------------------------


    // Static --------------------------------------------------------

    // Constructors --------------------------------------------------

    // Public -------------------------------------------

    /** Called by the container to invoke this tag.
     * The implementation of this method is provided by the tag library developer,
     * and handles all tag processing, body iteration, etc.
     *
     * Calling this tag also has the side effect of setting the layout in use into the context's attributes.
     *
     * @throws javax.servlet.jsp.JspException
     */
    @Override
    public void doTag() throws JspException {

        final PageContext cxt = (PageContext) getJspContext();
        final DataModel datamodel = (DataModel) cxt.findAttribute(DataModel.KEY);
        final Layout layout = findLayout(cxt);

        final String front = (layout!=null && layout.getFront()!=null && layout.getFront().length()>0)?layout.getFront():null;
        final String main = (layout!=null && layout.getMain()!=null && layout.getMain().length()>0)?layout.getMain():null;

        String include = datamodel.getQuery() != null && datamodel.getQuery().getQuery().isBlank() && null != front
                ? front
                : main;

        try{
            if(null != include){

                include = include.startsWith("/")
                        ? include
                        : PAGES_DIRECTORY + include;

                final Map<String,Object> map = new HashMap<String,Object>();
                map.put("layout", layout);

                if(layout.getContentType() != null) {
                    cxt.getResponse().setContentType(layout.getContentType());
                }

                if(layout.getExpiresInSeconds() != -1) {
                    HttpServletResponse httpResponse = (HttpServletResponse)cxt.getResponse();
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, layout.getExpiresInSeconds());
                    httpResponse.setHeader("EXPIRES", dateFormat.format(cal.getTime()));
                }

                if(include.endsWith(".jsp")){

                    forwardJsp(include);

                }else if(include.endsWith(".vm")){

                    importVelocity(include, map);

                }else{
                    // legacy
                    importVelocity(include, map);
                }

            }
            if(null == include
                    || Boolean.TRUE == cxt.getAttribute("Missing_" + include.replaceAll("/","") + "_Template")){

                LOG.error(MISSING);
                cxt.getOut().println(MISSING); // (authorized)
                cxt.setAttribute(MISSING, Boolean.TRUE);
            }
        }catch(IOException ioe){
            throw new JspException(ioe);
        }
    }

    // Package protected ---------------------------------------------

    // Protected -----------------------------------------------------

    // Private -------------------------------------------------------

    // Inner classes -------------------------------------------------
}
