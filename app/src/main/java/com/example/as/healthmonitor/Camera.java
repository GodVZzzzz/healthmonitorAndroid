package com.example.as.healthmonitor;

import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.as.healthmonitor.JsonBean.JsonRootBean;
import com.example.as.healthmonitor.icon.CameraUtil;
import com.google.gson.Gson;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Camera extends Activity {

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private ImageView iv_personal_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        FloatingActionButton btn_change = (FloatingActionButton) findViewById(R.id.take_photo);
        iv_personal_icon = (ImageView) findViewById(R.id.camera_reviewer);
        final String account = getIntent().getStringExtra("user_account");
        final int id = getIntent().getIntExtra("user_id",0);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChoosePicDialog();
            }
        });

        FloatingActionButton btn_sumbit = (FloatingActionButton) findViewById(R.id.submit_photo);
        btn_sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account == null){
                    Toast.makeText(Camera.this,"请先登录",Toast.LENGTH_LONG).show();
                }
                else {
                    if(iv_personal_icon.getDrawable() == null) {
                        Toast.makeText(Camera.this, "请选择图片", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Bitmap image = ((BitmapDrawable) iv_personal_icon.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        final InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                FTPClient ftp = new FTPClient();
                                try {
                                    ftp.connect("39.108.137.129", 21);
                                    ftp.login("mhealth", "123456");// 登录
                                    int reply = ftp.getReplyCode();// 连接FTP服务器
                                    final boolean isSuccess = FTPReply.isPositiveCompletion(reply);
                                    final boolean isExist = ftp.changeWorkingDirectory("/vsftp/image");

                                    ftp.setBufferSize(1024);
                                    ftp.setControlEncoding("utf-8");
                                    ftp.enterLocalActiveMode();
                                    ftp.setFileType(FTP.BINARY_FILE_TYPE);
                                    //处理中文名称的文件名，如果不加这一句的话，中文命名的文件是不能上传的
                                    String filename = account;
                                    filename = new String(account.getBytes("GBK"), "iso-8859-1");
                                    final Boolean isStore = ftp.storeFile(filename, isBm);

                                    isBm.close();
                                    ftp.logout();

                                    if (isStore == true) {

                                        try {
                                            OkHttpClient client = new OkHttpClient();

                                            RequestBody requestBody = new FormBody.Builder()
                                                    .add("id", String.valueOf(id))
                                                    .add("imageid", account)
                                                    .build();

                                            Request request = new Request.Builder()
                                                    .url("http://39.108.137.129:8080/healthmonitor/user/update_information.do")
                                                    .post(requestBody)
                                                    .build();

                                            Response response = client.newCall(request).execute();
                                            String responseData = response.body().string();
                                            Gson gson = new Gson();
                                            final JsonRootBean jsonRootBean = gson.fromJson(responseData, JsonRootBean.class);

                                            Intent intent = new Intent(Camera.this, MainActivity.class);
                                            intent.putExtra("user_account", account);
                                            intent.putExtra("user_id", id);
                                            startActivity(intent);

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Camera.this, "上传成功", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        }).start();
                    }

                }

            }
        });
    }

    /**
     * 显示修改头像的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        String intentactiong = "";
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {//4.4版本前
                            intentactiong = Intent.ACTION_PICK;
                        } else {//4.4版本后
                            intentactiong = Intent.ACTION_GET_CONTENT;
                        }
                        Intent openAlbumIntent = new Intent(intentactiong);
                        openAlbumIntent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(openAlbumIntent,CHOOSE_PICTURE );
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "image.jpg"));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.activity_camera);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_style);


        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        String path = CameraUtil.getPath(this,uri);
        intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     *
     * @param picdata
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            iv_personal_icon.setImageBitmap(photo);
        }
    }

    public static String savePhoto(Bitmap photoBitmap, String path,
                                   String photoName) {
        String localPath = null;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File photoFile = new File(path, photoName + ".png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                            fileOutputStream)) { // 转换完成
                        localPath = photoFile.getPath();
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                localPath = null;
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                localPath = null;
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                        fileOutputStream = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return localPath;
    }

    public static boolean upload(String url, int port, String username,
                                 String password, String path, String filename, InputStream input) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(url, port);// 连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            boolean isExist = ftp.changeWorkingDirectory(path);
            if(!isExist){
                ftp.makeDirectory(path) ;
                ftp.changeWorkingDirectory(path);
            }

            ftp.setBufferSize(1024);
            ftp.setControlEncoding("utf-8");
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //处理中文名称的文件名，如果不加这一句的话，中文命名的文件是不能上传的
            filename = new String(filename.getBytes("GBK"), "iso-8859-1") ;
            ftp.storeFile(filename, input);


            input.close();
            ftp.logout();
            success = true;
            Log.e("FTPutil", "success") ;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }
}
