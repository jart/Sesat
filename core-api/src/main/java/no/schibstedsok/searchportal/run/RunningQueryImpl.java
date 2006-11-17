/*
 * Copyright (2005-2006) Schibsted Søk AS
 *
 */
package no.schibstedsok.searchportal.run;


import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import no.schibstedsok.common.ioc.BaseContext;
import no.schibstedsok.common.ioc.ContextWrapper;
import no.schibstedsok.searchportal.query.analyser.AnalysisRule;
import no.schibstedsok.searchportal.query.analyser.AnalysisRuleFactory;
import no.schibstedsok.searchportal.query.QueryStringContext;
import no.schibstedsok.searchportal.query.parser.QueryParserImpl;
import no.schibstedsok.searchportal.query.token.ReportingTokenEvaluator;
import no.schibstedsok.searchportal.query.token.TokenEvaluationEngine;
import no.schibstedsok.searchportal.query.token.TokenEvaluationEngineImpl;
import no.schibstedsok.searchportal.query.token.TokenMatch;
import no.schibstedsok.searchportal.query.token.TokenPredicate;
import no.schibstedsok.searchportal.mode.command.SearchCommand;
import no.schibstedsok.searchportal.mode.SearchCommandFactory;
import no.schibstedsok.searchportal.mode.config.SearchConfiguration;
import no.schibstedsok.searchportal.mode.config.SearchMode;
import no.schibstedsok.searchportal.query.parser.AbstractQueryParserContext;
import no.schibstedsok.searchportal.query.Query;
import no.schibstedsok.searchportal.query.parser.QueryParser;
import no.schibstedsok.searchportal.query.token.VeryFastListQueryException;
import no.schibstedsok.searchportal.result.Enrichment;
import no.schibstedsok.searchportal.result.Modifier;
import no.schibstedsok.searchportal.result.SearchResult;
import no.schibstedsok.searchportal.result.handler.ResultHandler;
import no.schibstedsok.searchportal.view.config.SearchTab;
import no.schibstedsok.searchportal.view.output.VelocityResultHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * An object representing a running queryStr.
 *
 *
 * @author <a href="mailto:magnus.eklund@schibsted.no">Magnus Eklund</a>
 * @version <tt>$Revision$</tt>
 */
public class RunningQueryImpl extends AbstractRunningQuery implements RunningQuery {

    private static final Logger LOG = Logger.getLogger(RunningQueryImpl.class);
    private static final Logger ANALYSIS_LOG = Logger.getLogger("no.schibstedsok.searchportal.analyzer.Analysis");
    private static final Logger PRODUCT_LOG = Logger.getLogger("no.schibstedsok.Product");

    private static final String ERR_PARSING = "Unable to create RunningQuery's query due to ParseException";
    private static final String ERR_RUN_QUERY = "Failure to run query";
    private static final String ERR_EXECUTION_ERROR = "Failure on a search command: ";
    private static final String INFO_COMMAND_COUNT = "Commands to invoke ";

    private final AnalysisRuleFactory rules;
    private final String queryStr;
    private final Query queryObj;
    
    /** Map of search command IDs to SearchResult */
    private final Map<String, Future<SearchResult>> results = new HashMap<String,Future<SearchResult>>();
    
    /** Mutext for results variable */
    private final ReadWriteLock resultsLock = new ReentrantReadWriteLock();
    
    /** have all search commands been cancelled.
     * implementation details allowing web subclasses to send correct error to client. **/
    protected boolean allCancelled = false;
    /** TODO comment me. **/
    protected final Map<String,Object> parameters;
    private final Locale locale = new Locale("no", "NO");
    private final List<Modifier> sources = new Vector<Modifier>();
    /** TODO comment me. **/
    protected final TokenEvaluationEngine engine;
    private final List<Enrichment> enrichments = new ArrayList<Enrichment>();
    private final Map<String,Integer> hits = new HashMap<String,Integer>();
    private final Map<String,Integer> scores = new HashMap<String,Integer>();
    private final Map<String,Integer> scoresByRule = new HashMap<String,Integer>();
    private static final String PARAM_OUTPUT = "output";

    /**
     * Create a new Running Query instance.
     *
     * @param mode
     * @param queryStr
     * @param parameters
     */
    public RunningQueryImpl(final Context cxt, final String query, final Map parameters) {

        super(cxt);

        LOG.trace("RunningQuery(cxt," + query + "," + parameters + ")");

        queryStr = trimDuplicateSpaces(query);

        this.parameters = parameters;

        final TokenEvaluationEngine.Context tokenEvalFactoryCxt =
                ContextWrapper.wrap(
                    TokenEvaluationEngine.Context.class,
                    context,
                    new QueryStringContext() {
                        public String getQueryString() {
                            return RunningQueryImpl.this.getQueryString();
                        }
                    });

        // This will among other things perform the initial fast search
        // for textual analysis.
        engine = new TokenEvaluationEngineImpl(tokenEvalFactoryCxt);

        // queryStr parser
        final QueryParser parser = new QueryParserImpl(new AbstractQueryParserContext() {
            public TokenEvaluationEngine getTokenEvaluationEngine() {
                return engine;
            }
        });

        queryObj = parser.getQuery();

        rules = AnalysisRuleFactory.valueOf(ContextWrapper.wrap(AnalysisRuleFactory.Context.class, context));

    }

    private List<TokenMatch> getTokenMatches(final TokenPredicate token) throws VeryFastListQueryException {

        final ReportingTokenEvaluator e = (ReportingTokenEvaluator) engine.getEvaluator(token);
        return e.reportToken(token, queryStr);
    }

    /** TODO comment me. **/
    public List<TokenMatch> getGeographicMatches() {


        final List<TokenMatch> matches = new ArrayList<TokenMatch>();

        try{

            matches.addAll(getTokenMatches(TokenPredicate.GEOLOCAL));
            matches.addAll(getTokenMatches(TokenPredicate.GEOGLOBAL));

        }catch(VeryFastListQueryException ie){
            LOG.error("Magnus: fix SEARCH-943   :-)");
        }
        Collections.sort(matches);

        return matches;
    }

    /**
     * First find out if the user types in an advanced search etc by analyzing the queryStr.
     * Then lookup correct tip using message resources.
     *
     * @return user tip
     */
    public String getGlobalSearchTips () {

        LOG.trace("getGlobalSearchTips()");
        return null;
    }


    /** TODO comment me. **/
    public Integer getNumberOfHits(final String configName) {

        LOG.trace("getNumberOfHits(" + configName + ")");
        return hits.get(configName) != null ? hits.get(configName) : Integer.valueOf(0);
    }

    /** TODO comment me. **/
    public Map<String,Integer> getHits(){
        return Collections.unmodifiableMap(hits);
    }

    /** {@inheritDoc} */
    public final SearchResult getSearchResult(final String id) throws InterruptedException, ExecutionException {
        final Future<SearchResult> task;
        resultsLock.readLock().lock();
        try {
            if (!results.containsKey(id)) {
                return null;
            }
            task = results.get(id);
        } finally {
            resultsLock.readLock().unlock();
        }
        
        return task.get();
    }
    
    /**
     * Thread run. Guts of the logic behind this class.
     *
     * @throws InterruptedException
     */
    public void run() throws InterruptedException {

        LOG.trace("run()");
        ANALYSIS_LOG.info("<analyse><query>" + queryStr + "</query>");

        try {

            final Collection<Callable<SearchResult>> commands = new ArrayList<Callable<SearchResult>>();

            final boolean isRss = parameters.get(PARAM_OUTPUT) != null && parameters.get(PARAM_OUTPUT).equals("rss");

            for (SearchConfiguration searchConfiguration : context.getSearchMode().getSearchConfigurations()) {

                final SearchConfiguration config = searchConfiguration;
                final String configName = config.getName();

                // If output is rss, only run the one command that will produce the rss output.
                if (!isRss || context.getSearchTab().getRssResultName().equals(configName)) {

                    hits.put(config.getName(), Integer.valueOf(0));

                    final SearchCommand.Context searchCmdCxt = ContextWrapper.wrap(
                            SearchCommand.Context.class,
                            context,
                            new BaseContext() {
                                public SearchConfiguration getSearchConfiguration() {
                                    return config;
                                }

                                public RunningQuery getRunningQuery() {
                                    return RunningQueryImpl.this;
                                }

                                public Query getQuery() {
                                    return queryObj;
                                }

                                public TokenEvaluationEngine getTokenEvaluationEngine() {
                                    return engine;
                                }
                            }
                    );

                    final SearchTab.EnrichmentHint eHint = context.getSearchTab().getEnrichmentByCommand(configName);

                    if (eHint != null && !queryObj.isBlank()) {

                        final AnalysisRule rule = rules.getRule(eHint.getRule());

                        if (context.getSearchMode().isAnalysis()
                                && "0".equals(parameters.get("offset"))
                                && eHint.getWeight() > 0) {

                            int score = 0;

                            if (scoresByRule.get(eHint.getRule()) == null) {

                                ANALYSIS_LOG.info(" <analysis name=\"" + eHint.getRule() + "\">");

                                score = rule.evaluate(queryObj, engine);
                                scoresByRule.put(eHint.getRule(), score);

                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Score for " + searchConfiguration.getName() + " is " + score);
                                }

                                if (score != 0) {
                                    ANALYSIS_LOG.info("  <score>" + score + "</score>");
                                }

                                ANALYSIS_LOG.info(" </analysis>");

                            } else {
                                score = scoresByRule.get(eHint.getRule());
                            }

                            scores.put(config.getName(), score);

                            if (config.isAlwaysRun() || score >= eHint.getThreshold()) {
                                commands.add(SearchCommandFactory.createSearchCommand(searchCmdCxt, parameters));
                            }

                        } else if (config.isAlwaysRun()) {
                            commands.add(SearchCommandFactory.createSearchCommand(searchCmdCxt, parameters));
                        }

                    } else {

                        commands.add(SearchCommandFactory.createSearchCommand(searchCmdCxt, parameters));
                    }
                }
            }
            ANALYSIS_LOG.info("</analyse>");

            LOG.info(INFO_COMMAND_COUNT + commands.size());

            // mark state that we're about to execute the sub threads
            allCancelled = true;

            final int timeout = Logger.getRootLogger().getLevel().isGreaterOrEqual(Level.INFO) ? 10000 : Integer.MAX_VALUE;
            
            /* Entering CS */
            resultsLock.writeLock().lock();
            try {
                context.getSearchMode().getExecutor().invokeAll(commands, results, timeout);
            } finally {
                /* Leaving CS */
                resultsLock.writeLock().unlock();
            }
            
            // TODO This loop-(task.isDone()) code should become individual listeners to each executor to minimise time
            //  spent in task.isDone()
            boolean hitsToShow = false;

            /* Give the commands a chance to finish its work */
            for (Future<SearchResult> task : results.values()) {
                task.get(timeout, TimeUnit.MILLISECONDS);
            }
            
            // Ensure any cancellations are properly handled
            for(Callable<SearchResult> command : commands){
                allCancelled &= ((SearchCommand)command).handleCancellation();
            }

            if( !allCancelled ){

                for (Future<SearchResult> task : results.values()) {

                    if (task.isDone() && !task.isCancelled()) {

                        try{
                            final SearchResult searchResult = task.get();
                            if (searchResult != null) {

                                // Information we need about and for the enrichment
                                final SearchConfiguration config
                                        = searchResult.getSearchCommand().getSearchConfiguration();

                                final String name = config.getName();
                                final SearchTab.EnrichmentHint eHint
                                        = context.getSearchTab().getEnrichmentByCommand(name);

                                final float score = scores.get(name) != null
                                        ? scores.get(name) * eHint.getWeight()
                                        : 0;

                                // update hit status
                                hitsToShow |= searchResult.getHitCount() > 0;
                                hits.put(name, searchResult.getHitCount());

                                // score
                                if(eHint != null && searchResult.getHitCount() > 0 && score >= eHint.getThreshold()) {

                                    // add enrichment
                                    final Enrichment e = new Enrichment(score, name);
                                    enrichments.add(e);
                                }
                            }
                        }catch(ExecutionException ee){
                            LOG.error(ERR_EXECUTION_ERROR, ee);
                        }
                    }
                }

                performModifierHandling();

                if (!hitsToShow) {
                    PRODUCT_LOG.info("<no-hits mode=\"" + context.getSearchTab().getKey() + "\">"
                            + "<query>" + queryStr + "</query></no-hits>");
    // FIXME: i do not know how to reset/clean the sitemesh's outputStream so
    //                  the result from the new RunningQuery are used.
    //                int sourceHits = 0;
    //                for (final Iterator it = sources.iterator(); it.hasNext();) {
    //                    sourceHits += ((Modifier) it.next()).getCount();
    //                }
    //                if (sourceHits == 0) {
    //                    // there were no hits for any of the search tabs!
    //                    // maybe we can modify the query to broaden the search
    //                    // replace all DefaultClause with an OrClause
    //                    //  [simply done with wrapping the query string inside ()'s ]
    //                    if (!queryStr.startsWith("(") && !queryStr.endsWith(")") && queryObj.getTermCount() > 1) {
    //                        // create and run a new RunningQueryImpl
    //                        new RunningQueryImpl(context, '(' + queryStr + ')', parameters).run();
    //                    }
    //                }
                }  else  {

                    performEnrichmentHandling(results.values());
                }
            }
        } catch (Exception e) {
            LOG.error(ERR_RUN_QUERY, e);
        }
    }

    private void performEnrichmentHandling(final Collection<Future<SearchResult>> results) throws InterruptedException, ExecutionException {

        Collections.sort(enrichments);
        
        final StringBuilder log = new StringBuilder();

        log.append("<enrichments mode=\"" + context.getSearchTab().getKey()
                + "\" size=\"" + enrichments.size() + "\">"
                + "<query>" + queryStr + "</query>");

        Enrichment tvEnrich = null;
        Enrichment webtvEnrich = null;

        /* Write product log and find webtv and tv enrichments */
        for(Enrichment e : enrichments){
            log.append("<enrichment name=\"" + e.getName()
                    + "\" score=\"" + e.getAnalysisResult() + "\"/>");

            /* Store reference to webtv and tv enrichments */
            if ("webtvEnrich".equals(e.getName())) {
                webtvEnrich = e;
            } else if ("tvEnrich".equals(e.getName())) {
                tvEnrich = e;
            }
        }
        log.append("</enrichments>");
        PRODUCT_LOG.info(log.toString());


        /* Update score and if necessary the enrichment name */
        if (webtvEnrich != null && tvEnrich != null) {
            if (webtvEnrich.getAnalysisResult() > tvEnrich.getAnalysisResult()) {
                tvEnrich.setAnalysisResult(webtvEnrich.getAnalysisResult());
            }
            enrichments.remove(webtvEnrich);
        } else if (webtvEnrich != null && tvEnrich == null) {
            tvEnrich = webtvEnrich;
            webtvEnrich.setName("tvEnrich");
        }

        if (tvEnrich != null) {
            SearchResult tvResult = null;
            SearchResult webtvResult = null;

            /* Find webtv and tv results */
            for (Future<SearchResult> task : results) {
                if (task.isDone() && !task.isCancelled()) {
                    final SearchResult sr = task.get();
                    if ("webtvEnrich".equals(sr.getSearchCommand().getSearchConfiguration().getName())) {
                        webtvResult = sr;
                    } else if ("tvEnrich".equals(sr.getSearchCommand().getSearchConfiguration().getName())) {
                        tvResult = sr;
                    }
                }
            }

            /* Merge webtv results into tv results */
            if (webtvResult != null && webtvResult.getResults().size() > 0) {
                if (tvResult != null) {
                    /* If tv results exists we only want the two first results from webtv. */
                    if (tvResult.getResults().size() > 0 && webtvResult.getResults().size() > 2) {
                        webtvResult.getResults().remove(2);
                    }
                    tvResult.getResults().addAll(webtvResult.getResults());
                    tvResult.setHitCount(tvResult.getHitCount() + webtvResult.getHitCount());
                }
            }
        }
    }

    private void performModifierHandling(){

        final List<Modifier> toRemove = new ArrayList<Modifier>();
        for(Modifier m : sources){
            if(m.getCount() > -1 ){
                m.setNavigationHint(context.getSearchTab().getNavigationHint(m.getName()));
            }else{
                toRemove.add(m);
            }
        }
        sources.removeAll(toRemove);

        if (getSearchTab().getAbsoluteOrdering()) {
            Collections.sort(sources, Modifier.getHintPriorityComparator());
        } else {
            Collections.sort(sources);
        }
    }

    private String getSingleParameter(final String paramName) {

        LOG.trace("getSingleParameter()");

        final String[] param = (String[]) parameters.get(paramName);

        return (param != null) ? param[0] : null;
    }

    private boolean isInternational(final SearchConfiguration searchConfiguration) {
        return "globalSearch".equals(searchConfiguration.getName());
    }

    private boolean isNorwegian(final SearchConfiguration searchConfiguration) {
        return "defaultSearch".equals(searchConfiguration.getName());
    }

    /** TODO comment me. **/
    protected void addParameter(final String key, final Object obj) {
        parameters.put(key, obj);
    }

    /** TODO comment me. **/
    public String getQueryString() {

        LOG.trace("getQueryString()");

        return queryStr;
    }

    /** TODO comment me. **/
    public Locale getLocale() {

        LOG.trace("getLocale()");

        return locale;
    }

    /** TODO comment me. **/
    public SearchMode getSearchMode() {

        LOG.trace("getSearchMode()");

        return context.getSearchMode();
    }

    /** TODO comment me. **/
    public SearchTab getSearchTab(){

        LOG.trace("getSearchTab()");

        return context.getSearchTab();
    }

    /** TODO comment me. **/
    public List<Modifier> getSources() {

        LOG.trace("getSources()");

        return sources;
    }

    /** TODO comment me. **/
    public void addSource(final Modifier modifier) {

        LOG.trace("addSource()");

        sources.add(modifier);
    }

    /** TODO comment me. **/
    public List<Enrichment> getEnrichments() {

        LOG.trace("getEnrichments()");

        return enrichments;
    }

    /** TODO comment me. **/
    public Query getQuery() {
        return queryObj;
    }
}
