/*
 * Copyright 2011, Mysema Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mysema.query.orientdb;

import com.google.common.base.Function;
import com.mysema.commons.lang.CloseableIterator;
import com.mysema.query.*;
import com.mysema.query.support.QueryMixin;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.Path;
import com.mysema.query.types.Predicate;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.fetch.OFetchPlan;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class OrientQuery<K> implements SimpleQuery<OrientQuery<K>>, SimpleProjectable<K> {

    @SuppressWarnings("serial")
    private static class NoResults extends RuntimeException {}

    private final OrientSerializer serializer;

    private final QueryMixin<OrientQuery<K>> queryMixin;

    protected final String url;
    protected final String username;
    protected final String password;

    protected final Function<ODocument, K> transformer;

    public OrientQuery(String url, String username, String password, Function<ODocument, K> transformer, OrientSerializer serializer) {
        this.queryMixin = new QueryMixin<OrientQuery<K>>(this, new DefaultQueryMetadata().noValidate(), false);
        this.transformer = transformer;
        this.url = url;
        this.username = username;
        this.password = password;
        this.serializer = serializer;
    }

    // configure pool
    protected abstract void configure();

    protected abstract ODatabase<?> db();

    @Override
    public boolean exists() {
        try {
            QueryMetadata metadata = queryMixin.getMetadata();
            //Predicate filter = createFilter(metadata);
            //return collection.findOne(createQuery(filter)) != null;
            return false; // TODO: implement this
        } catch (NoResults ex) {
            return false;
        }
    }

    @Override
    public boolean notExists() {
        return !exists();
    }

    @Override
    public OrientQuery<K> distinct() {
        return queryMixin.distinct();
    }

    public OrientQuery<K> where(Predicate e) {
        return queryMixin.where(e);
    }

    @Override
    public OrientQuery<K> where(Predicate... e) {
        return queryMixin.where(e);
    }

    @Override
    public OrientQuery<K> limit(long limit) {
        return queryMixin.limit(limit);
    }

    @Override
    public OrientQuery<K> offset(long offset) {
        return queryMixin.offset(offset);
    }

    @Override
    public OrientQuery<K> restrict(QueryModifiers modifiers) {
        return queryMixin.restrict(modifiers);
    }

    public OrientQuery<K> orderBy(OrderSpecifier<?> o) {
        return queryMixin.orderBy(o);
    }

    @Override
    public OrientQuery<K> orderBy(OrderSpecifier<?>... o) {
        return queryMixin.orderBy(o);
    }

    @Override
    public <T> OrientQuery<K> set(ParamExpression<T> param, T value) {
        return queryMixin.set(param, value);
    }

    public CloseableIterator<K> iterate(Path<?>... paths) {
        queryMixin.addProjection(paths);
        return iterate();
    }

    @Override
    public CloseableIterator<K> iterate() {
        //final DBCursor cursor = createCursor();
        final Iterator<ODocument> iterator = null;
        return new CloseableIterator<K>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public K next() {
                return transformer.apply(iterator.next());
            }

            @Override
            public void remove() {
            }

            @Override
            public void close() {
            }
        };
    }

    public List<K> list(Path<?>... paths) {
        queryMixin.addProjection(paths);
        return list();
    }

    @Override
    public List<K> list() {
        try {
            final List<ODocument> rows = null;
            List<K> results = new ArrayList<K>();
            for(ODocument doc : rows) {
                results.add(transformer.apply(doc));
            }
            return results;
        } catch (NoResults ex) {
            return Collections.emptyList();
        }
    }

    protected OFetchPlan createFetchPlan(String fetchPlan) {
        QueryMetadata metadata = queryMixin.getMetadata();
        OFetchPlan plan = new OFetchPlan(""); // TODO: implement this
        return plan;
    }

    public K singleResult(Path<?>...paths) {
        queryMixin.addProjection(paths);
        return singleResult();
    }

    @Override
    public K singleResult() {
        try {
            // TODO: implement this
            return null;
        } catch (NoResults ex) {
            return null;
        }
    }

    public K uniqueResult(Path<?>... paths) {
        queryMixin.addProjection(paths);
        return uniqueResult();
    }

    @Override
    public K uniqueResult() {
        try {
            Long limit = queryMixin.getMetadata().getModifiers().getLimit();
            if (limit == null) {
                limit = 2l;
            }
            // TODO: implement this
            return null;
        } catch (NoResults ex) {
            return null;
        }
    }

    public SearchResults<K> listResults(Path<?>... paths) {
        queryMixin.addProjection(paths);
        return listResults();
    }

    @Override
    public SearchResults<K> listResults() {
        try {
            long total = count();
            if (total > 0l) {
                return new SearchResults<K>(list(), queryMixin.getMetadata().getModifiers(), total);
            } else {
                return SearchResults.emptyResults();
            }
        } catch (NoResults ex) {
            return SearchResults.emptyResults();
        }
    }

    @Override
    public long count() {
        try {
            // TODO: implement this
            return 0l;
        } catch (NoResults ex) {
            return 0l;
        }
    }

    @Override
    public String toString() {
        // TODO: implement this
        return "";
    }

}