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
 *
 * AbstractQueryParserContext.java
 *
 * Created on 12 January 2006, 12:06
 *
 */

package no.sesat.search.query.parser;

import no.sesat.search.query.AndClause;
import no.sesat.search.query.AndNotClause;
import no.sesat.search.query.Clause;
import no.sesat.search.query.DefaultOperatorClause;
import no.sesat.search.query.EmailClause;
import no.sesat.search.query.IntegerClause;
import no.sesat.search.query.NotClause;
import no.sesat.search.query.OrClause;
import no.sesat.search.query.NumberGroupClause;
import no.sesat.search.query.PhoneNumberClause;
import no.sesat.search.query.PhraseClause;
import no.sesat.search.query.UrlClause;
import no.sesat.search.query.WordClause;
import no.sesat.search.query.XorClause;
import org.apache.log4j.Logger;

/** Default implementation of QueryParser.Context's createXxxClause methods.
 *
 * @version $Id$
 *
 */
public abstract class AbstractQueryParserContext implements AbstractQueryParser.Context {

    private static final Logger LOG = Logger.getLogger(AbstractQueryParserContext.class);

    /** Creates a new instance of AbstractQueryParserContext.
     */
    public AbstractQueryParserContext() {
    }

    /** {@inheritDoc}
     */
    public final String getQueryString() {
        return getTokenEvaluationEngine().getQueryString();
    }


    //// Operator creators
    /** {@inheritDoc}
     */
    public DefaultOperatorClause createDefaultOperatorClause(final Clause first, final Clause second){

        LOG.debug("createDefaultOperatorClause(" + first + "," + second + ")");
        return DefaultOperatorClauseImpl.createDefaultOperatorClause(first, second, getTokenEvaluationEngine());
    }

    /** {@inheritDoc}
     */
    public final AndClause createAndClause(
        final Clause first,
        final Clause second) {

        LOG.debug("createAndClause(" + first + "," + second + ")");
        return AndClauseImpl.createAndClause(first, second, getTokenEvaluationEngine());
    }

    /** {@inheritDoc}
     */
    public final OrClause createOrClause(
        final Clause first,
        final Clause second) {

        LOG.debug("createOrClause(" + first + "," + second + ")");
        return OrClauseImpl.createOrClause(first, second, getTokenEvaluationEngine());
    }

    /** {@inheritDoc}
     */
    public final XorClause createXorClause(
        final Clause first,
        final Clause second,
        final XorClause.Hint hint) {

        LOG.debug("createXorClause(" + first + "," + second + "," + hint + ")");
        return XorClauseImpl.createXorClause(first, second, hint, getTokenEvaluationEngine());
    }

    /** {@inheritDoc}
     */
    public final AndNotClause createAndNotClause(
        final Clause first) {

        LOG.debug("createAndNotClause(" + first + ")");
        return AndNotClauseImpl.createAndNotClause(first, getTokenEvaluationEngine());
    }

    /** {@inheritDoc}
     */
    public final NotClause createNotClause(
        final Clause first) {

        LOG.debug("createNotClause(" + first + ")");
        return NotClauseImpl.createNotClause(first, getTokenEvaluationEngine());
    }


    //// Leaf creators

    /** {@inheritDoc}
     */
    public final WordClause createWordClause(
        final String term,
        final String field) {

        LOG.debug("createWordClause(" + term + "," + field + ")");
        return WordClauseImpl.createWordClause(term, field, getTokenEvaluationEngine());
    }

    /** {@inheritDoc}
     */
    public final PhraseClause createPhraseClause(
        final String term,
        final String field) {

        LOG.debug("createPhraseClause(" + term + "," + field + ")");
        return PhraseClauseImpl.createPhraseClause(term, field, getTokenEvaluationEngine());
    }

    /** {@inheritDoc}
     */
    public final IntegerClause createIntegerClause(
        final String term,
        final String field) {

        LOG.debug("createIntegerClause(" + term + "," + field + ")");
        return IntegerClauseImpl.createIntegerClause(term, field, getTokenEvaluationEngine());
    }

    /** {@inheritDoc}
     */
    public final PhoneNumberClause createPhoneNumberClause(
        final String term,
        final String field) {

        LOG.debug("createPhoneNumberClause(" + term + "," + field + ")");
        return PhoneNumberClauseImpl.createPhoneNumberClause(term, field, getTokenEvaluationEngine());
    }

    /** {@inheritDoc}
     */
    public final NumberGroupClause createNumberGroupClause(
        final String term,
        final String field) {

        LOG.debug("createNumberGroupClause(" + term + "," + field + ")");
        return NumberGroupClauseImpl.createNumberGroupClause(term, field, getTokenEvaluationEngine());
    }

    /** {@inheritDoc}
     */
    public final UrlClause createUrlClause(final String term, final String field){

        LOG.debug("createUrlClause(" + term + "," + field + ")");
        return UrlClauseImpl.createUrlClause(term, field, getTokenEvaluationEngine());
    }
    /** {@inheritDoc}
     */
    public final EmailClause createEmailClause(final String term, final String field){

        LOG.debug("createEmailClause(" + term + "," + field + ")");
        return EmailClauseImpl.createEmailClause(term, field, getTokenEvaluationEngine());
    }
}
