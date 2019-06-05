package com.ppfuns.ppfunstv.view.MarqueeView;

import android.content.Context;
import android.text.TextUtils;

import com.ppfuns.ppfunstv.data.MarqueeEntity;
import com.ppfuns.ppfunstv.utils.GsonUtil;
import com.ppfuns.ppfunstv.utils.Utils;

import java.util.Map;

/**
 *
 * Created by flyzebra on 17-3-29.
 */
public class MarqueeFactory {
    private static final float textScale = 0.56f;

    public static IMarquee createView(Context context, MarqueeEntity mq){
        IMarquee marquee = null;
        String text = "";
        if(mq.getText()!=null){
            Map<String, String> str = GsonUtil.json2Map(mq.getText());
            text = Utils.getLocalLanguageString(str);
        }

        if(TextUtils.isEmpty(text)){
            return null;
        }

        if(mq.isGlobal()){
            marquee = GlobalMarqueeView.getInstance(context);
        }else{
            marquee = new LocalMarqueeView(context);
        }
        marquee.setPoint(mq.getX(),mq.getY(),mq.getWidth(),mq.getHeight())
                .setText(text)
                .setTextSize((int) (mq.getSize()*textScale))
                .setTextColor(mq.getColor())
                .setDirection(mq.getDirection())
                .setDuration(mq.getSpeed())
                .init();
        return marquee;
    }

}
