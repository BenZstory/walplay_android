package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.ecustcs123.zhh.walplay.Utils.AppConstant;

public class TabSpotPicActivity extends Activity {
    private Button btnCamera;
    private ImageView imageView;
    private File file;
    private String filename;
    private String path;
    private View.OnClickListener btnCameraOnClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_spot_pic);
        btnCameraOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_Camera:
                        //拍照
                        String status = Environment.getExternalStorageState();
                        if (status.equals(Environment.MEDIA_MOUNTED)) {
                            try {
                                filename = getPicFileName();
                                path = Environment.getExternalStorageDirectory() + "/walplay/pic";
                                file = new File(path);
                                file.mkdirs();
                                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                file = new File(path, filename);
                                if (!file.exists()) ;
                                {
                                    file.createNewFile();
                                }
                                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                Toast.makeText(getApplicationContext(),"该图片将保存在:"+path+"/"+filename,Toast.LENGTH_LONG).show();
                                startActivityForResult(intent,  AppConstant.KEY.CAMERA_RESULT);

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
        };

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

    }

}


