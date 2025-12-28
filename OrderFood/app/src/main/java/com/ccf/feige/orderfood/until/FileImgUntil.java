package com.ccf.feige.orderfood.until;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ccf.feige.orderfood.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 图片管理实现保存与加载
 */
public class FileImgUntil {

    private static  final ExecutorService executorService =Executors.newSingleThreadExecutor();

    public static  Future<Void> saveBitmapAsync(final Bitmap bitmap,final String path){
        return executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                saveImageBitmapToFileImg(bitmap,path);
                return null;
            }
        });
    }


    /**
     *
     * @param bitmap
     * @param path
     */

    public static void   saveImageBitmapToFileImg(Bitmap bitmap,String path){

        File file=new File(path);
        try {
            FileOutputStream fos=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }



    }



    /**
     *
     * @param url
     * @param context
     * @param path
     */
    public static void   saveImageBitmapToFileImg(Uri url,Context context,String path){
        CustomTarget<Bitmap> target=new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                //实现一个保存图标
                File file=new File(path);

                try {
                    FileOutputStream fos=new FileOutputStream(file);
                    resource.compress(Bitmap.CompressFormat.PNG,100,fos);
                    fos.flush();
                    fos.close();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        };

        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(target);

    }

    /**
     * 获取路径
     * @return
     */
    public static String getImgName(){
        String pigName="/"+ UUID.randomUUID().toString().replace("-","")+".png";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+pigName;
    }

    /**
     * 将系统当中的，图片比如r.id.xxx  保存到系统
     * @param context
     * @param id
     * @param path
     */
    public static void saveSystemImgToPath(Context context,int id,String path){
        Drawable defaultDrawable= ContextCompat.getDrawable(context, id);
        Bitmap bitmapDef = ((BitmapDrawable) defaultDrawable).getBitmap();//获取这个图片的二进制文件
        FileImgUntil.saveBitmapAsync(bitmapDef, path);//保存图片
    }
    public static void saveSystemImgToPath(Bitmap bitmap,String path){

        FileImgUntil.saveBitmapAsync(bitmap, path);//保存图片
    }


}
