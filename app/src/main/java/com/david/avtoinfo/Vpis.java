package com.david.avtoinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class Vpis extends AppCompatActivity {

    Context context = Vpis.this;
    TextView tvNapaka;
    EditText etUpIme;
    EditText etGeslo;
    Button btVpis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpis);
        nastaviKomponente();

    }

    void nastaviKomponente(){
        tvNapaka = (TextView) findViewById(R.id.tvNapaka);
        etUpIme = (EditText) findViewById(R.id.etUporabnik);
        etGeslo = (EditText) findViewById(R.id.etGeslo);
        btVpis = (Button) findViewById(R.id.btVpis);

        btVpis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(etUpIme.getText().toString(), etGeslo.getText().toString());
            }
        });
    }

    private void attemptLogin(final String upIme, final String geslo) {

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Vpisujem...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        btVpis.setEnabled(false);
        Thread mThread = new Thread() {
            @Override
            public void run() {


                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                String SOAP_ACTION = preferences.getString(Nastavitve.TAGnamespace, "http://tempuri.org/") + "UporabnikLogin";
                String OPERATION_NAME = "UporabnikLogin";
                String NAMESPACE = preferences.getString(Nastavitve.TAGnamespace, "http://tempuri.org/");
                String URL = preferences.getString(Nastavitve.TAGservice, "http://localhost/Service/WebService1.asmx");
                Object response = null;
                try {
                    SoapObject request = new SoapObject(NAMESPACE, OPERATION_NAME);
                    PropertyInfo pi = new PropertyInfo();
                    pi.setName("uIme");
                    pi.setValue(upIme);
                    pi.setType(String.class);
                    request.addProperty(pi);

                    pi = new PropertyInfo();
                    pi.setName("geslo");
                    pi.setValue(geslo);
                    pi.setType(String.class);
                    request.addProperty(pi);

                    pi = new PropertyInfo();
                    pi.setName("mode");
                    pi.setValue(1);
                    pi.setType(Integer.class);
                    request.addProperty(pi);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    envelope.dotNet = true;

                    envelope.setOutputSoapObject(request);

                    HttpTransportSE httpTransport = new HttpTransportSE(URL, Integer.parseInt(preferences.getString(Nastavitve.TAGtimeout,"1000")));


                    httpTransport.call(SOAP_ACTION, envelope);
                    response = envelope.getResponse();
                } catch (Exception exception) {
                    response = exception.toString();
                }

                final String result = response.toString();
                if (result.startsWith("{") && result.length()>3) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int id = jsonObject.optInt("id");
                        String UpIme = jsonObject.optString("UpIme");
                        String Ime = jsonObject.optString("Ime");
                        String Priimek = jsonObject.optString("Priimek");
                        String dbIme = jsonObject.getString("dbIme");
                        String dbIp = jsonObject.getString("dbIp");
                        boolean admin = jsonObject.getBoolean("admin");
                        Session s = new Session(context);
                        s.Prijava(id,UpIme,Ime,Priimek,admin,dbIme,dbIp);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // dispaly toast here;
                                Toast.makeText(context, "Uspesno",Toast.LENGTH_LONG).show();
                                btVpis.setEnabled(true);
                            }
                        });
                    }
                    catch (final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // dispaly toast here;
                                btVpis.setEnabled(true);

                                Toast.makeText(context, e.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
                else if (result.equals("UpNeObst")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, R.string.nav_napacna_prijava,Toast.LENGTH_SHORT).show();
                            btVpis.setEnabled(true);
                        }
                    });

                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // dispaly toast here;
                            btVpis.setEnabled(true);
                            Toast.makeText(context, result,Toast.LENGTH_LONG).show();
                        }
                    });
                }
                pd.dismiss();
            }
        };
        mThread.start();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        ///// TODO: 21.02.2017 zamenjaj ikono menuja
        inflater.inflate(R.menu.menu_vpis, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.menu_nastavitve:
                //startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                startActivityForResult(new Intent(Vpis.this, Nastavitve.class), 0);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
