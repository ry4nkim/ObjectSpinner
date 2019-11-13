package kr.ry4nkim.objectspinner;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ObjectSpinnerAdapter<T extends ObjectSpinner.Delegate> extends RecyclerView.Adapter<ObjectSpinnerAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private List<T> mItemList;
    private List<T> mFilteredItemList;

    private T mSelectedItem;

    private OnFilterFinishedListener mOnFilterFinishedListener;

    private int mItemPaddingTop;
    private int mItemPaddingLeft;
    private int mItemPaddingBottom;
    private int mItemPaddingRight;

    private int mItemTextSize;
    private int mItemTextColor;
    private int mItemBackgroundColor;

    private int mSelectedItemTextSize;
    private int mSelectedItemTextColor;
    private int mSelectedItemBackgroundColor;

    public ObjectSpinnerAdapter(Context mContext) {
        this.mContext = mContext;
        this.mItemList = new ArrayList<>();
        this.mFilteredItemList = new ArrayList<>();
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.os_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ObjectSpinnerAdapter.ViewHolder holder, final int position) {
        T item = getFilteredItem(position);
        holder.mItemTextView.setPadding(mItemPaddingLeft, mItemPaddingTop, mItemPaddingRight, mItemPaddingBottom);

        if (getFilteredItem(position) == mSelectedItem) {
            holder.mItemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.convertPixelToDp(mContext, mSelectedItemTextSize));
            holder.mItemTextView.setTextColor(mSelectedItemTextColor);
            holder.mItemTextView.setBackgroundColor(mSelectedItemBackgroundColor);
        } else {
            holder.mItemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.convertPixelToDp(mContext, mItemTextSize));
            holder.mItemTextView.setTextColor(mItemTextColor);
            holder.mItemTextView.setBackgroundColor(mItemBackgroundColor);
        }

        holder.mItemTextView.setText(item.getSpinnerDelegate());
        holder.mItemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedItem = getFilteredItem(position);
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(view, mItemList.indexOf(mSelectedItem));
                }
            }
        });
    }

    public void setItemList(List<T> itemList) {
        this.mItemList = itemList;
        this.mFilteredItemList = itemList;
        notifyDataSetChanged();
    }

    /*public void addItem(T item) {
        this.mItemList.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        this.mItemList.remove(position);
        notifyDataSetChanged();
    }

    public void clear() {
        this.mItemList.clear();
        notifyDataSetChanged();
    }*/

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence searchText) {
                String charString = searchText.toString();
                if (charString.isEmpty()) {
                    mFilteredItemList = mItemList;
                } else {
                    ArrayList<T> filteringList = new ArrayList<>();
                    for (T item : mItemList) {
                        if (item.getSpinnerDelegate().toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(item);
                        }
                    }
                    mFilteredItemList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredItemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence searchText, FilterResults results) {
                mFilteredItemList = (ArrayList<T>) results.values;
                mOnFilterFinishedListener.onFilterFinished(mFilteredItemList);
                notifyDataSetChanged();
            }
        };
    }

    public void setOnFilterFinishedListener(OnFilterFinishedListener onFilterFinishedListener) {
        this.mOnFilterFinishedListener = onFilterFinishedListener;
    }

    public void setItemPadding(int left, int top, int right, int bottom) {
        this.mItemPaddingLeft = left;
        this.mItemPaddingTop = top;
        this.mItemPaddingRight = right;
        this.mItemPaddingBottom = bottom;
    }

    public void setItemTextSize(int size) {
        this.mItemTextSize = size;
    }

    public void setItemTextColor(int color) {
        this.mItemTextColor = color;
    }

    public void setItemBackgroundColor(int color) {
        this.mItemBackgroundColor = color;
    }

    public void setSelectedItemTextSize(int size) {
        this.mSelectedItemTextSize = size;
    }

    public void setSelectedItemTextColor(int color) {
        this.mSelectedItemTextColor = color;
    }

    public void setSelectedItemBackgroundColor(int color) {
        this.mSelectedItemBackgroundColor = color;
    }

    @Override
    public int getItemCount() {
        return this.mFilteredItemList.size();
    }

    public T getFilteredItem(int position) {
        return this.mFilteredItemList.get(position);
    }

    public T getItem(int position) {
        return this.mItemList.get(position);
    }

    public List<T> getItemList() {
        return mItemList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mItemTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemTextView = itemView.findViewById(R.id.tv_item);
        }
    }

}
