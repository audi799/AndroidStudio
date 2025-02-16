package kr.gimaek.mobilegimaek;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import kr.gimaek.mobilegimaek.fragment.Jh111;
import kr.gimaek.mobilegimaek.fragment.WelcomeGimaek;
import kr.gimaek.mobilegimaek.fragment.Yu121;
import kr.gimaek.mobilegimaek.fragment.Yu221;
import kr.gimaek.mobilegimaek.fragment.Yu421;
import kr.gimaek.mobilegimaek.fragment.Yu431;
import kr.gimaek.mobilegimaek.fragment.Yu432;
import kr.gimaek.mobilegimaek.fragment.Yu521;
import kr.gimaek.mobilegimaek.fragment.Yu531;
import kr.gimaek.mobilegimaek.fragment.Yu541;
import kr.gimaek.mobilegimaek.yu211.AppendYuChulhaPlan;

public class Yu000 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public boolean isWelcompage = false;   //두번눌렀는지 처리
    private boolean isTwo = false;   //두번눌렀는지 처리

    UserInfo userInfo;
    FragmentManager fm = getFragmentManager();
    Fragment Main = new WelcomeGimaek();
    public AppendYuChulhaPlan appendYuChulhaPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yu000);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userInfo = (UserInfo)getApplicationContext();
        process_data_receive();
        fm.beginTransaction().replace(R.id.content_frame, Main).commit();
        appendYuChulhaPlan = new AppendYuChulhaPlan(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    protected void process_data_receive(){
        userInfo.process_companydata_receive();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawers();
        if(isWelcompage){
            if (!isTwo) {
                Toast.makeText(this, "\'뒤로\'버튼을 한번더 누르시면 종료됩니다", Toast.LENGTH_SHORT).show();
                myTimer timer = new myTimer(2000, 1); //2초동안 수행
                timer.start(); //타이머를 이용해줍시다
            } else {
                moveTaskToBack(true);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }else{
            fm.beginTransaction().replace(R.id.content_frame, Main).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.yu000, menu);
        for (int i=0;i<UserInfo.userCompanyInfo.size();i++) {
            /*
            Menu.add(groupId, itemId, order, title)
                     groupId : 그룹 ID를 지정하며, Menu에서 사용할수 있는 그룹 옵션을 사용할때 쓰입니다
                     itemId : Menu 각각의 item의 ID를 지정합니다
                     order : item의 순서이며, android:orderInCategory와 같습니다
                     title : item의 Title입니다
             */
            menu.add(0, UserInfo.userCompanyInfo.get(i).companyNo, i, UserInfo.userCompanyInfo.get(i).companyName);
        }
        menu.setGroupCheckable(0, true, true);
        menu.getItem(0).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        for (int i=0;i<UserInfo.userCompanyInfo.size();i++) {
            if (id == UserInfo.userCompanyInfo.get(i).companyNo) {
                UserInfo.userCompanyIdx = i;   //-- 이 변수 하나로 회사정보들이 바뀜.
                userInfo.process_companydata_receive();
                Toast.makeText(Yu000.this, UserInfo.userCompanyInfo.get(i).companyName, Toast.LENGTH_SHORT).show();
                fm.beginTransaction().replace(R.id.content_frame, Main).commit();
            }
        }

        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        isWelcompage = false;

        if (id == R.id.jh111) {
            if (UserInfo.userCompanyInfo.get(UserInfo.userCompanyIdx).companyUserJh111 != "" ) {
                fm.beginTransaction().replace(R.id.content_frame, new Jh111()).commit();
            } else {
                Toast.makeText(Yu000.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }/* else if (id == R.id.jh214) {
            fm.beginTransaction().replace(R.id.content_frame, new jh214()).commit();
        }*/ else if (id == R.id.yu121) {
            if (UserInfo.userCompanyInfo.get(UserInfo.userCompanyIdx).companyUserYu121 != "" ) {
                fm.beginTransaction().replace(R.id.content_frame, new Yu121()).commit();
            } else {
                Toast.makeText(Yu000.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.yu211) {
            fm.beginTransaction().replace(R.id.content_frame, appendYuChulhaPlan.getCurentlyForm()).commit();
        } else if (id == R.id.yu221) {
            if (UserInfo.userCompanyInfo.get(UserInfo.userCompanyIdx).companyUserYu221 != "" ) {
                fm.beginTransaction().replace(R.id.content_frame, new Yu221()).commit();
            } else {
                Toast.makeText(Yu000.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        /* else if (id == R.id.gps) {
            fm.beginTransaction().replace(R.id.content_frame, new GPS()).commit();
        }*/
        /* else if (id == R.id.yu411) {
            fm.beginTransaction().replace(R.id.content_frame, new yu411()).commit();
        }*/
        } else if (id == R.id.yu421) {
            if (UserInfo.userCompanyInfo.get(UserInfo.userCompanyIdx).companyUserYu421 != "" ) {
                fm.beginTransaction().replace(R.id.content_frame, new Yu421()).commit();
            } else {
                Toast.makeText(Yu000.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.yu431) {
            if ( UserInfo.userCompanyInfo.get(UserInfo.userCompanyIdx).companyUserYu431 != "" ) {
                fm.beginTransaction().replace(R.id.content_frame, new Yu431()).commit();
            } else {
                Toast.makeText(Yu000.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.yu432) {
            if (UserInfo.userCompanyInfo.get(UserInfo.userCompanyIdx).companyUserYu432 != "" ) {
                fm.beginTransaction().replace(R.id.content_frame, new Yu432()).commit();
            } else {
                Toast.makeText(Yu000.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }/* else if (id == R.id.yu511) {
            fm.beginTransaction().replace(R.id.content_frame, new yu511()).commit();
        }*/ else if (id == R.id.yu521) {
            if (UserInfo.userCompanyInfo.get(UserInfo.userCompanyIdx).companyUserYu521 != "" ) {
                fm.beginTransaction().replace(R.id.content_frame, new Yu521()).commit();
            } else {
                Toast.makeText(Yu000.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.yu531) {
            if (UserInfo.userCompanyInfo.get(UserInfo.userCompanyIdx).companyUserYu531 != "" ) {
                fm.beginTransaction().replace(R.id.content_frame, new Yu531()).commit();
            } else {
                Toast.makeText(Yu000.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.yu541) {
            if (UserInfo.userCompanyInfo.get(UserInfo.userCompanyIdx).companyUserYu431 != "" ) {
                fm.beginTransaction().replace(R.id.content_frame, new Yu541()).commit();
            } else {
                Toast.makeText(Yu000.this, "권한이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class myTimer extends CountDownTimer {  //타이머로 두번 백버튼 눌럿는지 체크. 목적: 두번누를때 앱종료하기 위함.
        public myTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
// TODO Auto-generated constructor stub
            isTwo=true;
        }
        @Override
        public void onFinish()
        {
            isTwo=false;
        }
        @Override
        public void onTick(long millisUntilFinished) {
// TODO Auto-generated method stub
        }
    }

    public void stepNext(){
        fm.beginTransaction().replace(R.id.content_frame, appendYuChulhaPlan.stepNext()).commit();
    }

    public void stepPrevious(){
        fm.beginTransaction().replace(R.id.content_frame, appendYuChulhaPlan.stepPrevious()).commit();
    }
}
