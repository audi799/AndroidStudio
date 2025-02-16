package kr.gimaek.mobilegimaek.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
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
import android.widget.LinearLayout;
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

public class Jh111 extends Fragment {
    //어댑터에 적용할 master 데이터
    ArrayList<Jh111Info> alJh111Info;
    AdapterJh111 adapterJh111;
    //ApplicationClsss
    UserInfo userInfo;

    Yu000 mHostActivity;

    TextView tvJh111Ymd_1;
    TextView tvJh111Ymd_2;
    Button btnJh111Search;
    DatePickerDialog datePickerDialog_1;
    DatePickerDialog datePickerDialog_2;

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

    //Detail Layout
    TextView tvListviewDetailContentLine_Left;
    TextView tvListviewDetailContentLine_Center;
    TextView tvListviewDetailContentLine_Right;

    LinearLayout llDetailListview;
    ListView lvMaster;
    ListView lvDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_jh111, container, false);

        //ApplicationClass에 대한 접근
        userInfo = (UserInfo)getActivity().getApplicationContext();

        //각 view에 대한 정의
        tvJh111Ymd_1 = (TextView) rootView.findViewById(R.id.tvJh111Ymd_1);
        tvJh111Ymd_2 = (TextView) rootView.findViewById(R.id.tvJh111Ymd_2);

        btnJh111Search = (Button) rootView.findViewById(R.id.btnJh111Search);
        btnJh111Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_data_receive();
            }
        });

        lvMaster = (ListView)rootView.findViewById(R.id.lvJh111);

        //
        tvJh111Ymd_1.setText(EtcTool.setDate(EtcTool.getIncMonth(-6)));
        tvJh111Ymd_2.setText(EtcTool.setDate(EtcTool.getCurrentDay()));

        //리스트뷰와 연결될 실제 자료
        alJh111Info = new ArrayList<Jh111Info>();
        //마스터 어댑터 생성
        adapterJh111 = new AdapterJh111(getActivity(),R.layout.layout_listview_master,alJh111Info);

        //자료조회
        process_data_receive();

        tvJh111Ymd_1.setOnClickListener(onYmd_1ClickListener);
        tvJh111Ymd_2.setOnClickListener(onYmd_2ClickListener);

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

        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach( activity );
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("배정조회");
    }

    protected void process_data_receive(){
        String sql;
        String lymd1, lymd2, ljohapcode, ljohapcustcode, ljohapdbname;
        lymd1 = tvJh111Ymd_1.getText().toString();
        lymd2 = tvJh111Ymd_2.getText().toString();
        ljohapcode =userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyJohapCode;
        ljohapcustcode = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyJohapCustCode;
        ljohapdbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyJohapDBName;

        sql = "Jh111 '" + lymd1 + "', '" + lymd2 + "', '" + ljohapcustcode + "' ";
        Log.i("TEST",sql);

        String strURL = UserInfo.BACKUP_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(ljohapcode) + "&"
                + "S=" + Base64Tool.Base64Encode(ljohapdbname);
        strURL = strURL.replace("\n","");

        ConnectByAsyncTask conDS = new ConnectByAsyncTask();
        conDS.execute(strURL);
        try {
            setJh111Info(new JSONArray(conDS.get()));
            //마스터리스트뷰에 대한 정보갱신
            lvMaster.setAdapter(adapterJh111);
            Toast.makeText(getActivity(), "정보가 갱신 되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ERROR", "Jh111.process_data_receive:"+e.toString());
        }
    }
    protected void setJh111Info(JSONArray jsonArray){
        alJh111Info.clear();
        int masterIdx=-1;
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (!jsonObject.isNull("v_tongbo_ilja")){
                    Jh111Info tmpInfo = new Jh111Info();
                    //마스터 로우
                    masterIdx++;
                    tmpInfo.tongbo_ilja = EtcTool.isnullStr(jsonObject, "v_tongbo_ilja");
                    tmpInfo.jisi_beonho = EtcTool.isnullStr(jsonObject,"jisi_beonho");
                    tmpInfo.sangho = EtcTool.isnullStr(jsonObject, "sangho");
                    tmpInfo.gongsa_name = EtcTool.isnullStr(jsonObject, "gongsa_name");
                    tmpInfo.suryang = EtcTool.isnullFloat(jsonObject, "suryang");
                    tmpInfo.geumaek = EtcTool.isnullDouble(jsonObject,"geumaek");
                    tmpInfo.baejeong_name = EtcTool.isnullStr(jsonObject,"v_baejeong_name");
                    alJh111Info.add(tmpInfo);

                }else{
                    //디테일 로우
                    Jh111InfoDetail tmpDetail = new Jh111InfoDetail();

                    tmpDetail.jepumName = EtcTool.isnullStr(jsonObject, "sangho");
                    tmpDetail.Suryang = EtcTool.isnullFloat(jsonObject, "suryang");
                    tmpDetail.geumaek = EtcTool.isnullDouble(jsonObject, "geumaek");
                    tmpDetail.napgiIlja = EtcTool.isnullStr(jsonObject, "gongsa_name");
                    alJh111Info.get(masterIdx).alJh111InfoDetail.add(tmpDetail);
                }

            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            } catch (Exception e){
                Log.e("ERROR",e.toString());
            }
        }
    }

    protected class AdapterJh111 extends ArrayAdapter<Jh111Info> {
        private Context mContext;
        private int mResource;
        private ArrayList<Jh111Info> caAlJh111Info;//CustAdapterArrayListJh111Info
        private LayoutInflater mLayoutInflater;

        public AdapterJh111(Context context, int resource, ArrayList<Jh111Info> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.caAlJh111Info = objects;
            this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Jh111Info caTmpJh111Info = caAlJh111Info.get(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(caTmpJh111Info != null){
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

                    tvListviewMasterTitle.setText(caTmpJh111Info.jisi_beonho + "(" + caTmpJh111Info.baejeong_name + ")");
                    tvListviewMasterSubTitle_1.setText(caTmpJh111Info.sangho);
                    tvListviewMasterSubTitle_2.setText(caTmpJh111Info.gongsa_name);

                    tvListviewMasterContentLine_1_Left.setText(EtcTool.formatFloat(caTmpJh111Info.suryang, "#,###.###") + "㎥");
                    tvListviewMasterContentLine_1_Center.setVisibility(View.GONE);
                    tvListviewMasterContentLine_1_Right.setText(EtcTool.formatDouble(caTmpJh111Info.geumaek, "#,###"));

                    rlListviewMasterContentLine_2.setVisibility(View.GONE);
                    tvListviewMasterContentLine_2_Left.setVisibility(View.GONE);
                    tvListviewMasterContentLine_2_Center.setVisibility(View.GONE);
                    tvListviewMasterContentLine_2_Right.setVisibility(View.GONE);

                    rlListviewMasterContentLine_3.setVisibility(View.GONE);
                    tvListviewMasterContentLine_3_Left.setVisibility(View.GONE);
                    tvListviewMasterContentLine_3_Center.setVisibility(View.GONE);
                    tvListviewMasterContentLine_3_Right.setVisibility(View.GONE);

                    lvDetail = (ListView)convertView.findViewById(R.id.lvListviewDetailListview);
                    AdapterJh111InfoDetail adapterJh111InfoDetail = new AdapterJh111InfoDetail(getActivity(),
                            R.layout.layout_listview_detail,
                            caTmpJh111Info.alJh111InfoDetail);

                    //convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,convertView.getHeight()+108*caTmpJh111Info.alJh111InfoDetail.size()));
                    lvDetail.setAdapter(adapterJh111InfoDetail);
                    llDetailListview = (LinearLayout)convertView.findViewById(R.id.llDetailListview);
                    llDetailListview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, getListViewHeight(lvDetail)));

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

    protected class AdapterJh111InfoDetail extends ArrayAdapter<Jh111InfoDetail> {
        private Context mContext;
        private int mResource;
        private ArrayList<Jh111InfoDetail> caAlJh111InfoDetail;
        private LayoutInflater mLayoutInflater;

        public AdapterJh111InfoDetail(Context context, int resource, ArrayList<Jh111InfoDetail> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.caAlJh111InfoDetail = objects;
            this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Jh111InfoDetail caTmpJh111InfoDetail = caAlJh111InfoDetail.get(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if (caTmpJh111InfoDetail != null) {
                tvListviewDetailContentLine_Left = (TextView) convertView.findViewById(R.id.tvListviewDetailContentLine_Left);
                tvListviewDetailContentLine_Center = (TextView) convertView.findViewById(R.id.tvListviewDetailContentLine_Center);
                tvListviewDetailContentLine_Right = (TextView) convertView.findViewById(R.id.tvListviewDetailContentLine_Right);

                tvListviewDetailContentLine_Left.setText(caTmpJh111InfoDetail.jepumName+"("+caTmpJh111InfoDetail.napgiIlja+")");
                tvListviewDetailContentLine_Center.setVisibility(View.GONE);
                //setText();
                tvListviewDetailContentLine_Right.setText(
                        String.format("%10s%10s",EtcTool.formatDouble(caTmpJh111InfoDetail.geumaek,"#,###"),
                        EtcTool.formatFloat(caTmpJh111InfoDetail.Suryang, "#,###.###") + "㎥"));
            }
            return convertView;
        }
    }

    DatePickerDialog.OnDateSetListener onYmd_1DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tvJh111Ymd_1.setText(String.valueOf(year) + "-" + EtcTool.addZero(monthOfYear + 1) + "-" + EtcTool.addZero(dayOfMonth));
        }
    };

    DatePickerDialog.OnDateSetListener onYmd_2DateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            tvJh111Ymd_2.setText(String.valueOf(year) + "-" + EtcTool.addZero(monthOfYear + 1) + "-" + EtcTool.addZero(dayOfMonth));
        }
    };

    View.OnClickListener onYmd_1ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) { datePickerDialog_1.show(); }
    };

    View.OnClickListener onYmd_2ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) { datePickerDialog_2.show(); }
    };


    protected class Jh111Info{
        private String tongbo_ilja;
        private String jisi_beonho;
        private String sangho;
        private String gongsa_name;
        private Float suryang;
        private Double geumaek;
        private String baejeong_name;
        private ArrayList<Jh111InfoDetail> alJh111InfoDetail;
        Jh111Info(){
            tongbo_ilja = "";
            jisi_beonho = "";
            sangho = "";
            gongsa_name = "";
            suryang = 0f;
            geumaek = 0d;
            baejeong_name = "";
            alJh111InfoDetail = new ArrayList<Jh111InfoDetail>();

        }
    }

    protected class Jh111InfoDetail{
        String jepumName;
        Float Suryang;
        Double geumaek;
        String napgiIlja;
    }
}
