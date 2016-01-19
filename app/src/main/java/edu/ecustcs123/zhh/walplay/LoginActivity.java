package edu.ecustcs123.zhh.walplay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import edu.ecustcs123.zhh.walplay.Utils.AppConstant;
import edu.ecustcs123.zhh.walplay.Utils.AppStatus;
import edu.ecustcs123.zhh.walplay.Utils.Coding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private Button btn_register;
    private EditText et_cell;
    private EditText et_pwd;

    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);

        et_cell = (EditText) findViewById(R.id.et_loginCell);
        et_pwd = (EditText) findViewById(R.id.et_loginPwd);

        Intent i = getIntent();
        et_cell.setText(i.getStringExtra("cell"));

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
                String cell = String.valueOf(et_cell.getText());
                String pwd = String.valueOf(et_pwd.getText());
                intent.putExtra("cell",cell);
                intent.putExtra("pwd",pwd);
                startActivity(intent);
            }
        });
        requestQueue = Volley.newRequestQueue(this);
    }

    private void authCheck(){
        String cell = String.valueOf(et_cell.getText());
        String password = Coding.MD5(String.valueOf(et_pwd.getText()));
        String url = AppConstant.URL_domain+"login";

        HashMap<String, String> params = new HashMap<>();
        params.put("cell",cell);
        params.put("password",password);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO something
                        int errorCode = -1;
                        try {
                            errorCode = response.getInt("code");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(errorCode == 0){
                            try {
                                Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();

                                String cell = response.getString("cell");
                                String token = response.getString("token");

                                //save login info to SPF for auto login
                                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(AppConstant.PrefKey.user_name, cell);
                                editor.putString(AppConstant.PrefKey.user_cell, cell);
                                editor.putString(AppConstant.PrefKey.user_token, token);
                                editor.putBoolean(AppConstant.PrefKey.user_logged,true);
                                editor.apply();

                                //update appStatus
                                AppStatus appStatus =(AppStatus) getApplicationContext();
                                appStatus.setIsLogin(true);
                                appStatus.setUserName(cell);
                                appStatus.setUserToken(token);

                                Log.d(AppConstant.LOG.WPDVolley+"login","success_"+token);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(errorCode == 1){
                            Toast.makeText(getApplicationContext(),"登录信息有误",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"错误:"+String.valueOf(errorCode),Toast.LENGTH_LONG).show();
                        }
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
