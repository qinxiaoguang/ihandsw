package com.qxg.ihandsw.widget.CardWidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qxg.ihandsw.Config;
import com.qxg.ihandsw.R;
import com.qxg.ihandsw.model.BookModel;
import com.qxg.ihandsw.model.CardModel;
import com.qxg.ihandsw.model.LibraryModel;
import com.qxg.ihandsw.utils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

/**
 * Created by Qking on 2016/11/7.
 */
@SuppressLint("ValidFragment")
public class ChangePwdDialog extends BaseBottomDialog {

    @BindView(R.id.old_pwd)
    EditText oldPwd;
    @BindView(R.id.new_pwd)
    EditText newPwd;
    @BindView(R.id.confirm_pwd)
    EditText confirmPwd;
    @BindView(R.id.err_msg)
    TextView errMsg;
    @BindView(R.id.confirm)
    Button confirm;

    public int flag;
    public static final int CARD_CHANGE_PWD = 1;
    public static final int LIBRARY_CHANGE_PWD =2;
    public static final int BOOK_CHANGE_PWD = 3;

    public ChangePwdDialog(Context context,int flag){
        super(context);
        this.flag = flag;
    }

    @Override
    public void initView() {
        super.initView();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_change_pwd, null);
        ButterKnife.bind(this, view);
        if(flag!=CARD_CHANGE_PWD){
            oldPwd.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
            newPwd.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
            confirmPwd.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
        }
        setContentView(view);
    }

    @OnClick(R.id.confirm)
    public void onClick() {
        errMsg.setVisibility(View.GONE);
        String sOldPwd = oldPwd.getText().toString();
        String sNewPwd = newPwd.getText().toString();
        String sConfirmPwd = confirmPwd.getText().toString();

        if(sOldPwd.isEmpty() || sNewPwd.isEmpty() || sConfirmPwd.isEmpty()){
            showErrMsg("数据不得为空！");
            return;
        }

        if(!sNewPwd.equals(sConfirmPwd)){
            showErrMsg("确认密码与新密码不一致！");
            return;
        }

        switch (flag){
            case CARD_CHANGE_PWD:
                changeCardPwd(sOldPwd,sNewPwd);
                break;
            case LIBRARY_CHANGE_PWD:
                changeLibraryPwd(sOldPwd,sNewPwd);
                break;
            case BOOK_CHANGE_PWD:
                changeBookPwd(sOldPwd,sNewPwd);
            default:break;
        }
    }

    private void changeBookPwd(String sOldPwd,String sNewPwd){
        String bookPwd = context.getSharedPreferences(Config.BOOK_INFO_FILE_NAME,Context.MODE_PRIVATE)
                .getString(Config.BOOK_PWD,"");
        Log.i("两个密码分别是:",bookPwd + "," + sOldPwd);

        if(!bookPwd.equals(sOldPwd)){
            showErrMsg("原密码错误！");
            return;
        }
        //开始更改
        showLoading();
        bookModel.setOnChangePwdListener(new BookModel.OnChangePwdListener() {
            @Override
            public void onSucess(final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            }
        });
        bookModel.changePwd(sOldPwd,sNewPwd);
    }

    private void changeCardPwd(String sOldPwd,String sNewPwd){
        String queryPwd = context.getSharedPreferences(Config.CARD_LOGIN_NAME,Context.MODE_PRIVATE)
                .getString(Config.CARD_PWD,"");
        if(sOldPwd.length()!=6 || sNewPwd.length()!=6){
            showErrMsg("密码长度必须为6位！");
            return;
        }
        Log.i("两个密码分别是:",queryPwd + "," + sOldPwd);
        if(!queryPwd.equals(sOldPwd)){
            showErrMsg("查询密码错误！");
            return;
        }

        //开始更改
        showLoading();
        cardModel.setOnChangePwdListener(new CardModel.OnChangePwdListener() {
            @Override
            public void onSucess(boolean ret, final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            }
        });

        cardModel.changePwd(sOldPwd,sNewPwd);
    }

    private void changeLibraryPwd(String sOldPwd,String sNewPwd){

        String libraryPwd = context.getSharedPreferences(Config.LIBRARY_INFO_FILE_NAME,Context.MODE_PRIVATE)
                .getString(Config.LIBRARY_PWD,"");
        Log.i("两个密码分别是:",libraryPwd + "," + sOldPwd);

        if(!libraryPwd.equals(sOldPwd)){
            showErrMsg("原密码错误！");
            return;
        }
        //开始更改
        showLoading();
        libraryModel.setOnChangePwdListener(new LibraryModel.OnChangePwdListener() {
            @Override
            public void onSucess(boolean ret, final String msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            }
        });
        libraryModel.changePwd(sOldPwd,sNewPwd);
    }

    private void showErrMsg(String msg){
        errMsg.setText(msg);
        errMsg.setVisibility(View.VISIBLE);
    }
}
