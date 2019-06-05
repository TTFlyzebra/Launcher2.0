package com.ppfuns.ppfunstv.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/6/13.
 */
public class CellBean implements Serializable{

    /**
     * msg : 下载成功
     * ret : 0
     * cellList : [{"tabId":10001,"cellId":10001,"type":1,"x":200,"y":200,"width":100,"height":100,"stateStly":[{"state":1,"size":30,"color":"#ffff0000","alpah":0.5},{"state":2,"size":30,"color":"#ffff00ff","alpah":0.5}],"imgUrl":"http://aaa.bbb.png","intent":"","animation":[{"state":0,"type":"moveBy","delay":0,"duration":100},{"state":1,"type":"moveBy","delay":0,"duration":100}]},{"tabId":10001,"cellId":10002,"type":3,"x":400,"y":200,"width":100,"height":100,"stateStly":[{"state":1,"size":30,"color":"#ffff0000","alpah":0.5},{"state":2,"size":30,"color":"#ffff00ff","alpah":0.5}],"imgUrl":"http://aaa.bbb.png","intent":"","animation":[{"state":0,"type":"moveBy","delay":0,"duration":100},{"state":1,"type":"moveBy","delay":0,"duration":100}]},{"tabId":10001,"cellId":10001,"type":1,"x":600,"y":200,"width":100,"height":100,"stateStly":[{"state":1,"size":30,"color":"#ffff0000","alpah":0.5},{"state":2,"size":30,"color":"#ffff00ff","alpah":0.5}],"imgUrl":"http://aaa.bbb.png","intent":"","animation":[{"state":0,"type":"moveBy","delay":0,"duration":100},{"state":1,"type":"moveBy","delay":0,"duration":100}]}]
     */

    public String msg;
    public int ret;
    public List<CellEntity> cellList;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setCellList(ArrayList<CellEntity> cellList) {
        this.cellList = cellList;
    }

    public String getMsg() {
        return msg;
    }

    public int getRet() {
        return ret;
    }

    public List<CellEntity> getCellList() {
        return cellList;
    }

    public boolean isValid(){
        if(ret ==0 && msg !=null){
            return true;
        }
        return  false;
    }

    @Override
    public String toString() {
        return "CellBean{" +
                "msg='" + msg + '\'' +
                ", ret=" + ret +
                ", cellList=" + cellList +
                '}';
    }
}
