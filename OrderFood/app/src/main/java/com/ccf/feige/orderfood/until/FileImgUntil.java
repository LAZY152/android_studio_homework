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
 * 图片管理工具类
 * 功能：实现图片的各种保存（Bitmap、网络/本地Uri图片、系统资源图片）与图片路径生成，提供同步/异步保存方式
 */
public class FileImgUntil {

    // 单线程线程池，用于执行图片保存的异步任务，保证任务串行执行，避免多线程操作文件冲突
    private static  final ExecutorService executorService =Executors.newSingleThreadExecutor();

    /**
     * 异步保存Bitmap图片到指定路径
     * @param bitmap  需要保存的Bitmap图片对象
     * @param path    图片要保存到的文件绝对路径
     * @return Future<Void> 异步任务结果对象，可通过该对象判断任务是否完成、获取异常等
     */
    public static  Future<Void> saveBitmapAsync(final Bitmap bitmap,final String path){
        // 提交Callable任务到线程池，实现异步执行
        return executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // 调用同步保存方法，在子线程中执行图片写入操作
                saveImageBitmapToFileImg(bitmap,path);
                return null;
            }
        });
    }


    /**
     * 同步保存Bitmap图片到指定文件路径（核心同步保存方法）
     * @param bitmap  需要保存的Bitmap图片对象
     * @param path    图片要保存到的文件绝对路径
     */
    public static void   saveImageBitmapToFileImg(Bitmap bitmap,String path){
        // 根据传入路径创建文件对象
        File file=new File(path);
        try {
            // 创建文件输出流，用于将Bitmap数据写入文件
            FileOutputStream fos=new FileOutputStream(file);
            // 将Bitmap压缩为PNG格式写入输出流，100表示压缩质量（PNG格式为无损压缩，该参数无效）
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            // 刷新输出流，确保所有数据都写入文件
            fos.flush();
            // 关闭输出流，释放资源
            fos.close();
        }catch (IOException e){
            // 捕获文件写入过程中的IO异常，打印异常堆栈信息
            e.printStackTrace();
        }
    }

    /**
     * 通过Glide加载Url对应的图片（网络图片/本地图片Uri），并保存到指定路径
     * @param url     图片的Url地址（支持网络Url、本地文件Uri等Glide可识别的Uri格式）
     * @param context 上下文对象，用于Glide初始化和资源访问
     * @param path    图片要保存到的文件绝对路径
     */
    public static void   saveImageBitmapToFileImg(Uri url,Context context,String path){
        // 创建Glide的自定义目标对象，用于接收加载完成后的Bitmap对象
        CustomTarget<Bitmap> target=new CustomTarget<Bitmap>() {
            /**
             * Glide图片加载完成回调方法
             * @param resource  加载成功后的Bitmap图片资源对象
             * @param transition  图片加载过渡动画（此处未使用）
             */
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                // 实现一个保存图标
                // 根据传入路径创建文件对象
                File file=new File(path);

                try {
                    // 创建文件输出流，用于将加载得到的Bitmap写入文件
                    FileOutputStream fos=new FileOutputStream(file);
                    // 将Bitmap压缩为PNG格式写入输出流
                    resource.compress(Bitmap.CompressFormat.PNG,100,fos);
                    // 刷新输出流，确保数据完整写入
                    fos.flush();
                    // 关闭输出流，释放资源
                    fos.close();
                }catch (IOException e){
                    // 捕获文件写入IO异常，打印异常信息
                    e.printStackTrace();
                }

            }

            /**
             * Glide加载图片被清除时的回调方法（如Activity销毁、加载取消等）
             * @param placeholder  图片加载的占位图（此处未使用）
             */
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // 可在此处释放相关资源，避免内存泄漏
            }
        };

        // Glide图片加载流程：初始化 -> 指定加载为Bitmap格式 -> 传入Uri地址 -> 加载到自定义Target中
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(target);

    }

    /**
     * 生成唯一的图片保存路径（保存到系统公共图片目录）
     * @return  完整的图片文件绝对路径，文件名采用UUID保证唯一性，格式为.png
     */
    public static String getImgName(){
        // 生成UUID并去除分隔符“-”，拼接为.png后缀的文件名
        String pigName="/"+ UUID.randomUUID().toString().replace("-","")+".png";
        // 获取系统公共图片目录（DIRECTORY_PICTURES）的绝对路径，拼接文件名生成完整路径
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+pigName;
    }

    /**
     * 保存Android系统资源图片（res/drawable等目录下的图片）到指定路径
     * @param context  上下文对象，用于获取系统资源
     * @param id       系统图片资源ID（如R.drawable.ic_launcher）
     * @param path     图片要保存到的文件绝对路径
     */
    public static void saveSystemImgToPath(Context context,int id,String path){
        // 通过ContextCompat获取系统资源对应的Drawable对象，兼容低版本Android系统
        Drawable defaultDrawable= ContextCompat.getDrawable(context, id);
        // 将Drawable对象强转为BitmapDrawable，提取其中的Bitmap图片对象
        Bitmap bitmapDef = ((BitmapDrawable) defaultDrawable).getBitmap();//获取这个图片的二进制文件
        // 调用异步保存方法，将系统资源图片保存到指定路径
        FileImgUntil.saveBitmapAsync(bitmapDef, path);//保存图片
    }

    /**
     * 重载方法：保存Bitmap图片到指定路径（调用异步保存方法）
     * @param bitmap  需要保存的Bitmap图片对象
     * @param path    图片要保存到的文件绝对路径
     */
    public static void saveSystemImgToPath(Bitmap bitmap,String path){
        // 调用异步保存方法，将Bitmap图片保存到指定路径
        FileImgUntil.saveBitmapAsync(bitmap, path);//保存图片
    }
}