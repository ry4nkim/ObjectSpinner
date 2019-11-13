package kr.ry4nkim.objectspinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ObjectSpinner<T extends ObjectSpinner.Delegate> extends LinearLayout {

    // Spinner Layout View
    private LinearLayout mRootLayout;
    private LinearLayout mSpinnerLayout;
    private TextView mSelectedItemTextView;
    private ImageView mArrowIcon;

    // Popup Window View
    private PopupWindow mPopupWindow;
    private FrameLayout mSearchLayout;
    private EditText mSearchText;
    private TextWatcher mSearchTextWatcher;
    private ImageView mSearchIcon;
    private MaxHeightRecyclerView mRecyclerView;
    private TextView mListEmptyTextView;

    // Spinner Adapter
    private ObjectSpinnerAdapter<T> mAdapter;

    // Item Position
    private int mPrevSelectedItemPosition = -1;
    private int mCurrSelectedItemPosition = -1;
    private int mTempSelectedItemPosition = -1;

    // Spinner Event Listener
    private OnItemSelectedListener mOnItemSelectedListener;
    private OnNothingSelectedListener mOnNothingSelectedListener;

    // Spinner Layout Attrs
    private int mPadding;
    private int mPaddingTop;
    private int mPaddingLeft;
    private int mPaddingBottom;
    private int mPaddingRight;
    private int mTextSize;
    private int mTextColor;
    private int mBackgroundColor;
    private String mHint;
    private int mHintColor;
    private int mArrowColor;
    private boolean mShadow;

    // Spinner List Layout Attrs
    private int mListMaxHeight;
    private String mListEmptyText;

    // Spinner Item Layout Attrs
    private int mItemPadding;
    private int mItemPaddingTop;
    private int mItemPaddingLeft;
    private int mItemPaddingBottom;
    private int mItemPaddingRight;
    private int mItemTextSize;
    private int mItemTextColor;
    private int mItemBackgroundColor;

    // Spinner Selected Item Layout Attrs
    private int mSelectedItemTextSize;
    private int mSelectedItemTextColor;
    private int mSelectedItemBackgroundColor;

    // Spinner Search Layout Attrs
    private boolean mSearchable;
    private int mSearchTextColor;
    private int mSearchIconColor;
    private int mSearchBackgroundColor;

    public ObjectSpinner(Context context) {
        super(context);
        initView();
    }

    public ObjectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public ObjectSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
        getAttrs(attrs, defStyle);
    }

    private void initView() {
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.os_object_spinner, this, false);
        addView(v);

        // Init Spinner Layout
        mRootLayout = findViewById(R.id.layout_root);
        mSpinnerLayout = findViewById(R.id.layout_spinner);
        mSpinnerLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPopupWindow.isShowing()) {
                    animateArrow(true);
                    mTempSelectedItemPosition = mCurrSelectedItemPosition;
                    mPopupWindow.showAsDropDown(view, Utils.convertDpToPixel(getContext(), mShadow ? -5 : 0), 0);
                    mRecyclerView.scrollToPosition(mCurrSelectedItemPosition);
                    mSearchText.setText("");
                }
            }
        });
        mSelectedItemTextView = findViewById(R.id.tv_selected_item);
        mArrowIcon = findViewById(R.id.ic_arrow);

        // Init Popup Window
        mPopupWindow = new PopupWindow(getContext());
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupLayout = layoutInflater.inflate(R.layout.os_popup_window, null);
        mPopupWindow.setContentView(popupLayout);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(null);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                animateArrow(false);
                if (mOnNothingSelectedListener != null && mTempSelectedItemPosition == mCurrSelectedItemPosition) {
                    mOnNothingSelectedListener.onNothingSelected(ObjectSpinner.this);
                }
            }
        });

        mSearchLayout = popupLayout.findViewById(R.id.layout_search);
        mSearchText = popupLayout.findViewById(R.id.et_search);
        mSearchText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        mSearchTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAdapter.getFilter().filter(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        mSearchIcon = popupLayout.findViewById(R.id.ic_search);
        mRecyclerView = popupLayout.findViewById(R.id.rv_item_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ObjectSpinnerAdapter<>(getContext());
        mAdapter.setOnItemClickListener(new ObjectSpinnerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                mSelectedItemTextView.setText(mAdapter.getItem(position).getSpinnerDelegate());

                mPrevSelectedItemPosition = mCurrSelectedItemPosition;
                mCurrSelectedItemPosition = position;

                if (mOnItemSelectedListener != null && mCurrSelectedItemPosition != mPrevSelectedItemPosition)
                    mOnItemSelectedListener.onItemSelected(ObjectSpinner.this, position, mAdapter.getItem(position));

                mPopupWindow.dismiss();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mListEmptyTextView = popupLayout.findViewById(R.id.tv_list_empty);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.os_ObjectSpinner);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.os_ObjectSpinner, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {
        // Set Spinner Layout Attrs
        mPadding = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_padding, 0);
        mPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_padding_left, Utils.convertDpToPixel(getContext(), 16));
        mPaddingTop = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_padding_top, Utils.convertDpToPixel(getContext(), 8));
        mPaddingRight = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_padding_right, Utils.convertDpToPixel(getContext(), 16));
        mPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_padding_bottom, Utils.convertDpToPixel(getContext(), 8));
        if (mPadding > 0) mPadding = mPaddingLeft = mPaddingTop = mPaddingRight = mPaddingBottom;
        mSpinnerLayout.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);

        mTextSize = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_text_size, Utils.convertDpToPixel(getContext(), 16));
        if (mTextSize > 0)
            mSelectedItemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.convertPixelToDp(getContext(), mTextSize));

        mTextColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_text_color, Color.rgb(51, 51, 51));
        mSelectedItemTextView.setTextColor(mTextColor);

        mBackgroundColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_background_color, Color.rgb(255, 255, 255));
        mSpinnerLayout.setBackgroundColor(mBackgroundColor);

        mHint = typedArray.getString(R.styleable.os_ObjectSpinner_os_hint);
        mSelectedItemTextView.setHint(mHint);

        mHintColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_hint_color, Color.rgb(153, 153, 153));
        mSelectedItemTextView.setHintTextColor(mHintColor);

        mArrowColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_arrow_color, Color.rgb(51, 51, 51));
        mArrowIcon.setColorFilter(mArrowColor);

        mShadow = typedArray.getBoolean(R.styleable.os_ObjectSpinner_os_shadow, true);
        if (mShadow) {
            mRootLayout.setBackground(getContext().getResources().getDrawable(R.drawable.os_pinner_bg));
            mPopupWindow.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.os_popup_bg));
        }

        // Set Spinner List Layout Attrs
        mListMaxHeight = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_list_max_height, 0);
        mRecyclerView.setMaxHeight(mListMaxHeight > 0 ? mListMaxHeight : Utils.convertDpToPixel(getContext(), 160));

        mListEmptyText = typedArray.getString(R.styleable.os_ObjectSpinner_os_list_empty_text);
        mListEmptyTextView.setText(mListEmptyText);

        // Set Spinner Item Layout Attrs
        mItemPadding = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_item_padding, 0);
        mItemPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_item_padding_left, Utils.convertDpToPixel(getContext(), 16));
        mItemPaddingTop = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_item_padding_top, Utils.convertDpToPixel(getContext(), 8));
        mItemPaddingRight = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_item_padding_right, Utils.convertDpToPixel(getContext(), 16));
        mItemPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_item_padding_bottom, Utils.convertDpToPixel(getContext(), 8));
        if (mItemPadding > 0)
            mItemPadding = mItemPaddingLeft = mItemPaddingTop = mItemPaddingRight = mItemPaddingBottom;
        mAdapter.setItemPadding(mItemPaddingLeft, mItemPaddingTop, mItemPaddingRight, mItemPaddingBottom);
        mListEmptyTextView.setPadding(mItemPaddingLeft, mItemPaddingTop + Utils.convertDpToPixel(getContext(), 1), mItemPaddingRight, mItemPaddingBottom + Utils.convertDpToPixel(getContext(), 1));

        mItemTextSize = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_item_text_size, Utils.convertDpToPixel(getContext(), 16));
        if (mItemTextSize > 0) {
            mAdapter.setItemTextSize(mItemTextSize);
            mListEmptyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.convertPixelToDp(getContext(), mItemTextSize) - 2);
        }

        mItemTextColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_item_text_color, Color.rgb(51, 51, 51));
        mAdapter.setItemTextColor(mItemTextColor);

        mItemBackgroundColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_item_background_color, Color.rgb(255, 255, 255));
        mSearchLayout.setBackgroundColor(mItemBackgroundColor);
        mAdapter.setItemBackgroundColor(mItemBackgroundColor);

        // Set Spinner Selected Item Layout Attrs
        mSelectedItemTextSize = typedArray.getDimensionPixelSize(R.styleable.os_ObjectSpinner_os_selected_item_text_size, mItemTextSize);
        if (mSelectedItemTextSize > 0)
            mAdapter.setSelectedItemTextSize(mSelectedItemTextSize);

        mSelectedItemTextColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_selected_item_text_color, Color.rgb(24, 24, 24));
        mAdapter.setSelectedItemTextColor(mSelectedItemTextColor);

        mSelectedItemBackgroundColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_selected_item_background_color, Color.rgb(243, 243, 243));
        mAdapter.setSelectedItemBackgroundColor(mSelectedItemBackgroundColor);

        // Set Spinner Search Layout Attrs
        mSearchable = typedArray.getBoolean(R.styleable.os_ObjectSpinner_os_searchable, false);
        if (mSearchable) {
            mSearchLayout.setVisibility(View.VISIBLE);
            mSearchText.addTextChangedListener(mSearchTextWatcher);
            mAdapter.setOnFilterFinishedListener(new OnFilterFinishedListener<T>() {
                @Override
                public void onFilterFinished(List<T> results) {
                    if (!mListEmptyText.isEmpty() && results.isEmpty())
                        mListEmptyTextView.setVisibility(View.VISIBLE);
                    else
                        mListEmptyTextView.setVisibility(View.GONE);
                }
            });
        } else {
            mSearchLayout.setVisibility(View.GONE);
            mSearchText.removeTextChangedListener(mSearchTextWatcher);
            mAdapter.setOnFilterFinishedListener(null);
        }

        mSearchTextColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_search_text_color, Color.rgb(51, 51, 51));
        mSearchText.setTextColor(mSearchTextColor);

        mSearchIconColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_search_icon_color, Color.rgb(192, 192, 192));
        mSearchIcon.setColorFilter(mSearchIconColor);

        mSearchBackgroundColor = typedArray.getColor(R.styleable.os_ObjectSpinner_os_search_background_color, Color.rgb(243, 243, 243));
        Drawable bgDrawable = DrawableCompat.wrap(mSearchText.getBackground());
        DrawableCompat.setTint(bgDrawable, mSearchBackgroundColor);
        mSearchText.setBackground(bgDrawable);

        typedArray.recycle();
    }

    private void animateArrow(boolean shouldRotateUp) {
        int fromDegrees = shouldRotateUp ? 0 : 180;
        int toDegrees = shouldRotateUp ? 180 : 0;
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);
        mArrowIcon.startAnimation(rotate);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mSpinnerLayout.post(new Runnable() {
            @Override
            public void run() {
                mPopupWindow.setWidth(mSpinnerLayout.getWidth() + Utils.convertDpToPixel(getContext(), mShadow ? 10 : 0));
            }
        });

        if (mAdapter != null) {
            CharSequence currentText = mSelectedItemTextView.getText();
            String longestItem = currentText.toString();
            if (mListEmptyTextView.getText().length() > longestItem.length()) {
                longestItem = mListEmptyTextView.getText().toString();
            }
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                String itemText = mAdapter.getItem(i).getSpinnerDelegate();
                if (itemText.length() > longestItem.length()) {
                    longestItem = itemText;
                }
            }
            mSelectedItemTextView.setText(longestItem);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mSelectedItemTextView.setText(currentText);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setItemList(List<T> itemList) {
        mAdapter.setItemList(itemList);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    public void setOnNothingSelectedListener(OnNothingSelectedListener onNothingSelectedListener) {
        this.mOnNothingSelectedListener = onNothingSelectedListener;
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public MaxHeightRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public List<T> getItemList() {
        return mAdapter.getItemList();
    }

    public int getSelectedItemPosition() {
        return mCurrSelectedItemPosition;
    }

    public T getSelectedItem() {
        return mAdapter.getItem(mCurrSelectedItemPosition);
    }

    public boolean isShadow() {
        return mShadow;
    }

    public boolean isSearchable() {
        return mSearchable;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.mPaddingLeft = left;
        this.mPaddingTop = top;
        this.mPaddingRight = right;
        this.mPaddingBottom = bottom;
        mSpinnerLayout.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
    }

    public void setTextSize(int size) {
        this.mTextSize = size;
        mSelectedItemTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.convertPixelToDp(getContext(), mTextSize));
    }

    public void setTextColor(int color) {
        this.mTextColor = color;
        mSelectedItemTextView.setTextColor(mTextColor);
    }

    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
        mSpinnerLayout.setBackgroundColor(mBackgroundColor);
    }

    public void setHint(String hint) {
        this.mHint = hint;
        mSelectedItemTextView.setHint(mHint);
    }

    public void setHintColor(int color) {
        this.mHintColor = color;
        mSelectedItemTextView.setHintTextColor(mHintColor);
    }

    public void setArrowColor(int color) {
        this.mArrowColor = color;
        mArrowIcon.setColorFilter(mArrowColor);
    }

    public void setShadow(boolean bool) {
        this.mShadow = bool;
        mSpinnerLayout.post(new Runnable() {
            @Override
            public void run() {
                mPopupWindow.setWidth(mSpinnerLayout.getWidth() + Utils.convertDpToPixel(getContext(), mShadow ? 10 : 0));
            }
        });
        if (mShadow) {
            mRootLayout.setBackground(getContext().getResources().getDrawable(R.drawable.os_pinner_bg));
            mPopupWindow.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.os_popup_bg));
        } else {
            mRootLayout.setPadding(0, 0, 0, 0);
            mRootLayout.setBackground(null);
            mPopupWindow.setBackgroundDrawable(null);
        }
    }

    public void setListMaxHeight(int maxHeight) {
        this.mListMaxHeight = maxHeight;
        mRecyclerView.setMaxHeight(mListMaxHeight > 0 ? mListMaxHeight : Utils.convertDpToPixel(getContext(), 160));
    }

    public void setListEmptyText(String listEmptyText) {
        this.mListEmptyText = listEmptyText;
        mListEmptyTextView.setText(mListEmptyText);
    }

    public void setItemPadding(int left, int top, int right, int bottom) {
        this.mItemPaddingLeft = left;
        this.mItemPaddingTop = top;
        this.mItemPaddingRight = right;
        this.mItemPaddingBottom = bottom;
        mAdapter.setItemPadding(mItemPaddingLeft, mItemPaddingTop, mItemPaddingRight, mItemPaddingBottom);
        mListEmptyTextView.setPadding(mItemPaddingLeft, mItemPaddingTop + Utils.convertDpToPixel(getContext(), 1), mItemPaddingRight, mItemPaddingBottom + Utils.convertDpToPixel(getContext(), 1));
    }

    public void setItemTextSize(int textSize) {
        this.mItemTextSize = textSize;
        mAdapter.setItemTextSize(mItemTextSize);
        mListEmptyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.convertPixelToDp(getContext(), mItemTextSize) - 2);
    }

    public void setItemTextColor(int color) {
        this.mItemTextColor = color;
        mAdapter.setItemTextColor(mItemTextColor);
    }

    public void setItemBackgroundColor(int color) {
        this.mItemBackgroundColor = color;
        mSearchLayout.setBackgroundColor(mItemBackgroundColor);
        mAdapter.setItemBackgroundColor(mItemBackgroundColor);
    }

    public void setSelectedItemTextSize(int textSize) {
        this.mSelectedItemTextSize = textSize;
        mAdapter.setSelectedItemTextSize(mSelectedItemTextSize);
    }

    public void setSelectedItemTextColor(int color) {
        this.mSelectedItemTextColor = color;
        mAdapter.setSelectedItemTextColor(mSelectedItemTextColor);
    }

    public void setSelectedItemBackgroundColor(int color) {
        this.mSelectedItemBackgroundColor = color;
        mAdapter.setSelectedItemBackgroundColor(mSelectedItemBackgroundColor);
    }

    public void setSearchable(boolean searchable) {
        this.mSearchable = searchable;
        if (mSearchable) {
            mSearchLayout.setVisibility(View.VISIBLE);
            mSearchText.removeTextChangedListener(mSearchTextWatcher);
            mSearchText.addTextChangedListener(mSearchTextWatcher);
            mAdapter.setOnFilterFinishedListener(new OnFilterFinishedListener<T>() {
                @Override
                public void onFilterFinished(List<T> results) {
                    if (!mListEmptyText.isEmpty() && results.isEmpty())
                        mListEmptyTextView.setVisibility(View.VISIBLE);
                    else
                        mListEmptyTextView.setVisibility(View.GONE);
                }
            });
        } else {
            mSearchLayout.setVisibility(View.GONE);
            mSearchText.removeTextChangedListener(mSearchTextWatcher);
            mAdapter.setOnFilterFinishedListener(null);
        }
    }

    public void setSearchTextColor(int color) {
        this.mSearchTextColor = color;
        mSearchText.setTextColor(mSearchTextColor);
    }

    public void setSearchIconColor(int color) {
        this.mSearchIconColor = mSearchIconColor;
        mSearchIcon.setColorFilter(mSearchIconColor);
    }

    public void setSearchBackgroundColor(int color) {
        this.mSearchBackgroundColor = color;
        Drawable bgDrawable = DrawableCompat.wrap(mSearchText.getBackground());
        DrawableCompat.setTint(bgDrawable, mSearchBackgroundColor);
        mSearchText.setBackground(bgDrawable);
    }

    public void setSelection(int position) {
        mSelectedItemTextView.setText(mAdapter.getItem(position).getSpinnerDelegate());

        mPrevSelectedItemPosition = mCurrSelectedItemPosition;
        mCurrSelectedItemPosition = position;

        if (mOnItemSelectedListener != null && mCurrSelectedItemPosition != mPrevSelectedItemPosition)
            mOnItemSelectedListener.onItemSelected(ObjectSpinner.this, position, mAdapter.getItem(position));
        else if (mOnNothingSelectedListener != null && mCurrSelectedItemPosition == mPrevSelectedItemPosition)
            mOnNothingSelectedListener.onNothingSelected(ObjectSpinner.this);
    }

    public interface Delegate {

        String getSpinnerDelegate();
    }

    public interface OnItemSelectedListener {

        void onItemSelected(ObjectSpinner view, int position, Object item);
    }

    public interface OnNothingSelectedListener {

        void onNothingSelected(ObjectSpinner view);
    }
}