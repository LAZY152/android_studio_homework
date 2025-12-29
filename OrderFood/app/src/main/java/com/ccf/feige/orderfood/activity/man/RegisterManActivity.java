package com.ccf.feige.orderfood.activity.man;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ccf.feige.orderfood.MainActivity;
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.until.FileImgUntil;

/**
 * 商家注册页面（Java适配版）
 * 替换Kotlin协程为Java原生AsyncTask，解决Java与Kotlin协程的兼容问题
 * 新增：确认密码一致性校验逻辑
 * 修复：未完成注册退出后再次进入崩溃问题（统一退出清理、任务取消、脏数据清理）
 */
public class RegisterManActivity extends AppCompatActivity {
    // 常量定义
    /** 图片选择器支持的MIME类型，限定所有图片格式 */
    private static final String IMAGE_MIME_TYPE = "image/*";
    /** 注册成功的返回状态码，与AdminDao.saveBusinessUser返回值对应 */
    private static final int REGISTER_SUCCESS = 1;

    // 组件声明
    /** 系统相册图片选择器启动器，用于获取用户选择的头像Uri */
    private ActivityResultLauncher<String> getContentLauncher;
    /** 存储用户选择的头像Uri，用于后续图片保存操作 */
    private Uri avatarUri;
    /** 店铺头像展示ImageView */
    private ImageView ivAvatar;
    /** 店铺账号输入框 */
    private EditText etShopId;
    /** 店铺密码输入框 */
    private EditText etShopPwd;
    // 新增1：声明确认密码输入框组件
    /** 店铺密码确认输入框，用于前端两次密码一致性校验 */
    private EditText etShopPwdConfirm;
    /** 店铺名称输入框 */
    private EditText etShopName;
    /** 店铺描述输入框 */
    private EditText etShopDesc;
    /** 店铺类型输入框 */
    private EditText etShopType;

    // 【修改点1】添加AsyncTask引用，用于持有未完成的注册任务，方便后续取消
    /** 持有当前未完成的注册异步任务，用于退出页面或重复点击时取消任务，避免内存泄漏和重复执行 */
    private RegisterAsyncTask mRegisterTask;

    /**
     * 页面创建生命周期方法，初始化页面布局和所有组件
     * @param savedInstanceState 页面保存的状态数据，用于页面重建时恢复状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置页面布局文件
        setContentView(R.layout.activity_register_man);

        // 初始化Toolbar
        initToolbar();
        // 初始化视图组件
        initViews();
        // 初始化图片选择器
        initImagePicker();
        // 初始化注册按钮点击事件
        initRegisterButton();
    }

    /**
     * 初始化顶部Toolbar，设置返回按钮点击事件
     */
    private void initToolbar() {
        // 绑定布局中的Toolbar组件
        Toolbar toolbar = findViewById(R.id.register_man_toolbar);
        // 将Toolbar设置为页面的ActionBar
        setSupportActionBar(toolbar);
        // 【修改点2】修改Toolbar返回点击事件，调用统一的清理退出方法，而非默认onBackPressed()
        // 设置Toolbar左侧返回按钮的点击事件，触发统一的资源清理和页面退出
        toolbar.setNavigationOnClickListener(v -> cleanAndFinish());
    }

    /**
     * 初始化所有页面视图组件，绑定布局ID并设置相关点击事件
     */
    private void initViews() {
        // 绑定所有视图组件
        ivAvatar = findViewById(R.id.register_man_tx);
        etShopId = findViewById(R.id.register_man_id);
        etShopPwd = findViewById(R.id.register_man_pwd);
        // 新增2：初始化确认密码输入框（与布局中新增的id对应）
        etShopPwdConfirm = findViewById(R.id.register_man_pwd_confirm);
        etShopName = findViewById(R.id.register_man_name);
        etShopDesc = findViewById(R.id.register_man_des);
        etShopType = findViewById(R.id.register_man_type);

        // 初始化头像Tag（使用资源ID，需提前在ids.xml定义），标记是否已选择自定义头像
        ivAvatar.setTag(R.id.tag_avatar_selected, false);
        // 头像点击事件，触发打开系统相册选择图片
        ivAvatar.setOnClickListener(v -> openGallery());
    }

    /**
     * 初始化系统相册图片选择器，注册ActivityResult回调获取选择结果
     */
    private void initImagePicker() {
        // 注册图片选择器，指定获取内容的契约和结果回调
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), // 获取系统内容的契约，支持选择相册图片
                new ActivityResultCallback<Uri>() { // 选择结果回调
                    /**
                     * 图片选择完成后的回调方法
                     * @param result 选择的图片Uri，未选择时为null
                     */
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            // 保存选择的图片Uri
                            avatarUri = result;
                            // 在ImageView中展示选择的图片
                            ivAvatar.setImageURI(avatarUri);
                            // 标记已选择自定义头像
                            ivAvatar.setTag(R.id.tag_avatar_selected, true);
                        } else {
                            // 未选择图片，提示用户
                            showToast("未选择头像");
                            // 标记未选择自定义头像
                            ivAvatar.setTag(R.id.tag_avatar_selected, false);
                        }
                    }
                }
        );
    }

    /**
     * 初始化注册按钮，绑定点击事件触发注册流程
     */
    private void initRegisterButton() {
        // 绑定注册按钮组件
        Button btnRegister = findViewById(R.id.register_man_zcsj);
        // 设置注册按钮点击事件，执行注册逻辑
        btnRegister.setOnClickListener(v -> performRegister());
    }

    /**
     * 执行注册核心流程：获取表单数据 -> 表单验证 -> 异步执行注册任务
     */
    private void performRegister() {
        // 获取表单数据，trim()去除首尾空格
        String shopId = etShopId.getText().toString().trim();
        String shopPwd = etShopPwd.getText().toString().trim();
        // 新增3：获取确认密码输入值
        String shopPwdConfirm = etShopPwdConfirm.getText().toString().trim();
        String shopName = etShopName.getText().toString().trim();
        String shopDesc = etShopDesc.getText().toString().trim();
        String shopType = etShopType.getText().toString().trim();

        // 修改：传递确认密码到表单验证方法
        // 表单验证不通过则直接返回，终止注册流程
        if (!validateForm(shopId, shopPwd, shopPwdConfirm, shopName, shopDesc, shopType)) {
            return;
        }

        // 【修改点3】先取消已有未完成任务，再创建新任务并持有引用，避免重复执行
        // 如果存在未完成的注册任务，先取消该任务，防止重复注册
        if (mRegisterTask != null && !mRegisterTask.isCancelled()) {
            mRegisterTask.cancel(true);
        }
        // 执行异步任务（替换协程为AsyncTask，确认密码仅前端校验，无需传入异步任务）
        // 创建新的注册异步任务，传入表单核心数据
        mRegisterTask = new RegisterAsyncTask(shopId, shopPwd, shopName, shopDesc, shopType);
        // 执行异步任务
        mRegisterTask.execute();
    }

    // 修改：增加确认密码参数（shopPwdConfirm）
    /**
     * 表单数据合法性校验，包括非空、长度、一致性、头像选择校验
     * @param shopId 店铺账号
     * @param shopPwd 店铺密码
     * @param shopPwdConfirm 店铺确认密码
     * @param shopName 店铺名称
     * @param shopDesc 店铺描述
     * @param shopType 店铺类型
     * @return 校验通过返回true，校验失败返回false
     */
    private boolean validateForm(String shopId, String shopPwd, String shopPwdConfirm,
                                 String shopName, String shopDesc, String shopType) {
        // 验证头像：是否选择了自定义头像
        if (isDefaultAvatar()) {
            showToast("请点击图片添加店铺头像");
            return false;
        }
        // 非空验证：逐个校验输入框是否为空
        if (TextUtils.isEmpty(shopId)) { showToast("请输入店铺账号"); return false; }
        if (TextUtils.isEmpty(shopPwd)) { showToast("请输入店铺密码"); return false; }
        // 新增4：确认密码非空验证
        if (TextUtils.isEmpty(shopPwdConfirm)) { showToast("请确认店铺密码"); return false; }
        if (TextUtils.isEmpty(shopName)) { showToast("请输入店铺名称"); return false; }
        if (TextUtils.isEmpty(shopDesc)) { showToast("请输入店铺描述"); return false; }
        if (TextUtils.isEmpty(shopType)) { showToast("请输入店铺类型"); return false; }

        // 长度验证：校验输入内容的长度合法性
        if (shopPwd.length() < 6) { showToast("密码长度不能少于6位"); return false; }
        // 新增5：确认密码长度验证（与原密码保持一致规则）
        if (shopPwdConfirm.length() < 6) { showToast("确认密码长度不能少于6位"); return false; }
        if (shopName.length() > 50) { showToast("店铺名称不能超过50个字符"); return false; }

        // 新增6：核心逻辑 - 两次密码一致性校验
        if (!shopPwd.equals(shopPwdConfirm)) {
            showToast("两次输入的密码不一致，请重新输入");
            return false;
        }

        // 所有校验通过
        return true;
    }

    /**
     * Java原生异步任务：处理图片保存+数据库注册
     * 子线程执行耗时操作（文件保存、数据库写入），主线程更新UI结果
     */
    private class RegisterAsyncTask extends AsyncTask<Void, Void, Integer> {
        // 店铺注册核心数据，通过构造方法传入
        private String shopId, shopPwd, shopName, shopDesc, shopType;
        /** 存储异步任务执行过程中的错误信息，用于后续提示用户 */
        private String errorMsg;

        /**
         * 异步任务构造方法，初始化店铺注册核心数据
         * @param shopId 店铺账号
         * @param shopPwd 店铺密码
         * @param shopName 店铺名称
         * @param shopDesc 店铺描述
         * @param shopType 店铺类型
         */
        public RegisterAsyncTask(String shopId, String shopPwd, String shopName, String shopDesc, String shopType) {
            this.shopId = shopId;
            this.shopPwd = shopPwd;
            this.shopName = shopName;
            this.shopDesc = shopDesc;
            this.shopType = shopType;
        }

        // 子线程执行耗时操作
        /**
         * 异步任务后台执行方法，运行在子线程，处理耗时操作
         * @param voids 可变参数，此处无实际传入值
         * @return 执行结果状态码：1=成功，-1=异常失败，-2=任务被取消
         */
        @Override
        protected Integer doInBackground(Void... voids) {
            // 【修改点4】添加任务取消判断，避免取消后仍执行耗时操作
            if (isCancelled()) {
                return -2; // 任务被取消的标记
            }
            try {
                // 1. 保存图片：生成图片文件名并保存选择的头像
                String imgPath = FileImgUntil.getImgName();
                if (avatarUri != null && !isCancelled()) {
                    FileImgUntil.saveImageBitmapToFileImg(avatarUri, RegisterManActivity.this, imgPath);
                }
                // 2. 数据库注册（添加取消判断，避免无效操作）
                if (isCancelled()) {
                    return -2;
                }
                // 调用Dao层方法将店铺信息写入数据库
                return AdminDao.saveBusinessUser(shopId, shopPwd, shopName, shopDesc, shopType, imgPath);
            } catch (Exception e) {
                // 捕获执行过程中的异常，打印异常堆栈并记录错误信息
                e.printStackTrace();
                errorMsg = "注册失败，发生未知错误";
                return -1; // 失败标记
            }
        }

        // 主线程更新UI
        /**
         * 异步任务后台执行完成后的回调方法，运行在主线程，用于更新UI结果
         * @param result 后台执行返回的状态码
         */
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            // 【修改点5】添加Activity状态判断，避免已销毁/正在销毁时操作UI
            if (isFinishing() || isDestroyed()) {
                return;
            }
            // 任务被取消则不处理UI
            if (result == -2) {
                return;
            }
            // 根据返回状态码处理不同的UI结果
            if (result == REGISTER_SUCCESS) {
                // 注册成功，提示用户并延迟退出页面
                showToast("注册成功！即将返回登录页面");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 注册完成后也调用统一清理退出方法，保持逻辑一致
                        cleanAndFinish();
                    }
                }, 500); // 延迟时间：500毫秒（半秒），给用户足够的提示查看时间
            } else if (result == -1) {
                // 异常失败，提示具体错误信息
                showToast(errorMsg);
            } else {
                // 账号冲突等业务失败，提示用户
                showToast("注册商家失败，账号冲突");
            }
            // 任务执行完成，置空任务引用，帮助GC回收
            mRegisterTask = null;
        }
    }

    /**
     * 打开系统相册，启动图片选择器
     */
    private void openGallery() {
        // 启动图片选择器，传入支持的图片MIME类型
        getContentLauncher.launch(IMAGE_MIME_TYPE);
    }

    /**
     * 判断是否为默认头像（是否未选择自定义头像）
     * @return 未选择自定义头像返回true，已选择返回false
     */
    private boolean isDefaultAvatar() {
        // 获取头像的选择标记Tag
        Object tag = ivAvatar.getTag(R.id.tag_avatar_selected);
        if (tag == null) {
            // Tag未初始化时，默认标记为未选择，并返回true
            ivAvatar.setTag(R.id.tag_avatar_selected, false);
            return true;
        }
        // 转换Tag类型并返回取反结果（Tag为false表示未选择，返回true）
        return !(Boolean) tag;
    }

    /**
     * 从Drawable中安全获取Bitmap，避免空指针和异常
     * @param drawable 待转换的Drawable对象
     * @param context 上下文对象，用于获取资源
     * @return 转换后的Bitmap，异常时返回最小空白Bitmap
     */
    private Bitmap getSafeBitmapFromDrawable(Drawable drawable, Context context) {
        if (drawable == null) {
            // Drawable为空时，返回1x1的空白Bitmap
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
        if (drawable instanceof BitmapDrawable) {
            // Drawable为BitmapDrawable时，直接获取内部Bitmap
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return bitmap != null ? bitmap : Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
        try {
            // 其他类型Drawable，通过Canvas绘制转换为Bitmap
            int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 200;
            int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 200;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            // 捕获转换异常，打印堆栈并返回空白Bitmap
            e.printStackTrace();
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
    }

    /**
     * 统一的Toast提示方法，简化吐司显示代码
     * @param msg 要显示的提示信息
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // 【修改点6】封装统一的清理资源并退出方法（核心修复）
    /**
     * 统一的资源清理和页面退出方法，解决未完成注册退出后再次进入崩溃问题
     * 步骤：取消异步任务 -> 清理输入框 -> 重置头像 -> 置空组件引用 -> 退出页面
     */
    private void cleanAndFinish() {
        // 步骤1：取消未完成的注册异步任务，避免子线程操作已销毁的Activity
        if (mRegisterTask != null && !mRegisterTask.isCancelled()) {
            mRegisterTask.cancel(true);
            mRegisterTask = null;
        }

        // 步骤2：清理所有EditText的脏数据，避免再次进入时残留
        clearAllEditTexts();

        // 步骤3：重置头像状态，释放图片资源
        resetAvatarState();

        // 步骤4：置空组件引用，帮助GC回收，避免内存泄漏
        unbindViews();

        // 步骤5：正常关闭当前页面
        finish();
    }

    /**
     * 辅助方法：清理所有输入框的内容，清空脏数据
     */
    private void clearAllEditTexts() {
        if (etShopId != null) etShopId.setText("");
        if (etShopPwd != null) etShopPwd.setText("");
        if (etShopPwdConfirm != null) etShopPwdConfirm.setText("");
        if (etShopName != null) etShopName.setText("");
        if (etShopDesc != null) etShopDesc.setText("");
        if (etShopType != null) etShopType.setText("");
    }

    /**
     * 辅助方法：重置头像状态，释放图片资源，避免内存泄漏
     */
    private void resetAvatarState() {
        // 置空头像Uri
        avatarUri = null;
        if (ivAvatar != null) {
            // 清空ImageView的图片，替换为透明背景（可替换为默认头像）
            ivAvatar.setImageResource(android.R.color.transparent);
            // 重置头像选择标记
            ivAvatar.setTag(R.id.tag_avatar_selected, false);
            // 释放图片Bitmap资源
            Drawable drawable = ivAvatar.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    /**
     * 辅助方法：置空所有视图组件引用，帮助GC回收，避免内存泄漏
     */
    private void unbindViews() {
        ivAvatar = null;
        etShopId = null;
        etShopPwd = null;
        etShopPwdConfirm = null;
        etShopName = null;
        etShopDesc = null;
        etShopType = null;
        getContentLauncher = null;
    }

    // 【修改点7】重写物理返回键事件，调用统一清理退出方法
    /**
     * 重写物理返回键事件，确保用户点击物理返回时也执行统一的资源清理流程
     */
    @Override
    public void onBackPressed() {
        cleanAndFinish();
    }

    /**
     * 页面销毁生命周期方法，再次强化资源释放，双重保障避免内存泄漏
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 【修改点8】再次强化资源释放，双重保障
        // 取消未完成任务
        if (mRegisterTask != null && !mRegisterTask.isCancelled()) {
            mRegisterTask.cancel(true);
            mRegisterTask = null;
        }
        // 释放图片资源
        Drawable drawable = ivAvatar != null ? ivAvatar.getDrawable() : null;
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        // 置空所有引用
        avatarUri = null;
        unbindViews();
    }
}