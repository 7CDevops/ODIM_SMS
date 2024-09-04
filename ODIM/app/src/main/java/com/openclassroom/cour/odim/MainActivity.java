package com.openclassroom.cour.odim;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.openclassroom.cour.odim.utils.FileChooser;
import com.openclassroom.cour.odim.utils.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity  {


    private static final int FILECHOOSER_REQUEST_CODE = 785;

    private Button btnSend;
    private Button btnSave;
    private String file;
    private ArrayList<Cible> cibleList;
    private ArrayList<Cible> a ;

    public final String SENT = "sent";
    public final String DELIVERED = "delivered";

    public TextView txtSuccessCount;
    public TextView txtTransmissionFailed;
    public TextView txtTransmissionSuccess;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    private EditText etTemp;
    ProgressDialog barreProgress;
    private int idContact = 0;


    //pour les demandes de permissions
    private final int MES_PERMISSIONS = 123;
    private static String[] PERMISSIONS_ALL = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS};

    public void onResume() {
        super.onResume();
        smsSentReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {

                // TODO Auto-generated method stub
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS has been sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic Failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No Service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio Off", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }
        };
        smsDeliveredReceiver=new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // barreProgress.setProgress(idContact+1);

                //dès que la progession est au max on fait disparaitre la barre
               /* if( barreProgress.getProgress() == barreProgress.getMax()){
                    barreProgress.dismiss();
                }*/
                // TODO Auto-generated method stub
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS Delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(smsDeliveredReceiver , new IntentFilter("SMS_DELIVERED"));
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    /**
     * Controle des permissions afin d'afficher popup à l'utilisateur
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MES_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // initialisation des permissions demandées
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                // passage en revue des permissions
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // si l'ensemble des permisssions sont autorisées on lance l'application
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    lanceAppli();
                } else {
                    // Permissions refusées
                    Toast.makeText(MainActivity.this, "Des permissions ont été refusées", Toast.LENGTH_SHORT)
                            .show();
                    super.onDestroy();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     *  1
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String filePath = Environment.getExternalStorageDirectory() + "/logcat" + new SimpleDateFormat("yyyyMMdd'.txt'").format(new Date());
        try {
            //Runtime.getRuntime().exec(new String[]{"logcat", "-f", filePath, "ODIM:V", "*:S"});
            Runtime.getRuntime().exec(new String[]{"logcat", "-f", filePath});
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("ODIM SUCCESS", "OnCreate");
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        //ajout des différentes permissions pour le controle
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Accès stockage");
        if (!addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("Envoi SMS");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // message demande d'autoriser un accès
                String message = "Vous devez autoriser l'accès à " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        (dialog, which) -> requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                MES_PERMISSIONS));
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    MES_PERMISSIONS);
            return;
        }
        lanceAppli();
    }

    /**
     * permet de lancer l'application suite aux demandes d'autorisations
     */
    private void lanceAppli(){
        setContentView(R.layout.activity_main);

        txtSuccessCount = findViewById(R.id.txt_success);
        txtTransmissionFailed =  findViewById(R.id.txt_transmission_failed);
        txtTransmissionSuccess =  findViewById(R.id.txt_transmission_success);
        cibleList = new ArrayList<Cible>();
        btnSend =  findViewById(R.id.btn_send);
        btnSend.setAlpha(0);
        btnSave =  findViewById(R.id.btn_save);
        btnSave.setAlpha(0);
        etTemp = findViewById(R.id.etTemp);

        btnSaveClick();

        Toast.makeText(this, Environment.getExternalStorageDirectory().toString(), Toast.LENGTH_LONG).show();
    }

    //affichage message ok cancel
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * ajout des permissions dans la liste
     * @param permissionsList
     * @param permission
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private boolean addPermission(List<String> permissionsList, String permission) {
        //check des permissions autorisées
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    /**
     *  2
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        super.onCreateOptionsMenu(menu);
        MenuInflater infl = getMenuInflater();
        infl.inflate(R.menu.main, menu);

        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            int end = spanString.length();
            spanString.setSpan(new AbsoluteSizeSpan(24,true), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(spanString);

        }
        //on ajoute les deux boutons configurés
        //getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /**
     *  3
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.load_file) {
            //Je start mon activiyé en utilisant la méthode startActivityForResult qui spécifie qu'un retour est attendu à la fin de l'activité.
            // Ce résultat est le fichier importé.
            startActivityForResult(new Intent(getBaseContext(),FileChooser.class),FILECHOOSER_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * Arraylist des cibles
     * @param arStr2
     * @return
     */
    public ArrayList<Cible> populateCible(ArrayList<String> arStr2){
        ArrayList<Cible> tempCibleList = new ArrayList<Cible>();
        System.out.println("total=="+arStr2.size());
        for (int i = 0; i < arStr2.size(); i++) {
            String[] elements = arStr2.get(i).split("\t");
            System.out.println(elements.length);
            if (elements.length ==2) {
                String number = Html.fromHtml(elements[0]).toString();
                String msg = Html.fromHtml(elements[1]).toString();
                System.out.println("ajout num=="+number);
                System.out.println("ajout msg=="+msg);
                Cible currentCible = new Cible(number, msg);
                tempCibleList.add(currentCible);
            }
        }
        Log.d("ODIM SUCCESS", "Ajout de " + tempCibleList.size() + " contacts avec succes");
        return tempCibleList;
    }


    /**
     * pour l'import du fichier des contacts
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (FILECHOOSER_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            // Fetch the score from the Intent

            file = data.getStringExtra(FileChooser.BUNDLE_EXTRA_FILE);
        }

        if (file != null && !file.isEmpty()) {
            btnSave.setAlpha(0);
            if (file.contains("bomb") || file.contains("txt")) {
                try {
                    btnSend.setAlpha(1);
                    ValidateToast.show(this, "Import effectué ", false);
                    Log.d("ODIM SUCESS", "import du fichier txt ok");

                } catch (Exception e) {
                    Log.d("ODIM CRASH", "echec import du fichier txt");
                    System.out.println(e);
                }
            }
        } else {
            Log.d("ODIM CRASH", "fichier txt vide");
            CautionToast.show(this, "Le fichier importé est vide", false);
        }
    }

    /**
     * clic sur bouton rapport
     */
    private void btnSaveClick() {
        btnSave.setOnClickListener(arg0 -> {
            Log.d("ODIM SUCCESS", "click sur rapport");
            Boolean success=true;

            //DONE: generer nom de fichier
            String filename = "report-" + new SimpleDateFormat("yyyyMMddhhmmss'.txt'").format(new Date());
            for (int i = 0; i < cibleList.size(); i++) {
                Utils.writeData(Environment.getExternalStorageDirectory() +"/"+ filename, cibleList.get(i).toString(), true);
            }
            try {
                Log.d("ODIM SUCCESS", "écriture du rapport ok");
                // Utils.writeData(where + filename, Utils.getFileContentRapport(where + filename), false);
                writeIntoFile(MainActivity.this,Environment.getExternalStorageDirectory() +"/"+ filename,Utils.getFileContentRapport(Environment.getExternalStorageDirectory() +"/"+ filename));
                Toast.makeText(MainActivity.this, Environment.getExternalStorageDirectory() +"/"+ filename, Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "Le rapport a été généré dans le stockage interne /Android/data/com.dev.odim/", Toast.LENGTH_LONG)
                        .show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btnSend.setAlpha(0);
        });
    }
    public void writeIntoFile(Context context, String fileName, String content) throws IOException {
//        File appSpecificExternalStorageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File appSpecificInternalStorageDirectory = context.getFilesDir();
        File file = new File(appSpecificInternalStorageDirectory, fileName);
        Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_LONG)
                .show();
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file, false);
        fos.write(content.getBytes());
        fos.close();
    }

    /*
     *public void writeIntoFile(Context context, String fileName, String content) throws IOException {
//        File appSpecificExternalStorageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File appSpecificInternalStorageDirectory = context.getFilesDir();
        File file = new File(appSpecificInternalStorageDirectory, fileName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file, false);
        fos.write(content.getBytes());
        fos.close();
    }
     */

    /**
     * clic sur bouton envoi sms
     * @param view
     * @throws InterruptedException
     */
    public void btnSendClick(View view) throws InterruptedException {
        idContact = 0;
        btnSend.setAlpha(0);
        long timer = 1000;


        try {
            String str = "";
            str = Utils.getFileContent(file);
            String[] elements = str.split("\n");
            ArrayList<String> arStr2 = new ArrayList<String>();
            for (int i = 0; i < elements.length; i++) {
                arStr2.add(elements[i] + "\n");
            }
            cibleList = populateCible(arStr2);

        }catch (IOException e) {
            Log.d("ODIM CRASH", "clic sur envoi messages : " + e);
            System.out.println(e);
        }catch (NullPointerException e){
            Log.d("ODIM CRASH", "clic sur envoi messages : " + e);
            System.out.println(e);
        }


        //recup temporisation
        String tempo = etTemp.getText().toString();

        if (!tempo.equals("")){
            timer = Integer.parseInt(tempo) * 1000;
        }

        //temporisation envoi sms
        for (int i = 0; i < cibleList.size(); i++) {
            int index = i;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("ODIM","Try sending sms");
                        sendSms(index);
                    }catch(Exception e){
                        Log.d("ODIM CRASH", "echec envoi sms : " + e);
                    }

                }
            },(i+1) * timer);
        }
        btnSave.setAlpha(1);
    }

    /**
     *
     * @param i index contact
     */
    private void sendSms(int i){
        idContact = i;
        //recup de la cible
        Cible c = a.get(i);

        //intent de l'envoi
        Intent sentIntent = new Intent(SENT);
        sentIntent.putExtra("num", c.getPhoneNumber());

        PendingIntent sentPI = PendingIntent.getBroadcast(
                getApplicationContext(), i, sentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);



        //intent de la reception


        Intent deliveryIntent = new Intent(DELIVERED);
        deliveryIntent.putExtra("num", c.getPhoneNumber());

        PendingIntent deliveryPI = PendingIntent.getBroadcast(
                getApplicationContext(), i, deliveryIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        ArrayList<PendingIntent> deliverList = new ArrayList<>();
        deliverList.add(deliveryPI);


        registerReceiver(new BroadcastReceiver() {
            /**
             * suivant le resultat de la reception
             * @param context
             * @param intent
             */
            @Override
            public void onReceive(Context context, Intent intent) {
                String result = "";

                //progress de la barre
                //barreProgress.setProgress(idContact+1);

                //dès que la progession est au max on fait disparaitre la barre
               /* if(
               .getProgress() == barreProgress.getMax()){
                    barreProgress.dismiss();
                }*/

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

                tryThis(result+"/"+intent.getStringExtra("num"));
            }
        }, new IntentFilter(SENT));
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                tryThis(DELIVERED+"/"+intent.getStringExtra("num"));
            }

        }, new IntentFilter(DELIVERED));

        //envoi des messages avec smsmanager
        SmsManager smsManager = SmsManager.getDefault();
        Log.d("ODIM","Passage envoi message");
        smsManager.sendTextMessage(c.getPhoneNumber(), null,Html.fromHtml(c.getMsg()).toString() , sentPI,deliveryPI);
    }


    /**
     * traitement des recpetions pour afficher si message recu ou echec
     * @param string
     */
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
            txtSuccessCount.setText("Messages Reçus : "+deliveredCount);
            txtTransmissionFailed.setText("Echec d'envoi : "+failedCount);
            txtTransmissionSuccess.setText("Messages envoyés : "+transSuccessCount);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}