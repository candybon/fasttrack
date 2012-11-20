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
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Large keyed object test
 */
public class SmallKeyedObjectTest {

    @Test
    public void testConstructor() throws InvalidKeyException, DataClassException {
        SmallKeyedObject sko = new SmallKeyedObject();
        assertNull(sko.getId());
        assertNull(sko.getIndex1());
        assertNull(sko.getIndex2());
        assertNull(sko.getIndex3());
        assertNull(sko.getIndex4());
        assertNull(sko.getIndex5());
        assertNull(sko.getIndex6());
        assertNull(sko.getValue());
        assertNull(sko.getOptLock());

        KeyedId key = new KeyedId(String.class, "1");
        sko.setId(key);
        sko.setIndex1("1".hashCode());
        sko.setIndex2("2".hashCode());
        sko.setIndex3("3".hashCode());
        sko.setIndex4("4".hashCode());
        sko.setIndex5("5".hashCode());
        sko.setIndex6("6".hashCode());

        assertEquals("1".hashCode(), sko.getIndex1().intValue());
        assertEquals("2".hashCode(), sko.getIndex2().intValue());
        assertEquals("3".hashCode(), sko.getIndex3().intValue());
        assertEquals("4".hashCode(), sko.getIndex4().intValue());
        assertEquals("5".hashCode(), sko.getIndex5().intValue());
        assertEquals("6".hashCode(), sko.getIndex6().intValue());

        sko.setValue(Base64.encode("12345".getBytes()));
        byte[] value = sko.getValue();
        assertEquals("12345", new String(value));

        sko.setValue(null);
        assertNull(sko.getValue());        
    }

    @Test
    public void testToString()  {
        SmallKeyedObject lko = new SmallKeyedObject();
        assertNotNull(lko.toString());

    }
}
