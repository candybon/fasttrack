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
 * The class <code>FastTrackException</code> and its subclasses are a form of <code>Throwable</code> that indicates
 * conditions that a reasonable application might want to catch in the context of using the Fast Track service API.
 * <p/>
 * This class is the generic exception.
 *
 * @author Xiaowei Chen
 */
public class FastTrackException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The cause is not initialized, and may subsequently
     * be initialized by a call to {@link #initCause}.
     *
     * @param msg the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     * method.
     */
    public FastTrackException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.  <p>Note that the detail message
     * associated with <code>cause</code> is <i>not</i> automatically incorporated in this exception's detail message.
     *
     * @param msg the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param t the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
     * value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public FastTrackException(String msg, Throwable t) {
        super(msg, t);
    }
}
