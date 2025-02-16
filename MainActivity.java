package kr.gimaek.loader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.gimaek.loader.adapters.YuChulhaAdapter;
import kr.gimaek.loader.controllers.YuChulhaController;
import kr.gimaek.loader.models.Yu_Chulha;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final int timerInterval = 1000*10;//30초
    ListView lvData;
    public static Handler mainActivityHandler;
    YuChulhaAdapter yuChulhaAdapter;
    Message rcvMessage;
    YuChulhaController chulhaController;

    LinearLayout llCancel;
    String workDate;

    Yu_Chulha lastLoaded;

    private final Timer mTimer = new Timer();
    private TimerTask mTimerTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        workDate = Tools.getToday();

        setRcvDataHandler();
        lvData = (ListView)findViewById(R.id.lvData);
        rcvMessage = mainActivityHandler.obtainMessage();
        chulhaController = new YuChulhaController();
        chulhaController.rcvData(workDate);

        llCancel = (LinearLayout)findViewById(R.id.llCancel);
        llCancel.setVisibility(View.GONE);

        TextView tvCancelLoad = (TextView)findViewById(R.id.tvCancelLoad);
        tvCancelLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastLoaded != null){
                    if(!lastLoaded.getYmd().equals("")){
                        chulhaController.cancelLoad(lastLoaded.getYmd(), lastLoaded.getNno());
                    }
                }
            }
        });
    }

    private void setRcvDataHandler(){
        mainActivityHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case CommonDefine.RCV_YU_CHULHA_SUCC:
                        showMessage("출하자료 조회 성공!");
                        YuChulhaAdapter adapter = new YuChulhaAdapter(MainActivity.this, (ArrayList<Yu_Chulha>)msg.obj);
                        lvData.setAdapter(adapter);
                        break;
                    case CommonDefine.RCV_YU_CHULHA_FAIL:
                        showMessage("출하자료 조회 실패!");
                        break;
                    case CommonDefine.LOAD_SUCC:
                        chulhaController.rcvData(workDate);
                        break;
                    case CommonDefine.LOAD_FAIL:
                        showMessage("상차처리에 실패했습니다. 다시 시도하세요.");
                        break;
                    case CommonDefine.TRY_LOAD:
                        lastLoaded = (Yu_Chulha)msg.obj;
                        chulhaController.load(lastLoaded.getYmd(), lastLoaded.getNno());
                        showCancelLoad();
                        break;
                    case CommonDefine.CANCEL_LOAD_FAIL:
                        showMessage("상차취소 처리에 실패했습니다. 다시 시도하세요.");
                        break;
                    case CommonDefine.CANCEL_LOAD_SUCC:
                        lastLoaded = null;
                        chulhaController.rcvData(workDate);
                        llCancel.setVisibility(View.GONE);
                        break;
                    case 9999:
                        llCancel.setVisibility(View.GONE);
                        break;
                }
            }
        };
    }

    private void showMessage(String msg){
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
    }

    private void showCancelLoad(){
        llCancel.setVisibility(View.VISIBLE);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "취소 타이머 실행");
                Message message = mainActivityHandler.obtainMessage();
                message.what = 9999;
                mainActivityHandler.sendMessage(message);
                timer.cancel();
            }
        };
        timer.schedule(timerTask, 1000*5, 1000);
    }

    @Override
    protected void onResume() {
        startTimer();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopTimer();
        super.onPause();
    }

    private void stopTimer(){
        if (mTimerTask != null){
            mTimerTask.cancel();
        }
    }

    private void startTimer(){
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                chulhaController.rcvData(workDate);
                Log.i(TAG, "타이머 실행");
            }
        };
        mTimer.schedule(mTimerTask, timerInterval, timerInterval);
    }
}