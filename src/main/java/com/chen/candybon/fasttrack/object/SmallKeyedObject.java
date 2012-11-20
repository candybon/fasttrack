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


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * Class comments ...
 */
@NamedQueries(
        {
                @NamedQuery(
                        name = "SmallKeyedObject.findByKeyName",
                        query = "SELECT ko FROM SmallKeyedObject ko WHERE ko.id.name = :keyName " +
                                "ORDER BY ko.id.uid"
                ),
                @NamedQuery(
                        name = "SmallKeyedObject.getValue",
                        query = "SELECT ko.value FROM SmallKeyedObject ko WHERE ko.id = :id"
                )
        }
)
@Entity
@Table(name = "SMALL_KEYED_OBJECT")
public class SmallKeyedObject extends KeyedObject {
    @Transient
    public static final int MAX_SIZE = 2000;

    @Column(name = "BIN_OBJECT", length = MAX_SIZE)
    private String value = null;

    /**
     * Enables optimistic logging.
     */
    @SuppressWarnings("unused")
    @Version
    private Long version;

    public SmallKeyedObject() {
    }

    public SmallKeyedObject(KeyedId id, byte[] value, String... indexes) {
        this.value = Base64.encode(value);
        this.setId(id);
        fillIndexes(indexes);
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public byte[] getValue() {
        return Base64.decode(value);
    }

}
