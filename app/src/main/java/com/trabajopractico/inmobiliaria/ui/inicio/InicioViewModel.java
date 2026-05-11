package com.trabajopractico.inmobiliaria.ui.inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InicioViewModel extends ViewModel {

    private final MutableLiveData<String> texto;

    public InicioViewModel() {
        texto = new MutableLiveData<>();
        texto.setValue("Inicio - Ubicación de la Inmobiliaria");
    }

    public LiveData<String> getTexto() {
        return texto;
    }
}
