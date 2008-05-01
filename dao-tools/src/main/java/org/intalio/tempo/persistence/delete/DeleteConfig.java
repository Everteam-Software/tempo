package org.intalio.tempo.persistence.delete;

import java.util.ArrayList;

public class DeleteConfig {
    public DeleteConfig() {
    }

    String username;
    boolean fakerun;
    ArrayList<Query> queries;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isFakerun() {
        return fakerun;
    }

    public void setFakerun(boolean fakerun) {
        this.fakerun = fakerun;
    }

    public ArrayList<Query> getQueries() {
        return queries;
    }

    public void setQueries(ArrayList<Query> queries) {
        this.queries = queries;
    }

}
