package edu.ecustcs123.zhh.walplay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import edu.ecustcs123.zhh.walplay.Utils.AppConstant;
import edu.ecustcs123.zhh.walplay.Utils.Coding;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private Button btn_register;
    private TextView tv_cell;
    private TextView tv_pwd;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);

        tv_cell = (TextView) findViewById(R.id.tv_loginCell);
        tv_pwd = (TextView) findViewById(R.id.tv_loginPwd);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCheck();

            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),RegisterActivity.class);
                String cell = String.valueOf(tv_cell.getText());
                String pwd = String.valueOf(tv_pwd.getText());
                intent.putExtra("cell",cell);
                intent.putExtra("pwd",pwd);
                startActivity(intent);
            }
        });
        requestQueue = Volley.newRequestQueue(this);
    }

    private void authCheck(){
        String cell = String.valueOf(tv_cell.getText());
        String password = Coding.MD5(String.valueOf(tv_pwd.getText()));
        String url = AppConstant.URL_domain+"login";

        HashMap<String, String> params = new HashMap<>();
        params.put("cell",cell);
        params.put("password",password);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO something
                        VolleyLog.v("Response", response.toString());
                        Log.v("volleyResponse",response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }




}
