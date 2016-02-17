package gr.plushost.prototypeapp.widgets;

import java.util.List;

import android.content.Context;
import android.text.Spannable;
import android.util.AttributeSet;
import android.widget.CompoundButton;


/**
* Representation of a group-like widget.
*
* @author Matias Dumrauf
* @param <T>
* @since Mar 15, 2013
*/
public interface Groupable<T extends CompoundButton> {
    public interface Predicate<F> {
        CharSequence applyToText(F item);
        <K> K applyToTag(F item);
    }

    T addItem(CharSequence text, Object tag);

    T addItem(CharSequence text, Object tag, boolean checked);

    T addItem(Spannable text, Object tag);

    T addItem(Spannable text, Object tag, boolean checked);

    T findWithTag(Object tag);

    int size();

    boolean isEmpty();

    void clear();

    List<T> getAllChecked();

    <F> void populate(List<F> items, Predicate<F> predicate);

    void setChecked(Object tag, boolean checked);

    public interface GroupableFactory {
        <T extends CompoundButton> T craete(Context context);
        <T extends CompoundButton> T craete(Context context, AttributeSet attrs);
        <T extends CompoundButton> T craete(Context context, AttributeSet attrs, int defStyle);
    }
}
