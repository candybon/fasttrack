/*
 * Copyright (c) XIAOWEI CHEN, 2009.
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * 
 * XIAOWEI CHEN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. XIAOWEI CHEN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * All rights reserved.
 */
package com.chen.candybon.common.utils;

/**
 * @author Xiaowei Chen
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Provide common utility methods for {@link java.lang.String} objects.
 */
public final class StringUtils {
    public static final int MAX_STR_LENGTH = 255;

    /**
     * Protect access to constructor.
     */
    private StringUtils() {
    }

    /**
     * Returns trus if the String is empty or null.
     *
     * @param value A String
     * @return True if String is null or emtpy.
     */
    public static boolean isEmptyOrNull(final String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Returns true if the input string is composed of Digits ([0-9] chars) and is less than the imposed length.
     *
     * @param an A String to check.
     * @param length Maximum length of the input.
     * @return True if the String is made of digits only, otherwise an IllegalArgumentException is sent.
     */
    public static boolean isDigits(final String an, final int length) {
        if (an == null) {
            throw new IllegalArgumentException("Invalid String value (null)");
        }
        return an.length() <= length && isDigits(an);
    }

    /**
     * Returns true if the input string is composed of Digits ([0-9] chars).
     *
     * @param an A String to check.
     * @return True if the String is made of digits only, otherwise false.
     */
    public static boolean isDigits(final String an) {
        if (an == null) {
            throw new IllegalArgumentException("Invalid String value (null)");
        }
        return an.matches("\\p{Digit}+");
    }

    /**
     * Validates mandatory (not null), max and min length of a string.
     *
     * @param argumentValue The value to be tested for existence and
     * minimum and maximum length
     * @param isMandatory If true it will check if the argument is null
     * which will cause a negative result
     * @param minLength The minimum length this argumentValue can be
     * @param maxLength The maximum lenght this argumentValue can be
     * @return True if the mandatory parameter has an acceptable length, otherwise false if
     *         - The argument is null and mandatory
     *         - The argument does not respect the min  and max length.
     */
    public static boolean isValid(String argumentValue, boolean isMandatory, int minLength,
                                  int maxLength) {

        // Check mandatory
        if (argumentValue == null) {
            return !isMandatory;
        }

        // Validate min and max length
        if (argumentValue.length() < minLength) {
            return false;
        } else if (argumentValue.length() > maxLength) {
            return false;
        }
        return true;
    }

    /**
     * Removes all spaces from a specific String.
     *
     * @param input A String.
     * @return A String with no spaces.
     */
    public static String removeAllSpaces(final String input) {
        if (input == null) {
            return input;
        }
        return input.trim().replaceAll("\\s+", "");
    }

    /**
     * Removes special characters and replace them with an underscore.
     * @param str the string to replace special characters in
     * @return the string with special characters replaced with an underscore.
     */
    public static String getNormalizedString(String str) {
        if (str == null) {
            return str;
        }

        return str.replaceAll("[^0-9a-zA-Z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u00FF-]", "_");
    }

    /**
     * Throws a RuntimeException if the specified string is longer
     * than the specified maximum number of characters.
     * Null strings are skipped.
     *
     * @param str A Tring
     * @param maxLength The max length for this String.
     * @throws IllegalArgumentException In case the String exceeds the maximum length.
     */
    public static void checkLength(final String str, final int maxLength) {
        if (str != null && str.length() > maxLength) {
            throw new IllegalArgumentException("String too long: " + str.length() + " characters. " +
                    maxLength + " allowed");
        }
    }

    /**
     * Compares two char arrays, and compute how many characters are equal starting from the first
     * character in the array.  The count is stopped as soon as one character does not match.
     * <p/>
     * ex:
     * compareAndCount({a, a, z}, {a, b, z})
     * <p/>
     * Will return 1.
     *
     * @param a A char array
     * @param b A char array
     * @return Number of matching char starting from the first position of the array.
     */
    public static int compareAndCount(char[] a, char[] b) {
        int count = 0;
        if (a == null || b == null) {
            return 0;
        }
        for (int i = 0; i < a.length && i < b.length; i++) {
            if (a[i] == b[i]) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    /**
     * Reverse a String.
     * <p/>
     * Ex;
     * "abcd" -> "dcba"
     *
     * @param s What to reverse.
     * @return The reversed string, or null if the String to reverse is null.
     */
    public static String reverse(final String s) {
        if (s == null) {
            return null;
        }
        return new StringBuffer(s).reverse().toString();
    }
   
    /**
     * Extracts a List of Strings from a single String previously concatenated with a specific separator.
     * The separating tokens are not returned as part of the List.
     * <p/>
     * "abcd%%def", %% -> {"abcd", "def"}
     * <p/>
     * If the <code>value</code> is null, then null is returned.
     *
     * @param value What to separate.
     * @param separator What to use as the string delimiter.
     * @return The List of String extracted.
     */
    public static List<String> fromStringToSeparatedList(final String value, final String separator) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return Collections.emptyList();
        }
        StringTokenizer tokenizer = new StringTokenizer(value, separator);
        List<String> result = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }
        return result;
    }


}

