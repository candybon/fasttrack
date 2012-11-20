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
package com.chen.candybon.fasttrack.data;
/**
 * @author Xiaowei Chen
 */
import java.io.Serializable;

/**
 * Interface implemented by all data objects that are going to use less than 8000 bytes once serialized.
 * <p/>
 * The serialization of this object will be stored in a VARCHAR or VARBIN in the database in the same row as the
 * different indexes.
 * <p/>
 * The result is a faster access time.
 * <p/>
 * Serialization uses a calculated hash based on just about everything in a given source file method names, field
 * names, field types, access modifiers, you name it ï¿?and compares that hash value against the hash value in the
 * serialized stream.
 * <p/>
 * To convince the Java runtime that the two types are in fact the same, the second and subsequent versions of Data must
 * have the same serialization version hash (stored as the private static final serialVersionUID field) as the first
 * one. What we need, therefore, is the serialVersionUID field, which is calculated by running the JDK serialver command
 * against the original (or V1) version of the Data implementation class. Once we have Data's serialVersionUID, not only
 * can we create DataV2 objects out of the original object's serialized data (where the new fields appear, they will
 * default to whatever the default value is for a field, most often "null"), but the opposite is also true: we can
 * de-serialize original Data objects out of DataV2 data, with no added fuss.
 * <p/>
 * Which is the reason why this abstract class enforces the serialVersionUID to a constant value. And, as a result, a
 * class that extends SmallData MUST NOT under any circumstances override the serialVersionUID.  If this is overridden,
 * then it implies that serialization/de-serialization after a class change might not work.
 *
 * @see LargeData
 */
public abstract class SmallData implements Serializable {
    private static long serialVersionUID = 1L;
}
