package com.swak.utils;

/**
 * see org.apache.commons.lang3.ObjectUtils
 * 
 * @author lifeng
 *
 */
public class ObjectUtils {

    // Null-safe equals/hashCode
    //-----------------------------------------------------------------------
    /**
     * <p>Compares two objects for equality, where either one or both
     * objects may be {@code null}.</p>
     *
     * <pre>
     * ObjectUtils.equals(null, null)                  = true
     * ObjectUtils.equals(null, "")                    = false
     * ObjectUtils.equals("", null)                    = false
     * ObjectUtils.equals("", "")                      = true
     * ObjectUtils.equals(Boolean.TRUE, null)          = false
     * ObjectUtils.equals(Boolean.TRUE, "true")        = false
     * ObjectUtils.equals(Boolean.TRUE, Boolean.TRUE)  = true
     * ObjectUtils.equals(Boolean.TRUE, Boolean.FALSE) = false
     * </pre>
     *
     * @param object1  the first object, may be {@code null}
     * @param object2  the second object, may be {@code null}
     * @return {@code true} if the values of both objects are the same
     */
    public static boolean equals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        return object1.equals(object2);
    }
    
    /**
     * <p>Gets the hash code of an object returning zero when the
     * object is {@code null}.</p>
     *
     * <pre>
     * ObjectUtils.hashCode(null)   = 0
     * ObjectUtils.hashCode(obj)    = obj.hashCode()
     * </pre>
     *
     * @param obj  the object to obtain the hash code of, may be {@code null}
     * @return the hash code of the object, or zero if null
     * @since 2.1
     */
    public static int hashCode(Object obj) {
        // hashCode(Object) retained for performance, as hash code is often critical
        return obj == null ? 0 : obj.hashCode();
    }
}
