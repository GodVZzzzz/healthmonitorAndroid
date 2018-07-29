package com.example.as.healthmonitor;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.Data;
import com.example.as.healthmonitor.JsonBean.JsonRootBean;
import com.example.as.healthmonitor.circlebar.view.MyCircleBar;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.CONNECTIVITY_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    private MyCircleBar myCircleBar;

    private TextView mTextView;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        myCircleBar = (MyCircleBar) view.findViewById(R.id.my_circle_bar);
        mTextView = (TextView) view.findViewById(R.id.event_tips);

        final String account = getActivity().getIntent().getStringExtra("user_account");
        final int id = getActivity().getIntent().getIntExtra("user_id",0);

        if(account == null){
            myCircleBar.showProgress(0,0);
        }

        else if(account != null) {
            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);

            NetworkInfo network = cm.getActiveNetworkInfo();
            int type = ConnectivityManager.TYPE_DUMMY;
            if (network == null) {
                Toast.makeText(getActivity(), "当前网络未连接，无法加载", Toast.LENGTH_LONG).show();
                myCircleBar.showProgress(0, 0);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient();

                            RequestBody requestBody = new FormBody.Builder()
                                    .add("id", String.valueOf(id))
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://39.108.137.129:8080/healthmonitor/user/get_user_info.do")
                                    .post(requestBody)
                                    .build();

                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            Gson gson = new Gson();
                            final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);
                            final Data data = jsonRootBean.getData();


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Integer weightPer = 60;
                                    Integer bmiPer = 20;   //18-24-28-32
                                    Integer stepPer = 8000;
                                    Integer sleepPer = 8;
                                    Integer heartPer = 80; //45-60-100-120
                                    Integer bloodPer = 110;  //90-140


                                    String weight = "50";
                                    String bmi = "50" ;
                                    String step = "50";
                                    String sleep = "50";
                                    String heart ="50";
                                    String blood ="50";



                                    if (data.getWeight() != null) {
                                        Double weightCur = Double.parseDouble(data.getWeight());
                                        if (Math.abs(weightCur - weightPer) < 20 && Math.abs(weightCur - weightPer) >= 0) {
                                            ;weight = "100";
                                        } else if (Math.abs(weightCur - weightPer) >= 20 && Math.abs(weightCur - weightPer) < 40) {
                                            weight = "80";
                                        } else if(Math.abs(weightCur - weightPer) >= 40 && Math.abs(weightCur - weightPer) < 60){
                                            weight = "60";
                                        } else if(Math.abs(weightCur - weightPer) >= 60 && Math.abs(weightCur - weightPer) < 80) {
                                            weight = "40";
                                        } else if(Math.abs(weightCur - weightPer) >= 80 && Math.abs(weightCur - weightPer) < 100){
                                            weight =  "20";
                                        }else if(Math.abs(weightCur - weightPer) >= 100 ){
                                            weight = "0";
                                        }
                                        if (data.getHeight() != null) {
                                            Double bmiCur = Double.parseDouble(data.getWeight())/Double.parseDouble(data.getWeight());
                                            if (Math.abs(bmiCur - bmiPer) < 5 && Math.abs(bmiCur - bmiPer) >= 0) {
                                                ;bmi = "100";
                                            } else if (Math.abs(bmiCur - bmiPer) >= 5 && Math.abs(bmiCur - bmiPer) < 10) {
                                                bmi = "80";
                                            } else if(Math.abs(bmiCur - bmiPer) >= 10 && Math.abs(bmiCur - bmiPer) < 15){
                                                bmi = "60";
                                            } else if(Math.abs(bmiCur - bmiPer) >= 15 && Math.abs(bmiCur - bmiPer) < 20) {
                                                bmi = "40";
                                            } else if(Math.abs(bmiCur - bmiPer) >= 20 && Math.abs(weightCur - weightPer) < 30){
                                                bmi =  "20";
                                            }else if(Math.abs(weightCur - weightPer) >= 30 ){
                                                bmi = "0";
                                            }
                                        }
                                    }
                                    if (data.getStep() != null) {
                                        Double stepCur = Double.parseDouble(data.getStep());
                                        if (stepCur >= stepPer ) {
                                            ;step = "100";
                                        } else if (stepCur >= 6000) {
                                            step = "80";
                                        } else if(stepCur >= 4000){
                                            step = "60";
                                        } else if(stepCur >= 2000) {
                                            step = "40";
                                        } else if(stepCur >= 500){
                                            step =  "20";
                                        }else{
                                            step = "0";
                                        }
                                    }
                                    if (data.getSleep() != null) {
                                        Double sleepCur = Double.parseDouble(data.getSleep());
                                        if (Math.abs(sleepCur - sleepPer) < 2 && Math.abs(sleepCur - sleepPer) >= 0) {
                                            ;sleep = "100";
                                        } else if (Math.abs(sleepCur - sleepPer) >= 2 && Math.abs(sleepCur - sleepPer) < 4) {
                                            sleep = "80";
                                        } else if(Math.abs(sleepCur - sleepPer) >= 4 && Math.abs(sleepCur - sleepPer) < 5){
                                            sleep = "60";
                                        } else if(Math.abs(sleepCur - sleepPer) >= 5 && Math.abs(sleepCur - sleepPer) < 6) {
                                            sleep = "40";
                                        } else if(Math.abs(sleepCur - sleepPer) >= 6 && Math.abs(sleepCur - sleepPer) < 8){
                                            sleep =  "20";
                                        }else if(Math.abs(sleepCur - sleepPer) >= 8 ){
                                            sleep = "0";
                                        }
                                    }
                                    if (data.getHeartrate() != null) {
                                        Double heartCur = Double.parseDouble(data.getHeartrate());
                                        if (Math.abs(heartCur - heartPer) < 20 && Math.abs(heartCur - heartPer) >= 0) {
                                            ;heart = "100";
                                        } else if (Math.abs(heartCur - heartPer) >= 20 && Math.abs(heartCur - heartPer) < 40) {
                                            heart = "80";
                                        } else if(Math.abs(heartCur - heartPer) >= 40 && Math.abs(heartCur - heartPer) < 60){
                                            heart = "60";
                                        } else if(Math.abs(heartCur - heartPer) >= 60 && Math.abs(heartCur - heartPer) < 80) {
                                            heart = "40";
                                        } else if(Math.abs(heartCur - heartPer) >= 80 && Math.abs(heartCur - heartPer) < 100){
                                            heart =  "20";
                                        }else if(Math.abs(heartCur - heartPer) >= 100 ){
                                            heart = "0";
                                        }
                                    }
                                    if (data.getBloodpressure() != null) {
                                        Double bloodCur = Double.parseDouble(data.getBloodpressure());
                                        if (Math.abs(bloodCur - bloodPer) < 20 && Math.abs(bloodCur - bloodPer) >= 0) {
                                            blood = "100";
                                        } else if (Math.abs(bloodCur - bloodPer) >= 20 && Math.abs(bloodCur - bloodPer) < 40) {
                                            blood = "80";
                                        } else if(Math.abs(bloodCur - bloodPer) >= 40 && Math.abs(bloodCur - bloodPer) < 60){
                                            blood = "60";
                                        } else if(Math.abs(bloodCur - bloodPer) >= 60 && Math.abs(bloodCur - bloodPer) < 80) {
                                            blood = "40";
                                        } else if(Math.abs(bloodCur - bloodPer) >= 80 && Math.abs(bloodCur - bloodPer) < 100){
                                            blood =  "20";
                                        }else if(Math.abs(bloodCur - bloodPer) >= 100 ){
                                            blood = "0";
                                        }
                                    }



                                    int count = Integer.parseInt(weight) + Integer.parseInt(bmi) + Integer.parseInt(step) +
                                            Integer.parseInt(sleep) + Integer.parseInt(heart) + Integer.parseInt(blood);

                                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.textanim);
                                    if(count > 0 && count <= 200){
                                        mTextView.setText("您今日身体状况不佳，请及时检查！！！");
                                        mTextView.setTextColor(getResources().getColor(R.color.red));
                                        mTextView.setTextSize(25);
                                        mTextView.startAnimation(animation);
                                    }else if(count > 200 && count <= 400){
                                        mTextView.setText("您今日身体状况一般，请多注意身体。");
                                        mTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        mTextView.setTextSize(25);
                                        mTextView.startAnimation(animation);
                                    }else if(count > 400 && count <= 600){
                                        mTextView.setText("您的身体很健康，请继续保持。");
                                        mTextView.setTextColor(getResources().getColor(R.color.green));
                                        mTextView.setTextSize(25);
                                        mTextView.startAnimation(animation);
                                    }

                                    myCircleBar.showProgress(count,1000);

                                }
                            });


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        }

        return view;
    }


}
