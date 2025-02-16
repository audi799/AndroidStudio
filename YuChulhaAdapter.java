package kr.gimaek.loader.adapters;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import kr.gimaek.loader.CommonDefine;
import kr.gimaek.loader.MainActivity;
import kr.gimaek.loader.R;
import kr.gimaek.loader.models.Yu_Chulha;

public class YuChulhaAdapter extends ArrayAdapter {
    Context mContext;
    public YuChulhaAdapter(Context context, ArrayList yuChulha){
        super(context, 0, yuChulha);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.vw_yu_chulha,
                    parent,
                    false);
        }

        Yu_Chulha chulha = (Yu_Chulha) getItem(position);

        TextView tvSangho = (TextView)convertView.findViewById(R.id.tvSangho);
        TextView tvNno = (TextView)convertView.findViewById(R.id.tvNno);
        TextView tvSigan = (TextView)convertView.findViewById(R.id.tvSigan);
        TextView tvCarCode = (TextView)convertView.findViewById(R.id.tvCarCode);
        TextView tvJepumName = (TextView)convertView.findViewById(R.id.tvJepumName);
        TextView tvSuryang = (TextView)convertView.findViewById(R.id.tvSuryang);

        tvSangho.setText(chulha.getSangho());
        tvNno.setText(Integer.toString(chulha.getNno()) + ". ");
        tvSigan.setText(chulha.getSigan());
        String carCode = chulha.getCar_Code();
        if(carCode.length() == 6)
            carCode = carCode.substring(0,2) + " " + carCode.substring(2);
        tvCarCode.setText(carCode);
        tvJepumName.setText(chulha.getJepum_Name());
        tvSuryang.setText(Double.toString(chulha.getSuryang()));

        Button bnChulha = (Button)convertView.findViewById(R.id.bnChulha);
        bnChulha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = MainActivity.mainActivityHandler.obtainMessage();
                message.what = CommonDefine.TRY_LOAD;
                message.obj = chulha;
                MainActivity.mainActivityHandler.sendMessage(message);
            }
        });
        return convertView;
    }
}
