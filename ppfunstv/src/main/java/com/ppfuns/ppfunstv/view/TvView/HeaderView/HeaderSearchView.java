package com.ppfuns.ppfunstv.view.TvView.HeaderView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.ppfuns.ppfunstv.R;
import com.ppfuns.ppfunstv.constant.Constants;
import com.ppfuns.ppfunstv.view.TvView.CellView.CellClickAction.CommondTool;

/**
 * Created by miles on 2017/6/15 0015.
 */

public class HeaderSearchView extends ImageView implements IHeaderImage {
    private Context mContext;
    public HeaderSearchView(Context context){
        this(context,null,0);
    }

    public HeaderSearchView(Context context, AttributeSet attrs){
        this(context, attrs,0);
    }

    public HeaderSearchView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mContext = context;
//        Glide.with(mContext).load(R.drawable.tv_header_search_unfocus).into(this);
        setImageResource(R.drawable.tv_header_search_unfocus);
    }

    @Override
    public void setFocusImage(boolean isFocus){
        if(isFocus){
//            Glide.with(mContext).load(R.drawable.tv_header_search_focus).into(this);
            setImageResource(R.drawable.tv_header_search_focus);
        }else{
//            Glide.with(mContext).load(R.drawable.tv_header_search_unfocus).into(this);
            setImageResource(R.drawable.tv_header_search_unfocus);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                String data = "searchType=all";
                CommondTool.execStartActivityAndShowTip(mContext, Constants.Action.VOD_SEARCH, data, "", false);
                break;
        }
        return super.onKeyDown(keyCode,event);
    }

}
