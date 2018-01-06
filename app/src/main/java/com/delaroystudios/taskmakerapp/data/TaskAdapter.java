package com.delaroystudios.taskmakerapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.delaroystudios.taskmakerapp.R;
import com.delaroystudios.taskmakerapp.views.TaskTitleView;

/**
 * Created by delaroy on 1/5/18.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {
    SharedPreferences pref;

    /* Callback for list item click events */
    public interface OnItemClickListener {
        void onItemClick(View v, int position);

        void onItemToggled(boolean active, int position);
    }

    /* ViewHolder for each task item */
    public class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TaskTitleView nameView;
        public TextView dateView;
        public ImageView priorityView;
        public CheckBox checkBox;

        public TaskHolder(View itemView) {
            super(itemView);

            nameView = (TaskTitleView) itemView.findViewById(R.id.text_description);
            dateView = (TextView) itemView.findViewById(R.id.text_date);
            priorityView = (ImageView) itemView.findViewById(R.id.priority);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener(this);
            checkBox.setOnClickListener(this);

        }



        @Override
        public void onClick(View v) {
            if (v == checkBox) {
                completionToggled(this);

            } else {
                postItemClick(this);
            }
        }


    }


    private Cursor mCursor;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;
    TaskTitleView taskTitleView;

    public TaskAdapter(Cursor cursor, Context context) {
        mCursor = cursor;
        this.mContext = context;
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private void completionToggled(TaskHolder holder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemToggled(holder.checkBox.isChecked(), holder.getAdapterPosition());

        }

    }



    private void postItemClick(TaskHolder holder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
        }
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_task, parent, false);

        return new TaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        final long NO_DATE = Long.MAX_VALUE;

        //TODO: Bind the task data to the views
        mCursor.moveToPosition(position);

        String mDescription = mCursor.getString(mCursor.getColumnIndex(TaskContract.TaskColumns.DESCRIPTION));
        long mDueDate = mCursor.getLong(mCursor.getColumnIndex(TaskContract.TaskColumns.DUE_DATE));
        int mIsComplete = mCursor.getInt(mCursor.getColumnIndex(TaskContract.TaskColumns.IS_COMPLETE));
        int mIsPriority = mCursor.getInt(mCursor.getColumnIndex(TaskContract.TaskColumns.IS_PRIORITY));

        long time= System.currentTimeMillis();

        if (mDueDate != NO_DATE && time > mDueDate){
            holder.nameView.setState(2);
            holder.nameView.getState();
            holder.nameView.setText(mDescription);
        }else if (mIsComplete != 0 ){
            holder.nameView.setState(1);
            holder.nameView.getState();
            holder.nameView.setText(mDescription);
        }else{
            holder.nameView.setState(0);
            holder.nameView.getState();
            holder.nameView.setText(mDescription);
        }

        if (mIsPriority == 0){
            holder.priorityView.setImageResource(R.drawable.ic_not_priority);
        }else{
            holder.priorityView.setImageResource(R.drawable.ic_priority);
        }


        Task task = new Task(mDescription, true, true, mDueDate);

        if (task.hasDueDate()){
            CharSequence formatted = DateUtils.getRelativeTimeSpanString(mContext, mDueDate);
            holder.dateView.setText(formatted);
        }else{
            holder.dateView.setText("");
        }


    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    /**
     * Retrieve a {@link Task} for the data at the given position.
     *
     * @param position Adapter item position.
     *
     * @return A new {@link Task} filled with the position's attributes.
     */
    public Task getItem(int position) {
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Invalid item position requested");
        }

        return new Task(mCursor);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    public void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }
}
