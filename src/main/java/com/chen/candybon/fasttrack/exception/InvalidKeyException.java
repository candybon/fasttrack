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
 * This exception indicates that the specific key for a {@link com.chen.candybon.fasttrack.data.Data} object is not
 * properly specified.
 * 
 * @author Xiaowei Chen
 */
public class InvalidKeyException extends FastTrackException {

    /**
     * {@inheritDoc}
     *
     * @param msg the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     * method.
     */
    public InvalidKeyException(String msg) {
        super(msg);
    }

}
