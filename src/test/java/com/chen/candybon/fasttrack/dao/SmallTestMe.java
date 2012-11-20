package com.chen.candybon.fasttrack.dao;

import com.chen.candybon.fasttrack.data.SmallData;

/**
 * SmallData Helper data test.
 */
public class SmallTestMe extends SmallData {
    String data = "";

    public SmallTestMe(String data) {
        this.data = data;
    }

    public SmallTestMe() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmallTestMe that = (SmallTestMe) o;
        return !(data != null ? !data.equals(that.data) : that.data != null);
    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }
}