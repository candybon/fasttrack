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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Not sure about the stategy for the PK.  Using generated ids is IMO the only way to have
 * different ids consistently generated, at the same time it makes looking up an object by
 * PK impossible, and implies using the indexes.  Which may or not be a good idea.
 */
@NamedQueries(
        {
                @NamedQuery(
                        name = "LargeKeyedObject.findByKeyName",
                        query = "SELECT ko FROM LargeKeyedObject ko WHERE ko.id.name = :keyName " +
                                "ORDER BY ko.id.uid"
                ),
                @NamedQuery(
                        name = "LargeKeyedObject.findByKeyId",
                        query = "SELECT ko FROM LargeKeyedObject ko WHERE ko.id = :id"
                )

        }
)
@Entity
@Table(name = "LARGE_KEYED_OBJECT")
public class LargeKeyedObject extends KeyedObject {

    @Lob
    @Column(name = "BIN_OBJECT")
    private byte[] value = null;

    /**
     * Enables optimistic logging.
     */
    @SuppressWarnings("unused")
    @Version
    private Long version;

    public LargeKeyedObject() {
    }

    public LargeKeyedObject(KeyedId id, byte[] value, String... indexes) {
        this.value = value;
        this.setId(id);
        fillIndexes(indexes);
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    public void setValue(final byte[] value) {
        this.value = value;
    }

}
