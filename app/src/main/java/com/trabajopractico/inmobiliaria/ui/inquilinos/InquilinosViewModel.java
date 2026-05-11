package com.trabajopractico.inmobiliaria.ui.inquilinos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InquilinosViewModel extends ViewModel {

    private final MutableLiveData<String> texto;

    public InquilinosViewModel() {
        texto = new MutableLiveData<>();
        texto.setValue("Listado de Inquilinos");
    }

    public LiveData<String> getTexto() {
        return texto;
    }
}
