package com.example.sfzhang.memo;

import android.os.Environment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileStorage {
    private File cropIconDir;
    private File iconDor;

    //图片保存到手机里
    public FileStorage() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File external = Environment.getExternalStorageDirectory();//将文件存储在外部sd卡
            String rootDir = "/" + "avater_path";
            cropIconDir = new File(external, rootDir + "/crop");//剪裁图片路径
            if (!cropIconDir.exists()) {
                cropIconDir.mkdirs();
            }
            iconDor = new File(external, rootDir + "/ic_launcher");
            if (!iconDor.exists()) {
                iconDor.mkdirs();
            }
        }
    }

    //创建裁剪文件，以及名字命名
    public File createCropFile() {
        String fileName = "";
        if (cropIconDir != null) {
            fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
        }
        return new File(iconDor, fileName);
    }

    //创建图标文件，以及名字命名
    public File createIconFile() {
        String fileName = "";
        if (iconDor != null) {
            fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
        }
        return new File(iconDor, fileName);
    }
}
