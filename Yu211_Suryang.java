package kr.gimaek.mobilegimaek.yu211;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import kr.gimaek.mobilegimaek.R;
import kr.gimaek.mobilegimaek.Yu000;
import kr.gimaek.mobilegimaek.tool.EtcTool;

/**
 * Created by fromstog on 2016. 6. 29..
 */
public class Yu211_Suryang extends Fragment {
    Yu000 mHostActivity;
    Button btnYu211_Suryang_Cancel, btnYu211_Suryang_Next;

    EditText etYu211_Suryang_Suryang;
    EditText etYu211_Suryang_Danga;
    EditText etYu211_Suryang_Geumaek;
    EditText etYu211_Suryang_Seaek;
    EditText etYu211_Suryang_Hapgye;
    EditText etYu211_Suryang_Bigo;
    EditText etYu211_Suryang_ChulhaJisi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_yu211_suryang,container, false);

        etYu211_Suryang_Suryang = (EditText)rootView.findViewById(R.id.etYu211_Suryang_Suryang);
        etYu211_Suryang_Danga = (EditText)rootView.findViewById(R.id.etYu211_Suryang_Danga);
        etYu211_Suryang_Geumaek = (EditText)rootView.findViewById(R.id.etYu211_Suryang_Geumaek);
        etYu211_Suryang_Seaek = (EditText)rootView.findViewById(R.id.etYu211_Suryang_Seaek);
        etYu211_Suryang_Hapgye = (EditText)rootView.findViewById(R.id.etYu211_Suryang_Hapgye);
        etYu211_Suryang_Bigo = (EditText)rootView.findViewById(R.id.etYu211_Suryang_Bigo);
        etYu211_Suryang_ChulhaJisi = (EditText)rootView.findViewById(R.id.etYu211_Suryang_ChulhaJisi);

        btnYu211_Suryang_Cancel = (Button)rootView.findViewById(R.id.btnYu211_Suryang_Cancel);
        btnYu211_Suryang_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.stepPrevious();
            }
        });

        btnYu211_Suryang_Next = (Button)rootView.findViewById(R.id.btnYu211_Suryang_Next);
        btnYu211_Suryang_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EtcTool.isStringDouble(etYu211_Suryang_Suryang.getText().toString())){
                    mHostActivity.appendYuChulhaPlan.setSuryang(Double.parseDouble(etYu211_Suryang_Suryang.getText().toString()));
                    mHostActivity.stepNext();

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  //키보드 강제로 가리기 - 1
                    imm.hideSoftInputFromWindow(etYu211_Suryang_Suryang.getWindowToken(), 0);                                  //키보드 강제로 가리기 - 2

                }else if(mHostActivity.appendYuChulhaPlan.suryang == 0){
                    Toast.makeText(mHostActivity.getApplication(), "예정량을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mHostActivity.getApplication(), "예정량을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach(activity);
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("출하예정등록[수량, 단가, 금액]");
    }
}
