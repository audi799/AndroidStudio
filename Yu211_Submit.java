package kr.gimaek.mobilegimaek.yu211;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import kr.gimaek.mobilegimaek.R;
import kr.gimaek.mobilegimaek.Yu000;
import kr.gimaek.mobilegimaek.fragment.WelcomeGimaek;
import kr.gimaek.mobilegimaek.fragment.Yu531;
import kr.gimaek.mobilegimaek.tool.EtcTool;

/**
 * Created by fromstog on 2016. 7. 2..
 */
public class Yu211_Submit extends Fragment {
    Yu000 mHostActivity;
    Button btnYu211_Submit_Cancel, btnYu211_Submit_Ok;

    TextView tvYu211_Submit_Ymd;
    TextView tvYu211_Submit_Sangho;
    TextView tvYu211_Submit_HyeonjangName;
    TextView tvYu211_Submit_JepumName;
    TextView tvYu211_Submit_Suryang;
    TextView tvYu211_Submit_Jijeong_1;
    TextView tvYu211_Submit_Jijeong_2;
    TextView tvYu211_Submit_Jijeong_3;
    TextView tvYu211_Submit_Bigo;
    TextView tvYu211_Submit_ChulhaJisi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        /*View rootView =null;
        try {
            rootView = inflater.inflate(R.layout.fragment_yu211_submit, container, false);
        }catch (Exception e){
            Log.i("TEST",e.toString());
        }*/
        View rootView =inflater.inflate(R.layout.fragment_yu211_submit, container, false);

        tvYu211_Submit_Ymd = (TextView) rootView.findViewById(R.id.tvYu211_Submit_Ymd);
        tvYu211_Submit_Sangho = (TextView) rootView.findViewById(R.id.tvYu211_Submit_Sangho);
        tvYu211_Submit_HyeonjangName = (TextView) rootView.findViewById(R.id.tvYu211_Submit_HyeonjangName);
        tvYu211_Submit_JepumName = (TextView) rootView.findViewById(R.id.tvYu211_Submit_JepumName);
        tvYu211_Submit_Suryang = (TextView) rootView.findViewById(R.id.tvYu211_Submit_Suryang);
        tvYu211_Submit_Jijeong_1 = (TextView) rootView.findViewById(R.id.tvYu211_Submit_Jijeong_1);
        tvYu211_Submit_Jijeong_2 = (TextView) rootView.findViewById(R.id.tvYu211_Submit_Jijeong_2);
        tvYu211_Submit_Jijeong_3 = (TextView) rootView.findViewById(R.id.tvYu211_Submit_Jijeong_3);
        tvYu211_Submit_Bigo = (TextView) rootView.findViewById(R.id.tvYu211_Submit_Bigo);
        tvYu211_Submit_ChulhaJisi = (TextView) rootView.findViewById(R.id.tvYu211_Submit_ChulhaJisi);

        tvYu211_Submit_Ymd.setText(mHostActivity.appendYuChulhaPlan.ymd);
        tvYu211_Submit_Sangho.setText(mHostActivity.appendYuChulhaPlan.sangho);
        tvYu211_Submit_HyeonjangName.setText(mHostActivity.appendYuChulhaPlan.hyeonjangName);
        tvYu211_Submit_JepumName.setText(mHostActivity.appendYuChulhaPlan.jepumName);
        tvYu211_Submit_Suryang.setText(EtcTool.formatDouble(mHostActivity.appendYuChulhaPlan.suryang,"#,###.##"));
        tvYu211_Submit_Jijeong_1.setText(mHostActivity.appendYuChulhaPlan.jijeongSahang_1);
        tvYu211_Submit_Jijeong_2.setText(mHostActivity.appendYuChulhaPlan.jijeongSahang_2);
        tvYu211_Submit_Jijeong_3.setText(mHostActivity.appendYuChulhaPlan.jijeongSahang_3);
        tvYu211_Submit_Bigo.setText(mHostActivity.appendYuChulhaPlan.bigo);
        tvYu211_Submit_ChulhaJisi.setText(mHostActivity.appendYuChulhaPlan.chulhaJisi);

        btnYu211_Submit_Cancel = (Button)rootView.findViewById(R.id.btnYu211_Submit_Cancel);
        btnYu211_Submit_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.stepPrevious();
            }
        });

        btnYu211_Submit_Ok = (Button)rootView.findViewById(R.id.btnYu211_Submit_Ok);
        btnYu211_Submit_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.stepNext();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.content_frame, new Yu211_Ymd()).commit();  //예정등록 완료 후, 출하예정등록 첫페이지로 이동.
            }
        });
        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach(activity);
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("출하예정등록");
    }
}
