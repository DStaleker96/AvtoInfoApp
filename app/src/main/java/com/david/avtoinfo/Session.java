package com.david.avtoinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by David on 21.02.2017.
 */

public class Session {
    private Context context;
    private SharedPreferences preferences;
    public Session(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    void Prijava(int id, String Upime, String Ime, String Priimek, boolean Admin, String bazaIme, String sqlIp){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Nastavitve.TAGidup,id);
        editor.putString(Nastavitve.TAGupime,Upime);
        editor.putString(Nastavitve.TAGime,Ime);
        editor.putString(Nastavitve.TAGpriimek,Priimek);
        editor.putBoolean(Nastavitve.TAGadmin,Admin);
        editor.putString(Nastavitve.TAGbazaime,bazaIme);
        editor.putString(Nastavitve.TAGpriimek,sqlIp);
        editor.commit();
    }

}
