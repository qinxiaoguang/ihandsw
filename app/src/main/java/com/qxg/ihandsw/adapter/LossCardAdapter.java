package com.qxg.ihandsw.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.CardModel;
import com.qxg.ihandsw.model.bean.LossCard;
import com.qxg.ihandsw.widget.CardWidget.LossCardInfoDialog;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.List;

/**
 * Created by Qking on 2016/11/6.
 */

public class LossCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<LossCard> lossCards;
    Context context;
    private String sid;
    private Handler handler;
    private CatLoadingView catLoadingView;
    LossCardInfoDialog dialog;
    public LossCardAdapter(List<LossCard> lossCards, Context context, LossCardInfoDialog lossCardInfoDialog){
        this.lossCards = lossCards;
        this.context = context;
        //获取个人卡帐号
        sid = context.getSharedPreferences(Config.CARD_INFO_FILE_NAME,Context.MODE_PRIVATE).getString(Config.CARD_INFO_SID,"");
        handler = new Handler(context.getMainLooper());
        catLoadingView = new CatLoadingView();
        catLoadingView.setCancelable(false);
        dialog = lossCardInfoDialog;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_loss_card,parent,false);
        return new MyHoler(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final LossCard l = lossCards.get(position);
        ((MyHoler)holder).name.setText(l.getName());
        ((MyHoler)holder).cardId.setText("卡号:" + l.getId());
        ((MyHoler)holder).sid.setText(l.getSid());
        ((MyHoler)holder).lossAdress.setText("丢失地点:" + l.getLossAdress());
        ((MyHoler)holder).phone.setText(l.getPhone());
        ((MyHoler)holder).des.setText("说明:" + l.getDes());
        ((MyHoler)holder).date.setText("发布时间: " + l.getDate());
        if(l.getSid().contains(sid)){
            ((MyHoler)holder).getBack.setVisibility(View.VISIBLE);
            ((MyHoler)holder).getBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setMessage("确认招领?")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //确认招领后，就让CardModel进行招领吧
                                    catLoadingView.show(((AppCompatActivity)context).getSupportFragmentManager(),"'");
                                    CardModel cardModel = new CardModel((Activity) context);
                                    cardModel.setOnGetBackCardListener(new CardModel.OnGetBackCardListener() {
                                        @Override
                                        public void onSucess(final String msg) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    catLoadingView.dismiss();
                                                    if(msg.contains("False")){
                                                        //失败
                                                        Toast.makeText(context,"招领失败，请稍后再试",Toast.LENGTH_SHORT).show();
                                                    }else if(msg.contains("True")){
                                                        Toast.makeText(context,"招领成功~",Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    cardModel.getBackCard(l.getId(),l.getName(),l.getSid(),l.getLossAdress(),l.getPhone(),l.getDes(),l.getCnt());
                                }
                            })
                            .setNegativeButton("否",null)
                            .show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return lossCards.size();
    }

    static class MyHoler extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView cardId;
        public TextView sid;
        public TextView lossAdress;
        public TextView phone;
        public TextView des;
        public TextView date;
        public TextView getBack;

        public MyHoler(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            cardId = (TextView) itemView.findViewById(R.id.card_id);
            sid = (TextView) itemView.findViewById(R.id.sid);
            lossAdress = (TextView) itemView.findViewById(R.id.loss_adress);
            phone = (TextView) itemView.findViewById(R.id.phone);
            des = (TextView) itemView.findViewById(R.id.des);
            date = (TextView) itemView.findViewById(R.id.date);
            getBack = (TextView) itemView.findViewById(R.id.getBack);
        }
    }
}
