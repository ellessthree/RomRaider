/*
 * RomRaider Open-Source Tuning, Logging and Reflashing
 * Copyright (C) 2006-2015 RomRaider.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.romraider.util;

import org.nfunk.jep.JEP;

import java.util.Map;
import java.util.LinkedHashMap;

public final class JEPUtil {
    @SuppressWarnings("serial")
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private int cacheSize;

        public LRUCache(int cacheSize) {
          super(16, 0.75f, true);
          this.cacheSize = cacheSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
          return size() >= cacheSize;
        }
    };
    
    private static final LRUCache<String, JEP> parserCache = new LRUCache<String, JEP>(16);

    private JEPUtil() {
    }

    public static double evaluate(String expression, double value) {
        JEP parser = parserCache.get(expression);
        if (parser == null) {
            parser = new JEP();
            parser.addStandardFunctions();
            parser.addFunction("BitWise", new BitWise());
            parser.initSymTab(); // clear the contents of the symbol table
            parser.addVariable("x", value);
            parser.parseExpression(expression);

            parserCache.put(expression, parser);
        } else {
            parser.setVarValue("x", value);
        }
        return parser.getValue();
    }

    public static double evaluate(String expression, Map<String, Double> valueMap) {
        final JEP parser = new JEP();
        parser.initSymTab(); // clear the contents of the symbol table
        for (String id : valueMap.keySet()) {
            parser.addVariable(id, valueMap.get(id));
        }
        parser.parseExpression(expression);
        return parser.getValue();
    }
}
