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
/**
 * @author Xiaowei Chen
 */
import com.chen.candybon.fasttrack.Predicate;
import com.chen.candybon.fasttrack.exception.DataException;
import com.chen.candybon.fasttrack.exception.InvalidPredicateException;
import com.chen.candybon.fasttrack.exception.NotFoundException;
import com.chen.candybon.fasttrack.object.KeyedId;
import com.chen.candybon.fasttrack.object.KeyedObject;

import java.util.List;

/**
 * DAO interface for manipulating the {@link KeyedObject} entity object.
 */
public interface LocalKeyedObjectDao {

    /**
     * Persist the object to data store.
     *
     * @param t A non null {@link KeyedObject}.
     * @return The persisted entity.
     * @throws DataException In case the parameter is null.
     */
    KeyedObject create(KeyedObject t) throws DataException;

    /**
     * Returns the keyed object associated with this id and class.
     *
     * @param tClass One of SmallKeyedObject or LargeKeyedObject.
     * @param id A unique {@link KeyedId}.
     * @return The Object is one if found, otherwise null.
     */
    KeyedObject get(Class<? extends KeyedObject> tClass, KeyedId id);

    /**
     * This checks that a valid object is persisted at in the Class table based on the {@link KeyedId}.  If such an
     * object is found, then the byte[] is inserted there, and the indexes updated.
     *
     * @param tClass The Class that is affected either {@link com.chen.candybon.fasttrack.object.SmallKeyedObject} or
     * {@link com.chen.candybon.fasttrack.object.LargeKeyedObject}.
     * @param id The unique id.
     * @param value What to store as a byte array.
     * @param indexes The indexes to update for this instance.
     * @return The updated persisted object.
     * @throws NotFoundException In case no such object is found.
     */
    KeyedObject update(Class<? extends KeyedObject> tClass, KeyedId id, byte[] value, String... indexes)
            throws NotFoundException;

    /**
     * Delete the keyed object specified by this class and {@link KeyedId}.
     *
     * @param tClass One of SmallKeyedObject or LargeKeyedObject.
     * @param id A unique id.
     */
    void delete(Class<? extends KeyedObject> tClass, KeyedId id);

    /**
     * Returns only the value part of the {@link KeyedObject} ie the serialized  instance.
     *
     * @param toUse The class name to use
     * @param keyedId A unique key for this object.
     * @return The serialized content of the KeyedObject.
     */
    byte[] getValue(Class toUse, KeyedId keyedId);

    /**
     * Used to search instance of tClass using {@link KeyedId} name part (keyIdName) as the object name marker
     * that respect the {@link Predicate} specified as argument.
     *
     * @param tClass One of SmallKeyedObject or LargeKeyedObject.
     * @param keyIdName The {@link KeyedId} name part of the unique key for objects to look up.
     * @param predicate a valid {@link Predicate}.
     * @return A {@link List} of instances.
     * @throws InvalidPredicateException If the predicate is not valid (like for example if it is empty).
     */
    List<KeyedObject> search(Class<? extends KeyedObject> tClass, String keyIdName, Predicate predicate)
            throws InvalidPredicateException;

    /**
     * Returns a maximum number (max parameter) of instances of the objects with key name as specified
     * from a starting position.
     *
     * @param toUse The class name to use
     * @param keyName The associated serialized data key name
     * @param position Starting position.
     * @param max Max number of results
     * @return A {@link List} of instances.
     */
    List<KeyedObject> search(Class toUse, String keyName, int position, int max);
}
