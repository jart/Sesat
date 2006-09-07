/*
 * Copyright (2005-2006) Schibsted Søk AS
 */
package no.schibstedsok.searchportal.query.token;



/**
 *
 * @author <a href="mailto:magnus.eklund@schibsted.no">Magnus Eklund</a>
 * @version <tt>$Revision$</tt>
 */
public interface TokenEvaluator {

    /**
     *
     * Evaluate the <code>token</code> with regards to <code>query</code>.
     * This usually means to check if the token occurs in query, but there are
     * other possibilities such as tokens that always evaluates to true.
     *
     * @param term     the token to look for.
     * @return true if token occurs in query
     */
    boolean evaluateToken(TokenPredicate token, String term, String query);

    boolean isQueryDependant(TokenPredicate predicate);
}
