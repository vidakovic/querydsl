/*
 * Copyright 2013, Mysema Ltd
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

import com.mysema.query.types.Operator;
import com.mysema.query.types.OperatorImpl;

public final class OrientOps {

    private static final String NS = OrientOps.class.getName();

    public static final Operator<Object> ALL = new OperatorImpl<Object>(NS, "ALL");

    public static final Operator<Object> CAST = new OperatorImpl<Object>(NS, "CAST");

    public static final Operator<Object> FIRSTVALUE = new OperatorImpl<Object>(NS, "FIRSTVALUE");

    public static final Operator<Object> RATIOTOREPORT = new OperatorImpl<Object>(NS, "RATIOTOREPORT");

    public static final Operator<Long> ROWNUMBER = new OperatorImpl<Long>(NS, "ROWNUMBER");

    public static final Operator<Object> UNION = new OperatorImpl<Object>(NS, "UNION");

    public static final Operator<Object> UNION_ALL = new OperatorImpl<Object>(NS, "UNION_ALL");

    public static final Operator<Object> WITH_ALIAS = new OperatorImpl<Object>(NS, "WITH_ALIAS");

    public static final Operator<Object> WITH_COLUMNS = new OperatorImpl<Object>(NS, "WITH_COLUMNS");

    private OrientOps() {}

}
