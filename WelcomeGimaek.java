package kr.gimaek.mobilegimaek.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.gimaek.mobilegimaek.R;
import kr.gimaek.mobilegimaek.UserInfo;
import kr.gimaek.mobilegimaek.Yu000;
import kr.gimaek.mobilegimaek.tool.Base64Tool;
import kr.gimaek.mobilegimaek.tool.ConnectByAsyncTask;
import kr.gimaek.mobilegimaek.tool.EtcTool;
import kr.gimaek.mobilegimaek.tool.ProgressArcView;

/**
 * Created by fromstog on 2016. 6. 27..
 */
public class WelcomeGimaek extends Fragment {
    Yu000 mHostActivity;
    UserInfo userInfo;
    ArrayList<MainInfo> alMainInfo;
    RelativeLayout rlGraph;
    ProgressArcView progressArcView;

    TextView tvWelcomeGimaekTotal, tvWelcomeGimaekChulha, tvWelcomeGimaekJarayng, tvWelcomeGimaekCnt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcomegimaek, container, false);

        alMainInfo = new ArrayList<MainInfo>();

        rlGraph = (RelativeLayout) rootView.findViewById(R.id.llGraph);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(700, 700);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        progressArcView = new ProgressArcView(rootView.getContext());
        progressArcView.setProgress(75);
        progressArcView.setLayoutParams(layoutParams);
        rlGraph.addView(progressArcView);

        tvWelcomeGimaekTotal = (TextView) rootView.findViewById(R.id.tvWelcomeGimaekTotal);
        tvWelcomeGimaekChulha = (TextView) rootView.findViewById(R.id.tvWelcomeGimaekChulha);
        tvWelcomeGimaekJarayng = (TextView) rootView.findViewById(R.id.tvWelcomeGimaekJarayng);
        tvWelcomeGimaekCnt = (TextView) rootView.findViewById(R.id.tvWelcomeGimaekCnt);

        process_data_receive();

        tvWelcomeGimaekTotal.setText(alMainInfo.get(0).plan_suryang.toString() + "㎥"); //--집계값이라서 어차피 로우1개라서 0으로 고정.
        tvWelcomeGimaekChulha.setText(alMainInfo.get(0).chulha_suryang.toString() + "㎥");
        tvWelcomeGimaekJarayng.setText(alMainInfo.get(0).janryang.toString() + "㎥");
        tvWelcomeGimaekCnt.setText(alMainInfo.get(0).chulha_cnt.toString() + "회");

        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach(activity);
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("WelcomeGimaek");
        mHostActivity.isWelcompage = true;
    }

    protected void process_data_receive() {
        String sql;
        String lymd, lcompanycode, ldbname;
        lymd = EtcTool.getCurrentDay();
        lcompanycode = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode;
        ldbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName;

        sql = "select isnull(sum(P.suryang),0) plan_suryang, isnull(sum(C.suryang),0) chulha_suryang, "
            +        "isnull(sum(P.suryang),0)-isnull(sum(C.suryang),0) janryang, count(C.suryang) chulha_cnt "
            +       "from yu_chulha_plan P left outer join yu_chulha C "
            +                                                "on P.ymd = C.ymd "
            +                                               "and P.nno = C.chulha_plan_no "
            +      "where P.ymd = " + " '" + lymd + "' ";

        String strURL = UserInfo.BACKUP_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(lcompanycode) + "&"
                + "S=" + Base64Tool.Base64Encode(ldbname);
        strURL = strURL.replace("\n", "");

        ConnectByAsyncTask conDS = new ConnectByAsyncTask();
        conDS.execute(strURL);
        Log.i("TEST",strURL);
        try {
            setMainInfo(new JSONArray(conDS.get()));
        } catch (Exception e) {
            Log.e("ERROR", "WelcomeGimaek.process_data_receive:" + e.toString());
        }
    }

    protected void setMainInfo(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MainInfo tmpInfo = new MainInfo();
                tmpInfo.plan_suryang = EtcTool.isnullFloat(jsonObject, "plan_suryang");
                tmpInfo.chulha_suryang = EtcTool.isnullFloat(jsonObject, "chulha_suryang");
                tmpInfo.janryang = EtcTool.isnullFloat(jsonObject, "janryang");
                tmpInfo.chulha_cnt = EtcTool.isnullInt(jsonObject, "chulha_cnt");
                alMainInfo.add(tmpInfo);
            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            } catch (Exception e) {
                Log.e("ERROR", e.toString());
            }
        }
    }

    protected class MainInfo {
        private Float plan_suryang;
        private Float chulha_suryang;
        private Float janryang;
        private Integer chulha_cnt;

        MainInfo() {
            plan_suryang = 0f;
            chulha_suryang = 0f;
            janryang = 0f;
            chulha_cnt = 0;
        }
    }
}



