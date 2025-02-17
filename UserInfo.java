package kr.gimaek.mobilegimaek;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import kr.gimaek.mobilegimaek.tool.Base64Tool;
import kr.gimaek.mobilegimaek.tool.ConnectByAsyncTask;
import kr.gimaek.mobilegimaek.tool.EtcTool;
import kr.gimaek.mobilegimaek.tool.GetIdentifier;

/**
 * Created by fromstog on 2016. 6. 20..
 */
public class UserInfo extends Application {
    public static final String APP_DOMAIN = "kr.gimaek.mobilegimaek.userinfo";
    public static final String DATA_CONNECTION_URL = "http://211.105.106.**:8080/GM/getData.jsp?APINO=100&";
    public static final String DATA_CONNECTION_EXC_URL = "http://211.105.106.**:8080/GM/excuteData.jsp?APINO=100&";
    public static final String BACKUP_CONNECTION_URL = "http://211.105.106.**:8080/GM/getData.jsp?APINO=100&";
    public static String userID;
    public static String userPass;
    public static String userName;
    public static String userBuseoCode;
    public static String userJikchaek;
    public static String userPhoneNo;
    public static String userAuthkey;
    public static String userLoginState;
    public static int userCompanyIdx;
    public static ArrayList<CompanyInfo> userCompanyInfo;

    @Override
    public void onCreate(){
        super.onCreate();
        userID = "";
        userPass = "";
        userName = "";
        userBuseoCode = "";
        userJikchaek = "";
        userPhoneNo = "";
        userAuthkey = "";
        userLoginState = "NONE";//SUCC001:로그인성공, SUCC002:처음사용자, FAIL001:아이디비밀번호 미등록 혹은 틀림, FAIL002:Authkey틀림
        userCompanyIdx = 0;
        userCompanyInfo = new ArrayList<CompanyInfo>();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setUserInfoIntoSP(){//SharedPreferences
        //로컬기기에 남아 있는 로그인정보(아이디, 비밀번호)를 저장한다.
        SharedPreferences setting;
        setting = getSharedPreferences(APP_DOMAIN,0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString("userID",userID);
        editor.putString("userPass",userPass);
        editor.commit();
    }

    public void getUserInfoFromSP(){
        //로컬기기에 남아 있는 로그인정보(아이디, 비밀번호)를 가져온다.
        SharedPreferences setting;
        setting = getSharedPreferences(APP_DOMAIN, 0);
        this.userID = setting.getString("userID", "");
        this.userPass = setting.getString("userPass","");
    }

    public boolean process_login(String userID, String userPass){
        boolean returnBoo = false;
        if((userID.length()==0)|(userPass.length()==0)){
            //토스트메세지보여주기
            returnBoo = false;
        }else{
            Log.i("TEST","로그인을 위한 연결 시작");
            String sql;
            sql = "select user_id, user_password, user_name, Buseo_Code, jikchaek, phoneNo, mobile_authkey "
                    + "from cm_connect_user "
                    + "where user_id = '" + userID + "' "
                    + "and user_password = '" + userPass + "' ";

            String strURL = BACKUP_CONNECTION_URL
                    + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                    + "C=" + Base64Tool.Base64Encode("gimaek") + "&"
                    + "S=" + Base64Tool.Base64Encode("J0");
            strURL = strURL.replace("\n","");
            ConnectByAsyncTask conLogin = new ConnectByAsyncTask();
            conLogin.execute(strURL);
            try {
                setUserInfo(new JSONArray(conLogin.get()));
                returnBoo = true;
            } catch (Exception e){
                Log.e("ERROR", "UserInfo.process_login:" + e.toString());
            }
            Log.i("TEST","로그인을 위한 연결 끝");

            Log.i("TEST","회사정보를 위한 연결 시작");
            sql = "select identity(int, 0, 1) nno, U.user_id, U.cust_code, "
                    + "C.company_name, C.johap_code, C.johap_cust_code, C.dbname, C.dbpath, C.johapdbname, C.johapdbpath, C.gpsdbpath, "
                    + "M.jh111, M.jh214, M.yu121, M.yu211, M.yu221, M.gps, M.yu411,"
                    + "M.yu421, M.yu431, M.yu432, M.yu511, M.yu521, M.yu531, M.yu541 "
                    + "into #tmp_01 "
                    + "from cm_connect_user U left outer join cm_connect_cust C "
                    +                                    "on C.cust_code = U.cust_code "
                    +                        "left outer join cm_connect_mobile_user M "
                    +                                    "on M.company_code = U.cust_code "
                    +                                   "and M.userid = U.user_id "
                    + "where U.user_id = '" + userID + "' "
                    + "and U.user_password = '" + userPass + "' "

                    + "select * "
                    +       "from #tmp_01 "

                    + "drop table #tmp_01 ";
            strURL = BACKUP_CONNECTION_URL
                    + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                    + "C=" + Base64Tool.Base64Encode("gimaek") + "&"
                    + "S=" + Base64Tool.Base64Encode("J0");
            strURL = strURL.replace("\n","");
            ConnectByAsyncTask conCompany = new ConnectByAsyncTask();
            conCompany.execute(strURL);
            try {
                setCompanyInfoList(new JSONArray(conCompany.get()));
                returnBoo = true;
            }catch (Exception e){
                Log.e("ERROR", "UserInfo.process_login:" + e.toString());
            }
            Log.i("TEST","회사정보를 위한 연결 끝");
        }
        return returnBoo;
    }

    public void process_companydata_receive(){
        for(int i = 0, l = userCompanyInfo.size(); i < l; i++){
            userCompanyInfo.get(i).alShCustInfo.clear();
            userCompanyInfo.get(i).alShHyeonjangInfo.clear();
            userCompanyInfo.get(i).alShJepumInfo.clear();
            userCompanyInfo.get(i).alShQcBaehapbiInfo.clear();

            String sqlCust = "select cust_code, sangho, daepyo, juso, cust_gubun_1, cust_gubun_2 from cm_cust ";

            String sqlHyeonjang = "select H.cust_code, H.hyeonjang_code, H.hyeonjang_name, H.tel_1 hyeonjang_tel, C.sangho "
                    + "from cm_hyeonjang H left outer join cm_cust C "
                    +                                 "on C.cust_code = H.cust_code "
                    + "order by H.cust_code, H.hyeonjang_code ";

            String sqlJepum = "select jepum_code, jepum_name from cm_jepum ";

            String sqlBaehapbi = "select qc_code, jepum_code, "
                               +        "jijeong_sahang_1, jijeong_sahang_2, jijeong_sahang_2, jijeong_sahang_3, jijeong_sahang_4, jijeong_sahang_5 "
                               +       "from qc_baehapbi "
                    + "order by ymd ";

            String strURLCust = BACKUP_CONNECTION_URL
                    + "Q=" + Base64Tool.Base64Encode(sqlCust) + "&"
                    + "C=" + Base64Tool.Base64Encode(this.userCompanyInfo.get(i).companyCode) + "&"
                    + "S=" + Base64Tool.Base64Encode(this.userCompanyInfo.get(i).companyDBName);
            strURLCust = strURLCust.replace("\n","");
            ConnectByAsyncTask conCompany = new ConnectByAsyncTask();
            conCompany.execute(strURLCust);
            try {
                userCompanyInfo.get(i).setShCustInfo(new JSONArray(conCompany.get()));
            }catch (Exception e){
                Log.e("ERROR", "UserInfo.process_companydata_receive(Cust): " + e.toString());
            }

            String strURLHyeonjang = DATA_CONNECTION_URL
                    + "Q=" + Base64Tool.Base64Encode(sqlHyeonjang) + "&"
                    + "C=" + Base64Tool.Base64Encode(this.userCompanyInfo.get(i).companyCode) + "&"
                    + "S=" + Base64Tool.Base64Encode(this.userCompanyInfo.get(i).companyDBName);
            strURLHyeonjang = strURLHyeonjang.replace("\n","");
            ConnectByAsyncTask conHyeonjang = new ConnectByAsyncTask();
            conHyeonjang.execute(strURLHyeonjang);
            try {
                userCompanyInfo.get(i).setShHyeonjangInfo(new JSONArray(conHyeonjang.get()));
            }catch (Exception e){
                Log.e("ERROR", "UserInfo.process_companydata_receive(Hyeonjang): " + e.toString());
            }

            String strURLJepum = DATA_CONNECTION_URL
                    + "Q=" + Base64Tool.Base64Encode(sqlJepum) + "&"
                    + "C=" + Base64Tool.Base64Encode(this.userCompanyInfo.get(i).companyCode) + "&"
                    + "S=" + Base64Tool.Base64Encode(this.userCompanyInfo.get(i).companyDBName);
            strURLJepum = strURLJepum.replace("\n","");
            ConnectByAsyncTask conJepum = new ConnectByAsyncTask();
            conJepum.execute(strURLJepum);
            try {
                userCompanyInfo.get(i).setShJepumInfo(new JSONArray(conJepum.get()));
            }catch (Exception e){
                Log.e("ERROR", "UserInfo.process_companydata_receive(Jepum): " + e.toString());
            }
/*
            String strURLBaehapbi = DATA_CONNECTION_URL
                    + "Q=" + Base64Tool.Base64Encode(sqlBaehapbi) + "&"
                    + "C=" + Base64Tool.Base64Encode(this.userCompanyInfo.get(i).companyCode) + "&"
                    + "S=" + Base64Tool.Base64Encode(this.userCompanyInfo.get(i).companyDBName);
            strURLBaehapbi = strURLBaehapbi.replace("\n","");
            ConnectByAsyncTask conBaehapbi = new ConnectByAsyncTask();
            conBaehapbi.execute(strURLBaehapbi);
            try {
                userCompanyInfo.get(i).setShQcBaehapbiInfo(new JSONArray(conBaehapbi.get()));
            }catch (Exception e){
                Log.e("ERROR", "UserInfo.process_companydata_receive(Baehapbi): " + e.toString());
            } */
        }
        /* 나중에 데이터 제대로 들어오는지 테스트해야지
        for(int i = 0, l = userCompanyInfo.get(0).alShCustInfo.size(); i < l; i++){
            String s = userCompanyInfo.get(0).alShCustInfo.get(i).cust_code+"/"
                    + userCompanyInfo.get(0).alShCustInfo.get(i).sangho+"/"
                    + userCompanyInfo.get(0).alShCustInfo.get(i).daepyo+"/"
                    + userCompanyInfo.get(0).alShCustInfo.get(i).juso+"/"
                    + userCompanyInfo.get(0).alShCustInfo.get(i).cust_gubun_1+"/"
                    + userCompanyInfo.get(0).alShCustInfo.get(i).cust_gubun_2;
            Log.i("TEST",s);
        }*/

    }
    public void setUserInfo(JSONArray jsonArray){
        int arrayLenth = jsonArray.length();
        if (arrayLenth>0)
        {
            JSONObject obj = null;
            try {
                obj = jsonArray.getJSONObject(0);
                this.userID = EtcTool.isnullStr(obj,"user_id").trim();
                this.userPass = EtcTool.isnullStr(obj,"user_password").trim();
                this.userName = EtcTool.isnullStr(obj, "user_name").trim();
                this.userBuseoCode = EtcTool.isnullStr(obj, "buseo_code").trim();
                this.userJikchaek = EtcTool.isnullStr(obj, "jikchaek").trim();
                this.userPhoneNo = EtcTool.isnullStr(obj, "phoneno").trim();
                this.userAuthkey = EtcTool.isnullStr(obj, "mobile_authkey").trim();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (this.userAuthkey.equals("")) {
                //처음 접속자
                this.userLoginState = "SUCC002";
                setUserInfoIntoSP();
            }else if(!this.userAuthkey.equals(GetIdentifier.getDeviceSerialNumber()))
            {
                //AuthKey가 틀림
                //인증되지 않은 사용자
                this.userLoginState = "FAIL002";
            } else {
                this.userLoginState = "SUCC001";
                setUserInfoIntoSP();
            }
        }else{
            this.userLoginState = "FAIL001";
        }
    }

    protected void setCompanyInfoList(JSONArray arrObj) {
        for (int i = 0; i < arrObj.length(); i++) {
            try {
                JSONObject jObj = arrObj.getJSONObject(i);
                CompanyInfo cInfo = new CompanyInfo();

                cInfo.companyNo = EtcTool.isnullInt(jObj, "nno");
                cInfo.companyCode = EtcTool.isnullStr(jObj, "cust_code");
                cInfo.companyName = EtcTool.isnullStr(jObj, "company_name");
                cInfo.companyDBName = EtcTool.isnullStr(jObj, "dbname");
                cInfo.companyDBPath = EtcTool.isnullStr(jObj, "dbpath");
                cInfo.companyJohapCode = EtcTool.isnullStr(jObj, "johap_code");
                cInfo.companyJohapDBName = EtcTool.isnullStr(jObj, "johapdbname");
                cInfo.companyJohapDBPath = EtcTool.isnullStr(jObj, "johapdbpath");
                cInfo.companyJohapCustCode = EtcTool.isnullStr(jObj, "johap_cust_code");
                cInfo.companyGPSDBPath = EtcTool.isnullStr(jObj, "gpsdbpath");
                cInfo.companyUserJh111 = EtcTool.isnullStr(jObj, "jh111");
                cInfo.companyUserJh214 = EtcTool.isnullStr(jObj, "jh214");
                cInfo.companyUserYu121 = EtcTool.isnullStr(jObj, "yu121");
                cInfo.companyUserYu211 = EtcTool.isnullStr(jObj, "yu211");
                cInfo.companyUserYu221 = EtcTool.isnullStr(jObj, "yu221");
                cInfo.companyUserGPS = EtcTool.isnullStr(jObj, "gps");
                cInfo.companyUserYu411 = EtcTool.isnullStr(jObj, "yu411");
                cInfo.companyUserYu421 = EtcTool.isnullStr(jObj, "yu421");
                cInfo.companyUserYu431 = EtcTool.isnullStr(jObj, "yu431");
                cInfo.companyUserYu432 = EtcTool.isnullStr(jObj, "yu432");
                cInfo.companyUserYu511 = EtcTool.isnullStr(jObj, "yu511");
                cInfo.companyUserYu521 = EtcTool.isnullStr(jObj, "yu521");
                cInfo.companyUserYu531 = EtcTool.isnullStr(jObj, "yu531");
                cInfo.companyUserYu541 = EtcTool.isnullStr(jObj, "yu541");

                this.userCompanyInfo.add(cInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public static interface Predicate<T> {
        boolean apply(T type);
    }
    public static <T> Collection<T> filterS(Collection<T> col, Predicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for (T element: col) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public Collection<CompanyInfo.ShCustInfo> getCustInfoByCustGubun2(final String custGubun2){

        Predicate<CompanyInfo.ShCustInfo> validPersonPredicate = new Predicate<CompanyInfo.ShCustInfo>() {
            public boolean apply(CompanyInfo.ShCustInfo custInfo) {
                return custInfo.cust_gubun_2.equals(custGubun2);
            }
        };
        Collection<CompanyInfo.ShCustInfo> filteredItem = filterS(userCompanyInfo.get(userCompanyIdx).alShCustInfo, validPersonPredicate);

        return filteredItem;
    }

    public Collection<CompanyInfo.ShCustInfo> getCustInfoByCustCode(final String custCode){
        Predicate<CompanyInfo.ShCustInfo> validPersonPredicate = new Predicate<CompanyInfo.ShCustInfo>() {
            public boolean apply(CompanyInfo.ShCustInfo custInfo) {
                return custInfo.cust_code.equals(custCode);
            }
        };
        Collection<CompanyInfo.ShCustInfo> filteredItem = filterS(userCompanyInfo.get(userCompanyIdx).alShCustInfo, validPersonPredicate);

        return filteredItem;
    }

    public Collection<CompanyInfo.ShHyeonjangInfo> getHyeonjangInfoByCustCode(final String custCode){
        try {
            Predicate<CompanyInfo.ShHyeonjangInfo> validPersonPredicate = new Predicate<CompanyInfo.ShHyeonjangInfo>() {
                public boolean apply(CompanyInfo.ShHyeonjangInfo hyeonjangInfo) {
                    return hyeonjangInfo.cust_code.equals(custCode);
                }
            };
            Collection<CompanyInfo.ShHyeonjangInfo> filteredItem = filterS(userCompanyInfo.get(userCompanyIdx).alShHyeonjangInfo, validPersonPredicate);
            return filteredItem;
        }catch (Exception e){
            Log.e("ERROR", "UserInfo.getHyeonjangInfoByCustCode:  "+e.toString());
            return null;
        }

    }
}
