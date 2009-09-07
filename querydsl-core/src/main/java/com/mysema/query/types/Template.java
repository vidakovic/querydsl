/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.query.types;

import java.util.List;

import javax.annotation.Nullable;

import com.mysema.query.types.expr.Expr;

import net.jcip.annotations.Immutable;


/**
 * Template for operation and path serialization
 * 
 * @author tiwe
 * 
 */
@Immutable
public final class Template {
    
    @Immutable
    public static final class Element {
        
        private final int index;
        
        @Nullable
        private final String staticText;
        
        @Nullable
        private final Converter<?,?> converter;
        
        private final boolean asString;
        
        private final String toString;

        Element(int index, Converter<?,?> converter) {
            this.asString = false;
            this.converter = converter;
            this.index = index;            
            this.staticText = null;
            this.toString = String.valueOf(index);
        }        
        Element(int index, boolean asString) {
            this.asString = asString;
            this.converter = null;
            this.index = index;            
            this.staticText = null;
            this.toString = index + (asString ? "s" : "");
        }
        Element(String text) {
            this.asString = false;
            this.converter = null;
            this.index = -1;            
            this.staticText = text;
            this.toString = "'" + staticText + "'";
        }
        
        public int getIndex() {
            return index;
        }

        @Nullable
        public String getStaticText() {
            return staticText;
        }

        public boolean isAsString() {
            return asString;
        }
        
        public boolean hasConverter(){
            return converter != null;
        }
        
        @SuppressWarnings("unchecked")
        public Expr<?> convert(Expr<?> source){
            return ((Converter)converter).convert(source);
        }

        @Override
        public String toString() {
            return toString;
        }
    }
    
    private final List<Element> elements;

    private final String template;

    Template(String template, List<Element> elements) {
        this.template = template;
        this.elements = elements;
    }

    public List<Element> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return template;
    }
    
}
