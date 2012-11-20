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

package com.chen.candybon.fasttrack.dao;

import com.chen.candybon.fasttrack.data.ClassUtils;
import com.chen.candybon.fasttrack.exception.DataClassException;
import com.chen.candybon.fasttrack.object.LargeKeyedObject;
import com.chen.candybon.fasttrack.object.SmallKeyedObject;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * ClassUtils test.
 */
public class ClassUtilsTest {
    @Test
    public void testClassSelectorUsingClass() throws Exception {
        assertTrue(SmallKeyedObject.class.equals(ClassUtils.classSelector(SmallTestMe.class)));
        assertFalse(SmallKeyedObject.class.equals(ClassUtils.classSelector(LargeTestMe.class)));
        assertTrue(LargeKeyedObject.class.equals(ClassUtils.classSelector(LargeTestMe.class)));
        assertFalse(LargeKeyedObject.class.equals(ClassUtils.classSelector(SmallTestMe.class)));
    }

    @Test
    public void testClassSelectorUsingData() throws Exception {
        SmallTestMe small = new SmallTestMe();
        LargeTestMe large = new LargeTestMe();

        assertTrue(SmallKeyedObject.class.equals(ClassUtils.classSelector(small)));
        assertFalse(SmallKeyedObject.class.equals(ClassUtils.classSelector(large)));
        assertTrue(LargeKeyedObject.class.equals(ClassUtils.classSelector(large)));
        assertFalse(LargeKeyedObject.class.equals(ClassUtils.classSelector(small)));
    }

    @Test
    public void testClassSelectorExceptions() throws Exception {
        Class clazz = null;
        try {
            ClassUtils.classSelector(clazz);
            fail("Invalid: null is not a proper class");
        } catch (DataClassException e) {
            // OK
        }

        clazz = String.class;
        try {
            ClassUtils.classSelector(clazz);
            fail("Invalid: String is not a proper class");
        } catch (DataClassException e) {
            // OK
        }

        try {
            ClassUtils.classSelector(null);
            fail("Invalid: null is not a proper class");
        } catch (DataClassException e) {
            // OK
        }

        String s = "invalid";
        try {
            ClassUtils.classSelector(s);
            fail("Invalid: String is not a data");
        } catch (DataClassException e) {
            // OK
        }

        SmallTestMe d = null;
        try {
            ClassUtils.classSelector(d);
            fail("Invalid: String is not a data");
        } catch (DataClassException e) {
            // OK
        }

        Object o = null;
        try {
            ClassUtils.classSelector(o);
            fail("Invalid: String is not a data");
        } catch (DataClassException e) {
            // OK
        }

    }

}
