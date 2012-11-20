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
 * An exception associated with the data to persist.  This exception will typically be caused by a serialization
 * error.
 * 
 * @author Xiaowei Chen
 */
public class DataException extends FastTrackException  {

    /**
     * {@inheritDoc}
     *
     * @param msg The specific message associated with this error.
     */
    public DataException(String msg) {
        super(msg);
    }

    /**
     * {@inheritDoc}
     *
     * @param msg the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param t the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     * value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public DataException(String msg, Throwable t) {
        super(msg, t);
    }
}
