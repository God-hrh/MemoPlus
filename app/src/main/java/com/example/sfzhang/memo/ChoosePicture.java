package com.example.sfzhang.memo;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**choosePicture扩展BaseActivity实现View.onClickListener*/
public class ChoosePicture extends BaseActivity implements View.OnClickListener {

    public  String AUTHOR_PROVIDERS = "";
    private Button bt_pick;
    private Button start_carmer;
    ArrayList<String> picturepath;

    /**选择图库,并选择具体图片后的返回值*/
    private static final int REQUEST_PICK_IMAGE = 2;

    /**被选择图片的URI*/
    private Uri imageUri;

    /**被选择图片的地址*/
    private String imagePath;

    /**保存的URI*/
    private Uri outputUri;

    /**裁剪图后*/
    private static final int REQUEST_PICK_CUT = 3;
    private static final int REQUEST_CAPTURE = 7;
    private GridView gv;
    private EditText tv_content;
    private Button bt_sure;
    private ProgressBar mProgressBar;
    private WebView wb_response;//响应
    private StringBuilder allResponse = new StringBuilder();//所有响应

    @Override
    protected void onCreate(Bundle savedInstanceState) {//保存的实例状态
        super.onCreate(savedInstanceState);
        initData();
        initView();
        initEvent();
        initPermission();
    }
    private void initData() {
        AUTHOR_PROVIDERS=getPackageName()+".fileprovider";//文件提供者
        picturepath = new ArrayList<>();
    }
    private void initEvent() {
        bt_sure.setOnClickListener(this);
        start_carmer.setOnClickListener(this);
    }

    private void initPermission() {//初始权限授权
        /**要授权，否则在选择图片裁剪后不能进行写入到SD卡的操作，会导致设置图片失败，调用BaseActivity的方法*/
        performCodeWithPermission("拍照", new PermissionCallback() {
                    @Override
                    public void hasPermission() {
                    }
                    @Override
                    public void noPermission() {
                    }

                    /**权限：相机，写存储，安装卸载文件系统*/
                }, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
    }

    /**初始化视图*/
    private void initView() {
        setContentView(R.layout.activity_choosepicture);
        wb_response = (WebView) findViewById(R.id.mTv);
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        bt_pick = (Button) findViewById(R.id.bt_pick);
        start_carmer = (Button) findViewById(R.id.start_carmer);
        bt_pick.setOnClickListener(this);
        tv_content = (EditText) findViewById(R.id.tv_content);
        bt_sure = (Button) findViewById(R.id.bt_sure);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pick:
                if (ContextCompat.checkSelfPermission(ChoosePicture.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChoosePicture.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    /**调用图库，获取所有本地图片：*/
                    /**在常见的Activity Action Intent常量中，ACTION_PICK  android.intent.action.PICK 是“选择数据”的意思*/
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, REQUEST_PICK_IMAGE);
                }
                break;
            case R.id.start_carmer:
                if (ContextCompat.checkSelfPermission(ChoosePicture.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChoosePicture.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    openCamera();
                }
                break;
        }
    }

    /**打开相机*/
    private void openCamera() {
        File file = new FileStorage().createCropFile();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            try {
                imageUri = FileProvider.getUriForFile(ChoosePicture.this, AUTHOR_PROVIDERS, file);
            } catch (Exception e) {
                e.printStackTrace();
                imageUri = Uri.fromFile(file);
            }
        } else
            imageUri = Uri.fromFile(file);
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    @Override
    //活动结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE) {
            if (resultCode == RESULT_CANCELED) {
                return;//没有选择图片
            }
            if (Build.VERSION.SDK_INT >= 19) {
                //机型适配
                handleImageOnKitKat(data);//7.0
            } else {
                handleImageBeforeKitKat(data);
            }
        }
        if (requestCode == REQUEST_PICK_CUT) {/**裁剪后*/
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
                System.out.println("路径：" + outputUri.getEncodedPath());
                picturepath.add(outputUri.getEncodedPath());
                gv = (GridView) findViewById(R.id.gv_picture);
                gv.setAdapter(new ImageAdapter(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_CAPTURE) {//拍照返回
            if (resultCode == RESULT_OK) {
                cropPhoto();
            }
        }
    }

    /**
      * 复制单个文件
      * @param oldPath String 原文件路径 如：data/video/xxx.mp4
      * @param newPath String 复制后路径 如：data/oss/xxx.mp4
      * @return boolean
      */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**图片显示适配器*/
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        public ImageAdapter(Context c) {
            mContext = c;
        }
        //获取图片的个数
        public int getCount() {
            return picturepath.size();
        }
        //获取图片在库中的位置
        public String getItem(int arg0) {
            System.out.println("新地址："+picturepath.get(arg0));

            String path = picturepath.get(arg0);
            System.out.println("图片地址："+path);

            String path_new = getFileName(path);
            String newpath = Environment.getExternalStorageDirectory().getPath() + "/"+path_new;
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            copyFile(path,newpath);
            System.out.println("测试输出");
            return picturepath.get(arg0);
        }

        /**获取图片的ID*/
        public long getItemId(int arg0) {
            return arg0;
        }

        public View getView(int position, View converView, ViewGroup parent) {
            ImageView imageView;
            if (converView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(1, 1, 1, 1);
            } else {
                imageView = (ImageView) converView;
            }
            imageView.setImageBitmap(BitmapFactory.decodeFile(getItem(position)));
            return imageView;
        }

    }

    //获取文件名
    private String getFileName(String fPath) {
        String[] fName = fPath.split("/");
        int length = fName.length;
        String nfPath = fName[length - 1];
        return nfPath;
    }

    /**
     * 获取文件路径
     */
    private void handleImageBeforeKitKat(Intent data) {
        imageUri = data.getData();
        imagePath = getImagePath(imageUri, null);
        cropPhoto();
    }

    /**裁剪图片*/
    private void cropPhoto() {
        File file = new FileStorage().createCropFile();
        outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputX", 300);//指定裁剪后图片的大小，避免出现大图片上传失败的情况
        intent.putExtra("outputY", 300);//
        startActivityForResult(intent, REQUEST_PICK_CUT);
    }

    /**
     * 获取各个版本的文件的路径
     */
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        imageUri = data.getData();
        if (DocumentsContract.isDocumentUri(this, imageUri)) {
            String docId = DocumentsContract.getDocumentId(imageUri);
            if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            imagePath = getImagePath(imageUri, null);
        } else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            imagePath = imageUri.getPath();
        }
        cropPhoto();
    }

    /**获取文件路径@param uri@param selection@return*/
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));//获取列索引
            }
            cursor.close();
        }
        return path;
    }
}
