package com.trabajopractico.inmobiliaria.ui.inicio;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class InicioViewModel extends AndroidViewModel {

    private MutableLiveData<MapaActual> mapaActual;

    public InicioViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<MapaActual> getMapaActual() {
        if (mapaActual == null) {
            mapaActual = new MutableLiveData<>();
        }
        return mapaActual;
    }

    public void cargarMapa() {
        MapaActual mapaActualNuevo = new MapaActual();
        mapaActual.setValue(mapaActualNuevo);
    }

    public class MapaActual implements OnMapReadyCallback {

        // ponemos una cualquiera por ahora, luego ajustamos la latitud y longitud
        LatLng SanLuis = new LatLng(-33.295830, -66.336860);

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            googleMap.addMarker(new MarkerOptions()
                    .position(SanLuis)
                    .title("Inmobiliaria La Punta"));


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(SanLuis)
                    .zoom(18)
                    .bearing(0)
                    .tilt(30)
                    .build();

            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            googleMap.animateCamera(cameraUpdate);
        }

    }

}
