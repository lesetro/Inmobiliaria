package com.trabajopractico.inmobiliaria.ui.inmuebles;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InmueblesViewModel extends ViewModel {

    private final MutableLiveData<String> texto;

    public InmueblesViewModel() {
        texto = new MutableLiveData<>();
        texto.setValue("Listado de Inmuebles");
    }

    public LiveData<String> getTexto() {
        return texto;
    }
}
