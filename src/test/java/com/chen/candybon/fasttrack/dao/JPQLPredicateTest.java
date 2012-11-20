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

import com.chen.candybon.fasttrack.exception.InvalidPredicateException;
import org.junit.Test;

import static org.junit.Assert.fail;

public class JPQLPredicateTest {

    @Test
    public void testExceptions() {
        JPQLPredicate p = new JPQLPredicate();

        try{
            p.setFirstResult(-1);
            fail("-1 is not a valid value");
        } catch (InvalidPredicateException e){
        }

        try{
            p.setMaxResults(-1);
            fail("-1 is not a valid value");
        } catch (InvalidPredicateException e){
        }
    }
}
