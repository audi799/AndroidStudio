package kr.gimaek.mobilegimaek.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class Yu521 extends Fragment {
    //어댑터에 적용할 master 데이터
    ArrayList<Yu521Info> alYu521Info;
    AdapterYu521 adapterYu521;
    //ApplicationClsss
    UserInfo userInfo;

    Yu000 mHostActivity;

    TextView tvYu521Ymd;
    Button btnYu521Search;
    DatePickerDialog datePickerDialog;

    //리스트뷰에 들어갈 공용 view에 대한 선언
    TextView tvListviewMasterTitle;
    TextView tvListviewMasterSubTitle_1;
    TextView tvListviewMasterSubTitle_2;

    RelativeLayout rlListviewMasterContentLine_1;
    TextView tvListviewMasterContentLine_1_Left;
    TextView tvListviewMasterContentLine_1_Center;
    TextView tvListviewMasterContentLine_1_Right;

    RelativeLayout rlListviewMasterContentLine_2;
    TextView tvListviewMasterContentLine_2_Left;
    TextView tvListviewMasterContentLine_2_Center;
    TextView tvListviewMasterContentLine_2_Right;

    RelativeLayout rlListviewMasterContentLine_3;
    TextView tvListviewMasterContentLine_3_Left;
    TextView tvListviewMasterContentLine_3_Center;
    TextView tvListviewMasterContentLine_3_Right;

    ListView lvMaster;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_yu521, container, false);

        //ApplicationClass에 대한 접근
        userInfo = (UserInfo)getActivity().getApplicationContext();

        //각 view에 대한 정의
        tvYu521Ymd = (TextView) rootView.findViewById(R.id.tvYu521Ymd);
        btnYu521Search = (Button) rootView.findViewById(R.id.btnYu521Search);
        lvMaster = (ListView)rootView.findViewById(R.id.lvYu521);

        datePickerDialog = new DatePickerDialog(getActivity(),
                onYmdDateSetListener,
                Integer.valueOf(EtcTool.getCurrentDay().substring(0, 4)),
                Integer.valueOf(EtcTool.getCurrentDay().substring(4, 6)) - 1,
                Integer.valueOf(EtcTool.getCurrentDay().substring(6, 8)));

        tvYu521Ymd.setText(EtcTool.setDate(EtcTool.getCurrentDay()));

        //리스트뷰와 연결될 실제 자료
        alYu521Info = new ArrayList<Yu521Info>();
        //마스터 어댑터 생성
        adapterYu521 = new AdapterYu521(getActivity(),R.layout.layout_listview_master,alYu521Info);

        //자료조회
        process_data_receive();

        tvYu521Ymd.setOnClickListener(onYmdClickListener);
        btnYu521Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_data_receive();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach( activity );
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("수금일보");
    }

    protected void process_data_receive(){
        String sql;
        String lymd, lcustcode, lcompanycode, ldbname;
        lymd = tvYu521Ymd.getText().toString();
        lcustcode = "";  //-- 공란을 준 이유 : 애초에 저장프로시저 자체가 필요없는 인자를 넘겨받기때문에...Yu521저장프로시저 참조.
        lcompanycode = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode;
        ldbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName;

        sql = "Yu521 '" + lcustcode + "', '" + lymd + "' ";
        Log.i("TEST",sql);

        String strURL = UserInfo.BACKUP_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(lcompanycode) + "&"
                + "S=" + Base64Tool.Base64Encode(ldbname);
        strURL = strURL.replace("\n","");

        ConnectByAsyncTask conDS = new ConnectByAsyncTask();
        conDS.execute(strURL);
        try {
            setYu521Info(new JSONArray(conDS.get()));
            //마스터리스트뷰에 대한 정보갱신
            lvMaster.setAdapter(adapterYu521);
            Toast.makeText(getActivity(), "정보가 갱신 되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ERROR", "Yu521.process_data_receive:"+e.toString());
        }
    }

    View.OnClickListener onYmdClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) { datePickerDialog.show(); }
    };

    DatePickerDialog.OnDateSetListener onYmdDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tvYu521Ymd.setText(String.valueOf(year) + "-" + EtcTool.addZero(monthOfYear + 1) + "-" + EtcTool.addZero(dayOfMonth));
            process_data_receive();
        }
    };

    protected void setYu521Info(JSONArray jsonArray){
        alYu521Info.clear();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Yu521Info tmpInfo = new Yu521Info();
                    //마스터 로우
                    tmpInfo.sangho = EtcTool.isnullStr(jsonObject, "sangho");
                    tmpInfo.hyeonjang_name= EtcTool.isnullStr(jsonObject,"hyeonjang_name");
                    tmpInfo.geumaek = EtcTool.isnullDouble(jsonObject, "geumaek");
                    tmpInfo.halin = EtcTool.isnullDouble(jsonObject, "halin");
                    tmpInfo.japiik = EtcTool.isnullDouble(jsonObject, "japiik");
                    tmpInfo.jeokyo = EtcTool.isnullStr(jsonObject, "jeokyo");
                    alYu521Info.add(tmpInfo);
            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            } catch (Exception e){
                Log.e("ERROR",e.toString());
            }
        }
    }

    protected class AdapterYu521 extends ArrayAdapter<Yu521Info> {
        private Context mContext;
        private int mResource;
        private ArrayList<Yu521Info> caAlYu521Info;//CustAdapterArrayListYu521Info
        private LayoutInflater mLayoutInflater;

        public AdapterYu521(Context context, int resource, ArrayList<Yu521Info> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.caAlYu521Info = objects;
            this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Yu521Info caTmpYu521Info = caAlYu521Info.get(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(caTmpYu521Info != null){
                try{
                    tvListviewMasterTitle = (TextView)convertView.findViewById(R.id.tvListviewMasterTitle);
                    tvListviewMasterSubTitle_1 = (TextView)convertView.findViewById(R.id.tvListviewMasterSubTitle_1);
                    tvListviewMasterSubTitle_2 = (TextView)convertView.findViewById(R.id.tvListviewMasterSubTitle_2);

                    rlListviewMasterContentLine_1 = (RelativeLayout)convertView.findViewById(R.id.rlListviewMasterContentLine_1);
                    tvListviewMasterContentLine_1_Left = (TextView)convertView.findViewById(R.id.tvListviewMasterContentLine_1_Left);
                    tvListviewMasterContentLine_1_Center = (TextView)convertView.findViewById(R.id.tvListviewMasterContentLine_1_Center);
                    tvListviewMasterContentLine_1_Right = (TextView)convertView.findViewById(R.id.tvListviewMasterContentLine_1_Right);

                    rlListviewMasterContentLine_2 = (RelativeLayout)convertView.findViewById(R.id.rlListviewMasterContentLine_2);
                    tvListviewMasterContentLine_2_Left = (TextView)convertView.findViewById(R.id.tvListviewMasterContentLine_2_Left);
                    tvListviewMasterContentLine_2_Center = (TextView)convertView.findViewById(R.id.tvListviewMasterContentLine_2_Center);
                    tvListviewMasterContentLine_2_Right = (TextView)convertView.findViewById(R.id.tvListviewMasterContentLine_2_Right);

                    rlListviewMasterContentLine_3 = (RelativeLayout)convertView.findViewById(R.id.rlListviewMasterContentLine_3);
                    tvListviewMasterContentLine_3_Left = (TextView)convertView.findViewById(R.id.tvListviewMasterContentLine_3_Left);
                    tvListviewMasterContentLine_3_Center = (TextView)convertView.findViewById(R.id.tvListviewMasterContentLine_3_Center);
                    tvListviewMasterContentLine_3_Right = (TextView)convertView.findViewById(R.id.tvListviewMasterContentLine_3_Right);

                    tvListviewMasterTitle.setText(caTmpYu521Info.sangho);
                    tvListviewMasterSubTitle_1.setText(caTmpYu521Info.hyeonjang_name);
                    tvListviewMasterSubTitle_2.setVisibility(View.GONE);

                    tvListviewMasterContentLine_1_Left.setText("수금액:" + EtcTool.formatDouble(caTmpYu521Info.geumaek, "#,##0"));
                    tvListviewMasterContentLine_1_Center.setText("할인:" + EtcTool.formatDouble(caTmpYu521Info.halin, "#,##0"));
                    tvListviewMasterContentLine_1_Right.setText("잡이익:" + EtcTool.formatDouble(caTmpYu521Info.japiik, "#,##0"));

                    tvListviewMasterContentLine_2_Left.setText("비고:" + caTmpYu521Info.jeokyo);
                    tvListviewMasterContentLine_2_Center.setVisibility(View.GONE);
                    tvListviewMasterContentLine_2_Right.setVisibility(View.GONE);

                    rlListviewMasterContentLine_3.setVisibility(View.GONE);
                    tvListviewMasterContentLine_3_Left.setVisibility(View.GONE);
                    tvListviewMasterContentLine_3_Center.setVisibility(View.GONE);
                    tvListviewMasterContentLine_3_Right.setVisibility(View.GONE);
                }catch (Exception e){
                    Log.e("ERROR", e.toString());
                }
            }
            return convertView;
        }
        private int getListViewHeight(ListView list) {
            ListAdapter adapter = list.getAdapter();

            int listviewHeight = 0;

            list.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            listviewHeight = list.getMeasuredHeight() * adapter.getCount() + (adapter.getCount() * list.getDividerHeight());

            Resources r = getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, listviewHeight, r.getDisplayMetrics());

            return listviewHeight;
        }
    }
    protected class Yu521Info{
        private String sangho;
        private String hyeonjang_name;
        private Double geumaek;
        private Double halin;
        private Double japiik;
        private String jeokyo;

        Yu521Info(){
            sangho = "";
            hyeonjang_name = "";
            geumaek = 0d;
            halin = 0d;
            japiik = 0d;
            jeokyo = "";
        }
    }
}
