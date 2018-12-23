package com.jerryjin.fastpermissionlib;

import android.support.v7.util.DiffUtil;

import java.util.List;

@SuppressWarnings("unchecked")
public class DifferCallback<T extends DifferCallback.IDiffer> extends DiffUtil.Callback {

    private List<T> oldList, newList;

    public DifferCallback(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldPosition, int newPosition) {
        T oldBean = oldList.get(oldPosition);
        T newBean = newList.get(newPosition);
        return oldBean.areItemsTheSame(newBean);
    }

    @Override
    public boolean areContentsTheSame(int oldPosition, int newPosition) {
        T oldBean = oldList.get(oldPosition);
        T newBean = newList.get(newPosition);
        return oldBean.areContentsTheSame(newBean);
    }

    public interface IDiffer<T> {
        boolean areItemsTheSame(T newBean);

        boolean areContentsTheSame(T newBean);
    }
}
