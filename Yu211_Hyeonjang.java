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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

import kr.gimaek.mobilegimaek.CompanyInfo;
import kr.gimaek.mobilegimaek.R;
import kr.gimaek.mobilegimaek.UserInfo;
import kr.gimaek.mobilegimaek.Yu000;

/**
 * Created by fromstog on 2016. 6. 29..
 */
public class Yu211_Hyeonjang extends Fragment{
    Yu000 mHostActivity;
    UserInfo userInfo;
    AdapterSearchInfo adapterSearchInfo;
    EditText etYu211_Hyeonjang_Filter;
    ListView lvYu211_Hyeonjang;
    Button btnYu211_Hyeonjang_Cancel, btnYu211_Hyeonjang_Next;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_yu211_hyeonjang,container, false);
        userInfo = (UserInfo)getActivity().getApplicationContext();
        etYu211_Hyeonjang_Filter = (EditText)rootView.findViewById(R.id.etYu211_Hyeonjang_Filter);
        etYu211_Hyeonjang_Filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterSearchInfo.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        try {

            adapterSearchInfo = new AdapterSearchInfo(getActivity(), R.layout.layout_listview_simple,
                    (ArrayList<CompanyInfo.ShHyeonjangInfo>)userInfo.getHyeonjangInfoByCustCode(mHostActivity.appendYuChulhaPlan.custCode));
                    //al);

            lvYu211_Hyeonjang = (ListView) rootView.findViewById(R.id.lvYu211_Hyeonjang);
            lvYu211_Hyeonjang.setAdapter(adapterSearchInfo);
            lvYu211_Hyeonjang.setOnItemClickListener(onHyeonjangItemClickListener);

        }catch (Exception e){
            Log.e("ERROR", "SearchInfo.onCreate.setAdapter: " + e.toString());
        }

        btnYu211_Hyeonjang_Cancel = (Button)rootView.findViewById(R.id.btnYu211_Hyeonjang_Cancel);
        btnYu211_Hyeonjang_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHostActivity.stepPrevious();
            }
        });

        btnYu211_Hyeonjang_Next = (Button)rootView.findViewById(R.id.btnYu211_Hyeonjang_Next);
        btnYu211_Hyeonjang_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHostActivity.appendYuChulhaPlan.hyeonjangCode.trim().equals("")){
                    Toast.makeText(mHostActivity.getApplication(), "현장을 선택하세요.", Toast.LENGTH_SHORT).show();
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
        mHostActivity.setTitle("출하예정등록[현장]");
    }

    public class AdapterSearchInfo extends ArrayAdapter<CompanyInfo.ShHyeonjangInfo> {
        private Context mContext;
        private int mResource;
        private ArrayList<CompanyInfo.ShHyeonjangInfo> orgArrayList;
        private ArrayList<CompanyInfo.ShHyeonjangInfo> colArrayList;
        private LayoutInflater mLayoutInflater;
        private GFilter filter;

        public AdapterSearchInfo(Context context, int resource, ArrayList<CompanyInfo.ShHyeonjangInfo> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.mResource = resource;

            this.orgArrayList = objects;
            this.colArrayList = new ArrayList<CompanyInfo.ShHyeonjangInfo>();
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
            CompanyInfo.ShHyeonjangInfo shHyeonjang_info = orgArrayList.get(position);
            if(convertView==null)
            {
                convertView = mLayoutInflater.inflate(mResource, null);
            }

            if(shHyeonjang_info!=null){
                TextView tvSimpleField_1 = (TextView)convertView.findViewById(R.id.tvListviewSimple_field_1);
                TextView tvSimpleField_2 = (TextView)convertView.findViewById(R.id.tvListviewSimple_field_2);
                tvSimpleField_1.setText(shHyeonjang_info.hyeonjang_code);
                tvSimpleField_2.setText(shHyeonjang_info.hyeonjang_name);
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
                    ArrayList<CompanyInfo.ShHyeonjangInfo> filteredItems = new ArrayList<CompanyInfo.ShHyeonjangInfo>();

                    for(int i = 0, l = colArrayList.size(); i < l; i++)
                    {
                        CompanyInfo.ShHyeonjangInfo shHyeonjang_info = colArrayList.get(i);
                        Log.i("CHECK",shHyeonjang_info.cust_code+"/"+mHostActivity.appendYuChulhaPlan.custCode);
                        if((shHyeonjang_info.cust_code.equals(mHostActivity.appendYuChulhaPlan.custCode))
                                &&(shHyeonjang_info.hyeonjang_name.contains(constraint)))
                            filteredItems.add(shHyeonjang_info);
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

                orgArrayList = (ArrayList<CompanyInfo.ShHyeonjangInfo>)results.values;
                notifyDataSetChanged();
                clear();
                for(int i = 0, l = orgArrayList.size(); i < l; i++)
                    add(orgArrayList.get(i));
                notifyDataSetInvalidated();
            }
        }
    }
    AdapterView.OnItemClickListener onHyeonjangItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                mHostActivity.appendYuChulhaPlan.setHyeonjang(
                        ((CompanyInfo.ShHyeonjangInfo) parent.getItemAtPosition(position)).hyeonjang_code,
                        ((CompanyInfo.ShHyeonjangInfo) parent.getItemAtPosition(position)).hyeonjang_name
                );

                etYu211_Hyeonjang_Filter.setText(((CompanyInfo.ShHyeonjangInfo) parent.getItemAtPosition(position)).hyeonjang_name.trim());
            }catch (Exception e){
                Log.i("ERROR",e.toString());
            }
        }
    };
}
