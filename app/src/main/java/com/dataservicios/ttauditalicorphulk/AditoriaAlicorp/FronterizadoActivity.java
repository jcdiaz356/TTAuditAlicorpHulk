package com.dataservicios.ttauditalicorphulk.AditoriaAlicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.ttauditalicorphulk.AndroidCustomGalleryActivity;
import com.dataservicios.ttauditalicorphulk.MainActivity;
import com.dataservicios.ttauditalicorphulk.Model.PollDetail;
import com.dataservicios.ttauditalicorphulk.Model.Publicity;
import com.dataservicios.ttauditalicorphulk.R;
import com.dataservicios.ttauditalicorphulk.SQLite.DatabaseHelper;
import com.dataservicios.ttauditalicorphulk.util.AuditUtil;
import com.dataservicios.ttauditalicorphulk.util.GlobalConstant;
import com.dataservicios.ttauditalicorphulk.util.SessionManager;

import java.util.HashMap;

public class FronterizadoActivity extends Activity {

    private Activity MyActivity = this ;
    private static final String LOG_TAG = FronterizadoActivity.class.getSimpleName();
    private SessionManager session;

    private Switch swSiNo ;
    private Button bt_guardar;

    private TextView tv_Pregunta;


    private String tipo,cadenaruc, fechaRuta;

    private Integer user_id,store_id,rout_id,audit_id, publicity_id, poll_id, company_id ;

    int  is_sino=0 ;
    private LinearLayout lyContent;

    private RadioGroup rgOpt1;
    private String opt1="";

    private DatabaseHelper db;
    private ProgressDialog pDialog;
    private PollDetail pollDetail;
    private RadioButton[] radioButton1Array;
    Publicity publicity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fronterizado);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Se encuentra fronterizada");

        swSiNo = (Switch) findViewById(R.id.swSiNo);

        lyContent = (LinearLayout) findViewById(R.id.lyContent);
        rgOpt1=(RadioGroup) findViewById(R.id.rgOpt1);

        radioButton1Array = new RadioButton[] {
                (RadioButton) findViewById(R.id.rbA1),
                (RadioButton) findViewById(R.id.rbB1),
                (RadioButton) findViewById(R.id.rbC1),

        };

        // tv_Pregunta = (TextView) findViewById(R.id.tvPregunta);
        bt_guardar = (Button) findViewById(R.id.btGuardar);

        Bundle bundle = getIntent().getExtras();

        company_id = GlobalConstant.company_id;
        store_id = bundle.getInt("store_id");
        rout_id = bundle.getInt("rout_id");
        publicity_id = bundle.getInt("publicity_id");
        audit_id = GlobalConstant.audit_id[1];
        fechaRuta = bundle.getString("fechaRuta");

        poll_id = GlobalConstant.poll_id[9]; // 0 "Existe Ventana?"

        db = new DatabaseHelper(getApplicationContext());
        publicity = new Publicity();
        publicity = db.getPublicity(publicity_id);

        //poll_id = 72 , solo para exhibiciones de bayer, directo de la base de datos

        pDialog = new ProgressDialog(MyActivity);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;

        swSiNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_sino = 1;
                } else {
                    is_sino = 0;
                }
            }
        });


        swSiNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_sino = 1;
                    lyContent.setEnabled(true);
                    lyContent.setVisibility(View.VISIBLE);
                    rgOpt1.clearCheck();

                } else {
                    is_sino = 0;
                    lyContent.setEnabled(false);
                    lyContent.setVisibility(View.INVISIBLE);
                    rgOpt1.clearCheck();
                }
            }
        });

        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                opt1="";

                if(swSiNo.isChecked()){

                    long id1 = rgOpt1.getCheckedRadioButtonId();
                    if (id1 == -1){
                        Toast.makeText(MyActivity, R.string.message_select_options , Toast.LENGTH_LONG).show();
                        return;
                    }
                    else{
                        for (int x = 0; x < radioButton1Array.length; x++) {
                            if(id1 ==  radioButton1Array[x].getId())  opt1 = poll_id.toString() + radioButton1Array[x].getTag();
                        }
                    }
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        pollDetail = new PollDetail();
                        pollDetail.setPoll_id(poll_id);
                        pollDetail.setStore_id(store_id);
                        pollDetail.setSino(1);
                        pollDetail.setOptions(1);
                        pollDetail.setLimits(0);
                        pollDetail.setMedia(0);
                        pollDetail.setComment(0);
                        pollDetail.setResult(is_sino);
                        pollDetail.setLimite("0");
                        pollDetail.setComentario("");
                        pollDetail.setAuditor(user_id);
                        pollDetail.setProduct_id(0);
                        pollDetail.setCategory_product_id(0);
                        pollDetail.setPublicity_id(publicity_id);
                        pollDetail.setCompany_id(GlobalConstant.company_id);
                        pollDetail.setCommentOptions(0);
                        pollDetail.setSelectdOptions(opt1);
                        pollDetail.setSelectedOtionsComment("");
                        pollDetail.setPriority("0");

                        new loadPoll().execute();

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);

            }
        });

    }

    class loadPoll extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");
            pDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            if(!AuditUtil.insertPollDetail(pollDetail)) return false;

            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){

                    publicity.setActive(0);
                    db.updatePublicity(publicity);
                    finish();

            } else {
                Toast.makeText(MyActivity , "No se pudo guardar la información intentelo nuevamente", Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
//                this.finish();
//                Intent a = new Intent(this,PanelAdmin.class);
//                //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(a);
//                overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }


//    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
//        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
//    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            //Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar pongase en contácto con el administrador", Toast.LENGTH_LONG).show();
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(MyActivity, "No se puede volver atras, los datos ya fueron guardado, para modificar póngase en contácto con el administrador", Toast.LENGTH_LONG).show();
//        super.onBackPressed();
//        this.finish();
//
//        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }

}
