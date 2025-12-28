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
    private static final String IMAGE_MIME_TYPE = "image/*";
    private static final int REGISTER_SUCCESS = 1;

    // 组件声明
    private ActivityResultLauncher<String> getContentLauncher;
    private Uri avatarUri;
    private ImageView ivAvatar;
    private EditText etShopId;
    private EditText etShopPwd;
    // 新增1：声明确认密码输入框组件
    private EditText etShopPwdConfirm;
    private EditText etShopName;
    private EditText etShopDesc;
    private EditText etShopType;

    // 【修改点1】添加AsyncTask引用，用于持有未完成的注册任务，方便后续取消
    private RegisterAsyncTask mRegisterTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_man);

        // 初始化Toolbar
        initToolbar();
        // 初始化视图
        initViews();
        // 初始化图片选择器
        initImagePicker();
        // 初始化注册按钮
        initRegisterButton();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.register_man_toolbar);
        setSupportActionBar(toolbar);
        // 【修改点2】修改Toolbar返回点击事件，调用统一的清理退出方法，而非默认onBackPressed()
        toolbar.setNavigationOnClickListener(v -> cleanAndFinish());
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.register_man_tx);
        etShopId = findViewById(R.id.register_man_id);
        etShopPwd = findViewById(R.id.register_man_pwd);
        // 新增2：初始化确认密码输入框（与布局中新增的id对应）
        etShopPwdConfirm = findViewById(R.id.register_man_pwd_confirm);
        etShopName = findViewById(R.id.register_man_name);
        etShopDesc = findViewById(R.id.register_man_des);
        etShopType = findViewById(R.id.register_man_type);

        // 初始化头像Tag（使用资源ID，需提前在ids.xml定义）
        ivAvatar.setTag(R.id.tag_avatar_selected, false);
        // 头像点击事件
        ivAvatar.setOnClickListener(v -> openGallery());
    }

    private void initImagePicker() {
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            avatarUri = result;
                            ivAvatar.setImageURI(avatarUri);
                            ivAvatar.setTag(R.id.tag_avatar_selected, true);
                        } else {
                            showToast("未选择头像");
                            ivAvatar.setTag(R.id.tag_avatar_selected, false);
                        }
                    }
                }
        );
    }

    private void initRegisterButton() {
        Button btnRegister = findViewById(R.id.register_man_zcsj);
        btnRegister.setOnClickListener(v -> performRegister());
    }

    private void performRegister() {
        // 获取表单数据
        String shopId = etShopId.getText().toString().trim();
        String shopPwd = etShopPwd.getText().toString().trim();
        // 新增3：获取确认密码输入值
        String shopPwdConfirm = etShopPwdConfirm.getText().toString().trim();
        String shopName = etShopName.getText().toString().trim();
        String shopDesc = etShopDesc.getText().toString().trim();
        String shopType = etShopType.getText().toString().trim();

        // 修改：传递确认密码到表单验证方法
        if (!validateForm(shopId, shopPwd, shopPwdConfirm, shopName, shopDesc, shopType)) {
            return;
        }

        // 【修改点3】先取消已有未完成任务，再创建新任务并持有引用，避免重复执行
        if (mRegisterTask != null && !mRegisterTask.isCancelled()) {
            mRegisterTask.cancel(true);
        }
        // 执行异步任务（替换协程为AsyncTask，确认密码仅前端校验，无需传入异步任务）
        mRegisterTask = new RegisterAsyncTask(shopId, shopPwd, shopName, shopDesc, shopType);
        mRegisterTask.execute();
    }

    // 修改：增加确认密码参数（shopPwdConfirm）
    private boolean validateForm(String shopId, String shopPwd, String shopPwdConfirm,
                                 String shopName, String shopDesc, String shopType) {
        // 验证头像
        if (isDefaultAvatar()) {
            showToast("请点击图片添加店铺头像");
            return false;
        }
        // 非空验证
        if (TextUtils.isEmpty(shopId)) { showToast("请输入店铺账号"); return false; }
        if (TextUtils.isEmpty(shopPwd)) { showToast("请输入店铺密码"); return false; }
        // 新增4：确认密码非空验证
        if (TextUtils.isEmpty(shopPwdConfirm)) { showToast("请确认店铺密码"); return false; }
        if (TextUtils.isEmpty(shopName)) { showToast("请输入店铺名称"); return false; }
        if (TextUtils.isEmpty(shopDesc)) { showToast("请输入店铺描述"); return false; }
        if (TextUtils.isEmpty(shopType)) { showToast("请输入店铺类型"); return false; }

        // 长度验证
        if (shopPwd.length() < 6) { showToast("密码长度不能少于6位"); return false; }
        // 新增5：确认密码长度验证（与原密码保持一致规则）
        if (shopPwdConfirm.length() < 6) { showToast("确认密码长度不能少于6位"); return false; }
        if (shopName.length() > 50) { showToast("店铺名称不能超过50个字符"); return false; }

        // 新增6：核心逻辑 - 两次密码一致性校验
        if (!shopPwd.equals(shopPwdConfirm)) {
            showToast("两次输入的密码不一致，请重新输入");
            return false;
        }

        return true;
    }

    /**
     * Java原生异步任务：处理图片保存+数据库注册
     */
    private class RegisterAsyncTask extends AsyncTask<Void, Void, Integer> {
        private String shopId, shopPwd, shopName, shopDesc, shopType;
        private String errorMsg;

        public RegisterAsyncTask(String shopId, String shopPwd, String shopName, String shopDesc, String shopType) {
            this.shopId = shopId;
            this.shopPwd = shopPwd;
            this.shopName = shopName;
            this.shopDesc = shopDesc;
            this.shopType = shopType;
        }

        // 子线程执行耗时操作
        @Override
        protected Integer doInBackground(Void... voids) {
            // 【修改点4】添加任务取消判断，避免取消后仍执行耗时操作
            if (isCancelled()) {
                return -2; // 任务被取消的标记
            }
            try {
                // 1. 保存图片
                String imgPath = FileImgUntil.getImgName();
                if (avatarUri != null && !isCancelled()) {
                    FileImgUntil.saveImageBitmapToFileImg(avatarUri, RegisterManActivity.this, imgPath);
                }
                // 2. 数据库注册（添加取消判断，避免无效操作）
                if (isCancelled()) {
                    return -2;
                }
                return AdminDao.saveBusinessUser(shopId, shopPwd, shopName, shopDesc, shopType, imgPath);
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = "注册失败，发生未知错误";
                return -1; // 失败标记
            }
        }

        // 主线程更新UI
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
            if (result == REGISTER_SUCCESS) {
                showToast("注册商家成功");
                // 核心修改：延迟500毫秒（半秒）后返回/跳转登录界面
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 注册完成后也调用统一清理退出方法，保持逻辑一致
                        cleanAndFinish();
                    }
                }, 500); // 延迟时间：500毫秒（半秒）
            } else if (result == -1) {
                showToast(errorMsg);
            } else {
                showToast("注册商家失败，账号冲突");
            }
            // 任务执行完成，置空任务引用
            mRegisterTask = null;
        }
    }

    private void openGallery() {
        getContentLauncher.launch(IMAGE_MIME_TYPE);
    }

    private boolean isDefaultAvatar() {
        Object tag = ivAvatar.getTag(R.id.tag_avatar_selected);
        if (tag == null) {
            ivAvatar.setTag(R.id.tag_avatar_selected, false);
            return true;
        }
        return !(Boolean) tag;
    }

    private Bitmap getSafeBitmapFromDrawable(Drawable drawable, Context context) {
        if (drawable == null) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return bitmap != null ? bitmap : Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
        try {
            int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 200;
            int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 200;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // 【修改点6】封装统一的清理资源并退出方法（核心修复）
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

    // 辅助方法：清理所有输入框内容
    private void clearAllEditTexts() {
        if (etShopId != null) etShopId.setText("");
        if (etShopPwd != null) etShopPwd.setText("");
        if (etShopPwdConfirm != null) etShopPwdConfirm.setText("");
        if (etShopName != null) etShopName.setText("");
        if (etShopDesc != null) etShopDesc.setText("");
        if (etShopType != null) etShopType.setText("");
    }

    // 辅助方法：重置头像状态
    private void resetAvatarState() {
        avatarUri = null;
        if (ivAvatar != null) {
            ivAvatar.setImageResource(android.R.color.transparent); // 清空图片，可替换为你的默认头像
            ivAvatar.setTag(R.id.tag_avatar_selected, false);
            // 释放图片Bitmap
            Drawable drawable = ivAvatar.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        }
    }

    // 辅助方法：置空组件引用
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
    @Override
    public void onBackPressed() {
        cleanAndFinish();
    }

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