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

import com.chen.candybon.fasttrack.dao.KeyedObjectDao;
import com.chen.candybon.fasttrack.PredicateBuilder;
import com.chen.candybon.fasttrack.exception.DataClassException;
import com.chen.candybon.fasttrack.exception.DataException;
import com.chen.candybon.fasttrack.exception.FastTrackException;
import com.chen.candybon.fasttrack.exception.InvalidKeyException;
import com.chen.candybon.fasttrack.exception.InvalidPredicateException;
import com.chen.candybon.fasttrack.exception.NotFoundException;
import com.chen.candybon.fasttrack.object.KeyedId;
import com.chen.candybon.fasttrack.object.KeyedObject;
import com.chen.candybon.fasttrack.object.LargeKeyedObject;
import com.chen.candybon.fasttrack.object.SmallKeyedObject;
import com.chen.candybon.fasttrack.type.Index;
import com.chen.candybon.fasttrack.utils.FastTrackTestFeatures;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Key Dao test cases
 */
public class KeyedObjectDaoTest extends FastTrackTestFeatures {

    KeyedObjectDao dao = null;
    private static final String TEST_OBJ = "1234567890";
    private final static String[] SETUP = {
            "DELETE FROM SMALL_KEYED_OBJECT;",
            "DELETE FROM LARGE_KEYED_OBJECT;"};

    @Before
    public void setup() throws Exception {
        dao = new KeyedObjectDao();
        Field f = dao.getClass().getDeclaredField("em");
        f.setAccessible(true);
        f.set(dao, getEntityManager());


        for (String cmd : SETUP) {
            beginTransaction();
            try {
                getEntityManager().createNativeQuery(cmd).executeUpdate();
                commitTransaction();
            } catch (Exception e) {
                rollbackTransaction();
            }
        }
    }

    @Test
    public void testCRUD() throws Exception {
        // The test just stores a String, and manipulates it
        KeyedId key = new KeyedId(String.class, "uniqueKey");

        KeyedObject read = dao.get(SmallKeyedObject.class, key);
        assertNull(read);

        SmallKeyedObject keyObj = new SmallKeyedObject(key, TEST_OBJ.getBytes());

        beginTransaction();
        KeyedObject ko = dao.create(keyObj);
        commitTransaction();
        assertTrue(String.class.getName().hashCode() == ko.getId().getName());
        assertEquals(TEST_OBJ, new String(ko.getValue()));

        read = dao.get(SmallKeyedObject.class, key);
        assertTrue(String.class.getName().hashCode() == read.getId().getName());
        assertEquals(TEST_OBJ, new String(read.getValue()));

        beginTransaction();
        dao.update(SmallKeyedObject.class, key, "NewContent".getBytes());
        commitTransaction();

        read = dao.get(SmallKeyedObject.class, key);
        assertTrue(String.class.getName().hashCode() == read.getId().getName());
        assertEquals("NewContent", new String(read.getValue()));

        beginTransaction();
        dao.delete(SmallKeyedObject.class, key);
        commitTransaction();


        read = dao.get(SmallKeyedObject.class, key);
        assertNull(read);
    }

    @Test
    public void testGetValueSmall() throws InvalidKeyException, DataException, DataClassException {
        KeyedId key = new KeyedId(String.class, "uniqueKey");

        byte[] bytes = dao.getValue(SmallKeyedObject.class, key);
        assertNull(bytes);

        SmallKeyedObject keyObj = new SmallKeyedObject(key, TEST_OBJ.getBytes());

        beginTransaction();
        dao.create(keyObj);
        commitTransaction();

        bytes = dao.getValue(SmallKeyedObject.class, key);
        assertNotNull(bytes);
        assertEquals(TEST_OBJ, new String(bytes));
    }

    @Test
    public void testGetValueLarge() throws InvalidKeyException, DataException, DataClassException {
        KeyedId key = new KeyedId(String.class, "uniqueKey");

        byte[] bytes = dao.getValue(LargeKeyedObject.class, key);
        assertNull(bytes);

        LargeKeyedObject keyObj = new LargeKeyedObject(key, TEST_OBJ.getBytes());

        beginTransaction();
        dao.create(keyObj);
        commitTransaction();

        bytes = dao.getValue(LargeKeyedObject.class, key);
        assertNotNull(bytes);
        assertEquals(TEST_OBJ, new String(bytes));
    }

    @Test
    public void testFindWithPos() throws InvalidKeyException, DataException, DataClassException {
        List<KeyedObject> bytes = dao.search(SmallKeyedObject.class, String.class.getName(), 0, 2);
        assertNotNull(bytes);
        assertEquals(0, bytes.size());

        KeyedId key1 = new KeyedId(String.class, "uniqueKey1");
        SmallKeyedObject keyObj = new SmallKeyedObject(key1, "obj1".getBytes());

        KeyedId key2 = new KeyedId(String.class, "uniqueKey2");
        SmallKeyedObject keyObj2 = new SmallKeyedObject(key2, "obj2".getBytes());

        beginTransaction();
        dao.create(keyObj);
        dao.create(keyObj2);
        commitTransaction();

        bytes = dao.search(SmallKeyedObject.class, String.class.getName(), 0, 2);
        assertNotNull(bytes);
        assertEquals(2, bytes.size());

        bytes = dao.search(SmallKeyedObject.class, String.class.getName(), 1, 2);
        assertNotNull(bytes);
        assertEquals(1, bytes.size());
        assertEquals(key2, bytes.get(0).getId());
    }

    @Test
    public void testFindWithPredicate() throws FastTrackException {
        PredicateBuilder p = new PredicateBuilder();
        p.addAND(Index.index1, "something")
                .setFirstResult(0)
                .setMaxResults(100);

        List<KeyedObject> bytes = dao.search(LargeKeyedObject.class, String.class.getName(), p.toPredicate());
        assertNotNull(bytes);
        assertEquals(0, bytes.size());

        KeyedId key1 = new KeyedId(String.class, "uniqueKey1");
        LargeKeyedObject keyObj = new LargeKeyedObject(key1, "obj1".getBytes(), "something");

        KeyedId key2 = new KeyedId(String.class, "uniqueKey2");
        LargeKeyedObject keyObj2 = new LargeKeyedObject(key2, "obj2".getBytes(), "nothing");

        beginTransaction();
        dao.create(keyObj);
        dao.create(keyObj2);
        commitTransaction();

        bytes = dao.search(LargeKeyedObject.class, String.class.getName(), p.toPredicate());
        assertNotNull(bytes);
        assertEquals(1, bytes.size());

    }

    @Test
    public void testUpdateWithInvalid() {
        try {
            dao.update(SmallKeyedObject.class, new KeyedId(), null);
            fail("Nothing to update");
        } catch (NotFoundException e) {
            // OK.
        }
        try {
            dao.update(SmallKeyedObject.class, new KeyedId(), "BLA".getBytes());
            fail("No such object to update");
        } catch (NotFoundException e) {
            // OK.
        }
    }

    @Test
    public void testWithNulls() throws NotFoundException, InvalidPredicateException {

        dao.delete(null, null);
        dao.delete(SmallKeyedObject.class, null);
        dao.delete(null, new KeyedId());

        assertNull(dao.get(null, null));
        assertNull(dao.get(SmallKeyedObject.class, null));
        assertNull(dao.get(null, new KeyedId()));

        assertNull(dao.getValue(null, null));
        assertNull(dao.getValue(SmallKeyedObject.class, null));
        assertNull(dao.getValue(null, new KeyedId()));

        try {
            dao.update(null, null, null);
            fail("There is no such object");
        } catch (NotFoundException e) {
            // OK.
        }

        try {
            dao.update(SmallKeyedObject.class, null, null);
            fail("both key and value are null");
        } catch (NotFoundException e) {
            // OK.
        }

        try {
            dao.update(SmallKeyedObject.class, new KeyedId(), null);
            fail("Nothing to update");
        } catch (NotFoundException e) {
            // OK.
        }

        try {
            dao.create(null);
            fail("Trying to create null");
        } catch (DataException e) {
        }
        assertEquals(0, dao.search(null, null, null).size());
        assertEquals(0, dao.search(null, "someKey", null).size());
        PredicateBuilder pb = new PredicateBuilder();
        try {
            dao.search(null, "someKey", pb.toPredicate());
            fail("predicate is empty");
        } catch (InvalidPredicateException e) {
        }

        pb.addAND(Index.index1, "something");
        assertEquals(0, dao.search(LargeKeyedObject.class, null, pb.toPredicate()).size());

        assertEquals(0, dao.search(null, null, 0, 100).size());
        assertEquals(0, dao.search(SmallKeyedObject.class, null, 0, 100).size());

    }

}

