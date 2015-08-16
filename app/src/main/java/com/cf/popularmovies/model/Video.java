package com.cf.popularmovies.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Video {

    @Expose
    private Integer id;
    @Expose
    private List<VideoResult> results = new ArrayList<>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The videoResults
     */
    public List<VideoResult> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The videoResults
     */
    public void setResults(List<VideoResult> results) {
        this.results = results;
    }


}
