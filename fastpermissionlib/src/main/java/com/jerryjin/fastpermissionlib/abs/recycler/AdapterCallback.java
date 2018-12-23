package com.jerryjin.fastpermissionlib.abs.recycler;

public interface AdapterCallback<T> {
    void update(T data, RecyclerAdapter.ViewHolder<T> holder);
}
