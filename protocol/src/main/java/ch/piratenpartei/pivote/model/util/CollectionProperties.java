/*
 * Copyright 2012 Piratenpartei Schweiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.piratenpartei.pivote.model.util;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CollectionProperties {

    private CollectionProperties() {
    }

    public static <T> List<T> copyOf(List<T> of) {
        if ( of == null ) {
            return null;
        }
        else {
            return ImmutableList.copyOf(of);
        }
    }

    public static <K, V> Map<K, V> copyOf(Map<K, V> of) {
        if ( of == null ) {
            return null;
        }
        else {
            return ImmutableMap.copyOf(of);
        }
    }

}
