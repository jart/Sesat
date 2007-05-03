/*
 * Copyright (2005-2007) Schibsted Søk AS
 *
 */
package no.schibstedsok.searchportal.mode.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @author <a href="mailto:magnus.eklund@schibsted.no">Magnus Eklund</a>
 * @version <tt>$Id$</tt>
 */
public final class SearchMode {
    
    /**
     * 
     */
    public enum SearchCommandExecutorConfig{
        /**
         * 
         */
        @Controller("SequentialSearchCommandExecutor")
        SEQUENTIAL,
        /**
         * 
         */
        @Controller("ParallelSearchCommandExecutor")
        PARALLEL;
                
        /**
         *
         */
        @Documented
        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD})
        @Inherited
        public @interface Controller {
            /**
             *
             * @return
             */
            public String value();
        }                
    }

    private SearchCommandExecutorConfig searchCommandExecutor = SearchCommandExecutorConfig.SEQUENTIAL;
    
    /** @deprecated the key comes from the view.xml and belongs in SearchTab **/
    private String key;

    private Collection<SearchConfiguration> searchConfigurations = new ArrayList<SearchConfiguration>();
    private SearchMode parentSearchMode;
    private boolean queryAnalysisEnabled = false;
    
    /**
     * 
     */
    public SearchMode(){
    }
    
    /**
     * 
     * @param inherit 
     */
    public SearchMode(final SearchMode inherit){
        if( inherit != null ){
            parentSearchMode = inherit;
            parentMode = inherit.parentMode;
            queryAnalysisEnabled = inherit.queryAnalysisEnabled;
            searchCommandExecutor = inherit.searchCommandExecutor;
        }
    }

//    /** @deprecated the key comes from the view.xml and belongs in SearchTab **/
//    public String getKey() {
//        if (parentMode != null) {
//            return parentMode;
//        } else {
//            return key;
//        }
//    }
//
//    /** @deprecated the key comes from the view.xml and belongs in SearchTab **/
//    public void setKey(String key) {
//        this.key = key;
//    }

    /**
     * 
     * @return 
     */
    public Collection<SearchConfiguration> getSearchConfigurations() {
        return searchConfigurations;
    }
    
    /**
     * 
     * @param name 
     * @return 
     */
    public SearchConfiguration getSearchConfiguration(final String name) {
       
        for( SearchConfiguration sc : searchConfigurations){
            if( sc.getName().equals(name) ){
                return sc;
            }
        }
        return null;
    }

    /**
     * 
     * @param searchConfigurations 
     */
    public void setSearchConfigurations(Collection<SearchConfiguration> searchConfigurations) {
        this.searchConfigurations = searchConfigurations;
    }

    /**
     * 
     * @param conf 
     */
    public void addSearchConfiguration(SearchConfiguration conf) {
        searchConfigurations.add(conf);
    }


    /**
     * 
     * @return 
     */
    public SearchCommandExecutorConfig getExecutor() {
        return searchCommandExecutor;
    }

    /**
     * 
     * @param searchCommandExecutor 
     */
    public void setExecutor(final SearchCommandExecutorConfig searchCommandExecutor) {
        this.searchCommandExecutor = searchCommandExecutor;
    }

    /**
     * 
     * @return 
     */
    public SearchMode getParentSearchMode() {
        return parentSearchMode;
    }

    /**
     * Get the queryAnalysisEnabled.
     *
     * @return the queryAnalysisEnabled.
     */
    public boolean isAnalysis() {
        return queryAnalysisEnabled;
    }

    /**
     * Set the queryAnalysisEnabled.
     *
     * @param queryAnalysisEnabled The queryAnalysisEnabled to set.
     */
    public void setAnalysis(boolean queryAnalysisEnabled) {
        this.queryAnalysisEnabled = queryAnalysisEnabled;
    }

    /**
     * Holds value of property parentMode.
     */
    private String parentMode;

    /**
     * Getter for property parentMode.
     * @return Value of property parentMode.
     */
    public String getParentMode() {
        return this.parentMode;
    }

    /**
     * Setter for property parentMode.
     * @param parentMode New value of property parentMode.
     */
    public void setParentMode(String parentMode) {
        this.parentMode = parentMode;
    }
    
    public String toString(){
        return id + (parentSearchMode != null ? " --> " + parentSearchMode.toString() : "");
    }

    /**
     * Holds value of property id.
     */
    private String id;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(String id) {
        this.id = id;
    }
}