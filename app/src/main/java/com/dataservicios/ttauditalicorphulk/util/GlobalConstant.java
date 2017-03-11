package com.dataservicios.ttauditalicorphulk.util;
/**
 * Created by usuario on 11/11/2014.
 */

public final class GlobalConstant {
    public static String dominio = "http://ttaudit.com";
   // public static String dominio = "http://appfiliaibk.com";
    public static final String LOGIN_URL = dominio + "/loginUser";
    public static final String KEY_USERNAME = "username";
    public static String inicio,fin;
    public static  double latitude_open, longitude_open;
    public static  int global_close_audit =0;
    public static int company_id = 67;
    // public static String albunName = "AlicorpPhoto";
    //public static String directory_images = "/Pictures/" + albunName;
    public static String directory_images = "/Pictures/" ;
    public static String type_aplication = "android";
    public static int[] poll_id = new int[]{
        963, // 	0	Se encuentra Abierto el punto?
        964, // 	1	¿Cliente permitió tomar información?
        965, // 	2	CLIENTE TIENE CONOCIMIENTO DEL PROGRAMA?
        966, // 	3	CONOCE LA CUOTA
        967, // 	4	Existe Ganchera
        968, // 	5	Stock Quitamanchas
        969, // 	6	Stock Suavizantes
        970, // 	7	Ganchera Contaminada
        971, // 	8	Existe Ventana?
        972, // 	9	Se encuentra fronterizada?

} ;

    public static int[] audit_id = new int[]{
            50, // 0	Preguntas Hulk
            49, // 1	Visibilidad Hulk
    } ;

    public static final String JPEG_FILE_PREFIX = "_alicorp_h_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";
}

