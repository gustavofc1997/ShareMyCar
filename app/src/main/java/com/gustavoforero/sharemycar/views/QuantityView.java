package com.gustavoforero.sharemycar.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gustavoforero.sharemycar.R;

/**
 * Created by Gustavo on 29/11/2017.
 */

public class QuantityView extends LinearLayout implements View.OnClickListener {

	private Drawable quantityBackground, addButtonBackground, removeButtonBackground;

	private String addButtonText, removeButtonText;

	private int quantity;
	private boolean quantityDialog;
	private int maxQuantity = Integer.MAX_VALUE, minQuantity = Integer.MAX_VALUE;
	private int quantityPadding;

	private int quantityTextColor, addButtonTextColor, removeButtonTextColor;

	private android.widget.TextView mButtonAdd, mButtonRemove;
	private TextView mTextViewQuantity;

	private String labelDialogTitle = "Change Quantity";
	private String labelPositiveButton = "Change";
	private String labelNegativeButton = "Cancel";

	public interface OnQuantityChangeListener {
		void onQuantityChanged(int oldQuantity, int newQuantity, boolean programmatically);

		void onLimitReached();

	}

	private OnQuantityChangeListener onQuantityChangeListener;
	private OnClickListener mTextViewClickListener;

	public QuantityView(Context context) {
		super (context);
		init (null, 0);
	}

	public QuantityView(Context context, AttributeSet attrs) {
		super (context, attrs);
		init (attrs, 0);
	}

	public QuantityView(Context context, AttributeSet attrs, int defStyle) {
		super (context, attrs, defStyle);
		init (attrs, defStyle);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void init(AttributeSet attrs, int defStyle) {
		final TypedArray a = getContext ().obtainStyledAttributes (attrs, R.styleable.QuantityView, defStyle, 0);

		addButtonText = getResources ().getString (R.string.add);
		if (a.hasValue (R.styleable.QuantityView_qv_addButtonText)) {
			addButtonText = a.getString (R.styleable.QuantityView_qv_addButtonText);
		}
		addButtonBackground = ContextCompat.getDrawable (getContext (), R.drawable.btn_selector);
		if (a.hasValue (R.styleable.QuantityView_qv_addButtonBackground)) {
			addButtonBackground = a.getDrawable (R.styleable.QuantityView_qv_addButtonBackground);
		}
		addButtonTextColor = a.getColor (R.styleable.QuantityView_qv_addButtonTextColor, Color.WHITE);

		removeButtonText = getResources ().getString (R.string.remove);
		if (a.hasValue (R.styleable.QuantityView_qv_removeButtonText)) {
			removeButtonText = a.getString (R.styleable.QuantityView_qv_removeButtonText);
		}
		removeButtonBackground = ContextCompat.getDrawable (getContext (), R.drawable.btn_selector);
		if (a.hasValue (R.styleable.QuantityView_qv_removeButtonBackground)) {
			removeButtonBackground = a.getDrawable (R.styleable.QuantityView_qv_removeButtonBackground);
		}
		removeButtonTextColor = a.getColor (R.styleable.QuantityView_qv_removeButtonTextColor, Color.WHITE);

		quantity = a.getInt (R.styleable.QuantityView_qv_quantity, 0);
		maxQuantity = a.getInt (R.styleable.QuantityView_qv_maxQuantity, Integer.MAX_VALUE);
		minQuantity = a.getInt (R.styleable.QuantityView_qv_minQuantity, 0);

		quantityPadding = (int) a.getDimension (R.styleable.QuantityView_qv_quantityPadding, pxFromDp (5));
		quantityTextColor = a.getColor (R.styleable.QuantityView_qv_quantityTextColor, Color.WHITE);
		quantityBackground = ContextCompat.getDrawable (getContext (), R.drawable.bg_selector);
		if (a.hasValue (R.styleable.QuantityView_qv_quantityBackground)) {
			quantityBackground = a.getDrawable (R.styleable.QuantityView_qv_quantityBackground);
		}

		quantityDialog = a.getBoolean (R.styleable.QuantityView_qv_quantityDialog, true);

		a.recycle ();
		int size = pxFromDp (25);
		int widthQuantity = pxFromDp (35);

		mButtonAdd = new android.widget.TextView (getContext ());
		mButtonAdd.setGravity (Gravity.CENTER);
		setAddButtonBackground (addButtonBackground);
		setAddButtonText (addButtonText);
		setAddButtonTextColor (addButtonTextColor);

		mButtonRemove = new android.widget.TextView (getContext ());
		mButtonRemove.setGravity (Gravity.CENTER);
		setRemoveButtonBackground (removeButtonBackground);
		setRemoveButtonText (removeButtonText);
		setRemoveButtonTextColor (removeButtonTextColor);
		mTextViewQuantity = new TextView (getContext ());
		mTextViewQuantity.setGravity (Gravity.CENTER);
		setQuantityTextColor (quantityTextColor);
		setQuantity (quantity);
		setQuantityBackground (quantityBackground);
		setQuantityPadding (quantityPadding);

		setOrientation (HORIZONTAL);

		addView (mButtonRemove, size, size);
		mTextViewQuantity.setPadding(20,10,20,10);
		addView (mTextViewQuantity, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView (mButtonAdd, size, size);

		mButtonAdd.setOnClickListener (this);
		mButtonRemove.setOnClickListener (this);
		mTextViewQuantity.setOnClickListener (this);
	}


	public void setQuantityClickListener(OnClickListener ocl) {
		mTextViewClickListener = ocl;
	}

	@Override
	public void onClick(View v) {
		if (v == mButtonAdd) {
			if (quantity + 1 > maxQuantity) {
				if (onQuantityChangeListener != null) onQuantityChangeListener.onLimitReached ();
			} else {
				int oldQty = quantity;
				quantity += 1;
				mTextViewQuantity.setText (String.valueOf (quantity));
				if (onQuantityChangeListener != null)
					onQuantityChangeListener.onQuantityChanged (oldQty, quantity, false);
			}
		} else if (v == mButtonRemove) {
			if (quantity - 1 < minQuantity) {
				if (onQuantityChangeListener != null) onQuantityChangeListener.onLimitReached ();
			} else {
				int oldQty = quantity;
				quantity -= 1;
				mTextViewQuantity.setText (String.valueOf (quantity));
				if (onQuantityChangeListener != null)
					onQuantityChangeListener.onQuantityChanged (oldQty, quantity, false);
			}
		}
	}

	public void hideKeyboard(View focus) {
		InputMethodManager inputManager = (InputMethodManager) getContext ().getSystemService (Context.INPUT_METHOD_SERVICE);
		if (focus != null) {
			inputManager.hideSoftInputFromWindow (focus.getWindowToken (), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}


	public OnQuantityChangeListener getOnQuantityChangeListener() {
		return onQuantityChangeListener;
	}

	public void setOnQuantityChangeListener(OnQuantityChangeListener onQuantityChangeListener) {
		this.onQuantityChangeListener = onQuantityChangeListener;
	}

	public Drawable getQuantityBackground() {
		return quantityBackground;
	}

	public void setQuantityBackground(Drawable quantityBackground) {
		this.quantityBackground = quantityBackground;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mTextViewQuantity.setBackground (quantityBackground);
		} else {
			mTextViewQuantity.setBackgroundDrawable (quantityBackground);
		}
	}

	public Drawable getAddButtonBackground() {
		return addButtonBackground;
	}

	public void setAddButtonBackground(Drawable addButtonBackground) {
		this.addButtonBackground = addButtonBackground;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mButtonAdd.setBackground (addButtonBackground);
		} else {
			mButtonAdd.setBackgroundDrawable (addButtonBackground);
		}
	}

	public Drawable getRemoveButtonBackground() {
		return removeButtonBackground;
	}

	public void disableButtons(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mButtonAdd.setBackground ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector_disable));
			mButtonRemove.setBackground ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector_disable));
		} else {
			mButtonAdd.setBackgroundDrawable ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector_disable));
			mButtonRemove.setBackgroundDrawable ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector_disable));
		}
	}public void disableButtonsWithMinus(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mButtonAdd.setBackground ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector_disable));
			mButtonRemove.setBackground ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector));
		} else {
			mButtonAdd.setBackgroundDrawable ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector_disable));
			mButtonRemove.setBackgroundDrawable ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector));
		}
	}
	public void enableButtons(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mButtonRemove.setBackground ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector));
			mButtonAdd.setBackground ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector));
		} else {
			mButtonRemove.setBackgroundDrawable ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector));
			mButtonAdd.setBackgroundDrawable ( ContextCompat.getDrawable (getContext (), R.drawable.btn_selector));
		}
	}
	public void setRemoveButtonBackground(Drawable removeButtonBackground) {
		this.removeButtonBackground = removeButtonBackground;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mButtonRemove.setBackground (removeButtonBackground);
		} else {
			mButtonRemove.setBackgroundDrawable (removeButtonBackground);
		}
	}

	public String getAddButtonText() {
		return addButtonText;
	}

	public void setAddButtonText(String addButtonText) {
		this.addButtonText = addButtonText;
		mButtonAdd.setText (addButtonText);
	}

	public String getRemoveButtonText() {
		return removeButtonText;
	}

	public void setRemoveButtonText(String removeButtonText) {
		this.removeButtonText = removeButtonText;
		mButtonRemove.setText (removeButtonText);
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int newQuantity) {
		boolean limitReached = false;

		if (newQuantity > maxQuantity) {
			newQuantity = maxQuantity;
			limitReached = true;
		}
		if (newQuantity < minQuantity) {
			newQuantity = minQuantity;
			limitReached = true;
		}
		if (!limitReached) {
//            if (onQuantityChangeListener != null) {
//                onQuantityChangeListener.onQuantityChanged(quantity, newQuantity, true);
//            }
			this.quantity = newQuantity;
			mTextViewQuantity.setText (String.valueOf (this.quantity));
		} else {
			if (onQuantityChangeListener != null) onQuantityChangeListener.onLimitReached ();
		}
	}


	public int getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(int maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public int getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(int minQuantity) {
		this.minQuantity = minQuantity;
	}

	public int getQuantityPadding() {
		return quantityPadding;
	}

	public void setQuantityPadding(int quantityPadding) {
		this.quantityPadding = quantityPadding;
		mTextViewQuantity.setPadding (quantityPadding, 0, quantityPadding, 0);
	}

	public int getQuantityTextColor() {
		return quantityTextColor;
	}

	public void setQuantityTextColor(int quantityTextColor) {
		this.quantityTextColor = quantityTextColor;
		mTextViewQuantity.setTextColor (quantityTextColor);
	}

	public void setQuantityTextColorRes(int quantityTextColorRes) {
		this.quantityTextColor = ContextCompat.getColor (getContext (), quantityTextColorRes);
		mTextViewQuantity.setTextColor (quantityTextColor);
	}

	public int getAddButtonTextColor() {
		return addButtonTextColor;
	}

	public void setAddButtonTextColor(int addButtonTextColor) {
		this.addButtonTextColor = addButtonTextColor;
		mButtonAdd.setTextColor (addButtonTextColor);
	}

	public void setAddButtonTextColorRes(int addButtonTextColorRes) {
		this.addButtonTextColor = ContextCompat.getColor (getContext (), addButtonTextColorRes);
		mButtonAdd.setTextColor (addButtonTextColor);
	}

	public int getRemoveButtonTextColor() {
		return removeButtonTextColor;
	}

	public void setRemoveButtonTextColor(int removeButtonTextColor) {
		this.removeButtonTextColor = removeButtonTextColor;
		mButtonRemove.setTextColor (removeButtonTextColor);
	}

	public void setRemoveButtonTextColorRes(int removeButtonTextColorRes) {
		this.removeButtonTextColor = ContextCompat.getColor (getContext (), removeButtonTextColorRes);
		mButtonRemove.setTextColor (removeButtonTextColor);
	}

	public String getLabelDialogTitle() {
		return labelDialogTitle;
	}

	public void setLabelDialogTitle(String labelDialogTitle) {
		this.labelDialogTitle = labelDialogTitle;
	}

	public String getLabelPositiveButton() {
		return labelPositiveButton;
	}

	public void setLabelPositiveButton(String labelPositiveButton) {
		this.labelPositiveButton = labelPositiveButton;
	}

	public String getLabelNegativeButton() {
		return labelNegativeButton;
	}

	public void setLabelNegativeButton(String labelNegativeButton) {
		this.labelNegativeButton = labelNegativeButton;
	}

	public void setQuantityDialog(boolean quantityDialog) {
		this.quantityDialog = quantityDialog;
	}

	public boolean isQuantityDialog() {
		return quantityDialog;
	}

	private int dpFromPx(final float px) {
		return (int) (px / getResources ().getDisplayMetrics ().density);
	}

	private int pxFromDp(final float dp) {
		return (int) (dp * getResources ().getDisplayMetrics ().density);
	}


	public static boolean isValidNumber(String string) {
		try {
			return Integer.parseInt (string) <= Integer.MAX_VALUE;
		} catch (Exception e) {
			return false;
		}
	}
}
