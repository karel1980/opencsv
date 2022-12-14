/*
 * Copyright 2017 Andrew Rucker Jones.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opencsv.bean.mocks.split;

import org.apache.commons.text.TextStringBuilder;

import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author Andrew Rucker Jones
 */
public class IntegerSetSortedToString extends HashSet<Integer> {
    @Override
    public String toString() {
        TextStringBuilder sb = new TextStringBuilder("[");
        Integer[] intArray = this.toArray(new Integer[0]);
        Arrays.sort(intArray);
        sb.appendWithSeparators(intArray, ",");
        sb.append(']');
        return sb.toString();
    }
}
