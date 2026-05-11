package com.trabajopractico.inmobiliaria.ui.logout;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.trabajopractico.inmobiliaria.request.ApiClient;

public class LogoutViewModel extends ViewModel {

    private MutableLiveData<Boolean> cerrarSesion;

    public LiveData<Boolean> getCerrarSesion() {
        if (cerrarSesion == null) {
            cerrarSesion = new MutableLiveData<>();
        }
        return cerrarSesion;
    }

    public void cerrarSesion(Context context) {
        // Borrar el token guardado y avisar al fragment que puede navegar al Login
        ApiClient.borrarToken(context);
        cerrarSesion.setValue(true);
    }
}