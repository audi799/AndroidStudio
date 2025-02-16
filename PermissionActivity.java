package kr.mappers.atlansdk;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import kr.mappers.atlansdk.TestActivities.SplashExample;

public class PermissionActivity extends AppCompatActivity {
    int nCallPermissionWindow = 0;
    int nReqPermissionWindow = 0;

    static final String PACKAGE_URL_SCHEME = "package:";
    static final int PERMISSIONS_REQUEST_READ_LOCATION = 0x00000001;
    static final int PERMISSIONS_REQUEST_READ_EXT_STORAGE = 0x00000010;
    static final int PERMISSIONS_REQUEST_READ_PHONE = 0x00000011;
    static final int PERMISSIONS_REQUEST_READ_SYSTEM_ALERT_WINDOW = 0x00000110;
    static final int PERMISSIONS_REQUEST_ALL = 0x00000111;

    static final int SETTING_PERMISSION = 0x00001000;


    static final int CURRENT_STATE_START_ACTIVITY = 0;
    static final int CURRENT_STATE_SHOW_WARNING = 1;
    static final int CURRENT_STATE_NONE = 2;

    int nCurrentCheckStep = 0;

    Dialog permissionAlertDlg;

    private Context mContext;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_LOCATION) {
            nCurrentCheckStep = PERMISSIONS_REQUEST_READ_EXT_STORAGE;
            checkExtStorage();
        } else if (requestCode == PERMISSIONS_REQUEST_READ_EXT_STORAGE) {
            nCurrentCheckStep = PERMISSIONS_REQUEST_READ_PHONE;
            checkPhone();
        } else if (requestCode == PERMISSIONS_REQUEST_READ_PHONE) {
            if (isStartActivity() == CURRENT_STATE_SHOW_WARNING) {
                // 모든 퍼미션 완료되지 않은 경우 경고 팝업 띄움
                showPermissionWarningDialog();
            } else {
                nCurrentCheckStep = PERMISSIONS_REQUEST_ALL;
                // 모든 퍼미션이 완료 될 경우 아틀란 실행
                startSlashActivity();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        if (PermissionGranted() == PERMISSIONS_REQUEST_ALL) {
            startSlashActivity();
        } else {
            int nReturn = PermissionGranted();
            if (nReturn == PERMISSIONS_REQUEST_READ_LOCATION) {
                checkLocation();
            } else if (nReturn == PERMISSIONS_REQUEST_READ_EXT_STORAGE) {
                checkExtStorage();
            } else if (nReturn == PERMISSIONS_REQUEST_READ_PHONE) {
                checkPhone();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SETTING_PERMISSION:
                if (isStartActivity() == CURRENT_STATE_START_ACTIVITY) {
                    if (permissionAlertDlg != null) permissionAlertDlg.dismiss();

                    nCurrentCheckStep = PERMISSIONS_REQUEST_READ_PHONE;
                    checkPhone();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("isFinish", true);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkLocation() {
        nCallPermissionWindow = 0;
        nReqPermissionWindow = 0;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_DENIED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_READ_LOCATION);
                }

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_READ_LOCATION);

            }
        } else {
            nCurrentCheckStep = PERMISSIONS_REQUEST_READ_EXT_STORAGE;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXT_STORAGE);

        }
    }

    private void checkExtStorage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_READ_EXT_STORAGE);
                }
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_READ_EXT_STORAGE);

            }
        } else {
            nCurrentCheckStep = PERMISSIONS_REQUEST_READ_PHONE;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE);
        }
    }

    private void checkPhone() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_DENIED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            PERMISSIONS_REQUEST_READ_PHONE);
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE);
            }
        } else {
            if (isStartActivity() == CURRENT_STATE_SHOW_WARNING) {
                // 모든 퍼미션 완료되지 않은 경우 경고 팝업 띄움
                showPermissionWarningDialog();
            } else {
                nCurrentCheckStep = PERMISSIONS_REQUEST_ALL;
                // 모든 퍼미션이 완료 될 경우 아틀란 스마트 실행
                startSlashActivity();
            }
        }
    }

    private int PermissionGranted() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            nCurrentCheckStep = PERMISSIONS_REQUEST_READ_LOCATION;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            nCurrentCheckStep = PERMISSIONS_REQUEST_READ_EXT_STORAGE;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            nCurrentCheckStep = PERMISSIONS_REQUEST_READ_PHONE;
        } else {
            nCurrentCheckStep = PERMISSIONS_REQUEST_ALL;
        }

        return nCurrentCheckStep;
    }

    private int isStartActivity() {
        int nRet = CURRENT_STATE_NONE;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
            /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED*/) {
            nRet = CURRENT_STATE_START_ACTIVITY;
        } else {
            nRet = CURRENT_STATE_SHOW_WARNING;
        }

        return nRet;
    }

    private void showPermissionWarningDialog() {
        LayoutInflater inflater;
        View layout;

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.permission_alert_verify, null);

        TextView tvFinish = (TextView) layout.findViewById(R.id.btn_finish);
        TextView tvSetting = (TextView) layout.findViewById(R.id.btn_setting);

        tvFinish.setOnClickListener(onClick);
        tvSetting.setOnClickListener(onClick);

        permissionAlertDlg = new Dialog(mContext);
        permissionAlertDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        permissionAlertDlg.setContentView(layout);
        permissionAlertDlg.setCancelable(false);
        permissionAlertDlg.setCanceledOnTouchOutside(false);
        permissionAlertDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        permissionAlertDlg.show();
    }

    private void startSlashActivity() {
        Intent intent = new Intent(this, SplashExample.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btn_finish:
                    permissionAlertDlg.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra("isFinish", true);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;

                case R.id.btn_setting:
                    Intent settingIntent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    settingIntent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
                    startActivityForResult(settingIntent, SETTING_PERMISSION);
                    break;
            }
        }
    };
}
