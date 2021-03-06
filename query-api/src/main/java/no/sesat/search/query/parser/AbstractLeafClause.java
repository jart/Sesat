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
 * AbstractLeafClause.java
 *
 * Created on 7 January 2006, 16:06
 */

package no.sesat.search.query.parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import no.sesat.commons.ref.ReferenceMap;
import no.sesat.search.query.LeafClause;
import no.sesat.search.query.token.EvaluationState;
import no.sesat.search.query.token.TokenEvaluationEngine;
import no.sesat.search.query.token.TokenPredicate;
import org.apache.log4j.Logger;

/**
 * Basic implementation of the LeafClause interface.
 * Implements ontop of AbstractClause.
 * <b>Objects of this class are immutable</b>
 *
 * @version $Id$
 */
public abstract class AbstractLeafClause extends AbstractClause implements LeafClause {

    private static final Logger LOG = Logger.getLogger(AbstractLeafClause.class);

    /**
     * Works off the assumption that LeafClause constructor's have the exact parameter list:
     *       final String term,
     *       final String field,
     *       final Set&lt;Predicate&gt; knownPredicates,
     *       final Set&lt;Predicate&gt; possiblePredicates
     *
     * Where this is true subclasses are free to use this helper method.
     *
     * @param <T> specific type of AbstractLeafClause
     * @param clauseClass the exact subclass of AbstracLeafClause that we are about to create (or find already in use).
     * @param term the term the clause we are about to create (or find) will have.
     * @param field the field the clause we are about to create (or find) will have.
     * @param engine the factory handing out evaluators against TokenPredicates.
     * Also holds state information about the current term/clause we are finding predicates against.
     * @param weakCache the map containing the key to WeakReference (of the Clause) mappings.
     * @return Either a clause already in use that matches this term and field,
     *   or a newly created cluase for this term and field.
     */
    public static <T extends AbstractLeafClause> T createClause(
            final Class<T> clauseClass,
            final String term,
            final String field,
            final TokenEvaluationEngine engine,
            final ReferenceMap<String,T> weakCache) {


        // important that the key argument is unique to this object.
        final String key = field != null
            ? field + ':' + term
            : null != term ? term : "";

        // check weak reference cache of immutable wordClauses here.
        // no need to synchronise, no big lost if duplicate identical objects are created and added over each other
        //  into the cache, compared to the performance lost of trying to synchronise this.
        T clause = findClauseInUse(key, weakCache);

        if (clause == null) {
            try{
                // create predicate sets
                engine.setState(new EvaluationState(key, new HashSet<TokenPredicate>(), new HashSet<TokenPredicate>()));

                // find the applicale predicates now
                final boolean healthy = findPredicates(engine);
                try {
                    // find the constructor...
                    final Constructor<T> constructor = clauseClass.getDeclaredConstructor(
                        String.class, String.class, Set.class, Set.class
                    );
                    // use the constructor...
                    clause = constructor.newInstance(
                        null != term ? term : "",
                        field,
                        engine.getState().getKnownPredicates(),
                        engine.getState().getPossiblePredicates()
                    );

                } catch (SecurityException ex) {
                    LOG.error(ERR_FAILED_FINDING_OR_USING_CONSTRUCTOR + clauseClass.getName(), ex);
                } catch (NoSuchMethodException ex) {
                    LOG.error(ERR_FAILED_FINDING_OR_USING_CONSTRUCTOR + clauseClass.getName(), ex);
                } catch (IllegalArgumentException ex) {
                    LOG.error(ERR_FAILED_FINDING_OR_USING_CONSTRUCTOR + clauseClass.getName(), ex);
                } catch (InstantiationException ex) {
                    LOG.error(ERR_FAILED_FINDING_OR_USING_CONSTRUCTOR + clauseClass.getName(), ex);
                } catch (InvocationTargetException ex) {
                    LOG.error(ERR_FAILED_FINDING_OR_USING_CONSTRUCTOR + clauseClass.getName(), ex);
                } catch (IllegalAccessException ex) {
                    LOG.error(ERR_FAILED_FINDING_OR_USING_CONSTRUCTOR + clauseClass.getName(), ex);
                }

                if(healthy){
                    return addClauseInUse(key, clause, weakCache);
                }
            }finally{
                engine.setState(null);
            }
        }

        return clause;
    }

    /**
     * Create clause with the given term, known and possible predicates.
     * @param term the term (query string) for this clause.
     * @param field
     * @param knownPredicates the set of known predicates for this clause.
     * @param possiblePredicates the set of possible predicates for this clause.
     */
    protected AbstractLeafClause(
            final String term,
            final String field,
            final Set<TokenPredicate> knownPredicates,
            final Set<TokenPredicate> possiblePredicates) {

        super(term, knownPredicates, possiblePredicates);
        this.field = field;
    }


    /** TODO comment me. **/
    protected final String field;


    /**
     * Get the field.
     *
     * @return the field.
     */
    public String getField() {
        return field;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getTerm() + "," + getField() + "]";
     }
}
