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

import com.chen.candybon.fasttrack.exception.DataClassException;
import com.chen.candybon.fasttrack.exception.InvalidKeyException;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Large keyed object test
 */
public class LargeKeyedObjectTest {

    @Test
    public void testConstructor() throws InvalidKeyException, DataClassException {
        LargeKeyedObject lko = new LargeKeyedObject();
        assertNull(lko.getId());
        assertNull(lko.getIndex1());
        assertNull(lko.getIndex2());
        assertNull(lko.getIndex3());
        assertNull(lko.getIndex4());
        assertNull(lko.getIndex5());
        assertNull(lko.getIndex6());
        assertNull(lko.getValue());
        assertNull(lko.getOptLock());

        KeyedId key = new KeyedId(String.class, "1");
        lko.setId(key);
        lko.setIndex1("1".hashCode());
        lko.setIndex2("2".hashCode());
        lko.setIndex3("3".hashCode());
        lko.setIndex4("4".hashCode());
        lko.setIndex5("5".hashCode());
        lko.setIndex6("6".hashCode());

        assertEquals("1".hashCode(), lko.getIndex1().intValue());
        assertEquals("2".hashCode(), lko.getIndex2().intValue());
        assertEquals("3".hashCode(), lko.getIndex3().intValue());
        assertEquals("4".hashCode(), lko.getIndex4().intValue());
        assertEquals("5".hashCode(), lko.getIndex5().intValue());
        assertEquals("6".hashCode(), lko.getIndex6().intValue());

        lko.setValue("12345".getBytes());
        byte[] value = lko.getValue();
        assertEquals("12345", new String(value));

    }

    @Test
    public void testEqualsHashCode() {
        LargeKeyedObject lko = new LargeKeyedObject();
        LargeKeyedObject lko2 = new LargeKeyedObject();

        assertEquals(lko, lko2);
        assertEquals(lko.hashCode(), lko2.hashCode());

        lko.setIndex1("12345".hashCode());
        assertNotSame(lko, lko2);
        assertNotSame(lko.hashCode(), lko2.hashCode());        
    }

    @Test
    public void testToString()  {
        LargeKeyedObject lko = new LargeKeyedObject();
        assertNotNull(lko.toString());

    }
}
