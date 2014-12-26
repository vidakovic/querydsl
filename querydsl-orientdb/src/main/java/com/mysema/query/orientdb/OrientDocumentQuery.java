package com.mysema.query.orientdb;

import com.orientechnologies.orient.core.db.ODatabase;

public class OrientDocumentQuery extends OrientQuery {

    public OrientDocumentQuery() {
        // TODO: implement this
        super("", "", "", null, null);
    }

    @Override
    protected void configure() {

    }

    @Override
    protected ODatabase<?> db() {
        return null;
    }
}
