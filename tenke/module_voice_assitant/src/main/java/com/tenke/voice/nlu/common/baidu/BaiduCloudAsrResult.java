package com.tenke.voice.nlu.common.baidu;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class BaiduCloudAsrResult implements Serializable {
    private static final String TAG = BaiduCloudAsrResult.class.getSimpleName();

    private BaiduCloudNluResult baiduCloudNluResult;

    public static BaiduCloudAsrResult parseJson(String json) {
        BaiduCloudAsrResult baiduCloudAsrResult = new BaiduCloudAsrResult();
        try {
            JSONObject rootObject = new JSONObject(json);
            JSONObject mergedRes = rootObject.getJSONObject("merged_res");
            String semanticForm = mergedRes.getString("semantic_form");
            BaiduCloudNluResult result = new Gson().fromJson(semanticForm, BaiduCloudNluResult.class);
            baiduCloudAsrResult.setBaiduCloudNluResult(result);
        } catch (JSONException e) {
            Logger.t(TAG).e(e, "BaiduCloudAsrResult parseJson fail");
        }
        return baiduCloudAsrResult;
    }

    public BaiduCloudNluResult getBaiduCloudNluResult() {
        return baiduCloudNluResult;
    }

    public void setBaiduCloudNluResult(BaiduCloudNluResult baiduCloudNluResult) {
        this.baiduCloudNluResult = baiduCloudNluResult;
    }
}

class BaiduCloudNluResult extends BaiduLocalNluResult {

    @SerializedName("appid")
    private int appId;

    @SerializedName("err_no")
    private int errNo;

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }
}
