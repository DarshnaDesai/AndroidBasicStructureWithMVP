package com.basicstructurewithmvp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.basicstructurewithmvp.baseclasses.BaseRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Darshna Desai
 */
public class ExampleAdapter extends BaseRecyclerAdapter<ExampleAdapter.DataObjectHolder, String> {

    private ArrayList<String> data = null;
    private Context context;

    public ExampleAdapter(Context context, ArrayList<String> data) {
        super(data);
        this.context = context;
        this.data = data;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        //view = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_layout, parent, false);
        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

    }

    class DataObjectHolder extends BaseRecyclerAdapter.ViewHolder {
        /*Bind your view as mentioned below*/
      /*  @BindView(R.id.yourTv)
        TextView yourTv;*/

        DataObjectHolder(View itemView) {
            super(itemView);
            longClickableViews(itemView);
        }
    }
}