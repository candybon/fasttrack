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

import com.chen.candybon.fasttrack.Predicate;
import com.chen.candybon.fasttrack.exception.InvalidPredicateException;
import com.chen.candybon.fasttrack.type.Index;
import com.chen.candybon.fasttrack.type.Operand;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Predicate that uses the Java Persistence Query Language to generate a javax.persistence.Query.
 * 
 * @author Xiaowei Chen
 */
public class JPQLPredicate implements Predicate {
    static final int UNSET = -1;

    private int maxResultNumber = UNSET;
    private int firstResult = UNSET;

    private List<Operand> ops = null;
    private List<Index> indexes = null;
    private List<String> values = null;

    public JPQLPredicate() {
        ops = new ArrayList<Operand>();
        indexes = new ArrayList<Index>();
        values = new ArrayList<String>();
    }

    @Override
    public void add(Operand op, Index index, String value) {
        ops.add(op);
        indexes.add(index);
        values.add(value);
    }

    @Override
    public String toQuery() throws InvalidPredicateException {
        if (!ops.isEmpty() && ops.get(0) == Operand.OR) {
            throw new InvalidPredicateException("The first operand should not be OR.");
        }

        StringBuilder query = new StringBuilder(100);
        Operand op;
        Index index;
        String value;
        // First must be an AND
        query.append("ko.").append(indexes.get(0).name()).append('=').append(values.get(0).hashCode());


        for (int i = 1; i < ops.size(); i++) {
            op = ops.get(i);
            index = indexes.get(i);
            value = values.get(i);

            if (op == Operand.AND) {
                query.append(" AND ");
            } else {
                query.append(" OR ");
            }
            query.append("ko.").append(index.name()).append("=").append(value.hashCode());
        }
        return query.toString();
    }

    @Override
    public void setMaxResults(int max) throws InvalidPredicateException {
        if (max < 0) {
            throw new InvalidPredicateException("Max Result number should not be negative.");
        }
        this.maxResultNumber = max;
    }

    @Override
    public int getMaxResults() {
        return this.maxResultNumber;
    }

    @Override
    public void setFirstResult(int position) throws InvalidPredicateException {
        if (position < 0) {
            throw new InvalidPredicateException("First result number should not be negative.");
        }
        this.firstResult = position;
    }

    @Override
    public int getFirstResult() {
        return this.firstResult;
    }

    @Override
    public boolean isEmpty() {
        return ops.isEmpty() && maxResultNumber == UNSET && firstResult == UNSET;
    }
}
