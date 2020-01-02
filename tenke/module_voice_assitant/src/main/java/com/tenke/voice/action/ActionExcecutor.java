package com.tenke.voice.action;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.tenke.lib_common.ApplicationContextLink;
import com.tenke.voice.VoiceResult;

import java.util.Arrays;
import java.util.List;

public class ActionExcecutor {

    private ActionExcecutor(){

    }


    public static ActionExcecutor getInstance(){
        return Holder.INSTANCE;
    }

    private static final class Holder{
        private static final ActionExcecutor INSTANCE = new ActionExcecutor();
    }

    public boolean excecute(VoiceResult voiceResult){
        if(voiceResult.getAsrResult().contains("打开支付宝扫一扫")){
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ApplicationContextLink.LinkToApplication().startActivity(intent);
            return true;
        }

        if(voiceResult.getAsrResult().contains("打开浏览器")){
            PackageManager packageManager = ApplicationContextLink.LinkToApplication().getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage("mark.via");
            ApplicationContextLink.LinkToApplication().startActivity(intent);
            return true;
        }

        if(voiceResult.getAsrResult().contains("打开网易")){
            PackageManager packageManager = ApplicationContextLink.LinkToApplication().getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage("com.netease.cloudmusic");
            ApplicationContextLink.LinkToApplication().startActivity(intent);
            return true;
        }


        if (voiceResult.getIntent() != null && voiceResult.getIntent().equals("VIEW_SNS")){
            List<String> params = Arrays.asList(voiceResult.getParsedResult());
            if (params.contains("打开") && params.contains("微信") && params.contains("扫一扫")){
                PackageManager packageManager = ApplicationContextLink.LinkToApplication().getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage("com.tencent.mm");
                intent.putExtra("LauncherUI.From.Scaner.Shortcut",true);
                ApplicationContextLink.LinkToApplication().startActivity(intent);

            }else if(params.contains("打开") && params.contains("微信") && !params.contains("扫一扫")){
                PackageManager packageManager = ApplicationContextLink.LinkToApplication().getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage("com.tencent.mm");
                ApplicationContextLink.LinkToApplication().startActivity(intent);
            }

        }
        return true;
    }

}
