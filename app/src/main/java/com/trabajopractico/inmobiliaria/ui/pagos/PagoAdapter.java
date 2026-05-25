package com.trabajopractico.inmobiliaria.ui.pagos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trabajopractico.inmobiliaria.R;
import com.trabajopractico.inmobiliaria.modelo.Pago;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PagoAdapter extends RecyclerView.Adapter<PagoAdapter.ViewHolderPago> {

    private List<Pago> listaPagos;
    private LayoutInflater inflater;

    public PagoAdapter(List<Pago> pagos, LayoutInflater inflater) {
        this.listaPagos = pagos;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public ViewHolderPago onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_pago, parent, false);
        return new ViewHolderPago(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPago holder, int position) {
        Pago pago = listaPagos.get(position);

        holder.codigoPago.setText("Código pago: " + pago.getIdPago());
        holder.idContrato.setText("Código de contrato: " + pago.getIdContrato());
        holder.fechaPago.setText("Fecha de pago: " + pago.getFechaPago());

        NumberFormat nf = NumberFormat.getInstance(new Locale("es", "AR"));
        holder.monto.setText("Importe: $" + nf.format(pago.getMonto()));

        if (pago.getDetalle() != null && !pago.getDetalle().isEmpty()) {
            holder.detalle.setText("Detalle: " + pago.getDetalle());
        } else {
            holder.detalle.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return listaPagos != null ? listaPagos.size() : 0;
    }

    public static class ViewHolderPago extends RecyclerView.ViewHolder {
        TextView codigoPago, idContrato, fechaPago, monto, detalle;

        public ViewHolderPago(@NonNull View itemView) {
            super(itemView);
            codigoPago = itemView.findViewById(R.id.tvCodigoPago);
            idContrato = itemView.findViewById(R.id.tvIdContrato);
            fechaPago = itemView.findViewById(R.id.tvFechaPago);
            monto = itemView.findViewById(R.id.tvMonto);
            detalle = itemView.findViewById(R.id.tvDetalle);
        }
    }
}
