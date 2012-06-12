package ch.piratenpartei.pivote.serialize.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

    String name();

    String type();

    Security security() default Security.NONE;

}
