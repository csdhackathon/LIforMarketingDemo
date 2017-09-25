package com.ars.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.ars.utils.Utils;
import com.ars.utils.ConstantUnits;
import com.ars.utils.PermissionsUtility;
import com.ars.utils.DialogUtility;

import com.ars.R;

/**
 * Created by abhayraj on 14/09/17.
 */


public class SplashScreenActivity extends Activity {

    private final String TAG = SplashScreenActivity.class.getSimpleName();
    private boolean isLoadingActivity = false;
    protected Utils mUtility;
    private ConstantUnits mConstantUnits;
    public boolean isAllPermissionGranted = false;
    private AlertDialog mAlertDialog;

    private String[] mPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);
            mUtility = Utils.getInstance(SplashScreenActivity.this);
            mConstantUnits = ConstantUnits.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            // Check if all permissions are granted or not.
            if (PermissionsUtility.getInstance(this).checkPermissions(mPermissions))
            {

                initializeActivityProcess();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if(isAllPermissionGranted && mUtility.isConnectedToNetwork()
                    ) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to start the Activity process
     */
    public void initializeActivityProcess() {
        try {
            loadActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to load the activity
     */
    private void loadActivity() {
        try  {
            if(isLoadingActivity) {
                return;
            }
            else {
                isLoadingActivity = true;
            }
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            if(!mUtility.getMetaDataFromManifest(mConstantUnits.PBGEOMAP_ACCESS_TOKEN).equalsIgnoreCase(mConstantUnits.EMPTY)
                                    && !mUtility.getMetaDataFromManifest(mConstantUnits.PBGEOMAP_SECRET_KEY).equalsIgnoreCase(mConstantUnits.EMPTY)) {
                                /*Intent intent = null;
                                intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                SplashScreenActivity.this.finish();*/

                                startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                                finish();
                            }
                            else {
                                showCustomAlertDialog(SplashScreenActivity.this, getString(R.string.key_error), getString(R.string.api_key_not_found));
                            }

                        }
                    }, 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCustomAlertDialog(Context context, String title, String message)
    {
        try {
            if(mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.cancel();
            }
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.support.v7.appcompat.R.style.AlertDialog_AppCompat));
            mBuilder.setCancelable(false);
            if(!title.equalsIgnoreCase(ConstantUnits.getInstance().EMPTY)) {
                mBuilder.setTitle(title);
            }
            if(!message.equalsIgnoreCase(ConstantUnits.getInstance().EMPTY)) {
                mBuilder.setMessage(message);
            }
            String positiveText = context.getString(android.R.string.ok);
            mBuilder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // positive button logic
                    mAlertDialog.cancel();
                    SplashScreenActivity.this.finish();
                }
            });

            mAlertDialog = mBuilder.create();
            mAlertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtility.getInstance(SplashScreenActivity.this).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == DialogUtility.REQUEST_PERMISSION_SETTING) {
                //onStart();
            }
            else {
                SplashScreenActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
