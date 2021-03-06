package org.xbrlapi.aspects;

import java.io.Serializable;

/**
 * <h2>Aspect values</h2>
 * 
 * <p>
 * Each {@link org.xbrlapi.Fact fact} may have a value for an
 * {@link Aspect aspect}. Such a value is referred to as
 * an aspect value. An aspect defines a mapping from a fact to the associated
 * aspect value or to the missing aspect value for that aspect, for facts that
 * do not have a value defined for the aspect.
 * </p>
 * 
 * <p>
 * Aspect values have the following characteristics:
 * </p>
 * 
 * <ul>
 * <li>Each aspect value is associated with a specific aspect that has its own
 * unique aspect type. Aspect values for different aspects cannot be equal.</li>
 * <li>Semantically equivalent aspect values can be identified as such based
 * upon the value of a single property of the aspect value: its identifier.</li>
 * <li>Aspect values can have a heirarchical ordering. Thus, an aspect value can
 * have a single parent aspect value and multiple child aspect values. Also,
 * sibling aspect values can have a strict ordering.</li>
 * <li>Aspect values always have human readable labels. These can be in multiple
 * languages/locales, where appropriate.</li>
 * <li>Aspect values define a mapping from the aspect identifier to the aspect
 * value labels.</li>
 * <li>It must always be possible to construct an aspect value without resorting
 * to obtaining one from an XBRL fact. This is required to enable filtering of
 * XBRL data by aspect without having to start with actual XBRL data with the
 * required aspect values. This implies that aspect values are not defined in
 * terms of specific XBRL fragments.</li>
 * </ul>
 * 
 * <p>
 * Note that aspect values store the information necessary to enable the
 * determination of their appropriate human-readable labels in various locales.
 * In some cases this information will best be a fragment index. In others, it
 * might be an element QName or an XPointer expression. The details of the
 * information recorded in an aspect value to enable determination of aspect
 * value labels are private to aspect value implementations.
 * </p>
 * 
 * <ul>
 * <li>Do we need to record the aspect label in all possible languages?</li>
 * <li>We need to record the aspect type rather than the aspect object as part
 * of the aspect value identifier.</li>
 * </ul>
 * 
 * @author Geoff Shuetrim (geoff@galexy.net)
 */
public interface AspectValue extends AspectHandler, Serializable {

    /**
     * This method must be implemented by each concrete aspect value class.
     * 
     * The identifier associated with a "missing-value" aspect value is
     * implementation dependent. You cannot assume that it will be the empty
     * string or a null value.
     * 
     * @return the string value that uniquely identifies this aspect value, for
     *         a given aspect. This value is not generally human readable. The
     *         {@link LabelHandler label handler} methods are more suitable for
     *         obtaining human readable representations of aspect values.
     *         A unique value for missing aspect values needs to be generated by 
     *         implementations of this method.
     */
    public String getId();

    /**
     * This method must be implemented by each concrete aspect value class.
     * 
     * @return true if this aspect value represents a missing value and false
     *         otherwise.
     */
    public boolean isMissing();
    
}
