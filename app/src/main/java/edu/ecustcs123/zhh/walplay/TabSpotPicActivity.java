package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TabSpotPicActivity extends Activity {
    private Button btnCamera;
    private ImageView imageView;
    private File file;
    private String filename;
    private String path;
    private BtnCameraOnClickListener btnCameraOnClickListener;
    public final static int CAMERA_RESULT = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_spot_pic);
        btnCameraOnClickListener = new BtnCameraOnClickListener();

        btnCamera = (Button) findViewById(R.id.btn_Camera);
        imageView = (ImageView) findViewById(R.id.CurrentSpotPic);
        imageView.setBackgroundResource(R.drawable.bfzn_006);
        btnCamera.setOnClickListener(btnCameraOnClickListener);

    }


    /**
     * 用时间戳生成照片名称
     */
    private String getPicFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RESULT) {
            Bitmap bitmap = BitmapFactory.decodeFile(path, null);
            imageView.setImageBitmap(bitmap);
        }
    }

    private class BtnCameraOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_Camera:
                    //拍照
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) {
                        try {
                            filename = getPicFileName();
                            Log.d("Camera","getFilename");
                            path = Environment.getExternalStorageDirectory() + "/walplay/pic";
                            file = new File(path);
                            file.mkdirs();
                            path = path+
                            Log.d("Camera","getFilepath");
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            file = new File(path);
                            Log.d("Camera","file=newFile");
                            if(!file.exists())
                            {
                                Log.d("Camera","filenotfound");
                                file.createNewFile();
                                Log.d("Camera","createnewfile");
                            }
                            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            startActivityForResult(intent, CAMERA_RESULT);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(TabSpotPicActivity.this, "没有找到储存目录", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(TabSpotPicActivity.this, "没有储存卡", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }
}


