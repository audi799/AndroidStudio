package kr.gimaek.mobilegimaek.yu211;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;

import kr.gimaek.mobilegimaek.R;
import kr.gimaek.mobilegimaek.Yu000;
import kr.gimaek.mobilegimaek.tool.EtcTool;

/**
 * Created by fromstog on 2016. 6. 29..
 */
public class Yu211_Ymd extends Fragment {
    Yu000 mHostActivity;

    DatePicker dpYu211_Ymd_Ymd;
    Button btnYu211_Ymd_Cancel, btnYu211_Ymd_Next;
    RadioButton rbYu211_Ymd_Sa, rbYu211_Ymd_Gw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_yu211_ymd,container, false);

        dpYu211_Ymd_Ymd = (DatePicker)rootView.findViewById(R.id.dpYu211_Ymd_Ymd);
        btnYu211_Ymd_Cancel = (Button)rootView.findViewById(R.id.btnYu211_ymd_Cancel);
        btnYu211_Ymd_Next = (Button)rootView.findViewById(R.id.btnYu211_ymd_Next);

        btnYu211_Ymd_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mHostActivity.stepNext();
            }
        });

        btnYu211_Ymd_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.appendYuChulhaPlan.setYmd(String.valueOf(dpYu211_Ymd_Ymd.getYear()) + "-"
                        + EtcTool.addZero(dpYu211_Ymd_Ymd.getMonth() + 1) + "-"
                        + EtcTool.addZero(dpYu211_Ymd_Ymd.getDayOfMonth()));
                mHostActivity.stepNext();
            }
        });

        rbYu211_Ymd_Sa = (RadioButton) rootView.findViewById(R.id.rbYu211_Ymd_Sa);
        rbYu211_Ymd_Gw = (RadioButton) rootView.findViewById(R.id.rbYu211_Ymd_Gw);

        rbYu211_Ymd_Sa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.appendYuChulhaPlan.setAppendGubun(0); //사급은 0
            }
        });

        rbYu211_Ymd_Gw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.appendYuChulhaPlan.setAppendGubun(1); //관급은 1
            }
        });


        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach(activity);
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("출하예정등록[예정일자]");
    }
}
