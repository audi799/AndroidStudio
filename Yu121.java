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

public class Yu121 extends Fragment {
    //어댑터에 적용할 master 데이터
    ArrayList<Yu121Info> alYu121Info;
    AdapterYu121 adapterYu121;
    //ApplicationClsss
    UserInfo userInfo;

    Yu000 mHostActivity;

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
        View rootView = inflater.inflate(R.layout.fragment_yu121, container, false);

        //ApplicationClass에 대한 접근
        userInfo = (UserInfo)getActivity().getApplicationContext();

        //각 view에 대한 정의
        lvMaster = (ListView)rootView.findViewById(R.id.lvYu121);

        //리스트뷰와 연결될 실제 자료
        alYu121Info = new ArrayList<Yu121Info>();
        //마스터 어댑터 생성
        adapterYu121 = new AdapterYu121(getActivity(),R.layout.layout_listview_master,alYu121Info);

        //자료조회
        process_data_receive();

        return rootView;
    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach( activity );
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("관급주문대비판매현황");
    }

    protected void process_data_receive(){
        String sql;
        String lymd, lcompanycode, ldbname;
        lcompanycode =userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode;
        ldbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName;

        sql = "Yu121 ";
        Log.i("TEST",sql);

        String strURL = UserInfo.BACKUP_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(lcompanycode) + "&"
                + "S=" + Base64Tool.Base64Encode(ldbname);
        strURL = strURL.replace("\n","");

        ConnectByAsyncTask conDS = new ConnectByAsyncTask();
        conDS.execute(strURL);
        try {
            setYu121Info(new JSONArray(conDS.get()));
            //마스터리스트뷰에 대한 정보갱신
            lvMaster.setAdapter(adapterYu121);
            Toast.makeText(getActivity(), "정보가 갱신 되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ERROR", "Yu121.process_data_receive:"+e.toString());
        }
    }

    protected void setYu121Info(JSONArray jsonArray){
        alYu121Info.clear();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Yu121Info tmpInfo = new Yu121Info();
                    //마스터 로우
                    tmpInfo.jisi_gubun = EtcTool.isnullStr(jsonObject, "jisi_gubun");
                    tmpInfo.jisi_beonho= EtcTool.isnullStr(jsonObject,"jisi_beonho");
                    tmpInfo.sangho = EtcTool.isnullStr(jsonObject, "sangho");
                    tmpInfo.hyeonjang_name = EtcTool.isnullStr(jsonObject, "hyeonjang_name");
                    tmpInfo.baejeong_suryang = EtcTool.isnullFloat(jsonObject, "baejeong_suryang");
                    tmpInfo.panmae_suryang = EtcTool.isnullFloat(jsonObject, "panmae_suryang");
                    alYu121Info.add(tmpInfo);
            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            } catch (Exception e){
                Log.e("ERROR",e.toString());
            }
        }
    }

    protected class AdapterYu121 extends ArrayAdapter<Yu121Info> {
        private Context mContext;
        private int mResource;
        private ArrayList<Yu121Info> caAlYu121Info;//CustAdapterArrayListYu121Info
        private LayoutInflater mLayoutInflater;

        public AdapterYu121(Context context, int resource, ArrayList<Yu121Info> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;
            this.caAlYu121Info = objects;
            this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Yu121Info caTmpYu121Info = caAlYu121Info.get(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(caTmpYu121Info != null){
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

                    tvListviewMasterTitle.setText(caTmpYu121Info.jisi_gubun + "-" + caTmpYu121Info.jisi_beonho);
                    tvListviewMasterSubTitle_1.setText(caTmpYu121Info.sangho);
                    tvListviewMasterSubTitle_2.setText(caTmpYu121Info.hyeonjang_name);

                    tvListviewMasterContentLine_1_Left.setText("배정수량 : " + EtcTool.formatFloat(caTmpYu121Info.baejeong_suryang, "#,###.###") + "㎥");
                    tvListviewMasterContentLine_1_Center.setVisibility(View.GONE);
                    tvListviewMasterContentLine_1_Right.setText("판매수량 : " + EtcTool.formatFloat(caTmpYu121Info.panmae_suryang, "#,###.###") + "㎥");

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
    protected class Yu121Info{
        private String jisi_gubun;
        private String jisi_beonho;
        private String sangho;
        private String hyeonjang_name;
        private Float baejeong_suryang;
        private Float panmae_suryang;

        Yu121Info(){
            jisi_gubun = "";
            jisi_beonho = "";
            sangho = "";
            hyeonjang_name = "";
            baejeong_suryang = 0f;
            panmae_suryang = 0f;
        }
    }
}
