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

package com.chen.candybon.fasttrack.exception;


/**
 * Exception for Invalid Data type thrown when the specified query or object does not follow the Data inheritance
 * required.
 *
 * Any data to store through the fast track API mut implement either
 * {@link com.chen.candybon.fasttrack.data.SmallData} or {@link com.chen.candybon.fasttrack.data.LargeData}. 
 * 
 * @author Xiaowei Chen
 */
public class DataClassException extends FastTrackException {

    /**
     * {@inheritDoc}
     *
     * @param msg The specific message tied to this exception.
     */
    public DataClassException(String msg) {
        super(msg);
    }

}
