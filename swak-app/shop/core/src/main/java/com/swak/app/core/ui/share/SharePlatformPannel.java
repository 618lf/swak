/*
 *     Android基础开发个人积累、沉淀、封装、整理共通
 *     Copyright (c) 2016. 曾繁添 <zftlive@163.com>
 *     Github：https://github.com/zengfantian || http://git.oschina.net/zftlive
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.swak.app.core.ui.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swak.app.core.R;

import java.util.ArrayList;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 自定义分享面板
 *
 * @author 曾繁添
 * @version  1.0
 */
public class SharePlatformPannel extends Dialog {

    static final String PLATFORM = "platform";
    static final String LABEL = "label";
    static final String ICON = "icon";
    GridView shareGrid;
    ShareGridAdapter mGridAdapter;
    Activity mActivity;
    SharePlatformActionListener mSharePlatClickListener;
    ArrayList<String> platformList = new ArrayList<String>();
    String linkURL = "";
    String shareContent = "";
    ShareSDKHelper mShareSDKHelper;

    /**
     * 日志输出标志
     */
    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 分享数据
     */
    Platform.ShareParams mShareParams;

    public SharePlatformPannel(Activity context, ShareSDKHelper mShareSDKHelper) {
        super(context, R.style.Share_DialogTopButtomAnimation);
        this.mActivity = context;
        this.mShareSDKHelper = mShareSDKHelper;
        mShareParams = new Platform.ShareParams();
        init();
        initShareData(null, "", "", "", "");
    }

    public SharePlatformPannel(Activity mContext, ShareSDKHelper mShareSDKHelper, String imageURL, String title, String strContent, String linkURL, ArrayList<String> sharePlatform) {
        super(mContext, R.style.Share_DialogTopButtomAnimation);
        this.mActivity = mContext;
        this.mShareSDKHelper = mShareSDKHelper;
        this.linkURL = linkURL;
        this.shareContent = strContent;
        mShareParams = new Platform.ShareParams();
        init();
        initSharePlatform(sharePlatform);
        initShareData(null, imageURL, title, strContent, linkURL);

    }

    public SharePlatformPannel(Activity mContext, ShareSDKHelper mShareSDKHelper, int imageResId, String title, String strContent, String linkURL, ArrayList<String> sharePlatform) {
        super(mContext, R.style.Share_DialogTopButtomAnimation);
        this.mActivity = mContext;
        this.mShareSDKHelper = mShareSDKHelper;
        this.linkURL = linkURL;
        this.shareContent = strContent;
        mShareParams = new Platform.ShareParams();
        Bitmap imageData = BitmapFactory.decodeResource(getContext().getResources(), imageResId);
        mShareParams.setImageData(imageData);
        init();
        initSharePlatform(sharePlatform);
        initShareData(imageData, null, title, strContent, linkURL);
    }

    /**
     * 初始化分享数据
     * @param imageURL
     * @param title
     * @param strContent
     */
    public void initShareData(Bitmap imageData, String imageURL, String title, String strContent, String linkURL){
        this.linkURL = linkURL;
        this.shareContent = strContent;
        mShareParams.setShareType(Platform.SHARE_WEBPAGE);
        mShareParams.setTitle(title);
        if(!TextUtils.isEmpty(imageURL)){
            mShareParams.setImageUrl(imageURL);
        }
        if(null != imageData){
            mShareParams.setImageData(imageData);
        }
        mShareParams.setText(strContent);
        mShareParams.setTitleUrl(linkURL);
        //QQ空间需要SiteUrl
        mShareParams.setSiteUrl(linkURL);
        //微信分享网页、视频需要
        mShareParams.setUrl(linkURL);
    }

    /**
     * 添加分享平台
     * @param platform 分享平台 Wechat.NAME/WechatMoments.NAME/SinaWeibo.NAME/ShortMessage.NAME/QQ.NAME/QZone.NAME
     */
    public void addSharePlatform(String platform){
        if(platformList.contains(platform)){
            return;
        }
        platformList.add(platform);
    }

    /**
     * 设置平台点击事件
     * @param mClickListener
     */
    public void setPlatformClickListener(SharePlatformActionListener mClickListener){
        this.mSharePlatClickListener = mClickListener;
    }

    /**
     * 初始化
     */
    private void init(){
        setContentView(R.layout.anl_common_sharesdk_grid_dialog);
        if(null == mShareSDKHelper){
            mShareSDKHelper = ShareSDKHelper.getInstance();
        }

        // 设置窗体显示的位置和宽度
        getWindow().setGravity(Gravity.BOTTOM);
        // 是否撑满屏幕宽度
        WindowManager.LayoutParams windowparams = getWindow().getAttributes();
        windowparams.width = mActivity.getResources().getDisplayMetrics().widthPixels;
        getWindow().setAttributes(windowparams);
        // 点击其他区域是否关闭窗体
        setCanceledOnTouchOutside(true);

        //分享面板
        shareGrid = (GridView)findViewById(R.id.share_gridView);
        mGridAdapter = new ShareGridAdapter(mActivity);
        shareGrid.setAdapter(mGridAdapter);
        refreshSharePannel();

        //设置点击监听事件
        shareGrid.setOnItemClickListener(new GridOnItemClickListener());
    }

    /**
     * 刷新分享面板
     */
    private void refreshSharePannel(){
        mGridAdapter.clear();

        if(!platformList.isEmpty()){
            if(platformList.size() % 2 == 0){
                shareGrid.setNumColumns(2);
            }

            if(platformList.size() % 3 == 0){
                shareGrid.setNumColumns(3);
            }

            if(platformList.size() % 4 == 0){
                shareGrid.setNumColumns(4);
            }

            if(platformList.size() % 5 == 0){
                shareGrid.setNumColumns(5);
            }

            if(platformList.size() % 6 == 0){
                shareGrid.setNumColumns(3);
            }
        }
        //装填分享数据源
        Platform[] platforms = ShareSDK.getPlatformList();
        if(null == platforms){
            return;
        }
        for (int i = 0;i < platforms.length;i++ ){
            Platform mPlatform = platforms[i];
            String platform = mPlatform.getName();
            //判断是否在指定的范围之内
            if(!platformList.contains(platform)){
                continue;
            }

            PlatformItem item = new PlatformItem();
            item.platform = mPlatform;
            if(SinaWeibo.NAME.equals(platform)){
                item.icon = R.drawable.ssdk_oks_classic_sinaweibo;
                item.label = mActivity.getResources().getString(R.string.ssdk_sinaweibo);
            }else if(Wechat.NAME.equals(platform)){
                item.icon = R.drawable.ssdk_oks_classic_wechat;
                item.label = mActivity.getResources().getString(R.string.ssdk_wechat);
            }else if(WechatMoments.NAME.equals(platform)){
                item.icon = R.drawable.ssdk_oks_classic_wechatmoments;
                item.label = mActivity.getResources().getString(R.string.ssdk_wechatmoments);
            }else if(ShortMessage.NAME.equals(platform)){
                item.icon = R.drawable.ssdk_oks_classic_shortmessage;
                item.label = mActivity.getResources().getString(R.string.ssdk_shortmessage);
            }else if(QQ.NAME.equals(platform)){
                item.icon = R.drawable.ssdk_oks_classic_qq;
                item.label = mActivity.getResources().getString(R.string.ssdk_qq);
            }else if(QZone.NAME.equals(platform)){
                item.icon = R.drawable.ssdk_oks_classic_qzone;
                item.label = mActivity.getResources().getString(R.string.ssdk_qzone);
            }
            mGridAdapter.addItem(item);
        }
        mGridAdapter.notifyDataSetChanged();
    }

    /**
     * 加工分享平台
     * @param sharePlatform 分享平台集合
     */
    public void initSharePlatform(ArrayList<String> sharePlatform){
        try{
            if(null != sharePlatform){
                platformList.clear();
            }

            if(null == sharePlatform || sharePlatform.isEmpty()){
                addSharePlatform(Wechat.NAME);
                addSharePlatform(WechatMoments.NAME);
                addSharePlatform(SinaWeibo.NAME);
                addSharePlatform(ShortMessage.NAME);
                addSharePlatform(QQ.NAME);
                addSharePlatform(QZone.NAME);
            }else{
                for (String platform: sharePlatform) {
                    if("0".equals(platform)){
                        //0-微信朋友圈
                        addSharePlatform(WechatMoments.NAME);
                    }else if("1".equals(platform)){
                        //1-微信好友
                        addSharePlatform(Wechat.NAME);
                    }else if("2".equals(platform)){
                        //2-新浪微博
                        addSharePlatform(SinaWeibo.NAME);
                    }else if("3".equals(platform)){
                        //3-短信
                        addSharePlatform(ShortMessage.NAME);
                    }else if("4".equals(platform)){
                        //4-qq好友
                        addSharePlatform(QQ.NAME);
                    }else if("5".equals(platform)){
                        //5-qq空间
                        addSharePlatform(QZone.NAME);
                    }
                }
            }

            //刷新分享面板
            refreshSharePannel();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 分享网格点击监听器
     */
    class GridOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            final PlatformItem rowData = (PlatformItem) adapterView.getItemAtPosition(position);
            if(null == rowData)return;
            String platform = rowData.platform.getName();
            //回调监听
            if(null != mSharePlatClickListener){
                mSharePlatClickListener.onItemClick(rowData.platform);
            }
            mShareParams.setText(shareContent);
            mShareParams.setTitleUrl(linkURL);
            //QQ空间需要SiteUrl
            mShareParams.setSiteUrl(linkURL);
            //微信分享网页、视频需要
            mShareParams.setUrl(linkURL);
//            Logger.d(TAG, "分享链接：" + linkURL);
            if(SinaWeibo.NAME.equals(platform)){
                if(ShareSDKHelper.isInstallSina(mActivity)){
                    mShareParams.setText(shareContent+linkURL);
                    mShareSDKHelper.shareSinaWeibo(mShareParams);
                }else{
                    Toast.makeText(mActivity, "请安装新浪客户端再分享", Toast.LENGTH_SHORT).show();
                }
            }else if(Wechat.NAME.equals(platform)){
                if(ShareSDKHelper.isInstallWeChat(mActivity)){
                    mShareSDKHelper.shareWeChatFriend(mShareParams);
                }else{
                    Toast.makeText(mActivity, "请安装微信客户端再分享", Toast.LENGTH_SHORT).show();
                }
            }else if(WechatMoments.NAME.equals(platform)){
                if(ShareSDKHelper.isInstallWeChat(mActivity)){
                    mShareSDKHelper.shareWechatMoments(mShareParams);
                }else{
                    Toast.makeText(mActivity, "请安装微信客户端再分享", Toast.LENGTH_SHORT).show();
                }
            }else if(ShortMessage.NAME.equals(platform)){
                mShareParams.setText(shareContent+linkURL);
                mShareSDKHelper.shareShortMessage(mShareParams);
            }else if(QQ.NAME.equals(platform)){
                mShareParams.setSite("京东金融");
                if(ShareSDKHelper.isInstallQQ(mActivity)){
                    mShareSDKHelper.shareQQ(mShareParams);
                }else{
                    Toast.makeText(mActivity, "请安装QQ客户端再分享", Toast.LENGTH_SHORT).show();
                }
            }else if(QZone.NAME.equals(platform)){
                mShareParams.setSite("京东金融");
                if(ShareSDKHelper.isInstallQQ(mActivity)){
                    mShareSDKHelper.shareQQZone(mShareParams);
                }else{
                    Toast.makeText(mActivity, "请安装QQ客户端再分享", Toast.LENGTH_SHORT).show();
                }
            }

            //关闭对话框
            SharePlatformPannel.this.dismiss();
        }
    }

    /**
     * 网格列表适配器
     */
    class ShareGridAdapter extends BaseAdapter {

        Context mContext;

        ArrayList mDataSource = new ArrayList();

        public ShareGridAdapter(Activity context){
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mDataSource.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataSource.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View itemView, ViewGroup parent) {
            // 查找控件
            ViewHolder holder = null;
            if (null == itemView) {
                itemView =
                        LayoutInflater.from(mContext).inflate(R.layout.anl_common_sharesdk_grid_item, parent,
                                false);
                holder = new ViewHolder();
                holder.tv_label = (TextView) itemView.findViewById(R.id.tv_label);
                holder.iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
                // 缓存View
                itemView.setTag(holder);
            } else {
                holder = (ViewHolder) itemView.getTag();
            }

            // 装填数据
            final PlatformItem rowData = (PlatformItem) getItem(position);
            holder.tv_label.setText(rowData.label);
            // 图标
            holder.iv_icon.setImageResource(rowData.icon);
            return itemView;
        }

        public void addItem(Object item){
            mDataSource.add(item);
        }

        public void clear(){
            mDataSource.clear();
        }
    }

    class ViewHolder {
        TextView tv_label;
        ImageView iv_icon;
    }

    class PlatformItem {
        Platform platform;
        int icon;
        String label = "";

        public PlatformItem() {

        }

        public PlatformItem(Platform platform,int icon, String label) {
            this.platform = platform;
            this.icon = icon;
            this.label = label;
        }
    }
}
