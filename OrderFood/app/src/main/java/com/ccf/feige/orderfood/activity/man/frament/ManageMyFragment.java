package com.ccf.feige.orderfood.activity.man.frament;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ccf.feige.orderfood.MainActivity;
import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.man.ManageManActivity;
import com.ccf.feige.orderfood.activity.man.ManageManCommentActivity;
import com.ccf.feige.orderfood.activity.man.ManageManOrderFinishActivity;
import com.ccf.feige.orderfood.activity.man.ManageManOrderNoFinishActivity;
import com.ccf.feige.orderfood.activity.man.ManageManUpdateMesActivity;
import com.ccf.feige.orderfood.activity.man.ManageManUpdatePwdActivity;
import com.ccf.feige.orderfood.bean.UserBean;
import com.ccf.feige.orderfood.dao.AdminDao;
import com.ccf.feige.orderfood.until.Tools;

import java.io.File;

/**
 * 商家个人中心碎片
 * 功能：加载商家信息、头像展示、功能跳转（修改密码/信息、订单/评论管理等）、退出/注销账号
 */
public class ManageMyFragment extends Fragment {
    // 根视图（修正拼写错误，规范命名）
    private View rootView;
    // 商家信息相关控件（规范命名，语义清晰）
    private ImageView ivMerchantAvatar;
    private TextView tvMerchantAccount;
    private TextView tvMerchantName;
    private TextView tvMerchantDesc;
    // 功能入口相关控件
    private TextView tvExitAccount; // 退出账号（原退出系统）
    private TextView tvLogout; // 注销账号（需验证密码）
    private TextView tvChangePassword;
    private TextView tvChangeInfo;
    private Button btnUnfinishedOrder;
    private Button btnCommentManage;
    private Button btnFinishedOrder;

    // 全局变量：当前登录商家账号、商家信息（避免重复查询，便于后续使用）
    private String currentMerchantAccount;
    private UserBean currentMerchant;
    // 全局变量：宿主Activity（避免重复强转，提升容错性）
    private ManageManActivity hostActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局，初始化根视图
        rootView = inflater.inflate(R.layout.fragment_manage_my, container, false);

        // 1. 初始化所有控件（集中管理，避免分散findViewById）
        initViews();

        // 2. 加载并展示商家信息（含容错处理）
        loadMerchantInfo();

        // 3. 初始化所有控件的点击事件（集中管理，逻辑清晰）
        initViewClickEvents();

        return rootView;
    }

    /**
     * 初始化所有视图控件，规范命名，便于维护
     */
    private void initViews() {
        // 商家信息控件
        ivMerchantAvatar = rootView.findViewById(R.id.man_manage_my_tx);
        tvMerchantAccount = rootView.findViewById(R.id.man_manage_my_account);
        tvMerchantName = rootView.findViewById(R.id.man_manage_my_name);
        tvMerchantDesc = rootView.findViewById(R.id.man_manage_my_des);

        // 功能入口控件
        tvExitAccount = rootView.findViewById(R.id.man_manage_my_exit); // 退出账号
        tvLogout = rootView.findViewById(R.id.man_manage_my_zx); // 注销账号
        tvChangePassword = rootView.findViewById(R.id.man_manage_my_changePwd);
        tvChangeInfo = rootView.findViewById(R.id.man_manage_my_changeMes);
        btnUnfinishedOrder = rootView.findViewById(R.id.man_manage_my_orderManage);
        btnCommentManage = rootView.findViewById(R.id.man_manage_my_commentMan);
        btnFinishedOrder = rootView.findViewById(R.id.man_manage_my_finish);

        // 初始化宿主Activity（含容错，避免强转崩溃）
        hostActivity = (getActivity() instanceof ManageManActivity) ? (ManageManActivity) getActivity() : null;
    }

    /**
     * 加载商家信息（含全链路容错，避免空指针崩溃）
     */
    private void loadMerchantInfo() {
        // 1. 获取当前登录商家账号（判空，避免无效查询）
        currentMerchantAccount = Tools.getOnAccount(getContext());
        if (currentMerchantAccount == null || currentMerchantAccount.trim().isEmpty()) {
            showToast("未获取到登录商家账号，请重新登录");
            return;
        }

        // 2. 查询商家信息（判空，避免查询结果为null导致崩溃）
        currentMerchant = AdminDao.getBusinessUser(currentMerchantAccount);
        if (currentMerchant == null) {
            showToast("商家信息查询失败，请重新登录");
            return;
        }

        // 3. 展示商家账号与基本信息（含字段判空，避免空字符串展示）
        tvMerchantAccount.setText(currentMerchantAccount);
        tvMerchantName.setText(currentMerchant.getsName() != null ? currentMerchant.getsName() : "未知商家");
        tvMerchantDesc.setText("店铺简介:" + (currentMerchant.getsDescribe() != null ? currentMerchant.getsDescribe() : "未填写店铺简介"));

        // 4. 加载并展示商家头像（含容错与压缩，避免OOM和崩溃）
        loadMerchantAvatar(currentMerchant.getsImg());
    }

    /**
     * 加载商家头像（含文件判空、图片压缩、容错处理，避免OOM）
     * @param imgPath 头像文件路径
     */
    private void loadMerchantAvatar(String imgPath) {
        // 判空：路径为null/空或文件不存在，设置默认头像
        if (imgPath == null || imgPath.trim().isEmpty() || !new File(imgPath).exists()) {
            ivMerchantAvatar.setImageResource(R.drawable.upimg);
            return;
        }

        // 图片压缩：避免大图片导致OOM，提升加载性能
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取图片尺寸，不加载到内存
        BitmapFactory.decodeFile(imgPath, options);

        // 计算压缩比例（宽高均不超过500px，可根据需求调整）
        int reqWidth = 500;
        int reqHeight = 500;
        int inSampleSize = 1;
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        // 加载压缩后的图片（指定配置，减少内存占用）
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap avatarBitmap = BitmapFactory.decodeFile(imgPath, options);
        // 判空：图片加载失败则设置默认头像
        ivMerchantAvatar.setImageBitmap(avatarBitmap != null ? avatarBitmap : BitmapFactory.decodeResource(getResources(), R.drawable.upimg));
    }

    /**
     * 初始化所有控件的点击事件（集中管理，封装通用方法，减少冗余）
     */
    private void initViewClickEvents() {
        // 1. 退出账号（修改：直接返回商家登录界面，不删除数据）
        tvExitAccount.setOnClickListener(v -> exitMerchantAccount());

        // 2. 注销账号（修改：弹出密码验证弹窗，验证通过删除数据并返回登录页）
        tvLogout.setOnClickListener(v -> showMerchantLogoutPwdDialog());

        // 3. 修改密码（跳转至商家密码修改页面）
        tvChangePassword.setOnClickListener(v -> jumpToActivity(ManageManUpdatePwdActivity.class));

        // 4. 修改商家信息（跳转至商家信息修改页面）
        tvChangeInfo.setOnClickListener(v -> jumpToActivity(ManageManUpdateMesActivity.class));

        // 5. 未完成订单管理（跳转至未完成订单页面）
        btnUnfinishedOrder.setOnClickListener(v -> jumpToActivity(ManageManOrderNoFinishActivity.class));

        // 6. 评论管理（跳转至评论管理页面）
        btnCommentManage.setOnClickListener(v -> jumpToActivity(ManageManCommentActivity.class));

        // 7. 已完成订单管理（跳转至已完成订单页面）
        btnFinishedOrder.setOnClickListener(v -> jumpToActivity(ManageManOrderFinishActivity.class));
    }

    // ---------------------- 核心功能1：退出账号 - 直接返回商家登录界面 ----------------------
    /**
     * 退出商家账号：关闭当前商家中心，跳转到商家登录界面，不删除数据库数据
     */
    private void exitMerchantAccount() {
        if (getContext() == null) {
            return;
        }
        // 1. 跳转到商家登录界面，清除返回栈（避免用户返回当前页面）
        Intent loginIntent = new Intent(getContext(), MainActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);

        // 2. 关闭当前宿主Activity（ManageManActivity）
        if (hostActivity != null && !hostActivity.isFinishing() && !hostActivity.isDestroyed()) {
            hostActivity.finish();
        }

        // 3. 提示用户
        showToast("已退出商家账号");
    }

    // ---------------------- 核心功能2：注销账号 - 密码验证 + 删除数据 + 返回登录页 ----------------------
    /**
     * 显示商家注销密码验证弹窗
     */
    private void showMerchantLogoutPwdDialog() {
        // 前置容错：商家信息异常直接返回
        if (currentMerchant == null || currentMerchantAccount == null || getContext() == null) {
            showToast("商家信息异常，无法注销");
            return;
        }

        // 1. 加载弹窗布局
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_merchant_logout, null);
        if (dialogView == null) {
            showToast("弹窗加载失败，请重试");
            return;
        }

        // 2. 获取弹窗控件
        EditText etLogoutPwd = dialogView.findViewById(R.id.et_logout_pwd);
        Button btnCancel = dialogView.findViewById(R.id.btn_logout_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_logout_confirm);

        // 3. 构建弹窗（点击外部不关闭，提升安全性）
        AlertDialog logoutDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // 4. 取消按钮 - 关闭弹窗
        btnCancel.setOnClickListener(v -> logoutDialog.dismiss());

        // 5. 确认注销按钮 - 验证密码 + 删除数据 + 跳转登录页
        btnConfirm.setOnClickListener(v -> {
            // 5.1 获取输入密码并判空
            String inputPwd = etLogoutPwd.getText() != null ? etLogoutPwd.getText().toString().trim() : "";
            if (inputPwd.isEmpty()) {
                showToast("请输入商家账号密码");
                return;
            }

            // 5.2 从数据库查询商家密码
            String merchantPwd = AdminDao.getBusinessUserPwd(currentMerchantAccount);
            if (merchantPwd == null) {
                showToast("商家密码查询失败，无法注销");
                logoutDialog.dismiss();
                return;
            }

            // 5.3 验证密码是否一致
            if (!inputPwd.equals(merchantPwd)) {
                showToast("密码输入错误，请重新输入");
                etLogoutPwd.setText(""); // 清空输入框，便于重新输入
                return;
            }

            // 5.4 密码验证通过，删除数据库中的商家数据
            boolean isDeleteSuccess = AdminDao.deleteBusinessUser(currentMerchantAccount);
            if (isDeleteSuccess) {
                showToast("商家账号注销成功");

                // 5.5 跳转到商家登录界面，清除返回栈
                Intent loginIntent = new Intent(getContext(), MainActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);

                // 5.6 关闭弹窗和当前商家中心
                logoutDialog.dismiss();
                if (hostActivity != null && !hostActivity.isFinishing() && !hostActivity.isDestroyed()) {
                    hostActivity.finish();
                }
            } else {
                showToast("商家账号注销失败，请重试");
                logoutDialog.dismiss();
            }
        });

        // 6. 显示弹窗
        logoutDialog.show();
    }

    /**
     * 通用页面跳转方法（封装Intent，减少冗余代码，含上下文容错）
     * @param targetActivity 目标Activity的Class
     */
    private void jumpToActivity(Class<?> targetActivity) {
        if (getContext() == null || targetActivity == null) {
            showToast("页面跳转失败，请重试");
            return;
        }
        Intent intent = new Intent(getContext(), targetActivity);
        startActivity(intent);
    }

    /**
     * 封装Toast提示（含上下文容错，减少冗余代码，提升用户体验）
     * @param msg 提示文本
     */
    private void showToast(String msg) {
        if (getContext() == null || msg == null) {
            return;
        }
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Fragment销毁时释放资源，避免内存泄漏和OOM
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 1. 回收头像Bitmap资源，释放内存
        if (ivMerchantAvatar != null) {
            ivMerchantAvatar.setImageBitmap(null);
        }

        // 2. 清空所有控件引用，解除内存绑定
        rootView = null;
        ivMerchantAvatar = null;
        tvMerchantAccount = null;
        tvMerchantName = null;
        tvMerchantDesc = null;
        tvExitAccount = null;
        tvLogout = null;
        tvChangePassword = null;
        tvChangeInfo = null;
        btnUnfinishedOrder = null;
        btnCommentManage = null;
        btnFinishedOrder = null;

        // 3. 清空全局变量引用，避免内存泄漏
        currentMerchantAccount = null;
        currentMerchant = null;
        hostActivity = null;
    }
}