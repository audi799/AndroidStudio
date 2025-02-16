package kr.gimaek.mobilegimaek.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
public class Yu431 extends Fragment {
    Yu000 mHostActivity;
    UserInfo userInfo;
    ArrayList<Yu431Info> alYu431Info;
    AdapterYu431 adapterYu431;
    ListView lvMaster;
    Button btnYu431Search;
    TextView tvYu431Sangho, tvYu431Ymd_1, tvYu431Ymd_2;

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
        View rootView = inflater.inflate(R.layout.fragment_yu431, container, false);

        tvYu431Ymd_1 = (TextView) rootView.findViewById(R.id.tvYu431Ymd_1);
        tvYu431Ymd_2 = (TextView) rootView.findViewById(R.id.tvYu431Ymd_2);
        tvYu431Ymd_1.setText(EtcTool.setDate(EtcTool.getIncMonth(-6)));
        tvYu431Ymd_2.setText(EtcTool.setDate(EtcTool.getCurrentDay()));
        tvYu431Ymd_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog_1.show();
            }
        });

        tvYu431Ymd_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog_2.show();
            }
        });

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

        btnYu431Search = (Button) rootView.findViewById(R.id.btnYu431Search);
        btnYu431Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_data_receive();
            }
        });

        tvYu431Sangho = (TextView) rootView.findViewById(R.id.tvYu431Sangho);
        tvYu431Sangho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShCustInfo.class);
                startActivityForResult(intent, 1);
            }
        });

        lvMaster = (ListView) rootView.findViewById(R.id.lvYu431);
        alYu431Info = new ArrayList<Yu431Info>();
        adapterYu431 = new AdapterYu431(getActivity(), R.layout.layout_listview_master, alYu431Info);

        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach( activity );
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("거래처별 판매원장");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 & requestCode == 1) { //resultCode가 -1 일때가 정상처리임. RESULT_OK의 반환값은 -1임.
            TextView tvYu431Sangho = (TextView)getActivity().findViewById(R.id.tvYu431Sangho);
            tvYu431Sangho.setText(data.getStringExtra("getSangho"));
            strCustCode = data.getStringExtra("getCustCode"); //거래처코드 변수에 저장.
        }
    }

    DatePickerDialog.OnDateSetListener onYmd_1DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tvYu431Ymd_1.setText(String.valueOf(year) + "-" + EtcTool.addZero(monthOfYear + 1) + "-" + EtcTool.addZero(dayOfMonth));
        }
    };

    DatePickerDialog.OnDateSetListener onYmd_2DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tvYu431Ymd_2.setText(String.valueOf(year) + "-" + EtcTool.addZero(monthOfYear + 1) + "-" + EtcTool.addZero(dayOfMonth));
        }
    };

    protected void setYu431Info(JSONArray jsonArray){
        alYu431Info.clear();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Yu431Info tmpInfo = new Yu431Info();
                tmpInfo.ymd = EtcTool.isnullStr(jsonObject, "ymd");
                tmpInfo.hyeonjang_name = EtcTool.isnullStr(jsonObject,"hyeonjang_name");
                tmpInfo.jepum_name = EtcTool.isnullStr(jsonObject, "jepum_name");
                tmpInfo.suryang = EtcTool.isnullFloat(jsonObject, "suryang");
                tmpInfo.hapgye_geumaek = EtcTool.isnullDouble(jsonObject, "hapgye_geumaek");
                tmpInfo.ipgeum = EtcTool.isnullDouble(jsonObject,"ipgeum");
                tmpInfo.misu = EtcTool.isnullDouble(jsonObject, "misu");
                alYu431Info.add(tmpInfo);
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
        lstartymd = "2012-01-01";
        lymd1 = tvYu431Ymd_1.getText().toString();
        lymd2 = tvYu431Ymd_2.getText().toString();
        lcustcode = strCustCode;
        lcompanycode = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode;
        ldbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName;

        sql = "Yu431 '" + lstartymd + "', '" + lymd1 + "', '" + lymd2 + "', '" + lcustcode + "' ";
        Log.i("TEST",sql);

        String strURL = UserInfo.BACKUP_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(lcompanycode) + "&"
                + "S=" + Base64Tool.Base64Encode(ldbname);
        strURL = strURL.replace("\n","");

        ConnectByAsyncTask conDS = new ConnectByAsyncTask();
        conDS.execute(strURL);
        try {
            setYu431Info(new JSONArray(conDS.get()));
            //마스터리스트뷰에 대한 정보갱신
            lvMaster.setAdapter(adapterYu431);
            Toast.makeText(getActivity(), "정보가 갱신 되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ERROR", "Yu431.process_data_receive:"+e.toString());
        }
    }
//-- 50533

    protected class AdapterYu431 extends ArrayAdapter<Yu431Info> {
        private Context mContext;
        private int mResource;
        private ArrayList<Yu431Info> caAlYu431Info;//CustAdapterArrayListYu431Info
        private LayoutInflater mLayoutInflater;

        public AdapterYu431(Context context, int resource, ArrayList<Yu431Info> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.caAlYu431Info = objects;
            this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Yu431Info caTmpYu431Info = caAlYu431Info.get(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(caTmpYu431Info != null){
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

                    tvListviewMasterTitle.setText(caTmpYu431Info.ymd);
                    tvListviewMasterSubTitle_1.setText(caTmpYu431Info.hyeonjang_name);
                    tvListviewMasterSubTitle_2.setText(caTmpYu431Info.jepum_name);

                    tvListviewMasterContentLine_1_Left.setText(EtcTool.formatFloat(caTmpYu431Info.suryang, "#,###.###") + "㎥");
                    tvListviewMasterContentLine_1_Center.setVisibility(View.GONE);
                    tvListviewMasterContentLine_1_Right.setVisibility(View.GONE);

                    tvListviewMasterContentLine_2_Left.setText("판매:" + EtcTool.formatDouble(caTmpYu431Info.hapgye_geumaek, "#,##0"));
                    tvListviewMasterContentLine_2_Center.setText("입금:" + EtcTool.formatDouble(caTmpYu431Info.ipgeum, "#,##0"));
                    tvListviewMasterContentLine_2_Right.setText("미수:" + EtcTool.formatDouble(caTmpYu431Info.misu, "#,##0"));

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

    protected class Yu431Info {
        private String ymd;
        private String hyeonjang_name;
        private String jepum_name;
        private Float suryang;
        private Double hapgye_geumaek;
        private Double ipgeum;
        private Double misu;

        Yu431Info() {
            ymd = "";
            hyeonjang_name = "";
            jepum_name = "";
            suryang = 0f;
            hapgye_geumaek = 0d;
            ipgeum = 0d;
            misu = 0d;
        }
    }
}

