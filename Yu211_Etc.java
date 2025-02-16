package kr.gimaek.mobilegimaek.yu211;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.gimaek.mobilegimaek.R;
import kr.gimaek.mobilegimaek.Yu000;

/**
 * Created by fromstog on 2016. 6. 29..
 */
public class Yu211_Etc extends Fragment {
    Yu000 mHostActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_yu211_etc,container, false);
        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach(activity);
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("출하예정들록[기타 지정사항]");
    }
}
