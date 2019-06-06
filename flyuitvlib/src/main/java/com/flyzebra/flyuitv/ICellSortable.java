package com.flyzebra.flyuitv;

import com.flyzebra.flyui.bean.CellBean;

import java.util.List;
import java.util.Queue;

/**
 *
 * Created by lenovo on 2016/6/16.
 */
public interface ICellSortable {
    /**
     * 返回通过list  list内部的entity经过计算,构建好了上下左右的关系
     *
     * @param entityList
     * @return
     */
    List<CellBean> getSortedEntity(List<CellBean> entityList);

    /**
     * 返回最左上角的entity的下标
     *
     * @param entityList
     * @return -1表示获取失败
     */
    int getLeftUpIndex(List<CellBean> entityList);

    /**
     * 返回最右上角的entity的下标
     *
     * @param entityList
     * @return -1表示获取失败
     */
    int getRightUpIndex(List<CellBean> entityList);

    /**
     * 返回最左下角的entity的下标
     *
     * @param entityList
     * @return -1表示获取失败
     */
    int getLeftDownIndex(List<CellBean> entityList);

    /**
     * 返回最右下角的entity的下标
     *
     * @param entityList
     * @return -1表示获取失败
     */
    int getRightDownIndex(List<CellBean> entityList);

    /**
     * 从index1和index2中选一个距离cur更近的
     * @param entityList
     * @param cur 当前pos
     * @param index1
     * @param index2
     * @param direction 0:上
     *                  1：下
     *                  2：左
     *                  3：右
     * @return 返回一个更近的下标
     */
    @Deprecated
    int getNext(List<CellBean> entityList, int cur, int index1, int index2, int direction);


    Queue<Integer> getLeftQueue(CellBean entity, List<CellBean> entityList);

    Queue<Integer> getRightQueue(CellBean entity, List<CellBean> entityList);

    Queue<Integer> getUpQueue(CellBean entity, List<CellBean> entityList);

    Queue<Integer> getDownQueue(CellBean entity, List<CellBean> entityList);
}
