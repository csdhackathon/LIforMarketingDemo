package com.ars.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Data;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ars.R;
import com.ars.auth.ARSAuthenticator;
import com.ars.orm.ListRow;
import com.ars.table.ResCountry;
import com.ars.table.ResPartner;
import com.ars.table.ResState;
import com.ars.utils.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ars.utils.ConstantUnits;
import com.ars.utils.MapUtility;
import com.ars.utils.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import android.os.AsyncTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.ParseException;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.message.BufferedHeader;
import pb.JSON;

import android.app.*;

public class ContactDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_ASK_PERMISSIONS_WRITE_CONTACT = 11;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_CALL_CONTACT = 22;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_SEND_SMS = 33;
    private ResPartner resPartner;
    private ResState resState;
    private ResCountry resCountry;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fabEdit;
    private TextView textMobileNumber, textPhoneNumber, textEmail, textStreet, textStreet2,
            textCity, textState, textCountry, textPincode, textWebsite, textFax,
            textAudience,textIncomeGroup,textBrands, textEthnicity;



    private ImageView profileImage, callImage;
    private LinearLayout contactNumberLayout, emailLayout, addressLayout, websiteLayout, faxLayout;
    private RelativeLayout mobileLayout, phoneLayout;
    private EditText editMobileNumber, editPhoneNumber, editCity, editEmail, editState, editCountry,
            editPincode, editWebsite, editFax, editStreet, editStreet2;
    private String stringName, stringMobileNumber, stringPhoneNumber, stringEmail, stringStreet, stringStreet2,
            stringCity, stringPincode, stringStateId, stringStateName, stringCountryId,
            stringCountryName, stringWebsite, stringFax, stringImage, stringAddress, stringLatitude, stringLongitude;
    private int _id;
    private String address;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout viewLayout, editLayout;
    private Intent dial;

    private MapUtility mMapUtility;
    private MapTileProviderBase mProvider;

    private MapView mMapView;
    private Location mLocation;
    private Utils mUtility;

    private ConstantUnits mConstantUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail_activity);

        mUtility = Utils.getInstance(this);
        mConstantUnits = ConstantUnits.getInstance();

        toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.profile_collapsing);

        fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);
        fabEdit.setOnClickListener(this);

        resState = new ResState(this);
        resCountry = new ResCountry(this);

        init();

        _id = getIntent().getIntExtra("id", 0);
        resPartner = new ResPartner(this);
        List<ListRow> rows = resPartner.select("_id = ?", String.valueOf(_id));
        for (ListRow row : rows) {

            stringName = row.getString("name");
            stringMobileNumber = row.getString("mobile");
            stringPhoneNumber = row.getString("phone");
            stringEmail = row.getString("email");
            stringStreet = row.getString("street");
            stringStreet2 = row.getString("street2");
            stringCity = row.getString("city");
            stringPincode = row.getString("zip");
            stringStateId = row.getString("state_id");
            stringCountryId = row.getString("country_id");
            stringFax = row.getString("fax");
            stringWebsite = row.getString("website");
            stringImage = row.getString("image_medium");

            stringAddress = row.getString("address");
            stringLatitude = row.getString("latitude");
            stringLongitude = row.getString("longitude");

            //TODO: state_name and Country_name from id
            stringStateName = "false";
            stringCountryName = "false";

            collapsingToolbarLayout.setTitle(row.getString("name"));
            //contact number
            textMobileNumber.setText(stringMobileNumber);
            if (stringMobileNumber.equals("false") && !stringPhoneNumber.equals("false")) {
                mobileLayout.setVisibility(View.GONE);
                callImage.setVisibility(View.VISIBLE);
            }

            if (stringMobileNumber.equals("false")) {
                mobileLayout.setVisibility(View.GONE);
            }

            textPhoneNumber.setText(stringPhoneNumber);
            if (stringPhoneNumber.equals("false")) {
                phoneLayout.setVisibility(View.GONE);
            }

            if (stringMobileNumber.equals("false") && stringPhoneNumber.equals("false")) {
                contactNumberLayout.setVisibility(View.GONE);
            }

            //email
            textEmail.setText(stringEmail);
            if (stringEmail.equals("false")) {
                emailLayout.setVisibility(View.GONE);
            }

            //address
            textStreet.setText(stringAddress);
            textStreet.setVisibility(stringAddress.equals("false") ? View.GONE : View.VISIBLE);

            textStreet2.setText(stringStreet2);
            textStreet2.setVisibility(stringStreet2.equals("false") ? View.GONE : View.GONE);

            textCity.setText(stringCity);
            textCity.setVisibility(stringCity.equals("false") ? View.GONE : View.GONE);

            textPincode.setText(stringPincode);
            textPincode.setVisibility(stringPincode.equals("false") ? View.GONE : View.GONE);

            textState.setText(stringStateId);
            textState.setVisibility(stringStateId.equals("0") ? View.GONE : View.GONE);

            textCountry.setText(stringCountryId);
            textCountry.setVisibility(stringCountryId.equals("0") ? View.GONE : View.GONE);

            if (stringStreet.equals("false") && stringStreet2.equals("false") &&
                    stringCity.equals("false") && stringPincode.equals("false") &&
                    stringStateId.equals("0") && stringCountryId.equals("0")) {
                addressLayout.setVisibility(View.GONE);
            }

            //website
            textWebsite.setText(stringWebsite);
            if (stringWebsite.equals("false")) {
                websiteLayout.setVisibility(View.GONE);
            }

            //fax
            textFax.setText(stringFax);
            if (stringFax.equals("false")) {
                faxLayout.setVisibility(View.GONE);
            }

            //profile image
            if (!stringImage.equals("false")) {
                profileImage.setImageBitmap(BitmapUtils.getBitmapImage(this, stringImage));
            } else {
                profileImage.setImageBitmap(BitmapUtils.getAlphabetImage(this,
                        row.getString("name")));
            }

            List<Double> cordinate =  new ArrayList<>( Arrays.asList(Double.parseDouble(stringLongitude), Double.parseDouble(stringLatitude)));;// Arrays.asList(1.38, 2.56, 4.3);
            mMapView = (MapView) findViewById(R.id.map_view_tab_fragment);
            mMapUtility = new MapUtility(ContactDetailActivity.this, mMapView);

            if(mMapUtility != null) {
                mMapUtility.initializeMapViews(ContactDetailActivity.this, mProvider);
            }

            try {
                mLocation = new Location("");
                //*************************************************************************//
                mLocation.setLatitude(Double.parseDouble(stringLatitude));
                mLocation.setLongitude(Double.parseDouble(stringLongitude));
                //*************************************************************************//

                mMapUtility.setMyLocationMarker(mLocation, true, stringAddress);


                //AsyncHttpClient client = new AsyncHttpClient();
                /*Header[] headers = {
                        new BasicHeader("Content-type", "application/json")
                        ,new BasicHeader("Accept", "application/json")
                        //,new BasicHeader("Connection", "keep-alive")
                        //,new BasicHeader("keep-alive", "115")
                        //,new BasicHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                };*/

                JSONArray locationArray = new JSONArray();
                JSONObject location = new JSONObject();
                location.put("latitude", Double.parseDouble(stringLatitude));
                location.put("longitude", Double.parseDouble(stringLongitude));
                locationArray.put(location);

                JSONObject obj = new JSONObject();
                obj.put("startDate", "2017-08-28");
                obj.put("endDate", "2017-09-29");
                obj.put("dayInterval", "Evening");
                obj.put("locations", locationArray);
                obj.put("transactionId", "1");


                final String bodyText = obj.toString();  //JSON "{\"startDate\":\"2017-09-02\",\"endDate\":\"2017-09-06\",\"dayInterval\":\"Evening\",\"locations\":[{\"latitude\":41.84152,\"longitude\":-87.6436}],\"transactionId\":\"1\"}";

                //HttpEntity entity= new StringEntity(bodyText);

                //final String url = mUtility.getMetaDataFromManifest(mConstantUnits.PBLI4MKT_URL);

                try {
                    Log.i("GeoCode","geocode");
                    String urlLi4Mkt = mUtility.getMetaDataFromManifest(mConstantUnits.PBLI4MKT_URL);

                    URL url = new URL(urlLi4Mkt);
                    //For Sync call in main thread.
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                    String line;
                    StringBuffer jsonString = new StringBuffer();

                    uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    uc.setRequestProperty("Accept", "application/json; charset=UTF-8");
                    uc.setRequestMethod("POST");
                    uc.setDoOutput(true);
                    uc.setInstanceFollowRedirects(false);
                    uc.connect();
                    OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
                    writer.write(bodyText);
                    writer.flush();
                    writer.close();
                    try {

                        int statusCode = uc.getResponseCode();
                        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            // handle unauthorized (if service requires user login)
                        } else if (statusCode != HttpURLConnection.HTTP_OK) {
                            // handle any other errors, like 404, 500,..
                        }

                        BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                        while((line = br.readLine()) != null){
                            jsonString.append(line);
                        }
                        br.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    uc.disconnect();
                    String result = jsonString.toString();//"{\"locationProfileData\":{\"audience\":[{\"count\":1,\"name\":\"Shopping Addicts\",\"weight\":0.54562}],\"incomeGroup\":[{\"count\":14,\"name\":\"$15000-$24999\"}],\"brands\":[{\"count\":5,\"name\":\"SMARTSTYLE\"},{\"count\":6,\"name\":\"TOYS R US\"},{\"count\":6,\"name\":\"FISERV\"},{\"count\":6,\"name\":\"PNC\"},{\"count\":6,\"name\":\"REDBOX\"},{\"count\":6,\"name\":\"WALGREENS\"},{\"count\":6,\"name\":\"DOMINICK'S FINER FOODS\"},{\"count\":6,\"name\":\"BURGER KING\"},{\"count\":7,\"name\":\"WINGSTOP\"},{\"count\":7,\"name\":\"AVENUE\"}],\"ethnicity\":[{\"count\":14,\"name\":\"Asian Alone\"}],\"totalProspects\":{\"count\":18,\"name\":\"TOTAL PROSPECTS\"}},\"transactionId\":1}"; //
                    JSONObject jsonObj = new JSONObject(result);
                    JSONObject locationProfileData = jsonObj.getJSONObject("locationProfileData");
                    JSONArray audience = locationProfileData.getJSONArray("audience");
                    JSONArray incomeGroup = locationProfileData.getJSONArray("incomeGroup");
                    JSONArray brands = locationProfileData.getJSONArray("brands");
                    JSONArray ethnicity = locationProfileData.getJSONArray("ethnicity");
                    StringBuilder stringBuilder = new StringBuilder();

                    for(int count = 0; count < audience.length(); count++){
                        stringBuilder.append(audience.getJSONObject(count).get("name"));
                        stringBuilder.append("\n");
                    }

                    textAudience.setText(stringBuilder.toString());
                    textAudience.setLines((audience.length() > 0 ? audience.length() - 1 : 0) );
                    textAudience.setMaxLines(audience.length());

                    stringBuilder = new StringBuilder();

                    for(int count = 0; count < incomeGroup.length(); count++){
                        stringBuilder.append(incomeGroup.getJSONObject(count).get("name"));
                        stringBuilder.append("\n");
                    }

                    textIncomeGroup.setText(stringBuilder.toString());
                    textIncomeGroup.setLines((incomeGroup.length() > 0 ? incomeGroup.length() - 1 : 0));
                    textIncomeGroup.setMaxLines(incomeGroup.length());

                    stringBuilder = new StringBuilder();

                    for(int count = 0; count < brands.length(); count++){
                        stringBuilder.append(brands.getJSONObject(count).get("name"));
                        stringBuilder.append("\n");
                    }

                    textBrands.setText(stringBuilder.toString());
                    textBrands.setLines(brands.length());
                    textBrands.setMaxLines(brands.length());

                    stringBuilder = new StringBuilder();

                    for(int count = 0; count < ethnicity.length(); count++){
                        stringBuilder.append(ethnicity.getJSONObject(count).get("name"));
                        stringBuilder.append("\n");
                    }

                    textEthnicity.setText(stringBuilder.toString());
                    textEthnicity.setLines((ethnicity.length() > 0 ? ethnicity.length() - 1 : 0));
                    textEthnicity.setMaxLines(ethnicity.length());

                } catch (Exception e) {
                    e.printStackTrace();
                }


                /*client.post(this, url, headers, entity, null , new AsyncHttpResponseHandler () {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        try {
                            JSONArray results = new JSONObject(new String(bytes)).getJSONArray("results");
                            //loop
                               //name\nname\n

                            //textAudience.setText("");
                            //textAudience.setLines(10);
                            //textAudience.setMaxLines(11);

                            //textAudience = (TextView) findViewById(R.id.textAudience);
                            //textIncomeGroup = (TextView) findViewById(R.id.textIncomeGroup);
                            //textBrands = (TextView) findViewById(R.id.textBrands);
                            //textEthnicity = (TextView) findViewById(R.id.textEthnicity);

                        } catch (JSONException e) {
                            //Toast.makeText(parent, "Error updating map", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    }



                });*/


                // post(Context context, String url, Header[] headers, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler)
                //client.post(getApplicationContext(), mUtility.getMetaDataFromManifest(mConstantUnits.PBLI4MKT_URL), headers, entity, "application/json");


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private class MyAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            try {
                Log.i("GeoCode","geocode");
                String urlLi4Mkt = mUtility.getMetaDataFromManifest(mConstantUnits.PBLI4MKT_URL);

                JSONArray locationArray = new JSONArray();
                JSONObject location = new JSONObject();
                location.put("latitude", 41.84152);
                location.put("longitude", -87.6436);
                locationArray.put(location);

                JSONObject obj = new JSONObject();
                obj.put("startDate", "2017-08-28");
                obj.put("endDate", "2017-09-29");
                obj.put("dayInterval", "Evening");
                obj.put("locations", locationArray);
                obj.put("transactionId", "1");


                final String bodyText = obj.toString();

                URL url = new URL(urlLi4Mkt);
                HttpURLConnection uc = (HttpURLConnection) url.openConnection();
                String line;
                StringBuffer jsonString = new StringBuffer();

                uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                uc.setRequestMethod("POST");
                uc.setDoInput(true);
                uc.setInstanceFollowRedirects(false);
                uc.connect();
                OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
                writer.write(bodyText);
                writer.close();
                try {

                    BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                    while((line = br.readLine()) != null){
                        jsonString.append(line);
                    }
                    br.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                uc.disconnect();
                return jsonString.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "".toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you*/
            Log.i("Result",result);


        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {


        }
    }


    public static String makePostRequest(String stringUrl, String payload,
                                         Context context) throws IOException {

        URL url = new URL(stringUrl);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        String line;
        StringBuffer jsonString = new StringBuffer();

        uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        uc.setRequestMethod("POST");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();
        OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while((line = br.readLine()) != null){
                jsonString.append(line);
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        uc.disconnect();
        return jsonString.toString();
    }


    private void init() {

        textMobileNumber = (TextView) findViewById(R.id.textMobileNumber);
        textPhoneNumber = (TextView) findViewById(R.id.textPhoneNumber);
        textEmail = (TextView) findViewById(R.id.textEmail);
        textCity = (TextView) findViewById(R.id.textCity);
        textStreet = (TextView) findViewById(R.id.textStreet);
        textStreet2 = (TextView) findViewById(R.id.textStreet2);
        textState = (TextView) findViewById(R.id.textState);
        textCountry = (TextView) findViewById(R.id.textCountry);
        textWebsite = (TextView) findViewById(R.id.textWebsite);
        textFax = (TextView) findViewById(R.id.textFax);
        textPincode = (TextView) findViewById(R.id.textPincode);

        textAudience = (TextView) findViewById(R.id.textAudience);
        textIncomeGroup = (TextView) findViewById(R.id.textIncomeGroup);
        textBrands = (TextView) findViewById(R.id.textBrands);
        textEthnicity = (TextView) findViewById(R.id.textEthnicity);

        profileImage = (ImageView) findViewById(R.id.avatar);
        callImage = (ImageView) findViewById(R.id.imageCall2);

        contactNumberLayout = (LinearLayout) findViewById(R.id.contactNumberLayout);
        emailLayout = (LinearLayout) findViewById(R.id.emailLayout);
        addressLayout = (LinearLayout) findViewById(R.id.addressLayout);
        websiteLayout = (LinearLayout) findViewById(R.id.websiteLayout);
        faxLayout = (LinearLayout) findViewById(R.id.faxLayout);

        viewLayout = (LinearLayout) findViewById(R.id.viewLayout);
        editLayout = (LinearLayout) findViewById(R.id.editLayout);

        mobileLayout = (RelativeLayout) findViewById(R.id.mobileLayout);
        phoneLayout = (RelativeLayout) findViewById(R.id.phoneLayout);

        editMobileNumber = (EditText) findViewById(R.id.editMobileNumber);
        editPhoneNumber = (EditText) findViewById(R.id.editPhoneNumber);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editStreet = (EditText) findViewById(R.id.editStreet);
        editStreet2 = (EditText) findViewById(R.id.editStreet2);
        editCity = (EditText) findViewById(R.id.editCity);
        editState = (EditText) findViewById(R.id.editState);
        editCountry = (EditText) findViewById(R.id.editCountry);
        editPincode = (EditText) findViewById(R.id.editPincode);
        editWebsite = (EditText) findViewById(R.id.editWebsite);
        editFax = (EditText) findViewById(R.id.editFax);

    }

    /*private class MyAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(mUtility.getMetaDataFromManifest(mConstantUnits.PBGEOMAP_ACCESS_TOKEN));

            try {
                //add data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("data", data[0]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //execute http post
                HttpResponse response = httpclient.execute(httppost);


                return "".toString();

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }

            return "".toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            //Log.i("Result",result);

        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {


        }
    }*/

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fabEdit) {

            fabEdit.setImageResource(R.drawable.ic_done_24dp);

            viewLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.VISIBLE);

            editMobileNumber.setText(stringMobileNumber.equals("false") ? "" : stringMobileNumber);
            editPhoneNumber.setText(stringPhoneNumber.equals("false") ? "" : stringPhoneNumber);
            editEmail.setText(stringEmail.equals("false") ? "" : stringEmail);
            editCity.setText(stringCity.equals("false") ? "" : stringCity);
            editStreet.setText(stringStreet.equals("false") ? "" : stringStreet);
            editStreet2.setText(stringStreet2.equals("false") ? "" : stringStreet2);
            editState.setText(stringStateId.equals("0") ? "" : stringStateId);
            editCountry.setText(stringCountryId.equals("0") ? "" : stringCountryId);
            editWebsite.setText(stringWebsite.equals("false") ? "" : stringWebsite);
            editFax.setText(stringFax.equals("false") ? "" : stringFax);
            editPincode.setText(stringPincode.equals("false") ? "" : stringPincode);

            profileImage.setClickable(true);
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });

            fabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateRecords();
                    finish();
                }
            });
        }
    }


    private void updateRecords() {

        ContentValues values = new ContentValues();

        if (editMobileNumber.getText().toString().equals("")) {
            values.put("mobile", "false");
        } else {
            values.put("mobile", editMobileNumber.getText().toString());
        }

        if (editPhoneNumber.getText().toString().equals("")) {
            values.put("phone", "false");
        } else {
            values.put("phone", editPhoneNumber.getText().toString());
        }

        if (editCity.getText().toString().equals("")) {
            values.put("city", "false");
        } else {
            values.put("city", editCity.getText().toString());
        }

        if (editStreet.getText().toString().equals("")) {
            values.put("street", "false");
        } else {
            values.put("street", editStreet.getText().toString());
        }

        if (editStreet2.getText().toString().equals("")) {
            values.put("street2", "false");
        } else {
            values.put("street2", editStreet2.getText().toString());
        }

        if (editEmail.getText().toString().equals("")) {
            values.put("email", "false");
        } else {
            values.put("email", editEmail.getText().toString());
        }

        if (editWebsite.getText().toString().equals("")) {
            values.put("website", "false");
        } else {
            values.put("website", editWebsite.getText().toString());
        }

        if (editState.getText().toString().equals("")) {
            values.put("state_id", "0");
        } else {
            //TODO: state name
            values.put("state_id", "1");
        }

        if (editCountry.getText().toString().equals("")) {
            values.put("country_id", "0");
        } else {
            //TODO: Country name
            values.put("country_id", "1");
        }

        if (editFax.getText().toString().equals("")) {
            values.put("fax", "false");
        } else {
            values.put("fax", editFax.getText().toString());
        }

        if (editPincode.getText().toString().equals("")) {
            values.put("zip", "false");
        } else {
            values.put("zip", editPincode.getText().toString());
        }

        resPartner.update(values, "_id = ? ", String.valueOf(_id));
        Toast.makeText(ContactDetailActivity.this, "Contact Updated", Toast.LENGTH_SHORT).show();
    }

    private void selectImage() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);

        try {
            intent.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(intent,
                    "Complete action using"), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = extras.getParcelable("data");
                    profileImage.setImageBitmap(bitmap);
                    stringImage = BitmapUtils.bitmapToBase64(bitmap);
                    ContentValues values = new ContentValues();
                    values.put("image_medium", stringImage);
                    resPartner.update(values, "_id = ? ", String.valueOf(_id));
                }
            } else {
                NavUtils.getParentActivityIntent(this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_call:
                if (stringMobileNumber.equals("false")) {
                    if (stringPhoneNumber.equals("false")) {
                        Toast.makeText(this, "Number not found", Toast.LENGTH_LONG).show();
                    } else {
                        callToContact(stringPhoneNumber);
                    }
                } else {
                    callToContact(stringMobileNumber);
                }
                break;

            case R.id.menu_add_contact_to_device:
                stringImage = stringImage.equals("false") ? "" : stringImage;
                stringMobileNumber = stringMobileNumber.equals("false") ? "" : stringMobileNumber;
                stringPhoneNumber = stringPhoneNumber.equals("false") ? "" : stringPhoneNumber;
                stringEmail = stringEmail.equals("false") ? "" : stringEmail;
                stringStreet = stringStreet.equals("false") ? "" : stringStreet;
                stringStreet2 = stringStreet2.equals("false") ? "" : stringStreet2;
                stringCity = stringCity.equals("false") ? "" : stringCity;
                stringCountryName = stringCountryName.equals("false") ? "" : stringCountryName;
                stringWebsite = stringWebsite.equals("false") ? "" : stringWebsite;
                stringFax = stringFax.equals("false") ? "" : stringFax;
                stringPincode = stringPincode.equals("false") ? "" : stringPincode;

                addContactToDevice(stringName, stringImage, stringMobileNumber, stringPhoneNumber, stringEmail,
                        stringStreet, stringStreet2, stringCity, stringCountryName, stringFax,
                        stringWebsite, stringPincode);

                break;

            case R.id.menu_send_message:
                if (stringMobileNumber.equals("false")) {
                    if (stringPhoneNumber.equals("false")) {
                        Toast.makeText(this, "Number not found", Toast.LENGTH_LONG).show();
                    } else {
                        sendMessage(stringPhoneNumber);
                    }
                } else {
                    sendMessage(stringMobileNumber);
                }
                break;

            case R.id.menu_send_mail:
                if (stringEmail.equals("false")) {
                    Toast.makeText(this, "Email not found", Toast.LENGTH_LONG).show();
                } else {
                    Intent mailIntent = new Intent(Intent.ACTION_SEND);
                    mailIntent.setData(Uri.parse("mailto:"));
                    mailIntent.setType("text/plain");
                    mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{stringEmail});
                    startActivity(mailIntent);
                }
                break;

            case R.id.menu_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("You want to delete contact ?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resPartner.delete("_id = ? ", String.valueOf(_id));
                        Toast.makeText(ContactDetailActivity.this, "Contact Deleted", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addContactToDevice(String stringName, String stringImage, String stringMobileNumber,
                                    String stringPhoneNumber, String stringEmail, String stringStreet,
                                    String stringStreet2, String stringCity, String stringCountryName,
                                    String stringFax, String stringWebsite, String stringPincode) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int rawContactInsertIndex = 0;

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, ARSAuthenticator.AUTH_TYPE)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, getAccount().name).build());

        // Display name
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        stringName)
                .build());

        // avatar
        if (!stringImage.equals("false") && !stringImage.isEmpty()) {
            Bitmap bitmap = BitmapUtils.getBitmapImage(this, stringImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,
                            baos.toByteArray()).build());
        }

        // Mobile number
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, stringMobileNumber)
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE).build());

        // Phone number
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, stringPhoneNumber)
                .withValue(Phone.TYPE, Phone.TYPE_HOME)
                .build());

        // Fax number
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, stringFax)
                .withValue(Phone.TYPE, Phone.TYPE_OTHER_FAX)
                .build());

        // Email
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, stringEmail)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK).build());

        // Website
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Website.URL, stringWebsite)
                .withValue(ContactsContract.CommonDataKinds.Website.TYPE,
                        ContactsContract.CommonDataKinds.Website.TYPE_HOME).build());

        // address, city, zip, county

        address = String.valueOf(new StringBuilder(stringStreet).append(", ").append(stringStreet2));
        if (stringStreet.equals("")) {
            if (stringStreet2.equals("")) {
                address = "";
            } else {
                address = stringStreet2;
            }
        } else {
            address = stringStreet;
        }

        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address)
                .withValue(Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                        stringCity)
                .withValue(Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                        stringPincode)
                //TODO : country name
                /*.withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                        stringCountryName)*/
                .build());

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, REQUEST_CODE_ASK_PERMISSIONS_WRITE_CONTACT);
                }
            } else {
                ContentProviderResult[] res = this.getContentResolver().applyBatch(
                        ContactsContract.AUTHORITY, ops);
                if (res.length > 0) {
                    final ContentProviderResult result = res[0];
                    Snackbar.make(coordinatorLayout, R.string.contact_created, Snackbar.LENGTH_LONG)
                            .setAction(R.string.label_view, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, result.uri);
                                    startActivity(intent);
                                }
                            }).show();

                }
            }
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String number) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", number);
        smsIntent.putExtra("sms_body", "");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_ASK_PERMISSIONS_SEND_SMS);
            }
        } else {
            startActivity(smsIntent);
        }
    }

    public void callToContact(String number) {
        Uri phoneCall;
        phoneCall = Uri.parse("tel:" + number);
        dial = new Intent(Intent.ACTION_CALL, phoneCall);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_ASK_PERMISSIONS_CALL_CONTACT);
            }
        } else {
            startActivity(dial);
        }
    }

    private Account getAccount() {
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(ARSAuthenticator.AUTH_TYPE);
        if (accounts.length == 1) {
            return accounts[0];
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_CALL_CONTACT:
                startActivity(dial);
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}