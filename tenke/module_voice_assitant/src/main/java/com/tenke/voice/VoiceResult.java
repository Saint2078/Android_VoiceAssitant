package com.tenke.voice;

import java.util.Arrays;
import java.util.List;

public class VoiceResult {

    private final String asrResult;
    private final String[] parsedResult;
    private final int size;

    private final String domain;
    private final String intent;

    public VoiceResult(VoiceResult instance,String asrResult) {
        this.asrResult = asrResult;
        this.parsedResult = instance.parsedResult;
        this.size = instance.size;
        this.domain = instance.domain;
        this.intent = instance.intent;
    }

    public VoiceResult(ASRBean bean) {
        asrResult = bean.getRaw_text();
        parsedResult = bean.getParsed_text().split(" ");
        size = bean.getResults().size();
        if (size !=0){
            domain = bean.getResults().get(0).getDomain();
            intent = bean.getResults().get(0).getIntent();
        }else {
            domain = null;
            intent = null;
        }
    }

    public String getAsrResult() {
        return asrResult;
    }

    public String[] getParsedResult() {
        return parsedResult;
    }

    public String getDomain() {
        return domain;
    }

    public String getIntent() {
        return intent;
    }

    @Override
    public String toString() {
        return "VoiceResult{" +
                "asrResult='" + asrResult + '\'' +
                ", parsedResult=" + Arrays.toString(parsedResult) +
                ", size=" + size +
                ", domain='" + domain + '\'' +
                ", intent='" + intent + '\'' +
                '}';
    }

    public static class ASRBean{
        /**
         * appid : 15363
         * encoding : UTF-8
         * results : [{"domain":"contact","intent":"OPEN_ADDRESS_BOOK","score":100,"slots":{}}]
         * err_no : 0
         * parsed_text : 打开 通讯 录 。
         * raw_text : 打开通讯录。
         */

        private int appid;
        private String encoding;
        private int err_no;
        private String parsed_text;
        private String raw_text;
        private List<ResultsBean> results;

        public int getAppid() {
            return appid;
        }

        public void setAppid(int appid) {
            this.appid = appid;
        }

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public int getErr_no() {
            return err_no;
        }

        public void setErr_no(int err_no) {
            this.err_no = err_no;
        }

        public String getParsed_text() {
            return parsed_text;
        }

        public void setParsed_text(String parsed_text) {
            this.parsed_text = parsed_text;
        }

        public String getRaw_text() {
            return raw_text;
        }

        public void setRaw_text(String raw_text) {
            this.raw_text = raw_text;
        }

        public List<ResultsBean> getResults() {
            return results;
        }

        public void setResults(List<ResultsBean> results) {
            this.results = results;
        }

        public static class ResultsBean {
            /**
             * domain : contact
             * intent : OPEN_ADDRESS_BOOK
             * score : 100
             * slots : {}
             */

            private String domain;
            private String intent;
            private int score;
            private SlotsBean slots;

            public String getDomain() {
                return domain;
            }

            public void setDomain(String domain) {
                this.domain = domain;
            }

            public String getIntent() {
                return intent;
            }

            public void setIntent(String intent) {
                this.intent = intent;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public SlotsBean getSlots() {
                return slots;
            }

            public void setSlots(SlotsBean slots) {
                this.slots = slots;
            }

            public static class SlotsBean {
            }
        }
    }



}
