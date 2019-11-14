package com.sd.lib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sd.lib.adapter.annotation.ASuperViewHolder;
import com.sd.lib.adapter.viewholder.FRecyclerViewHolder;
import com.sd.lib.adapter.viewholder.FSuperRecyclerViewHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class FSuperRecyclerAdapter<T> extends FRecyclerAdapter<T>
{
    private final Map<Class<?>, ViewHolderInfo> mMapViewHolderInfo = new HashMap<>();
    private final Map<Integer, ViewHolderInfo> mMapTypeViewHolderInfo = new HashMap<>();

    private ViewHolderFactory mViewHolderFactory;

    /**
     * 注册ViewHolder
     *
     * @param clazz
     * @param <T>
     */
    public <T extends FSuperRecyclerViewHolder> void registerViewHolder(Class<T> clazz)
    {
        registerViewHolder(clazz, null);
    }

    /**
     * 注册ViewHolder
     *
     * @param clazz
     * @param viewHolderCallback
     * @param <T>
     */
    public <T extends FSuperRecyclerViewHolder> void registerViewHolder(Class<T> clazz, ViewHolderCallback<T> viewHolderCallback)
    {
        final ASuperViewHolder annotation = getAnnotation(clazz);

        final int layoutId = annotation.layoutId();
        if (layoutId == 0)
            throw new IllegalArgumentException(ASuperViewHolder.class.getSimpleName() + "'s layoutId == 0");

        final Class<?> modelClass = getModelClass(clazz, annotation);
        if (modelClass == null)
            throw new IllegalArgumentException("model class was not found in " + clazz.getSimpleName());

        Constructor<?> constructor = null;
        try
        {
            constructor = clazz.getConstructor(View.class);
        } catch (Exception e)
        {
            throw new IllegalArgumentException("Constructor with View params was not found");
        }

        if (mMapViewHolderInfo.containsKey(modelClass))
            throw new IllegalArgumentException("ViewHolder with model class " + modelClass.getName() + " has been registered:" + clazz);

        final int viewType = System.identityHashCode(modelClass);
        final ViewHolderInfo viewHolderInfo = new ViewHolderInfo(
                clazz,
                viewType,
                layoutId,
                constructor,
                viewHolderCallback
        );

        mMapViewHolderInfo.put(modelClass, viewHolderInfo);
        mMapTypeViewHolderInfo.put(viewType, viewHolderInfo);
    }

    /**
     * {@link ViewHolderFactory}
     *
     * @return
     */
    public ViewHolderFactory getViewHolderFactory()
    {
        if (mViewHolderFactory == null)
            mViewHolderFactory = new DefaultViewHolderFactory();
        return mViewHolderFactory;
    }

    /**
     * {@link ViewHolderFactory}
     *
     * @param viewHolderFactory
     */
    public void setViewHolderFactory(ViewHolderFactory viewHolderFactory)
    {
        mViewHolderFactory = viewHolderFactory;
    }

    @Override
    public final int getItemViewType(int position)
    {
        final Class<?> modelClass = getDataHolder().get(position).getClass();

        final ViewHolderInfo info = mMapViewHolderInfo.get(modelClass);
        if (info == null)
            throw new RuntimeException("ViewHolder for model " + modelClass.getName() + " has not been registered");

        return info.mViewType;
    }

    @Override
    public final FRecyclerViewHolder<T> onCreateVHolder(ViewGroup parent, int viewType)
    {
        final ViewHolderInfo viewHolderInfo = mMapTypeViewHolderInfo.get(viewType);

        final FSuperRecyclerViewHolder viewHolder = getViewHolderFactory().create(viewHolderInfo, parent);
        if (viewHolder == null)
            throw new RuntimeException(ViewHolderFactory.class.getSimpleName() + " create view holder null for:" + viewHolderInfo.mClass.getName());

        viewHolderInfo.notifyViewHolderCreated(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindData(FRecyclerViewHolder<T> holder, int position, T model)
    {

    }

    private static <T extends FSuperRecyclerViewHolder> ASuperViewHolder getAnnotation(Class<T> clazz)
    {
        if (clazz == null)
            throw new IllegalArgumentException("clazz is null");

        if (clazz == FSuperRecyclerViewHolder.class)
            throw new IllegalArgumentException("clazz must not be " + FSuperRecyclerViewHolder.class.getName());

        while (true)
        {
            ASuperViewHolder annotation = clazz.getAnnotation(ASuperViewHolder.class);
            if (annotation != null)
                return annotation;

            if (clazz == FSuperRecyclerViewHolder.class)
                break;

            clazz = (Class<T>) clazz.getSuperclass();
        }

        throw new IllegalArgumentException(ASuperViewHolder.class.getSimpleName() + " annotation was not found in " + clazz.getName());
    }

    private static <T extends FSuperRecyclerViewHolder> Class<?> getModelClass(Class<T> clazz, ASuperViewHolder annotation)
    {
        if (clazz == null)
            throw new IllegalArgumentException("clazz is null");

        final Class<?> modelClass = annotation.modelClass();
        if (modelClass != null && modelClass != ASuperViewHolder.class)
            return modelClass;

        final Type type = getGenericType(clazz);
        if (type == null)
            throw new IllegalArgumentException("model generic type for " + clazz.getSimpleName() + " was not found");

        return (Class<?>) type;
    }

    private static Type getGenericType(Class<?> clazz)
    {
        final ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        final Type[] types = parameterizedType.getActualTypeArguments();
        if (types != null && types.length > 0)
        {
            return types[0];
        } else
        {
            return null;
        }
    }

    public static class ViewHolderInfo
    {
        public final Class<? extends FSuperRecyclerViewHolder> mClass;
        public final int mLayoutId;

        private final int mViewType;
        private final Constructor<?> mConstructor;
        private final ViewHolderCallback mViewHolderCallback;

        public ViewHolderInfo(Class<? extends FSuperRecyclerViewHolder> clazz, int viewType, int layoutId, Constructor<?> constructor, ViewHolderCallback viewHolderCallback)
        {
            mClass = clazz;
            mViewType = viewType;
            mLayoutId = layoutId;
            mConstructor = constructor;
            mViewHolderCallback = viewHolderCallback;
        }

        private void notifyViewHolderCreated(FSuperRecyclerViewHolder viewHolder)
        {
            if (mViewHolderCallback != null)
                mViewHolderCallback.onCreated(viewHolder);
        }
    }

    private final class DefaultViewHolderFactory implements ViewHolderFactory
    {
        @Override
        public FSuperRecyclerViewHolder create(ViewHolderInfo viewHolderInfo, ViewGroup parent)
        {
            final View view = LayoutInflater.from(parent.getContext()).inflate(viewHolderInfo.mLayoutId, parent, false);

            FSuperRecyclerViewHolder viewHolder = null;
            try
            {
                viewHolder = (FSuperRecyclerViewHolder) viewHolderInfo.mConstructor.newInstance(view);
            } catch (Exception e)
            {
                throw new RuntimeException("ViewHolder create failed: " + e);
            }
            return viewHolder;
        }
    }

    public interface ViewHolderCallback<T extends FSuperRecyclerViewHolder>
    {
        void onCreated(T viewHolder);
    }

    public interface ViewHolderFactory
    {
        /**
         * 创建ViewHolder
         *
         * @param viewHolderInfo
         * @param parent
         * @return
         */
        FSuperRecyclerViewHolder create(ViewHolderInfo viewHolderInfo, ViewGroup parent);
    }
}
