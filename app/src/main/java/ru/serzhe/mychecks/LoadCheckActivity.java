package ru.serzhe.mychecks;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ru.serzhe.mychecks.objects.Check;
import ru.serzhe.mychecks.objects.CheckItem;

public class LoadCheckActivity extends AppCompatActivity {

    TextView textView;
    ProgressBar progressBar;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_check);

        textView = (TextView) findViewById(R.id.loadCheckTextView);
        progressBar = (ProgressBar) findViewById(R.id.loadCheckProgressBar);

        Intent intent = getIntent();
        if (intent.getAction().equals("android.intent.action.VIEW")) {
            filePath = intent.getData().getPath();
        }
        else
            return;

        if (hasPermissions()){
            // our app has permissions.
            new LoadCheckTask(this).execute(filePath);

        }
        else {
            //our app doesn't have permissions, So i m requesting permissions.
            requestPermissionWithRationale();
        }
    }

    private boolean hasPermissions(){
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode){
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed){
            //user granted all permissions we can perform our task.
            new LoadCheckTask(this).execute(filePath);
        }
        else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();

                } else {
                    showNoStoragePermissionSnackbar();
                }
            }
        }

    }

    public void showNoStoragePermissionSnackbar() {
        Snackbar.make(LoadCheckActivity.this.findViewById(R.id.activity_load_check), "Storage permission isn't granted" , Snackbar.LENGTH_LONG)
                .setAction("SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(getApplicationContext(),
                                "Open Permissions and grant the Storage permission",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            new LoadCheckTask(this).execute(filePath);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            Snackbar.make(LoadCheckActivity.this.findViewById(R.id.activity_load_check), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();
                        }
                    })
                    .show();
        } else {
            requestPerms();
        }
    }

    private class LoadCheckTask extends AsyncTask<String, Integer, Integer> {

        private Context mContext;

        public LoadCheckTask (Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute(){
            textView.setText("Loading...");
        }

        @Override
        protected Integer doInBackground(String... path) {
            int cntLoaded = 0;
            try {
                FileInputStream fis = new FileInputStream(path[0]);
                JsonReader reader = new JsonReader(new InputStreamReader(fis, "UTF-8"));
                Check chk;
                CheckItem chkItem;
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                String name;
                reader.beginArray();
                while (reader.hasNext()) {
                    chk = new Check(mContext);
                    chk.items = new ArrayList<>();
                    reader.beginObject();
                    reader.nextName();
                    reader.nextString();
                    reader.nextName();
                    reader.nextString();
                    reader.nextName();
                    reader.beginObject();   //ticket
                    reader.nextName();
                    reader.beginObject();   //document
                    reader.nextName();
                    reader.beginObject();   //receipt
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("ecashTotalSum")) {
                            chk.ecashTotalSum = (float) reader.nextDouble() / 100;
                        } else if (name.equals("kktRegId")) {
                            chk.kktRegId = reader.nextString();
                        } else if (name.equals("receiptCode")) {
                            chk.receiptCode = reader.nextInt();
                        } else if (name.equals("fiscalDocumentNumber")) {
                            chk.fiscalDocumentNumber = reader.nextInt();
                        } else if (name.equals("operationType")) {
                            chk.operationType = reader.nextInt();
                        } else if (name.equals("totalSum")) {
                            chk.totalSum = (float) reader.nextDouble() / 100;
                        } else if (name.equals("operator")) {
                            chk.operator = reader.nextString();
                        } else if (name.equals("fiscalDriveNumber")) {
                            chk.fiscalDriveNumber = reader.nextString();
                        } else if (name.equals("dateTime")) {
                            chk.DT = format.parse(reader.nextString());
                        } else if (name.equals("user")) {
                            chk.seller = reader.nextString();
                        } else if (name.equals("shiftNumber")) {
                            chk.shiftNumber = reader.nextInt();
                        } else if (name.equals("fiscalSign")) {
                            chk.fiscalSign = reader.nextString();
                        } else if (name.equals("taxationType")) {
                            chk.taxationType = reader.nextInt();
                        } else if (name.equals("userInn")) {
                            chk.sellerInn = reader.nextString();
                        } else if (name.equals("cashTotalSum")) {
                            chk.cashTotalSum = (float) reader.nextDouble() / 100;
                        } else if (name.equals("requestNumber")) {
                            chk.requestNumber = reader.nextInt();
                        } else if (name.equals("nds18")) {
                            chk.nds18 = (float) reader.nextDouble() / 100;
                        } else if (name.equals("nds10")) {
                            chk.nds10 = (float) reader.nextDouble() / 100;
                        } else if (name.equals("retailPlaceAddress")) {
                            chk.retailPlaceAddress = reader.nextString();
//                    } else if (name.equals("modifiers") || name.equals("stornoItems")) {
//                        reader.beginArray();
//                        reader.endArray();
                        } else if (name.equals("items")) {
                            reader.beginArray();
                            while (reader.hasNext()) {
                                chkItem = new CheckItem();
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    String itemName = reader.nextName();
                                    if (itemName.equals("sum")) {
                                        chkItem.sum = (float) reader.nextDouble() / 100;
                                    } else if (itemName.equals("name")) {
                                        chkItem.product = reader.nextString();
                                    } else if (itemName.equals("price")) {
                                        chkItem.price = (float) reader.nextDouble() / 100;
                                    } else if (itemName.equals("quantity")) {
                                        chkItem.quantity = (float) reader.nextDouble();
                                    } else if (itemName.equals("nds18")) {
                                        chkItem.nds18 = (float) reader.nextDouble() / 100;
                                    } else if (itemName.equals("nds10")) {
                                        chkItem.nds10 = (float) reader.nextDouble() / 100;
                                    } else {
                                        reader.skipValue();
                                    }
                                }
                                reader.endObject();
                                chk.items.add(chkItem);
                            }
                            reader.endArray();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    reader.endObject();   //document
                    reader.endObject();   //receipt
                    chk.saveToSQL();
                    publishProgress(cntLoaded++);
                    //Thread.sleep(500);
                }
                reader.endArray();
                reader.close();
            }
            catch (Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.d("LOAD_CHECKS", e.getMessage());
            }
            return cntLoaded;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            textView.setText("Loading completed!");
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            //Toast.makeText(mContext, "Loading completed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            textView.setText("Loaded... " + progress[0]);
            //Toast.makeText(mContext, "Loaded... "+progress[0], Toast.LENGTH_SHORT).show();
        }

    }
}
