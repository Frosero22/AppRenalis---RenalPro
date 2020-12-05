package com.eykcorp.renalapp.adaptador;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.activity.PacientesActivity;
import com.eykcorp.renalapp.dto.PacienteDTO;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by galo.penaherrera on 28/11/2017.
 */

public class PacientesAdapter extends RecyclerView.Adapter<PacientesAdapter.PacientesViewHolder>{

    private String TAG = "PacientesAdapter";
    private List<PacienteDTO> items;
    private Context context;
    private boolean verTodos;
    private boolean verFavoritos;

    public static class PacientesViewHolder extends RecyclerView.ViewHolder {
        private TextView txvNombrePaciente;
        //private TextView txvEdad;
        //private TextView txvPeso;
        //private TextView txvTalla;
        private TextView txvEquipo;
        private Button txvAgregarFavoritos;
        private Button txvEliminarFavoritos;
        private TextView txvEstadoPaciente;
        private TextView txvTiempoDialisis;
        private TextView txvQsPrescrito;
        private TextView txvFiltro;
        private TextView txvMedicamento;
        private TextView txvValorUF;
        private CardView cardPaciente;

        //private ConstraintLayout ctrPaciente;

        public PacientesViewHolder(View v){
            super(v);
            txvNombrePaciente = (TextView) v.findViewById(R.id.txvNombrePaciente);
            //txvEdad = (TextView) v.findViewById(R.id.txvEdad);
            //txvPeso = (TextView) v.findViewById(R.id.txvPeso);
            //txvTalla = (TextView) v.findViewById(R.id.txvTalla);
            txvEquipo = (TextView) v.findViewById(R.id.txvEquipo);
            txvEstadoPaciente = (TextView) v.findViewById(R.id.txvEstadoPaciente);
            txvValorUF = (TextView) v.findViewById(R.id.txvValorUF);
            txvQsPrescrito = (TextView) v.findViewById(R.id.txvQsPrescrito);
            txvTiempoDialisis = (TextView) v.findViewById(R.id.txvTiempoDialisis);
            cardPaciente = (CardView) v.findViewById(R.id.cardPaciente);
            txvAgregarFavoritos = (Button) v.findViewById(R.id.btnAgregarFavoritos);
            txvEliminarFavoritos = (Button) v.findViewById(R.id.btnEliminarFavoritos);
            txvFiltro = (TextView) v.findViewById(R.id.txvFiltro);
            txvMedicamento = (TextView) v.findViewById(R.id.txvMedicamento);
        }
    }

    public PacientesAdapter(List<PacienteDTO> items, Context context, boolean favoritos, boolean todos){
        this.items = items;
        this.context = context;
        this.verFavoritos = favoritos;
        this.verTodos = todos;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public PacientesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_paciente_v2, viewGroup, false);
        this.context = viewGroup.getContext();
        return new PacientesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PacientesViewHolder viewHolder, final int i) {
        try{
            //viewHolder.txvEdad.setText(items.get(i).getEdad());
            //viewHolder.txvTalla.setVisibility(View.GONE);

            if(items.get(i).getDescripcionMaquina().isEmpty()){
                viewHolder.txvEquipo.setVisibility(View.GONE);
            }else{
                viewHolder.txvEquipo.setText(items.get(i).getDescripcionMaquina());
            }

            /*if(items.get(i).getTalla().isEmpty()){
                viewHolder.txvTalla.setVisibility(View.GONE);
            }else{
                viewHolder.txvTalla.setText(items.get(i).getTalla());
            }*/

            if(items.get(i).getTiempoDialisis().isEmpty()){
                viewHolder.txvTiempoDialisis.setVisibility(View.GONE);
            }else{
                viewHolder.txvTiempoDialisis.setText(items.get(i).getTiempoDialisis()+" (Tiempo de diálisis)");
            }

            if(items.get(i).getQsPrescrito().isEmpty()){
                viewHolder.txvQsPrescrito.setVisibility(View.GONE);
            }else{
                viewHolder.txvQsPrescrito.setText(items.get(i).getQsPrescrito()+" (QS Prescrito)");
            }

            if(items.get(i).getNombreFiltro().isEmpty()){
                viewHolder.txvFiltro.setVisibility(View.GONE);
            }else{
                viewHolder.txvFiltro.setText(items.get(i).getNombreFiltro()+" (Filtro)");
            }

            if(items.get(i).getValorUF().isEmpty()){
                viewHolder.txvValorUF.setVisibility(View.GONE);
            }else{
                viewHolder.txvValorUF.setText(items.get(i).getValorUF()+" (UF)");
            }

            /*if(items.get(i).getPeso().isEmpty()){
                viewHolder.txvPeso.setVisibility(View.GONE);
            }else{
                viewHolder.txvPeso.setText(items.get(i).getPeso());
            }*/

            if(items.get(i).getPlanMedicacion().isEmpty()){
                viewHolder.txvMedicamento.setVisibility(View.GONE);
            }else{
                viewHolder.txvMedicamento.setText(items.get(i).getPlanMedicacion());
            }

            viewHolder.txvNombrePaciente.setText(items.get(i).getNombre());

            viewHolder.txvEstadoPaciente.setText(items.get(i).getDescripcionEstado());

            viewHolder.cardPaciente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PacientesActivity)context).mostrarMenuInferior(items.get(i));
                }
            });

            if(verFavoritos){
                viewHolder.txvAgregarFavoritos.setVisibility(View.GONE);
                viewHolder.txvEliminarFavoritos.setVisibility(View.VISIBLE);
            }
            if(verTodos){
                viewHolder.txvEliminarFavoritos.setVisibility(View.GONE);
                viewHolder.txvAgregarFavoritos.setVisibility(View.VISIBLE);
            }

            viewHolder.txvEliminarFavoritos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PacientesActivity)context).eliminarFavoritos(items.get(i));
                }
            });

            viewHolder.txvAgregarFavoritos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PacientesActivity)context).agregarFavoritos(items.get(i));
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
