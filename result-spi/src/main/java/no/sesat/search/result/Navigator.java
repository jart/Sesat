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

 */
package no.sesat.search.result;

import java.io.Serializable;

/** Configuration within a CommandConfig used to define facets for any facet supporting search command.
 *
 * Each facet contains an id, name, displayName, sort definition, boundaryMatch enabled, and children navigators.
 *
 * @todo rename to Facet
 *
 * @version <tt>$Id$</tt>
 */
public final class Navigator implements Serializable {

    private String id;
    private String name;
    private String field;
    private Navigator childNavigator;
    private String displayName;
    private Sort sort;
    private final boolean boundaryMatch;


    public enum Sort {
        COUNT,
        YEAR,
        MONTH_YEAR(),
        DAY_MONTH_YEAR(),
        DAY_MONTH_YEAR_DESCENDING,
        YEAR_MONTH_DAY_DESCENDING,
        YEAR_MONTH,
        ALPHABETICAL,
        ALPHABETICAL_DESCENDING,
        CUSTOM,
        NONE
    }

    /** Constructor with preset values.
     */
    public Navigator(
            final String name,
            final String field,
            final String displayName,
            final Sort sort,
            final boolean boundaryMatch) {
        this.name = name;
        this.field = field;
        this.displayName = displayName;
        this.sort = sort;
        this.boundaryMatch = boundaryMatch;
    }

    /** Default Constructor. **/
    public Navigator() {
        boundaryMatch = false;
    }

    public Navigator getChildNavigator() {
        return childNavigator;
    }

    public void setChildNavigator(final Navigator childNavigator) {
        this.childNavigator = childNavigator;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getField() {
        return field;
    }

    public void setField(final String field) {
        this.field = field;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get navigator sort by enum.
     *
     * @return sort enum.
     */
    public final Sort getSort() {
        return this.sort;
    }

    /**
     * Getter for property id.
     *
     * @return Value of property id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter for property id.
     *
     * @param id New value of property id.
     */
    public void setId(final String id) {
        this.id = id;
    }

    public boolean isBoundaryMatch() {
        return boundaryMatch;
    }
}
