package ch.piratenpartei.pivote.serialize.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Serialize {

    /**
     * A list of fields and their types. The syntax of each entry is
     * "<code>identifier: type</code>". See <code>{@link ch.piratenpartei.pivote.serialize.Type}</code> for available types,
     * type names are case-insensitive. For lists, append a '*' to type, for maps, use
     * "<code>keyType->valueType</code>. Spaces between the elements are ignored. See the
     * regular expression in {@link HandlerBuilder#SYNTAX}.
     * <p/>
     * Examples:
     * <ul>
     *     <li>name: string</li>
     *     <li>myMap: bigint->byte</li>
     *     <li>messages: langstring*</li>
     * </ul>
     *
     * @return
     */
    String[] value();

}
