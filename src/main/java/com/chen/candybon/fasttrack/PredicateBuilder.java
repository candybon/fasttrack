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
package com.chen.candybon.fasttrack;

/**
 * @author Xiaowei Chen
 */

import com.chen.candybon.fasttrack.dao.JPQLPredicate;
import com.chen.candybon.fasttrack.exception.InvalidPredicateException;
import com.chen.candybon.fasttrack.type.Index;
import com.chen.candybon.fasttrack.type.Operand;

/**
 * The predicateBuilder is to use to produce a Predicate.
 * The conditions that need to be satisfied are composed by Operand(AND, OR) and index indication and value.
 *
 * The predicate builder is based on the Fluent design pattern.
 *
 * Be wary when doing a search using a predicate builder that the built query will not return too many instances of
 * a specific object.  The larger the data set to return the slower the query.  Also doing a search on an index with
 * a very low distribution of values will be expensive.
 *
 * @author exeicen
 */
public class PredicateBuilder {
    
    private Predicate predicate = null;

    /**
     * Constructor
     */
    public PredicateBuilder() {
        predicate = new JPQLPredicate();
    }

    /**
     * Append AND condition.
     *
     * If either index or value is null then nothing gets added.
     *
     * @param index search index
     * @param value value that the index should satisfy
     * @return A reference to this object.
     */
    public PredicateBuilder addAND(Index index, String value) {
        if (index == null || value == null) {
            return this;
        }
        predicate.add(Operand.AND, index, value);
        return this;
    }

    /**
     * Append OR condition
     *
     * If either index or value is null then nothing gets added.
     *
     * @param index search index
     * @param value value that the index should satisfy
     * @return A reference to this object.
     */
    public PredicateBuilder addOR(Index index, String value) {
        if (index == null || value == null) {
            return this;
        }
        predicate.add(Operand.OR, index, value);
        return this;
    }

    /**
     * Produce the Predicate
     * @return the predicate that satisfy all the conditions.
     * @throws com.chen.candybon.fasttrack.exception.InvalidPredicateException If the PredicateBuilder fails to generate a valid {@link Predicate}, like,
     * for example, if no conditions have been specified and the predicate is empty. 
     */
    public Predicate toPredicate() throws InvalidPredicateException {
        if (predicate.isEmpty()) {
            throw new InvalidPredicateException("Predicate is empty. No condition defined.");
        }
        return predicate;
    }

    /**
     * Set the maximum number of results to retrieve.
     * @param max The max number of results.
     * @return A reference to this object.
     * @throws com.chen.candybon.fasttrack.exception.InvalidPredicateException if argument is negative
     */
    public PredicateBuilder setMaxResults(int max) throws InvalidPredicateException {
        predicate.setMaxResults(max);
        return this;
    }

    /**
     * Set the position of the first result to retrieve.
     * @param position Starting position for the set.

     * @return A reference to this object.
     * @throws com.chen.candybon.fasttrack.exception.InvalidPredicateException if argument is negative
     */
    public PredicateBuilder setFirstResult(int position) throws InvalidPredicateException {
        predicate.setFirstResult(position);
        return this;
    }
}
