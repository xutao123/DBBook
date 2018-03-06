package com.xt.dbbook.bean.file;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by xt on 2018/01/29.
 */

public class HistroySearchData {
    public Set<String> getDataList() {
        return dataList;
    }

    public void setDataList(Set<String> dataList) {
        this.dataList = dataList;
    }

    private Set<String> dataList;

    public HistroySearchData() {
        dataList = new HashSet<>();
    }

    public HistroySearchData(Set<String> data) {
        dataList = data;
    }


    public void putSearchData(String data) {
        if (dataList == null)
            dataList = new HashSet<>();
        dataList.add(data);
    }

    public void updataSearchData(String data) {
        removeData(data);
        putSearchData(data);
    }

    public boolean hasData(String data) {
        if (dataList == null)
            return false;

        if (dataList.contains(data))
            return true;
        return false;
    }

    public void removeData(String data) {
        if (dataList == null)
            return;
        if (dataList.contains(data))
            dataList.remove(data);
    }

    public void clearAll() {
        if (dataList == null || dataList.size() <= 0)
            return;
        dataList.clear();
    }

}
