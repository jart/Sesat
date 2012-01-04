/* Copyright (2005-2012) Schibsted ASA
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
 * SearchConfigurationContext.java
 *
 * Created on 23 February 2006, 14:42
 */

package no.sesat.search.mode.config;

import no.sesat.commons.ioc.BaseContext;

/**
 * @version $Id$
 *
 */
public interface SearchConfigurationContext extends BaseContext {
    /**
     *
     * @return
     */
    SearchConfiguration getSearchConfiguration();
}
