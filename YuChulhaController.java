package kr.gimaek.loader.controllers;

import android.os.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

import kr.gimaek.loader.CommonDefine;
import kr.gimaek.loader.MainActivity;
import kr.gimaek.loader.WebController;
import kr.gimaek.loader.models.Yu_Chulha;

public class YuChulhaController {
    private final String TAG = "YuChulhaController";

    public void rcvData(String ymd){
        String sql = "select convert(varchar(10), C.Ymd, 120) Ymd, C.Nno, C.Chulha_Plan_No, "
                + "C.Suryang, C.Sigan, C.Car_Code, C.Car_No, "
                + "P.Cust_Code, P.Hyeonjang_Code, "
                + "S.Sangho, "
                + "H.Hyeonjang_Name, "
                + "J.Jepum_Name "
                + "from Yu_Chulha C left outer join Yu_Chulha_Plan P "
                +                              "on P.Ymd = C.Ymd "
                +                             "and P.Nno = C.Chulha_Plan_No "
                +                  "left outer join Cm_Cust S "
                +                               "on S.Cust_Code = P.Cust_Code "
                +                   "left outer join Cm_Hyeonjang H "
                +                               "on H.Cust_Code = P.Cust_Code "
                +                              "and H.Hyeonjang_Code = P.Hyeonjang_Code "
                +                   "left outer join Cm_Jepum J "
                +                               "on J.Jepum_Code = P.Jepum_Code "
                //+ "where ymd = '" + ymd + "' ";
                + "where C.ymd = '2011-01-03' "
                + "and isnull(Loaded,0) = 0 ";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                WebController webController = new WebController();
                WebController.Result result = webController.get(sql);
                Message message = MainActivity.mainActivityHandler.obtainMessage();
                if(result.isSuccessed()){
                    ArrayList<Yu_Chulha> dsYu_Chulha = new Gson().fromJson(
                            result.getData().toString(),
                            new TypeToken<List<Yu_Chulha>>(){}.getType()
                    );
                    message.what = CommonDefine.RCV_YU_CHULHA_SUCC;
                    message.obj = dsYu_Chulha;
                }
                else {
                    message.what = CommonDefine.RCV_YU_CHULHA_FAIL;
                }
                MainActivity.mainActivityHandler.sendMessage(message);
            }
        });
        thread.start();
    }

    public void load(String ymd, int nno){
        String sql = "update Yu_Chulha set Loaded = 1 "
                + "where Ymd = '" + ymd + "' "
                + "and Nno = " + Integer.toString(nno) + " ";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                WebController webController = new WebController();
                WebController.Result result = webController.excute(sql);
                Message message = MainActivity.mainActivityHandler.obtainMessage();
                if(result.isSuccessed())
                    message.what = CommonDefine.LOAD_SUCC;
                else
                    message.what = CommonDefine.LOAD_FAIL;
                MainActivity.mainActivityHandler.sendMessage(message);
            }
        });
        thread.start();
    }

    public void cancelLoad(String ymd, int nno){
        String sql = "update Yu_Chulha set Loaded = 0 "
                + "where Ymd = '" + ymd + "' "
                + "and Nno = " + Integer.toString(nno) + " ";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                WebController webController = new WebController();
                WebController.Result result = webController.excute(sql);
                Message message = MainActivity.mainActivityHandler.obtainMessage();
                if(result.isSuccessed())
                    message.what = CommonDefine.CANCEL_LOAD_SUCC;
                else
                    message.what = CommonDefine.CANCEL_LOAD_FAIL;
                MainActivity.mainActivityHandler.sendMessage(message);
            }
        });
        thread.start();
    }
}
