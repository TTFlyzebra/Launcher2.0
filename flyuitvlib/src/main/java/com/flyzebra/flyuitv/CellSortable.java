package com.flyzebra.flyuitv;


import com.flyzebra.flyui.bean.CellBean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 修改焦点移动算法，根据交集计算
 * 为传入的CellBean类,构建好上下左右的位置关系索引
 * Created by Nelon on 2016/6/16.
 */
public class CellSortable implements ICellSortable {
    public final static String TAG = CellSortable.class.getSimpleName();
    public static final int NO_LEFT = -1;
    public static final int NO_UP = -1;
    public static final int NO_RIGHT = Integer.MAX_VALUE;
    public static final int NO_DOWN = Integer.MAX_VALUE;

    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;


    @Override
    public List<CellBean> getSortedEntity(List<CellBean> entityList) {
        for (CellBean cell : entityList) {

            cell.lefts = getLeftQueue(cell, entityList);
            cell.rights = getRightQueue(cell, entityList);
            cell.ups = getUpQueue(cell, entityList);
            cell.downs = getDownQueue(cell, entityList);

            cell.left = cell.lefts.poll();
            cell.right = cell.rights.poll();
            cell.up = cell.ups.poll();
            cell.down = cell.downs.poll();

        }

        return entityList;
    }

    @Override
    public int getLeftUpIndex(List<CellBean> entityList) {
        for (CellBean en : entityList) {
            if (en.left == NO_LEFT && en.up == NO_UP) {
                return entityList.indexOf(en);
            }
        }
        return -1;
    }

    @Override
    public int getRightUpIndex(List<CellBean> entityList) {
        for (CellBean en : entityList) {
            if (en.right == NO_RIGHT && en.up == NO_UP) {
                return entityList.indexOf(en);
            }
        }
        return -1;
    }

    @Override
    public int getLeftDownIndex(List<CellBean> entityList) {
        for (CellBean en : entityList) {
            if (en.left == NO_LEFT && en.down == NO_DOWN) {
                return entityList.indexOf(en);
            }
        }
        return -1;
    }

    @Override
    public int getRightDownIndex(List<CellBean> entityList) {
        for (CellBean en : entityList) {
            if (en.right == NO_RIGHT && en.down == DOWN) {
                return entityList.indexOf(en);
            }
        }
        return -1;
    }

    @Override
    public int getNext(List<CellBean> entityList, int cur, int index1, int index2, int direction) {
        int pos = -1;
        try {
            CellBean cell1 = null;
            CellBean cell2 = null;
            CellBean curCell = entityList.get(cur);
            if (direction < 2) {//上下
                if (0 <= index1 && index1 < entityList.size()) {
                    cell1 = entityList.get(index1);
                    if (!betweenX(curCell, cell1)) {
                        cell1 = null;
                    }
                }
                if (0 <= index2 && index2 < entityList.size()) {
                    cell2 = entityList.get(index2);
                    if (!betweenX(curCell, cell2)) {
                        cell2 = null;
                    }
                }
            } else {//左右
                if (0 <= index1 && index1 < entityList.size()) {
                    cell1 = entityList.get(index1);
                    if (!betweenY(curCell, cell1)) {
                        cell1 = null;
                    }
                }
                if (0 <= index2 && index2 < entityList.size()) {
                    cell2 = entityList.get(index2);
                    if (!betweenY(curCell, cell2)) {
                        cell2 = null;
                    }
                }
            }


            if (cell1 != null && cell2 != null) {
                int distance1 = Math.abs(curCell.x + curCell.width / 2 - cell1.x - cell1.width / 2);
                int distance2 = Math.abs(curCell.x + curCell.width / 2 - cell2.x - cell2.width / 2);
                if (distance1 < distance2) {
                    pos = index1;
                } else {
                    pos = index2;
                }
            } else if (cell1 != null) {
                pos = index1;
            } else if (cell2 != null) {
                pos = index2;
            }
        } catch (Exception e) {

        }
        return pos;
    }

    public CellBean getNextLeft(CellBean entity, List<CellBean> entityList) {
        return getLeftCellBean1(entity, getLeftList(entity, entityList));
    }

    public CellBean getNextRight(CellBean entity, List<CellBean> entityList) {
        return getRightCellBean1(entity, getRightList(entity, entityList));
    }


    public CellBean getNextUp(CellBean entity, List<CellBean> entityList) {
        return getUpCellBean1(entity, getUpList(entity, entityList));
    }


    public CellBean getNextDown(CellBean entity, List<CellBean> entityList) {
        return getDownCellBean1(entity, getDownList(entity, entityList));
    }

    @Override
    public Queue<Integer> getLeftQueue(CellBean entity, List<CellBean> entityList) {
        Queue<Integer> queue = new LinkedList<>();

        List<CellBean> leftCellBeanList = getLeftList(entity, entityList);

        CellBean nextLeft;

        while ((nextLeft = getLeftCellBean1(entity, leftCellBeanList)) != null) {
            queue.add(entityList.indexOf(nextLeft));
            leftCellBeanList.remove(nextLeft);
        }

        while ((nextLeft = getLeftCellBean2(entity, leftCellBeanList)) != null) {
            queue.add(entityList.indexOf(nextLeft));
            leftCellBeanList.remove(nextLeft);
        }

        queue.add(NO_LEFT);

        return queue;
    }

    @Override
    public Queue<Integer> getRightQueue(CellBean entity, List<CellBean> entityList) {
        Queue<Integer> queue = new LinkedList<>();

        List<CellBean> rightCellBeanList = getRightList(entity, entityList);

        CellBean nextRight;

        while ((nextRight = getRightCellBean1(entity, rightCellBeanList)) != null) {
            queue.add(entityList.indexOf(nextRight));
            rightCellBeanList.remove(nextRight);
        }


        while ((nextRight = getRightCellBean2(entity, rightCellBeanList)) != null) {
            queue.add(entityList.indexOf(nextRight));
            rightCellBeanList.remove(nextRight);
        }

        queue.add(NO_RIGHT);

        return queue;
    }

    @Override
    public Queue<Integer> getUpQueue(CellBean entity, List<CellBean> entityList) {
        Queue<Integer> queue = new LinkedList<>();

        List<CellBean> upCellBeanList = getUpList(entity, entityList);

        CellBean nextUp;

        while ((nextUp = getUpCellBean1(entity, upCellBeanList)) != null) {
            queue.add(entityList.indexOf(nextUp));
            upCellBeanList.remove(nextUp);
        }

        while ((nextUp = getUpCellBean2(entity, upCellBeanList)) != null) {
            queue.add(entityList.indexOf(nextUp));
            upCellBeanList.remove(nextUp);
        }

        queue.add(NO_UP);

        return queue;
    }


    @Override
    public Queue<Integer> getDownQueue(CellBean entity, List<CellBean> entityList) {
        Queue<Integer> queue = new LinkedList<>();

        List<CellBean> downCellBeanList = getDownList(entity, entityList);

        CellBean nextDown;

        while ((nextDown = getDownCellBean1(entity, downCellBeanList)) != null) {
            queue.add(entityList.indexOf(nextDown));
            downCellBeanList.remove(nextDown);
        }

        while ((nextDown = getDownCellBean2(entity, downCellBeanList)) != null) {
            queue.add(entityList.indexOf(nextDown));
            downCellBeanList.remove(nextDown);
        }

        queue.add(NO_DOWN);

        return queue;
    }


    private boolean betweenXY(int x1, int x2, int num) {
        int n1 = Math.min(x1, x2);
        int n2 = Math.max(x1, x2);
        return num >= n1 && num <= n2;
    }

    /**
     * 检测{y1,y2}与{num1,num2}是否有交集
     *
     * @param y1
     * @param y2
     * @param num1
     * @param num2
     * @return
     */
    private boolean betweenXY(int y1, int y2, int num1, int num2) {
        return betweenXY(y1, y2, num1) || betweenXY(y1, y2, num2) || betweenXY(num1, num2, y1) || betweenXY(num1, num2, y2);
    }

    private boolean betweenX(CellBean curCell, CellBean cell1) {
        if (betweenXY(curCell.x, curCell.x + curCell.width, cell1.x, cell1.x + cell1.width)) {
            return true;
        }
        return false;
    }

    private boolean betweenY(CellBean curCell, CellBean cell1) {
        if (betweenXY(curCell.y, curCell.y + curCell.height, cell1.y, cell1.y + cell1.height)) {
            return true;
        }
        return false;
    }


    private static int getDistanceBetween(CellBean center, CellBean next, int direction) {
        float dx = 0;
        float dy = 0;

        switch (direction) {
            case LEFT:
                dx = Math.abs(center.x - (next.x + next.width));
                dy = Math.abs(center.y - next.y);
                break;
            case RIGHT:
                dx = Math.abs((center.x + center.width) - next.x);
                dy = Math.abs(center.y - next.y);
                break;
            case UP:
                dx = Math.abs(center.x - next.x);
                dy = Math.abs(center.y - (next.y + next.height));
                break;
            case DOWN:
                dx = Math.abs(center.x - next.x);
                dy = Math.abs((center.y + center.height) - next.y);
                break;
        }
        return (int) Math.sqrt(
                Math.pow(dx, 2) + Math.pow(dy, 2)
        );
    }


    /**
     * 此方法计算筛选entityList中可以往左移动的CellBean控件
     *
     * @param entity
     * @param entityList
     * @return
     */
    private List<CellBean> getLeftList(CellBean entity, List<CellBean> entityList) {
        List<CellBean> leftCellBeanList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            CellBean CellBean = entityList.get(i);
            if (!CellBean.isFocus) continue;
            if (CellBean.x + CellBean.width <= entity.x) {
                leftCellBeanList.add(CellBean);
            }
        }
        return leftCellBeanList;
    }

    /**
     * 从列表中选择一个位于entity左边并在Y方向与entity有交集距离entity最近的CellBean控件
     *
     * @param entity
     * @param leftCellBeanList
     * @return
     */
    private CellBean getLeftCellBean1(CellBean entity, List<CellBean> leftCellBeanList) {
        int nearX = Integer.MAX_VALUE;
        CellBean nextLeft = null;
        for (CellBean en : leftCellBeanList) {
            if (!en.isFocus) continue;
            if (betweenXY(entity.y, entity.y + entity.height, en.y, en.y + en.height)) {
                int distance = Math.abs(en.x + en.width - entity.x);
                if (distance < nearX) {
                    nextLeft = en;
                    nearX = distance;
                } else if (distance == nearX) {
                    if (en.y < nextLeft.y) {
                        nextLeft = en;
                    }
                }
            }
        }
        return nextLeft;
    }

    /**
     * 从列表中选择一个距离entity最近的CellBean控件
     *
     * @param entity
     * @param leftCellBeanList
     * @return
     */
    private CellBean getLeftCellBean2(CellBean entity, List<CellBean> leftCellBeanList) {
        CellBean nextLeft = null;
        int minDistance = Integer.MAX_VALUE;
        for (CellBean en : leftCellBeanList) {
            if (!en.isFocus) continue;
            int distance = getDistanceBetween(entity, en, LEFT);
            if (distance < minDistance) {
                minDistance = distance;
                nextLeft = en;
            }
        }
        return nextLeft;
    }


    /**
     * 此方法计算筛选entityList中可以往右移动的CellBean控件
     *
     * @param entity
     * @param entityList
     * @return
     */
    private List<CellBean> getRightList(CellBean entity, List<CellBean> entityList) {
        List<CellBean> rightCellBeanList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            CellBean CellBean = entityList.get(i);
            if (!CellBean.isFocus) continue;
            if (CellBean.x >= entity.x + entity.width) {
                rightCellBeanList.add(CellBean);
            }
        }
        return rightCellBeanList;
    }

    /**
     * 从列表中选择一个位于entity右边并在Y方向与entity有交集距离entity最近的CellBean控件
     *
     * @param entity
     * @param rightCellBeanList
     * @return
     */
    private CellBean getRightCellBean1(CellBean entity, List<CellBean> rightCellBeanList) {
        int nearX = Integer.MAX_VALUE;
        CellBean nextRight = null;
        for (CellBean en : rightCellBeanList) {
            if (!en.isFocus) continue;
            if (betweenXY(entity.y, entity.y + entity.height, en.y, en.y + en.height)) {
                int distance = Math.abs(en.x - entity.x);
                if (distance < nearX) {
                    nextRight = en;
                    nearX = distance;
                } else if (distance == nearX) {
                    if (en.y < nextRight.y) {
                        nextRight = en;
                    }
                }
            }
        }
        return nextRight;
    }

    /**
     * 从列表中选择一个距离entity最近的CellBean控件
     *
     * @param entity
     * @param rightCellBeanList
     * @return
     */
    private CellBean getRightCellBean2(CellBean entity, List<CellBean> rightCellBeanList) {
        CellBean nextRight = null;
        int minDistance = Integer.MAX_VALUE;
        for (CellBean en : rightCellBeanList) {
            if (!en.isFocus) continue;
            int distance = getDistanceBetween(entity, en, RIGHT);
            if (distance < minDistance) {
                minDistance = distance;
                nextRight = en;
            }
        }
        return nextRight;
    }

    /**
     * 此方法计算筛选entityList中可以往上移动的CellBean控件
     *
     * @param entity
     * @param entityList
     * @return
     */
    private List<CellBean> getUpList(CellBean entity, List<CellBean> entityList) {
        List<CellBean> upCellBeanList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            CellBean CellBean = entityList.get(i);
            if (!CellBean.isFocus) continue;
            if (CellBean.y + CellBean.height <= entity.y) {
                upCellBeanList.add(CellBean);
            }
        }
        return upCellBeanList;
    }


    /**
     * 从列表中选择一个位于entity上边并在X方向与entity有交集距离entity最近的CellBean控件
     *
     * @param entity
     * @param upCellBeanList
     * @return
     */
    private CellBean getUpCellBean1(CellBean entity, List<CellBean> upCellBeanList) {
        CellBean nextUp = null;
        int nearX = Integer.MAX_VALUE;
        for (CellBean en : upCellBeanList) {
            if (!en.isFocus) continue;
            if (betweenXY(entity.x, entity.x + entity.width, en.x, en.x + en.width)) {
                int distance = Math.abs(en.y - entity.y);
                if (distance < nearX) {
                    nextUp = en;
                    nearX = distance;
                } else if (distance == nearX) {
                    if (en.x < nextUp.x) {
                        nextUp = en;
                    }
                }
            }
        }
        return nextUp;
    }


    /**
     * 从列表中选择一个距离entity最近的CellBean控件
     *
     * @param entity
     * @param upCellBeanList
     * @return
     */
    private CellBean getUpCellBean2(CellBean entity, List<CellBean> upCellBeanList) {
        CellBean nextUp = null;
        int minDistance = Integer.MAX_VALUE;
        for (CellBean en : upCellBeanList) {
            if (!en.isFocus) continue;
            int distance = getDistanceBetween(entity, en, UP);
            if (distance < minDistance) {
                minDistance = distance;
                nextUp = en;
            }
        }
        return nextUp;
    }


    /**
     * 此方法计算筛选entityList中可以往下移动的CellBean控件
     *
     * @param entity
     * @param entityList
     * @return
     */
    private List<CellBean> getDownList(CellBean entity, List<CellBean> entityList) {
        List<CellBean> downCellBeanList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            CellBean CellBean = entityList.get(i);
            if (!CellBean.isFocus) continue;
            if (CellBean.y >= entity.y + entity.height) {
                downCellBeanList.add(CellBean);
            }
        }
        return downCellBeanList;
    }

    /**
     * 从列表中选择一个位于entity下边并在Y方向与entity有交集距离entity最近的CellBean控件
     *
     * @param entity
     * @param downCellBeanList
     * @return
     */
    private CellBean getDownCellBean1(CellBean entity, List<CellBean> downCellBeanList) {
        CellBean nextDown = null;
        int nearX = Integer.MAX_VALUE;
        for (CellBean en : downCellBeanList) {
            if (!en.isFocus) continue;
            if (betweenXY(entity.x, entity.x + entity.width, en.x, en.x + en.width)) {
                int distance = Math.abs(en.y - entity.y);
                if (distance < nearX) {
                    nextDown = en;
                    nearX = distance;
                } else if (distance == nearX) {
                    if (en.x < nextDown.x) {
                        nextDown = en;
                    }
                }
            }
        }

        return nextDown;
    }


    /**
     * 从列表中选择一个距离entity最近的CellBean控件
     *
     * @param entity
     * @param downCellBeanList
     * @return
     */
    private CellBean getDownCellBean2(CellBean entity, List<CellBean> downCellBeanList) {
        CellBean nextDown = null;
        int minDistance = Integer.MAX_VALUE;
        for (CellBean en : downCellBeanList) {
            if (!en.isFocus) continue;
            int distance = getDistanceBetween(entity, en, DOWN);
            if (distance < minDistance) {
                minDistance = distance;
                nextDown = en;
            }
        }
        return nextDown;
    }

}
