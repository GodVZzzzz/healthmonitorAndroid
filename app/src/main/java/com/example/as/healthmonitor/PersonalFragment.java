package com.example.as.healthmonitor;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.as.healthmonitor.JsonBean.Data;
import com.example.as.healthmonitor.JsonBean.JsonRootBean;
import com.example.as.healthmonitor.JsonBean.LinkmanBean;
import com.example.as.healthmonitor.JsonBean.ListBean;
import com.example.as.healthmonitor.Mail.MailUtils;
import com.example.as.healthmonitor.step.StepDetector;
import com.example.as.healthmonitor.step.StepService;
import com.google.gson.Gson;
import com.show.api.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.Runnable;
import java.lang.Thread;
import java.lang.String;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PersonalFragment extends Fragment implements ViewPager.OnPageChangeListener{


    private SwipeRefreshLayout swipeRefreshLayout;

    private List<health> healthList = new ArrayList<>();

    private healthAdapter adapter;

    private static int sleepTime = 0;

    private String message = "尊敬的客户：\n"+"您好！检测到您的健康指数出现较大问题，请及时检查";

    private String telNo = "";
    private String user_name = "";
    private String user_email = "";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private static final int MY_PERMISSIONS_REQUEST_SEND_CON_SMS = 1;

    private ListBean listBean = null;
    private List<LinkmanBean> linkmanBean = null;

    private List<String> telCon = new ArrayList<>();
    private List<String> linkManMail = new ArrayList<>();

    //viewPager
    private ViewPager viewPager;
    private int[] imageResIds;
    private ArrayList<ImageView> imageViewList;
    private LinearLayout ll_point_container;
    private String[] contentDescs;
    private TextView tv_desc;
    private int previousSelectedPosition = 0;
    boolean isRunning = false;



    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_personal, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHealth();
            }
        });

        initHealth();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new healthAdapter(healthList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));

        final String account = getActivity().getIntent().getStringExtra("user_account");
        final int id = getActivity().getIntent().getIntExtra("user_id",0);

        if(account != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody requestBody = new FormBody.Builder()
                                .add("id",String.valueOf(id))
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

                        telNo = data.getTel();
                        user_email = data.getEmail();
                        user_name = data.getName();

                        final String messageCon = "您好：\n" + "您的联系人" + user_name + "健康出现较大问题，请及时注意。";

                        int heart_count = 0;
                        int blood_count = 0;

                        try{
                            heart_count = Integer.parseInt(data.getHeartrate());
                            blood_count = Integer.parseInt(data.getBloodpressure());
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }

                        if ((heart_count >= 120 || heart_count <= 50
                                || blood_count <= 50 ||
                                blood_count >= 150) && (heart_count > 0
                                && blood_count > 0)) {

                            if (user_email != null) {
                                try {
                                    sendMail(user_email);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (telNo != null) {
                                        SmsManager smsManager = SmsManager.getDefault();
                                        smsManager.sendTextMessage(telNo, null, message, null, null);

                                    }
                                    SmsManager smsManager1 = SmsManager.getDefault();
                                    for (int i = 0; i < telCon.size(); i++) {
                                        if (telCon.get(i) != null)
                                            smsManager1.sendTextMessage(telCon.get(i), null, messageCon, null, null);
                                    }

                                }
                            });

                        }



                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (jsonRootBean.getStatus() == 1){
                                    Toast.makeText(getActivity(),jsonRootBean.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                                else if(jsonRootBean.getStatus() == 0){


                                    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
                                    Date now = new Date(System.currentTimeMillis());
                                    String date = sdf.format(now);



                                    healthList.clear();
                                    if(data.getWeight() != null) {
                                        health weight = new health("体重", R.drawable.weight, "kg", date, data.getWeight(),"weight");

                                        healthList.add(weight);
                                        if(data.getHeight() != null) {
                                            double d = new BigDecimal(Double.parseDouble(data.getWeight()) / Double.parseDouble(data.getHeight())).setScale(
                                                    2,BigDecimal.ROUND_HALF_UP).doubleValue();
                                            String dd = String.valueOf(d);
                                            health bmi = new health("身高体重比", R.drawable.bmi, "m/kg", date, dd,"bmi");
                                            healthList.add(bmi);
                                        }
                                        else {
                                            health bmi = new health("身高体重比",R.drawable.bmi,"m/kg","0:00","0","bmi");
                                            healthList.add(bmi);
                                        }
                                    }else {
                                        health weight = new health("体重",R.drawable.weight,"kg","0:00","0","weight");
                                        healthList.add(weight);
                                    }
                                    if(data.getStep() != null) {
                                        health steps = new health("步数", R.drawable.steps, "步", date, data.getStep(),"step");
                                        healthList.add(steps);
                                    }else {
                                        health steps = new health("步数",R.drawable.steps,"步","0:00","0","step");
                                        healthList.add(steps);
                                    }
                                    if(data.getSleep() != null) {
                                        health sleep = new health("睡眠时间", R.drawable.sleep, "小时", date, data.getSleep(),"sleep");
                                        healthList.add(sleep);
                                    }else {
                                        health sleep = new health("睡眠时间",R.drawable.sleep,"小时","0:00","0","sleep");
                                        healthList.add(sleep);
                                    }
                                    if(data.getHeartrate() != null) {
                                        health heartRate = new health("心率", R.drawable.heartrate, "bpm", date, data.getHeartrate(),"heartrate");
                                        healthList.add(heartRate);
                                    }else {
                                        health heartRate = new health("心率",R.drawable.heartrate,"bpm","0:00","0","heartrate");
                                        healthList.add(heartRate);
                                    }
                                    if(data.getBloodpressure() !=null) {
                                        health bloodPressure = new health("血压", R.drawable.bloodpressure, "mmHg", date, data.getBloodpressure(),"bloodpressure");
                                        healthList.add(bloodPressure);
                                    }else {
                                        health bloodPressure = new health("血压", R.drawable.bloodpressure,"mmHg","0:00","0","bloodpressure");
                                        healthList.add(bloodPressure);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        adapter.setOnItemClickListener(new healthAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, final int position) {
                if(position == 1){
                    Toast.makeText(getActivity(),"该项无需手动上传",Toast.LENGTH_SHORT).show();
                }

                else if(position == 0){
                    SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日 HH:mm");
                    Date now1 = new Date(System.currentTimeMillis());
                    final String date1 = sdf1.format(now1);

                    LayoutInflater factory = getLayoutInflater(Bundle.EMPTY);
                    final View textEntryView = factory.inflate(R.layout.update_dialog, null);
                    final EditText editText = (EditText) textEntryView.findViewById(R.id.input_new_data);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle(healthList.get(position).getName());
                    alertBuilder.setIcon(healthList.get(position).getImageId());
                    alertBuilder.setView(textEntryView);
                    alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            final int id = getActivity().getIntent().getIntExtra("user_id", 0);
                            if (id == 0)
                                Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();

                            else {
                                if (editText.getText().toString().isEmpty())
                                    Toast.makeText(getActivity(), "没有要更新的数据", Toast.LENGTH_SHORT).show();
                                else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                OkHttpClient client = new OkHttpClient();

                                                RequestBody requestBody = new okhttp3.FormBody.Builder()
                                                        .add("id", String.valueOf(id))
                                                        .add(healthList.get(position).getClassName(), editText.getText().toString())
                                                        .build();

                                                Request request = new Request.Builder()
                                                        .url("http://39.108.137.129:8080/healthmonitor/user/update_information.do")
                                                        .post(requestBody)
                                                        .build();

                                                Response response = client.newCall(request).execute();
                                                String responseData = response.body().string();
                                                Gson gson = new Gson();
                                                final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);

                                                getActivity().runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        Double bmi = new BigDecimal(Double.parseDouble(editText.getText().toString())/
                                                                ((Double.parseDouble(healthList.get(position).getHealthCount())/Double
                                                                        .parseDouble(healthList.get(position+1).getHealthCount())))).setScale(
                                                                                2,BigDecimal.ROUND_HALF_UP).doubleValue();


                                                        Toast.makeText(getActivity(), jsonRootBean.getMsg(), Toast.LENGTH_SHORT).show();
                                                        healthList.get(position).setHealthCount(editText.getText().toString());
                                                        healthList.get(position).setTime(date1);



                                                        healthList.get(position+1).setHealthCount(String.valueOf(bmi));
                                                        healthList.get(position+1).setTime(date1);

                                                        adapter.notifyItemRangeChanged(0,2);
                                                    }
                                                });
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertBuilder.create();
                    alertBuilder.show();

                }

                else{
                    SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日 HH:mm");
                    Date now1 = new Date(System.currentTimeMillis());
                    final String date1 = sdf1.format(now1);

                    LayoutInflater factory = getLayoutInflater(Bundle.EMPTY);
                    final View textEntryView = factory.inflate(R.layout.update_dialog, null);
                    final EditText editText = (EditText) textEntryView.findViewById(R.id.input_new_data);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle(healthList.get(position).getName());
                    alertBuilder.setIcon(healthList.get(position).getImageId());
                    alertBuilder.setView(textEntryView);
                    alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            final int id = getActivity().getIntent().getIntExtra("user_id", 0);
                            if (id == 0)
                                Toast.makeText(getActivity(), "未登录", Toast.LENGTH_SHORT).show();

                            else {
                                if (editText.getText().toString().isEmpty())
                                    Toast.makeText(getActivity(), "没有要更新的数据", Toast.LENGTH_SHORT).show();
                                else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                OkHttpClient client = new OkHttpClient();

                                                RequestBody requestBody = new okhttp3.FormBody.Builder()
                                                        .add("id", String.valueOf(id))
                                                        .add(healthList.get(position).getClassName(), editText.getText().toString())
                                                        .build();

                                                Request request = new Request.Builder()
                                                        .url("http://39.108.137.129:8080/healthmonitor/user/update_information.do")
                                                        .post(requestBody)
                                                        .build();

                                                Response response = client.newCall(request).execute();
                                                String responseData = response.body().string();
                                                Gson gson = new Gson();
                                                final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);

                                                final String messageCon = "您好：\n" + "您的联系人" + user_name + "健康出现较大问题，请及时注意。";

                                                int heart_count = 0;
                                                int blood_count = 0;

                                                if(position == 4 ) {
                                                    try {
                                                        heart_count = Integer.parseInt(editText.getText().toString());
                                                    } catch (NumberFormatException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if ((heart_count >= 120 || heart_count <= 50) && (heart_count > 0)) {

                                                        if (user_email != null) {
                                                            try {
                                                                sendMail(user_email);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (telNo != null) {
                                                                    SmsManager smsManager = SmsManager.getDefault();
                                                                    smsManager.sendTextMessage(telNo, null, message, null, null);

                                                                }
                                                                SmsManager smsManager1 = SmsManager.getDefault();
                                                                for (int i = 0; i < telCon.size(); i++) {
                                                                    if (telCon.get(i) != null)
                                                                        smsManager1.sendTextMessage(telCon.get(i), null, messageCon, null, null);
                                                                }

                                                            }
                                                        });

                                                    }
                                                }
                                                if(position == 5){
                                                    try {
                                                        blood_count = Integer.parseInt(editText.getText().toString());
                                                    } catch (NumberFormatException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (( blood_count <= 50 ||
                                                            blood_count >= 150) && (blood_count > 0)) {

                                                        if (user_email != null) {
                                                            try {
                                                                sendMail(user_email);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (telNo != null) {
                                                                    SmsManager smsManager = SmsManager.getDefault();
                                                                    smsManager.sendTextMessage(telNo, null, message, null, null);

                                                                }
                                                                SmsManager smsManager1 = SmsManager.getDefault();
                                                                for (int i = 0; i < telCon.size(); i++) {
                                                                    if (telCon.get(i) != null)
                                                                        smsManager1.sendTextMessage(telCon.get(i), null, messageCon, null, null);
                                                                }

                                                            }
                                                        });

                                                    }
                                                }



                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getActivity(), jsonRootBean.getMsg(), Toast.LENGTH_SHORT).show();
                                                        healthList.get(position).setHealthCount(editText.getText().toString());
                                                        healthList.get(position).setTime(date1);

                                                        adapter.notifyItemChanged(position);
                                                    }
                                                });
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertBuilder.create();
                    alertBuilder.show();
                }
            }

        });

        if(account != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //联系人信息
                    try {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody requestBody = new FormBody.Builder()
                                .add("user_account", getActivity().getIntent().getStringExtra("user_account"))
                                .build();

                        Request request = new Request.Builder()
                                .url("http://39.108.137.129:8080/healthmonitor/linkman/search_man.do")
                                .post(requestBody)
                                .build();

                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Gson gson = new Gson();
                        listBean = gson.fromJson(responseData, ListBean.class);

                        if (listBean.getStatus() == 0) {
                            linkmanBean = listBean.getData();

                            int l = linkmanBean.size();

                            for (int i = 0; i < linkmanBean.size(); i++) {
                                if(linkmanBean.get(i).getTel() != null)
                                    telCon.add(i, linkmanBean.get(i).getTel());
                                if(linkmanBean.get(i).getEmail() != null)
                                    linkManMail.add(linkmanBean.get(i).getEmail());
                            }

                        }else if(listBean.getStatus() == 1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        //计步、睡眠
        Intent intent = new Intent(getActivity(), StepService.class);
        getActivity().startService(intent);
        if(account != null && healthList.get(2).getHealthCount() != null) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        int StepPre = Integer.parseInt(healthList.get(2).getHealthCount());
                        int StepNow = StepDetector.CURRENT_SETP + StepPre;

                        int ifStep = StepNow - StepPre;
                        if (ifStep == 0) {
                            sleepTime++;
                        }

                        if (ifStep > 0) {
                            sleepTime = 0;
                        }

                        int sleepHour = sleepTime / 60;

                        if (sleepHour >= 3) {
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("id", String.valueOf(id))
                                    .add("step", String.valueOf(StepNow))
                                    .add("sleep", String.valueOf(sleepHour))
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://39.108.137.129:8080/healthmonitor/user/update_information.do")
                                    .post(requestBody)
                                    .build();

                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            Gson gson = new Gson();
                            final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (jsonRootBean.getStatus() == 0) {
                                        healthList.get(2).setHealthCount(jsonRootBean.getData().getStep());
                                        healthList.get(3).setHealthCount(jsonRootBean.getData().getSleep());
                                        adapter.notifyItemRangeChanged(2, 2);
                                    } else ;
                                }
                            });
                        } else {
                            RequestBody requestBody = new FormBody.Builder()
                                    .add("id", String.valueOf(id))
                                    .add("step", String.valueOf(StepNow))
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://39.108.137.129:8080/healthmonitor/user/update_information.do")
                                    .post(requestBody)
                                    .build();

                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            Gson gson = new Gson();
                            final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                if (jsonRootBean.getStatus() == 0) {
                                    healthList.get(2).setHealthCount(jsonRootBean.getData().getStep());
                                    adapter.notifyItemChanged(2);
                                } else ;
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        ClearZero();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }


            };
            Timer timer = new Timer();
            long delay = 5000;
            long intevalPeriod = 60000;
            timer.schedule(task, delay, intevalPeriod);
        }


        //viewPager
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setOnPageChangeListener(this);// 设置页面更新监听
        //viewPager.setOffscreenPageLimit(1);// 左右各保留几个对象
        ll_point_container = (LinearLayout) view.findViewById(R.id.ll_point_container);

        tv_desc = (TextView) view.findViewById(R.id.tv_desc);

        initData();
        initAdapter();
        //viewPager轮询
        new Thread() {
            public void run() {
                isRunning = true;
                while (isRunning) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 往下跳一位
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            System.out.println("设置当前位置: " + viewPager.getCurrentItem());
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    });
                }
            }

            ;
        }.start();


        return view;


    }



    private void initHealth() {
        health weight = new health("体重",R.drawable.weight,"kg","0:00","0","weight");
        healthList.add(weight);
        health bmi = new health("身高体重比",R.drawable.bmi,"m/kg","0:00","0","");
        healthList.add(bmi);
        health steps = new health("步数",R.drawable.steps,"步","0:00","0","step");
        healthList.add(steps);
        health sleep = new health("睡眠时间",R.drawable.sleep,"小时","0:00","0","sleep");
        healthList.add(sleep);
        health heartRate = new health("心率",R.drawable.heartrate,"bpm","0:00","0","heartrate");
        healthList.add(heartRate);
        health bloodPressure = new health("血压", R.drawable.bloodpressure,"mmHg","0:00","0","bloodpressure");
        healthList.add(bloodPressure);
    }

    private void refreshHealth() {
        final String account1 = getActivity().getIntent().getStringExtra("user_account");
        final int id1 = getActivity().getIntent().getIntExtra("user_id",0);
        if(account1 == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            healthList.clear();
                            initHealth();
                            Toast.makeText(getActivity(),"未登录",Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }).start();
        }

        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();

                        RequestBody requestBody = new FormBody.Builder()
                                .add("id", String.valueOf(id1))
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
                                if (jsonRootBean.getStatus() == 1) {
                                    Toast.makeText(getActivity(), jsonRootBean.getMsg(), Toast.LENGTH_SHORT).show();
                                } else if (jsonRootBean.getStatus() == 0) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
                                    Date now = new Date(System.currentTimeMillis());
                                    String date = sdf.format(now);

                                    healthList.clear();

                                    if (data.getWeight() != null) {
                                        health weight = new health("体重", R.drawable.weight, "kg", date, data.getWeight(), "weight");
                                        healthList.add(weight);
                                        if (data.getHeight() != null) {
                                            double d = new BigDecimal(Double.parseDouble(data.getWeight()) / Double.parseDouble(data.getHeight())).setScale(
                                                    2,BigDecimal.ROUND_HALF_UP).doubleValue();
                                            String dd = String.valueOf(d);
                                            health bmi = new health("身高体重比", R.drawable.bmi, "m/kg", date, dd, "bmi");
                                            healthList.add(bmi);
                                        } else {
                                            health bmi = new health("身高体重比", R.drawable.bmi, "m/kg", "0:00", "0", "bmi");
                                            healthList.add(bmi);
                                        }
                                    } else {
                                        health weight = new health("体重", R.drawable.weight, "kg", "0:00", "0", "weight");
                                        healthList.add(weight);
                                    }
                                    if (data.getStep() != null) {
                                        health steps = new health("步数", R.drawable.steps, "步", date, data.getStep(), "step");
                                        healthList.add(steps);
                                    } else {
                                        health steps = new health("步数", R.drawable.steps, "步", "0:00", "0", "step");
                                        healthList.add(steps);
                                    }
                                    if (data.getSleep() != null) {
                                        health sleep = new health("睡眠时间", R.drawable.sleep, "小时", date, data.getSleep(), "sleep");
                                        healthList.add(sleep);
                                    } else {
                                        health sleep = new health("睡眠时间", R.drawable.sleep, "小时", "0:00", "0", "sleep");
                                        healthList.add(sleep);
                                    }
                                    if (data.getHeartrate() != null) {
                                        health heartRate = new health("心率", R.drawable.heartrate, "bpm", date, data.getHeartrate(), "heartrate");
                                        healthList.add(heartRate);
                                    } else {
                                        health heartRate = new health("心率", R.drawable.heartrate, "bpm", "0:00", "0", "heartrate");
                                        healthList.add(heartRate);
                                    }
                                    if (data.getBloodpressure() != null) {
                                        health bloodPressure = new health("血压", R.drawable.bloodpressure, "mmHg", date, data.getBloodpressure(), "bloodpressure");
                                        healthList.add(bloodPressure);
                                    } else {
                                        health bloodPressure = new health("血压", R.drawable.bloodpressure, "mmHg", "0:00", "0", "bloodpressure");
                                        healthList.add(bloodPressure);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    private void ClearZero() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date now = new Date(System.currentTimeMillis());
        String date = sdf.format(now);

        if(date == "00:00"){
            StepDetector.CURRENT_SETP = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        RequestBody requestBody = new FormBody.Builder()
                                .add("id", String.valueOf(getActivity().getIntent().getIntExtra("user_id", 0)))
                                .add("step", String.valueOf(0))
                                .build();

                        Request request = new Request.Builder()
                                .url("http://39.108.137.129:8080/healthmonitor/user/update_information.do")
                                .post(requestBody)
                                .build();

                        OkHttpClient client = new OkHttpClient();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Gson gson = new Gson();
                        final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (jsonRootBean.getStatus() == 0) {
                                    healthList.get(2).setHealthCount(jsonRootBean.getData().getStep());
                                    adapter.notifyDataSetChanged();
                                } else ;
                            }
                        });
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }



            }).start();
        }
        else if(date == "12:00"){
            sleepTime = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        RequestBody requestBody = new FormBody.Builder()
                                .add("id", String.valueOf(getActivity().getIntent().getIntExtra("user_id", 0)))
                                .add("sleep", String.valueOf(0))
                                .build();

                        Request request = new Request.Builder()
                                .url("http://39.108.137.129:8080/healthmonitor/user/update_information.do")
                                .post(requestBody)
                                .build();

                        OkHttpClient client = new OkHttpClient();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Gson gson = new Gson();
                        final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (jsonRootBean.getStatus() == 0) {
                                    healthList.get(3).setHealthCount(jsonRootBean.getData().getSleep());
                                    adapter.notifyDataSetChanged();
                                } else ;
                            }
                        });
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }



            }).start();
        }
    }

    private void sendMail(String toAdd) throws Exception{
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();          // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");  // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", "smtp.163.com");   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");      // 需要请求认证
        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        // 设置为debug模式, 可以查看详细的发送 log
        session.setDebug(true);
        // 3. 创建一封邮件
        MimeMessage message = MailUtils.createMimeMessage(session, "18335020653@163.com", toAdd);//我这里是以163邮箱为发信邮箱测试通过
        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();
        transport.connect("18335020653@163.com", "zl1909617156");
        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());
        // 7. 关闭连接
        transport.close();
    }



    private void initData() {
        // 图片资源id数组
        imageResIds = new int[]{R.drawable.aa, R.drawable.bb, R.drawable.cc, R.drawable.dd, R.drawable.ee};

        // 文本描述
        contentDescs = new String[]{
                "呵护您的健康",
                "呵护您的健康",
                "呵护您的健康",
                "呵护您的健康",
                "呵护您的健康"
        };

        // 初始化要展示的5个ImageView
        imageViewList = new ArrayList<ImageView>();

        ImageView imageView;
        View pointView;
        LinearLayout.LayoutParams layoutParams;
        for (int i = 0; i < imageResIds.length; i++) {
            // 初始化要显示的图片对象
            imageView = new ImageView(getContext());
            imageView.setBackgroundResource(imageResIds[i]);
            imageViewList.add(imageView);

            // 加小白点, 指示器
            pointView = new View(getContext());
            pointView.setBackgroundResource(R.drawable.if_check);
            layoutParams = new LinearLayout.LayoutParams(5, 5);
            if (i != 0)
                layoutParams.leftMargin = 10;
            // 设置默认所有都不可用
            pointView.setEnabled(false);
            ll_point_container.addView(pointView, layoutParams);
        }
    }

    private void initAdapter() {
        ll_point_container.getChildAt(0).setEnabled(true);
        tv_desc.setText(contentDescs[0]);
        previousSelectedPosition = 0;

        // 设置适配器
        viewPager.setAdapter(new ViewPageAdapter());

        // 默认设置到中间的某个位置
        int pos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % imageViewList.size());
        // 2147483647 / 2 = 1073741823 - (1073741823 % 5)
        viewPager.setCurrentItem(5000000); // 设置到某个位置
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }


    //适配器
    class ViewPageAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        // 3. 指定复用的判断逻辑, 固定写法
        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
        }

        // 1. 返回要显示的条目内容, 创建条目
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            System.out.println("instantiateItem初始化: " + position);

//          newPosition = position % 5
            int newPosition = position % imageViewList.size();

            ImageView imageView = imageViewList.get(newPosition);
            // a. 把View对象添加到container中
            container.addView(imageView);
            // b. 把View对象返回给框架, 适配器
            return imageView; // 必须重写, 否则报异常
        }

        // 2. 销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // object 要销毁的对象
            System.out.println("destroyItem销毁: " + position);
            container.removeView((View) object);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // 滚动时调用
    }

    @Override
    public void onPageSelected(int position) {
        // 新的条目被选中时调用
        System.out.println("onPageSelected: " + position);
        int newPosition = position % imageViewList.size();

        //设置文本
        tv_desc.setText(contentDescs[newPosition]);


        ll_point_container.getChildAt(previousSelectedPosition).setEnabled(false);
        ll_point_container.getChildAt(newPosition).setEnabled(true);

        // 记录之前的位置
        previousSelectedPosition = newPosition;

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // 滚动状态变化时调用
    }



}
