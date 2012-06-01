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
