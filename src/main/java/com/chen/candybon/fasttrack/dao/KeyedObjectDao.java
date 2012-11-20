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

import com.chen.candybon.common.utils.StringUtils;
import com.chen.candybon.fasttrack.Predicate;
import com.chen.candybon.fasttrack.exception.DataException;
import com.chen.candybon.fasttrack.exception.InvalidPredicateException;
import com.chen.candybon.fasttrack.exception.NotFoundException;
import com.chen.candybon.fasttrack.object.KeyedId;
import com.chen.candybon.fasttrack.object.KeyedObject;
import com.chen.candybon.fasttrack.object.LargeKeyedObject;
import com.chen.candybon.fasttrack.object.SmallKeyedObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Dao implementation class
 */
@Stateless
public class KeyedObjectDao implements LocalKeyedObjectDao {

    /**
     * Logger instance
     */

    private static final Logger LOG = LoggerFactory.getLogger(KeyedObjectDao.class);
    @PersistenceContext(unitName = "keyservice")
    private EntityManager em = null;

    /**
     * {@inheritDoc}
     *
     * @param ko The object to persist.
     * @return The persisted object.
     * @throws DataException In case the instance to store is null.
     */
    @Override
    public KeyedObject create(KeyedObject ko) throws DataException {
        if (ko == null) {
            LOG.debug("Attempting to create a null object -> Rejecting.");
            throw new DataException("Nothing to create. Provide a non-null KeyedObject instance.");
        }
        em.persist(ko);
        em.flush();
        return ko;
    }

    /**
     * {@inheritDoc}
     *
     * @param tClass One of SmallKeyedObject or LargeKeyedObject.
     * @param id A unique {@link KeyedId}.
     * @return The persisted object or null.
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public KeyedObject get(final Class<? extends KeyedObject> tClass, final KeyedId id) {
        if (tClass == null || id == null) {
            // nothing to do
            return null;
        }
        return em.find(tClass, id);
    }

    /**
     * {@inheritDoc}
     *
     * @param tClass The Class that is affected either {@link com.chen.candybon.fasttrack.object.SmallKeyedObject} or
     * {@link com.chen.candybon.fasttrack.object.LargeKeyedObject}.
     * @param id The unique id.
     * @param value What to store as a byte array.
     * @param indexes The indexes to update for this instance.
     * @return The updated object.
     * @throws NotFoundException
     */
    @SuppressWarnings("unchecked")
    @Override
    public KeyedObject update(final Class<? extends KeyedObject> tClass, final KeyedId id,
                              final byte[] value, String... indexes) throws NotFoundException {
        KeyedObject ko = get(tClass, id);
        if (ko == null) {
            throw new NotFoundException("No such object with id: " + id);
        }
        if (ko instanceof LargeKeyedObject) {
            ((LargeKeyedObject) ko).setValue(value);
        } else {
            ((SmallKeyedObject) ko).setValue(Base64.encode(value));
        }
        ko.fillIndexes(indexes);
        em.merge(ko);
        return ko;
    }

    /**
     * {@inheritDoc}
     *
     * @param tClass One of SmallKeyedObject or LargeKeyedObject.
     * @param id A unique id.
     */
    @Override
    public void delete(final Class<? extends KeyedObject> tClass, KeyedId id) {
        KeyedObject ko = get(tClass, id);
        if (ko != null) {
            LOG.debug("Removing Keyed object: {}", id);
            em.remove(ko);
        }
    }


    /**
     * {@inheritDoc}
     *
     * @param tClass One of SmallKeyedObject or LargeKeyedObject.
     * @param keyIdName The {@link KeyedId} name part of the unique key for objects to look up.
     * @param predicate a valid {@link Predicate}.
     * @return A List of KeyedObject instances.
     * @throws InvalidPredicateException
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<KeyedObject> search(final Class<? extends KeyedObject> tClass,
                                    final String keyIdName,
                                    final Predicate predicate) throws InvalidPredicateException {
        if (StringUtils.isEmptyOrNull(keyIdName)) {
            return new ArrayList<KeyedObject>(0);
        }
        StringBuilder query = new StringBuilder(100);        
        if (LargeKeyedObject.class.equals(tClass)) {
            query.append("SELECT DISTINCT ko FROM LargeKeyedObject ko WHERE ko.id.name =");
        } else {
            query.append("SELECT DISTINCT ko FROM SmallKeyedObject ko WHERE ko.id.name =");
        }
        query.append(keyIdName.hashCode());

        if (predicate != null) {
            query.append(" AND (").append(predicate.toQuery()).append(')');
        }

        // LOG.info("Query to run:" + query);
        Query q = em.createQuery(query.toString());
        if (predicate != null) {
            if (predicate.getMaxResults() >= 0) {
                q.setMaxResults(predicate.getMaxResults());
            }
            if (predicate.getFirstResult() >= 0) {
                q.setFirstResult(predicate.getFirstResult());
            }
        }

        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     *
     * @param toUse The class name to use
     * @param keyedId A unique key for this object.
     * @return The serialized byte array stored.
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public byte[] getValue(Class toUse, KeyedId keyedId) {
        if (toUse == null || keyedId == null) {
            LOG.debug("No class ({}) or no key({}) specified -> returning null.", toUse, keyedId);
            return null;
        }
        try {
            Query q;
            if (LargeKeyedObject.class.equals(toUse)) {
                q = em.createNamedQuery("LargeKeyedObject.findByKeyId");
                q.setParameter("id", keyedId);
                return ((LargeKeyedObject)q.getSingleResult()).getValue();
            } else {
                q = em.createNamedQuery("SmallKeyedObject.getValue");
                q.setParameter("id", keyedId);
                String value = (String) q.getSingleResult();
                return Base64.decode(value);
            }

        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param toUse The class name to use
     * @param keyName The associated serialized data key name
     * @param position Starting position.
     * @param max Max number of results
     * @return A Collection of KeyedObjects up to a max of 'max' elements, or an empty collection if nothing is found.
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    @SuppressWarnings("unchecked")
    public List<KeyedObject> search(Class toUse, String keyName, int position, int max) {
        if (toUse == null) {
            LOG.debug("No class specified -> Returning empty collection.");
            return new ArrayList<KeyedObject>(0);
        }
        if (keyName == null) {
            LOG.debug("No key name specified -> Returning empty collection.");
            return new ArrayList<KeyedObject>(0);
        }
        Query q;
        if (LargeKeyedObject.class.equals(toUse)) {
            q = em.createNamedQuery("LargeKeyedObject.findByKeyName");
        } else {
            q = em.createNamedQuery("SmallKeyedObject.findByKeyName");
        }
        q.setParameter("keyName", keyName.hashCode());
        q.setFirstResult(position);
        q.setMaxResults(max);
        return q.getResultList();
    }
}
