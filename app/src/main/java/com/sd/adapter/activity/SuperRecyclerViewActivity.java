package com.sd.adapter.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sd.adapter.R;
import com.sd.adapter.adapter.TestSuperAdapter;
import com.sd.adapter.viewholder.ButtonViewHolder;
import com.sd.adapter.viewholder.TextViewViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SuperRecyclerViewActivity extends AppCompatActivity
{
    private RecyclerView mRecyclerView;
    private final TestSuperAdapter mAdapter = new TestSuperAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_super_recyclerview);
        mRecyclerView = findViewById(R.id.recyclerview);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        fillData();
    }

    /**
     * 设置数据给Adapter
     */
    private void fillData()
    {
        final List<Object> listData = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            if (i % 2 == 0)
            {
                TextViewViewHolder.Model model = new TextViewViewHolder.Model();
                model.name = String.valueOf(i);
                listData.add(model);
            } else
            {
                ButtonViewHolder.Model model = new ButtonViewHolder.Model();
                model.name = String.valueOf(i);
                listData.add(model);
            }
        }

        mAdapter.getDataHolder().setData(listData);
    }
}
