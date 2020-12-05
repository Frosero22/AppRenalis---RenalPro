package com.eykcorp.renalapp.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.activity.TurnosActivity;
import com.eykcorp.renalapp.dto.TurnosDTO;

import java.net.URLDecoder;
import java.util.List;

/**
 * Created by galo.penaherrera on 24/11/2017.
 */

public class TurnosAdapter extends RecyclerView.Adapter<TurnosAdapter.TurnosViewHolder> {

    private String TAG = "TurnosAdapter";
    private List<TurnosDTO> items;
    private Context context;

    public static class TurnosViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout lyTurno;
        private TextView txvTurno;

        public TurnosViewHolder(View v){
            super(v);
            lyTurno = (LinearLayout) v.findViewById(R.id.lyTurno);
            txvTurno = (TextView) v.findViewById(R.id.txvTurno);
        }
    }

    public TurnosAdapter(List<TurnosDTO> items, Context context){
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public TurnosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_turno, viewGroup, false);
        this.context = viewGroup.getContext();
        return new TurnosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TurnosViewHolder viewHolder, final int i) {
        try{
            viewHolder.txvTurno.setText(items.get(i).getDescripcionHorario());
            viewHolder.lyTurno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((TurnosActivity)context).seleccionarTurno(items.get(i));
                    //Toast.makeText(context, items.get(i).getDescripcionTurno(), Toast.LENGTH_LONG).show();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
