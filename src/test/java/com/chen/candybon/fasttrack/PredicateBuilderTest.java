/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chen.candybon.fasttrack;

import com.chen.candybon.fasttrack.exception.InvalidPredicateException;
import com.chen.candybon.fasttrack.type.Index;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Xiaowei Chen
 */
public class PredicateBuilderTest {
                                     
    final static String querySegment = "ko.index1=" + "IndexOne".hashCode() +
            " OR ko.index2=" + "IndexTwo".hashCode();    
     final static String queryEmptySegment = "ko.index1=" + "".hashCode() +
            " OR ko.index2=" + "".hashCode();
    final static int FIRST = 10;
    final static int MAX = 100;


    @Test
    public void testPredicate() throws InvalidPredicateException {
        PredicateBuilder builder = new PredicateBuilder();

        builder.addAND(Index.index1, "IndexOne")
                .addOR(Index.index2, "IndexTwo");

        Predicate predicate = builder.toPredicate();
        assertEquals(querySegment, predicate.toQuery());
    }

    @Test
    public void testWithEmptyValue() throws InvalidPredicateException {
        PredicateBuilder builder = new PredicateBuilder();

        // teh following is valid
        builder.addAND(Index.index1, "")
                .addOR(Index.index2, "");
        Predicate predicate = builder.toPredicate();
        assertEquals(queryEmptySegment, predicate.toQuery());
    }

    @Test
    public void testPredicateWithMax() throws InvalidPredicateException {
        PredicateBuilder builder = new PredicateBuilder();

        builder.addAND(Index.index1, "IndexOne")
                .addOR(Index.index2, "IndexTwo")
                .setFirstResult(FIRST)
                .setMaxResults(MAX);

        Predicate predicate = builder.toPredicate();


        assertEquals(querySegment, predicate.toQuery());
        assertEquals(FIRST, predicate.getFirstResult());
        assertEquals(MAX, predicate.getMaxResults());
    }

    @Test
    public void testWithNulls() {
        PredicateBuilder builder = new PredicateBuilder();

        builder.addAND(null, "IndexOne")
                .addAND(Index.index6, null)
                .addAND(null, null)
                .addOR(Index.index2, null)
                .addOR(null, "something")
                .addOR(null, null);

        try {
            builder.toPredicate();
            fail("no query to execute");
        } catch (InvalidPredicateException e) {
        }
    }

    @Test
    public void testEmpty() throws InvalidPredicateException {
        PredicateBuilder builder = new PredicateBuilder();

        try {
            builder.toPredicate();
            fail("no query to execute");
        } catch (InvalidPredicateException e) {
        }
        builder.setFirstResult(FIRST);
        assertNotNull(builder.toPredicate());

        builder = new PredicateBuilder();
        builder.setMaxResults(MAX);
        assertNotNull(builder.toPredicate());

        builder = new PredicateBuilder();
        builder.addAND(Index.index1, "something");
        assertNotNull(builder.toPredicate());

    }

    @Test
    public void testBadStatement() {
        PredicateBuilder builder = new PredicateBuilder();

        builder.addOR(Index.index1, "IndexOne")
                .addAND(Index.index2, "IndexTwo");
        try {
            builder.toPredicate().toQuery();
        } catch (InvalidPredicateException ex) {
            assertTrue(ex.getMessage().equals("The first operand should not be OR."));
        }

    }
}

