package no.schibstedsok.front.searchportal.velocity;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import java.io.InputStream;
import java.util.Vector;
import no.schibstedsok.front.searchportal.configuration.loader.UrlResourceLoader;
import no.schibstedsok.front.searchportal.site.Site;
import org.apache.log4j.Logger;

import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.exception.ResourceNotFoundException;

import org.apache.commons.collections.ExtendedProperties;

/** XXX This source file needs to be published to the internet as it is open-source code.
 *
 *
 * This is a simple URL-based loader.
 * ORIGINAL FROM http://svn.apache.org/repos/asf/jakarta/velocity/engine/trunk/whiteboard/geir/URLResourceLoader.java
 *
 * original version Id: URLResourceLoader.java,v 1.3 2004/03/19 17:13:40 dlr Exp
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 *
 *
 * MODIFIED TO SUIT SCHIBSTEDSØK's NEEDS.
 * There was a choice here to implement all the URL handling stuff from scratch or to plug into the existing 
 * functionality found in no.schibstedsøk.front.searchportal.configuration.loader
 * Since this class is hidden between the velocity API it made more sense to go from scratch to best 
 * meet velocity's requirements...
 *
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 * @version $Id: URLResourceLoader.java,v 1.3 2004/03/19 17:13:40 dlr Exp $
 */
public final class URLVelocityTemplateLoader extends ResourceLoader {

    private static final Logger LOG = Logger.getLogger(URLVelocityTemplateLoader.class);
    
    private static final String ERR_RESOURCE_NOT_FOUND = "Cannot find resource ";
    
    private Site site;
    private Site fallbackSite;

    /** {@inheritDoc}
     */
    public void init(final ExtendedProperties configuration) {
        // the engine's properties actually come from the RuntimeServices *not* the ExtendedProperties
        site = (Site)rsvc.getProperty(Site.NAME_KEY);
        fallbackSite = (Site)rsvc.getProperty("site.fallback");
    }

    /**
     * Get an InputStream so that the Runtime can build a
     * template with it.
     *
     * @param url  url of template to fetch bytestream of
     * @return InputStream containing the template
     * @throws ResourceNotFoundException if template not found
     *         in the file template path.
     */
    public synchronized InputStream getResourceStream( final String url )
        throws ResourceNotFoundException{

        LOG.trace("start getResourceStream( "+url+" )");
        try{

            final URLConnection conn = getResourceURLConnection(url);
            return conn.getInputStream();

        }catch( IOException e ){
            LOG.debug( ERR_RESOURCE_NOT_FOUND + url);
            throw new ResourceNotFoundException( ERR_RESOURCE_NOT_FOUND + url );
        }
        
        

    }

    /** {@inheritDoc}
     */
    public boolean isSourceModified(Resource resource){

        final boolean result =  getLastModified(resource) > resource.getLastModified();
        LOG.debug("isSourceModified( "+resource.getName()+" ): "+result);
        return result;
    }

    /** {@inheritDoc}
     */
    public long getLastModified(Resource resource){
        
        final String url = resource.getName();
        LOG.trace("start getLastModified( "+url+" )");
        try{
        
            final URLConnection conn = getResourceURLConnection(url);
            return conn.getLastModified();

        }catch( ResourceNotFoundException e ){
            LOG.error( ERR_RESOURCE_NOT_FOUND + url );
        }
        return 0;
        
    }
    
    private URLConnection getResourceURLConnection( final String url )
        throws ResourceNotFoundException{

        URLConnection conn = null;

        try{
            if( UrlResourceLoader.urlExists(url) ){
                final URL u = new URL( UrlResourceLoader.getURL(url) );
                conn = u.openConnection();
                conn.addRequestProperty("host", UrlResourceLoader.getHostHeader(url));

            }else{
                final URL u = new URL( getFallbackURL( url ));
                conn = u.openConnection();
                conn.addRequestProperty("host", UrlResourceLoader.getHostHeader(url));
            }
            
        }catch( IOException e ){

            LOG.error( ERR_RESOURCE_NOT_FOUND + url, e );
            throw new ResourceNotFoundException( ERR_RESOURCE_NOT_FOUND + url );
        }
        return conn;
    }
    
    private String getFallbackURL(final String url){
        final String oldUrl = site.getName() + site.getConfigContext();
        final String newUrl = fallbackSite.getName() + fallbackSite.getConfigContext();
        
        return url.replaceFirst(oldUrl, newUrl);
    }
    
}

