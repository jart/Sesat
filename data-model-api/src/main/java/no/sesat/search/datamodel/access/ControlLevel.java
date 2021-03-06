/* Copyright (2007-2012) Schibsted ASA
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
/*
 * ControlLevel.java
 *
 * Created on 23 January 2007, 14:20
 *
 */

package no.sesat.search.datamodel.access;

/**
 *
 *
 * @version <tt>$Id$</tt>
 */
public enum ControlLevel {
    /**
     * Default state the DataModel is created in.
     * Also used when the DataModel is being cleaned at the end of each request.
     */
    DATA_MODEL_CONSTRUCTION,
    /**
     * The state while in http filters or the search servlet.
     */
    REQUEST_CONSTRUCTION,
    /**
     * The state while the RunningQuery is being constructed.
     */
    RUNNING_QUERY_CONSTRUCTION,
    /**
     * The state while the SearchCommand is being constructed.
     */
    SEARCH_COMMAND_CONSTRUCTION,
    /**
     * The state while SearchCommands are being executed.
     */
    SEARCH_COMMAND_EXECUTION,
    /**
     * The state while RunningQuery run handlers are being processed.
     */
    RUNNING_QUERY_HANDLING,
    /**
     * The state while jsps and velocity templates are being executed.
     */
    VIEW_CONSTRUCTION;


//    /**
//     *
//     * @return
//     */
//    public ControlLevel next(){
//
//        switch(this){
//            case DATA_MODEL_CONSTRUCTION:
//                return REQUEST_CONSTRUCTION;
//            case REQUEST_CONSTRUCTION:
//                return RUNNING_QUERY_CONSTRUCTION;
//            case RUNNING_QUERY_CONSTRUCTION:
//                return SEARCH_COMMAND_CONSTRUCTION;
//            case SEARCH_COMMAND_CONSTRUCTION:
//                return SEARCH_COMMAND_QUERY_TRANSFORMATION;
//            case SEARCH_COMMAND_QUERY_TRANSFORMATION:
//                return SEARCH_COMMAND_EXECUTION;
//            case SEARCH_COMMAND_EXECUTION:
//                return SEARCH_COMMAND_RESULT_HANDLING;
//            case SEARCH_COMMAND_RESULT_HANDLING:
//                return RUNNING_QUERY_HANDLING;
//            case RUNNING_QUERY_HANDLING:
//                return VIEW_CONSTRUCTION;
//            case VIEW_CONSTRUCTION:
//                // a new request
//                return REQUEST_CONSTRUCTION;
//
//            default:
//                throw new IllegalStateException("WTF?!");
//        }
//    }
}
