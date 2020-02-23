package com.example.persistencia5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int CALLBACK_INTERNET = 10;
    ReceptorXarxa receptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receptor = new ReceptorXarxa();
        this.registerReceiver(receptor, filter);
        pedirPermisos();
    }

    public void pedirPermisos() {
        int permissionCheck;
        permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // No tenim el permis. L'usuari l'ha declinat abans
            // tornam a demanar el permís
            // CALLBACK_NUMERO es una constant de tipus sencer
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, CALLBACK_INTERNET);
            // El mètode callback onRequestPermissionsResult recollirà la resposta a la sol·licitud de permís.
        }
    }

    //Respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CALLBACK_INTERNET) {// Si es cancela la petició l'aray de tornada es buit.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permís concedit
                Toast.makeText(getApplicationContext(), "Se ha concedido el permiso de internet", Toast.LENGTH_SHORT).show();

            } else {
                // permís denegat
                // Desactivar la funcionalitat relacionada amb el permís
                Toast.makeText(getApplicationContext(), "Se ha denegado el permiso de internet", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickAnar(View view) {
        WebView webView = findViewById(R.id.webView1);
        WebSettings wSettings = webView.getSettings();
        //wSettings.set
        EditText editText = findViewById(R.id.editText1);
        String dir = editText.getText().toString();
        if (!dir.startsWith("http://") && !dir.startsWith("https://")) {
            dir = "http://" + dir;
            editText.setText(dir);
        }
        webView.loadUrl(dir);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Donam de baixa el receptor de broadcast quan es destrueix l’aplicació
        if (receptor != null) {
            this.unregisterReceiver(receptor);
        }
    }

    public void comprovaConnectivitat() {
        //Obtenim un gestor de les connexions de xarxa
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Obtenim l’estat de la xarxa
        NetworkInfo networkInfo;
        //Obtenim l’estat de la xarxa mòbil
        boolean connectat4G = false;
        if (connMgr != null) {
            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                connectat4G = networkInfo.isConnected();
            }
        }

        //Obtenim l’estat de la xarxa Wifi
        boolean connectatWifi = false;
        if (connMgr != null) {
            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null) {
                connectatWifi = networkInfo.isConnected();
            }
        }
        //Si està connectat
        if (connectat4G) {
            //Xarxa OK
            Toast.makeText(this, "Connectat per 4G", Toast.LENGTH_LONG).show();
        }
        if (connectatWifi) {
            //Xarxa no disponible
            Toast.makeText(this, "Connectat per Wi-Fi", Toast.LENGTH_LONG).show();
        } else {
            //Xarxa no disponible
            Toast.makeText(this, "Xarxa no disponible", Toast.LENGTH_LONG).show();
        }
    }

    class ReceptorXarxa extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Actualitza l'estat de la xarxa
            comprovaConnectivitat();
        }
    }

}
