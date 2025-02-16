package kr.gimaek.mobilegimaek.yu211;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
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
public class Yu211_Jumun extends Fragment {
    Yu000 mHostActivity;
    UserInfo userInfo;
    AdapterSearchInfo adapterSearchInfo;
    EditText etYu211_Jumun_Filter;
    ListView lvYu211_Jumun;
    Button btnYu211_Jumun_Cancel, btnYu211_Jumun_Next;

    ArrayList<Yu221_JumunInfo> alYu221_JumunInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_yu211_jumun,container, false);

        userInfo = (UserInfo) getActivity().getApplicationContext();

        lvYu211_Jumun = (ListView) rootView.findViewById(R.id.lvYu211_Jumun);
        alYu221_JumunInfo = new ArrayList<Yu221_JumunInfo>();

        process_data_receive();

        try {
            adapterSearchInfo = new AdapterSearchInfo(getActivity(), R.layout.layout_listview_simple, alYu221_JumunInfo);
            lvYu211_Jumun.setAdapter(adapterSearchInfo);
            lvYu211_Jumun.setOnItemClickListener(onJumunItemClickListener);

        }catch (Exception e){
            Log.e("ERROR", "SearchInfo.onCreate.setAdapter: " + e.toString());
        }

        etYu211_Jumun_Filter = (EditText)rootView.findViewById(R.id.etYu211_Jumun_Filter);
        etYu211_Jumun_Filter.addTextChangedListener(new TextWatcher() {
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

        btnYu211_Jumun_Cancel = (Button) rootView.findViewById(R.id.btnYu211_Jumun_Cancel);
        btnYu211_Jumun_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.stepPrevious();
            }
        });
        btnYu211_Jumun_Next = (Button) rootView.findViewById(R.id.btnYu211_Jumun_Next);
        btnYu211_Jumun_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHostActivity.appendYuChulhaPlan.jisiBeonho.trim().equals("")){
                    Toast.makeText(mHostActivity.getApplication(), "주문자료 선택하세요.", Toast.LENGTH_SHORT).show();
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
        mHostActivity.setTitle("출하예정등록[관급주문자료]");
    }

    public class AdapterSearchInfo extends ArrayAdapter<Yu221_JumunInfo> {
        private Context mContext;
        private int mResource;
        private ArrayList<Yu221_JumunInfo> orgArrayList;
        private ArrayList<Yu221_JumunInfo> colArrayList;
        private LayoutInflater mLayoutInflater;
        private GFilter filter;

        public AdapterSearchInfo(Context context, int resource, ArrayList<Yu221_JumunInfo> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;

            this.orgArrayList = objects;
            this.colArrayList = new ArrayList<Yu221_JumunInfo>();
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
            Yu221_JumunInfo yu221_jumunInfo = orgArrayList.get(position);
            if(convertView==null)
            {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(yu221_jumunInfo!=null){
                TextView tvSimpleField_1 = (TextView)convertView.findViewById(R.id.tvListviewSimple_field_1);
                TextView tvSimpleField_2 = (TextView)convertView.findViewById(R.id.tvListviewSimple_field_2);
                tvSimpleField_1.setText(yu221_jumunInfo.jisi_beonho);
                tvSimpleField_2.setText(yu221_jumunInfo.jisi_gubun);
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
                    ArrayList<Yu221_JumunInfo> filteredItems = new ArrayList<Yu221_JumunInfo>();

                    for(int i = 0, l = colArrayList.size(); i < l; i++)
                    {
                        Yu221_JumunInfo yu221_jumunInfo = colArrayList.get(i);
                        if(yu221_jumunInfo.jisi_beonho.contains(constraint))
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

                orgArrayList = (ArrayList<Yu221_JumunInfo>)results.values;
                notifyDataSetChanged();
                clear();
                for(int i = 0, l = orgArrayList.size(); i < l; i++)
                    add(orgArrayList.get(i));
                notifyDataSetInvalidated();
            }
        }
    }

    protected void setYu221_JumunInfo(JSONArray jsonArray){
        alYu221_JumunInfo.clear();
        for(int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Yu221_JumunInfo tmpInfo = new Yu221_JumunInfo();
                tmpInfo.jisi_gubun = EtcTool.isnullStr(jsonObject, "jisi_gubun");
                tmpInfo.jisi_beonho = EtcTool.isnullStr(jsonObject, "jisi_beonho");
                alYu221_JumunInfo.add(tmpInfo);
            } catch (JSONException e) {
                Log.e("ERROR", e.toString());
            } catch (Exception e){
                Log.e("ERROR",e.toString());
            }
        }
    }

    protected void process_data_receive() {
        String sql;
        String lcustcode, lhyeonjangcode, lcompanycode, ldbname;

        lcustcode = mHostActivity.appendYuChulhaPlan.custCode;
        lhyeonjangcode = mHostActivity.appendYuChulhaPlan.hyeonjangCode;
        lcompanycode = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyCode;
        ldbname = userInfo.userCompanyInfo.get(userInfo.userCompanyIdx).companyDBName;

        sql = "select distinct J.jisi_gubun, J.jisi_beonho "
            +       "from yu_jumun J , yu_jumun_jp JP "
            +      "where J.ymd = JP.ymd "
            +        "and J.nno = JP.nno "
            +        "and J.cust_code = '" + lcustcode + "' "
            +        "and JP.hyeonjang_code = '" + lhyeonjangcode + "' ";
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
            Log.e("ERROR", "Yu221_Jumun.process_data_receive:"+e.toString());
        }
    }

    protected class Yu221_JumunInfo {
        private String jisi_gubun;
        private String jisi_beonho;

        Yu221_JumunInfo() {
            jisi_gubun = "";
            jisi_beonho = "";
        }
    }

    AdapterView.OnItemClickListener onJumunItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mHostActivity.appendYuChulhaPlan.setJisiBeonho(
                    ((Yu221_JumunInfo)parent.getItemAtPosition(position)).jisi_gubun,
                    ((Yu221_JumunInfo)parent.getItemAtPosition(position)).jisi_beonho
            );

            etYu211_Jumun_Filter.setText(((Yu221_JumunInfo) parent.getItemAtPosition(position)).jisi_beonho.trim());
        }
    };
}
