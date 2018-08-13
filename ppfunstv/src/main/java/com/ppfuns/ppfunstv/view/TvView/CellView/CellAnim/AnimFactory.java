package com.ppfuns.ppfunstv.view.TvView.CellView.CellAnim;

/**
 *
 * Created by FlyZebra on 2016/7/19.
 */
public class AnimFactory implements IAnimTYPE {

    private AnimFactory(){
    }

    public static AnimFactory getInstance(){
        return AnimFactoryHolder.sInstance;
    }

    private static class AnimFactoryHolder {
        public static final AnimFactory sInstance = new AnimFactory();
    }

    public IBaseAnim createAnim(String type){
        IBaseAnim baseAnim = null;
        switch(type){
            case Rotate3D:
                baseAnim = new Rotate3DAnim();
                break;
            case LOAD:
                baseAnim = new LoadAnim();
                baseAnim.create(null);
                break;
            case SCALE:
            default:
                baseAnim = new Rotate3DAnim();
                break;
        }
        return baseAnim;
    }
}
