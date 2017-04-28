package com.qxg.ihandsw.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.BookModel;
import com.qxg.ihandsw.model.bean.NowBorrow;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.List;

import static android.view.View.GONE;

/**
 * Created by Qking on 2016/11/9.
 */

public class NowBorrowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    List<NowBorrow> borrows;
    BookModel bookModel;
    Handler handler;
    CatLoadingView loadingView;
    public NowBorrowAdapter(Activity activity, List<NowBorrow> borrows){
        this.activity = activity;
        this.borrows = borrows;
        bookModel = new BookModel(activity);
        handler = new Handler(activity.getMainLooper());
        loadingView = new CatLoadingView();
        loadingView.setCancelable(false);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_now_borrow,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final NowBorrow borrow = borrows.get(position);
        ((MyHolder)holder).id.setText("条码号:  " + borrow.id);
        ((MyHolder)holder).name.setText(borrow.name);
        ((MyHolder)holder).borrowDate.setText("借阅日期: " + borrow.borrowData);
        ((MyHolder)holder).returnDate.setText("应还日期: " + borrow.returnData);
        ((MyHolder)holder).address.setText(borrow.address);
        ((MyHolder)holder).continueBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击续借后，就开始续借吧
                new AlertDialog.Builder(activity)
                        .setMessage("确定要续借?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bookModel.setOnContinueBorrowListener(new BookModel.OnContinueBorrowListener() {
                                    @Override
                                    public void onSucess(boolean flag, final String msg) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadingView.dismiss();
                                                new AlertDialog.Builder(activity)
                                                        .setMessage(msg)
                                                        .setNegativeButton("知道了",null)
                                                        .show();
                                            }
                                        });

                                    }
                                });
                                loadingView.show(((AppCompatActivity)activity).getSupportFragmentManager(),"");
                                bookModel.continueBorrow(borrow.id,borrow.check);
                            }
                        })
                        .setNegativeButton("否",null)
                        .show();

            }
        });
        ((MyHolder)holder).layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MyHolder)holder).infoLayout.getVisibility() == View.VISIBLE){
                    ((MyHolder)holder).infoLayout.setVisibility(GONE);
                }else{
                    ((MyHolder)holder).infoLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return borrows.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView id;
        public TextView borrowDate;
        public TextView returnDate;
        public TextView address;
        public View continueBorrow;
        public View layout;
        public View infoLayout;

        public MyHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.name);
            borrowDate = (TextView) itemView.findViewById(R.id.borrow_date);
            returnDate = (TextView) itemView.findViewById(R.id.return_date);
            address = (TextView) itemView.findViewById(R.id.address);
            continueBorrow =  itemView.findViewById(R.id.continue_borrow);
            layout = itemView.findViewById(R.id.layout);
            infoLayout = itemView.findViewById(R.id.info_layout);
        }
    }
}
