package com.openclassroom.cour.odim;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.openclassroom.cour.odim.utils.AESUtil;
import com.openclassroom.cour.odim.utils.FileChooser;
import com.openclassroom.cour.odim.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.os.Environment.getExternalStorageDirectory;


public class MainActivity extends AppCompatActivity  {


    private static final int FILECHOOSER_REQUEST_CODE = 785;

    private Button btnSend;
    private Button btnSave;
    private String file;
    private PopupWindow popupMessage;
    private ArrayList<Cible> cibleList;

    public final String SENT = "sent";
    public final String DELIVERED = "delivered";
    private String passwdSession;

    public TextView txtSuccessCount;
    public TextView txtTransmissionFailed;
    public TextView txtTransmissionSuccess;

    private EditText etTemp;
    private CheckBox cbTempo;

    /**
     *  1
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSuccessCount = findViewById(R.id.txt_success);
        txtTransmissionFailed =  findViewById(R.id.txt_transmission_failed);
        txtTransmissionSuccess =  findViewById(R.id.txt_transmission_success);

        cibleList = new ArrayList();

        btnSend =  findViewById(R.id.btn_send);

        cbTempo = findViewById(R.id.cbTempo);
        etTemp = findViewById(R.id.etTemp);
        etTemp.setAlpha(0);

        cbTempo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    etTemp.setAlpha(1);
                }else {
                    etTemp.setAlpha(0);
                }
            }
        });

        btnSaveClick();

        passwdSession = "";
        Toast.makeText(this, Environment.getExternalStorageDirectory().toString(), Toast.LENGTH_LONG).show();
    }

    /**
     *  2
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //on ajoute les deux boutons configurés
        //getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    // ----
    /**
     *  3
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.load_file) {
            //Je start mon activiyé en utilisant la méthode startActivityForResult qui spécifie qu'un retour est attendu à la fin de l'activité.
            // Ce résultat est la fichier importé.
            startActivityForResult(new Intent(getBaseContext(),FileChooser.class),FILECHOOSER_REQUEST_CODE);
        }
    return super.onOptionsItemSelected(item);

    }

    public ArrayList<Cible> populateCible(ArrayList<String> arStr2){
        ArrayList<Cible> tempCibleList = new ArrayList<Cible>();
        for (int i = 0; i < arStr2.size(); i++) {
            String[] elements = arStr2.get(i).split("\t");
            Cible currentCible = new Cible(elements[0], elements[1]);
            tempCibleList.add(currentCible);
        }
        return tempCibleList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (FILECHOOSER_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            // Fetch the score from the Intent
            file = data.getStringExtra(FileChooser.BUNDLE_EXTRA_FILE);
        }

        if (!file.isEmpty()) {
            if (file.contains("bomb") || file.contains("txt")) {
                try {
                    //popupInit();
                    btnSend.setAlpha(1);
                    ValidateToast.show(this, "Import effectué ",false);

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }else {
            CautionToast.show(this, "Le fichier importé est vide",false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        SharedPreferences prefs = getSharedPreferences("bombers", Context.MODE_PRIVATE);
        file = prefs.getString("file", "");
        if (!file.isEmpty()) {
            if (file.contains("bomb") || file.contains("txt")) {
                try {
                    //popupInit();
                    btnSend.setAlpha(1);
                    ValidateToast.show(this, "Import effectué",false);

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }else {
            CautionToast.show(this, "Le fichier importé est vide",false);
        }
        */


    }

    private void btnSaveClick() {
        btnSave =  findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                //DONE: Ecrire les data sur fichiers
                //String root  = Utils.whichDir(getString(R.string.directory1), getString(R.string.directory2),getString(R.string.directory3)).getAbsolutePath();
                String root = getExternalStorageDirectory().getAbsolutePath();
                String where = root+"/Android/data/com.dev.bombers/";

                Boolean success=true;
                File f = new File(where);
                if (!f.exists()) {
                    success=f.mkdirs();
                }

                if (success) {

                    //DONE: generer nom de fichier
                    String filename = "report-" + new SimpleDateFormat("yyyyMMddhhmmss'.txt'").format(new Date());
                    for (int i = 0; i < cibleList.size(); i++) {
                        Utils.writeData(where + filename, cibleList.get(i).toString(), true);
                    }

                    //DONE: Chiffrer les datas
                    /**
                     * Plus de chiffrement
                     */
                    try {
                        Utils.writeData(where + filename, Utils.getFileContent(where + filename), false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void btnSendClick(View view) {

                if(cbTempo.isChecked() && etTemp.getText().length() > 0){

                    ComponentName componentName = new ComponentName(this,JobServiceTempo.class);
                    JobInfo info = new JobInfo.Builder(123,componentName)
                            .setRequiresCharging(true)
                            .setPersisted(true)
                            .setPeriodic(Integer.parseInt(etTemp.getText().toString())*1000)
                            .build();


                    JobScheduler job = (JobScheduler) getApplicationContext().getSystemService(JobScheduler.class);
                    int result = job.schedule(info);

                    if(result == JobScheduler.RESULT_SUCCESS){
                        System.out.println("Great success");

                    }else{
                        System.out.println("BADDDDD");
                    }
                }else {
                    System.out.println("Pas de tempo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }







                try {
                    String str = "";
                    str = Utils.getFileContent(file);
                     //DONE: mettre le claire dans cibleList
                    String[] elements = str.split("\n");
                    ArrayList<String> arStr2 = new ArrayList<String>();
                    for (int i = 0; i < elements.length; i++) {
                        arStr2.add(elements[i] + "\n");
                    }
                    //DONE: populateCible();
                    cibleList = populateCible(arStr2);

                }catch (IOException e) {
                    System.out.println(e);
                }catch (NullPointerException e){
                    System.out.println(e);
                }

                ArrayList<Cible> a = cibleList;




                for (int i = 0; i < a.size(); i++) {
                    Cible c = a.get(i);

                    Intent sentIntent = new Intent(SENT);
                    sentIntent.putExtra("num", c.getPhoneNumber());


                    PendingIntent sentPI = PendingIntent.getBroadcast(
                            getApplicationContext(), i, sentIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    Intent deliveryIntent = new Intent(DELIVERED);
                    deliveryIntent.putExtra("num", c.getPhoneNumber());

                    PendingIntent deliverPI = PendingIntent.getBroadcast(
                            getApplicationContext(), i, deliveryIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    registerReceiver(new BroadcastReceiver() {


                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String result = "";

                            switch (getResultCode()) {

                                case Activity.RESULT_OK:
                                    result = "Transmission successful";
                                    break;
                                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                    result = "Transmission failed";
                                    break;
                                case SmsManager.RESULT_ERROR_RADIO_OFF:
                                    result = "Radio off";
                                    break;
                                case SmsManager.RESULT_ERROR_NULL_PDU:
                                    result = "No PDU defined";
                                    break;
                                case SmsManager.RESULT_ERROR_NO_SERVICE:
                                    result = "No service";
                                    break;
                            }

                            //Toast.makeText(getApplicationContext(), result,Toast.LENGTH_SHORT).show();
                            //Log.d("Sys Acqu SMS : ", result+"/"+intent.getStringExtra("num"));
                            tryThis(result+"/"+intent.getStringExtra("num"));
                        }
                    }, new IntentFilter(SENT));
                    registerReceiver(new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            //Toast.makeText(getApplicationContext(), "delivered",Toast.LENGTH_SHORT).show();
                            //Log.d("Sys Acqu SMS : ", DELIVERED+"/"+intent.getStringExtra("num"));
                            tryThis(DELIVERED+"/"+intent.getStringExtra("num"));
                        }

                    }, new IntentFilter(DELIVERED));
                    //======================================================================//

                    //======================================================================//

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(c.getPhoneNumber(), null, c.getMsg(), sentPI,deliverPI);

                }




    }


    public void tryThis(String string){
        String[] elements = string.split("/");
        if (string.contains("delivered")) {
            for (int i = 0; i < cibleList.size(); i++) {
                if (cibleList.get(i).getPhoneNumber().equals(elements[1])) {
                    cibleList.get(i).setDelivered(true);
                }
            }
        }else {
            for (int i = 0; i < cibleList.size(); i++) {
                if (cibleList.get(i).getPhoneNumber().equals(elements[1])) {
                    cibleList.get(i).setTransmissionStatus(elements[0]);
                }
            }
        }
        //===============================
        //Print Status

        int deliveredCount = 0;
        int failedCount = 0;
        int transSuccessCount = 0;

        for (int i = 0; i < cibleList.size(); i++) {
            if (cibleList.get(i).isDelivered()) {
                deliveredCount++;
            }
            if (cibleList.get(i).getTransmissionStatus().contains("successful")) {
                transSuccessCount++;

            } else if(cibleList.get(i).getTransmissionStatus().contains("failed")){
                failedCount++;
            }
        }
        txtSuccessCount.setText("Messages Reçus : "+deliveredCount);
        txtTransmissionFailed.setText("Echec d'envoi : "+failedCount);
        txtTransmissionSuccess.setText("Messages envoyés : "+transSuccessCount);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences prefs = getSharedPreferences("bombers", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        android.os.Process.killProcess(android.os.Process.myPid());
    }
}