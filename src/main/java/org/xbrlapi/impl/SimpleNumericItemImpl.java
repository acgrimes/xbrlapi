package org.xbrlapi.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xbrlapi.SimpleNumericItem;
import org.xbrlapi.utilities.XBRLException;

/**
 * @author Geoffrey Shuetrim (geoff@galexy.net)
 */

public class SimpleNumericItemImpl extends NumericItemImpl implements SimpleNumericItem {
	
	/**
     * 
     */
    private static final long serialVersionUID = 5023073637732952730L;

    /** 
	 * Get the value of the fact as a string.
	 * @return the value of fact as a string.
	 * @throws XBRLException
	 * @see SimpleNumericItem#getValue()
	 */
	public String getValue() throws XBRLException {
	    if (this.isNil()) return null;
		return getDataRootElement().getTextContent().trim();
	}
	


	/** 
	 * Get the value of the fact after adjusting for the specified precision.
	 * @return the value of the fact as a string, adjusted for the specified precision.
	 * @throws XBRLException
	 * @see SimpleNumericItem#getPrecisionAdjustedValue()
	 */
	public String getPrecisionAdjustedValue() throws XBRLException {
		String precision = getInferredPrecision();
		if (precision.equals("INF")) return getValue();
		
		String rawValue = getValue();
		Pattern pattern = Pattern.compile("^(-?)(\\d*)(\\.?)(\\d*)(e?-?\\d*)?$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(getValue());
        String digitsBeforeDecimal = "";
        String digitsAfterDecimal = "";
        // String exponent = "";
        if (matcher.matches()) {
    		digitsBeforeDecimal = matcher.group(2);
    		digitsAfterDecimal= matcher.group(4);
        	String digits =  digitsBeforeDecimal + digitsAfterDecimal;
        	Pattern pattern2 = Pattern.compile("^(0*)(\\d{0," + precision + "})(\\d*)$");
            Matcher matcher2 = pattern2.matcher(digits);
            if (! matcher2.matches()) throw new XBRLException("The raw value is not formatted in a way that allows precision adjustment.");            
            digits = matcher2.group(1) + matcher2.group(2);
            int zerosToInsert = matcher2.group(3).length();
            for (int i=1; i<=zerosToInsert; i++) digits += "0";
    		digitsBeforeDecimal = digits.substring(0,digitsBeforeDecimal.length());
    		digitsAfterDecimal= digits.substring(digitsBeforeDecimal.length(), digits.length());
        	String value = matcher.group(1) + digitsBeforeDecimal + matcher.group(3) + digitsAfterDecimal + matcher.group(5);   	
        	return value;
        }
		throw new XBRLException("Precision adjustment failed using precision " + precision + " and value " + rawValue);
	}
	
	/**
	 * Get the inferred value for precision from the value for 
	 * decimals and the value of the fact.
	 * @return inferred value for precision.
	 * @throws XBRLException
	 * @see SimpleNumericItem#getInferredPrecision()
	 */
	public String getInferredPrecision() throws XBRLException {
		if (! hasDecimals()) return getPrecision();
		
		String decimals = getDecimals();
		if (decimals.equals("INF")) return "INF";
				
		//int d = new Integer(decimals).intValue();

		// Parse the value into its components
		Pattern pattern = Pattern.compile("^-?(\\d*)\\.?(\\d*)(e?(-?\\d*))?$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(getValue());
        String digitsBeforeDecimal = null;
        String digitsAfterDecimal = null;
        String exponent = null;
        if (matcher.matches()) {
    		digitsBeforeDecimal = matcher.group(1);
    		digitsAfterDecimal= matcher.group(2);
    		exponent = matcher.group(4);
        }
		
		// Eliminate leading zeros before decimal place.
		if (digitsBeforeDecimal != null) {
			pattern = Pattern.compile("^(0+)");
	        matcher = pattern.matcher(digitsBeforeDecimal);
	        digitsBeforeDecimal = matcher.replaceAll("");
		}
		
		int part1 = 0;
		if (digitsBeforeDecimal != null) {
			part1 = digitsBeforeDecimal.length();
		} else {
			if (digitsAfterDecimal != null) {
				pattern = Pattern.compile("^(0+)");
		        matcher = pattern.matcher(digitsBeforeDecimal);
		        if (matcher.matches()) {
			        String zerosAfterDecimal = matcher.group(1);
			        if (zerosAfterDecimal != null) {
			        	part1 = -(zerosAfterDecimal.length()); 
			        }
		        }
			}
		}
		
		int part2 = 0;
		if (exponent != null && !exponent.equals("")) part2 = (new Integer(exponent)).intValue();

		int part3 = (new Integer(decimals)).intValue();
		
		int x = part1 + part2 + part3;
		
		int precision = (x > 0) ? x : 0;
		
		return (new Integer(precision)).toString();	
	}
}
