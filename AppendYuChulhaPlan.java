package kr.gimaek.mobilegimaek.yu211;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Handler;

import kr.gimaek.mobilegimaek.R;
import kr.gimaek.mobilegimaek.UserInfo;
import kr.gimaek.mobilegimaek.tool.Base64Tool;
import kr.gimaek.mobilegimaek.tool.ConnectByAsyncTask;

/**
 * Created by fromstog on 2016. 6. 28..
 */
public class AppendYuChulhaPlan{
    UserInfo userInfo;
    String ymd;
    String companyCode;
    String jisiGubun, jisiBeonho;
    String custCode, sangho;
    String hyeonjangCode, hyeonjangName;
    String jepumCode, jepumName, qcCode, qcBigo;
    double suryang, danga, geumaek, seaek, hapgye;
    String sigan, chulbalGubun;
    String hyeonjangGeori, hyeonjangTel;
    String bigo, chulhaJisi;
    String jijeongSahang_1,jijeongSahang_2,jijeongSahang_3,jijeongSahang_4,jijeongSahang_5;
    ArrayList<Fragment> listAppendStep_Sa;
    ArrayList<Fragment> listAppendStep_Gw;
    Context context;

    int idx;
    int appendGubun = 0;//0:사급 1:관급
    Yu211_Ymd yu211Ymd;
    Yu211_Jumun yu211Jumun;
    Yu211_JumunJp yu211JumunJp;
    Yu211_Cust yu211Cust;
    Yu211_Hyeonjang yu211Hyeonjang;
    Yu211_Jepum yu211Jepum;
    Yu211_Baehapbi yu211Baehapbi;
    Yu211_Suryang yu211Suryang;
    Yu211_Etc yu211Etc;
    Yu211_Submit yu211Submit;

    public AppendYuChulhaPlan(Context toastContext){
        idx = 0;
        Log.i("TEST","AppendYuCHulhaPlan은 몇번실행되는가?");
        context = toastContext;

        //업체 종류에 따른 다른 순서, 다른 구성으로 진행가능
        listAppendStep_Sa = new ArrayList<Fragment>();
        listAppendStep_Gw = new ArrayList<Fragment>();
        yu211Ymd = new Yu211_Ymd();
        yu211Jumun = new Yu211_Jumun();
        yu211JumunJp = new Yu211_JumunJp();
        yu211Cust = new Yu211_Cust();
        yu211Hyeonjang = new Yu211_Hyeonjang();
        yu211Jepum = new Yu211_Jepum();
        yu211Baehapbi = new Yu211_Baehapbi();
        yu211Suryang = new Yu211_Suryang();
        yu211Submit = new Yu211_Submit();
        yu211Etc = new Yu211_Etc();

        ymd = "";
        companyCode = "";
        jisiGubun = ""; jisiBeonho = "";
        custCode=""; sangho = "";
        hyeonjangCode = ""; hyeonjangName = "";
        jepumCode=""; jepumName=""; qcCode=""; qcBigo="";
        suryang=0; danga=0; geumaek=0; seaek=0; hapgye=0;
        sigan=""; chulbalGubun="";
        hyeonjangGeori=""; hyeonjangTel="";
        bigo=""; chulhaJisi="";
        jijeongSahang_1=""; jijeongSahang_2=""; jijeongSahang_3=""; jijeongSahang_4=""; jijeongSahang_5="";

        listAppendStep_Sa.add(yu211Ymd);
        listAppendStep_Sa.add(yu211Cust);
        listAppendStep_Sa.add(yu211Hyeonjang);
        listAppendStep_Sa.add(yu211Jepum);
        listAppendStep_Sa.add(yu211Baehapbi);
        listAppendStep_Sa.add(yu211Suryang);
        listAppendStep_Sa.add(yu211Submit);

        listAppendStep_Gw.add(yu211Ymd);
        listAppendStep_Gw.add(yu211Cust);
        listAppendStep_Gw.add(yu211Hyeonjang);
        listAppendStep_Gw.add(yu211Jumun);
        listAppendStep_Gw.add(yu211JumunJp);
        listAppendStep_Gw.add(yu211Baehapbi);
        listAppendStep_Gw.add(yu211Suryang);
        listAppendStep_Gw.add(yu211Submit);
    }

    public Fragment getCurentlyForm(){
        switch (appendGubun) {
            case 0:
                return listAppendStep_Sa.get(idx);
            case 1:
                return listAppendStep_Gw.get(idx);
            default:
                return null;
        }
    }

    public Fragment stepNext(){
        idx++;
        Fragment fr = new Fragment();
        switch (appendGubun){
            case 0:  //사급
                if(idx>=listAppendStep_Sa.size()){
                    idx = 0;
                    applyUpdate();
                    //fr = listAppendStep_Sa.get(-1);
                }else{
                    try {
                        fr = listAppendStep_Sa.get(idx);
                    }catch (Exception e){
                        Log.i("TEST",e.toString());
                    }
                }
                break;
            case 1:  //관급
                if(idx>=listAppendStep_Gw.size()){
                    idx = 0;
                    applyUpdate();
                    //fr = listAppendStep_Gw.get(-1);

                }else{
                    try {
                        fr = listAppendStep_Gw.get(idx);
                    }catch (Exception e){
                        Log.i("TEST",e.toString());
                    }
                }
                break;
        }
        return fr;
    }

    public Fragment stepPrevious(){
        Fragment fr = new Fragment();
        idx--;
        switch (appendGubun){
            case 0:
                if(idx>listAppendStep_Sa.size()){
                    idx = 0;
                    applyUpdate();
                    fr =  listAppendStep_Sa.get(-1);
                }else{
                    fr = listAppendStep_Sa.get(idx);
                }
                break;
            case 1:
                if(idx>listAppendStep_Gw.size()){
                    idx = 0;
                    applyUpdate();
                    fr = listAppendStep_Gw.get(-1);
                }else{
                    fr = listAppendStep_Gw.get(idx);
                }
        }
        return fr;
    }

    public String setYmd(String ymd){
        Log.i("TEST",ymd);
        this.ymd = ymd;
        return "";
    }

    public String setAppendGubun(int appendGubun){
        Log.i("TEST",String.valueOf(appendGubun));
        this.appendGubun = appendGubun;
        return "";
    }

    public String setCust(String cust, String sangho){
        this.custCode = cust;
        this.sangho = sangho;
        return "";
    }

    public String setHyeonjang(String hyeonjangCode, String hyeonjangName){
        this.hyeonjangCode = hyeonjangCode;
        this.hyeonjangName = hyeonjangName;
        return "";
    }

    public String setJisiBeonho(String jisiGubun, String jisiBeonho){
        this.jisiGubun = jisiGubun;
        this.jisiBeonho = jisiBeonho;
        return "";
    }

    public String setJepum(String jepumCode, String jepumName){
        this.jepumCode = jepumCode;
        this.jepumName = jepumName;
        return "";
    }
    public String setQcCode(String qcCode, String qcBigo,
                            String jijeong_1,String jijeong_2,String jijeong_3,String jijeong_4,String jijeong_5){
        this.qcCode = qcCode;
        this.qcBigo = qcBigo;
        this.jijeongSahang_1 = jijeong_1;
        this.jijeongSahang_2 = jijeong_2;
        this.jijeongSahang_3 = jijeong_3;
        this.jijeongSahang_4 = jijeong_4;
        this.jijeongSahang_5 = jijeong_5;
        return "";
    }

    public String setSuryangGeumaekEtc(Double suryang, Double danga, Double geumaek, Double seaek, Double hapgye, String Bigo, String ChulhaJisi){
        this.suryang = suryang;
        this.danga = danga;
        this.geumaek = geumaek;
        this.seaek = seaek;
        this.hapgye = hapgye;
        this.bigo = bigo;
        this.chulhaJisi = chulhaJisi;
        return "";
    }

    public String setSuryang(Double suryang){
        this.suryang = suryang;
        return  "";
    }
    public void setReset(){
        //초기화
        ymd="";
        companyCode="";
        jisiGubun=""; jisiBeonho="";
        custCode=""; sangho="";
        hyeonjangCode=""; hyeonjangName="";
        jepumCode=""; jepumName=""; qcCode=""; qcBigo="";
        suryang=0; danga=0; geumaek=0; seaek=0; hapgye=0;
        sigan=""; chulbalGubun="";
        hyeonjangGeori=""; hyeonjangTel="";
        bigo=""; chulhaJisi="";
        jijeongSahang_1=""; jijeongSahang_2=""; jijeongSahang_3=""; jijeongSahang_4=""; jijeongSahang_5="";
    }

    private boolean applyUpdate(){
        String jisigubun;
        if(this.jisiGubun.equals("")){
            jisigubun = "null";
        }else{
            jisigubun = this.jisiGubun;
        }
        String sql = "declare @ymd datetime "
                + "declare @max int "
                //+ "set @ymd = '" + ymd + "' "
                + "set @ymd = '" + "2017-01-01" + "' "
                + "set @max = isnull((select max(nno) from yu_chulha_plan where ymd = @ymd),0)+1 "
                + "insert into yu_chulha_plan(ymd, nno, company_code, jisi_gubun, jisi_beonho, "
                + "cust_code, hyeonjang_code, jepum_code, qc_code, "
                + "suryang, bigo, chulha_jisi, "
                + "jijeong_sahang_1, jijeong_sahang_2, jijeong_sahang_3, jijeong_sahang_4, jijeong_sahang_5) "
                + "values(@ymd, @max, '"
                + userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode.trim() + "',"
                + jisigubun + ",'" + jisiBeonho + "', '"
                + custCode + "', '"+ hyeonjangCode + "','"+ jepumCode + "','"+ qcCode + "', "
                //+ suryang +"," + danga + ","+ geumaek + "," + seaek + "," + hapgye + ","
                + suryang +",'"+ bigo +"','"+ chulhaJisi +"','"
                + jijeongSahang_1 +"','"+ jijeongSahang_2 +"','"+ jijeongSahang_3 +"','"+ jijeongSahang_4 +"','"+ jijeongSahang_5 +"') ";
        Log.i("TEST",sql);
        String strURL = userInfo.DATA_CONNECTION_EXC_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode) + "&"
                + "S=" + Base64Tool.Base64Encode(userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName);
        strURL = strURL.replace("\n","");
        Log.i("TEST",strURL);
        ConnectByAsyncTask con = new ConnectByAsyncTask();
        try {
            con.execute(strURL);
            String sResult = con.get();
            sResult = sResult.trim().replace("\n","");
            Log.i("TEST","Result: "+sResult);
            if(sResult.equals("SUCC")){
                Toast.makeText(context, "출하예정이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "예정등록에 실패하였습니다!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.i("TEST",e.toString());
        }


        /*ymd;
        companyCode;
        jisiGubun, jisiBeonho;
        custCode, sangho;
        hyeonjangCode, hyeonjangName;
        jepumCode, jepumName, qcCode, qcBigo;
        suryang, danga, geumaek, seaek, hapgye;
        sigan, chulbalGubun;
        hyeonjangGeori, hyeonjangTel;
        bigo, chulhaJisi;
        jijeongSahang_1,jijeongSahang_2,jijeongSahang_3,jijeongSahang_4,jijeongSahang_5;*/

        return true;
    }

}
