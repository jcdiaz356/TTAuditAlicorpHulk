package com.dataservicios.ttauditalicorphulk.AditoriaAlicorp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.ttauditalicorphulk.AndroidCustomGalleryActivity;
import com.dataservicios.ttauditalicorphulk.DetallePdv;
import com.dataservicios.ttauditalicorphulk.MainActivity;
import com.dataservicios.ttauditalicorphulk.Model.Audit;
import com.dataservicios.ttauditalicorphulk.Model.PollDetail;
import com.dataservicios.ttauditalicorphulk.R;
import com.dataservicios.ttauditalicorphulk.SQLite.DatabaseHelper;
import com.dataservicios.ttauditalicorphulk.util.AuditUtil;
import com.dataservicios.ttauditalicorphulk.util.GlobalConstant;
import com.dataservicios.ttauditalicorphulk.util.JSONParserX;
import com.dataservicios.ttauditalicorphulk.util.SessionManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Jaime on 19/03/2016.
 */
public class StoreOpenClose extends Activity {
    private Activity MyActivity = this ;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SessionManager session;

    private Switch sw_open, swPermitio ;
    private Button bt_photo, bt_guardar;
    private EditText et_Comentario,etComent1,etComent2;
    private TextView tv_Pregunta;
    private LinearLayout lyPermitio, lyOpenClose, lyAuditoria;

    private String tipo,cadenaruc, fechaRuta, comentario1="",comentario2="", type, region,typeBodega;

    private Integer user_id, company_id,store_id,rout_id,audit_id, product_id, poll_id, poll_id2;

    int  is_open=0 ,is_permitio=0;


    private DatabaseHelper db;

    private ProgressDialog pDialog;

    private RadioGroup rgOpt1;
    private RadioGroup rgOpt2;
    private String opt1="",opt2="";

    private RadioButton[] radioButton1Array;
    private RadioButton[] radioButton2Array;

    private PollDetail pollDetail, pollDetail2;
    private Audit mAudit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_open_close);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Tienda");

        sw_open = (Switch) findViewById(R.id.swOpen);
        swPermitio = (Switch) findViewById(R.id.swPermitio);
        lyPermitio = (LinearLayout) findViewById(R.id.lyPermitio);
        lyAuditoria = (LinearLayout) findViewById(R.id.lyAuditoria);
        lyOpenClose = (LinearLayout) findViewById(R.id.lyOpenClose);
        rgOpt1=(RadioGroup) findViewById(R.id.rgOpt1);
        rgOpt2=(RadioGroup) findViewById(R.id.rgOpt2);
        radioButton1Array = new RadioButton[] {
                (RadioButton) findViewById(R.id.rbA1),
                (RadioButton) findViewById(R.id.rbB1),
                (RadioButton) findViewById(R.id.rbC1),
                (RadioButton) findViewById(R.id.rbD1),
        };
        radioButton2Array = new RadioButton[] {
                (RadioButton) findViewById(R.id.rbA2),
                (RadioButton) findViewById(R.id.rbB2),
                (RadioButton) findViewById(R.id.rbC2),
                (RadioButton) findViewById(R.id.rbD2),
        };

        tv_Pregunta = (TextView) findViewById(R.id.tvPregunta);
        bt_guardar = (Button) findViewById(R.id.btGuardar);
        bt_photo = (Button) findViewById(R.id.btPhoto);
        //et_Comentario = (EditText) findViewById(R.id.etComentario);
        etComent1 = (EditText) findViewById(R.id.etComent1);
        etComent2 = (EditText) findViewById(R.id.etComent2);

        Bundle bundle = getIntent().getExtras();
        company_id = GlobalConstant.company_id;
        store_id = bundle.getInt("idPDV");
        rout_id = bundle.getInt("idRuta");
        region = bundle.getString("region");
        fechaRuta = bundle.getString("fechaRuta");
        type = bundle.getString("type");
        typeBodega = bundle.getString("typeBodega");



        audit_id = GlobalConstant.audit_id[0]; //Inicio de auditoría Alicorp
        poll_id = GlobalConstant.poll_id[0] ;// 5 "Se encuentra Abierto el punto?"
        poll_id2 = GlobalConstant.poll_id[1] ;// 6 "¿Cliente permitió tomar información?"

        //poll_id = 72 , solo para exhibiciones de bayer, directo de la base de datos

        pDialog = new ProgressDialog(MyActivity);
        pDialog.setMessage(getString(R.string.text_loading));
        pDialog.setCancelable(false);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // id
        user_id = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;

        tv_Pregunta.setText("¿Se encuentra abierto el establecimiento?");


        rgOpt1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radioButton1Array[3].isChecked())
                {
                    etComent1.setEnabled(true);
                    etComent1.setVisibility(View.VISIBLE);
                    etComent1.setText("");
                }
                else
                {
                    etComent1.setEnabled(false);
                    etComent1.setVisibility(View.INVISIBLE);
                    etComent1.setText("");
                }
            }
        });

        rgOpt2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radioButton2Array[3].isChecked())
                {
                    etComent2.setEnabled(true);
                    etComent2.setVisibility(View.VISIBLE);
                    etComent2.setText("");
                }
                else
                {
                    etComent2.setEnabled(false);
                    etComent2.setVisibility(View.INVISIBLE);
                    etComent2.setText("");
                }
            }
        });


        sw_open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_open = 1;
                    bt_photo.setVisibility(View.INVISIBLE);
                    bt_photo.setEnabled(false);
                    lyPermitio.setEnabled(false);
                    lyPermitio.setVisibility(View.VISIBLE);
                    lyOpenClose.setVisibility(View.INVISIBLE);
                    lyOpenClose.setEnabled(false);
                    swPermitio.setChecked(false);
                    //clearRadioButtonCheck(radioButton1Array, false);
                    rgOpt1.clearCheck();
                    is_permitio = 0;
                } else {
                    is_open = 0;
                    bt_photo.setVisibility(View.VISIBLE);
                    bt_photo.setEnabled(true);
                    lyPermitio.setEnabled(true);
                    lyPermitio.setVisibility(View.INVISIBLE);
                    lyOpenClose.setVisibility(View.VISIBLE);
                    lyOpenClose.setEnabled(true);
                    swPermitio.setChecked(false);
                    //clearRadioButtonCheck(radioButton1Array, false);
                    rgOpt1.clearCheck();
                    is_permitio = 0;
                }
            }
        });
        swPermitio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lyAuditoria.setVisibility(View.INVISIBLE);
                    lyAuditoria.setEnabled(false);
                    is_permitio = 1;
                    //clearRadioButtonCheck(radioButton2Array, false);
                    rgOpt2.clearCheck();

                } else {
                    lyAuditoria.setVisibility(View.VISIBLE);
                    lyAuditoria.setEnabled(true);
                    is_permitio = 0;
                    //clearRadioButtonCheck(radioButton2Array, false);
                    rgOpt2.clearCheck();
                }
            }
        });

        bt_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                opt1="";
                opt2="";
                if(!sw_open.isChecked()){
//                    if(!evaluateOptionRadioButton(radioButton1Array)) {
//                        Toast.makeText(getApplicationContext(), "Si se encuentra el establecimiento cerrado, selecione una opción?", Toast.LENGTH_SHORT).show();
//                        return ;
//                    } else {
                        long id1 = rgOpt1.getCheckedRadioButtonId();
                        if (id1 == -1){
                            Toast.makeText(MyActivity,"Si se encuentra el establecimiento cerrado, selecione una opción?" , Toast.LENGTH_LONG).show();
                            return;
                        }
                        else{
                            for (int x = 0; x < radioButton1Array.length; x++) {
                                if(id1 ==  radioButton1Array[x].getId())  opt1 = poll_id.toString() + radioButton1Array[x].getTag();
                            }

                        }
//                    }
                } else {
                    if(!swPermitio.isChecked()){

                        long id2 = rgOpt2.getCheckedRadioButtonId();
                        if (id2 == -1){
                            Toast.makeText(MyActivity,"Si el cliente no permite tomar información, indique por que" , Toast.LENGTH_LONG).show();
                            return;
                        }
                        else{
                            for (int x = 0; x < radioButton2Array.length; x++) {
                                if(id2 ==  radioButton2Array[x].getId())  opt2 = poll_id2.toString() + radioButton2Array[x].getTag();
                            }

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
                        comentario1 = String.valueOf(etComent1.getText()) ;
                        comentario2 = String.valueOf(etComent2.getText()) ;

                        pollDetail = new PollDetail();
                        pollDetail.setPoll_id(poll_id);
                        pollDetail.setStore_id(store_id);
                        pollDetail.setSino(1);
                        pollDetail.setOptions(1);
                        pollDetail.setLimits(0);
                        pollDetail.setMedia(0);
                        pollDetail.setComment(0);
                        pollDetail.setResult(is_open);
                        pollDetail.setLimite("0");
                        pollDetail.setComentario("");
                        pollDetail.setAuditor(user_id);
                        pollDetail.setProduct_id(0);
                        pollDetail.setCategory_product_id(0);
                        pollDetail.setPublicity_id(0);
                        pollDetail.setCompany_id(GlobalConstant.company_id);
                        pollDetail.setCommentOptions(1);
                        pollDetail.setSelectdOptions(opt1);
                        pollDetail.setSelectedOtionsComment(comentario1);
                        pollDetail.setPriority("0");

                        pollDetail2 = new PollDetail();
                        pollDetail2.setPoll_id(poll_id2);
                        pollDetail2.setStore_id(store_id);
                        pollDetail2.setSino(1);
                        pollDetail2.setOptions(1);
                        pollDetail2.setLimits(0);
                        pollDetail2.setMedia(0);
                        pollDetail2.setComment(0);
                        pollDetail2.setResult(is_permitio);
                        pollDetail2.setLimite("0");
                        pollDetail2.setComentario("");
                        pollDetail2.setAuditor(user_id);
                        pollDetail2.setProduct_id(0);
                        pollDetail2.setCategory_product_id(0);
                        pollDetail2.setPublicity_id(0);
                        pollDetail2.setCompany_id(GlobalConstant.company_id);
                        pollDetail2.setCommentOptions(1);
                        pollDetail2.setSelectdOptions(opt2);
                        pollDetail2.setSelectedOtionsComment(comentario2);
                        pollDetail2.setPriority("0");

                        new loadPoll().execute();

                       // new loadPoll().execute();
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

    private void takePhoto() {

        Intent i = new Intent( MyActivity, AndroidCustomGalleryActivity.class);
        Bundle bolsa = new Bundle();

        bolsa.putString("store_id", String.valueOf(store_id));
        bolsa.putString("product_id", String.valueOf("0"));
        bolsa.putString("publicities_id", String.valueOf("0"));
        bolsa.putString("poll_id", String.valueOf(poll_id));
        bolsa.putString("sod_ventana_id", String.valueOf("0"));
        bolsa.putString("company_id", String.valueOf(GlobalConstant.company_id));
        bolsa.putString("category_product_id", "0");
        bolsa.putString("monto","");
        bolsa.putString("razon_social","");
        bolsa.putString("url_insert_image", GlobalConstant.dominio + "/insertImagesProductPollAlicorp");
        bolsa.putString("tipo", "1");
        i.putExtras(bolsa);
        startActivity(i);
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



            String time_close = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
            mAudit = new Audit();
            mAudit.setCompany_id(GlobalConstant.company_id);
            mAudit.setStore_id(store_id);
            mAudit.setId(audit_id);
            mAudit.setRoute_id(rout_id);
            mAudit.setUser_id(user_id);
            mAudit.setLatitude_close("");
            mAudit.setLongitude_close("");
            mAudit.setLatitude_open(String.valueOf(GlobalConstant.latitude_open));
            mAudit.setLongitude_open(String.valueOf(GlobalConstant.longitude_open));
            mAudit.setTime_open(GlobalConstant.inicio);
            mAudit.setTime_close(time_close);

            if(is_open == 1) {

                if(is_permitio == 1){
                    if(!AuditUtil.insertPollDetail(pollDetail)) return false;
                    if(!AuditUtil.insertPollDetail(pollDetail2)) return false;
                } else {
                    if(!AuditUtil.insertPollDetail(pollDetail)) return false;
                    if(!AuditUtil.insertPollDetail(pollDetail2)) return false;
                    if(!AuditUtil.closeAuditRoadStore(mAudit)) return false;
                    if(!AuditUtil.closeAuditRoadAll(mAudit)) return false;
                }

            } else{

                if(!AuditUtil.insertPollDetail(pollDetail)) return false;
                //if(!AuditUtil.insertPollDetail(pollDetail2)) return false;
                if(!AuditUtil.closeAuditRoadStore(mAudit)) return false;
                if(!AuditUtil.closeAuditRoadAll(mAudit)) return false;

            }


            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                // loadLoginActivity();
                if(is_open==1) {

                    if(is_permitio == 1){
                        Bundle argRuta = new Bundle();
                        argRuta.clear();
                        argRuta.putInt("idPDV", store_id);
                        argRuta.putString("fechaRuta", fechaRuta);
                        argRuta.putInt("idAuditoria", audit_id);
                        argRuta.putInt("rout_id", rout_id);
                        argRuta.putString("type", type);
                        argRuta.putString("region", region);
                        argRuta.putString("typeBodega", typeBodega);

                        Intent intent;
                        //intent = new Intent(MyActivity, Product.class);
                        //intent = new Intent(MyActivity, TipoDex.class);
                        intent = new Intent(MyActivity, DetallePdv.class);
                        intent.putExtras(argRuta);
                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                    }


                } else if(is_open==0){
//                    Bundle argRuta = new Bundle();
//                    argRuta.clear();
//                    argRuta.putInt("store_id", store_id);
//                    argRuta.putInt("rout_id", rout_id);
//                    Intent intent;
//                    intent = new Intent(MyActivity, Ubicacion.class);
//                    intent.putExtras(argRuta);
//                    startActivity(intent);
                    finish();
                }



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




    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
