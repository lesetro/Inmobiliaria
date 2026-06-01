package com.trabajopractico.inmobiliaria.ui.logout;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.trabajopractico.inmobiliaria.request.ApiClient;

public class LogoutViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> cerrarSesion;
    public LogoutViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getCerrarSesion() {
        if (cerrarSesion == null) {
            cerrarSesion = new MutableLiveData<>();
        }
        return cerrarSesion;
    }

    public void cerrarSesion() {
        // Borrar el token guardado y avisar al fragment que puede navegar al Login
        ApiClient.borrarToken(getApplication());
        cerrarSesion.setValue(true);
    }
}