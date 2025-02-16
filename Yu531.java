package kr.gimaek.mobilegimaek.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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
import kr.gimaek.mobilegimaek.tool.ShCustInfo;

/**
 * Created by Administrator on 2016-06-29.
 */
public class Yu531 extends Fragment {
    Yu000 mHostActivity;
    UserInfo userInfo;
    ArrayList<Yu531Info> alYu531Info;
    AdapterYu531 adapterYu531;
    ListView lvMaster;
    Button btnYu531Search;
    TextView tvYu531Sangho, tvYu531Ymd_1, tvYu531Ymd_2;

    String strCustCode;

    DatePickerDialog datePickerDialog_1;
    DatePickerDialog datePickerDialog_2;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_yu531, container, false);

        tvYu531Ymd_1 = (TextView) rootView.findViewById(R.id.tvYu531Ymd_1);
        tvYu531Ymd_2 = (TextView) rootView.findViewById(R.id.tvYu531Ymd_2);
        tvYu531Ymd_1.setText(EtcTool.setDate(EtcTool.getIncMonth(-6)));
        tvYu531Ymd_2.setText(EtcTool.setDate(EtcTool.getCurrentDay()));

        datePickerDialog_1 = new DatePickerDialog(getActivity(),
                onYmd_1DateSetListener,
                Integer.valueOf(EtcTool.getCurrentDay().substring(0, 4)),
                Integer.valueOf(EtcTool.getIncMonth(-6).substring(4, 6)) - 1,
                Integer.valueOf(EtcTool.getCurrentDay().substring(6, 8)));

        datePickerDialog_2 = new DatePickerDialog(getActivity(),
                onYmd_2DateSetListener,
                Integer.valueOf(EtcTool.getCurrentDay().substring(0, 4)),
                Integer.valueOf(EtcTool.getCurrentDay().substring(4, 6)) - 1,
                Integer.valueOf(EtcTool.getCurrentDay().substring(6, 8)));

        tvYu531Ymd_1.setOnClickListener(onYmd_1ClickListener);
        tvYu531Ymd_2.setOnClickListener(onYmd_2ClickListener);

        btnYu531Search = (Button) rootView.findViewById(R.id.btnYu531Search);

        btnYu531Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_data_receive();
            }
        });

        tvYu531Sangho = (TextView) rootView.findViewById(R.id.tvYu531Sangho);
        tvYu531Sangho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShCustInfo.class);
                startActivityForResult(intent, 1);
            }
        });

        lvMaster = (ListView) rootView.findViewById(R.id.lvYu531);
        alYu531Info = new ArrayList<Yu531Info>();
        adapterYu531 = new AdapterYu531(getActivity(), R.layout.layout_listview_master, alYu531Info);

        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach( activity );
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("거래처별 수금현황");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 & requestCode == 1) { //resultCode가 -1 일때가 정상처리임. RESULT_OK의 반환값은 -1임.
            TextView tvYu531Sangho = (TextView)getActivity().findViewById(R.id.tvYu531Sangho);
            tvYu531Sangho.setText(data.getStringExtra("getSangho"));
            strCustCode = data.getStringExtra("getCustCode"); //거래처코드 변수에 저장.
        }
    }

    View.OnClickListener onYmd_1ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) { datePickerDialog_1.show(); }
    };

    View.OnClickListener onYmd_2ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) { datePickerDialog_2.show(); }
    };

    DatePickerDialog.OnDateSetListener onYmd_1DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tvYu531Ymd_1.setText(String.valueOf(year) + "-" + EtcTool.addZero(monthOfYear + 1) + "-" + EtcTool.addZero(dayOfMonth));
        }
    };

    DatePickerDialog.OnDateSetListener onYmd_2DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tvYu531Ymd_2.setText(String.valueOf(year) + "-" + EtcTool.addZero(monthOfYear + 1) + "-" + EtcTool.addZero(dayOfMonth));
        }
    };

    protected void setYu531Info(JSONArray jsonArray){
        alYu531Info.clear();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Yu531Info tmpInfo = new Yu531Info();
                tmpInfo.gubun = EtcTool.isnullInt(jsonObject, "gubun");
                tmpInfo.ymd = EtcTool.isnullStr(jsonObject, "ymd");
                tmpInfo.ipgeum_name = EtcTool.isnullStr(jsonObject, "ipgeum_name");
                tmpInfo.geumaek = EtcTool.isnullDouble(jsonObject, "geumaek");
                tmpInfo.halin = EtcTool.isnullDouble(jsonObject, "halin");
                tmpInfo.japiik = EtcTool.isnullDouble(jsonObject, "japiik");
                alYu531Info.add(tmpInfo);
            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            } catch (Exception e){
                Log.e("ERROR",e.toString());
            }
        }
    }

    protected void process_data_receive() {
        String sql;
        String lstartymd, lymd1, lymd2, lcustcode, lcompanycode, ldbname;
        lymd1 = tvYu531Ymd_1.getText().toString();
        lymd2 = tvYu531Ymd_2.getText().toString();
        lcustcode = strCustCode;
        lcompanycode = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode;
        ldbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName;

        sql = "Yu531 '" + lymd1 + "', '" + lymd2 + "', '" + lcustcode + "' ";
        Log.i("TEST",sql);

        String strURL = UserInfo.BACKUP_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(lcompanycode) + "&"
                + "S=" + Base64Tool.Base64Encode(ldbname);
        strURL = strURL.replace("\n","");

        ConnectByAsyncTask conDS = new ConnectByAsyncTask();
        conDS.execute(strURL);
        try {
            setYu531Info(new JSONArray(conDS.get()));
            //마스터리스트뷰에 대한 정보갱신
            lvMaster.setAdapter(adapterYu531);
            Toast.makeText(getActivity(), "정보가 갱신 되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ERROR", "Yu531.process_data_receive:"+e.toString());
        }
    }
//-- 50533

    protected class AdapterYu531 extends ArrayAdapter<Yu531Info> {
        private Context mContext;
        private int mResource;
        private ArrayList<Yu531Info> caAlYu531Info;//CustAdapterArrayListYu531Info
        private LayoutInflater mLayoutInflater;

        public AdapterYu531(Context context, int resource, ArrayList<Yu531Info> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.caAlYu531Info = objects;
            this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Yu531Info caTmpYu531Info = caAlYu531Info.get(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(caTmpYu531Info != null){
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

                    tvListviewMasterTitle.setText(caTmpYu531Info.ymd);
                    tvListviewMasterSubTitle_1.setText(caTmpYu531Info.ipgeum_name);
                    tvListviewMasterSubTitle_2.setVisibility(View.GONE);

                    if (caTmpYu531Info.gubun == 2) {  //--- 총계부분
                        tvListviewMasterContentLine_1_Left.setTextColor(Color.parseColor("#0100FF"));
                        tvListviewMasterContentLine_1_Center.setTextColor(Color.parseColor("#0100FF"));
                        tvListviewMasterContentLine_1_Right.setTextColor(Color.parseColor("#0100FF"));
                        tvListviewMasterContentLine_1_Left.setText("총입금액:" + EtcTool.formatDouble(caTmpYu531Info.geumaek, "#,##0"));
                        tvListviewMasterContentLine_1_Center.setText("총할인:" + EtcTool.formatDouble(caTmpYu531Info.halin, "#,##0"));
                        tvListviewMasterContentLine_1_Right.setText("총잡이익:" + EtcTool.formatDouble(caTmpYu531Info.japiik, "#,##0"));

                    } else if (caTmpYu531Info.gubun == 1) {
                        tvListviewMasterContentLine_1_Left.setTextColor(Color.parseColor("#000000"));
                        tvListviewMasterContentLine_1_Center.setTextColor(Color.parseColor("#000000"));
                        tvListviewMasterContentLine_1_Right.setTextColor(Color.parseColor("#000000"));
                        tvListviewMasterContentLine_1_Left.setText("입금액:" + EtcTool.formatDouble(caTmpYu531Info.geumaek, "#,##0"));
                        tvListviewMasterContentLine_1_Center.setText("할인:" + EtcTool.formatDouble(caTmpYu531Info.halin, "#,##0"));
                        tvListviewMasterContentLine_1_Right.setText("잡이익:" + EtcTool.formatDouble(caTmpYu531Info.japiik, "#,##0"));
                    }

                    tvListviewMasterContentLine_2_Left.setVisibility(View.GONE);
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

    protected class Yu531Info {
        private Integer gubun;   //--- 1: 디테일, 2:집계
        private String ymd;
        private String ipgeum_name;
        private Double geumaek;
        private Double halin;
        private Double japiik;

        Yu531Info() {
            gubun = 0;
            ymd = "";
            ipgeum_name = "";
            geumaek = 0d;
            halin = 0d;
            japiik = 0d;
        }
    }
}

