package com.example.apple.b10256013;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText edit_username,edit_password;

    private static AlertDialog mDialog;

    private final static int LOGIN_SUCCESS = 1;
    private final static int LOGIN_FAIL = 2;
    private final static int REGISTER_SUCCESS = 3;
    private final static int REGISTER_FAIL = 4;
    private final static int ERROR = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //註冊←讓手機知道這些物件可以做什麼事
        edit_username = (EditText)findViewById(R.id.username);
        edit_password = (EditText)findViewById(R.id.password);

        //宣告提示視窗
        mDialog = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setTitle("系統訊息")
                .setPositiveButton("確認", null).create();
    }

    public String getResponseContent(String path, Map<String, String> params) {

        OkHttpClient okHttpClient = new OkHttpClient();

        //設定連線逾時的時間為3秒
        okHttpClient.setConnectTimeout(3000, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(3000, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(3000, TimeUnit.MILLISECONDS);

        String username = params.get("username_key");
        String password = params.get("password_key");

        //利用post送出數據給伺服器
        RequestBody requestBody = new FormEncodingBuilder().
                add("username", username).
                add("password", password).
                build();

        //發送要求
        Request request = new Request.Builder().url(path).post(requestBody).build();

        String result = "";
        try
        {
            //等待網頁回應
            Response response = okHttpClient.newCall(request).execute();

            //回應成功
            if (response.isSuccessful())
            {
                //回傳網頁資料
                return response.body().string();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }


    public void loginClick(View view)
    {
        //Android使用網路時都需要用到執行緒
        new Thread()
        {
            public void run()
            {
                //呼叫新增使用者的php網頁
                String path = "http://nokia124t.ddns.net:8080/select_user.php";
                //第一個參數Key值會對應到第二個參數的值 這邊都設為String
                Map<String,String> map = new HashMap<>();
                map.put("username_key",edit_username.getText().toString());
                map.put("password_key",edit_password.getText().toString());

                String loginResult = getResponseContent(path, map);

                try
                {
                    JSONArray jsonArray = new JSONArray(loginResult);

                    //這一定只有一筆 所以拿索引值0的資料
                    if(jsonArray.getJSONObject(0).getString("login").equals("登入成功"))
                    {
                        handler.sendEmptyMessage(LOGIN_SUCCESS);
                    }
                    else
                    {
                        handler.sendEmptyMessage(LOGIN_FAIL);
                    }
                }
                catch (final Exception error)
                {
                    handler.sendEmptyMessage(ERROR);
                }
            }
        }.start();

    }

    public void registerClick(View view)
    {
        //Android使用網路時都需要用到執行緒
        new Thread()
        {
            public void run()
            {
                //呼叫新增使用者的php網頁
                String path = "http://nokia124t.ddns.net:8080/insert_user.php";
                //第一個參數Key值會對應到第二個參數的值 這邊都設為String
                Map<String,String> map = new HashMap<>();
                map.put("username_key",edit_username.getText().toString());
                map.put("password_key",edit_password.getText().toString());

                String loginResult = getResponseContent(path, map);

                try
                {
                    JSONArray jsonArray = new JSONArray(loginResult);

                    if(jsonArray.getJSONObject(0).getString("register").equals("註冊成功"))
                    {
                        handler.sendEmptyMessage(REGISTER_SUCCESS);
                    }
                    else
                    {
                        handler.sendEmptyMessage(REGISTER_FAIL);
                    }
                }
                catch (final Exception error)
                {
                    handler.sendEmptyMessage(ERROR);
                }
            }
        }.start();
    }

    private static Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message message)
        {
            super.handleMessage(message);
            String result = "";

            switch (message.what)
            {
                case LOGIN_SUCCESS:
                    result = "登入成功";
                    break;
                case LOGIN_FAIL:
                    result = "登入失敗";
                    break;
                case REGISTER_SUCCESS:
                    result = "註冊成功";
                    break;
                case REGISTER_FAIL:
                    result = "註冊失敗";
                    break;
                case ERROR:
                    result="發生錯誤";
                    break;
            }

            mDialog.setMessage(result);
            mDialog.show();
        }
    };
}
