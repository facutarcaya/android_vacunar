package com.example.micovid.asincronico;

import android.os.AsyncTask;
import android.util.Log;

import com.example.micovid.actividadprincipal.Usuario;
import com.example.micovid.comm.Communication;
import com.example.micovid.pantallaprincipal.PantallaInicioActivity;
import com.example.micovid.registrar.RegistrarActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsincroTaskRegistrar extends AsyncTask<Object, Void, Boolean> {
    private RegistrarActivity registrarActivity;
    private String mensaje;
    private Usuario usuario;


    public AsincroTaskRegistrar(RegistrarActivity registrarActivity) {
        this.registrarActivity = registrarActivity;
    }

    @Override
    protected void onPreExecute() {
        this.registrarActivity.toggleProgressBar(true);
        this.registrarActivity.habilitarBotones(false);
    }

    @Override
    protected Boolean doInBackground(Object... objects) {
        Communication communication = new Communication();

        String respuesta = communication.registrarUsuario(objects[0].toString(),objects[1].toString(),objects[2].toString(),objects[3].toString(),objects[4].toString());

        if(respuesta.compareTo(communication.MSG_ERROR) == 0) {
            this.mensaje = "Error en la conexión, intente de nuevo más tarde";
            return false;
        }

        JSONObject answer = null;
        try {
            answer = new JSONObject(respuesta);

            if (answer.get("success").toString().compareTo("true") == 0) {
                this.usuario = new Usuario(objects[3].toString(),answer.get("token").toString(),answer.get("token_refresh").toString());

                return true;
            } else {
                this.mensaje = answer.get("msg").toString();

                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            this.mensaje = "Error inesperado, intente nuevamente";
            return false;
        }
    }
    public static StringBuilder convertInputStreamToString(InputStreamReader inputStreamReader) throws IOException {
        BufferedReader br = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ( (line = br.readLine()) != null ){
            stringBuilder.append(line + "\n");
        }
        br.close();
        return stringBuilder;
    }
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        this.registrarActivity.toggleProgressBar(false);
        if(aBoolean) {
            this.registrarActivity.lanzarActivity(PantallaInicioActivity.class, this.usuario);
            this.registrarActivity.showMessage("Datos correctos");
        } else {
            this.registrarActivity.habilitarBotones(true);
            this.registrarActivity.showMessage(this.mensaje);
        }
        super.onPostExecute(aBoolean);
    }
}
