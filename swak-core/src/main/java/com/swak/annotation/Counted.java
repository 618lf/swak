package com.swak.annotation;

import com.swak.utils.StringUtils;

import java.lang.annotation.*;

/**
 * An annotation for marking a method of an annotated object as counted.
 * <p>
 * <p/>
 * Given a method like this:
 * <pre><code>
 *       {@literal @}Counted(name = "fancyName")
 *       public String fancyName(String name) {
 *           return "Sir Captain " + name;
 *       }
 *  </code></pre>
 * <p/>
 * A counter for the defining class with the name {@code fancyName} will be created and each time the
 * {@code #fancyName(String)} method is invoked, the counter will be marked.
 *
 * @author: lifeng
 * @date: 2020/3/28 17:12
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Counted {

    /**
     * @return The name of the counter.
     */
    String name() default StringUtils.EMPTY;

    /**
     * @return If {@code true}, use the given name as an absolute name. If {@code false}, use the given name
     * relative to the annotated class. When annotating a class, this must be {@code false}.
     */
    boolean absolute() default true;

    /**
     * @return If {@code false} (default), the counter is decremented when the annotated
     * method returns, counting current invocations of the annotated method.
     * If {@code true}, the counter increases monotonically, counting total
     * invocations of the annotated method.
     */
    boolean monotonic() default false;

}
