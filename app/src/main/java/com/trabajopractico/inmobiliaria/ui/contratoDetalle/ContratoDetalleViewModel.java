package com.trabajopractico.inmobiliaria.ui.contratoDetalle;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.trabajopractico.inmobiliaria.modelo.Contrato;
import com.trabajopractico.inmobiliaria.modelo.Inmueble;
import com.trabajopractico.inmobiliaria.request.ApiClient;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContratoDetalleViewModel extends AndroidViewModel {

    private MutableLiveData<Contrato> contratoMutable;
    private MutableLiveData<Boolean> botonPagosHabilitado;
    private MutableLiveData<Integer> idContratoParaNavegar;
    private MutableLiveData<String> nombreInquilinoMutable;
    private MutableLiveData<String> montoAlquilerMutable;
    private MutableLiveData<String> direccionInmuebleMutable;

    public ContratoDetalleViewModel(@NonNull Application application) {

        super(application);
    }

    public LiveData<Contrato> getContratoMutable() {
        if (contratoMutable == null) {
            contratoMutable = new MutableLiveData<>();
        }
        return contratoMutable;
    }

    public LiveData<Boolean> getBotonPagosHabilitado() {
        if (botonPagosHabilitado == null) {
            botonPagosHabilitado = new MutableLiveData<>(false);
        }
        return botonPagosHabilitado;
    }

    public LiveData<Integer> getIdContratoParaNavegar() {
        if (idContratoParaNavegar == null) idContratoParaNavegar = new MutableLiveData<>();
        return idContratoParaNavegar;
    }

    public LiveData<String> getNombreInquilinoMutable() {
        if (nombreInquilinoMutable == null) nombreInquilinoMutable = new MutableLiveData<>();
        return nombreInquilinoMutable;
    }

    public LiveData<String> getMontoAlquilerMutable() {
        if (montoAlquilerMutable == null) montoAlquilerMutable = new MutableLiveData<>();
        return montoAlquilerMutable;
    }

    public LiveData<String> getDireccionInmuebleMutable() {
        if (direccionInmuebleMutable == null) direccionInmuebleMutable = new MutableLiveData<>();
        return direccionInmuebleMutable;
    }

    // El Fragment llama esto cuando el usuario toca PAGOS.
    // El VM decide si navegar (si tiene contrato) y emite el id.
    public void solicitarNavegacionAPagos() {
        Contrato contrato = contratoMutable != null ? contratoMutable.getValue() : null;
        if (contrato == null) return;
        idContratoParaNavegar.setValue(contrato.getIdContrato());
    }

    // Resetea el evento de navegacion para evitar que se dispare al volver atras
    public void resetIdContratoNavegacion() {
        if (idContratoParaNavegar != null) idContratoParaNavegar.setValue(null);
    }

    // Lee el inmueble del Bundle y dispara la llamada para traer su contrato
    public void cargarContrato(Bundle bundle) {
        if (bundle == null) return;
        Inmueble inmueble = (Inmueble) bundle.getSerializable("inmueble");
        if (inmueble == null) return;

        direccionInmuebleMutable.setValue("Inmueble en " + inmueble.getDireccion());
        obtenerContratoPorInmueble(inmueble.getIdInmueble());
    }

    private void obtenerContratoPorInmueble(int idInmueble) {
        String token = ApiClient.tokenBearer(getApplication());
        ApiClient.MiServicioInmobiliaria api = ApiClient.getServicio();
        Call<Contrato> call = api.obtenerContratoPorInmueble(token, idInmueble);

        call.enqueue(new Callback<Contrato>() {
            @Override
            public void onResponse(Call<Contrato> call, Response<Contrato> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Contrato contrato = response.body();
                    contratoMutable.postValue(contrato);

                    NumberFormat nf = NumberFormat.getInstance(new Locale("es", "AR"));
                    montoAlquilerMutable.postValue("$ " + nf.format(contrato.getMontoAlquiler()));

                    if (contrato.getInquilino() != null) {
                        String nombre = contrato.getInquilino().getNombre() + " " +
                                contrato.getInquilino().getApellido();
                        nombreInquilinoMutable.postValue(nombre);
                    } else {
                        nombreInquilinoMutable.postValue("-");
                    }

                    botonPagosHabilitado.postValue(true);
                } else {
                    Toast.makeText(getApplication(), "No se obtuvo el contrato",
                            Toast.LENGTH_LONG).show();
                    Log.d("ERROR", "codigo: " + response.code());
                    Log.d("ERROR", "mensaje: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Contrato> call, Throwable t) {
                Toast.makeText(getApplication(), "On failure", Toast.LENGTH_LONG).show();
            }
        });
    }
}