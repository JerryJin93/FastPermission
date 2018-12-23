package com.jerryjin.fastpermissionlib.abs.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jerryjin.fastpermissionlib.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<T>>
        implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<T> {

    private final List<T> mDataList;
    private AdapterListener<T> mListener;

    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener<T> mListener) {
        this(new ArrayList<T>(), mListener);
    }

    public RecyclerAdapter(List<T> mDataList, AdapterListener<T> mListener) {
        this.mDataList = mDataList;
        this.mListener = mListener;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    protected abstract int getItemViewType(int position, T data);

    @NonNull
    @Override
    public ViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(viewType, parent, false);
        ViewHolder<T> holder = onCreateViewHolder(root, viewType);
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);
        root.setTag(R.id.tag_recycler_holder, holder);
        holder.unbinder = ButterKnife.bind(holder, root);
        holder.callback = this;
        return holder;
    }

    protected abstract ViewHolder<T> onCreateViewHolder(View root, int viewType);

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<T> holder, int position) {
        T data = mDataList.get(position);
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * Newly added method.
     *
     * @param mDataList dataList.
     */
    public void set(List<T> mDataList) {
        this.mDataList.clear();
        this.mDataList.addAll(mDataList);
    }

    public void add(T data) {
        mDataList.add(data);
        notifyItemInserted(mDataList.size() - 1);
    }

    public void add(T... dataList) {
        if (dataList != null && dataList.length > 0) {
            int startPos = mDataList.size() - 1;
            Collections.addAll(mDataList, dataList);
            notifyItemRangeChanged(startPos, dataList.length);
        }
    }

    public void add(Collection<T> dataCollection) {
        if (dataCollection != null && dataCollection.size() > 0) {
            int startPos = mDataList.size();
            mDataList.addAll(dataCollection);
            notifyItemRangeInserted(startPos, dataCollection.size());
        }
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void replace(Collection<T> dataCollection) {
        mDataList.clear();
        if (dataCollection == null || dataCollection.size() == 0) {
            return;
        }
        mDataList.addAll(dataCollection);
        notifyDataSetChanged();
    }

    @Override
    public void update(T data, ViewHolder<T> holder) {
        int pos = holder.getAdapterPosition();
        if (pos > 0) {
            mDataList.remove(pos);
            mDataList.add(pos, data);
            notifyItemChanged(pos);
        }
    }

    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (this.mListener != null) {
            int position = viewHolder.getAdapterPosition();
            this.mListener.onItemClick(viewHolder, mDataList.get(position));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (this.mListener != null) {
            int position = viewHolder.getAdapterPosition();
            this.mListener.onItemLongClick(viewHolder, mDataList.get(position));
            return true;
        }
        return false;
    }

    public void setListener(AdapterListener<T> listener) {
        this.mListener = listener;
    }

    public interface AdapterListener<T> {
        void onItemClick(RecyclerAdapter.ViewHolder holder, T data);

        void onItemLongClick(RecyclerAdapter.ViewHolder holder, T data);
    }


    public static abstract class ViewHolder<T> extends RecyclerView.ViewHolder {

        protected T mData;
        private Unbinder unbinder;
        private AdapterCallback<T> callback;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(T data) {
            this.mData = data;
            onBind(data);
        }

        protected abstract void onBind(T data);

        public void updateData(T data) {
            if (this.callback != null) {
                this.callback.update(data, this);
            }

        }
    }

    public static abstract class AdapterListenerImpl<T> implements AdapterListener<T> {
        @Override
        public void onItemClick(ViewHolder holder, T data) {

        }

        @Override
        public void onItemLongClick(ViewHolder holder, T data) {

        }
    }
}
