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
import com.chen.candybon.fasttrack.dao.KoFastTrackDataService;
import com.chen.candybon.fasttrack.FastTrackDataService;
import com.chen.candybon.fasttrack.Predicate;
import com.chen.candybon.fasttrack.PredicateBuilder;
import com.chen.candybon.fasttrack.exception.DataClassException;
import com.chen.candybon.fasttrack.exception.DataException;
import com.chen.candybon.fasttrack.exception.FastTrackException;
import com.chen.candybon.fasttrack.exception.InvalidKeyException;
import com.chen.candybon.fasttrack.exception.InvalidPredicateException;
import com.chen.candybon.fasttrack.object.SmallKeyedObject;
import com.chen.candybon.fasttrack.type.Index;
import com.chen.candybon.fasttrack.utils.FastTrackTestFeatures;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * KoFastTrackDataService test.
 */
public class KoFastTrackDataServiceTest extends FastTrackTestFeatures {

    FastTrackDataService service = null;
    KeyedObjectDao dao = null;
    private static final String TEST_KEY = "1234567890";
    private static final String[] SETUP = {
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
        service = new KoFastTrackDataService();

        f = KoFastTrackDataService.class.getDeclaredField("dao");
        f.setAccessible(true);
        f.set(service, dao);
    }

    @Test
    public void testCRUD() throws Exception {

        // Nothing there
        LargeTestMe read = service.get(LargeTestMe.class, TEST_KEY);
        assertNull(read);

        // Create it
        beginTransaction();
        boolean ok = service.put(TEST_KEY, new LargeTestMe());
        commitTransaction();
        assertTrue(ok);

        read = service.get(LargeTestMe.class, TEST_KEY);
        assertNotNull(read);


        read.setTest("Updated Test");

        beginTransaction();
        boolean ok2 = service.update(TEST_KEY, read);
        commitTransaction();
        assertTrue(ok2);

        read = service.get(LargeTestMe.class, TEST_KEY);
        assertNotNull(read);
        assertEquals(read.getTest(), "Updated Test");

    }

    @Test
    public void testWrongType() throws FastTrackException {
        try {
            beginTransaction();
            service.put(TEST_KEY, "Type not implements neither SmallData nor Large");
            commitTransaction();
            fail("Invalid class type.");
        } catch (DataClassException ex) {
            rollbackTransaction();
        }

        try {
            beginTransaction();
            service.put(TEST_KEY, "".getBytes());
            commitTransaction();
            fail("Nothing to store (empty) should fail.");
        } catch (FastTrackException ex) {
            rollbackTransaction();
        }
    }

    @Test
    public void testWithNulls() throws FastTrackException {

        try {
            service.put(null, null);
            fail("really wrong.");
        } catch (InvalidKeyException e) {
        }

        try {
            service.put(null, LargeTestMe.class);
            fail("Not a valid key nor data.");
        } catch (InvalidKeyException e) {
        }

        try {
            service.put(null, new LargeTestMe());
            fail("Not a valid key.");
        } catch (InvalidKeyException e) {
        }

        try {
            service.put(TEST_KEY, null);
            fail("No valid data instance.");
        } catch (DataException e) {
        }

        try {
            service.get(null, null);
            fail("No valid key.");
        } catch (DataClassException e) {
        }

        try {
            service.get(LargeTestMe.class, null);
            fail("No valid key.");
        } catch (InvalidKeyException e) {
        }

        try {
            service.get(null, TEST_KEY);
            fail("Not a valid class.");
        } catch (DataClassException e) {
        }

        try {
            service.update(null, null);
            fail("Not a valid key.");
        } catch (InvalidKeyException e) {
        }

        try {
            service.update("key", null);
            fail("Not a valid class.");
        } catch (DataException e) {
        }

        try {
            service.update(null, new LargeTestMe());
            fail("Not a valid key.");
        } catch (InvalidKeyException e) {
        }

        try {
            service.update(null, null);
            fail("Not a valid key.");
        } catch (InvalidKeyException e) {
        }

        try {
            service.delete(null, null);
        } catch (DataClassException e) {
        }

        try {
            service.delete(LargeTestMe.class, null);
        } catch (InvalidKeyException e) {
        }

        try {
            service.delete(null, TEST_KEY);
        } catch (DataClassException e) {
        }

        //service.find()

        // Try put wih null indexes... Will work.
        beginTransaction();
        assertTrue(service.put(TEST_KEY, new LargeTestMe()));
        commitTransaction();

    }

    @Test
    public void testFindByBatch() throws FastTrackException {

        SmallTestMe stm = new SmallTestMe("1");
        SmallTestMe stm2 = new SmallTestMe("2");

        // Create it
        beginTransaction();
        assertTrue(service.put(TEST_KEY + "1", stm));
        assertTrue(service.put(TEST_KEY + "2", stm2));
        commitTransaction();

        List<SmallTestMe> stms = service.find(SmallTestMe.class, 0, 10);
        assertEquals(2, stms.size());

        stms = service.find(SmallTestMe.class, 1, 10);
        assertEquals(1, stms.size());
        assertEquals(stm2, stms.get(0));


    }

    @Test
    public void searchable() throws FastTrackException {
        SmallSearch user = new SmallSearch("Chen", "SW-Dev", 10);

        // Nothing there
        SmallSearch read = service.get(SmallSearch.class, user.getName());
        assertNull(read);

        beginTransaction();
        boolean ok = service.put(user.getName(), user);
        commitTransaction();
        assertTrue(ok);

        PredicateBuilder pb = new PredicateBuilder();
        pb.addAND(Index.index1, "Chen");

        List<SmallSearch> result = service.find(SmallSearch.class, pb.toPredicate());

        assertEquals(1, result.size());
        assertEquals("SW-Dev", result.get(0).getTitle());

        service.delete(SmallSearch.class, "Chen");
    }

    @Test
    public void searchableFieldIngnored() throws FastTrackException {
        SmallSearch user = new SmallSearch("Chen", "SW-Dev", 10);

        // Nothing there
        SmallSearch read = service.get(SmallSearch.class, user.getName());
        assertNull(read);

        beginTransaction();
        boolean ok = service.put(user.getName(), user, "CitiGroup");
        commitTransaction();
        assertTrue(ok);

        //could not find the result, because the field is ignored to set as index
        PredicateBuilder pb = new PredicateBuilder();
        pb.addAND(Index.index1, "Chen");
        List<SmallSearch> result = service.find(SmallSearch.class, pb.toPredicate());
        assertEquals(0, result.size());

        //could only search by "CitiGroup"
        pb = new PredicateBuilder();
        pb.addAND(Index.index1, "CitiGroup");

        result = service.find(SmallSearch.class, pb.toPredicate());

        assertEquals(1, result.size());
        assertEquals("Chen", result.get(0).getName());
        assertEquals("SW-Dev", result.get(0).getTitle());

        service.delete(SmallSearch.class, "Chen");
    }


    @Test
    public void wrongSearchable() {
        WrongSearchable user = new WrongSearchable("Chen", "SW-Dev", 10);
        beginTransaction();
        boolean ok = false;
        try {
            ok = service.put(user.getName(), user);
        } catch (FastTrackException ex) {
            assertEquals("Index index1 declaration is missing.", ex.getMessage());
        }
        commitTransaction();
        assertFalse(ok);
    }

    @Test
    public void duplicatedSearchable() {
        DuplicateIndexSearchable user = new DuplicateIndexSearchable("Chen", "SW-Dev", 10);
        beginTransaction();
        boolean ok = false;
        try {
            ok = service.put(user.getName(), user);
        } catch (FastTrackException ex) {
            assertEquals("Duplicated Declaration of Index index1 on field title", ex.getMessage());
        }
        commitTransaction();
        assertFalse(ok);
    }

    @Test
    public void testPutWithInvalidConditions() {

        try {
            // Create it with too many indexes
            service.put(TEST_KEY, new LargeTestMe(), "idx1", "idx2", "idx3", "idx4", "idx5", "idx6", "onetoomany");
            fail("max 6 indexes are allowed");
        } catch (FastTrackException e) {
            assertTrue(e instanceof DataException);
        }

    }

    @Test
    public void testUpdateWithInvalidConditions() {

        try {
            // Create it with too many indexes
            service.update(TEST_KEY, new LargeTestMe(), "idx1", "idx2", "idx3", "idx4", "idx5", "idx6", "onetoomany");
            fail("max 6 indexes are allowed");
        } catch (FastTrackException e) {
            assertTrue(e instanceof DataException);
        }

    }


    @Test
    public void test() {
        PredicateBuilder builder = new PredicateBuilder();
        Predicate predicate = null;
        try {
            builder.addAND(Index.index1, "index1")
                    .addOR(Index.index3, "index3")
                    .addAND(Index.index2, "index2")
                    .addOR(Index.index4, "index4")
                    .setFirstResult(0)
                    .setMaxResults(10);
            predicate = builder.toPredicate();
        } catch (InvalidPredicateException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            dao.search(SmallKeyedObject.class, "MyData", predicate);
        } catch (FastTrackException e) {
            e.printStackTrace();
        }


    }

}
