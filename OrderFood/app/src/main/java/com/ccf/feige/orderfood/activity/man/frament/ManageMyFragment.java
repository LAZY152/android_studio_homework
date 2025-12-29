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
 * 所属模块：商家管理模块（ManageMan）
 * 依赖组件：
 *  1. 布局文件：fragment_manage_my.xml（商家个人中心布局）
 *  2. 工具类：Tools（获取登录账号、通用工具方法）
 *  3. 数据访问层：AdminDao（商家信息查询、删除、密码获取）
 *  4. 实体类：UserBean（存储商家/用户核心信息）
 *  5. 关联Activity：ManageManActivity（宿主Activity）、各类商家管理/修改Activity
 *  注意事项：Fragment依附于ManageManActivity，销毁时需手动释放资源避免内存泄漏
 */
public class ManageMyFragment extends Fragment {
    // 根视图：存储Fragment加载的布局根节点，避免重复inflater
    private View rootView;
    // 商家信息相关控件
    private ImageView ivMerchantAvatar; // 商家头像展示控件
    private TextView tvMerchantAccount; // 商家账号展示控件
    private TextView tvMerchantName; // 商家/店铺名称展示控件
    private TextView tvMerchantDesc; // 店铺简介展示控件
    // 功能入口相关控件
    private TextView tvExitAccount; // 退出账号（原退出系统）：仅退出登录，不删除账号数据
    private TextView tvLogout; // 注销账号（需验证密码）：验证通过后删除账号数据，永久注销
    private TextView tvChangePassword; // 修改密码入口：跳转至密码修改页面
    private TextView tvChangeInfo; // 修改商家信息入口：跳转至店铺信息修改页面
    private Button btnUnfinishedOrder; // 未完成订单管理入口：跳转至未完成订单列表
    private Button btnCommentManage; // 评论管理入口：跳转至用户评论列表
    private Button btnFinishedOrder; // 已完成订单管理入口：跳转至已完成订单列表

    // 全局变量：当前登录商家账号、商家信息（避免重复查询，便于后续使用）
    private String currentMerchantAccount; // 当前登录商家的账号（从Tools工具类获取）
    private UserBean currentMerchant; // 当前登录商家的完整信息（从AdminDao查询）
    // 全局变量：宿主Activity（避免重复强转，提升容错性）
    private ManageManActivity hostActivity; // Fragment的宿主Activity（ManageManActivity），用于后续关闭宿主

    /**
     * Fragment生命周期方法：创建视图
     * 执行时机：Fragment首次创建界面时调用
     * 核心职责：加载布局、初始化控件、加载商家信息、绑定点击事件
     * @param inflater 布局填充器，用于加载xml布局为View对象
     * @param container 父容器视图，用于承载Fragment的布局（非Fragment所属，仅作为容器）
     * @param savedInstanceState 保存状态的Bundle，用于恢复Fragment之前的状态（如屏幕旋转）
     * @return View Fragment的根视图（rootView）
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加载布局，初始化根视图：将fragment_manage_my.xml布局填充为View，并赋值给rootView
        rootView = inflater.inflate(R.layout.fragment_manage_my, container, false);

        // 1. 初始化所有控件（集中管理，避免分散findViewById）：统一绑定布局控件与Java对象
        initViews();

        // 2. 加载并展示商家信息（含容错处理）：从本地获取账号、从数据库查询信息、展示到界面
        loadMerchantInfo();

        // 3. 初始化所有控件的点击事件（集中管理，逻辑清晰）：为所有功能控件绑定点击监听
        initViewClickEvents();

        // 返回Fragment的根视图，供宿主Activity加载展示
        return rootView;
    }

    /**
     * 初始化所有视图控件，规范命名，便于维护
     * 核心职责：
     *  1. 绑定所有布局控件（findViewById）
     *  2. 初始化宿主Activity（含强转容错，避免崩溃）
     *  注意：所有控件均从rootView获取，避免直接从Activity获取导致的空指针
     */
    private void initViews() {
        // 商家信息控件：绑定布局中的商家信息展示控件
        ivMerchantAvatar = rootView.findViewById(R.id.man_manage_my_tx);
        tvMerchantAccount = rootView.findViewById(R.id.man_manage_my_account);
        tvMerchantName = rootView.findViewById(R.id.man_manage_my_name);
        tvMerchantDesc = rootView.findViewById(R.id.man_manage_my_des);

        // 功能入口控件：绑定布局中的所有功能入口控件（按钮/文本）
        tvExitAccount = rootView.findViewById(R.id.man_manage_my_exit); // 退出账号
        tvLogout = rootView.findViewById(R.id.man_manage_my_zx); // 注销账号
        tvChangePassword = rootView.findViewById(R.id.man_manage_my_changePwd);
        tvChangeInfo = rootView.findViewById(R.id.man_manage_my_changeMes);
        btnUnfinishedOrder = rootView.findViewById(R.id.man_manage_my_orderManage);
        btnCommentManage = rootView.findViewById(R.id.man_manage_my_commentMan);
        btnFinishedOrder = rootView.findViewById(R.id.man_manage_my_finish);

        // 初始化宿主Activity（含容错，避免强转崩溃）：
        // 判断当前Fragment的宿主Activity是否为ManageManActivity，是则强转，否则赋值为null
        hostActivity = (getActivity() instanceof ManageManActivity) ? (ManageManActivity) getActivity() : null;
    }

    /**
     * 加载商家信息（含全链路容错，避免空指针崩溃）
     * 执行流程（全链路容错，每一步均判空）：
     *  1. 获取当前登录商家账号（从Tools工具类获取本地存储的登录账号）
     *  2. 查询商家完整信息（从AdminDao数据库访问层查询账号对应的商家信息）
     *  3. 展示商家基本信息（账号、店铺名称、店铺简介）
     *  4. 加载并展示商家头像（单独封装方法，含图片压缩与容错）
     */
    private void loadMerchantInfo() {
        // 1. 获取当前登录商家账号（判空，避免无效查询）：从Tools工具类获取本地存储的登录账号
        currentMerchantAccount = Tools.getOnAccount(getContext());
        if (currentMerchantAccount == null || currentMerchantAccount.trim().isEmpty()) {
            showToast("未获取到登录商家账号，请重新登录");
            return;
        }

        // 2. 查询商家信息（判空，避免查询结果为null导致崩溃）：从AdminDao查询该账号对应的商家完整信息
        currentMerchant = AdminDao.getBusinessUser(currentMerchantAccount);
        if (currentMerchant == null) {
            showToast("商家信息查询失败，请重新登录");
            return;
        }

        // 3. 展示商家账号与基本信息（含字段判空，避免空字符串展示）：将查询到的信息设置到对应控件
        tvMerchantAccount.setText(currentMerchantAccount); // 展示商家账号（无需判空，已提前校验）
        // 展示店铺名称：若UserBean中的店铺名称不为null则展示，否则展示"未知商家"
        tvMerchantName.setText(currentMerchant.getsName() != null ? currentMerchant.getsName() : "未知商家");
        // 展示店铺简介：拼接前缀+店铺简介，若简介为null则展示"未填写店铺简介"
        tvMerchantDesc.setText("店铺简介:" + (currentMerchant.getsDescribe() != null ? currentMerchant.getsDescribe() : "未填写店铺简介"));

        // 4. 加载并展示商家头像（含容错与压缩，避免OOM和崩溃）：传入头像路径，调用专用方法加载
        loadMerchantAvatar(currentMerchant.getsImg());
    }

    /**
     * 加载商家头像（含文件判空、图片压缩、容错处理，避免OOM）
     * 核心优化点：
     *  1. 路径与文件容错：避免无效路径导致的文件读取异常
     *  2. 图片压缩：采用采样率压缩（inSampleSize），避免大图片加载导致的内存溢出（OOM）
     *  3. 默认头像兜底：图片加载失败时展示默认头像（R.drawable.upimg）
     * @param imgPath 头像文件路径（从UserBean中获取，对应本地存储的头像文件路径）
     */
    private void loadMerchantAvatar(String imgPath) {
        // 判空：路径为null/空或文件不存在，设置默认头像
        if (imgPath == null || imgPath.trim().isEmpty() || !new File(imgPath).exists()) {
            ivMerchantAvatar.setImageResource(R.drawable.upimg);
            return;
        }

        // 图片压缩：避免大图片导致OOM，提升加载性能
        // 第一步：创建BitmapFactory.Options，设置inJustDecodeBounds=true，仅获取图片尺寸不加载到内存
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 不加载图片到内存，仅获取图片宽高信息
        BitmapFactory.decodeFile(imgPath, options); // 解析图片文件，获取宽高信息存入options

        // 计算压缩比例（宽高均不超过500px，可根据需求调整）：采用2的幂次倍压缩，保证图片不失真
        int reqWidth = 500; // 目标图片宽度（px）
        int reqHeight = 500; // 目标图片高度（px）
        int inSampleSize = 1; // 初始压缩比例（1=不压缩）
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            // 循环计算最大压缩比例，保证压缩后的图片宽高均不小于目标宽高
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        // 第二步：加载压缩后的图片（指定配置，减少内存占用）
        options.inJustDecodeBounds = false; // 关闭仅获取尺寸模式，开始加载图片到内存
        options.inSampleSize = inSampleSize; // 设置计算得到的压缩比例
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // 设置图片像素格式，平衡画质与内存占用

        // 解析压缩后的图片文件，得到Bitmap对象
        Bitmap avatarBitmap = BitmapFactory.decodeFile(imgPath, options);
        // 判空：图片加载失败则设置默认头像，否则设置压缩后的头像
        ivMerchantAvatar.setImageBitmap(avatarBitmap != null ? avatarBitmap : BitmapFactory.decodeResource(getResources(), R.drawable.upimg));
    }

    /**
     * 初始化所有控件的点击事件（集中管理，封装通用方法，减少冗余）
     * 核心职责：为所有功能入口控件绑定点击监听，统一管理跳转/业务逻辑，提升代码可读性
     * 注意：所有业务逻辑均封装为独立方法，避免点击监听内部代码冗余
     */
    private void initViewClickEvents() {
        // 1. 退出账号（直接返回商家登录界面，不删除数据）
        tvExitAccount.setOnClickListener(v -> exitMerchantAccount());

        // 2. 注销账号（弹出密码验证弹窗，验证通过删除数据并返回登录页）
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
     * 核心流程：
     *  1. 构建登录界面Intent，设置Flags清除返回栈（避免用户返回已退出的页面）
     *  2. 启动登录界面（MainActivity）
     *  3. 关闭当前宿主Activity（ManageManActivity）
     *  4. 展示退出成功提示
     *  注意：仅退出登录状态，商家账号数据仍保留在数据库中，可再次登录
     */
    private void exitMerchantAccount() {
        // 上下文容错：避免getContext()为null导致的Intent创建失败
        if (getContext() == null) {
            return;
        }
        // 1. 跳转到商家登录界面，清除返回栈（避免用户返回当前页面）
        Intent loginIntent = new Intent(getContext(), MainActivity.class);
        // 设置Intent Flags：清除当前任务栈顶部所有Activity，创建新任务栈，避免返回原商家中心
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
     * 核心流程：
     *  1. 前置容错校验（商家信息、上下文是否有效）
     *  2. 加载注销弹窗布局（dialog_merchant_logout.xml）
     *  3. 构建AlertDialog（点击外部不关闭，提升安全性）
     *  4. 绑定弹窗按钮点击事件（取消/确认注销）
     *  5. 展示弹窗
     *  注意：注销前必须验证商家密码，防止误操作导致账号丢失
     */
    private void showMerchantLogoutPwdDialog() {
        // 前置容错：商家信息异常直接返回
        if (currentMerchant == null || currentMerchantAccount == null || getContext() == null) {
            showToast("商家信息异常，无法注销");
            return;
        }

        // 1. 加载弹窗布局：从dialog_merchant_logout.xml加载弹窗视图
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_merchant_logout, null);
        if (dialogView == null) {
            showToast("弹窗加载失败，请重试");
            return;
        }

        // 2. 获取弹窗控件：绑定弹窗布局中的输入框与按钮
        EditText etLogoutPwd = dialogView.findViewById(R.id.et_logout_pwd); // 密码输入框（用于输入商家密码）
        Button btnCancel = dialogView.findViewById(R.id.btn_logout_cancel); // 取消按钮（关闭弹窗）
        Button btnConfirm = dialogView.findViewById(R.id.btn_logout_confirm); // 确认注销按钮（执行注销逻辑）

        // 3. 构建弹窗（点击外部不关闭，提升安全性）：使用AlertDialog.Builder构建弹窗
        AlertDialog logoutDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView) // 设置弹窗视图
                .setCancelable(false) // 点击弹窗外部不关闭，必须通过按钮操作
                .create();

        // 4. 取消按钮 - 关闭弹窗：不执行任何业务逻辑，仅关闭弹窗
        btnCancel.setOnClickListener(v -> logoutDialog.dismiss());

        // 5. 确认注销按钮 - 验证密码 + 删除数据 + 跳转登录页
        btnConfirm.setOnClickListener(v -> {
            // 5.1 获取输入密码并判空：获取输入框中的密码，去除前后空格，判空校验
            String inputPwd = etLogoutPwd.getText() != null ? etLogoutPwd.getText().toString().trim() : "";
            if (inputPwd.isEmpty()) {
                showToast("请输入商家账号密码");
                return;
            }

            // 5.2 从数据库查询商家密码：从AdminDao获取该账号对应的正确密码
            String merchantPwd = AdminDao.getBusinessUserPwd(currentMerchantAccount);
            if (merchantPwd == null) {
                showToast("商家密码查询失败，无法注销");
                logoutDialog.dismiss();
                return;
            }

            // 5.3 验证密码是否一致：对比输入密码与数据库中的正确密码
            if (!inputPwd.equals(merchantPwd)) {
                showToast("密码输入错误，请重新输入");
                etLogoutPwd.setText(""); // 清空输入框，便于重新输入
                return;
            }

            // 5.4 密码验证通过，删除数据库中的商家数据：调用AdminDao删除该商家账号的所有数据
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

        // 6. 显示弹窗：将构建好的弹窗展示给用户
        logoutDialog.show();
    }

    /**
     * 通用页面跳转方法（封装Intent，减少冗余代码，含上下文容错）
     * 核心职责：统一处理Activity跳转，封装Intent创建与启动逻辑，减少重复代码
     * 容错处理：上下文与目标Activity判空，避免跳转失败导致的崩溃
     * @param targetActivity 目标Activity的Class（如ManageManUpdatePwdActivity.class）
     */
    private void jumpToActivity(Class<?> targetActivity) {
        // 容错校验：上下文或目标Activity为null时，提示跳转失败
        if (getContext() == null || targetActivity == null) {
            showToast("页面跳转失败，请重试");
            return;
        }
        // 构建Intent，启动目标Activity
        Intent intent = new Intent(getContext(), targetActivity);
        startActivity(intent);
    }

    /**
     * 封装Toast提示（含上下文容错，减少冗余代码，提升用户体验）
     * 核心职责：统一处理Toast提示，封装Toast创建与显示逻辑，减少重复代码
     * 容错处理：上下文与提示信息判空，避免Toast显示失败导致的崩溃
     * @param msg 提示文本（要展示给用户的提示信息）
     */
    private void showToast(String msg) {
        // 容错校验：上下文或提示信息为null时，不执行Toast显示
        if (getContext() == null || msg == null) {
            return;
        }
        // 显示短时长Toast提示
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Fragment生命周期方法：销毁视图
     * 执行时机：Fragment的视图被销毁时调用（如Fragment被隐藏、移除）
     * 核心职责：释放所有资源，避免内存泄漏和OOM
     *  1. 回收Bitmap资源（头像），释放内存
     *  2. 清空所有控件引用，解除与布局的绑定
     *  3. 清空全局变量引用，避免持有宿主Activity导致的内存泄漏
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 1. 回收头像Bitmap资源，释放内存：将ImageView的Bitmap置为null，触发GC回收
        if (ivMerchantAvatar != null) {
            ivMerchantAvatar.setImageBitmap(null);
        }

        // 2. 清空所有控件引用，解除内存绑定：将所有控件对象置为null，避免持有布局资源
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

        // 3. 清空全局变量引用，避免内存泄漏：将全局变量置为null，避免持有宿主Activity/商家信息
        currentMerchantAccount = null;
        currentMerchant = null;
        hostActivity = null;
    }
}