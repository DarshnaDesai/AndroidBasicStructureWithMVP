package com.basicstructurewithmvp.baseclasses;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Darshna Desai
 */
public abstract class BaseRecyclerAdapter<T extends BaseRecyclerAdapter.ViewHolder, M>
        extends RecyclerView.Adapter<T> {

    private List<M> data;
    private RecycleOnItemEventListener mRecycleOnItemEventListener;

    public BaseRecyclerAdapter(List<M> data) {
        this.data = data;
    }

    public BaseRecyclerAdapter setRecycleOnItemEventListener(
            RecycleOnItemEventListener mRecycleOnItemClickListener) {
        this.mRecycleOnItemEventListener = mRecycleOnItemClickListener;
        return this;
    }

    public M getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public final List<M> getData() {
        return data;
    }

    public interface RecycleOnItemEventListener {
        public void onItemClick(View view, int position);

        public void onItemLongPress(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecycleOnItemEventListener != null) {
                    mRecycleOnItemEventListener.onItemClick(v, getLayoutPosition());
                }
            }
        };

        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mRecycleOnItemEventListener != null) {
                    mRecycleOnItemEventListener.onItemLongPress(view, getLayoutPosition());
                }
                return true;
            }
        };

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        //put here clickable views list
        public void clickableViews(View... views) {
            for (View view : views) {
                view.setOnClickListener(mOnClickListener);
            }
        }

        public void longClickableViews(View... views) {
            for (View view : views) {
                view.setOnLongClickListener(mOnLongClickListener);
            }
        }
    }
}
