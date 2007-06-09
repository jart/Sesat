/* Copyright (2005-2006) Schibsted Søk AS
 *
 * UrlResourceLoader.java
 *
 * Created on 20 January 2006, 10:24
 *
 */

package no.schibstedsok.searchportal.site.config;


import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import no.schibstedsok.searchportal.http.HTTPClient;
import no.schibstedsok.searchportal.site.Site;
import no.schibstedsok.searchportal.site.SiteContext;
import org.apache.log4j.Logger;

/** Loads resources through URL references.
 *
 * @version $Id$
 * @author <a href="mailto:mick@wever.org">Michael Semb Wever</a>
 */
public class UrlResourceLoader extends AbstractResourceLoader {

    // Constants -----------------------------------------------------

    private static final GeneralCacheAdministrator PRESENCE_CACHE = new GeneralCacheAdministrator();
    private static final int REFRESH_PERIOD = 60; // one minute

    private static final Logger LOG = Logger.getLogger(UrlResourceLoader.class);

    private static final String DEBUG_CHECKING_EXISTANCE_OF = "Checking existance of ";


    // Attributes ----------------------------------------------------


    // Static --------------------------------------------------------

    /** Create a new PropertiesLoader for the given resource name/path and load it into the given properties.
     * @param siteCxt the SiteContext that will tell us which site we are dealing with.
     * @param resource the resource name/path.
     * @param properties the properties to hold the individual properties loaded.
     * @return the new PropertiesLoader to use.
     **/
    public static PropertiesLoader newPropertiesLoader(
            final SiteContext siteCxt,
            final String resource,
            final Properties properties) {

        final PropertiesLoader pl = new UrlResourceLoader(siteCxt);
        pl.init(resource, properties);
        return pl;
    }

    /** Create a new DocumentLoader for the given resource name/path and load it with the given DocumentBuilder.
     * @param siteCxt the SiteContext that will tell us which site we are dealing with.
     * @param resource the resource name/path.
     * @param builder the DocumentBuilder to build the DOM resource with.
     * @return the new DocumentLoader to use.
     **/
    public static DocumentLoader newDocumentLoader(
            final SiteContext siteCxt,
            final String resource,
            final DocumentBuilder builder) {

        final DocumentLoader dl = new UrlResourceLoader(siteCxt);
        builder.setEntityResolver(new LocalEntityResolver());
        dl.init(resource, builder);
        return dl;
    }

    /**
     * Creates new BytecodeLoader for the given site and resource.
     *
     * @param siteCxt context telling us which site to use.
     * @param resource the class to load bytecode for.
     * @return a bytecode loader for resource.
     */
    public static BytecodeLoader newBytecodeLoader(final SiteContext siteCxt, final String resource, final String jar) {
        final BytecodeLoader bcLoader = new UrlResourceLoader(siteCxt);
        bcLoader.initBytecodeLoader(resource, jar);
        return bcLoader;
    }

    public static boolean doesUrlExist(final URL url){

        boolean success = false;

        try{
            success = (Boolean)PRESENCE_CACHE.getFromCache(url.toString(), REFRESH_PERIOD);

        }catch(NeedsRefreshException nre){

            boolean updatedCache = false;
            try {

                LOG.trace(DEBUG_CHECKING_EXISTANCE_OF + url + " is " + success);

                success = HTTPClient.instance(url, "localhost").exists("");
                PRESENCE_CACHE.putInCache(url.toString(), success);
                updatedCache = true;

            } catch (NullPointerException e) {
                LOG.debug(url.toString(), e);

            } catch (SocketTimeoutException ste) {
                LOG.debug(url.toString() + '\n' + ste);

            } catch (IOException e) {
                LOG.warn(url.toString(), e);

            }  finally  {
                if(!updatedCache){
                    PRESENCE_CACHE.cancelUpdate(url.toString());
                }
            }
        }

        return success;
    }

    // Constructors --------------------------------------------------

    /**
     */
    protected UrlResourceLoader(final SiteContext cxt) {
        super(cxt);
    }


    // Public --------------------------------------------------------


    @Override
    public final boolean urlExists(final URL url) {

        return doesUrlExist(url);
    }


    // Z implementation ----------------------------------------------

    // Y overrides ---------------------------------------------------

    // Package protected ---------------------------------------------

    // Protected -----------------------------------------------------

    @Override
    protected final URL getResource(final Site site) {
        return getURL(getResource(), site);
    }

    private static String getResourceDirectory(final String resource) {
        if (resource.contains("jar!")) {
            return "lib/";
        } else if (resource.endsWith(".class")) {
            return "classes/";
        } else {
            return "conf/";
        }
    }

    public static URL getURL(final String resource, final Site site) {

        final String jarScheme = resource.contains("jar!") ? "jar:" : "";

        try {
            return new URL(jarScheme + "http://"
                    + site.getName()
                    + site.getConfigContext()
                    + getResourceDirectory(resource)
                    + resource);
        } catch (MalformedURLException ex) {
            throw new ResourceLoadException(ex.getMessage());
        }
    }

    @Override
    protected final InputStream getInputStreamFor(final URL url) {

        HTTPClient client = null;
        try {
            client = HTTPClient.instance(url, "localhost");
            return client.getBufferedStream("");

        }catch (IOException ex) {
            throw new ResourceLoadException(ex.getMessage(), client.interceptIOException(ex));
        }


    }

    public static String getHostHeader(final String resource){
        return resource.substring(7,resource.indexOf('/',8));
    }

    protected final String readResourceDebug(final URL url){

        return "Read Configuration from " + url;
    }

    // Private -------------------------------------------------------

    // Inner classes -------------------------------------------------
}