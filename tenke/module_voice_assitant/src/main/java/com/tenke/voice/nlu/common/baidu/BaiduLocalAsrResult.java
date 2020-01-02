package com.tenke.voice.nlu.common.baidu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaiduLocalAsrResult implements Serializable {
    private static final String TAG = BaiduLocalAsrResult.class.getSimpleName();
    private static final int ERROR_NONE = 0;

    private String origalJson;
    private String[] resultsRecognition;
    private String origalResult;
    private String sn;
    private String desc;
    private String resultType;
    private int error = -1;
    private int subError = -1;
    @Nullable
    private BaiduLocalNluResult localAsrResult = new BaiduLocalNluResult();

    @NonNull
    public static BaiduLocalAsrResult  parseJson(String jsonStr) {
        BaiduLocalAsrResult baiduLocalAsrResult = new BaiduLocalAsrResult();
        baiduLocalAsrResult.setOrigalJson(jsonStr);
        try {
            JSONObject json = new JSONObject(jsonStr);
            int error = json.optInt("error");
            int subError = json.optInt("sub_error");
            baiduLocalAsrResult.setError(error);
            baiduLocalAsrResult.setDesc(json.optString("desc"));
            baiduLocalAsrResult.setResultType(json.optString("result_type"));
            baiduLocalAsrResult.setSubError(subError);
            if (error == ERROR_NONE) {
                baiduLocalAsrResult.setOrigalResult(json.getString("origin_result"));
                JSONArray arr = json.optJSONArray("results_recognition");
                if (arr != null) {
                    int size = arr.length();
                    String[] recogs = new String[size];
                    for (int i = 0; i < size; i++) {
                        recogs[i] = arr.getString(i);
                    }
                    baiduLocalAsrResult.setResultsRecognition(recogs);
                }
                String nluStr = json.optString("results_nlu");
                if (nluStr != null) {
                    BaiduLocalNluResult result = new Gson().fromJson(nluStr, BaiduLocalNluResult.class);
                    baiduLocalAsrResult.setLocalNluResult(result);
                }
            }
        } catch (JSONException e) {
            Logger.t(TAG).e(e, "BaiduLocalAsrResult parseJson fail");
        }

        return baiduLocalAsrResult;
    }

    public boolean hasError() {
        return error != ERROR_NONE;
    }

    public boolean isFinalResult() {
        return "final_result".equals(resultType);
    }

    public boolean isPartialResult() {
        return "partial_result".equals(resultType);
    }

    public boolean isNluResult() {
        return "nlu_result".equals(resultType);
    }

    public String getOrigalJson() {
        return origalJson;
    }

    public void setOrigalJson(String origalJson) {
        this.origalJson = origalJson;
    }

    @NonNull
    public String[] getResultsRecognition() {
        return MoreObjects.firstNonNull(resultsRecognition, new String[0]);
    }

    public void setResultsRecognition(@Nullable String[] resultsRecognition) {
        this.resultsRecognition = resultsRecognition;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOrigalResult() {
        return origalResult;
    }

    public void setOrigalResult(String origalResult) {
        this.origalResult = origalResult;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public int getSubError() {
        return subError;
    }

    public void setSubError(int subError) {
        this.subError = subError;
    }

    @NonNull
    public BaiduLocalNluResult getLocalNluResult() {
        return MoreObjects.firstNonNull(localAsrResult, new BaiduLocalNluResult());
    }

    public void setLocalNluResult(@Nullable BaiduLocalNluResult baiduLocalNluResult) {
        this.localAsrResult = baiduLocalNluResult;
    }
}

class BaiduLocalNluResult implements Serializable {
    private final static long serialVersionUID = 8431886031823628529L;

    @SerializedName("raw_text")
    private String rawText;

    @SerializedName("parsed_text")
    private String parsedText;

    @Nullable
    @SerializedName("results")
    private List<NluVo> nluVos = null;

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    @NonNull
    public List<NluVo> getNluVos() {
        return MoreObjects.firstNonNull(nluVos, new ArrayList<NluVo>());
    }

    public void setNluVos(@Nullable List<NluVo> nluVos) {
        this.nluVos = nluVos;
    }

    public String getParsedText() {
        return parsedText;
    }

    public void setParsedText(String parsedText) {
        this.parsedText = parsedText;
    }
}