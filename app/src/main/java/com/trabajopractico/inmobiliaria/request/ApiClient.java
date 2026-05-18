package com.trabajopractico.inmobiliaria.request;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trabajopractico.inmobiliaria.modelo.Contrato;
import com.trabajopractico.inmobiliaria.modelo.Inmueble;
import com.trabajopractico.inmobiliaria.modelo.Pago;
import com.trabajopractico.inmobiliaria.modelo.Propietario;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class ApiClient {

    public final static String BASE_URL = "https://capacitacion.alwaysdata.net/";

    // Crear el servicio Retrofit + Gson
    public static MiServicioInmobiliaria getServicio() {
        // setLenient permite que Gson sea mas flexible
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(MiServicioInmobiliaria.class);
    }

    // Interfaz con todos los endpoints
    public interface MiServicioInmobiliaria {

        // AUTENTICACION

        // Login: recibe Usuario y Clave (devuelve un token (string).
        @FormUrlEncoded
        @POST("api/Propietarios/login")
        Call<String> login(@Field("Usuario") String usuario,
                           @Field("Clave") String clave);

        // Resetear contraseña ("me olvide la contraseña").
        @PUT("api/propietarios/fix-id3")
        Call<ResetResponse> resetearPassword();

        //  PROPIETARIO

        // Obtener perfil del propietario logueado. El back lo deduce del token.
        @GET("api/Propietarios")
        Call<Propietario> obtenerPerfil(@Header("Authorization") String token);

        // Actualizar el perfil (nombre, apellido, telefono, email...). NO mandar la clave.
        @PUT("api/Propietarios/actualizar")
        Call<Propietario> actualizarPerfil(@Header("Authorization") String token,
                                           @Body Propietario propietario);

        // Cambiar la contraseña por separado.
        @FormUrlEncoded
        @PUT("api/Propietarios/changePassword")
        Call<Void> cambiarPassword(@Header("Authorization") String token,
                                   @Field("currentPassword") String passwordActual,
                                   @Field("newPassword") String passwordNueva);

        //  INMUEBLES

        // Lista TODOS los inmuebles del propietario logueado.
        @GET("api/Inmuebles")
        Call<List<Inmueble>> obtenerInmuebles(@Header("Authorization") String token);


        @GET("api/Inmuebles/GetContratoVigente")
        Call<List<Inmueble>> obtenerInmueblesAlquilados(@Header("Authorization") String token);

        // Cargar un inmueble nuevo CON imagen
        @Multipart
        @POST("api/Inmuebles/cargar")
        Call<Inmueble> cargarInmueble(@Header("Authorization") String token,
                                      @Part MultipartBody.Part imagen,
                                      @Part("inmueble") RequestBody inmueble);

        // Actualizar un inmueble existente
        @PUT("api/Inmuebles/actualizar")
        Call<Inmueble> actualizarInmueble(@Header("Authorization") String token,
                                          @Body Inmueble inmueble);

        //CONTRATOS

        // Devuelve el contrato vigente asociado a un inmueble.
        @GET("api/contratos/inmueble/{id}")
        Call<Contrato> obtenerContratoPorInmueble(@Header("Authorization") String token,
                                                  @Path("id") int idInmueble);

        // PAGOS

        // Devuelve los pagos asociados a un contrato.
        @GET("api/pagos/contrato/{id}")
        Call<List<Pago>> obtenerPagosPorContrato(@Header("Authorization") String token,
                                                 @Path("id") int idContrato);
    }
    // Clase interna para parsear la respuesta del endpoint de reseteo.
    // El back devuelve: { "message", "idPropietario", "email" }
    public static class ResetResponse {
        public String message;
        public int idPropietario;
        public String email;
    }

    // Manejo del token en SharedPreferences

    private static final String SP_FILE = "datos.xml";
    private static final String SP_KEY_TOKEN = "token";

    public static void guardarToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SP_KEY_TOKEN, token);
        editor.apply();
    }

    public static String recuperarToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        return sp.getString(SP_KEY_TOKEN, null);
    }

    // Devuelve el token con el prefijo "Bearer " listo para mandar como header.
    // Si no hay token, devuelve null.
    public static String tokenBearer(Context context) {
        String t = recuperarToken(context);
        return t == null ? null : "Bearer " + t;
    }

    public static void borrarToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        sp.edit().remove(SP_KEY_TOKEN).apply();
    }
}
