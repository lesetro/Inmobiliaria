package com.trabajopractico.inmobiliaria.ui.inmuebles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.trabajopractico.inmobiliaria.R;
import com.trabajopractico.inmobiliaria.modelo.Inmueble;
import com.trabajopractico.inmobiliaria.request.ApiClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class InmuebleAdapter extends RecyclerView.Adapter<InmuebleAdapter.ViewHolderInmueble> {

    // Tipo de destino al que navega el adapter cuando se toca un item.
    // Permite reutilizar el mismo adapter desde varios fragments distintos.
    public enum Destino {
        DETALLE,    // desde el listado normal de Inmuebles
        CONTRATO,   // desde Contratos
        INQUILINO   // desde Inquilinos
    }

    private List<Inmueble> listaInmuebles;
    private LayoutInflater inflater;
    private Destino destino;

    public InmuebleAdapter(List<Inmueble> inmuebles, LayoutInflater inflater) {
        this.listaInmuebles = inmuebles;
        this.inflater = inflater;
    }
    // Constructor nuevo: permite elegir a donde navega al tocar un item
    public InmuebleAdapter(List<Inmueble> inmuebles, LayoutInflater inflater, Destino destino) {
        this.listaInmuebles = inmuebles;
        this.inflater = inflater;
        this.destino = destino;
    }

    // Infla el card de cada inmueble
    @NonNull
    @Override
    public ViewHolderInmueble onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_inmueble, parent, false);
        return new ViewHolderInmueble(itemView);
    }

    // Recorre la lista y carga los datos en cada card
    @Override
    public void onBindViewHolder(@NonNull ViewHolderInmueble holder, int position) {
        Inmueble inmuebleActual = listaInmuebles.get(position);
        holder.direccion.setText(inmuebleActual.getDireccion());

        // Formatear el valor como pesos argentinos
        NumberFormat nf = NumberFormat.getInstance(new Locale("es", "AR"));
        String valorFormateado = nf.format(inmuebleActual.getValor());
        holder.precio.setText("$ " + valorFormateado);

        // Cargar la imagen del inmueble con Glide
        Glide.with(holder.itemView.getContext())
                .load(ApiClient.BASE_URL + inmuebleActual.getImagen())
                .placeholder(R.drawable.loading)
                .error(R.drawable.house)
                .into(holder.foto);

        // Click en el card: navega al destino segun el tipo configurado
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("inmueble", inmuebleActual);

            int idAccion;
            switch (destino) {
                case CONTRATO:
                    idAccion = R.id.action_nav_contratos_to_contratoDetalleFragment;
                    break;
                case INQUILINO:
                    idAccion = R.id.action_nav_inquilinos_to_inquilinoDetalleFragment;
                    break;
                case DETALLE:
                default:
                    idAccion = R.id.action_nav_inmuebles_to_inmuebleDetalleFragment;
                    break;
            }

            Navigation.findNavController(v).navigate(idAccion, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return listaInmuebles != null ? listaInmuebles.size() : 0;
    }

    public static class ViewHolderInmueble extends RecyclerView.ViewHolder {
        TextView direccion, precio;
        ImageView foto;

        public ViewHolderInmueble(@NonNull View itemView) {
            super(itemView);
            direccion = itemView.findViewById(R.id.tvDireccion);
            precio = itemView.findViewById(R.id.tvPrecio);
            foto = itemView.findViewById(R.id.ivImagen);
        }
    }
}