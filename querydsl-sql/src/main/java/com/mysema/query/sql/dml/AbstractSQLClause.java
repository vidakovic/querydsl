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
package com.mysema.query.sql.dml;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.mysema.query.QueryMetadata;
import com.mysema.query.dml.DMLClause;
import com.mysema.query.sql.*;
import com.mysema.query.support.QueryBase;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.ParamNotSetException;
import com.mysema.query.types.Path;
import org.slf4j.Logger;
import org.slf4j.MDC;

/**
 * AbstractSQLClause is a superclass for SQL based DMLClause implementations
 *
 * @author tiwe
 */
public abstract class AbstractSQLClause<C extends AbstractSQLClause<C>> implements DMLClause<C> {

    protected final Configuration configuration;

    protected final SQLListeners listeners;

    protected boolean useLiterals;

    protected SQLListenerContextImpl context;

    /**
     * @param configuration
     */
    public AbstractSQLClause(Configuration configuration) {
        this.configuration = configuration;
        this.listeners = new SQLListeners(configuration.getListeners());
        this.useLiterals = configuration.getUseLiterals();
    }

    /**
     * @param listener
     */
    public void addListener(SQLListener listener) {
        listeners.add(listener);
    }

    /**
     * Called to create and start a new SQL Listener context
     *
     * @param connection the database connection
     * @param metadata   the meta data for that context
     * @param entity     the entity for that context
     * @return the newly started context
     */
    protected SQLListenerContextImpl startContext(Connection connection, QueryMetadata metadata, RelationalPath<?> entity) {
        SQLListenerContextImpl context = new SQLListenerContextImpl(metadata, connection, entity);
        listeners.start(context);
        return context;
    }

    /**
     * Called to make the call back to listeners when an exception happens
     *
     * @param context the current context in play
     * @param e       the exception
     */
    protected void onException(SQLListenerContextImpl context, Exception e) {
        context.setException(e);
        listeners.exception(context);
    }

    /**
     * Called to end a SQL listener context
     *
     * @param context the listener context to end
     */
    protected void endContext(SQLListenerContextImpl context) {
        listeners.end(context);
        this.context = null;
    }


    protected SQLBindings createBindings(QueryMetadata metadata, SQLSerializer serializer) {
        String queryString = serializer.toString();
        ImmutableList.Builder<Object> args = ImmutableList.builder();
        Map<ParamExpression<?>, Object> params = metadata.getParams();
        for (Object o : serializer.getConstants()) {
            if (o instanceof ParamExpression) {
                if (!params.containsKey(o)) {
                    throw new ParamNotSetException((ParamExpression<?>) o);
                }
                o = metadata.getParams().get(o);
            }
            args.add(o);
        }
        return new SQLBindings(queryString, args.build());
    }

    protected SQLSerializer createSerializer() {
        SQLSerializer serializer = new SQLSerializer(configuration, true);
        serializer.setUseLiterals(useLiterals);
        return serializer;
    }

    /**
     * Get the SQL string and bindings
     *
     * @return
     */
    public abstract List<SQLBindings> getSQL();

    /**
     * Set the parameters to the given PreparedStatement
     *
     * @param stmt preparedStatement to be populated
     * @param objects list of constants
     * @param constantPaths list of paths related to the constants
     * @param params map of param to value for param resolving
     */
    protected void setParameters(PreparedStatement stmt, List<?> objects,
            List<Path<?>> constantPaths, Map<ParamExpression<?>, ?> params) {
        if (objects.size() != constantPaths.size()) {
            throw new IllegalArgumentException("Expected " + objects.size() + " paths, " +
                    "but got " + constantPaths.size());
        }
        for (int i = 0; i < objects.size(); i++) {
            Object o = objects.get(i);
            try {
                if (o instanceof ParamExpression) {
                    if (!params.containsKey(o)) {
                        throw new ParamNotSetException((ParamExpression<?>) o);
                    }
                    o = params.get(o);
                }
                configuration.set(stmt, constantPaths.get(i), i+1, o);
            } catch (SQLException e) {
                throw configuration.translate(e);
            }
        }
    }

    private long executeBatch(PreparedStatement stmt) throws SQLException {
        if (configuration.getUseLiterals()) {
            return stmt.executeUpdate();
        } else if (configuration.getTemplates().isBatchCountViaGetUpdateCount()) {
            stmt.executeBatch();
            return stmt.getUpdateCount();
        } else {
            long rv = 0;
            for (int i : stmt.executeBatch()) {
                rv += i;
            }
            return rv;
        }
    }

    protected long executeBatch(Collection<PreparedStatement> stmts) throws SQLException {
        long rv = 0;
        for (PreparedStatement stmt : stmts) {
            rv += executeBatch(stmt);
        }
        return rv;
    }

    protected void close(Statement stmt) {
        try {
            stmt.close();
        } catch (SQLException e) {
            throw configuration.translate(e);
        }
    }

    protected void close(Collection<? extends Statement> stmts) {
        for (Statement stmt : stmts) {
            close(stmt);
        }
    }

    protected void close(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            throw configuration.translate(e);
        }
    }

    protected void logQuery(Logger logger, String queryString, Collection<Object> parameters) {
        String normalizedQuery = queryString.replace('\n', ' ');
        MDC.put(QueryBase.MDC_QUERY, normalizedQuery);
        MDC.put(QueryBase.MDC_PARAMETERS, String.valueOf(parameters));
        if (logger.isDebugEnabled()) {
            logger.debug(normalizedQuery);
        }
    }

    protected void cleanupMDC() {
        MDC.remove(QueryBase.MDC_QUERY);
        MDC.remove(QueryBase.MDC_PARAMETERS);
    }

    protected void reset() {
        cleanupMDC();
    }

    public void setUseLiterals(boolean useLiterals) {
        this.useLiterals = useLiterals;
    }

}
