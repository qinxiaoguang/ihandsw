package com.qxg.ihandsw.widget.LibraryWidget;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.adapter.SeatsInfoAdapter;
import com.qxg.ihandsw.model.bean.SeatInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/11.
 */

public class ShowSeatInfoDialog extends Dialog {

    List<SeatInfo> seatInfos;
    Context context;
    @BindView(R.id.seatInfosListView)
    RecyclerView seatInfoListView;

    public ShowSeatInfoDialog(Context context, List<SeatInfo> seatInfos) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
        this.seatInfos = seatInfos;
    }

    public ShowSeatInfoDialog initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_show_seat_info, null);
        ButterKnife.bind(this, view);

        seatInfoListView.setAdapter(new SeatsInfoAdapter(context,seatInfos));
        seatInfoListView.setLayoutManager(new LinearLayoutManager(context));
        setContentView(view);
        return this;
    }
}
