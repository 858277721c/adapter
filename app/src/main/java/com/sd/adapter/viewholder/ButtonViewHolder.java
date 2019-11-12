package com.sd.adapter.viewholder;

import android.view.View;
import android.widget.Button;

import com.sd.adapter.R;
import com.sd.adapter.model.DataModel;
import com.sd.lib.adapter.annotation.ASuperViewHolder;
import com.sd.lib.adapter.viewholder.FSuperRecyclerViewHolder;

@ASuperViewHolder(layoutId = R.layout.item_super_button)
public class ButtonViewHolder extends FSuperRecyclerViewHolder<ButtonViewHolder.Model>
{
    private Button btn_content;

    public ButtonViewHolder(View itemView)
    {
        super(itemView);
    }

    @Override
    public void onCreate()
    {
        btn_content = findViewById(R.id.btn_content);
    }

    @Override
    public void onBindData(int position, ButtonViewHolder.Model model)
    {
        btn_content.setText(model.getSource().name);
    }

    /**
     * ViewHolder对应的实体
     */
    public static class Model extends FSuperRecyclerViewHolder.Model<DataModel>
    {
    }
}