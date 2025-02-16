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
import kr.gimaek.mobilegimaek.tool.ShHyeonjangInfo;
import kr.gimaek.mobilegimaek.tool.ShCustInfo;

/**
 * Created by Administrator on 2016-06-29.
 */
public class Yu432 extends Fragment {
    Yu000 mHostActivity;
    UserInfo userInfo;
    ArrayList<Yu432Info> alYu432Info;
    AdapterYu432 adapterYu432;
    ListView lvMaster;
    Button btnYu432Search;
    TextView tvYu432Sangho, tvYu432HyeonjangName, tvYu432Ymd_1, tvYu432Ymd_2;

    String strCustCode, strHyeonjangCode;

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
        View rootView = inflater.inflate(R.layout.fragment_yu432, container, false);

        tvYu432Ymd_1 = (TextView) rootView.findViewById(R.id.tvYu432Ymd_1);
        tvYu432Ymd_2 = (TextView) rootView.findViewById(R.id.tvYu432Ymd_2);
        tvYu432Ymd_1.setText(EtcTool.setDate(EtcTool.getIncMonth(-6)));
        tvYu432Ymd_2.setText(EtcTool.setDate(EtcTool.getCurrentDay()));

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

        tvYu432Ymd_1.setOnClickListener(onYmd_1ClickListener);
        tvYu432Ymd_2.setOnClickListener(onYmd_2ClickListener);

        tvYu432Sangho = (TextView) rootView.findViewById(R.id.tvYu432Sangho);
        tvYu432HyeonjangName = (TextView) rootView.findViewById(R.id.tvYu432HyeonjangName);

        tvYu432Sangho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShCustInfo.class);
                startActivityForResult(intent, 1);
            }
        });
        tvYu432HyeonjangName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strCustCode != "" & strCustCode != null) {  //--거래처 선택 하지않고 현장선택 불가능 하도록
                    Intent intent = new Intent(getActivity(), ShHyeonjangInfo.class);
                    intent.putExtra("custCode", strCustCode);  //---현장서치에서 거래처코드를 알아야하기때문에..
                    startActivityForResult(intent, 2);
                } else {
                    Toast.makeText(getActivity(), "거래처를 선택 후 진행해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnYu432Search = (Button) rootView.findViewById(R.id.btnYu432Search);
        btnYu432Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_data_receive();
            }
        });

                lvMaster = (ListView) rootView.findViewById(R.id.lvYu432);
        alYu432Info = new ArrayList<Yu432Info>();
        adapterYu432 = new AdapterYu432(getActivity(), R.layout.layout_listview_master, alYu432Info);

        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach( activity );
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("현장별 판매원장");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 & requestCode == 1) { //resultCode가 -1 일때가 정상처리임. RESULT_OK의 반환값은 -1임.
            TextView tvYu432Sangho = (TextView) getActivity().findViewById(R.id.tvYu432Sangho);
            tvYu432Sangho.setText(data.getStringExtra("getSangho"));
            strCustCode = data.getStringExtra("getCustCode"); //거래처코드 변수에 저장.
        }else if (resultCode == -1 & requestCode == 2) {
            TextView tvYu432Hyeonjango = (TextView) getActivity().findViewById(R.id.tvYu432HyeonjangName);
            tvYu432Hyeonjango.setText(data.getStringExtra("getHyeonjangName"));
            strHyeonjangCode = data.getStringExtra("getHyeonjangCode");
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
            tvYu432Ymd_1.setText(String.valueOf(year) + "-" + EtcTool.addZero(monthOfYear + 1) + "-" + EtcTool.addZero(dayOfMonth));
        }
    };

    DatePickerDialog.OnDateSetListener onYmd_2DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tvYu432Ymd_2.setText(String.valueOf(year) + "-" + EtcTool.addZero(monthOfYear + 1) + "-" + EtcTool.addZero(dayOfMonth));
        }
    };

    protected void setYu432Info(JSONArray jsonArray){
        alYu432Info.clear();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Yu432Info tmpInfo = new Yu432Info();
                tmpInfo.ymd = EtcTool.isnullStr(jsonObject, "ymd");
                tmpInfo.jepum_name = EtcTool.isnullStr(jsonObject, "jepum_name");
                tmpInfo.danga = EtcTool.isnullDouble(jsonObject, "hyeonjang_name");
                tmpInfo.suryang = EtcTool.isnullFloat(jsonObject, "suryang");
                tmpInfo.hapgye_geumaek = EtcTool.isnullDouble(jsonObject, "hapgye_geumaek");
                tmpInfo.ipgeum = EtcTool.isnullDouble(jsonObject,"ipgeum");
                tmpInfo.ipgeum_gubun_name = EtcTool.isnullStr(jsonObject, "ipgeum_gubun_name");
                alYu432Info.add(tmpInfo);
            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            } catch (Exception e){
                Log.e("ERROR",e.toString());
            }
        }
    }

    protected void process_data_receive() {
        String sql;
        String lstartymd, lymd1, lymd2, lcustcode, lhyeonjangcode, lcompanycode, ldbname;
        lstartymd = "2012-01-01";
        lymd1 = tvYu432Ymd_1.getText().toString();
        lymd2 = tvYu432Ymd_2.getText().toString();
        lcustcode = strCustCode;
        lhyeonjangcode = strHyeonjangCode;
        lcompanycode = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode;
        ldbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName;

        sql = "Yu432 '" + lstartymd + "', '" + lymd1 + "', '" + lymd2 + "', '" + lcustcode + "', '" + lhyeonjangcode + "' ";
        Log.i("TEST",sql);

        String strURL = UserInfo.BACKUP_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(lcompanycode) + "&"
                + "S=" + Base64Tool.Base64Encode(ldbname);
        strURL = strURL.replace("\n","");

        ConnectByAsyncTask conDS = new ConnectByAsyncTask();
        conDS.execute(strURL);
        try {
            setYu432Info(new JSONArray(conDS.get()));
            //마스터리스트뷰에 대한 정보갱신
            lvMaster.setAdapter(adapterYu432);
            Toast.makeText(getActivity(), "정보가 갱신 되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ERROR", "Yu432.process_data_receive:"+e.toString());
        }
    }

    protected class AdapterYu432 extends ArrayAdapter<Yu432Info> {
        private Context mContext;
        private int mResource;
        private ArrayList<Yu432Info> caAlYu432Info;//CustAdapterArrayListYu432Info
        private LayoutInflater mLayoutInflater;

        public AdapterYu432(Context context, int resource, ArrayList<Yu432Info> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.caAlYu432Info = objects;
            this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Yu432Info caTmpYu432Info = caAlYu432Info.get(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(caTmpYu432Info != null){
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

                    tvListviewMasterTitle.setText(caTmpYu432Info.ymd);
                    tvListviewMasterSubTitle_1.setText(caTmpYu432Info.jepum_name);
                    tvListviewMasterSubTitle_2.setVisibility(View.GONE);

                    tvListviewMasterContentLine_1_Left.setText("단가:" + EtcTool.formatDouble(caTmpYu432Info.danga, "#,##0"));
                    tvListviewMasterContentLine_1_Center.setVisibility(View.GONE);
                    tvListviewMasterContentLine_1_Right.setText(EtcTool.formatFloat(caTmpYu432Info.suryang, "#,###.###") + "㎥");

                    tvListviewMasterContentLine_2_Left.setText("판매:" + EtcTool.formatDouble(caTmpYu432Info.hapgye_geumaek, "#,##0"));
                    tvListviewMasterContentLine_2_Center.setText("입금:" + EtcTool.formatDouble(caTmpYu432Info.ipgeum, "#,##0"));
                    tvListviewMasterContentLine_2_Right.setText(caTmpYu432Info.ipgeum_gubun_name.toString());

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

    protected class Yu432Info {
        private String ymd;
        private String jepum_name;
        private Double danga;
        private Float suryang;
        private Double hapgye_geumaek;
        private Double ipgeum;
        private String ipgeum_gubun_name;

        Yu432Info() {
            ymd = "";
            jepum_name = "";
            danga = 0d;
            suryang = 0f;
            hapgye_geumaek = 0d;
            ipgeum = 0d;
            ipgeum_gubun_name = "";
        }
    }
}

