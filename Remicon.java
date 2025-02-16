package kr.gimaek.mobilegimaek;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Remicon extends AppCompatActivity {
    UserInfo userInfo;
    TextView etUserID;
    TextView etUserPass;
    Intent intentYu000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remicon);

        //1.userInfo클래스 생성(전체앱에서 한번 생성하게 된다.)
        userInfo = (UserInfo)getApplicationContext();
        //2.저장된 로컬로그인정보 조회
        userInfo.getUserInfoFromSP();
        //3.로그인정보 TextView에 세팅
        etUserID = (TextView)findViewById(R.id.etUserID);
        etUserPass = (TextView)findViewById(R.id.etUserPass);
        etUserID.setText(userInfo.userID);
        etUserPass.setText(userInfo.userPass);
    }

    public void btnLogin(View view){
        if (userInfo.process_login(etUserID.getText().toString(), etUserPass.getText().toString())){
            Log.i("TEST","로그인 성공:" + userInfo.userLoginState);
            Log.i("TEST",userInfo.userCompanyInfo.get(0).companyGPSDBPath);
            intentYu000 = new Intent(this, Yu000.class);
            startActivity(intentYu000);

        }else{
            Log.i("TEST","로그인실패");
        }
    }
}
