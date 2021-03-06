/* Copyright (2012) Schibsted ASA
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
package no.sesat.search.result.handler;

import no.sesat.search.result.handler.AbstractResultHandlerConfig.Controller;
import org.w3c.dom.Element;

/**
 * Configuration for the NewsSearchSetBodyResultHandler ...
 * <p/>
 * Created: Jun 13, 2007 1:14:57 PM
 * Author: Ola MH Sagli <a href="ola@sesam.no">ola at sesam.no</a>
 */
@Controller("NewsSearchSetBodyResultHandler")
public class NewsSearchSetBodyResultHandlerConfig extends AbstractResultHandlerConfig {

    private String source;


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public AbstractResultHandlerConfig readResultHandler(final Element element) {
        source = element.getAttribute("source");
        return this;
    }
}
