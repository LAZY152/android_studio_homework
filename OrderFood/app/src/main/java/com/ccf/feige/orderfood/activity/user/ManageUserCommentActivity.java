package com.ccf.feige.orderfood.activity.user;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccf.feige.orderfood.R;
import com.ccf.feige.orderfood.activity.user.listen.StartListen;
import com.ccf.feige.orderfood.dao.CommentDao;
import com.ccf.feige.orderfood.dao.OrderDao;
import com.ccf.feige.orderfood.until.FileImgUntil;
import com.ccf.feige.orderfood.until.Tools;

/**
 * 订单评论页面（修复数据缺失+评论成功更新订单状态4）
 * 功能说明：用户针对已完成订单进行评分、输入评论内容、上传图片（拍照/相册选择），提交后插入评论数据并更新订单为已评论状态
 */
public class ManageUserCommentActivity extends AppCompatActivity {
    // 控件成员变量 - 页面所有交互与展示控件的引用
    private Toolbar toolbar; // 顶部导航栏（包含返回按钮）
    private EditText etCommentContent; // 评论内容输入框
    private ImageView ivStar1, ivStar2, ivStar3, ivStar4, ivStar5; // 5颗评分星星（1星~5星）
    private TextView tvScoreDesc; // 评分对应的文字描述（如“满意”“非常满意”）
    private ImageView ivCommentImg; // 评论图片展示控件（显示拍照/相册选择的图片）
    private Button btnTakePhoto; // 拍照按钮（触发相机拍摄评论图片）
    private Button btnPickAlbum; // 相册选择按钮（从手机相册选取评论图片）
    private Button btnSubmitComment; // 提交评论按钮（最终提交评论数据并更新订单状态）

    // 业务参数成员变量（核心：确保非空，为评论提交和订单更新提供必要数据支撑）
    private String account; // 当前登录用户账号（用于关联评论所属用户）
    private String orderId; // 订单ID（必传，用于定位需要更新状态的订单）
    private String businessId; // 商家ID（必传，用于关联评论所属商家）

    // 拍照/相册回调 - 用于接收相机/相册返回的结果（AndroidX推荐的ActivityResultLauncher，替代旧版startActivityForResult）
    private ActivityResultLauncher<Intent> cameraLauncher; // 相机拍照结果回调器
    private ActivityResultLauncher<Intent> galleryLauncher; // 相册选择结果回调器

    // 评分描述常量（与原有逻辑一致，索引对应1~5星，分别对应不同的满意度描述）
    private static final String[] SCORE_DESC_ARRAY = {"非常差", "差", "一般", "满意", "非常满意"};

    /**
     * 页面生命周期创建方法 - 页面初始化入口，执行各项初始化操作
     * @param savedInstanceState 页面状态保存对象（用于页面重建时恢复数据，此处未使用）
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载页面布局文件，将XML布局与当前Activity绑定
        setContentView(R.layout.activity_manage_user_comment);

        // 1. 初始化控件：通过布局ID获取所有控件引用，为后续事件绑定和数据操作做准备
        initView();

        // 2. 初始化Toolbar返回事件：设置导航栏返回按钮的点击事件，实现页面返回
        initToolbar();

        // 3. 初始化拍照/相册回调：注册相机和相册的结果回调，接收并处理返回的图片数据
        initActivityResultLauncher();

        // 4. 初始化控件事件：为所有交互控件绑定点击事件，实现用户操作响应
        initViewListener();

        // 5. 解析并校验参数（核心：解决“数据缺失无法评论”问题）：获取跳转参数和用户登录信息，校验关键数据有效性
        parseAndCheckParams();
    }

    /**
     * 初始化控件（与用户原有布局id保持一致）
     * 功能：通过findViewById方法获取XML布局中所有控件的实例，赋值给对应的成员变量
     * 说明：确保控件ID与布局文件中的定义完全匹配，避免空指针异常
     */
    private void initView() {
        toolbar = findViewById(R.id.user_manage_comment_bar);
        etCommentContent = findViewById(R.id.user_manage_comment_text);
        ivStar1 = findViewById(R.id.user_comment_one);
        ivStar2 = findViewById(R.id.user_comment_two);
        ivStar3 = findViewById(R.id.user_comment_three);
        ivStar4 = findViewById(R.id.user_comment_four);
        ivStar5 = findViewById(R.id.user_comment_five);
        tvScoreDesc = findViewById(R.id.user_comment_con);
        ivCommentImg = findViewById(R.id.user_manage_comment_img);
        btnTakePhoto = findViewById(R.id.user_manage_comment_pz);
        btnPickAlbum = findViewById(R.id.user_manage_comment_xc);
        btnSubmitComment = findViewById(R.id.user_manage_comment_fbpl);
    }

    /**
     * 初始化Toolbar返回事件
     * 功能：为Toolbar的导航返回按钮绑定点击事件，点击时触发页面返回操作
     * 说明：onBackPressed()为Activity默认的返回方法，会销毁当前页面并返回上一个页面
     */
    private void initToolbar() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * 初始化拍照/相册回调（拆分回调，避免逻辑混淆）
     * 功能：注册两个ActivityResultLauncher，分别处理相机和相册的返回结果，解耦两种操作的逻辑
     * 说明：使用ActivityResultContracts.StartActivityForResult()契约，接收目标Activity的返回结果
     */
    private void initActivityResultLauncher() {
        // 拍照回调：处理相机拍摄完成后的返回结果
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // 判断结果是否有效：返回码为RESULT_OK（表示操作成功）且返回数据不为空
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                // 获取相机返回的额外数据（拍照的位图数据存储在extras中）
                Bundle extras = result.getData().getExtras();
                // 校验extras中是否包含位图数据（key为"data"），避免空指针异常
                if (extras != null && extras.containsKey("data")) {
                    // 从extras中获取位图对象
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    // 将位图设置到图片展示控件中，显示拍摄的照片
                    ivCommentImg.setImageBitmap(bitmap);
                }
            }
        });

        // 相册回调：处理相册选择图片后的返回结果
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // 判断结果是否有效：返回码为RESULT_OK（表示操作成功）且返回数据不为空
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                // 获取相册返回的选中图片的Uri（统一资源标识符，用于定位手机中的图片文件）
                Uri selectImgUri = result.getData().getData();
                // 将Uri设置到图片展示控件中，显示选中的相册图片
                ivCommentImg.setImageURI(selectImgUri);
            }
        });
    }

    /**
     * 初始化控件事件（保留原有逻辑）
     * 功能：为所有交互控件绑定点击事件监听器，实现用户操作的响应逻辑
     * 说明：保留原有星星评分的监听器，拆分其他按钮的点击事件，逻辑更清晰
     */
    private void initViewListener() {
        // 评分星星点击事件（保留原有监听器）：使用自定义StartListen监听器处理星星评分逻辑
        ivStar1.setOnClickListener(new StartListen(this));
        ivStar2.setOnClickListener(new StartListen(this));
        ivStar3.setOnClickListener(new StartListen(this));
        ivStar4.setOnClickListener(new StartListen(this));
        ivStar5.setOnClickListener(new StartListen(this));

        // 拍照按钮事件：点击时调用paiZhao()方法，触发相机拍摄流程
        btnTakePhoto.setOnClickListener(v -> paiZhao());

        // 相册按钮事件：点击时调用xiangCe()方法，触发相册选择流程
        btnPickAlbum.setOnClickListener(v -> xiangCe());

        // 提交评论按钮事件（核心：修复数据缺失+更新订单状态4）：点击时调用submitComment()方法，提交评论并更新订单
        btnSubmitComment.setOnClickListener(v -> submitComment());
    }

    /**
     * 解析并校验跳转参数（核心：解决“数据缺失无法评论”）
     * 功能：
     *  1. 获取当前登录用户账号；
     *  2. 解析上一个页面跳转传递的订单ID和商家ID；
     *  3. 校验所有关键参数的非空有效性；
     *  4. 参数无效时禁用提交按钮，避免无效操作。
     * 说明：关键参数缺失会导致评论无法提交和订单无法更新，因此必须提前校验并给出提示
     */
    private void parseAndCheckParams() {
        // 获取当前登录用户账号：通过Tools工具类从本地获取已登录用户的账号信息
        account = Tools.getOnAccount(this);

        // 解析跳转参数：获取上一个页面通过Intent传递的额外数据
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra("orderId"); // 获取订单ID参数
            businessId = intent.getStringExtra("businessId"); // 获取商家ID参数
        }

        // 校验关键参数（非空判断）：初始化参数有效标记为true，逐个校验参数
        boolean isParamValid = true;
        if (account == null || account.trim().isEmpty()) {
            Toast.makeText(this, "用户未登录，无法评论", Toast.LENGTH_SHORT).show();
            isParamValid = false;
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            Toast.makeText(this, "订单ID缺失，无法评论", Toast.LENGTH_SHORT).show();
            isParamValid = false;
        }
        if (businessId == null || businessId.trim().isEmpty()) {
            Toast.makeText(this, "商家信息缺失，无法评论", Toast.LENGTH_SHORT).show();
            isParamValid = false;
        }

        // 参数无效时，禁用提交按钮：避免用户点击无效的提交按钮，减少异常发生
        btnSubmitComment.setEnabled(isParamValid);
    }

    /**
     * 提交评论（核心：插入评论+更新订单状态4）
     * 功能流程：
     *  1. 校验评论内容和评分的有效性；
     *  2. 转换评分为数字格式；
     *  3. 处理评论图片并保存到本地；
     *  4. 插入评论数据到数据库；
     *  5. 评论成功后更新订单状态为4（完成且被评论）；
     *  6. 根据操作结果给出用户提示并关闭页面。
     * 说明：订单状态4为业务约定的“已评论”状态，确保订单不会被重复评论
     */
    private void submitComment() {
        // 1. 获取并校验评论内容：获取输入框中的文本并去除首尾空格，判断是否为空
        String commentContent = etCommentContent.getText().toString().trim();
        if (commentContent.isEmpty()) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return; // 内容为空，直接返回，不执行后续操作
        }

        // 2. 获取并校验评分：获取评分描述文本并去除首尾空格，判断是否为空
        String scoreDesc = tvScoreDesc.getText() == null ? "" : tvScoreDesc.getText().toString().trim();
        if (scoreDesc.isEmpty()) {
            Toast.makeText(this, "请选择评分", Toast.LENGTH_SHORT).show();
            return; // 评分未选择，直接返回，不执行后续操作
        }

        // 3. 转换评分为数字（1-5分）：通过评分描述匹配常量数组，获取对应的数字评分（数组索引+1）
        int score = 1; // 默认1星评分
        for (int i = 0; i < SCORE_DESC_ARRAY.length; i++) {
            if (SCORE_DESC_ARRAY[i].equals(scoreDesc)) {
                score = i + 1; // 数组索引0对应1星，索引4对应5星，与业务逻辑一致
                break; // 匹配成功后跳出循环，提高效率
            }
        }

        // 4. 处理评论图片：判断图片控件是否有图片，有则转换为位图并保存到本地，获取图片路径
        String imgPath = ""; // 图片路径默认为空（无图片时提交空路径）
        Drawable drawable = ivCommentImg.getDrawable();
        // 判断Drawable是否为BitmapDrawable（拍照/相册选择的图片均为该类型）
        if (drawable != null && drawable instanceof BitmapDrawable) {
            // 从BitmapDrawable中获取位图对象
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            // 获取图片保存的文件名（通过FileImgUntil工具类生成）
            imgPath = FileImgUntil.getImgName();
            // 将位图保存到指定路径（通过FileImgUntil工具类完成文件写入）
            FileImgUntil.saveSystemImgToPath(bitmap, imgPath);
        }

        // 5. 插入评论到数据库：调用CommentDao的插入方法，将评论数据存入数据库，获取插入结果
        // 插入参数：用户账号、商家ID、评论内容、数字评分（转字符串）、图片路径
        int insertResult = CommentDao.insertComment(account, businessId, commentContent, String.valueOf(score), imgPath);

        // 6. 评论成功后，更新订单状态为4（完成且被评论）：判断评论插入成功，再执行订单状态更新
        boolean updateOrderSuccess = false;
        if (insertResult == 1) { // 插入结果为1表示数据库插入成功（DAO层约定的成功标识）
            // 调用OrderDao的更新方法，将指定订单的状态更新为4（已评论）
            updateOrderSuccess = OrderDao.updateOrderStatusToCommented(orderId) == 1;
        }

        // 7. 结果提示与页面关闭：根据评论插入和订单更新的结果，给出对应的用户提示并处理页面
        if (insertResult == 1 && updateOrderSuccess) {
            Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
            finish(); // 关闭当前评论页面，返回上一个订单列表页面
        } else {
            Toast.makeText(this, "评论失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 拍照功能
     * 功能：创建相机Intent，启动相机应用进行拍照，拍摄结果由cameraLauncher回调接收
     * 说明：通过resolveActivity判断是否有可用的相机应用，避免无相机时的异常
     */
    private void paiZhao() {
        // 创建相机Intent，指定动作为拍照（MediaStore.ACTION_IMAGE_CAPTURE）
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断设备是否有可用的相机应用，避免Intent跳转失败
        if (picIntent.resolveActivity(getPackageManager()) != null) {
            // 启动相机应用，通过cameraLauncher接收返回结果
            cameraLauncher.launch(picIntent);
        } else {
            // 无可用相机应用，给出用户提示
            Toast.makeText(this, "未找到相机应用", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 相册选择功能
     * 功能：创建相册Intent，启动相册应用选择图片，选择结果由galleryLauncher回调接收
     * 说明：通过resolveActivity判断是否有可用的相册应用，避免无相册时的异常
     */
    private void xiangCe() {
        // 创建相册Intent，指定动作为选择图片，数据路径为手机外部存储的图片资源
        Intent picIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 判断设备是否有可用的相册应用，避免Intent跳转失败
        if (picIntent.resolveActivity(getPackageManager()) != null) {
            // 启动相册应用，通过galleryLauncher接收返回结果
            galleryLauncher.launch(picIntent);
        } else {
            // 无可用相册应用，给出用户提示
            Toast.makeText(this, "未找到相册应用", Toast.LENGTH_SHORT).show();
        }
    }
}