package com.opencsv.bean;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright 2007,2010 Kyle Miller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Expands on {@link HeaderColumnNameMappingStrategy} by allowing the user to
 * pass in a map of column names to bean names.
 * This way the fields in the bean do not have to match the fields in the CSV
 * file. This is only for when the user passes in the header names
 * programmatically, and not for annotated beans.
 *
 * @param <T> Class to be mapped.
 */
public class HeaderColumnNameTranslateMappingStrategy<T> extends HeaderNameBaseMappingStrategy<T> {
    private final Map<String, String> columnMapping;

    /**
     * Default constructor. Considered stable.
     * @see HeaderColumnNameTranslateMappingStrategyBuilder
     */
    public HeaderColumnNameTranslateMappingStrategy() {
        columnMapping = new HashMap<>();
    }

    /**
     * Constructor to allow setting options for header name mapping.
     * Not considered stable. As new options are introduced for the mapping
     * strategy, they will be introduced here. You are encouraged to use
     * {@link HeaderColumnNameTranslateMappingStrategyBuilder}.
     *
     * @param forceCorrectRecordLength If set, every record will be shortened
     *                                 or lengthened to match the number of
     *                                 headers
     * @see HeaderColumnNameTranslateMappingStrategyBuilder
     */
    public HeaderColumnNameTranslateMappingStrategy(boolean forceCorrectRecordLength) {
        super(forceCorrectRecordLength);
        columnMapping = new HashMap<>();
    }

    @Override
    public String getColumnName(int col) {
        String name = headerIndex.getByPosition(col);
        if(name != null) {
            name = columnMapping.get(name.toUpperCase());
        }
        return name;
    }

    /**
     * Retrieves the column mappings of the strategy.
     * @return The column mappings of the strategy.
     */
    public Map<String, String> getColumnMapping() {
        return columnMapping;
    }

    /**
     * Sets the column mapping to those passed in.
     * @param columnMapping Source column mapping.
     */
    public void setColumnMapping(Map<String, String> columnMapping) {
        this.columnMapping.clear();
        for (Map.Entry<String, String> entry : columnMapping.entrySet()) {
            this.columnMapping.put(entry.getKey().toUpperCase(), entry.getValue());
        }
        if(getType() != null) {
            loadFieldMap();
        }
    }
}
