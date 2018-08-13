package com.ppfuns.ppfunstv.view.TvView;

import com.ppfuns.ppfunstv.data.CellEntity;

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
    List<CellEntity> getSortedEntity(List<CellEntity> entityList);

    /**
     * 返回最左上角的entity的下标
     *
     * @param entityList
     * @return -1表示获取失败
     */
    int getLeftUpIndex(List<CellEntity> entityList);

    /**
     * 返回最右上角的entity的下标
     *
     * @param entityList
     * @return -1表示获取失败
     */
    int getRightUpIndex(List<CellEntity> entityList);

    /**
     * 返回最左下角的entity的下标
     *
     * @param entityList
     * @return -1表示获取失败
     */
    int getLeftDownIndex(List<CellEntity> entityList);

    /**
     * 返回最右下角的entity的下标
     *
     * @param entityList
     * @return -1表示获取失败
     */
    int getRightDownIndex(List<CellEntity> entityList);

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
    int getNext(List<CellEntity> entityList,int cur, int index1, int index2,int direction);


    Queue<Integer> getLeftQueue(CellEntity entity,List<CellEntity> entityList);

    Queue<Integer> getRightQueue(CellEntity entity,List<CellEntity> entityList);

    Queue<Integer> getUpQueue(CellEntity entity,List<CellEntity> entityList);

    Queue<Integer> getDownQueue(CellEntity entity,List<CellEntity> entityList);
}
