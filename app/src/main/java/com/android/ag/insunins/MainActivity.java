package com.android.ag.insunins;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by User on 04.03.2016.
 */
public class MainActivity extends Activity {

    public String TAG = "LOGI";

    public Button btnInstall, btnUninstall, btnCreateFile;
    public int UNINSTALL_REQUEST_CODE = 1;

    public ProgressDialog dialog;

    public CheckBoxAdapter dataAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInstall = (Button) findViewById(R.id.btnInstall);
        btnUninstall = (Button) findViewById(R.id.btnUninstall);
        btnCreateFile = (Button) findViewById(R.id.btnCreateFile);

        displayListView();

        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pathFile = "";

                ArrayList<ApkListElement> apkList = dataAdapter.apkList;
                for(int i=0;i<apkList.size();i++){
                    ApkListElement apkElm = apkList.get(i);
                    if(apkElm.isSelected()){
                        createNotification(i+1);
                        pathFile="/sdcard/agregator/"+apkElm.getApk_name();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(pathFile)), "application/vnd.android.package-archive");
                        startActivity(intent);
                        //if(flag_install)
                        //Toast.makeText(getApplicationContext(),apkElm.getApk_name()+" установлен", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnCreateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeFile();
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Создаю файл.");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                copyAssetsFile();
                dialog.dismiss();
            }
        });

        btnUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new RequestTask().execute();
                deleteFile();
            }
        });

    }


    public void createNotification(int number){
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        if(Build.VERSION.SDK_INT > 10) {
            Notification.Builder builder = new Notification.Builder(context);

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.installer)
                            // большая картинка
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.installer))
                            //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                    .setTicker("Происходит установка приложений")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                            //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                    .setContentTitle("Агрегатор работает")
                            //.setContentText(res.getString(R.string.notifytext))
                    .setContentText("Происходит установка"); // Текст уведомления

            // Notification notification = builder.getNotification(); // до API 16
            Notification notification;
            if (Build.VERSION.SDK_INT < 16)
                notification = builder.getNotification();
            else
                notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(888, notification);
        }
        else
        {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

            int icon = R.drawable.installer;
            CharSequence tickerText = "Происходит установка приложений";
            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, tickerText, when);

            CharSequence contentTitle = "Агрегатор работает";
            CharSequence contentText = "Происходит установка";

            final int HELLO_ID = 888;

            mNotificationManager.notify(HELLO_ID, notification);
        }
    }
///sdcard/agregator/

    private void displayListView() {

        //Array list of countries
        ArrayList<ApkListElement> apkList = new ArrayList<ApkListElement>();
        ApkListElement apkElement = new ApkListElement("OpenVPN+for+Android+0.6.43.apk",true);
        apkList.add(apkElement);
        apkElement = new ApkListElement("ownCloud+v1.9.0+build+10900000.apk",true);
        apkList.add(apkElement);
        apkElement = new ApkListElement("Codec.Pack.CSip.Simple_v1.7.apk",true);
        apkList.add(apkElement);
        apkElement = new ApkListElement("CSipSimple-r2456-trunk.apk",true);
        apkList.add(apkElement);
        apkElement = new ApkListElement("CSipSimpleVideoPlugin.apk",true);
        apkList.add(apkElement);
        apkElement = new ApkListElement("CSSG729-signed.apk",true);
        apkList.add(apkElement);
        apkElement = new ApkListElement("org.thoughtcrime.redphone-30.apk",true);
        apkList.add(apkElement);
        apkElement = new ApkListElement("com-xabber-android-1.0.30.apk",true);
        apkList.add(apkElement);


        //create an ArrayAdaptar from the String Array
        dataAdapter = new CheckBoxAdapter(this,
                R.layout.list_element, apkList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                ApkListElement country = (ApkListElement) parent.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(),"Clicked on Row: ",Toast.LENGTH_LONG).show();
            }
        });

    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            boolean res_flag = slientUninstall("com.owncloud.android");
            Log.d("LOGI", "res_flag: " + res_flag);

            res_flag = slientUninstall("de.blinkt.openvpn");
            Log.d("LOGI", "res_flag: " + res_flag);

            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Удаляю...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("LOGI", "onActivityResult: user accepted the (un)install");
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("LOGI", "onActivityResult: user canceled the (un)install");
            } else if (resultCode == RESULT_FIRST_USER) {
                Log.d("LOGI", "onActivityResult: failed to (un)install");
            }
        }
    }

    public boolean slientUninstall(String packageName)
    {
        Process process = null;
        try
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter pPrintWriter = new PrintWriter(process.getOutputStream());
            pPrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            pPrintWriter.println("pm uninstall "+packageName);
            pPrintWriter.flush();
            pPrintWriter.close();
            int value = process.waitFor();
            return (value == 0) ? true : false ;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false ;
    }

    public void writeFile(){
        File fileName = null;
        String sdState = android.os.Environment.getExternalStorageState();
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            fileName = new File(sdDir, "TestApp/primer.ovpn");
        } else {
            fileName = getApplicationContext().getCacheDir();
        }
        if (!fileName.exists())
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            FileWriter f = new FileWriter(fileName);
            f.write("hello world");
            f.flush();
            f.close();
        } catch (Exception e) {

        }
    }

    public class CheckBoxAdapter extends ArrayAdapter<ApkListElement> {

        public ArrayList<ApkListElement> apkList;

        public CheckBoxAdapter(Context context, int textViewResourceId,
                               ArrayList<ApkListElement> countryList) {
            super(context, textViewResourceId, countryList);
            this.apkList = new ArrayList<ApkListElement>();
            this.apkList.addAll(countryList);
        }

        private class ViewHolder {
            TextView apk_name;
            CheckBox check_text;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_element, null);

                holder = new ViewHolder();
                holder.apk_name = (TextView) convertView.findViewById(R.id.apk_name);
                holder.check_text = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.check_text.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        ApkListElement country = (ApkListElement) cb.getTag();
                        /*Toast.makeText(getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();*/
                        country.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            ApkListElement apkListElement = apkList.get(position);
            holder.apk_name.setText(apkListElement.getApk_name());
            holder.check_text.setChecked(apkListElement.isSelected());
            holder.check_text.setTag(apkListElement);

            return convertView;

        }

    }
    //
    public void copyAssetsFile()
    {
        InputStream is = null;
        FileOutputStream fos = null;
        try
        {
            //path= .\yourProject\app\src\main\assets\your.Apk
            is = MainActivity.this.getAssets().open("testvpn.ovpn");
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/testvpn.ovpn");
            file.createNewFile();
            Log.e(TAG, "Copy Assets File="+file.toString());
            fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0)
            {
                fos.write(temp, 0, i);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteFile(){
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/testvpn.ovpn");
        file.delete();
    }
}
