package com.qxg.ihandsw;

/**
 * Created by Qking on 2016/10/31.
 */

public class Config {
    public static final String baseUrl = "http://www.baiduc.com";
    public static final String SERVER_ADDRESS = "http://www.qxgzone.com";
    public static final String CARD_ADDRESS = "http://iecard.wh.sdu.edu.cn/";
    public static final String CARD_INFO_ADDRESS = "http://iecard.wh.sdu.edu.cn/CardManage/CardInfo/BasicInfo";
    public static final String CARD_FEE_ADDRESS = "http://iecard.wh.sdu.edu.cn/CardManage/CardInfo/TransferAccount";// 转账地址
    public static final String CARD_PWD_TABLE_ADDRESS = "http://iecard.wh.sdu.edu.cn//Account/GetNumKeyPadImg";  //密码表
    public static final String NET_FEE_DOPAY_ADRESS = "http://iecard.wh.sdu.edu.cn/AutoPay/NetFee/DoPay";
    public static final String NET_FEE_DOPAY_CONFIRM = "https://pay.wh.sdu.edu.cn:8443/synpay/web/doPay";
    public static final String FORWARD_PAY_TOOL = "https://pay.wh.sdu.edu.cn:8443/synpay/web/forwardPayTool";
    public static final String E_CARD_PAY = "https://pay.wh.sdu.edu.cn:8443/synpay/web/eCardPay";
    public static final String GET_NET_FEE = "http://iecard.wh.sdu.edu.cn/AutoPay/NetFee/Index"; //用于获取网费信息
    public static final String GET_ELECTRIC_CNT = "http://iecard.wh.sdu.edu.cn/AutoPay/PowerFee/GetPowerBalance"; //用于获取度数
    public static final String POWER_FEE_DOPAY = "http://iecard.wh.sdu.edu.cn/AutoPay/PowerFee/DoPay";
    public static final String GET_LOSS_CARD_INFO = "http://iecard.wh.sdu.edu.cn/CardManage/CardLoss/ManageIndex";
    public static final String UP_LOSS = "http://iecard.wh.sdu.edu.cn/CardManage/CardLoss/Lose";
    public static final String GET_BACK_CARD = "http://iecard.wh.sdu.edu.cn/CardManage/CardLoss/PickUpCard";
    public static final String GUASHI_CARD = "http://iecard.wh.sdu.edu.cn/CardManage/CardInfo/SetCardLost";  //只需要两个参数checkCode,和password 返回 该校园卡已挂失，校园卡挂失成功 以及，验证码不正确
    public static final String JIEGUA_CARD = "http://iecard.wh.sdu.edu.cn/CardManage/CardInfo/SetCardUnLossForNotSignIn";//解挂 参数是checkCode,cardno="618872+",以及password 返回数据"校园卡解挂成功" ，"持卡人已解挂","验证码不正确",
    public static final String CHANGE_CARD_PWD = "http://iecard.wh.sdu.edu.cn/CardManage/CardInfo/ChangeCardPwd"; //OldPassword  NewPassword ConfirmPassword  ChgCheckCode=6798  返回json,ret msg
    public static final String LOAD_ORDER_ADDRESS = "http://202.194.46.22/FunctionPages/SeatBespeak/BespeakSeat.aspx";
    public static final String LOAD_FLOOR_INFO_ADDRESS = "http://202.194.46.22/FunctionPages/SeatBespeak/BespeakSeat.aspx";
    public static final String ORDER_ITEM_BASE_URL = "http://202.194.46.22/FunctionPages/SeatBespeak/BespeakSeatLayout.aspx";
    public static final String LIBRARY_CHANGE_PWD = "http://202.194.46.22/FunctionPages/UsersManage/ChangePassword.aspx";

    public static final String LIBRARY_SEARCH_BOOK_URL = "http://mslib.wh.sdu.edu.cn:8080/sms/opac/search/showSearch.action?xc=5"; //馆藏书查询

    public static final String BOOK_LOAD_CHECK_URL = "http://202.194.40.71:8080/reader/captcha.php";  //加载验证码
    public static final String BOOK_LOGIN_URL = "http://202.194.40.71:8080/reader/redr_verify.php"; //图书登录url

    public static final String JUNEBERRY_LOGIN_URL = "http://yuyue.juneberry.cn/";  //首先登录一下，然后取得数据
    public static final String JUN_TRUE_LOGIN_URL = "http://yuyue.juneberry.cn/Login.aspx"; //

    public static final String JUN_SEAT_INFO_URL = "http://yuyue.juneberry.cn/ReadingRoomInfos/ReadingRoomState.aspx";//获取登录信息


    public static final String OFFICE_NOTIFY_URL = "http://jwc.wh.sdu.edu.cn/articleList_nologin.do?article.columnlsh=60";

    public static final String TERM_DATE="https://www.wh.sdu.edu.cn/html/xiaoli/";  //学期校历
    public static final String TERM_DATE_IMG_URL = "http://www.wh.sdu.edu.cn/images/xiaoli/";

    public static final String RELEASE_SEAT_URL = "http://yuyue.juneberry.cn/MainFunctionPage.aspx";

    public static final String WORK_ARRANGE_URL = "http://xsgzc.wh.sdu.edu.cn/list.action?class_id=3&source=page&currentPage=1"; //近期工作安排界面


    public static final String SCHOOL_HIRE_URL ="http://job.wh.sdu.edu.cn/item/79.html"; //校内招聘
    public static final String ONLINE_HIRE_URL = "http://job.wh.sdu.edu.cn/item/80.html";



    public final static String CARD_LOGIN_NAME = "LQJLAOFJ234L12LJ4"; //文件名
    //保存的名字
    public final static String NAME = "name";
    public final static String USER_ID = "userid";
    public final static String SET_COOKIE = "setCookie";
    public final static String IS_LOGIN = "isLogin";
    public final static String COOKIE = "cookie";
    public final static String CHECK_NUM = "check";
    public final static String CARD_PWD = "queryPwd";
    public final static String IS_REM = "isRem";

    //保存校园卡信息
    public final static  String CARD_INFO_FILE_NAME = "KLFKELK2J30JIO";  //保存文件名字
    public final static String CARD_INFO_NAME = "name";   //姓名
    public final static String CARD_INFO_SID = "sid";         //学工号
    public final static String CARD_INFO_ID = "id";             //卡帐号
    public final static String CARD_INFO_BALANCE = "balance"; //余额
    public final static String CARD_INFO_TRANSLATION_BALANCE = "translation_balance"; //过度余额，应该用不到
    public final static String CARD_INFO_STATE = "state"; //卡状态；
    public final static String CARD_INFO_FREEZE_STATE = "freeze_state"; //卡冻结状态


    //图书馆的一些信息：
    public final static String GET_SEAT_INFO_ADDRESS = "http://202.194.46.22/FunctionPages/Statistical/LibraryUsedStat.aspx";//该网址返回IMG的地址，然后对该IMG进行访问
    public final static String LIBRARY_LOGIN_DEFAULT_ADDRESS = "http://202.194.46.22/default.aspx";
    public final static String LIBRARY_ADDRESS = "http://202.194.46.22";
    public final static String ORDER_RECORD_ADDRESS = "http://202.194.46.22/FunctionPages/ReaderLog/SelectBespeakLog.aspx"; //预约记录查询，使用get方法
    public final static String CANCEL_ORDER_ADDRESS = "http://202.194.46.22/FunctionPages/ReaderLog/SelectBespeakLog.aspx"; //取消预约
    public final static String BOOK_NOW_BORROW_URL = "http://202.194.40.71:8080/reader/book_lst.php"; //当前借阅url
    //http://202.194.40.71:8080/reader/ajax_renew.php?bar_code=TS0948322&check=74A7FC3F&captcha=8573&time=1478657272742
    public final static String BOOK_CONTINUE_BORROW_URL = "http://202.194.40.71:8080/reader/ajax_renew.php";//续借URL，返回数据：<font color=green>续借成功</font>
    public final static String BOOK_CHANGE_PWD_URL = "http://202.194.40.71:8080/reader/change_passwd_result.php"; //post数据 old_passwd=820119&new_passwd=201400820119&chk_passwd=201400820119&submit1=确定
    public final static String LIBRARY_INFO_FILE_NAME = "ojallkajwef"; //保存文件名字
    public final static String LIBRARY_PWD = "pwd"; //图书馆密码
    public final static String LIBRARY_LOGIN_COOKIE = "cookie";

    //图书馆图书 的一些信息：

    public final static String BOOK_INFO_FILE_NAME = "adf32rfa";
    public final static String BOOK_PWD = "pwd"; //图书密码
    public final static String BOOK_LOGIN_COOKIE = "cookie";
    public final static String BOOK_LOGIN_CHECK = "check";

}
