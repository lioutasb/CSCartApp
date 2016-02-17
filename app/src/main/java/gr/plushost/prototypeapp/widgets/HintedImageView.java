package gr.plushost.prototypeapp.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by billiout on 2/4/2015.
 */
public class HintedImageView extends ImageView implements View.OnLongClickListener
{
    private OnLongClickListener mOnLongClickListener;

    public HintedImageView(Context context)
    {
        super(context);

        setOnLongClickListener(this);
    }

    public HintedImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setOnLongClickListener(this);
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l)
    {
        if (l == this)
        {
            super.setOnLongClickListener(l);
            return;
        }

        mOnLongClickListener = l;
    }

    @Override
    public boolean onLongClick(View v)
    {
        if (mOnLongClickListener != null)
        {
            if (!mOnLongClickListener.onLongClick(v))
            {
                handleLongClick(v);
                return true;
            }
        }
        else
        {
            handleLongClick(v);
            return true;
        }

        return false;
    }

    private void handleLongClick(View view)
    {
        String contentDesc = getContentDescription().toString();
        if (!TextUtils.isEmpty(contentDesc))
        {
            final int[] screenPos = new int[2];
            final Rect displayFrame = new Rect();
            view.getLocationOnScreen(screenPos);
            view.getWindowVisibleDisplayFrame(displayFrame);
            final Context context = view.getContext();
            final int width = view.getWidth();
            final int height = view.getHeight();
            final int midy = screenPos[1] + height/2;
            final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            Toast cheatSheet = Toast.makeText(context, contentDesc, Toast.LENGTH_SHORT);
            System.out.println(width + " " + height + " " + midy + " " + screenWidth + "  ");
            if (midy < displayFrame.height()) {
                //cheatSheet.setGravity(Gravity.TOP, screenWidth - screenPos[0] - width/2, height);
            } else {
                //cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
            }
            cheatSheet.show();
        }
    }
}
