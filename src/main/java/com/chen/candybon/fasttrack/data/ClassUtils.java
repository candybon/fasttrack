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
package com.chen.candybon.fasttrack.data;
/**
 * @author Xiaowei Chen
 */
import com.chen.candybon.fasttrack.exception.DataClassException;
import com.chen.candybon.fasttrack.object.LargeKeyedObject;
import com.chen.candybon.fasttrack.object.SmallKeyedObject;


/**
 * Util class that analyzes a given class and assesses if this class is inheriting from {@link SmallKeyedObject} or
 * {@link LargeKeyedObject}.
 */
public final class ClassUtils {
    private ClassUtils() {
    }

    /**
     * Analyzes any class.
     *
     * @param tClass Any class.
     * @return SmallKeyedObject class if the class inherits from it, otherwise LargeKeyedObject.
     * @throws com.chen.candybon.fasttrack.exception.DataClassException in case the class is null.
     */
    public static Class classSelector(Class tClass) throws DataClassException {
        if (tClass == null) {
            throw new DataClassException("Null is not a valid Fast Track Data Class.");
        }

        if (SmallData.class.isAssignableFrom(tClass)) {
            return SmallKeyedObject.class;
        }
        if (LargeData.class.isAssignableFrom(tClass)) {
            return LargeKeyedObject.class;
        }
        
        throw new DataClassException("Invalid class inheritance. A fast track class must implement either SmallData "
                + "or LargeData interface.");
    }

    /**
     * Analyzes any class instance (object) and asses if it implements either {@link LargeData} or {@link SmallData}.
     *
     * @param data Any object of base type <code>SmallData</code> or <code>LargeData</code>.
     * @return SmallKeyedObject class if the class inherits from it, otherwise LargeKeyedObject.
     * @throws com.chen.candybon.fasttrack.exception.DataClassException in case the class is null, or if
     * the class does not implement either of <code>SmallData</code> or <code>LargeData</code>. 
     */
    public static Class classSelector(Object data) throws DataClassException {
        if (data == null) {
            throw new DataClassException("Null is not a valid Fast Track Data Class.");
        }

        if (data instanceof SmallData) {
            return SmallKeyedObject.class;
        } else if (data instanceof LargeData) {
            return LargeKeyedObject.class;
        }

        throw new DataClassException("Invalid class inheritance. Any fast track instance must " +
                "inherit from either SmallData or LargeData interfaces.");

    }

    /**
     * Validates that an object is either an instanc of {@link LargeData} or {@link SmallData}.
     *
     * @param data The Object to verify.
     * @return True of type inheritance is respected.
     * @throws DataClassException If the inheritance is not respected.
     */
    public static boolean typeValidation(Object data) throws DataClassException {
        if (data instanceof SmallData) {
            return true;
        } else if (data instanceof LargeData) {
            return true;
        }
        throw new DataClassException("Invalid class inheritance. Any fast track instance must " +
                "inherit from either SmallData or LargeData interfaces.");
    }
}
