package edu.ecustcs123.zhh.walplay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import edu.ecustcs123.zhh.walplay.Utils.AppConstant;
import edu.ecustcs123.zhh.walplay.Utils.Coding;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_regCell;
    private EditText et_regPassword;
    private EditText et_smsCode;
    private Button btn_verify;
    private Button btn_register;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init(){
        sharedPreferences = this.getPreferences(MODE_PRIVATE);
        et_regCell = (EditText) findViewById(R.id.regCell);
        et_regPassword = (EditText) findViewById(R.id.regPassword);
        et_smsCode = (EditText) findViewById(R.id.smsCode);
        btn_verify = (Button) findViewById(R.id.btn_verify);
        btn_register = (Button) findViewById(R.id.btn_register);
        requestQueue = Volley.newRequestQueue(this);

        Intent i = getIntent();
        et_regCell.setText(i.getStringExtra("cell"));
        et_regPassword.setText(i.getStringExtra("pwd"));

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register(v.getContext());
            }
        });
    }

    private void Register(final Context context){
        String cell = String.valueOf(et_regCell.getText());
        String password = Coding.MD5(String.valueOf(et_regPassword.getText()));
        String url = AppConstant.URL_domain+"register";

        HashMap<String, String> params = new HashMap<>();
        params.put("cell",cell);
        params.put("password",password);

        Log.d(AppConstant.LOG.WPDJson+"url",url);
        Log.d(AppConstant.LOG.WPDJson+"cell",cell);
        Log.d(AppConstant.LOG.WPDJson+"password",password);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int errCode = response.getInt("code");
                            if(errCode == 0){//成功注册
                                String token = response.getString("token");
                                String cell = response.getString("cell");
                                Toast.makeText(getApplicationContext(), "成功注册", Toast.LENGTH_SHORT).show();
                                /*SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(AppConstant.PrefKey.user_cell, cell);
                                editor.putString(AppConstant.PrefKey.user_token, token);
                                editor.commit();*/
                                Log.d(AppConstant.LOG.WPDJson+"token",token);

                                //直接跳转到login
                                Intent intent = new Intent();
                                intent.setClass(context, LoginActivity.class);
                                intent.putExtra("cell",cell);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"code:"+errCode, Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(),"message:"+response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(AppConstant.LOG.WPDVolley, String.valueOf(response));
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
