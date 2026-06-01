package com.trabajopractico.inmobiliaria.ui.inmuebleNuevo;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

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
    private MutableLiveData<String> mensajeMutable;

    public InmuebleNuevoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Uri> getmUri() {
        if (uriMutableLiveData == null) {
            uriMutableLiveData = new MutableLiveData<>();
        }
        return uriMutableLiveData;
    }

    public LiveData<String> getMensaje() {
        if (mensajeMutable == null) mensajeMutable = new MutableLiveData<>();
        return mensajeMutable;
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
            mensajeMutable.postValue("Debe ingresar una foto");
            return new byte[]{};
        } catch (Exception ex) {
            mensajeMutable.postValue("Error al procesar la imagen");
            return new byte[]{};
        }
    }

    // Carga un inmueble nuevo: valida campos, transforma imagen, arma Multipart y manda POST.
    public void cargarInmueble(String direccion, String uso, String tipo,
                               String ambientes, String superficie, String valor) {
        try {
            // Validar que ningun campo este vacio
            if (direccion.trim().isEmpty() || uso.isEmpty() || tipo.isEmpty() ||
                    ambientes.isEmpty() || superficie.isEmpty() || valor.isEmpty()) {
                mensajeMutable.setValue("Debe completar todos los campos");
                return;
            }

            int amb = Integer.parseInt(ambientes);
            int sup = Integer.parseInt(superficie);
            double val = Double.parseDouble(valor);

            if (amb <= 0) {
                mensajeMutable.setValue("Los ambientes deben ser mayor a 0");
                return;
            }
            if (sup <= 0) {
                mensajeMutable.setValue("La superficie debe ser mayor a 0");
                return;
            }
            if (val <= 0) {
                mensajeMutable.setValue("El precio debe ser mayor a 0");
                return;
            }

            // Construir el inmueble con los datos del form
            Inmueble i = new Inmueble();
            i.setDireccion(direccion.trim());
            i.setUso(uso);
            i.setTipo(tipo);
            i.setAmbientes(amb);
            i.setSuperficie(sup);
            i.setValor(val);

            // Transformar la imagen a bytes
            byte[] imagen = transformarImagen();
            if (imagen.length == 0) {
                mensajeMutable.setValue("Debe ingresar imagen");
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
                        mensajeMutable.postValue("Inmueble guardado correctamente");
                        inmuebleCreado.postValue(true);
                    } else {
                        mensajeMutable.postValue("Error al cargar inmueble");
                        Log.d("ERROR", "codigo: " + response.code());
                        Log.d("ERROR", "mensaje: " + response.message());
                        Log.d("ERROR", "body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Inmueble> call, Throwable t) {
                    mensajeMutable.postValue("Error de conexión");
                }
            });

        } catch (NumberFormatException e) {
            mensajeMutable.setValue("Los campos numéricos deben tener un formato válido");
        } catch (Exception e) {
            mensajeMutable.setValue("Error: " + e.getMessage());
        }
    }
}