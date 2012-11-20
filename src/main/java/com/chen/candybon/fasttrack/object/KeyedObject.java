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

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Version;

/**
 * Super class for both types of KeyedObject:
 * <ul>
 *     <li>{@link LargeKeyedObject}</li>
 *     <li>{@link SmallKeyedObject}</li>
 * </ul>
 *
 *  Defines the key id, and the indexes
 */
@MappedSuperclass
public abstract class KeyedObject {

    @EmbeddedId
    private KeyedId id = null;

    @Transient
    static final int MAX_IDX = 6;

    @Column
    //@Index
    protected Integer index1 = null;
    @Column
    //@Index
    protected Integer index2 = null;
    @Column
    //@Index
    protected Integer index3 = null;
    @Column
    //@Index
    protected Integer index4 = null;
    @Column
    //@Index
    protected Integer index5 = null;
    @Column
    //@Index
    protected Integer index6 = null;

    // optimistic locking
    @Version
    private Long optLock = null;
    static final int IDX_1 = 0;
    static final int IDX_2 = 1;
    static final int IDX_3 = 2;
    static final int IDX_4 = 3;
    static final int IDX_5 = 4;
    static final int IDX_6 = 5;

    public KeyedId getId() {
        return id;
    }

    public void setId(KeyedId id) {
        this.id = id;
    }

    public Long getOptLock() {
        return optLock;
    }

    public Integer getIndex1() {
        return index1;
    }

    public void setIndex1(final Integer index1) {
        this.index1 = index1;
    }

    public Integer getIndex2() {
        return index2;
    }

    public void setIndex2(final Integer index2) {
        this.index2 = index2;
    }

    public Integer getIndex3() {
        return index3;
    }

    public void setIndex3(final Integer index3) {
        this.index3 = index3;
    }

    public Integer getIndex4() {
        return index4;
    }

    public void setIndex4(final Integer index4) {
        this.index4 = index4;
    }

    public Integer getIndex5() {
        return index5;
    }

    public void setIndex5(final Integer index5) {
        this.index5 = index5;
    }

    public Integer getIndex6() {
        return index6;
    }

    public void setIndex6(final Integer index6) {
        this.index6 = index6;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyedObject)) {
            return false;
        }

        KeyedObject that = (KeyedObject) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (index1 != null ? !index1.equals(that.index1) : that.index1 != null) {
            return false;
        }
        if (index2 != null ? !index2.equals(that.index2) : that.index2 != null) {
            return false;
        }
        if (index3 != null ? !index3.equals(that.index3) : that.index3 != null) {
            return false;
        }
        if (index4 != null ? !index4.equals(that.index4) : that.index4 != null) {
            return false;
        }
        if (index5 != null ? !index5.equals(that.index5) : that.index5 != null) {
            return false;
        }
        if (index6 != null ? !index6.equals(that.index6) : that.index6 != null) {
            return false;
        }

        // Use a checksum for the value comparison?
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (index1 != null ? index1.hashCode() : 0);
        result = 31 * result + (index2 != null ? index2.hashCode() : 0);
        result = 31 * result + (index3 != null ? index3.hashCode() : 0);
        result = 31 * result + (index4 != null ? index4.hashCode() : 0);
        result = 31 * result + (index5 != null ? index5.hashCode() : 0);
        result = 31 * result + (index6 != null ? index6.hashCode() : 0);
        return result;
    }

    public void fillIndexes(String... indexes) {
        if (indexes == null || indexes.length > MAX_IDX) {
            throw new IllegalArgumentException("Indexes are incorrect");
        }
        for (int i = 0; i < indexes.length; i++) {
            switch (i) {
                case IDX_1:
                    index1 = indexes[i].hashCode();
                    break;
                case IDX_2:
                    index2 = indexes[i].hashCode();
                    break;
                case IDX_3:
                    index3 = indexes[i].hashCode();
                    break;
                case IDX_4:
                    index4 = indexes[i].hashCode();
                    break;
                case IDX_5:
                    index5 = indexes[i].hashCode();
                    break;
                default:
                    index6 = indexes[i].hashCode();
                    break;
            }
        }
    }

    public abstract byte[] getValue();

}
