package com.eykcorp.renalapp.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.com.eykcorp.renalapp.util.GenericActivity;
import com.eykcorp.renalapp.dto.MedicamentoDTO;
import com.eykcorp.renalapp.dto.SignosVitalesDTO;
import com.eykcorp.renalapp.util.WsUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.net.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by galo.penaherrera on 12/12/2017.
 */

public class EdicionTomaSignosActivity extends GenericActivity {

    private String TAG = "EdicionTomaSignosActiv";

    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    private String horaObtenida;
    private String minutosObtenido;
    private ProgressBar loading;
    private ProgressDialog progress;
    private RequestQueue requestQueue;
    private TextView txvTituloPaciente;
    private String idAtencionPaciente;
    private String idTurno;
    private EditText txtDiastolica;
    private EditText txtSistolica;
    private EditText txtPulso;
    private EditText txtTemperatura;
    private EditText txtQs;
    private EditText txtQsEfectivo;
    private EditText txtQd;
    private EditText txtPmas;
    private EditText txtPmenos;
    private EditText txtPtm;
    private EditText txtObservacion;
    private EditText txtHora;
    private EditText txtDia;

    private TextInputLayout txtInpSistolica;
    private TextInputLayout txtInpDiastolica;
    private TextInputLayout txtInpPulso;
    private TextInputLayout txtInpTemperatura;
    private TextInputLayout txtInpQs;
    private TextInputLayout txtInpQd;
    private TextInputLayout txtInpPmas;
    private TextInputLayout txtInpPmenos;
    private TextInputLayout txtInpPtm;
    private TextInputLayout txtInpObservacion;
    private TextInputLayout txtInpHora;
    private TextInputLayout txtInpQsEfectivo;

    //private ScrollView  scroll;
    private String idSignoVital;
    private Boolean edicion;
    private AppCompatButton btnGuardar;
    private AppCompatButton btnMedicacion;
    private int dia;
    private int mes;
    private int anio;
    Calendar calfecha;
    public SimpleDateFormat formatoDate = new SimpleDateFormat("dd/MM/yyyy");
    public ArrayList<MedicamentoDTO> lsMedicamentos;

    public boolean inicio = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_edicion_toma_signos);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);

        loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.GONE);


        lsMedicamentos = new ArrayList<>();

        txtInpSistolica = (TextInputLayout) findViewById(R.id.txtInpSistolica);
        txtInpDiastolica = (TextInputLayout) findViewById(R.id.txtInpDiastolica);
        txtInpPulso = (TextInputLayout) findViewById(R.id.txtInpPulso);
        txtInpTemperatura = (TextInputLayout) findViewById(R.id.txtInpTemperatura);
        txtInpQs = (TextInputLayout) findViewById(R.id.txtInpQs);
        txtInpQsEfectivo = (TextInputLayout) findViewById(R.id.txtInpQsEfectivo);
        txtInpQd = (TextInputLayout) findViewById(R.id.txtInpQd);
        txtInpPmas = (TextInputLayout) findViewById(R.id.txtInpPmas);
        txtInpPmenos = (TextInputLayout) findViewById(R.id.txtInpPmenos);
        txtInpPtm = (TextInputLayout) findViewById(R.id.txtInpPtm);
        txtInpHora = (TextInputLayout) findViewById(R.id.txtInpHora);
        txtInpObservacion = (TextInputLayout) findViewById(R.id.txtInpObservacion);

        txvTituloPaciente = (TextView) findViewById(R.id.txvTituloPaciente);
        txtDiastolica = (EditText) findViewById(R.id.txtDiastolica);
        txtSistolica = (EditText) findViewById(R.id.txtSistolica);
        txtPulso = (EditText) findViewById(R.id.txtPulso);
        txtTemperatura = (EditText) findViewById(R.id.txtTemperatura);
        txtQs = (EditText) findViewById(R.id.txtQs);
        txtQsEfectivo = (EditText) findViewById(R.id.txtQsEfectivo);
        txtQd = (EditText) findViewById(R.id.txtQd);
        txtPmas = (EditText) findViewById(R.id.txtPmas);
        txtPmenos = (EditText) findViewById(R.id.txtPmenos);
        txtObservacion = (EditText) findViewById(R.id.txtObservacion);
        txtPtm = (EditText) findViewById(R.id.txtPtm);
        txtHora = (EditText) findViewById(R.id.txtHora);
        txtDia = (EditText) findViewById(R.id.txtDia);
        btnGuardar = (AppCompatButton) findViewById(R.id.btnGuardar);
        btnMedicacion = (AppCompatButton) findViewById(R.id.btnMedicacion);

        txtHora.setKeyListener(null);
        txtDia.setKeyListener(null);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            txvTituloPaciente.setText(bundle.get("nombrePaciente").toString());
            idAtencionPaciente = bundle.getString("idAtencionPaciente");
            idTurno = bundle.getString("idTurno");
            edicion = bundle.getBoolean("editar");
            if(edicion){
                SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
                idSignoVital = bundle.get("idSignoVital")!=null?bundle.get("idSignoVital").toString():null;
                txtDiastolica.setText(bundle.get("taDiastolica")!=null?bundle.get("taDiastolica").toString():"");
                txtObservacion.setText(bundle.get("observacion")!=null?bundle.get("observacion").toString():"");
                txtPmas.setText(bundle.get("pmas")!=null?bundle.get("pmas").toString():"");
                txtPmenos.setText(bundle.get("pmenos")!=null?bundle.get("pmenos").toString():"");
                txtSistolica.setText(bundle.get("taSistolica")!=null?bundle.get("taSistolica").toString():"");
                txtPulso.setText(bundle.get("pulso")!=null?bundle.get("pulso").toString():"");
                txtTemperatura.setText(bundle.get("temperatura")!=null?bundle.get("temperatura").toString():"");
                txtQs.setText(bundle.get("qs")!=null?bundle.get("qs").toString():"");
                txtQsEfectivo.setText(bundle.get("qsEfectivo")!=null?bundle.get("qsEfectivo").toString():"");
                txtQd.setText(bundle.get("qd")!=null?bundle.get("qd").toString():"");
                txtPtm.setText(bundle.get("ptm")!=null?bundle.get("ptm").toString():"");
                btnMedicacion.setText(bundle.get("medicacion")!=null?bundle.get("medicacion").toString():"MEDICACION");

                String fechaHora = bundle.get("hora").toString();
                String hora = fechaHora.substring(fechaHora.lastIndexOf("-")+1, fechaHora.length());
                txtHora.setText(hora.trim());
                txtDia.setText(bundle.get("fecha").toString());

                horaObtenida = hora.substring(0,hora.lastIndexOf(":")).trim();
                minutosObtenido = hora.substring(hora.lastIndexOf(":")+1,hora.length());
                txtHora.setText(horaObtenida+":"+minutosObtenido);

                calfecha = Calendar.getInstance();
                try{
                    Log.d(TAG, form.parse(bundle.get("fecha").toString()).toString());
                    calfecha.setTime(form.parse(bundle.get("fecha").toString()));
                }catch (Exception e){
                    e.printStackTrace();
                }

                obtenerRecetaActuaLog();
            }else{

                SimpleDateFormat dateHora = new SimpleDateFormat("HH:mm");
                calfecha = Calendar.getInstance();
                txtDia.setText(formatoDate.format(calfecha.getTime()));
                txtHora.setText(dateHora.format(new Date()));
                horaObtenida = txtHora.getText().toString().substring(0,txtHora.getText().toString().lastIndexOf(":")).trim();
                minutosObtenido = txtHora.getText().toString().substring(txtHora.getText().toString().lastIndexOf(":")+1,txtHora.getText().toString().length());

                //Se obtiene el qs para cargarlo
                obtenerQsPreescrito();

                //Se buscan las nuevas medicinas
                obtenerRecetaNuevoLog();
            }

            btnMedicacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for (int i = 0; i < lsMedicamentos.size(); i++){
                        Log.d(TAG, lsMedicamentos.get(i).getIsChecked());
                    }

                    Intent i;
                    i = new Intent(EdicionTomaSignosActivity.this, SeleccionMedicamentoActivity.class);
                    i.putParcelableArrayListExtra("medicamentos", lsMedicamentos);
                    startActivityForResult(i,200);
                }
            });
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos()){
                    if(edicion){
                        //actualizarLogTomaSignos();
                        actualizarLogTomaSignosV2();
                    }else{
                        //crearLogTomaSignos();
                        crearLogTomaSignosV2();
                    }
                }
            }
        });

        /*txtInpTemperatura.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpTemperatura.setError("Ingresa Temperatura");
                    txtInpTemperatura.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpTemperatura.setError(null);
                    txtInpTemperatura.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

        txtInpSistolica.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpSistolica.setError("Ingresa TA(Sistólica)");
                    txtInpSistolica.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpSistolica.setError(null);
                    txtInpSistolica.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtInpDiastolica.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpDiastolica.setError("Ingresa TA(Diastólica)");
                    txtInpDiastolica.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpDiastolica.setError(null);
                    txtInpDiastolica.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerHora();
            }
        });

        txtHora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    obtenerHora();
                }
            }
        });

        txtDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calfecha = Calendar.getInstance();
                int mYear = calfecha.get(Calendar.YEAR);
                int mMonth = calfecha.get(Calendar.MONTH);
                int mDay = calfecha.get(Calendar.DAY_OF_MONTH);
                obtenerDia(mYear,mMonth,mDay);
            }
        });


        txtDia.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    //calfecha = Calendar.getInstance();
                    int mYear = calfecha.get(Calendar.YEAR);
                    int mMonth = calfecha.get(Calendar.MONTH);
                    int mDay = calfecha.get(Calendar.DAY_OF_MONTH);
                    obtenerDia(mYear,mMonth,mDay);
                }
            }
        });

        /*txtInpPulso.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpPulso.setError("Ingresa Pulso");
                    txtInpPulso.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpPulso.setError(null);
                    txtInpPulso.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtInpQs.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpQs.setError("Ingresa valor Qs");
                    txtInpQs.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpQs.setError(null);
                    txtInpQs.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtInpQd.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpQd.setError("Ingresa valor Qd");
                    txtInpQd.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpQd.setError(null);
                    txtInpQd.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtInpPmas.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpPmas.setError("Ingresa valor P+");
                    txtInpPmas.setErrorEnabled(true);
                }

                if(charSequence.length() >  0){
                    txtInpPmas.setError(null);
                    txtInpPmas.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtInpPmenos.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpPmenos.setError("Ingresa valor P-");
                    txtInpPmenos.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpPmenos.setError(null);
                    txtInpPmenos.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        txtInpPtm.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpPtm.setError("Ingresa valor PTM");
                    txtInpPtm.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpPtm.setError(null);
                    txtInpPtm.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        txtInpHora.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < 1){
                    txtInpHora.setError("Ingresa hora");
                    txtInpHora.setErrorEnabled(true);

                }

                if(charSequence.length() >  0){
                    txtInpHora.setError(null);
                    txtInpHora.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

        txtSistolica.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            lsMedicamentos = data.getExtras().getParcelableArrayList("medicamentos");
            String med = "";
            for (int i = 0; i < lsMedicamentos.size(); i++) {
                //    Log.d(TAG, lsMedicamentos.get(i).getDescripcionMedicamento());
                if(lsMedicamentos.get(i).getIsChecked().equals("S")){
                    med = med + "\n"+lsMedicamentos.get(i).getDescripcionMedicamento();
                }
            }
            if(med.isEmpty()){
                btnMedicacion.setText("MEDICACION");
            }else{
                btnMedicacion.setText(med);
            }


        }
    }


    public void crearLogTomaSignos(){
        try{
            //scroll.setVisibility(View.GONE);

            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/historico";
            Log.d(TAG, "URL: " + url);

            JSONObject objJson = new JSONObject();
            objJson.put("idTurno", idTurno);
            objJson.put("idSignoVital",idSignoVital!=null?idSignoVital:null);
            objJson.put("tensionArterialSistolica", Double.parseDouble(txtSistolica.getText().toString()));
            objJson.put("tensionArterialDiastolica", Double.parseDouble(txtDiastolica.getText().toString()));
            objJson.put("pulso", !txtPulso.getText().toString().isEmpty()?Double.parseDouble(txtPulso.getText().toString()):JSONObject.NULL);
            objJson.put("temperatura", !txtTemperatura.getText().toString().isEmpty()?Double.parseDouble(txtTemperatura.getText().toString()):JSONObject.NULL);
            objJson.put("qs", !txtQs.getText().toString().isEmpty()?Double.parseDouble(txtQs.getText().toString()):JSONObject.NULL);
            objJson.put("qsEfectivo", !txtQsEfectivo.getText().toString().isEmpty()?Double.parseDouble(txtQsEfectivo.getText().toString()):JSONObject.NULL);
            objJson.put("qd", !txtQd.getText().toString().isEmpty()?Double.parseDouble(txtQd.getText().toString()):JSONObject.NULL);
            objJson.put("pmas", !txtPmas.getText().toString().isEmpty()?Double.parseDouble(txtPmas.getText().toString()):JSONObject.NULL);
            objJson.put("pmenos", !txtPmenos.getText().toString().isEmpty()?Double.parseDouble(txtPmenos.getText().toString()):JSONObject.NULL);
            objJson.put("ptm", !txtPtm.getText().toString().isEmpty()?Double.parseDouble(txtPtm.getText().toString()):JSONObject.NULL);
            objJson.put("observacion", txtObservacion.getText().toString());
            objJson.put("usuarioSistema", sesion.obtenerDatosSession().get("codigoUsuario"));
            objJson.put("medicamento", removerMedicamentosInactivos());
            objJson.put("hora",  !txtHora.getText().toString().isEmpty()?(horaObtenida+"-"+minutosObtenido):"");

            Log.d(TAG, objJson.toString());

            requestQueue= Volley.newRequestQueue(this);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    objJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                progress.dismiss();
                                //scroll.setVisibility(View.VISIBLE);
                                lanzarMensaje("Transaccion exitosa");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                progress.dismiss();
                                //scroll.setVisibility(View.VISIBLE);
                                if(error.networkResponse.statusCode == 401){
                                    //Debe hacer login nuevamente y obtener un nuevo token
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonResponse = new JSONObject(jsonError);
                                    Integer codigoInterno = jsonResponse.getInt("codigo");

                                    if(codigoInterno.equals(30)){
                                        Log.d(TAG, "Bearer token expirado se solicita uno nuevo");
                                        solicitarToken();
                                    }
                                }
                                WsUtil.lanzarMensajeError(EdicionTomaSignosActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(EdicionTomaSignosActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            progress = ProgressDialog.show(this, "Procesando","Espere un momento", true, false);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void crearLogTomaSignosV2(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v2/mobile/pacientes/"+idAtencionPaciente+"/historico";
            Log.d(TAG, "URL: " + url);

            JSONObject objJson = new JSONObject();
            objJson.put("idTurno", idTurno);
            objJson.put("idSignoVital",idSignoVital!=null?idSignoVital:null);
            objJson.put("tensionArterialSistolica", Double.parseDouble(txtSistolica.getText().toString()));
            objJson.put("tensionArterialDiastolica", Double.parseDouble(txtDiastolica.getText().toString()));
            objJson.put("pulso", !txtPulso.getText().toString().isEmpty()?Double.parseDouble(txtPulso.getText().toString()):JSONObject.NULL);
            objJson.put("temperatura", !txtTemperatura.getText().toString().isEmpty()?Double.parseDouble(txtTemperatura.getText().toString()):JSONObject.NULL);
            objJson.put("qs", !txtQs.getText().toString().isEmpty()?Double.parseDouble(txtQs.getText().toString()):JSONObject.NULL);
            objJson.put("qsEfectivo", !txtQsEfectivo.getText().toString().isEmpty()?Double.parseDouble(txtQsEfectivo.getText().toString()):JSONObject.NULL);
            objJson.put("qd", !txtQd.getText().toString().isEmpty()?Double.parseDouble(txtQd.getText().toString()):JSONObject.NULL);
            objJson.put("pmas", !txtPmas.getText().toString().isEmpty()?Double.parseDouble(txtPmas.getText().toString()):JSONObject.NULL);
            objJson.put("pmenos", !txtPmenos.getText().toString().isEmpty()?Double.parseDouble(txtPmenos.getText().toString()):JSONObject.NULL);
            objJson.put("ptm", !txtPtm.getText().toString().isEmpty()?Double.parseDouble(txtPtm.getText().toString()):JSONObject.NULL);
            objJson.put("observacion", txtObservacion.getText().toString());
            objJson.put("usuarioSistema", sesion.obtenerDatosSession().get("codigoUsuario"));
            objJson.put("medicamento", removerMedicamentosInactivos());
            //Log.d(TAG, calfecha.get(Calendar.DAY_OF_MONTH)+"-"+calfecha.get(Calendar.MONTH)+"-"+calfecha.get(Calendar.YEAR));

            objJson.put("hora",  !txtHora.getText().toString().isEmpty()?(calfecha.get(Calendar.DAY_OF_MONTH)+"-"+calfecha.get(Calendar.MONTH)+"-"+calfecha.get(Calendar.YEAR)+"-"+horaObtenida+"-"+minutosObtenido):"");

            Log.d(TAG, objJson.toString());

            requestQueue= Volley.newRequestQueue(this);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    objJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //loading.setVisibility(View.GONE);
                                progress.dismiss();
                                lanzarMensaje("Transaccion exitosa");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                //loading.setVisibility(View.GONE);
                                progress.dismiss();
                                if(error.networkResponse.statusCode == 401){
                                    //Debe hacer login nuevamente y obtener un nuevo token
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonResponse = new JSONObject(jsonError);
                                    Integer codigoInterno = jsonResponse.getInt("codigo");

                                    if(codigoInterno.equals(30)){
                                        Log.d(TAG, "Bearer token expirado se solicita uno nuevo");
                                        solicitarToken();
                                    }
                                }
                                WsUtil.lanzarMensajeError(EdicionTomaSignosActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(EdicionTomaSignosActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            progress = ProgressDialog.show(this, "Procesando","Espere un momento", true, false);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String removerMedicamentosInactivos(){
        String medicamentos = "";
        try{
            for (int i = 0; i < lsMedicamentos.size(); i++){
                if(lsMedicamentos.get(i).getIsChecked().equals("S")){
                    medicamentos = medicamentos + lsMedicamentos.get(i).getIdReceta()   + "|";
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return medicamentos;
    }

    public void lanzarMensaje(String mensaje){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Corprenal");
        dialogo1.setMessage(mensaje);

        dialogo1.setCancelable(false);
        dialogo1.setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.dismiss();
                finish();
            }
        });
        dialogo1.show();
    }

    public void actualizarLogTomaSignos(){
        try{

            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/historico";
            Log.d(TAG, "URL: " + url);

            JSONObject objJson = new JSONObject();
            objJson.put("idTurno", idTurno);

            if(idSignoVital != null){
                objJson.put("idSignoVital", idSignoVital);
            }else{
                objJson.put("idSignoVital","");
            }
            objJson.put("tensionArterialSistolica", Double.parseDouble(txtSistolica.getText().toString()));
            objJson.put("tensionArterialDiastolica", Double.parseDouble(txtDiastolica.getText().toString()));
            objJson.put("temperatura", !txtTemperatura.getText().toString().isEmpty()?Double.parseDouble(txtTemperatura.getText().toString()):JSONObject.NULL);
            objJson.put("qs", !txtQs.getText().toString().isEmpty()?Double.parseDouble(txtQs.getText().toString()):JSONObject.NULL);
            objJson.put("qsEfectivo", !txtQsEfectivo.getText().toString().isEmpty()?Double.parseDouble(txtQsEfectivo.getText().toString()):JSONObject.NULL);
            objJson.put("qd", !txtQd.getText().toString().isEmpty()?Double.parseDouble(txtQd.getText().toString()):JSONObject.NULL);
            objJson.put("pmas", !txtPmas.getText().toString().isEmpty()?Double.parseDouble(txtPmas.getText().toString()):JSONObject.NULL);
            objJson.put("pmenos", !txtPmenos.getText().toString().isEmpty()?Double.parseDouble(txtPmenos.getText().toString()):JSONObject.NULL);
            objJson.put("ptm", !txtPtm.getText().toString().isEmpty()?Double.parseDouble(txtPtm.getText().toString()):JSONObject.NULL);
            objJson.put("medicamento", removerMedicamentosInactivos());
            objJson.put("observacion", txtObservacion.getText().toString());
            objJson.put("usuarioSistema", sesion.obtenerDatosSession().get("codigoUsuario"));
            objJson.put("pulso", !txtPulso.getText().toString().isEmpty()?Double.parseDouble(txtPulso.getText().toString()):JSONObject.NULL);
            objJson.put("hora", !txtHora.getText().toString().isEmpty()?(horaObtenida+"-"+minutosObtenido):"");

            Log.d(TAG, objJson.toString());
            requestQueue= Volley.newRequestQueue(this);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    objJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //loading.setVisibility(View.GONE);
                                progress.dismiss();
                                lanzarMensaje("Transaccion exitosa");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                //loading.setVisibility(View.GONE);
                                progress.dismiss();
                                if(error.networkResponse.statusCode == 401){
                                    //Debe hacer login nuevamente y obtener un nuevo token
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonResponse = new JSONObject(jsonError);
                                    Integer codigoInterno = jsonResponse.getInt("codigo");

                                    if(codigoInterno.equals(30)){
                                        Log.d(TAG, "Bearer token expirado se solicita uno nuevo");
                                        solicitarToken();
                                    }
                                }
                                WsUtil.lanzarMensajeError(EdicionTomaSignosActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(EdicionTomaSignosActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            progress = ProgressDialog.show(this, "Procesando","Espere un momento", true, false);
            //loading.setVisibility(View.VISIBLE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void actualizarLogTomaSignosV2(){
        try{

            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v2/mobile/pacientes/"+idAtencionPaciente+"/historico";
            Log.d(TAG, "URL: " + url);

            JSONObject objJson = new JSONObject();
            objJson.put("idTurno", idTurno);

            if(idSignoVital != null){
                objJson.put("idSignoVital", idSignoVital);
            }else{
                objJson.put("idSignoVital","");
            }
            objJson.put("tensionArterialSistolica", Double.parseDouble(txtSistolica.getText().toString()));
            objJson.put("tensionArterialDiastolica", Double.parseDouble(txtDiastolica.getText().toString()));
            objJson.put("temperatura", !txtTemperatura.getText().toString().isEmpty()?Double.parseDouble(txtTemperatura.getText().toString()):JSONObject.NULL);
            objJson.put("qs", !txtQs.getText().toString().isEmpty()?Double.parseDouble(txtQs.getText().toString()):JSONObject.NULL);
            objJson.put("qsEfectivo", !txtQsEfectivo.getText().toString().isEmpty()?Double.parseDouble(txtQsEfectivo.getText().toString()):JSONObject.NULL);
            objJson.put("qd", !txtQd.getText().toString().isEmpty()?Double.parseDouble(txtQd.getText().toString()):JSONObject.NULL);
            objJson.put("pmas", !txtPmas.getText().toString().isEmpty()?Double.parseDouble(txtPmas.getText().toString()):JSONObject.NULL);
            objJson.put("pmenos", !txtPmenos.getText().toString().isEmpty()?Double.parseDouble(txtPmenos.getText().toString()):JSONObject.NULL);
            objJson.put("ptm", !txtPtm.getText().toString().isEmpty()?Double.parseDouble(txtPtm.getText().toString()):JSONObject.NULL);
            objJson.put("medicamento", removerMedicamentosInactivos());
            objJson.put("observacion", txtObservacion.getText().toString());
            objJson.put("usuarioSistema", sesion.obtenerDatosSession().get("codigoUsuario"));
            objJson.put("pulso", !txtPulso.getText().toString().isEmpty()?Double.parseDouble(txtPulso.getText().toString()):JSONObject.NULL);
            //objJson.put("hora", !txtHora.getText().toString().isEmpty()?(horaObtenida+"-"+minutosObtenido):"");

            objJson.put("hora",  !txtHora.getText().toString().isEmpty()?(calfecha.get(Calendar.DAY_OF_MONTH)+"-"+calfecha.get(Calendar.MONTH)+"-"+calfecha.get(Calendar.YEAR)+"-"+horaObtenida+"-"+minutosObtenido):"");
            Log.d(TAG, objJson.toString());
            requestQueue= Volley.newRequestQueue(this);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    objJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //loading.setVisibility(View.GONE);
                                progress.dismiss();
                                lanzarMensaje("Transaccion exitosa");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                //loading.setVisibility(View.GONE);
                                progress.dismiss();
                                if(error.networkResponse.statusCode == 401){
                                    //Debe hacer login nuevamente y obtener un nuevo token
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonResponse = new JSONObject(jsonError);
                                    Integer codigoInterno = jsonResponse.getInt("codigo");

                                    if(codigoInterno.equals(30)){
                                        Log.d(TAG, "Bearer token expirado se solicita uno nuevo");
                                        solicitarToken();
                                    }
                                }
                                WsUtil.lanzarMensajeError(EdicionTomaSignosActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(EdicionTomaSignosActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            progress = ProgressDialog.show(this, "Procesando","Espere un momento", true, false);
            //loading.setVisibility(View.VISIBLE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void obtenerQsPreescrito(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            ///pacientes/{idPaciente}/medicamentos/nuevos
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/qsprescrito";
            Log.d(TAG, "URL: " + url);

            requestQueue= Volley.newRequestQueue(this);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                //loading.setVisibility(View.GONE);
                                Integer qs = response.getInt("qsPrescrito");
                                if(qs == 0){
                                    txtQs.setText("");
                                }else{
                                    txtQs.setText(qs.toString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                //loading.setVisibility(View.GONE);
                                if(error.networkResponse.statusCode == 401){
                                    //Debe hacer login nuevamente y obtener un nuevo token
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonResponse = new JSONObject(jsonError);
                                    Integer codigoInterno = jsonResponse.getInt("codigo");

                                    if(codigoInterno.equals(30)){
                                        Log.d(TAG, "Bearer token expirado se solicita uno nuevo");
                                        solicitarToken();
                                    }
                                }
                                WsUtil.lanzarMensajeError(EdicionTomaSignosActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(EdicionTomaSignosActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            //loading.setVisibility(View.VISIBLE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void obtenerRecetaNuevoLog(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            ///pacientes/{idPaciente}/medicamentos/nuevos
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/medicamentos/nuevos";
            Log.d(TAG, "URL: " + url);

            requestQueue= Volley.newRequestQueue(this);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                loading.setVisibility(View.GONE);
                                JSONArray jsonMedicamento = response.getJSONArray("receta");
                                lsMedicamentos = new ArrayList<>();
                                for (int i = 0; i < jsonMedicamento.length(); i++){
                                    MedicamentoDTO objMed = new MedicamentoDTO();
                                    objMed.cargarEntidadconJson(jsonMedicamento.getJSONObject(i));
                                    objMed.setIsChecked("N");
                                    lsMedicamentos.add(objMed);
                                    //Log.d(TAG, objMed.getDescripcionMedicamento());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                loading.setVisibility(View.GONE);
                                if(error.networkResponse.statusCode == 401){
                                    //Debe hacer login nuevamente y obtener un nuevo token
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonResponse = new JSONObject(jsonError);
                                    Integer codigoInterno = jsonResponse.getInt("codigo");

                                    if(codigoInterno.equals(30)){
                                        Log.d(TAG, "Bearer token expirado se solicita uno nuevo");
                                        solicitarToken();
                                    }
                                }
                                WsUtil.lanzarMensajeError(EdicionTomaSignosActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(EdicionTomaSignosActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void obtenerRecetaActuaLog(){
        try{
            final String token = sesion.obtenerDatosSession().get("tokenWs").toString();
            String url = this.getString(R.string.base_path) + "/v1/mobile/pacientes/"+idAtencionPaciente+"/"+idSignoVital+"/medicamentos";
            Log.d(TAG, "URL: " + url);

            requestQueue= Volley.newRequestQueue(this);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                loading.setVisibility(View.GONE);
                                JSONArray jsonMedicamento = response.getJSONArray("receta");
                                lsMedicamentos = new ArrayList<>();
                                for (int i = 0; i < jsonMedicamento.length(); i++){
                                    MedicamentoDTO objMed = new MedicamentoDTO();
                                    objMed.cargarEntidadconJson(jsonMedicamento.getJSONObject(i));
                                    lsMedicamentos.add(objMed);
                                    Log.d(TAG, objMed.getDescripcionMedicamento()+" "+objMed.getIsChecked());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try{
                                loading.setVisibility(View.GONE);
                                if(error.networkResponse.statusCode == 401){
                                    //Debe hacer login nuevamente y obtener un nuevo token
                                    String jsonError = new String(error.networkResponse.data);
                                    JSONObject jsonResponse = new JSONObject(jsonError);
                                    Integer codigoInterno = jsonResponse.getInt("codigo");

                                    if(codigoInterno.equals(30)){
                                        Log.d(TAG, "Bearer token expirado se solicita uno nuevo");
                                        solicitarToken();
                                    }
                                }
                                WsUtil.lanzarMensajeError(EdicionTomaSignosActivity.this, error.networkResponse);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return WsUtil.generarHeader(EdicionTomaSignosActivity.this,null,null,token, MediaType.JSON_UTF_8);
                }

            };
            jsonRequest.setRetryPolicy(WsUtil.setTimeOut());

            requestQueue.add(jsonRequest);
            loading.setVisibility(View.VISIBLE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void solicitarToken(){
        String url = this.getString(R.string.base_path) + "/v1/mobile/login";
        final String usuario = sesion.obtenerDatosSession().get("codigoUsuario").toString();
        final String clave = sesion.obtenerDatosSession().get("claveUsuario").toString();

        Log.d("LoginActivity", "URL: " + url);

        JSONObject objJson = new JSONObject();

        requestQueue= Volley.newRequestQueue(this);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                objJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loading.setVisibility(View.GONE);
                            String newToken = response.getString("token");
                            sesion.actualizarToken(newToken);
                            Log.d(TAG, "Se genero nuevo token");
                            if(edicion){
                                actualizarLogTomaSignos();
                            }else{
                                crearLogTomaSignos();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            loading.setVisibility(View.GONE);
                            WsUtil.lanzarMensajeError(EdicionTomaSignosActivity.this, error.networkResponse);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return WsUtil.generarHeader(EdicionTomaSignosActivity.this,usuario,clave,null, MediaType.JSON_UTF_8);
            }

        };

        jsonRequest.setRetryPolicy(WsUtil.setTimeOut());
        requestQueue.add(jsonRequest);
        loading.setVisibility(View.VISIBLE);
    }

    private boolean validarCampos(){
        if(txtSistolica.getText().toString().equals("")){
            txtInpSistolica.setErrorEnabled(true);
            txtInpSistolica.setError("Ingresa TA(Sistólica)");
            txtSistolica.requestFocus();
            Toast.makeText(this,"Ingresa TA(Sistólica)",Toast.LENGTH_LONG).show();
            return false;
        }
        if(txtDiastolica.getText().toString().equals("")) {
            txtInpDiastolica.setErrorEnabled(true);
            txtInpDiastolica.setError("Ingresa TA(Diastólica)");
            txtDiastolica.requestFocus();
            Toast.makeText(this,"Ingresa TA(Diastólica)",Toast.LENGTH_LONG).show();
            return false;
        }
        /*if(txtPulso.getText().toString().equals("")){
            txtInpPulso.setErrorEnabled(true);
            txtInpPulso.setError("Ingresa Pulso");
            txtPulso.requestFocus();
            Toast.makeText(this,"Ingresa Pulso",Toast.LENGTH_LONG).show();
            return false;
        }
        if(txtTemperatura.getText().toString().equals("")){
            txtInpTemperatura.setErrorEnabled(true);
            txtInpTemperatura.setError("Ingresa Temperatura");
            txtTemperatura.requestFocus();
            Toast.makeText(this,"Ingresa Temperatura",Toast.LENGTH_LONG).show();
            return false;
        }
        if(txtQs.getText().toString().equals("")){
            txtInpQs.setErrorEnabled(true);
            txtInpQs.setError("Ingresa valor Qs");
            txtQs.requestFocus();
            Toast.makeText(this,"Ingresa valor Qs",Toast.LENGTH_LONG).show();
            return false;
        }
        if(txtQd.getText().toString().equals("")){
            txtInpQd.setErrorEnabled(true);
            txtInpQd.setError("Ingresa valor Qd");
            txtQd.requestFocus();
            Toast.makeText(this,"Ingresa valor Qd",Toast.LENGTH_LONG).show();
            return false;
        }
        if(txtPmas.getText().toString().equals("")){
            txtInpPmas.setErrorEnabled(true);
            txtInpPmas.setError("Ingresa valor P+");
            txtPmas.requestFocus();
            Toast.makeText(this,"Ingresa valor P+",Toast.LENGTH_LONG).show();
            return false;
        }
        if(txtPmenos.getText().toString().equals("")){
            txtInpPmenos.setErrorEnabled(true);
            txtInpPmenos.setError("Ingresa P-");
            txtPmenos.requestFocus();
            Toast.makeText(this,"Ingresa P-",Toast.LENGTH_LONG).show();
            return false;
        }
        if(txtPtm.getText().toString().equals("")){
            txtInpPtm.setErrorEnabled(true);
            txtInpPtm.setError("Ingresa valor PTM");
            txtPtm.requestFocus();
            Toast.makeText(this,"Ingresa valor PTM",Toast.LENGTH_LONG).show();
            return false;
        }
        if(txtHora.getText().toString().equals("")){
            txtInpHora.setErrorEnabled(true);
            txtInpHora.setError("Ingresa Hora");
            txtHora.requestFocus();
            Toast.makeText(this,"Ingresa Hora",Toast.LENGTH_LONG).show();
            return false;
        }
        if(txtObservacion.getText().toString().equals("")){
            txtInpObservacion.setErrorEnabled(true);
            txtInpObservacion.setError("Ingresa Observación");
            return false;
        }*/
        return true;
    }

    private void obtenerHora(){
        TimePickerDialog
                recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                //horaObtenida =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                //minutosObtenido = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario

                if(hourOfDay>=0 && hourOfDay <=9){
                    horaObtenida = "0"+String.valueOf(hourOfDay);
                }else{
                    horaObtenida = String.valueOf(hourOfDay);
                }
                if(minute>=0 && minute <=9){
                    minutosObtenido = "0"+String.valueOf(minute);
                }else{
                    minutosObtenido = String.valueOf(minute);
                }

                /*String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "A.M.";
                } else {
                    AM_PM = "P.M.";
                }*/
                //Muestro la hora con el formato deseado
                txtHora.setText(horaObtenida + DOS_PUNTOS + minutosObtenido);
            }
            //Estos valores deben ir en ese orden
            //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
            //Pero el sistema devuelve la hora en formato 24 horas
        }, hora, minuto, true);
        recogerHora.show();
    }

    private void obtenerDia(int mYear, int mMonth, int mDay){


            DatePickerDialog dpd = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            dia = dayOfMonth;
                            mes = monthOfYear;
                            anio = year;
                            calfecha.set(anio, mes, dia);
                            txtDia.setText(formatoDate.format(calfecha.getTime()));
                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
            dpd.getDatePicker().setMaxDate((new Date()).getTime());
        }
}
