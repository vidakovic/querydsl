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

import com.mysema.query.types.*;

public abstract class OrientSerializer implements Visitor<Object, Void> {

    @Override
    public Object visit(Constant<?> expr, Void context) {
        if (Enum.class.isAssignableFrom(expr.getType())) {
            return ((Enum<?>)expr.getConstant()).name();
        } else {
            return expr.getConstant();
        }
    }

    @Override
    public Object visit(TemplateExpression<?> expr, Void context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visit(FactoryExpression<?> expr, Void context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visit(Operation<?> expr, Void context) {
        throw new UnsupportedOperationException("Illegal operation " + expr);
    }

    @Override
    public String visit(Path<?> expr, Void context) {
        return "";
    }

    @Override
    public Object visit(SubQueryExpression<?> expr, Void context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object visit(ParamExpression<?> expr, Void context) {
        throw new UnsupportedOperationException();
    }

}