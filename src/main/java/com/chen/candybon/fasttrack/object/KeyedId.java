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
package com.chen.candybon.fasttrack.object;

import com.chen.candybon.common.utils.StringUtils;
import com.chen.candybon.fasttrack.exception.DataClassException;
import com.chen.candybon.fasttrack.exception.InvalidKeyException;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * A KeyedId is a container for the unique primary key of a Keyed Object(either a {@link SmallKeyedObject} or a {@link
 * LargeKeyedObject}.
 * <p/>
 * It is composed of two parts: A String name uniquely identifying the type of object that is stored (that is hashed),
 * and a unique id part (uid) part (or unique attribute) that also gets hashed.
 *
 * The uid needs only to be unique in the context of the name.
 */
@Embeddable
public class KeyedId implements Serializable {

    private Integer uid;
    private Integer name;

    /**
     * Default constructor.
     */
    public KeyedId() {
    }

    public KeyedId(Class clazz, String key) throws InvalidKeyException, DataClassException {
        if (clazz == null) {
            throw new DataClassException("Null is not a valid class instance implementing SmallData or LargeData.");
        }
        if (StringUtils.isEmptyOrNull(key)) {
            throw new InvalidKeyException("Null is not a valid Key.");
        }
        this.name = clazz.getName().hashCode();
        this.uid = key.hashCode();
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer key) {
        this.uid = key;
    }

    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof KeyedId)) {
            return false;
        }
        KeyedId other = (KeyedId) object;
        return !((this.name == null && other.name != null)
                || (this.name != null && !this.name.equals(other.name))
                || (this.uid == null && other.uid != null)
                || (this.uid != null && !this.uid.equals(other.uid)));
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KeyedId{" +
                "name=" + name +
                ", key=" + uid +
                '}';
    }
}
