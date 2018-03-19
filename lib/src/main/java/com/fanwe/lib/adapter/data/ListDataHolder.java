package com.fanwe.lib.adapter.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengjun on 2018/3/19.
 */
public class ListDataHolder<T> implements DataHolder<T>
{
    private List<T> mListData = new ArrayList<>();
    private final List<DataChangeCallback<T>> mListDataChangeCallback = new ArrayList<>();

    @Override
    public void addDataChangeCallback(DataChangeCallback<T> callback)
    {
        if (callback == null || mListDataChangeCallback.contains(callback))
        {
            return;
        }
        mListDataChangeCallback.add(callback);
    }

    @Override
    public void removeDataChangeCallback(DataChangeCallback<T> callback)
    {
        mListDataChangeCallback.remove(callback);
    }

    //---------- modify start ----------

    @Override
    public void setData(List<T> list)
    {
        if (list != null)
        {
            mListData = list;
        } else
        {
            mListData.clear();
        }

        for (DataChangeCallback<T> item : mListDataChangeCallback)
        {
            item.onSetData(list);
        }
    }

    @Override
    public void appendData(T data)
    {
        if (data == null)
        {
            return;
        }

        mListData.add(data);
        for (DataChangeCallback<T> item : mListDataChangeCallback)
        {
            item.onAppendData(data);
        }
    }

    @Override
    public void appendData(List<T> list)
    {
        if (list == null || list.isEmpty())
        {
            return;
        }

        mListData.addAll(list);
        for (DataChangeCallback<T> item : mListDataChangeCallback)
        {
            item.onAppendData(list);
        }
    }

    @Override
    public void removeData(T data)
    {
        final int position = mListData.indexOf(data);
        removeData(position);
    }

    @Override
    public T removeData(int index)
    {
        if (isIndexLegal(index))
        {
            final T model = mListData.remove(index);

            for (DataChangeCallback<T> item : mListDataChangeCallback)
            {
                item.onRemoveData(index, model);
            }
            return model;
        } else
        {
            return null;
        }
    }

    @Override
    public void insertData(int index, T data)
    {
        if (data == null)
        {
            return;
        }

        mListData.add(index, data);
        for (DataChangeCallback<T> item : mListDataChangeCallback)
        {
            item.onInsertData(index, data);
        }
    }

    @Override
    public void insertData(int index, List<T> list)
    {
        if (list == null || list.isEmpty())
        {
            return;
        }

        mListData.addAll(index, list);
        for (DataChangeCallback<T> item : mListDataChangeCallback)
        {
            item.onInsertData(index, list);
        }
    }

    @Override
    public void updateData(int index, T data)
    {
        if (data == null || !isIndexLegal(index))
        {
            return;
        }

        mListData.set(index, data);
        for (DataChangeCallback<T> item : mListDataChangeCallback)
        {
            item.onUpdateData(index, data);
        }
    }

    //---------- modify end ----------

    @Override
    public boolean isIndexLegal(int index)
    {
        return index >= 0 && index < size();
    }

    @Override
    public T get(int index)
    {
        if (isIndexLegal(index))
        {
            return mListData.get(index);
        }
        return null;
    }

    @Override
    public int size()
    {
        return mListData.size();
    }

    @Override
    public int indexOf(T data)
    {
        return mListData.indexOf(data);
    }

    @Override
    public List<T> getData()
    {
        return mListData;
    }
}