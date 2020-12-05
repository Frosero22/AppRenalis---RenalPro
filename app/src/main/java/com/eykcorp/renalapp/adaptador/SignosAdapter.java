package com.eykcorp.renalapp.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.activity.PacientesActivity;
import com.eykcorp.renalapp.activity.TomaSignosActivity;
import com.eykcorp.renalapp.dto.SignosVitalesDTO;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by galo.penaherrera on 11/12/2017.
 */

public class SignosAdapter extends RecyclerView.Adapter<SignosAdapter.SignosViewHolder>{

    private String TAG = "SignosAdapter";
    private List<SignosVitalesDTO> items;
    private Context context;

    public static class SignosViewHolder extends RecyclerView.ViewHolder {
        private TextView txvTa;
        private TextView txvPulso;
        private TextView txvQs;
        private TextView txvQsEfectivo;
        private TextView txvQd;
        private TextView txvPmas;
        private TextView txvPmenos;
        private TextView txvPtm;
        private TextView txvMedicacion;
        private TextView txvObservacion;
        private TextView txvUltimaActual;
        private TextView txvTemperatura;
        private ImageButton imgButEditar;
        private ImageButton imgButEliminar;

        public SignosViewHolder(View v){
            super(v);
            txvTa = (TextView) v.findViewById(R.id.txvTa);
            txvPulso = (TextView) v.findViewById(R.id.txvPulso);
            txvQs = (TextView) v.findViewById(R.id.txvQs);
            txvQsEfectivo = (TextView) v.findViewById(R.id.txvQsEfectivo);
            txvQd = (TextView) v.findViewById(R.id.txvQd);
            txvPmas = (TextView) v.findViewById(R.id.txvPmas);
            txvPmenos = (TextView) v.findViewById(R.id.txvPmenos);
            txvPtm = (TextView) v.findViewById(R.id.txvPtm);
            txvMedicacion = (TextView) v.findViewById(R.id.txvMedicacion);
            txvObservacion = (TextView) v.findViewById(R.id.txvObservacion);
            txvUltimaActual = (TextView) v.findViewById(R.id.txvUltimaActual);
            txvTemperatura = (TextView) v.findViewById(R.id.txvTemp);
            imgButEditar = (ImageButton) v.findViewById(R.id.imgButEditar);
            imgButEliminar = (ImageButton) v.findViewById(R.id.imgButEliminar);
        }
    }

    public SignosAdapter(List<SignosVitalesDTO> items, Context context){
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public SignosAdapter.SignosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_signos_vitales, viewGroup, false);
        this.context = viewGroup.getContext();
        return new SignosAdapter.SignosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SignosAdapter.SignosViewHolder viewHolder, final int i) {
        try{
            viewHolder.txvTa.setText(items.get(i).getTensionArterialSistolica().toString()+" / "+items.get(i).getTensionArterialDiastolica().toString());
            viewHolder.txvPulso.setText(items.get(i).getPulso()!=null?items.get(i).getPulso().toString():"No disponible");
            viewHolder.txvQs.setText(items.get(i).getValorQs()!=null?items.get(i).getValorQs().toString():"No disponible");
            viewHolder.txvQsEfectivo.setText(items.get(i).getValorQsEfectivo()!=null?items.get(i).getValorQsEfectivo().toString():"No disponible");
            viewHolder.txvQd.setText(items.get(i).getValorQd()!=null?items.get(i).getValorQd().toString():"No disponible");
            viewHolder.txvPmas.setText(items.get(i).getValorPMas()!=null?items.get(i).getValorPMas().toString():"No disponible");
            viewHolder.txvPmenos.setText(items.get(i).getValorPMenos()!=null?items.get(i).getValorPMenos().toString():"No disponible");
            viewHolder.txvPtm.setText(items.get(i).getValorPtm()!=null?items.get(i).getValorPtm().toString():"No disponible");
            viewHolder.txvMedicacion.setText(items.get(i).getMedicacion()!=null?items.get(i).getMedicacion():"No disponible");
            viewHolder.txvObservacion.setText(items.get(i).getObservacion()!=null?items.get(i).getObservacion():"No disponible");
            viewHolder.txvTemperatura.setText(items.get(i).getTemperatura()!=null?items.get(i).getTemperatura().toString():"No disponible");
            viewHolder.txvUltimaActual.setText(items.get(i).getNombreCompletoUsuarioToma() + " - " + items.get(i).getDescripcionFechaToma());
            viewHolder.imgButEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((TomaSignosActivity)context).levantarEdicionSignos(items.get(i));
                }
            });
            viewHolder.imgButEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((TomaSignosActivity)context).eliminarRegistro(items.get(i));
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

