package gr.plushost.prototypeapp.widgets;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import android.content.Context;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RadioGroup extends android.widget.RadioGroup implements Groupable<RadioButton> {
    private int radioButtonsCount = 0;
    public RadioGroup(final Context context){
        super(context);
    }
    public RadioGroup(final Context context, final String headerTitle) {
        super(context);
        final TextView header = new TextView(getContext());
        header.setText(headerTitle);
        LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addView(header, params);
    }

    public RadioGroup(final Context context, final String headerTitle, final AttributeSet attrs) {
        super(context, attrs);
        final TextView header = new TextView(getContext());
        header.setText(headerTitle);
        LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addView(header, params);
    }

    @Override
    public RadioButton addItem(final CharSequence text, final Object tag) {
        return addItem(text, tag, false);
    }

    @Override
    public RadioButton addItem(final CharSequence text, final Object tag, final boolean checked) {
        checkNotNull(text, "Must supply a text");
        checkNotNull(tag, "Must supply a tag");
        int checkedRadioButtonId = getCheckedRadioButtonId();
        clearCheck();
        final RadioButton radioButton = new RadioButton(getContext());
        radioButton.setId(tag.hashCode());
        radioButton.setText(text);
        radioButton.setTag(tag);
        radioButton.setChecked(checked);
        addView(radioButton, new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        if (checked) {
            checkedRadioButtonId = radioButton.getId();
        }
        if (checkedRadioButtonId != View.NO_ID) {
            check(checkedRadioButtonId);
        }
        radioButtonsCount++;
        return radioButton;
    }

    @Override
    public RadioButton addItem(final Spannable text, final Object tag) {
        return addItem(text, tag, false);
    }

    @Override
    public RadioButton addItem(final Spannable text, final Object tag, final boolean checked) {
        checkNotNull(text, "Must supply a text");
        checkNotNull(tag, "Must supply a tag");
        int checkedRadioButtonId = getCheckedRadioButtonId();
        clearCheck();
        final RadioButton radioButton = new RadioButton(getContext());
        radioButton.setId(tag.hashCode());
        radioButton.setText(text);
        radioButton.setTag(tag);
        radioButton.setChecked(checked);
        addView(radioButton, new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        if (checked) {
            checkedRadioButtonId = radioButton.getId();
        }
        if (checkedRadioButtonId != View.NO_ID) {
            check(checkedRadioButtonId);
        }
        radioButtonsCount++;
        return radioButton;
    }

    @Override
    public RadioButton findWithTag(final Object tag) {
        checkNotNull(tag, "Must supply a tag");
        return (RadioButton) findViewWithTag(tag);
    }

    @Override
    public int size() {
        return getChildCount();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        removeAllViews();
    }

    @Override
    public List<RadioButton> getAllChecked() {
        final List<RadioButton> checkedOnes = newArrayList();
        final int checkedRadioButtonId = getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) findViewById(checkedRadioButtonId);

        if (checkedRadioButtonId != View.NO_ID && radioButton.isChecked()) {
            checkedOnes.add(radioButton);
        }
        return checkedOnes;
    }

    public String getCheckedTag() {
        final List<RadioButton> checkedOnes = newArrayList();
        final int checkedRadioButtonId = getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) findViewById(checkedRadioButtonId);

        if (checkedRadioButtonId != View.NO_ID && radioButton.isChecked()) {
            return (String) radioButton.getTag();
        }
        return "";
    }

    @Override
    public <F> void populate(final List<F> items, final Groupable.Predicate<F> predicate) {
        clear();
        for (final F item : items) {
            addItem(predicate.applyToText(item), predicate.applyToTag(item));
        }
        if (!items.isEmpty()) {
            setChecked(predicate.applyToTag(items.get(0)), true);
        }
    }

    @Override
    public void setChecked(final Object tag, final boolean checked) {
        checkNotNull(tag);
        final RadioButton item = findWithTag(tag);
        checkArgument(item != null, "No item found with tag: " + tag);
        assert item != null;
        item.setChecked(checked);
    }

    public List<RadioButton> getAllRadioButtons(){
        final List<RadioButton> radioButtons = newArrayList();

        for(int i = 0; i < radioButtonsCount; i++){
            RadioButton radioButton = (RadioButton) findViewById(i);
            radioButtons.add(radioButton);
        }

        return radioButtons;
    }

}
