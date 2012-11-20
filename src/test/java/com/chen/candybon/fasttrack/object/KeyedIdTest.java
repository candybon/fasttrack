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
package com.chen.candybon.fasttrack.object;

import com.chen.candybon.fasttrack.dao.SmallTestMe;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class KeyedIdTest {

    @Test
    public void testConstructor() throws Exception {
        KeyedId keyedId = new KeyedId();
        assertNull(keyedId.getName());
        assertNull(keyedId.getUid());

        keyedId = new KeyedId(SmallTestMe.class, "1234567890");
        assertNotNull(keyedId.getName());
        assertNotNull(keyedId.getUid());

        assertTrue(SmallTestMe.class.getName().hashCode() == keyedId.getName());
        assertTrue("1234567890".hashCode() == keyedId.getUid());

        Integer name = 333;
        assertNotSame(name, keyedId.getName());
        keyedId.setName(name);
        assertEquals(name, keyedId.getName());

        assertNotSame(name, keyedId.getUid());
        keyedId.setUid(name);
        assertEquals(name, keyedId.getUid());
    }

    @Test
    public void testToString() {
        KeyedId keyedId = new KeyedId();
        assertNotNull(keyedId.toString());
    }
}
