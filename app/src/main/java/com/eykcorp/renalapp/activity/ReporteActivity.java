package com.eykcorp.renalapp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.eykcorp.renalapp.R;
import com.eykcorp.renalapp.com.eykcorp.renalapp.util.GenericActivity;

/**
 * Created by Galo on 12/05/2018.
 */

public class ReporteActivity extends GenericActivity {

    private String TAG = "ReporteActivity";
    private WebView wbReporte;

    private TextView txvTituloPaciente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle parametros = new Bundle();
        parametros.putInt("layoutActivity", R.layout.activity_reporte);
        parametros.putString("mostrarSalir", "S");
        super.onCreate(parametros);
        txvTituloPaciente = (TextView) findViewById(R.id.txvTituloPaciente);

        String url = "";
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            url = (bundle.get("urlReporte").toString());
            txvTituloPaciente.setText(bundle.get("nombrePaciente").toString());
        }

        Log.d(TAG, url);
        wbReporte = (WebView) findViewById(R.id.wbReporte);
        wbReporte.getSettings().setJavaScriptEnabled(true);
        wbReporte.setWebChromeClient(new WebChromeClient() {
            private ProgressDialog mProgress;

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (mProgress == null) {
                    mProgress = new ProgressDialog(ReporteActivity.this);
                    mProgress.show();
                }
                mProgress.setMessage("Cargando reporte... " + String.valueOf(progress) + "%");
                if (progress == 100) {
                    mProgress.dismiss();
                    mProgress = null;
                }
            }
        });
        wbReporte.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onBackPressed();
    }
}
