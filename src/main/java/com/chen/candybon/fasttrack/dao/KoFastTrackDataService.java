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

import com.chen.candybon.fasttrack.FastTrackDataService;
import com.chen.candybon.fasttrack.Predicate;
import com.chen.candybon.fasttrack.Searchable;
import com.chen.candybon.fasttrack.data.ClassUtils;
import com.chen.candybon.fasttrack.data.LargeData;
import com.chen.candybon.fasttrack.exception.DataClassException;
import com.chen.candybon.fasttrack.exception.DataException;
import com.chen.candybon.fasttrack.exception.InvalidKeyException;
import com.chen.candybon.fasttrack.exception.InvalidPredicateException;
import com.chen.candybon.fasttrack.exception.NotFoundException;
import com.chen.candybon.fasttrack.object.KeyedId;
import com.chen.candybon.fasttrack.object.KeyedObject;
import com.chen.candybon.fasttrack.object.LargeKeyedObject;
import com.chen.candybon.fasttrack.object.SmallKeyedObject;
import com.chen.candybon.fasttrack.type.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of Fast Track Api
 */
@Stateless
public class KoFastTrackDataService<T> implements FastTrackDataService {

    /**
     * Logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(KoFastTrackDataService.class);
    @EJB()
    private LocalKeyedObjectDao dao = null;

    public KoFastTrackDataService() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean put(final String key, final Object data, final String... indexes)
            throws DataException, InvalidKeyException, DataClassException {
        if (key == null) {
            throw new InvalidKeyException("Null is not a valid key.");
        }
        if (data == null) {
            throw new DataException("Null is not a valid object to store.");
        }
        ClassUtils.typeValidation(data);
        KeyedId keyedId = new KeyedId(data.getClass(), key);

        String[] idxes = indexes;

        if (indexes.length == 0) {
            idxes = getIndexes(data);
        }

        validateIndexes(idxes);
        KeyedObject ko;
        try {
            byte[] value = serialize(data);
            if (value == null || value.length == 0) {
                LOG.debug("No data -> failing");
                throw new DataException("No data to store");
            }

            if (data instanceof LargeData) {
                ko = new LargeKeyedObject(keyedId, value, idxes);
            } else {
                ko = new SmallKeyedObject(keyedId, value, idxes);
            }
            ko = dao.create(ko);
        } catch (IOException e) {
            throw new DataException("Failed to serialize data with error: " + e.getMessage(), e);
        }
        return ko != null;
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @SuppressWarnings("unchecked")
    @Override
    public T get(Class tClass, String key) throws DataException, InvalidKeyException, DataClassException {
        KeyedId keyedId = new KeyedId(tClass, key);
        Class toUse = ClassUtils.classSelector(tClass);
        byte[] bytes = dao.getValue(toUse, keyedId);
        if (bytes == null) {
            return null;
        }
        try {
            return deserialize(bytes);
        } catch (Exception e) {
            LOG.warn("Failed to de-serialize data with error: " + e.getMessage());
            throw new DataException("Failed to de-serialize data with error: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean update(final String key, final Object data, String... indexes)
            throws DataException, InvalidKeyException, DataClassException, NotFoundException {
        if (key == null) {
            throw new InvalidKeyException("Null is not a valid key.");
        }
        if (data == null) {
            throw new DataException("Null is not a valid object to update.");
        }
        ClassUtils.typeValidation(data);

        String[] idxes = indexes;
        if (indexes.length == 0) {
            idxes = getIndexes(data);
        }
        validateIndexes(idxes);
        KeyedId keyedId = new KeyedId(data.getClass(), key);
        try {
            Class toUse = ClassUtils.classSelector(data);
            dao.update(toUse, keyedId, serialize(data), idxes);
        } catch (IOException e) {
            throw new DataException("Failed to serialize data with error: " + e.getMessage(), e);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete(final Class tClass, final String key) throws InvalidKeyException, DataClassException {
        KeyedId keyedId = new KeyedId(tClass, key);
        Class toUse = ClassUtils.classSelector(tClass);
        dao.delete(toUse, keyedId);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @SuppressWarnings("unchecked")
    @Override
    public List<T> find(final Class tClass, final Predicate predicate)
            throws DataException, DataClassException, InvalidPredicateException {
        Class toUse = ClassUtils.classSelector(tClass);
        Collection<KeyedObject> kos = dao.search(toUse, tClass.getName(), predicate);
        return convert(kos);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @SuppressWarnings("unchecked")
    @Override
    public List<T> find(Class tClass, final int position, final int max)
            throws DataException, DataClassException {
        Class toUse = ClassUtils.classSelector(tClass);
        Collection<KeyedObject> kos = dao.search(toUse, tClass.getName(), position, max);
        return convert(kos);
    }

    protected byte[] serialize(Object data) throws IOException, DataException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(data);
        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    protected T deserialize(byte[] bytes) throws IOException, ClassNotFoundException, DataException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (T) ois.readObject();
    }

    private void validateIndexes(String... indexes) throws DataException {
        if (indexes != null && indexes.length > Index.values().length) {
            throw new DataException("A Maximum of six(6) indexes can be specified.");
        }
    }

    private List<T> convert(Collection<KeyedObject> kos) {
        List<T> result = new ArrayList<T>(kos.size());
        byte[] bytes;
        for (KeyedObject ko : kos) {
            bytes = ko.getValue();
            try {
                result.add(deserialize(bytes));
            } catch (Exception e) {
                LOG.warn("Failed to de-serialize data for KeyedId { {} } with error: {}. Corrupted set."
                        + " Ignoring this element.", ko.getId(), e.getMessage());
            }
        }
        return result;
    }

    private String[] getIndexes(final Object data) throws DataClassException {
        Map<Index, String> indexValuePair = new EnumMap<Index, String>(Index.class);
        Field[] fields = data.getClass().getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field);
            if (field.isAnnotationPresent(Searchable.class)) {
                field.setAccessible(true);
                Searchable searchable = field.getAnnotation(Searchable.class);
                try {
                    Object previous = indexValuePair.put(searchable.value(), (String) field.get(data));
                    if (previous != null) {
                        throw new DataClassException("Duplicated Declaration of Index " + searchable.value()
                                + " on field " + field.getName());
                    }
                } catch (IllegalArgumentException ex) {
                    throw new DataClassException("Field " + field.getName() + " is not String!");
                } catch (IllegalAccessException ex) {
                    throw new DataClassException("Set field " + field.getName() + " accessible failed!");
                }
            }
        }

        String[] indexes = new String[indexValuePair.size()];

        for (int i = 0; i < indexValuePair.size(); i++) {
            String value = indexValuePair.get(Index.values()[i]);
            if (value == null) {
                throw new DataClassException("Index " + Index.values()[i] + " declaration is missing.");
            }
            indexes[i] = value;
        }
        return indexes;
    }

}
