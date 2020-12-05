package com.eykcorp.renalapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.adaptador.MedicamentosAdapter;
import com.eykcorp.renalapp.com.eykcorp.renalapp.util.GenericActivity;
import com.eykcorp.renalapp.dto.MedicamentoDTO;

import java.util.ArrayList;

/**
 * Created by galo.penaherrera on 28/02/2018.
 */

public class SeleccionMedicamentoActivity extends GenericActivity{

    private String TAG = "SelecciMedicameActiv";
    private ProgressBar loading;
    //private RequestQueue requestQueue;
    private AppCompatButton btnAceptar;
    private RecyclerView recReceta;
    private RecyclerView.Adapter adapter;

    private ArrayList<MedicamentoDTO> lsMedicamentos;

    @Override
    public void onBackPressed() {
        retornar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_seleccion_medicamento);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);

        loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.GONE);

        recReceta = (RecyclerView) findViewById(R.id.recReceta);
        recReceta.setHasFixedSize(true);
        recReceta.setLayoutManager(new LinearLayoutManager(this));

        btnAceptar = (AppCompatButton) findViewById(R.id.btnAceptar);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.get("medicamentos") != null){
                lsMedicamentos =  bundle.getParcelableArrayList("medicamentos");
                /*for (int i = 0; i < lsMedicamentos.size(); i++){
                    Log.d(TAG, lsMedicamentos.get(i).getDescripcionMedicamento());
                }*/
            }else{
                Log.d(TAG,"No llegaron los medicamentos");
            }
        }

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //lsMedicamentos.get(0).setDescripcionMedicamento("SE MODIFICO");
                retornar();
            }
        });

        adapter = new MedicamentosAdapter(lsMedicamentos, this);
        recReceta.setAdapter(adapter);
    }

    private void retornar(){
        Intent i = new Intent();
        i.putParcelableArrayListExtra("medicamentos", lsMedicamentos);
        setResult(RESULT_OK,i);
        finish();
    }
}
