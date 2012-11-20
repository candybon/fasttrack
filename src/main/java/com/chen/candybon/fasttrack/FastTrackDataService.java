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

import com.chen.candybon.fasttrack.exception.FastTrackException;

import javax.ejb.Local;
import java.util.List;

/**
 * Fast track service is used to manage fast track instances and to persist them.
 *
 * It can be used with object that are implementing either {@link com.chen.candybon.fasttrack.data.SmallData}  or
 * {@link com.chen.candybon.fasttrack.data.LargeData} interfaces.  The choice of one or the other is dependent on
 * the size of the data.  The fast track data is serialized as such, the users must select the proper type (Small or
 * Large) as the Small is limited to roughly 8Kb.
 *
 * Note: At this point the API does not do a pre-check of the data before trying to persist it.  And the API supposes
 * that the data stored is always stored as either SmallData or LargeData type not a mix. In other words
 *  {@link com.chen.candybon.fasttrack.data.SmallData} and {@link com.chen.candybon.fasttrack.data.LargeData} are
 * mutually exclusive and should not be used on the same class.
 *
 * This interface exposes the main methods for managing data (CRUD) in the form of:
 * <ul>
 * <li>{@link #put}: Used to insert one element into the data store.</li>
 * <li>{@link #get}: Used to retrieve one element from the data store.</li>
 * <li>{@link #delete}: Used to delete one element from the data store.</li>
 * <li>{@link #update}: Used to update one element in the data store.</li>
 * <li>{@link #find}: Used to search for some data based on a {@link Predicate}, ie a combination of
 * indexes.</li>
 * </ul>
 *
 * Data to be persisted through this API should typically be of a Key/Value kind.  In other words, the
 * implementation is geared towards high throughput of reads where a primary key to an object is known. Any attempts
 * to search for data will be slower than a direct key access.
 *
 * During the insertion of data, you can specify additional information to categorize or index this instance.  This
 * is useful in order to perform search or aggregate multiple rows for one data type.
 *
 * Hint: If an index value is specified for many entries (ie it is the same for most objects during the {@link #put},
 * then this index will give very bad performance when trying to perform a search that includes this
 * index. When building a {@link Predicate} for a {@link #find} it is recommended to use a combination of indexes
 * that minimizes the amount of data to retrieve.
 *
 * Example:
 * <pre>
 *    for(int i = 0; i < 100000; i ++){
 *           MyData data = new MyData(i);
 *           ft-service.put("key" + i, data, "myindex" , "good" +i);
 *          // use index1 always equal to "myindex
 *          // use index2 equals to "good" + i -> unique value for each key
 *     }
 * </pre>
 * In the above example, using a when constructing a Predicate using index1:
 * <pre>
 *  PredicateBuilder builder = new PredicateBuilder();
 * 	builder.addAND(Index.index1, "myindex")
			.addOR(Index.index2, "good3");
 * </pre>
 * The search will be slow as, in the example, "myindex" is true for all entries of MyData.
 *
 * Hint: As a recommendation when building a search try to use indexes that have a low hit rate in the data set. 
 *
 * Finally, if different applications are using this API to store different data instances, in order not to mix
 * data it is important that each application defines its own implementation of
 * {@link com.chen.candybon.fasttrack.data.SmallData} or {@link com.chen.candybon.fasttrack.data.LargeData} as
 * the class name is used as part of the hashing to define the primary key of a data instance. 
 *
 */
@Local
public interface FastTrackDataService {

    /**
     * Used to insert some data in the database. Think of this as a {@link java.util.Map#put}
     *
     * Associates the specified {@link T} instance with the specified key.  If the data store previously contained
     * an identical {@link T} instance of the same type and key, then the insertion will fail with an exception.
     *
     * @param key key with which the specified data instance is to be associated
     *
     * @param data An class instance (object) that implements either
     * {@link com.chen.candybon.fasttrack.data.SmallData} or {@link com.chen.candybon.fasttrack.data.LargeData} to
     * be associated with the specified key. Fields annotated as
     * Searchable{@link com.chen.candybon.fasttrack.Searchable} will be used as index for search function.
     *
     * @param indexes String var arg (similar to a String[]) that can be used to specify one(1) to six(6) indexes to
     * create for this object.
     * Note: if you specify these parameters, all fields annotated
     * as Searchable{@link com.chen.candybon.fasttrack.Searchable} will be ignored, and not to be stored as indexes.
     * Otherwise, if you did not specify any string variables as parameter, the fields annotated as
     * Searchable{@link com.chen.candybon.fasttrack.Searchable} will be used as indexes instead.
     *
     * @return True if the insertion is successful, false if not.
     *
     * @throws FastTrackException In case of an
     * invalid key {@link com.chen.candybon.fasttrack.exception.InvalidKeyException},
     * invalid class type {@link com.chen.candybon.fasttrack.exception.DataClassException}
     * or anything wrong with data {@link com.chen.candybon.fasttrack.exception.DataException}
     */
    <T> boolean put(String key, T data, String... indexes) throws FastTrackException;

    /**
     * Used to find data from the database by using key. Think of this as a {@link java.util.Map#remove}
     *
     * Get the instance of {@link T} with the specified key.
     *
     * @param tClass An class that implements either {@link com.chen.candybon.fasttrack.data.SmallData}
     * or {@link com.chen.candybon.fasttrack.data.LargeData} to be associated with the specified key.
     *
     * @param key key with which the specified data instance is to be associated
     *
     * @return The persisted object. Null if not found.
     *
     * @throws FastTrackException In case of an invalid key
     * {@link com.chen.candybon.fasttrack.exception.InvalidKeyException},
     * in case of an invalid class {@link com.chen.candybon.fasttrack.exception.DataClassException},
     * or if anything else goes wrong, eg. De-serialization
     * {@link com.chen.candybon.fasttrack.exception.DataException}.
     * 
     */
    <T> T get(Class<T> tClass, String key) throws FastTrackException;

    /**
     * Removes the data for this key from this database if it is present. Think of this as a {@link java.util.Map#get}
     *
     * @param tClass An class that implements either {@link com.chen.candybon.fasttrack.data.SmallData}
     * or {@link com.chen.candybon.fasttrack.data.LargeData} to be associated with the specified key.
     *
     * @param key key with which the specified data instance is to be associated
     *
     * @throws FastTrackException In case of an
     * invalid key {@link com.chen.candybon.fasttrack.exception.InvalidKeyException},
     * or in case of an invalid class {@link com.chen.candybon.fasttrack.exception.DataClassException}.
     */
    <T> void delete(Class<T> tClass, String key) throws FastTrackException;

    /**
     * Update the data for this key from this database if it is present. If not present in db,
     * it throws NotFoundException {@link com.chen.candybon.fasttrack.exception.NotFoundException}.
     * It is important to specify the indexes to use for this object, as the indexes will also be updated.
     *
     * @param key key with which the specified data instance is to be associated
     *
     * @param data An class instance (object) that implements either
     * {@link com.chen.candybon.fasttrack.data.SmallData} or {@link com.chen.candybon.fasttrack.data.LargeData} to
     * be associated with the specified key. Fields annotated as
     * Searchable{@link com.chen.candybon.fasttrack.Searchable} will be used as index for search function.
     *
     * @param indexes String var arg (similar to a String[]) that can be used to specify one(1) to six(6) indexes to
     * create for this object.
     * Note: if you specify these parameters, all fields annotated
     * as Searchable{@link com.chen.candybon.fasttrack.Searchable} will be ignored, and not to be stored as indexes.
     * Otherwise, if you did not specify any string variables as parameter, the fields annotated as
     * Searchable{@link com.chen.candybon.fasttrack.Searchable} will be used as indexes instead.
     *
     * @return True if the insertion is successful, false if not.
     *
     * @throws FastTrackException In case of an
     * invalid key {@link com.chen.candybon.fasttrack.exception.InvalidKeyException},
     * in case of an invalid class {@link com.chen.candybon.fasttrack.exception.DataClassException},
     * or Object not found {@link com.chen.candybon.fasttrack.exception.NotFoundException},
     * or if anything goes wrong, eg. Serialization {@link com.chen.candybon.fasttrack.exception.DataException}.

     */
    <T> boolean update(String key, T data, String... indexes)
            throws FastTrackException;

    /**
     * Search for the data of the specified Class, which satisfy the Criteria specified in QueryBuilder. The result is
     * a collection of a maximum number (default is 20) of records. The paging information can also be specified in
     * QueryBuilder to specify different result sets.
     *
     * @param tClass An class that implements either {@link com.chen.candybon.fasttrack.data.SmallData}
     * or {@link com.chen.candybon.fasttrack.data.LargeData} to be associated with the specified key.
     *
     * @param predicate Define a set of conditions that the elements of the response have in common,
     * which specify the criteria that should be satisfied in search. e.g. indexes combination, maximum number
     * of record per result set, starting position of the result.
     *
     * @return The a maximum number (default is 20) of result set according to QueryBuilder
     *
     * @throws FastTrackException In case of an
     * class {@link com.chen.candybon.fasttrack.exception.DataClassException},
     * bad queryStatement {@link com.chen.candybon.fasttrack.exception.InvalidPredicateException}
     * or if anything goes wrong, eg. Serialization {@link com.chen.candybon.fasttrack.exception.DataException}.
     */
    <T> List<T> find(Class<T> tClass, Predicate predicate)
            throws FastTrackException;

    /**
     * Search for the a maximum number of {@link T} data from a specific position.
     *
     * @param tClass An class that implements either {@link com.chen.candybon.fasttrack.data.SmallData}
     * or {@link com.chen.candybon.fasttrack.data.LargeData} to be associated with the specified key.
     * @param startingPosition Starting position.
     *
     * @param max Max number of results.
     *
     * @return A {@link List} of T with a maximum size of max.
     *
     * @throws FastTrackException In case of an
     * class {@link com.chen.candybon.fasttrack.exception.DataClassException},
     * or if anything goes wrong, eg. Serialization {@link com.chen.candybon.fasttrack.exception.DataException}.
     */
    <T> List<T> find(Class<T> tClass, int startingPosition, int max) throws FastTrackException;

}
