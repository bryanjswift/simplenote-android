package com.bryanjswift.simplenote.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import com.bryanjswift.simplenote.Constants;

/**
 * Custom EditText to allow the ScrollView to handle displaying the proper rectangle by killing requests for
 * different rectangles
 * @author bryanjswift
 */
public class ScrollWrappableEditText extends EditText {
    private static final String LOGGING_TAG = Constants.TAG + ScrollWrappableEditText.class.getSimpleName();
    private OnChangeListener onChangeListener = null;
    private String oldText = null;

    /**
     * Default constructor
     * @param context to which this View is being added
     */
    public ScrollWrappableEditText(Context context) {
        super(context);
        oldText = getText().toString();
    }

    /**
     * Default constructor
     * @param context to which this View is being added
     * @param attrs used when creating the View
     */
    public ScrollWrappableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        oldText = getText().toString();
    }

    /**
     * Default constructor
     * @param context to which this View is being added
     * @param attrs used when creating the View
     * @param defStyle id of style definition to add to attrs
     */
    public ScrollWrappableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        oldText = getText().toString();
    }

    /**
     * Don't request this view move if it is wrapped in a ScrollView
     * @see android.view.View#requestRectangleOnScreen(android.graphics.Rect, boolean)
     */
    @Override
    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        // Detect text changes here because onKey only fires for certain key presses
        final String newText = getText().toString();
        Log.d(LOGGING_TAG, String.format("Comparing %s to %s", newText, oldText));
        if (onChangeListener != null && !newText.equals(oldText)) {
            onChangeListener.onChange(this, oldText, newText);
            oldText = newText;
        }
        // Always return true, the ScrollView around this will handle the proper rectangle being on screen
        if (getParent() instanceof ScrollView) {
            ((ScrollView) getParent()).scrollBy(rectangle.left - getLeft(), rectangle.top - getTop());
            return true;
        } else {
            return super.requestRectangleOnScreen(rectangle, immediate);
        }
    }

    /**
     * Get the OnChangeListener
     * @return OnChangeListener set on thie instance
     */
    public OnChangeListener getOnChangeListener() {
        return onChangeListener;
    }

    /**
     * Set the OnChangeListener
     * @param onChangeListener to set
     */
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    /**
     * Interface for firing change events on edit text
     */
    public interface OnChangeListener {
        public void onChange(View v, String oldText, String newText);
    }
}