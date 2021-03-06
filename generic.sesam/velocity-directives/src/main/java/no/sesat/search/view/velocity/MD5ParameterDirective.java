/*
 * Copyright (2005-2012) Schibsted ASA
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
 */
package no.sesat.search.view.velocity;

import java.io.IOException;
import java.io.Writer;

import no.sesat.search.security.MD5Generator;
import no.sesat.search.site.config.SiteConfiguration;

import org.apache.log4j.Logger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * This directive hashes a url parameter so you can build url's with protected parameters.
 *
 *
 * @version <tt>$Revision: $</tt>
 */
public final class MD5ParameterDirective extends AbstractDirective {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(MD5ParameterDirective.class);

    /** Name of the directive. */
    private static final String NAME = "md5Parameter";

    /**
     * @return the name of the directive.
     */
    public String getName() {
        return NAME;
    }

    /**
     * @return the type of the directive. The type is LINE.
     */
    public int getType() {
        return LINE;
    }

    /**
     * Renders and returns the hashed text.
     *
     * @param context the directive context
     * @param writer the writer to write to
     * @param node the node to get input from
     * @throws java.io.IOException
     * @throws org.apache.velocity.exception.ResourceNotFoundException
     * @throws org.apache.velocity.exception.ParseErrorException
     * @throws org.apache.velocity.exception.MethodInvocationException
     * @return the hashed parameter
     */
    public boolean render(
            final InternalContextAdapter context,
            final Writer writer,
            final Node node)
            throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        if (node.jjtGetNumChildren() != 1) {
            LOG.error("#" + getName() + " - wrong number of argumants");
            return false;
        }

        SiteConfiguration siteConfig = getDataModel(context).getSite().getSiteConfiguration();
    	final MD5Generator digestGenerator = new MD5Generator(siteConfig.getProperty("md5.secret"));
        final String input = null != node.jjtGetChild(0).value(context)
                ? node.jjtGetChild(0).value(context).toString()
                : "";

        writer.write(digestGenerator.generateMD5(input));

        final Token lastToken = node.getLastToken();
        if (lastToken.image.endsWith("\n")) {
            writer.write("\n");
        }

        return true;
    }

}
