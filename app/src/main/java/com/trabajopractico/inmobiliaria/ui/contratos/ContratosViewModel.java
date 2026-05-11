package com.trabajopractico.inmobiliaria.ui.contratos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ContratosViewModel extends ViewModel {

    private final MutableLiveData<String> texto;

    public ContratosViewModel() {
        texto = new MutableLiveData<>();
        texto.setValue("Contratos Vigentes");
    }

    public LiveData<String> getTexto() {
        return texto;
    }
}
