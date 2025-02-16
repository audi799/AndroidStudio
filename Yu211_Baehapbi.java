package kr.gimaek.mobilegimaek.yu211;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.gimaek.mobilegimaek.CompanyInfo;
import kr.gimaek.mobilegimaek.R;
import kr.gimaek.mobilegimaek.UserInfo;
import kr.gimaek.mobilegimaek.Yu000;
import kr.gimaek.mobilegimaek.tool.Base64Tool;
import kr.gimaek.mobilegimaek.tool.ConnectByAsyncTask;
import kr.gimaek.mobilegimaek.tool.EtcTool;

/**
 * Created by fromstog on 2016. 7. 1..
 */
public class Yu211_Baehapbi extends Fragment {
    Yu000 mHostActivity;
    UserInfo userInfo;
    AdapterSearchInfo adapterSearchInfo;
    ListView lvYu211_Baehapbi;
    Button btnYu211_Baehapbi_Cancel;
    Button btnYu211_Baehapbi_Next;
    TextView tvYu211_Baehapbi;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_yu211_baehapbi,container, false);
        userInfo = (UserInfo)getActivity().getApplicationContext();
        tvYu211_Baehapbi = (TextView)rootView.findViewById(R.id.tvYu211_Baehapbi);

        try {
            //arrayList = process_data_receive();
            adapterSearchInfo = new AdapterSearchInfo(getActivity(), R.layout.layout_listview_simple,
                    //(ArrayList<QcBaehapbi>)userInfo.getBaehapbiInfoByJepum(mHostActivity.appendYuChulhaPlan.jepumCode));
                    process_data_receive());
            //al);

            lvYu211_Baehapbi = (ListView) rootView.findViewById(R.id.lvYu211_Baehapbi);
            lvYu211_Baehapbi.setAdapter(adapterSearchInfo);
            lvYu211_Baehapbi.setOnItemClickListener(onBaehapbiItemClickListener);

        }catch (Exception e){
            Log.e("ERROR", "SearchInfo.onCreate.setAdapter: " + e.toString());
        }

        btnYu211_Baehapbi_Cancel = (Button)rootView.findViewById(R.id.btnYu211_Baehapbi_Cancel);
        btnYu211_Baehapbi_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.stepPrevious();
            }
        });

        btnYu211_Baehapbi_Next = (Button)rootView.findViewById(R.id.btnYu211_Baehapbi_Next);
        btnYu211_Baehapbi_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.stepNext();
            }
        });

        return rootView;
    }

    protected ArrayList<QcBaehapbi> process_data_receive(){
        ArrayList<QcBaehapbi> r = new ArrayList<QcBaehapbi>();

        String sqlBaehapbi = "select qc_code, max(ymd) ymd "
                           +        "into #tmp_01 "
                           +        "from qc_baehapbi "
                           +       "where ymd <= '" + mHostActivity.appendYuChulhaPlan.ymd + "' "
                           +         "and jepum_code = " + mHostActivity.appendYuChulhaPlan.jepumCode + " "
                           +    "group by qc_code "

                + "select cast(convert(varchar(10),B.ymd,112) as int) ymd, B.qc_code, B.jepum_code, "
                +        "B.jijeong_sahang_1, B.jijeong_sahang_2, B.jijeong_sahang_2, B.jijeong_sahang_3, B.jijeong_sahang_4, B.jijeong_sahang_5 "
                +       "from #tmp_01 T, qc_baehapbi B "
                +      "where T.ymd = B.ymd "
                +        "and T.qc_code = B.qc_code "
                + "order by B.qc_code "
                + "drop table #tmp_01 ";

        String strURLBaehapbi = userInfo.DATA_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sqlBaehapbi) + "&"
                + "C=" + Base64Tool.Base64Encode(userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode) + "&"
                + "S=" + Base64Tool.Base64Encode(userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName);
        strURLBaehapbi = strURLBaehapbi.replace("\n","");

        ConnectByAsyncTask conBaehapbi = new ConnectByAsyncTask();
        conBaehapbi.execute(strURLBaehapbi);
        try {
            JSONArray jsonArray = new JSONArray(conBaehapbi.get());
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                QcBaehapbi tmpInfo = new QcBaehapbi();
                tmpInfo.ymd = EtcTool.isnullInt(jsonObject, "ymd");
                tmpInfo.qc_code = EtcTool.isnullStr(jsonObject, "qc_code").trim();
                tmpInfo.jepum_code = EtcTool.isnullStr(jsonObject, "jepum_code").trim();
                tmpInfo.jijeong_sahang_1 = EtcTool.isnullStr(jsonObject, "jijeong_sahang_1").trim();
                tmpInfo.jijeong_sahang_2 = EtcTool.isnullStr(jsonObject, "jijeong_sahang_2").trim();
                tmpInfo.jijeong_sahang_3 = EtcTool.isnullStr(jsonObject, "jijeong_sahang_3").trim();
                tmpInfo.jijeong_sahang_4 = EtcTool.isnullStr(jsonObject, "jijeong_sahang_4").trim();
                tmpInfo.jijeong_sahang_5 = EtcTool.isnullStr(jsonObject, "jijeong_sahang_5").trim();
                r.add(tmpInfo);
            }
            Log.i("TEST",String.valueOf(r.size()));
        }catch (Exception e){
            Log.e("ERROR", "UserInfo.process_companydata_receive(Baehapbi): " + e.toString());
        }
        return r;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach(activity);
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("출하예정등록[배합비]");
    }

    public class AdapterSearchInfo extends ArrayAdapter<QcBaehapbi> {
        private Context mContext;
        private int mResource;
        private ArrayList<QcBaehapbi> mArrayList;
        private LayoutInflater mLayoutInflater;

        public AdapterSearchInfo(Context context, int resource, ArrayList<QcBaehapbi> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.mArrayList = objects;
            this.mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            QcBaehapbi shQcBaehapbiInfo = mArrayList.get(position);
            if(convertView==null)
            {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(shQcBaehapbiInfo!=null){
                TextView tvSimpleField_1 = (TextView)convertView.findViewById(R.id.tvListviewSimple_field_1);
                TextView tvSimpleField_2 = (TextView)convertView.findViewById(R.id.tvListviewSimple_field_2);
                tvSimpleField_1.setText(shQcBaehapbiInfo.bigo + "\n"
                        + shQcBaehapbiInfo.jijeong_sahang_1 + "\n"
                        + shQcBaehapbiInfo.jijeong_sahang_2 + "\n"
                        + shQcBaehapbiInfo.jijeong_sahang_2 + "\n"
                 );
                tvSimpleField_2.setText(shQcBaehapbiInfo.qc_code);

            }

            return convertView;
        }


    }
    AdapterView.OnItemClickListener onBaehapbiItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                mHostActivity.appendYuChulhaPlan.setQcCode(
                        ((QcBaehapbi) parent.getItemAtPosition(position)).qc_code,
                        ((QcBaehapbi) parent.getItemAtPosition(position)).bigo,
                        ((QcBaehapbi) parent.getItemAtPosition(position)).jijeong_sahang_1,
                        ((QcBaehapbi) parent.getItemAtPosition(position)).jijeong_sahang_2,
                        ((QcBaehapbi) parent.getItemAtPosition(position)).jijeong_sahang_3,
                        ((QcBaehapbi) parent.getItemAtPosition(position)).jijeong_sahang_4,
                        ((QcBaehapbi) parent.getItemAtPosition(position)).jijeong_sahang_5
                );
                tvYu211_Baehapbi.setText(((QcBaehapbi) parent.getItemAtPosition(position)).qc_code);
            }catch (Exception e){
                Log.i("ERROR",e.toString());
            }
        }
    };

    public class QcBaehapbi{
        public int ymd;
        public String qc_code;
        public String jepum_code;
        public String bigo;
        public String jijeong_sahang_1;
        public String jijeong_sahang_2;
        public String jijeong_sahang_3;
        public String jijeong_sahang_4;
        public String jijeong_sahang_5;
        QcBaehapbi(){
            ymd = 0;
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
