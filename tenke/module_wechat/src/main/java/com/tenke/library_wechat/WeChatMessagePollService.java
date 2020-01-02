package com.tenke.library_wechat;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class WeChatMessagePollService extends IntentService {
    public static final String TAG = WeChatMessagePollService.class.getSimpleName();
    private static final String ACTION_MESSAGE_LOOP = "ACTION_MESSAGE_LOOP";
    private static final String WECHAT_AUTHENTICATION_BEAN = "WECHAT_AUTHENTICATION_BEAN";
    private WeChatAuthenticationBean mWeChatAuthenticationBean;
    private ExecutorService mExecutorService;
    private static volatile boolean isRunning = false;

    public WeChatMessagePollService() {
        super("WeChatMessagePollService");
        mExecutorService = Executors.newSingleThreadExecutor();
    }
    
    public static void start(Context context, WeChatAuthenticationBean bean) {
        Logger.t(TAG).d("start poll service");
        isRunning = true;
        Intent intent = new Intent(context, WeChatMessagePollService.class);
        intent.setAction(ACTION_MESSAGE_LOOP);
        intent.putExtra(WECHAT_AUTHENTICATION_BEAN, bean);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Logger.t(TAG).d( "stop poll service");
        isRunning = false;
        Intent intent = new Intent(context, WeChatMessagePollService.class);
        context.stopService(intent);
    }

    @Override
    public void onDestroy() {
        mExecutorService.shutdown();
        sendStopped();
        super.onDestroy();
    }

    private void sendStopped() {
        Logger.t(TAG).d("sendStopped");
        Intent intent = new Intent(WeChatManager.ACTION_SERVICE_STOPPED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendAuthFailed(String errorMessage) {
        Logger.t(TAG).d("sendAuthFailed " + errorMessage);
        Intent intent = new Intent(WeChatManager.ACTION_AUTH_FAILED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendStarted() {
        Logger.t(TAG).d("sendStarted");
        Intent intent = new Intent(WeChatManager.ACTION_SERVICE_STARTED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendNewMessage(WeChatMessageBean weChatMessageBean) {
        Logger.t(TAG).d("sendNewMessage");
        Intent intent = new Intent(WeChatManager.ACTION_NEW_MESSAGE);
        intent.putExtra(WeChatManager.MESSAGE_VO, weChatMessageBean);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendStarted();
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MESSAGE_LOOP.equals(action)) {
                mWeChatAuthenticationBean = (WeChatAuthenticationBean) intent.getSerializableExtra(WECHAT_AUTHENTICATION_BEAN);
                handleActionMessageLoop();
                while (isRunning) ;
            }
        }
    }


    private void handleActionMessageLoop() {
        if (mExecutorService.isShutdown()) {
            return;
        }
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                pollWeChatMessage(mWeChatAuthenticationBean);
            }
        });
    }

    private void pollWeChatMessage(WeChatAuthenticationBean authenticationBean) {
        WeChatManager.getInstance().pollWeChatMessage(authenticationBean, new WeChatManager.WeChatMessageLoopCallback() {
            @Override
            public void onSuccess(WeChatMessageBean vo) {
                if (vo != null) {
                    int msgCount = vo.getAddMsgCount();
                    mWeChatAuthenticationBean.setSyncKey(msgCount == 0 ? vo.getSyncCheckKey() : vo.getSyncKey());
                    if (msgCount > 0) {
                        sendNewMessage(vo);
                        WeChatMessage weChatMessage = vo.getAddMsgList().get(0);
                        if (!TextUtils.isEmpty(weChatMessage.getStatusNotifyUserName())) {
                            WeChatManager.getInstance().batchGetGroupContacts(mWeChatAuthenticationBean.getBaseUrl(),
                                    mWeChatAuthenticationBean.getPassTicket(),
                                    mWeChatAuthenticationBean.getBaseRequest(),
                                    weChatMessage.getStatusNotifyUserName()).subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(String s) {
                                    if (TextUtils.isEmpty(s)) {
                                        throw new NullPointerException("batchGetGroupContacts response is null");
                                    }
                                    WechatUiDataBean wechatUiDataBean = new Gson().fromJson(s, WechatUiDataBean.class);
                                    if (wechatUiDataBean.getBaseResponse().isSuccess()) {
                                        List<WeChatContactVo> weChatContactVos = wechatUiDataBean.getContactList();
                                        WeChatManager.getInstance().getWeChatRepository().saveWeChatConversationContacts(weChatContactVos);
                                        Logger.t(TAG).d("batchGetGroupContacts success.");
                                    } else {
                                        throw new RuntimeException("batchGetGroupContacts response fail");
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Logger.t(TAG).e("pollWeChatMessage batchGetGroupContacts error ", e);
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                        }
                    }
                }
                handleActionMessageLoop();
            }

            @Override
            public void onPollEmpty() {
                handleActionMessageLoop();
            }

            @Override
            public void onAuthFailed(String retCode, String errorMessage) {
                sendAuthFailed(errorMessage);
                switch (retCode) {
                    case WeChatManager.WECHAT_SYNC_CHECK_RETURN_CODE_NORMAL:
                    case WeChatManager.WECHAT_SYNC_CHECK_RETURN_CODE_SERVER_ERROR:
                        handleActionMessageLoop();
                        break;
                    case WeChatManager.WECHAT_SYNC_CHECK_RETURN_CODE_LOGOUT:
                    case WeChatManager.WECHAT_SYNC_CHECK_RETURN_CODE_QUIT_ON_PHONE:
                        Logger.t(TAG).d("onAuthFailed logoutWeChat" + retCode);
                        //ExternalLaunchUtil.wechatLogin(WeChatMessagePollService.this, false);
                        WeChatManager.getInstance().logoutWeChat();
                        break;
                    case WeChatManager.WECHAT_SYNC_CHECK_RETURN_CODE_OTHER_LOGIN:
                        Logger.t(TAG).d("onAuthFailed logoutWeChat" + retCode);
                        //ExternalLaunchUtil.wechatLogin(WeChatMessagePollService.this, false);
                        WeChatManager.getInstance().logoutWeChat();
                        break;
                }
            }
        });
    }
}
