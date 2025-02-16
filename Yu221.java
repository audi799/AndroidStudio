package kr.gimaek.mobilegimaek.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

public class Yu221 extends Fragment {
    //어댑터에 적용할 master 데이터
    ArrayList<Yu221Info> alYu221Info;
    AdapterYu221 adapterYu221;
    //ApplicationClsss
    UserInfo userInfo;

    Yu000 mHostActivity;

    TextView tvYu221Ymd;
    Button btnYu221Search;
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
        View rootView = inflater.inflate(R.layout.fragment_yu221, container, false);

        //ApplicationClass에 대한 접근
        userInfo = (UserInfo)getActivity().getApplicationContext();

        //각 view에 대한 정의
        tvYu221Ymd = (TextView) rootView.findViewById(R.id.tvYu221Ymd);
        btnYu221Search = (Button) rootView.findViewById(R.id.btnYu221Search);
        lvMaster = (ListView)rootView.findViewById(R.id.lvYu221);

        datePickerDialog = new DatePickerDialog(getActivity(),
                onYmdDateSetListener,
                Integer.valueOf(EtcTool.getCurrentDay().substring(0, 4)),
                Integer.valueOf(EtcTool.getCurrentDay().substring(4, 6)) - 1,
                Integer.valueOf(EtcTool.getCurrentDay().substring(6, 8)));

        tvYu221Ymd.setText(EtcTool.setDate(EtcTool.getCurrentDay()));

        //리스트뷰와 연결될 실제 자료
        alYu221Info = new ArrayList<Yu221Info>();
        //마스터 어댑터 생성
        adapterYu221 = new AdapterYu221(getActivity(),R.layout.layout_listview_master,alYu221Info);

        //자료조회
        process_data_receive();

        tvYu221Ymd.setOnClickListener(onYmdClickListener);
        btnYu221Search.setOnClickListener(new View.OnClickListener() {
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
        mHostActivity.setTitle("출하현황조회");
    }

    protected void process_data_receive(){
        String sql;
        String lymd, lcompanycode, ldbname;
        lymd = tvYu221Ymd.getText().toString();
        lcompanycode =userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode.trim();
        ldbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName;

        sql = "Yu221 '" +  lcompanycode + "' ,'" + lymd + "' ";
        Log.i("TEST",sql);

        String strURL = UserInfo.BACKUP_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(lcompanycode) + "&"
                + "S=" + Base64Tool.Base64Encode(ldbname);
        strURL = strURL.replace("\n","");

        ConnectByAsyncTask conDS = new ConnectByAsyncTask();
        conDS.execute(strURL);
        try {
            setYu221Info(new JSONArray(conDS.get()));
            //마스터리스트뷰에 대한 정보갱신
            lvMaster.setAdapter(adapterYu221);
            Toast.makeText(getActivity(), "정보가 갱신 되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ERROR", "Yu221.process_data_receive:"+e.toString());
        }
    }

    View.OnClickListener onYmdClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) { datePickerDialog.show(); }
    };

    DatePickerDialog.OnDateSetListener onYmdDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tvYu221Ymd.setText(String.valueOf(year) + "-" + EtcTool.addZero(monthOfYear + 1) + "-" + EtcTool.addZero(dayOfMonth));
            process_data_receive();
        }
    };

    protected void setYu221Info(JSONArray jsonArray){
        alYu221Info.clear();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Yu221Info tmpInfo = new Yu221Info();
                    //마스터 로우
                    tmpInfo.hap_gubun = EtcTool.isnullInt(jsonObject, "hap_gubun");
                    tmpInfo.nno = EtcTool.isnullStr(jsonObject, "nno");
                    tmpInfo.cust_name = EtcTool.isnullStr(jsonObject,"cust_name");
                    tmpInfo.hyeonjang_name = EtcTool.isnullStr(jsonObject,"hyeonjang_name");
                    tmpInfo.jepum_name = EtcTool.isnullStr(jsonObject, "jepum_name");
                    tmpInfo.plan_suryang = EtcTool.isnullFloat(jsonObject, "plan_suryang");
                    tmpInfo.chulha = EtcTool.isnullFloat(jsonObject, "chulha");
                    tmpInfo.chulha_cnt = EtcTool.isnullInt(jsonObject, "chulha_cnt");
                    tmpInfo.hyeonjang_tel = EtcTool.isnullStr(jsonObject, "hyeonjang_tel");
                    alYu221Info.add(tmpInfo);
            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            } catch (Exception e){
                Log.e("ERROR",e.toString());
            }
        }
    }

    protected class AdapterYu221 extends ArrayAdapter<Yu221Info> {
        private Context mContext;
        private int mResource;
        private ArrayList<Yu221Info> caAlYu221Info;//CustAdapterArrayListYu221Info
        private LayoutInflater mLayoutInflater;

        public AdapterYu221(Context context, int resource, ArrayList<Yu221Info> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.caAlYu221Info = objects;
            this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Yu221Info caTmpYu221Info = caAlYu221Info.get(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(caTmpYu221Info != null){
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

                    if (caTmpYu221Info.hap_gubun == 2) {
                        tvListviewMasterTitle.setText("");
                    } else if (caTmpYu221Info.hap_gubun == 1) {
                        tvListviewMasterTitle.setText("예정번호:" + caTmpYu221Info.nno);
                    }

                    tvListviewMasterSubTitle_1.setText(caTmpYu221Info.cust_name);

                    if (caTmpYu221Info.hap_gubun == 2) {
                        tvListviewMasterSubTitle_2.setTextColor((Color.parseColor("#0100FF")));  //글씨파란색
                    } else if (caTmpYu221Info.hap_gubun == 1) {
                        tvListviewMasterSubTitle_2.setTextColor((Color.parseColor("#000000")));  //글씨검은색
                    }
                    tvListviewMasterSubTitle_2.setText(caTmpYu221Info.hyeonjang_name);

                    if (caTmpYu221Info.hap_gubun == 2) {
                        tvListviewMasterContentLine_1_Left.setTextColor((Color.parseColor("#0100FF")));
                    } else if (caTmpYu221Info.hap_gubun == 1) {
                        tvListviewMasterContentLine_1_Left.setTextColor((Color.parseColor("#000000")));
                    }
                    tvListviewMasterContentLine_1_Left.setText(caTmpYu221Info.jepum_name);
                    tvListviewMasterContentLine_1_Center.setVisibility(View.GONE);
                    tvListviewMasterContentLine_1_Right.setText(caTmpYu221Info.hyeonjang_tel);

                    if (caTmpYu221Info.hap_gubun == 2) {
                        tvListviewMasterContentLine_2_Left.setTextColor((Color.parseColor("#0100FF")));
                        tvListviewMasterContentLine_2_Center.setTextColor((Color.parseColor("#0100FF")));
                        tvListviewMasterContentLine_2_Right.setTextColor((Color.parseColor("#0100FF")));
                    } else if (caTmpYu221Info.hap_gubun == 1) {
                        tvListviewMasterContentLine_2_Left.setTextColor((Color.parseColor("#000000")));
                        tvListviewMasterContentLine_2_Center.setTextColor((Color.parseColor("#000000")));
                        tvListviewMasterContentLine_2_Right.setTextColor((Color.parseColor("#000000")));
                    }
                    tvListviewMasterContentLine_2_Left.setText("예정량:" + EtcTool.formatFloat(caTmpYu221Info.plan_suryang, "#,###.###") + "㎥");
                    tvListviewMasterContentLine_2_Center.setText("출하량:" + EtcTool.formatFloat(caTmpYu221Info.chulha, "#,###.###") + "㎥");
                    tvListviewMasterContentLine_2_Right.setText("횟수:" + caTmpYu221Info.chulha_cnt);

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
    protected class Yu221Info{
        private Integer hap_gubun;
        private String nno;
        private String cust_name;
        private String hyeonjang_name;
        private String jepum_name;
        private Float plan_suryang;
        private Float chulha;
        private Integer chulha_cnt;
        private String hyeonjang_tel;

        Yu221Info(){
            hap_gubun = 0;
            nno = "";
            cust_name = "";
            hyeonjang_name = "";
            jepum_name = "";
            plan_suryang = 0f;
            chulha = 0f;
            chulha_cnt = 0;
            hyeonjang_tel = "";
        }
    }
}
