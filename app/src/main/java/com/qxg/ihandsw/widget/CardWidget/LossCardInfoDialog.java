package com.qxg.ihandsw.widget.CardWidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.adapter.LossCardAdapter;
import com.qxg.ihandsw.model.CardModel;
import com.qxg.ihandsw.model.bean.LossCard;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Qking on 2016/11/6.
 */
@SuppressLint("ValidFragment")
public class LossCardInfoDialog extends BaseBottomDialog {

    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.loss_card_recyclerView)
    RecyclerView lossCardRecyclerView;



    public LossCardInfoDialog(Context context) {
        super(context);
    }

    public void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loss_card, null);
        ButterKnife.bind(this, view);

        cardModel.setOnGetLossCardInfo(new CardModel.OnGetLossCardInfo() {
            @Override
            public void onSucess(final List<LossCard> lossCards) {
                //取得成功后
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.GONE);
                        lossCardRecyclerView.setAdapter(new LossCardAdapter(lossCards,context,LossCardInfoDialog.this));
                        lossCardRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        lossCardRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        cardModel.getLossCardInfo();
        setContentView(view);
    }
}
