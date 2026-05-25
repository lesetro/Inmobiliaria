package com.trabajopractico.inmobiliaria.ui.inmuebleNuevo;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.trabajopractico.inmobiliaria.modelo.Inmueble;
import com.trabajopractico.inmobiliaria.request.ApiClient;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmuebleNuevoViewModel extends AndroidViewModel {

    // LiveData del URI de la imagen seleccionada (del celular)
    private MutableLiveData<Uri> uriMutableLiveData;
    // LiveData que avisa al fragment cuando el inmueble se guardo exitosamente
    private MutableLiveData<Boolean> inmuebleCreado;

    public InmuebleNuevoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Uri> getmUri() {
        if (uriMutableLiveData == null) {
            uriMutableLiveData = new MutableLiveData<>();
        }
        return uriMutableLiveData;
    }

    public LiveData<Boolean> getInmuebleCreado() {
        if (inmuebleCreado == null) {
            inmuebleCreado = new MutableLiveData<>();
        }
        return inmuebleCreado;
    }

    // Recibe el resultado del selector de galeria.
    // Si fue OK, extrae el Uri de la imagen elegida y lo pone en el LiveData
    // para que el Fragment lo muestre en el ImageView.
    public void recibirFoto(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            Uri uri = data.getData();
            Log.d("salada", uri.toString());
            uriMutableLiveData.setValue(uri);
        }
    }

    // Lee la imagen del Uri, la convierte a JPEG comprimido y la devuelve como bytes.
    // Si falla (no hay imagen seleccionada), devuelve array vacio.
    private byte[] transformarImagen() {
        try {
            Uri uri = uriMutableLiveData.getValue();
            InputStream inputStream = getApplication().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (FileNotFoundException ex) {
            Toast.makeText(getApplication(), "Debe ingresar una foto", Toast.LENGTH_LONG).show();
            return new byte[]{};
        } catch (Exception ex) {
            Toast.makeText(getApplication(), "Error al procesar la imagen", Toast.LENGTH_LONG).show();
            return new byte[]{};
        }
    }

    // Carga un inmueble nuevo: valida campos, transforma imagen, arma Multipart y manda POST.
    public void cargarInmueble(String direccion, String uso, String tipo,
                               String ambientes, String superficie, String valor) {
        try {
            // Validar que ningun campo este vacio
            if (direccion.isEmpty() || uso.isEmpty() || tipo.isEmpty() ||
                    ambientes.isEmpty() || superficie.isEmpty() || valor.isEmpty()) {
                Toast.makeText(getApplication(), "Debe completar todos los campos",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // Construir el inmueble con los datos del form
            Inmueble i = new Inmueble();
            i.setDireccion(direccion);
            i.setUso(uso);
            i.setTipo(tipo);
            i.setAmbientes(Integer.parseInt(ambientes));
            i.setSuperficie(Integer.parseInt(superficie));
            i.setValor(Double.parseDouble(valor));

            // Transformar la imagen a bytes
            byte[] imagen = transformarImagen();
            if (imagen.length == 0) {
                Toast.makeText(getApplication(), "Debe ingresar imagen",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // Armar el Multipart: una parte para el JSON del inmueble, otra para la imagen
            String inmuebleJson = new Gson().toJson(i);
            RequestBody inmuebleBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), inmuebleJson);
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/jpeg"), imagen);
            MultipartBody.Part imagenPart = MultipartBody.Part.createFormData(
                    "imagen", "imagen.jpg", requestFile);

            // Llamar al endpoint
            String token = ApiClient.tokenBearer(getApplication());
            ApiClient.MiServicioInmobiliaria servicio = ApiClient.getServicio();
            Call<Inmueble> call = servicio.cargarInmueble(token, imagenPart, inmuebleBody);

            call.enqueue(new Callback<Inmueble>() {
                @Override
                public void onResponse(Call<Inmueble> call, Response<Inmueble> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplication(), "Inmueble guardado correctamente",
                                Toast.LENGTH_SHORT).show();
                        inmuebleCreado.postValue(true);
                    } else {
                        Toast.makeText(getApplication(), "Error al cargar inmueble",
                                Toast.LENGTH_LONG).show();
                        Log.d("ERROR", "codigo: " + response.code());
                        Log.d("ERROR", "mensaje: " + response.message());
                        Log.d("ERROR", "body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Inmueble> call, Throwable t) {
                    Toast.makeText(getApplication(), "On failure al cargar inmueble",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(getApplication(),
                    "Los campos numericos deben tener un formato valido",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}