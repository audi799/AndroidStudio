package kr.gimaek.mobilegimaek;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.gimaek.mobilegimaek.tool.EtcTool;

/**
 * Created by fromstog on 2016. 7. 1..
 */
public class CompanyInfo {
    public int companyNo;
    public String companyCode;
    public String companyName;
    public String companyDBName;
    public String companyDBPath;
    public String companyJohapCode;
    public String companyJohapDBName;
    public String companyJohapDBPath;
    public String companyGPSDBPath;
    public String companyJohapCustCode;
    public String comapnyDataConnectionURL;
    public String companyJohapConnectionURL;
    public String companyUserJh111;
    public String companyUserJh214;
    public String companyUserYu121;
    public String companyUserYu211;
    public String companyUserYu221;
    public String companyUserGPS;
    public String companyUserYu411;
    public String companyUserYu421;
    public String companyUserYu431;
    public String companyUserYu432;
    public String companyUserYu511;
    public String companyUserYu521;
    public String companyUserYu531;
    public String companyUserYu541;
    public ArrayList<ShCustInfo> alShCustInfo;
    public ArrayList<ShHyeonjangInfo> alShHyeonjangInfo;
    public ArrayList<ShJepumInfo> alShJepumInfo;
    public ArrayList<ShQcBaehapbi> alShQcBaehapbiInfo;

    public CompanyInfo(){
        companyNo = 0;
        companyCode = "";
        companyName = "";
        companyDBName = "";
        companyDBPath = "";
        companyJohapCode = "";
        companyJohapDBName = "";
        companyJohapDBPath = "";
        companyGPSDBPath = "";
        companyJohapCustCode = "";
        comapnyDataConnectionURL = "";
        companyJohapConnectionURL = "";
        companyUserJh111 = "";
        companyUserJh214 = "";
        companyUserYu121 = "";
        companyUserYu211 = "";
        companyUserYu221 = "";
        companyUserGPS = "";
        companyUserYu411 = "";
        companyUserYu421 = "";
        companyUserYu431 = "";
        companyUserYu432 = "";
        companyUserYu511 = "";
        companyUserYu521 = "";
        companyUserYu531 = "";
        companyUserYu541 = "";
        alShCustInfo = new ArrayList<ShCustInfo>();
        alShHyeonjangInfo = new ArrayList<ShHyeonjangInfo>();
        alShJepumInfo = new ArrayList<ShJepumInfo>();
        alShQcBaehapbiInfo = new ArrayList<ShQcBaehapbi>();
    }

    public void setShCustInfo(JSONArray jsonArray){
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ShCustInfo tmpInfo = new ShCustInfo();
                tmpInfo.cust_code = EtcTool.isnullStr(jsonObject, "cust_code").trim();
                tmpInfo.sangho = EtcTool.isnullStr(jsonObject, "sangho").trim();
                tmpInfo.daepyo = EtcTool.isnullStr(jsonObject, "daepyo").trim();
                tmpInfo.juso = EtcTool.isnullStr(jsonObject, "juso").trim();
                tmpInfo.cust_gubun_1 = EtcTool.isnullStr(jsonObject, "cust_gubun_1").trim();
                tmpInfo.cust_gubun_2 = EtcTool.isnullStr(jsonObject,"cust_gubun_2").trim();
                alShCustInfo.add(tmpInfo);
            } catch (Exception e){
                Log.e("ERROR", "UserInfo.setShCustInfo: " + e.toString());
            }
        }
    }

    public void setShHyeonjangInfo(JSONArray jsonArray){
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ShHyeonjangInfo tmpInfo = new ShHyeonjangInfo();
                tmpInfo.cust_code = EtcTool.isnullStr(jsonObject, "cust_code").trim();
                tmpInfo.hyeonjang_code = EtcTool.isnullStr(jsonObject, "hyeonjang_code").trim();
                tmpInfo.sangho = EtcTool.isnullStr(jsonObject, "sangho").trim();
                tmpInfo.hyeonjang_name = EtcTool.isnullStr(jsonObject, "hyeonjang_name").trim();
                tmpInfo.hyeonjang_tel = EtcTool.isnullStr(jsonObject, "hyeonjang_tel").trim();
                alShHyeonjangInfo.add(tmpInfo);
            } catch (Exception e){
                Log.e("ERROR", "UserInfo.setShHyeonjangInfo: " + e.toString());
            }
        }
    }

    public void setShJepumInfo(JSONArray jsonArray){
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ShJepumInfo tmpInfo = new ShJepumInfo();
                tmpInfo.jepum_code = EtcTool.isnullStr(jsonObject, "jepum_code").trim();
                tmpInfo.jepum_name = EtcTool.isnullStr(jsonObject, "jepum_name").trim();
                alShJepumInfo.add(tmpInfo);
            } catch (Exception e){
                Log.e("ERROR", "UserInfo.setShJepumInfo: " + e.toString());
            }
        }
    }

    public void setShQcBaehapbiInfo(JSONArray jsonArray){
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ShQcBaehapbi tmpInfo = new ShQcBaehapbi();
                tmpInfo.qc_code = EtcTool.isnullStr(jsonObject, "qc_code".trim());
                tmpInfo.jepum_code = EtcTool.isnullStr(jsonObject, "jepum_code").trim();
                tmpInfo.jijeong_sahang_1 = EtcTool.isnullStr(jsonObject, "jijeong_sahang_1").trim();
                tmpInfo.jijeong_sahang_2 = EtcTool.isnullStr(jsonObject, "jijeong_sahang_2").trim();
                tmpInfo.jijeong_sahang_3 = EtcTool.isnullStr(jsonObject, "jijeong_sahang_3").trim();
                tmpInfo.jijeong_sahang_4 = EtcTool.isnullStr(jsonObject, "jijeong_sahang_4").trim();
                tmpInfo.jijeong_sahang_5 = EtcTool.isnullStr(jsonObject, "jijeong_sahang_5").trim();
                alShQcBaehapbiInfo.add(tmpInfo);
            } catch (Exception e){
                Log.e("ERROR", "UserInfo.setShJepumInfo: " + e.toString());
            }
        }
    }
    public class ShCustInfo{
        public String cust_code;
        public String sangho;
        public String daepyo;
        public String juso;
        public String cust_gubun_1;
        public String cust_gubun_2;

        ShCustInfo(){
            cust_code = "";
            sangho = "";
            daepyo = "";
            juso = "";
            cust_gubun_1 = "";
            cust_gubun_2 = "";
        }
    }

    public class ShHyeonjangInfo{
        public String cust_code;
        public String hyeonjang_code;
        public String sangho;
        public String hyeonjang_name;
        public String hyeonjang_tel;

        ShHyeonjangInfo(){
            cust_code = "";
            hyeonjang_code = "";
            sangho = "";
            hyeonjang_name = "";
            hyeonjang_tel = "";
        }
    }

    public class ShJepumInfo{
        public String jepum_code;
        public String jepum_name;
        ShJepumInfo(){
            jepum_code = "";
            jepum_name = "";
        }
    }

    public class ShQcBaehapbi{
        public String qc_code;
        public String jepum_code;
        public String bigo;
        public String jijeong_sahang_1;
        public String jijeong_sahang_2;
        public String jijeong_sahang_3;
        public String jijeong_sahang_4;
        public String jijeong_sahang_5;
        ShQcBaehapbi(){
            qc_code = "";
            jepum_code ="";
            bigo = "";
            jijeong_sahang_1 = "";
            jijeong_sahang_2 = "";
            jijeong_sahang_3 = "";
            jijeong_sahang_4 = "";
            jijeong_sahang_5 = "";
        }
    }
}
