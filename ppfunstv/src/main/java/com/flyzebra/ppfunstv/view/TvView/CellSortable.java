package com.flyzebra.ppfunstv.view.TvView;


import com.flyzebra.ppfunstv.data.CellEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 修改焦点移动算法，根据交集计算
 * 为传入的CellEntity类,构建好上下左右的位置关系索引
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
    public List<CellEntity> getSortedEntity(List<CellEntity> entityList) {
        for (CellEntity en : entityList) {

            en.setLefts(getLeftQueue(en, entityList));
            en.setRights(getRightQueue(en, entityList));
            en.setUps(getUpQueue(en, entityList));
            en.setDowns(getDownQueue(en, entityList));

            en.setLeft(en.getLefts().poll());
            en.setRight(en.getRights().poll());
            en.setUp(en.getUps().poll());
            en.setDown(en.getDowns().poll());

        }

        return entityList;
    }

    @Override
    public int getLeftUpIndex(List<CellEntity> entityList) {
        for (CellEntity en : entityList) {
            if (en.getLeft() == NO_LEFT && en.getUp() == NO_UP) {
                return entityList.indexOf(en);
            }
        }
        return -1;
    }

    @Override
    public int getRightUpIndex(List<CellEntity> entityList) {
        for (CellEntity en : entityList) {
            if (en.getRight() == NO_RIGHT && en.getUp() == NO_UP) {
                return entityList.indexOf(en);
            }
        }
        return -1;
    }

    @Override
    public int getLeftDownIndex(List<CellEntity> entityList) {
        for (CellEntity en : entityList) {
            if (en.getLeft() == NO_LEFT && en.getDown() == NO_DOWN) {
                return entityList.indexOf(en);
            }
        }
        return -1;
    }

    @Override
    public int getRightDownIndex(List<CellEntity> entityList) {
        for (CellEntity en : entityList) {
            if (en.getRight() == NO_RIGHT && en.getDown() == DOWN) {
                return entityList.indexOf(en);
            }
        }
        return -1;
    }

    @Override
    public int getNext(List<CellEntity> entityList, int cur, int index1, int index2, int direction) {
        int pos = -1;
        try {
            CellEntity cell1 = null;
            CellEntity cell2 = null;
            CellEntity curCell = entityList.get(cur);
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
                int distance1 = Math.abs(curCell.getX() + curCell.getWidth() / 2 - cell1.getX() - cell1.getWidth() / 2);
                int distance2 = Math.abs(curCell.getX() + curCell.getWidth() / 2 - cell2.getX() - cell2.getWidth() / 2);
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

    public CellEntity getNextLeft(CellEntity entity, List<CellEntity> entityList) {
        return getLeftCellEntity1(entity, getLeftList(entity, entityList));
    }

    public CellEntity getNextRight(CellEntity entity, List<CellEntity> entityList) {
        return getRightCellEntity1(entity, getRightList(entity, entityList));
    }


    public CellEntity getNextUp(CellEntity entity, List<CellEntity> entityList) {
        return getUpCellEntity1(entity, getUpList(entity, entityList));
    }


    public CellEntity getNextDown(CellEntity entity, List<CellEntity> entityList) {
        return getDownCellEntity1(entity, getDownList(entity, entityList));
    }

    @Override
    public Queue<Integer> getLeftQueue(CellEntity entity, List<CellEntity> entityList) {
        Queue<Integer> queue = new LinkedList<>();

        List<CellEntity> leftCellEntityList = getLeftList(entity, entityList);

        CellEntity nextLeft ;

        while ((nextLeft = getLeftCellEntity1(entity, leftCellEntityList)) != null) {
            queue.add(entityList.indexOf(nextLeft));
            leftCellEntityList.remove(nextLeft);
        }

        while ((nextLeft = getLeftCellEntity2(entity, leftCellEntityList)) != null) {
            queue.add(entityList.indexOf(nextLeft));
            leftCellEntityList.remove(nextLeft);
        }

        queue.add(NO_LEFT);

        return queue;
    }

    @Override
    public Queue<Integer> getRightQueue(CellEntity entity, List<CellEntity> entityList) {
        Queue<Integer> queue = new LinkedList<>();

        List<CellEntity> rightCellEntityList = getRightList(entity, entityList);

        CellEntity nextRight;

        while ((nextRight = getRightCellEntity1(entity, rightCellEntityList)) != null) {
            queue.add(entityList.indexOf(nextRight));
            rightCellEntityList.remove(nextRight);
        }


        while ((nextRight = getRightCellEntity2(entity, rightCellEntityList)) != null) {
            queue.add(entityList.indexOf(nextRight));
            rightCellEntityList.remove(nextRight);
        }

        queue.add(NO_RIGHT);

        return queue;
    }

    @Override
    public Queue<Integer> getUpQueue(CellEntity entity, List<CellEntity> entityList) {
        Queue<Integer> queue = new LinkedList<>();

        List<CellEntity> upCellEntityList = getUpList(entity, entityList);

        CellEntity nextUp;

        while ((nextUp = getUpCellEntity1(entity, upCellEntityList)) != null) {
            queue.add(entityList.indexOf(nextUp));
            upCellEntityList.remove(nextUp);
        }

        while ((nextUp = getUpCellEntity2(entity, upCellEntityList)) != null) {
            queue.add(entityList.indexOf(nextUp));
            upCellEntityList.remove(nextUp);
        }

        queue.add(NO_UP);

        return queue;
    }


    @Override
    public Queue<Integer> getDownQueue(CellEntity entity, List<CellEntity> entityList) {
        Queue<Integer> queue = new LinkedList<>();

        List<CellEntity> downCellEntityList = getDownList(entity, entityList);

        CellEntity nextDown;

        while ((nextDown = getDownCellEntity1(entity, downCellEntityList)) != null) {
            queue.add(entityList.indexOf(nextDown));
            downCellEntityList.remove(nextDown);
        }

        while ((nextDown = getDownCellEntity2(entity, downCellEntityList)) != null) {
            queue.add(entityList.indexOf(nextDown));
            downCellEntityList.remove(nextDown);
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

    private boolean betweenX(CellEntity curCell, CellEntity cell1) {
        if (betweenXY(curCell.getX(), curCell.getX() + curCell.getWidth(), cell1.getX(), cell1.getX() + cell1.getWidth())) {
            return true;
        }
        return false;
    }

    private boolean betweenY(CellEntity curCell, CellEntity cell1) {
        if (betweenXY(curCell.getY(), curCell.getY() + curCell.getHeight(), cell1.getY(), cell1.getY() + cell1.getHeight())) {
            return true;
        }
        return false;
    }


    private static int getDistanceBetween(CellEntity center, CellEntity next, int direction) {
        float dx = 0;
        float dy = 0;

        switch (direction) {
            case LEFT:
                dx = Math.abs(center.getX() - (next.getX() + next.getWidth()));
                dy = Math.abs(center.getY() - next.getY());
                break;
            case RIGHT:
                dx = Math.abs((center.getX() + center.getWidth()) - next.getX());
                dy = Math.abs(center.getY() - next.getY());
                break;
            case UP:
                dx = Math.abs(center.getX() - next.getX());
                dy = Math.abs(center.getY() - (next.getY() + next.getHeight()));
                break;
            case DOWN:
                dx = Math.abs(center.getX() - next.getX());
                dy = Math.abs((center.getY() + center.getHeight()) - next.getY());
                break;
        }
        return (int) Math.sqrt(
                Math.pow(dx, 2) + Math.pow(dy, 2)
        );
    }


    /**
     * 此方法计算筛选entityList中可以往左移动的CellEntity控件
     *
     * @param entity
     * @param entityList
     * @return
     */
    private List<CellEntity> getLeftList(CellEntity entity, List<CellEntity> entityList) {
        List<CellEntity> leftCellEntityList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            CellEntity cellEntity = entityList.get(i);
            if(!cellEntity.getCanFocus()) continue;
            if (cellEntity.getX() + cellEntity.getWidth() <= entity.getX()) {
                leftCellEntityList.add(cellEntity);
            }
        }
        return leftCellEntityList;
    }

    /**
     * 从列表中选择一个位于entity左边并在Y方向与entity有交集距离entity最近的CellEntity控件
     *
     * @param entity
     * @param leftCellEntityList
     * @return
     */
    private CellEntity getLeftCellEntity1(CellEntity entity, List<CellEntity> leftCellEntityList) {
        int nearX = Integer.MAX_VALUE;
        CellEntity nextLeft = null;
        for (CellEntity en : leftCellEntityList) {
            if(!en.getCanFocus()) continue;
            if (betweenXY(entity.getY(), entity.getY() + entity.getHeight(), en.getY(), en.getY() + en.getHeight())) {
                int distance = Math.abs(en.getX() + en.getWidth() - entity.getX());
                if (distance < nearX) {
                    nextLeft = en;
                    nearX = distance;
                } else if (distance == nearX) {
                    if (en.getY() < nextLeft.getY()) {
                        nextLeft = en;
                    }
                }
            }
        }
        return nextLeft;
    }

    /**
     * 从列表中选择一个距离entity最近的CellEntity控件
     *
     * @param entity
     * @param leftCellEntityList
     * @return
     */
    private CellEntity getLeftCellEntity2(CellEntity entity, List<CellEntity> leftCellEntityList) {
        CellEntity nextLeft = null;
        int minDistance = Integer.MAX_VALUE;
        for (CellEntity en : leftCellEntityList) {
            if(!en.getCanFocus()) continue;
            int distance = getDistanceBetween(entity, en, LEFT);
            if (distance < minDistance) {
                minDistance = distance;
                nextLeft = en;
            }
        }
        return nextLeft;
    }


    /**
     * 此方法计算筛选entityList中可以往右移动的CellEntity控件
     *
     * @param entity
     * @param entityList
     * @return
     */
    private List<CellEntity> getRightList(CellEntity entity, List<CellEntity> entityList) {
        List<CellEntity> rightCellEntityList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            CellEntity cellEntity = entityList.get(i);
            if(!cellEntity.getCanFocus()) continue;
            if (cellEntity.getX() >= entity.getX() + entity.getWidth()) {
                rightCellEntityList.add(cellEntity);
            }
        }
        return rightCellEntityList;
    }

    /**
     * 从列表中选择一个位于entity右边并在Y方向与entity有交集距离entity最近的CellEntity控件
     *
     * @param entity
     * @param rightCellEntityList
     * @return
     */
    private CellEntity getRightCellEntity1(CellEntity entity, List<CellEntity> rightCellEntityList) {
        int nearX = Integer.MAX_VALUE;
        CellEntity nextRight = null;
        for (CellEntity en : rightCellEntityList) {
            if(!en.getCanFocus()) continue;
            if (betweenXY(entity.getY(), entity.getY() + entity.getHeight(), en.getY(), en.getY() + en.getHeight())) {
                int distance = Math.abs(en.getX() - entity.getX());
                if (distance < nearX) {
                    nextRight = en;
                    nearX = distance;
                } else if (distance == nearX) {
                    if (en.getY() < nextRight.getY()) {
                        nextRight = en;
                    }
                }
            }
        }
        return nextRight;
    }

    /**
     * 从列表中选择一个距离entity最近的CellEntity控件
     *
     * @param entity
     * @param rightCellEntityList
     * @return
     */
    private CellEntity getRightCellEntity2(CellEntity entity, List<CellEntity> rightCellEntityList) {
        CellEntity nextRight = null;
        int minDistance = Integer.MAX_VALUE;
        for (CellEntity en : rightCellEntityList) {
            if(!en.getCanFocus()) continue;
            int distance = getDistanceBetween(entity, en, RIGHT);
            if (distance < minDistance) {
                minDistance = distance;
                nextRight = en;
            }
        }
        return nextRight;
    }

    /**
     * 此方法计算筛选entityList中可以往上移动的CellEntity控件
     *
     * @param entity
     * @param entityList
     * @return
     */
    private List<CellEntity> getUpList(CellEntity entity, List<CellEntity> entityList) {
        List<CellEntity> upCellEntityList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            CellEntity cellEntity = entityList.get(i);
            if(!cellEntity.getCanFocus()) continue;
            if (cellEntity.getY() + cellEntity.getHeight() <= entity.getY()) {
                upCellEntityList.add(cellEntity);
            }
        }
        return upCellEntityList;
    }


    /**
     * 从列表中选择一个位于entity上边并在X方向与entity有交集距离entity最近的CellEntity控件
     *
     * @param entity
     * @param upCellEntityList
     * @return
     */
    private CellEntity getUpCellEntity1(CellEntity entity, List<CellEntity> upCellEntityList) {
        CellEntity nextUp = null;
        int nearX = Integer.MAX_VALUE;
        for (CellEntity en : upCellEntityList) {
            if(!en.getCanFocus()) continue;
            if (betweenXY(entity.getX(), entity.getX() + entity.getWidth(), en.getX(), en.getX() + en.getWidth())) {
                int distance = Math.abs(en.getY() - entity.getY());
                if (distance < nearX) {
                    nextUp = en;
                    nearX = distance;
                } else if (distance == nearX) {
                    if (en.getX() < nextUp.getX()) {
                        nextUp = en;
                    }
                }
            }
        }
        return nextUp;
    }


    /**
     * 从列表中选择一个距离entity最近的CellEntity控件
     *
     * @param entity
     * @param upCellEntityList
     * @return
     */
    private CellEntity getUpCellEntity2(CellEntity entity, List<CellEntity> upCellEntityList) {
        CellEntity nextUp = null;
        int minDistance = Integer.MAX_VALUE;
        for (CellEntity en : upCellEntityList) {
            if(!en.getCanFocus()) continue;
            int distance = getDistanceBetween(entity, en, UP);
            if (distance < minDistance) {
                minDistance = distance;
                nextUp = en;
            }
        }
        return nextUp;
    }


    /**
     * 此方法计算筛选entityList中可以往下移动的CellEntity控件
     *
     * @param entity
     * @param entityList
     * @return
     */
    private List<CellEntity> getDownList(CellEntity entity, List<CellEntity> entityList) {
        List<CellEntity> downCellEntityList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            CellEntity cellEntity = entityList.get(i);
            if(!cellEntity.getCanFocus()) continue;
            if (cellEntity.getY() >= entity.getY() + entity.getHeight()) {
                downCellEntityList.add(cellEntity);
            }
        }
        return downCellEntityList;
    }

    /**
     * 从列表中选择一个位于entity下边并在Y方向与entity有交集距离entity最近的CellEntity控件
     *
     * @param entity
     * @param downCellEntityList
     * @return
     */
    private CellEntity getDownCellEntity1(CellEntity entity, List<CellEntity> downCellEntityList) {
        CellEntity nextDown = null;
        int nearX = Integer.MAX_VALUE;
        for (CellEntity en : downCellEntityList) {
            if(!en.getCanFocus()) continue;
            if (betweenXY(entity.getX(), entity.getX() + entity.getWidth(), en.getX(), en.getX() + en.getWidth())) {
                int distance = Math.abs(en.getY() - entity.getY());
                if (distance < nearX) {
                    nextDown = en;
                    nearX = distance;
                } else if (distance == nearX) {
                    if (en.getX() < nextDown.getX()) {
                        nextDown = en;
                    }
                }
            }
        }

        return nextDown;
    }


    /**
     * 从列表中选择一个距离entity最近的CellEntity控件
     *
     * @param entity
     * @param downCellEntityList
     * @return
     */
    private CellEntity getDownCellEntity2(CellEntity entity, List<CellEntity> downCellEntityList) {
        CellEntity nextDown = null;
        int minDistance = Integer.MAX_VALUE;
        for (CellEntity en : downCellEntityList) {
            if(!en.getCanFocus()) continue;
            int distance = getDistanceBetween(entity, en, DOWN);
            if (distance < minDistance) {
                minDistance = distance;
                nextDown = en;
            }
        }
        return nextDown;
    }

}
