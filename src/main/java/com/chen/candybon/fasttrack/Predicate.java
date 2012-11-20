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

import com.chen.candybon.fasttrack.exception.InvalidPredicateException;
import com.chen.candybon.fasttrack.type.Index;
import com.chen.candybon.fasttrack.type.Operand;

/**
 * Sometimes it is inconvenient or impossible to describe a set by listing all of its elements. Another useful way
 * to define a set is by specifying a property that the elements of the set have in common. The notation P(x) is used
 * to denote a sentence or statement P concerning the variable object x. The set defined by P(x) written
 *  {x | P(x)}, is just a collection of all the objects for which P is sensible and true.
 *
 * For instance, {x | x is a positive integer less than 4} is the set {1,2,3}.
 *
 * Thus, an element of the set {x | P(x)}, is an object t for which the statement P(t) is true. With such statements,
 *  P(x) is referred to as the Predicate (making x the subject of the proposition). P(x) is also referred to as
 * a propositional function, as each choice of x produces a proposition (as a result of the function P(x)) that is
 * either true or false.
 * 
 * @author exeicen
 */
public interface Predicate {
    /**
     * Append a condition to this predicate
     * @param op Operand indicating what kind of association about the condition
     * @param index Index specifying which index is the targeting index.
     * @param value The value to be satisfied.
     */
    void add(Operand op, Index index, String value);

    /**
     * Produce predicate content in string format
     * @return String indicating the predicate
     * @throws com.chen.candybon.fasttrack.exception.InvalidPredicateException in case of wrong combination of the conditions.
     */
    String toQuery() throws InvalidPredicateException;

    /**
     * Set the maximum number of results to retrieve.
     * @param max maxResult 
     * @throws com.chen.candybon.fasttrack.exception.InvalidPredicateException if argument is negative
     */
    void setMaxResults(int max) throws InvalidPredicateException;

    /**
     * Get the maximum number of results to retrieve.
     * @return the maximum number
     */
    int getMaxResults();

    /**
     * Set the position of the first result to retrieve.
     * @param position starting position.
     * @throws com.chen.candybon.fasttrack.exception.InvalidPredicateException if argument is negative
     */
    void setFirstResult(int position) throws InvalidPredicateException;

    /**
     * Get the number of starting results to retrieve.
     *
     * @return Starting position.
     */
    int getFirstResult();

    /**
     * Indicates if the Predicate contains any conditions to apply to the search.
     *
     * @return True if at least one index has been set, or first result is set or max result is set.
     */
    boolean isEmpty();
}
