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
public class Yu541 extends Fragment {
    Yu000 mHostActivity;
    UserInfo userInfo;
    ArrayList<Yu541Info> alYu541Info;
    AdapterYu541 adapterYu541;
    ListView lvMaster;
    Button btnYu541Search;
    TextView tvYu541Sangho;

    String strCustCode;

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
        View rootView = inflater.inflate(R.layout.fragment_yu541, container, false);

        btnYu541Search = (Button) rootView.findViewById(R.id.btnYu541Search);

        btnYu541Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_data_receive();
            }
        });

        tvYu541Sangho = (TextView) rootView.findViewById(R.id.tvYu541Sangho);
        tvYu541Sangho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShCustInfo.class);
                startActivityForResult(intent, 1);
            }
        });

        lvMaster = (ListView) rootView.findViewById(R.id.lvYu541);
        alYu541Info = new ArrayList<Yu541Info>();
        adapterYu541 = new AdapterYu541(getActivity(), R.layout.layout_listview_master, alYu541Info);

        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach( activity );
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("거래처별 미수금현황");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 & requestCode == 1) { //resultCode가 -1 일때가 정상처리임. RESULT_OK의 반환값은 -1임.
            TextView tvYu541Sangho = (TextView)getActivity().findViewById(R.id.tvYu541Sangho);
            tvYu541Sangho.setText(data.getStringExtra("getSangho"));
            strCustCode = data.getStringExtra("getCustCode"); //거래처코드 변수에 저장.
        }
    }

    protected void setYu541Info(JSONArray jsonArray){
        alYu541Info.clear();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Yu541Info tmpInfo = new Yu541Info();
                tmpInfo.sangho = EtcTool.isnullStr(jsonObject, "sangho");
                tmpInfo.sisan = EtcTool.isnullDouble(jsonObject, "sisan");
                tmpInfo.panmae = EtcTool.isnullDouble(jsonObject, "panmae");
                tmpInfo.ipgeum = EtcTool.isnullDouble(jsonObject, "ipgeum");
                tmpInfo.janaek = EtcTool.isnullDouble(jsonObject, "janaek");
                alYu541Info.add(tmpInfo);
            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            } catch (Exception e){
                Log.e("ERROR",e.toString());
            }
        }
    }

    protected void process_data_receive() {
        String sql;
        String lstartymd, lcustcode, lcompanycode, ldbname;
        lstartymd = "2012-01-01";
        lcustcode = strCustCode;
        lcompanycode = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode;
        ldbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName;

        sql = "Yu541 '" + lcustcode + "' ";
        Log.i("TEST",sql);

        String strURL = UserInfo.BACKUP_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(lcompanycode) + "&"
                + "S=" + Base64Tool.Base64Encode(ldbname);
        strURL = strURL.replace("\n","");

        ConnectByAsyncTask conDS = new ConnectByAsyncTask();
        conDS.execute(strURL);
        try {
            setYu541Info(new JSONArray(conDS.get()));
            //마스터리스트뷰에 대한 정보갱신
            lvMaster.setAdapter(adapterYu541);
            Toast.makeText(getActivity(), "정보가 갱신 되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ERROR", "Yu541.process_data_receive:"+e.toString());
        }
    }
//-- 50533

    protected class AdapterYu541 extends ArrayAdapter<Yu541Info> {
        private Context mContext;
        private int mResource;
        private ArrayList<Yu541Info> caAlYu541Info;//CustAdapterArrayListYu541Info
        private LayoutInflater mLayoutInflater;

        public AdapterYu541(Context context, int resource, ArrayList<Yu541Info> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.caAlYu541Info = objects;
            this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Yu541Info caTmpYu541Info = caAlYu541Info.get(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(caTmpYu541Info != null){
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

                    tvListviewMasterTitle.setText("작년미수금:" + EtcTool.formatDouble(caTmpYu541Info.sisan, "#,##0"));
                    tvListviewMasterSubTitle_1.setVisibility(View.GONE);
                    tvListviewMasterSubTitle_2.setVisibility(View.GONE);

                    tvListviewMasterContentLine_1_Left.setText("올해판매액");
                    tvListviewMasterContentLine_1_Center.setText("올해수금액");
                    tvListviewMasterContentLine_1_Right.setText("현재미수금");

                    tvListviewMasterContentLine_2_Left.setText(EtcTool.formatDouble(caTmpYu541Info.panmae, "#,##0"));
                    tvListviewMasterContentLine_2_Center.setText(EtcTool.formatDouble(caTmpYu541Info.ipgeum, "#,##0"));
                    tvListviewMasterContentLine_2_Right.setText(EtcTool.formatDouble(caTmpYu541Info.janaek, "#,##0"));

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

    protected class Yu541Info {
        private String sangho;
        private Double sisan;
        private Double panmae;
        private Double ipgeum;
        private Double janaek;

        Yu541Info() {
            sangho = "";
            sisan = 0d;
            panmae = 0d;
            ipgeum = 0d;
            janaek = 0d;
        }
    }
}

