package kr.gimaek.mobilegimaek.yu211;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
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

/**
 * Created by fromstog on 2016. 7. 1..
 */
public class Yu211_JumunJp extends Fragment {
    Yu000 mHostActivity;
    UserInfo userInfo;
    AdapterSearchInfo adapterSearchInfo;
    EditText etYu211_JumunJp_Filter;
    ListView lvYu211_JumunJp;
    Button btnYu211_JumunJp_Cancel, btnYu211_JumunJp_Next;

    ArrayList<Yu221_JumunJpInfo> alYu221_JumunJpInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_yu211_jumunjp,container, false);

        userInfo = (UserInfo) getActivity().getApplicationContext();

        lvYu211_JumunJp = (ListView) rootView.findViewById(R.id.lvYu211_JumunJp);
        alYu221_JumunJpInfo = new ArrayList<Yu221_JumunJpInfo>();

        process_data_receive();

        try {
            adapterSearchInfo = new AdapterSearchInfo(getActivity(), R.layout.layout_listview_simple, alYu221_JumunJpInfo);
            lvYu211_JumunJp.setAdapter(adapterSearchInfo);
            lvYu211_JumunJp.setOnItemClickListener(onJumunItemClickListener);

        }catch (Exception e){
            Log.e("ERROR", "SearchInfo.onCreate.setAdapter: " + e.toString());
        }

        etYu211_JumunJp_Filter = (EditText)rootView.findViewById(R.id.etYu211_JumunJp_Filter);
        etYu211_JumunJp_Filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterSearchInfo.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        btnYu211_JumunJp_Cancel = (Button) rootView.findViewById(R.id.btnYu211_JumunJp_Cancel);
        btnYu211_JumunJp_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.stepPrevious();
            }
        });
        btnYu211_JumunJp_Next = (Button) rootView.findViewById(R.id.btnYu211_JumunJp_Next);
        btnYu211_JumunJp_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHostActivity.appendYuChulhaPlan.jepumCode.trim().equals("")){
                    Toast.makeText(mHostActivity.getApplication(), "배정제품 선택하세요.", Toast.LENGTH_SHORT).show();
                }else {
                    mHostActivity.stepNext();
                }
            }
        });

        return rootView;

    }

    @Override
    public void onAttach( Activity activity ) {
        super.onAttach(activity);
        mHostActivity = (Yu000)activity;
        mHostActivity.setTitle("출하예정등록[배정제품]");
    }

    public class AdapterSearchInfo extends ArrayAdapter<Yu221_JumunJpInfo> {
        private Context mContext;
        private int mResource;
        private ArrayList<Yu221_JumunJpInfo> orgArrayList;
        private ArrayList<Yu221_JumunJpInfo> colArrayList;
        private LayoutInflater mLayoutInflater;
        private GFilter filter;

        public AdapterSearchInfo(Context context, int resource, ArrayList<Yu221_JumunJpInfo> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;

            this.orgArrayList = objects;
            this.colArrayList = new ArrayList<Yu221_JumunJpInfo>();
            this.colArrayList.addAll(orgArrayList);
            this.mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Filter getFilter() {
            if (filter == null){
                filter  = new GFilter();
            }
            return filter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Yu221_JumunJpInfo yu221_jumunJpInfo = orgArrayList.get(position);
            if(convertView==null)
            {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(yu221_jumunJpInfo!=null){
                TextView tvSimpleField_1 = (TextView)convertView.findViewById(R.id.tvListviewSimple_field_1);
                TextView tvSimpleField_2 = (TextView)convertView.findViewById(R.id.tvListviewSimple_field_2);
                tvSimpleField_1.setText(yu221_jumunJpInfo.jepum_name);
                tvSimpleField_2.setText(yu221_jumunJpInfo.jepum_code);
            }

            return convertView;
        }

        private class GFilter extends Filter   //----필터 상속 받아 재정의.
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {   //-- 필터가 될때 일어나는 이벤트인듯.

                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if(constraint != null && constraint.toString().length() > 0)
                {
                    ArrayList<Yu221_JumunJpInfo> filteredItems = new ArrayList<Yu221_JumunJpInfo>();

                    for(int i = 0, l = colArrayList.size(); i < l; i++)
                    {
                        Yu221_JumunJpInfo yu221_jumunInfo = colArrayList.get(i);
                        if(yu221_jumunInfo.jepum_name.contains(constraint))
                            filteredItems.add(yu221_jumunInfo);
                    }
                    result.count = filteredItems.size();
                    result.values = filteredItems;
                }
                else
                {
                    synchronized(this)
                    {
                        result.values = colArrayList;
                        result.count = colArrayList.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {     //----필터가 되고난 후 값을 보여주는 이벤트인듯.

                orgArrayList = (ArrayList<Yu221_JumunJpInfo>)results.values;
                notifyDataSetChanged();
                clear();
                for(int i = 0, l = orgArrayList.size(); i < l; i++)
                    add(orgArrayList.get(i));
                notifyDataSetInvalidated();
            }
        }
    }

    protected void setYu221_JumunInfo(JSONArray jsonArray){
        alYu221_JumunJpInfo.clear();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Yu221_JumunJpInfo tmpInfo = new Yu221_JumunJpInfo();
                tmpInfo.jisi_gubun = EtcTool.isnullStr(jsonObject, "jisi_gubun");
                tmpInfo.jisi_beonho = EtcTool.isnullStr(jsonObject, "jisi_beonho");
                tmpInfo.jisi_beonho_1 = EtcTool.isnullStr(jsonObject, "jisi_beonho_1");
                tmpInfo.ymd = EtcTool.isnullStr(jsonObject, "ymd");
                tmpInfo.jepum_code = EtcTool.isnullStr(jsonObject, "jepum_code");
                tmpInfo.jepum_name = EtcTool.isnullStr(jsonObject, "jepum_name");
                alYu221_JumunJpInfo.add(tmpInfo);
            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            } catch (Exception e){
                Log.e("ERROR",e.toString());
            }
        }
    }

    protected void process_data_receive() {
        String sql;
        String ljisigubun, ljisibeonho, lcompanycode, ldbname;

        ljisigubun = mHostActivity.appendYuChulhaPlan.jisiGubun;
        ljisibeonho = mHostActivity.appendYuChulhaPlan.jisiBeonho;
        lcompanycode = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode;
        ldbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName;

        sql = "select distinct J.jisi_gubun, J.jisi_beonho, max(jisi_beonho_1) jisi_beonho_1 "
            +       "into #tmp_01 "
            +       "from yu_jumun J , yu_jumun_jp JP "
            +      "where J.ymd = JP.ymd "
            +        "and J.nno = JP.nno "
            +        "and J.jisi_gubun = " + "'" + ljisigubun + "' "
            +        "and J.jisi_beonho = " + "'" + ljisibeonho + "' "
            +  "group by J.jisi_gubun, J.jisi_beonho "

            + "select T.jisi_gubun, T.jisi_beonho, T.jisi_beonho_1, max(J.ymd) ymd "
            +       "into #tmp_02 "
            +       "from #tmp_01 T, yu_jumun J "
            +      "where J.jisi_gubun = T.jisi_gubun "
            +        "and J.jisi_beonho = T.jisi_beonho "
            +        "and J.jisi_beonho_1 = T.jisi_beonho_1 "
            +  "group by T.jisi_gubun, T.jisi_beonho, T.jisi_beonho_1 "

            + "select T.jisi_gubun, T.jisi_beonho, T.jisi_beonho_1, T.ymd, max(J.nno) nno "
            +       "into #tmp_03 "
            +       "from #tmp_02 T, yu_jumun J "
            +      "where J.jisi_gubun = T.jisi_gubun "
            +        "and J.jisi_beonho = T.jisi_beonho "
            +        "and J.jisi_beonho_1 = T.jisi_beonho_1 "
            +        "and J.ymd = T.ymd "
            +  "group by T.jisi_gubun, T.jisi_beonho, T.jisi_beonho_1, T.ymd "

            + "select T.jisi_gubun, T.jisi_beonho,T.jisi_beonho_1, T.ymd, JP.jepum_code, J.jepum_name "
            +       "from #tmp_03 T, yu_jumun_jp JP left outer join cm_jepum J "
            +                                                          "on JP.jepum_code = J.jepum_code "
            +      "where JP.ymd = T.ymd "
            +        "and JP.nno = T.nno "

            + "drop table #tmp_01 "
            + "drop table #tmp_02 ";
        Log.i("TEST",sql);

        String strURL = UserInfo.BACKUP_CONNECTION_URL
                + "Q=" + Base64Tool.Base64Encode(sql) + "&"
                + "C=" + Base64Tool.Base64Encode(lcompanycode) + "&"
                + "S=" + Base64Tool.Base64Encode(ldbname);
        strURL = strURL.replace("\n","");

        ConnectByAsyncTask conDS = new ConnectByAsyncTask();
        conDS.execute(strURL);

        try {
            setYu221_JumunInfo(new JSONArray(conDS.get()));
            //마스터리스트뷰에 대한 정보갱신
        } catch (Exception e) {
            Log.e("ERROR", "Yu221_JumunJp.process_data_receive:"+e.toString());
        }
    }

    protected class Yu221_JumunJpInfo {
        private String jisi_gubun;
        private String jisi_beonho;
        private String jisi_beonho_1;
        private String ymd;
        private String jepum_code;
        private String jepum_name;

        Yu221_JumunJpInfo() {
            jisi_gubun = "";
            jisi_beonho = "";
            jisi_beonho_1 = "";
            ymd = "";
            jepum_code = "";
            jepum_name = "";
        }
    }

    AdapterView.OnItemClickListener onJumunItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mHostActivity.appendYuChulhaPlan.setJepum(
                    ((Yu221_JumunJpInfo)parent.getItemAtPosition(position)).jepum_code,
                    ((Yu221_JumunJpInfo)parent.getItemAtPosition(position)).jepum_name
            );

            etYu211_JumunJp_Filter.setText(((Yu221_JumunJpInfo) parent.getItemAtPosition(position)).jepum_name.trim());
        }
    };
}
