package com.eykcorp.renalapp.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.dto.MedicamentoDTO;
import com.eykcorp.renalapp.dto.TurnosDTO;

import java.util.List;

/**
 * Created by galo.penaherrera on 28/02/2018.
 */

public class MedicamentosAdapter extends RecyclerView.Adapter<MedicamentosAdapter.MedicamentosViewHolder> {

    private String TAG = "MedicamentosAdapter";
    private List<MedicamentoDTO> items;
    private Context context;

    public static class MedicamentosViewHolder extends RecyclerView.ViewHolder {
        private TextView txvCantidad;
        private TextView txvFrecuencia;
        private TextView txvDuracion;
        private TextView txtMedicamento;
        private CheckBox chkMed;
        private CardView cardMedicamento;

        public MedicamentosViewHolder(View v){
            super(v);
            txvCantidad = (TextView) v.findViewById(R.id.txvCantidad);
            txvFrecuencia = (TextView) v.findViewById(R.id.txvFrecuencia);
            txvDuracion = (TextView) v.findViewById(R.id.txvDuracion);
            txtMedicamento = (TextView) v.findViewById(R.id.txtMedicamento);
            chkMed = (CheckBox) v.findViewById(R.id.chkMed);
            cardMedicamento = (CardView) v.findViewById(R.id.cardMedicamento);
        }
    }

    public MedicamentosAdapter(List<MedicamentoDTO> items, Context context){
        this.items = items;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public MedicamentosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_medicamento, viewGroup, false);
        this.context = viewGroup.getContext();
        return new MedicamentosViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final MedicamentosViewHolder viewHolder, final int i) {

        viewHolder.txvCantidad.setText(items.get(i).getCantidad());
        viewHolder.txvFrecuencia.setText(items.get(i).getFrecuencia());
        viewHolder.txvDuracion.setText(items.get(i).getDuracion());
        viewHolder.txtMedicamento.setText(items.get(i).getDescripcionMedicamento());


        if(items.get(i).getIsChecked().equals("S")){
            viewHolder.chkMed.setChecked(true);
        }

        viewHolder.chkMed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    items.get(i).setIsChecked("S");
                }else{
                    items.get(i).setIsChecked("N");
                }

            }
        });

        viewHolder.cardMedicamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.chkMed.isChecked()){
                    viewHolder.chkMed.setChecked(false);
                    items.get(i).setIsChecked("N");
                }else{
                    viewHolder.chkMed.setChecked(true);
                    items.get(i).setIsChecked("S");
                }
            }
        });

    }

}
