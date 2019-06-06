package com.flyzebra.flyui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyzebra.flyui.bean.CellBean;
import com.flyzebra.flyui.view.cellview.CellViewFactory;
import com.flyzebra.flyui.view.cellview.ICell;

/**
 * Author FlyZebra
 * 2019/4/16 15:07
 * Describ:
 **/
public class CellFragment extends Fragment {
    public static String CELL = "cellbean";
    private CellBean mCellBean;
    public CellFragment(){
    }

    public static CellFragment newInstance(CellBean cellBean) {
        CellFragment cellFragment = new CellFragment();
        Bundle args = new Bundle();
        args.putParcelable(CELL,cellBean);
        cellFragment.setArguments(args);
        return cellFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        mCellBean = (CellBean) args.get(CELL);
        if(mCellBean!=null){
            ICell view =  CellViewFactory.createView(getActivity(),mCellBean);
            view.setCellBean(mCellBean);
            return (View) view;
        }else{
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
