package com.lexinsmart.xushun.cradle;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mqtt.MqttV3Service;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    WebView webView;
    private Spinner modeSp, speedSp, timeSp, yinpinSp, shiduSp;
    private ToggleButton musicTb, trunTb;
    private Button sureBtn;
    private LinearLayout speedLL, timeLL, tbLL, shiduLL, wenduLL;
    Context context;
    Map<String, String> data = new HashMap<String, String>();
    String ADDRESS = "180.76.179.148";
    String PORT = "1883";
    int Qos = 1;
    ArrayList<String> topicList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        findViewsById();
        webView = (WebView) findViewById(R.id.webView);
        webView.setInitialScale(200);

        webView.loadUrl("http://video.tunnel.lexinsmart.com/pure.html");
//        webView.loadUrl(" http://video.tunnel.lexinsmart.com/?action=stream");


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        data.put("mod", "000");
        data.put("spe", "001");
        data.put("tim", "002");
        data.put("tem", "000");
        data.put("hum", "000");
        data.put("mus", "000");
        data.put("run", "000");

        topicList.add("babycradle");
        new Thread(new MqttProcThread()).start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void findViewsById() {
        modeSp = (Spinner) findViewById(R.id.modeSpid);
        speedSp = (Spinner) findViewById(R.id.speedSpid);
        timeSp = (Spinner) findViewById(R.id.timeSpid);
        yinpinSp = (Spinner) findViewById(R.id.yinpinSpid);
        shiduSp = (Spinner) findViewById(R.id.shiduSpid);
        musicTb = (ToggleButton) findViewById(R.id.musicTbid);
        trunTb = (ToggleButton) findViewById(R.id.turnTbid);
        sureBtn = (Button) findViewById(R.id.sureBtnid);
        sureBtn.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        modeSp.setOnItemSelectedListener(this);
        speedSp.setOnItemSelectedListener(this);
        timeSp.setOnItemSelectedListener(this);
        yinpinSp.setOnItemSelectedListener(this);
        shiduSp.setOnItemSelectedListener(this);
        speedLL = (LinearLayout) findViewById(R.id.speedLLid);
        timeLL = (LinearLayout) findViewById(R.id.timeLLid);

        wenduLL = (LinearLayout) findViewById(R.id.tempetureLLid);
        shiduLL = (LinearLayout) findViewById(R.id.shiduLLid);
        tbLL = (LinearLayout) findViewById(R.id.tbLLid);
        speedLL.setVisibility(View.GONE);
        timeLL.setVisibility(View.GONE);
        musicTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    data.put("mus", "001");
                } else {
                    data.put("mus", "000");
                }
            }
        });
        trunTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    data.put("run", "001");
                } else {
                    data.put("run", "000");
                }
            }
        });
        Button refreshid = (Button) findViewById(R.id.refreshid);
        refreshid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("http://video.tunnel.lexinsmart.com/pure.html");
//        webView.loadUrl(" http://video.tunnel.lexinsmart.com/?action=stream");


                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // TODO Auto-generated method stub
                        //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                        view.loadUrl(url);
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sureBtnid:
                System.out.println("data---->" + data.toString());
                String jsonString = JSONObject.toJSONString(data);
                MqttV3Service.publishMsg(jsonString, Qos, 0);
                System.out.println("jsonObject---->" + jsonString);
                break;
            default:
                break;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        switch (parent.getId()) {
            case R.id.modeSpid:
                switch (position) {
                    case 0:
                        speedLL.setVisibility(View.GONE);
                        timeLL.setVisibility(View.GONE);
                        tbLL.setVisibility(View.GONE);


                        shiduLL.setVisibility(View.VISIBLE);
                        wenduLL.setVisibility(View.VISIBLE);
                        musicTb.setVisibility(View.INVISIBLE);
                        trunTb.setVisibility(View.INVISIBLE);

                        yinpinSp.setSelection(0);
                        shiduSp.setSelection(0);
                        data.put("mod", "000");
                        data.put("spe", "000");
                        data.put("tim", "000");
                        data.put("run", "000");
                        data.put("mus", "000");
                        break;
                    case 1:
                        speedSp.setSelection(0);
                        timeSp.setSelection(0);
                        musicTb.setChecked(false);
                        trunTb.setChecked(false);

                        speedLL.setVisibility(View.VISIBLE);
                        timeLL.setVisibility(View.VISIBLE);
                        tbLL.setVisibility(View.VISIBLE);
                        shiduLL.setVisibility(View.GONE);
                        wenduLL.setVisibility(View.GONE);
                        musicTb.setVisibility(View.VISIBLE);
                        trunTb.setVisibility(View.VISIBLE);

                        data.put("mod", "001");
                        data.put("tem", "000");
                        data.put("hum", "000");
                        break;
                    default:
                        break;
                }
                break;
            case R.id.speedSpid:
                switch (position) {
                    case 0:
                        data.put("spe", "001");
                        break;
                    case 1:
                        data.put("spe", "002");
                        break;
                    case 2:
                        data.put("spe", "003");
                        break;
                    default:
                        break;
                }
                break;
            case R.id.timeSpid:
                switch (position) {
                    case 0:
                        data.put("tim", "002");
                        break;
                    case 1:
                        data.put("tim", "004");
                        break;
                    case 2:
                        data.put("tim", "006");
                        break;
                    case 3:
                        data.put("tim", "008");
                        break;
                    default:
                        break;
                }
                break;
            case R.id.yinpinSpid:
                switch (position) {
                    case 0:
                        data.put("tem", "000");
                        break;
                    case 1:
                        data.put("tem", "001");
                        break;
                    case 2:
                        data.put("tem", "002");
                        break;
                    default:
                        break;
                }
                break;
            case R.id.shiduSpid:
                switch (position) {
                    case 0:
                        data.put("hum", "000");
                        break;
                    case 1:
                        data.put("hum", "001");
                        break;
                    case 2:
                        data.put("hum", "002");
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class MqttProcThread implements Runnable {

        int randomid = (int) Math.floor(10000 + Math.random() * 90000);

        @Override
        public void run() {
            Message msg = new Message();
            boolean ret = MqttV3Service.connectionMqttServer(myHandler, ADDRESS, PORT, "lexin" + randomid, topicList);
            if (ret) {
                msg.what = 1;
            } else {
                msg.what = 0;
            }
            msg.obj = "strresult";
            myHandler.sendMessage(msg);
        }
    }

    @SuppressWarnings("HandlerLeak")
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show();

            } else if (msg.what == 0) {
                Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                String strContent = "";
                strContent += msg.getData().getString("content");
                System.out.println("strcontent:" + strContent);
            } else if (msg.what == 3) {
                if (MqttV3Service.closeMqtt()) {
                    Toast.makeText(context, "断开连接", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
