package com.tenke.library_wechat;


import java.util.ArrayList;

public class WeChatSyncKeyBean extends Bean {

    /**
     * Count : 4
     * List : [{"Key":1,"Val":662998350},{"Key":2,"Val":662998581},{"Key":3,"Val":662998511},{"Key":1000,"Val":1482228721}]
     */

    private int Count;
    private ArrayList<ListBean> List;

    public int getCount() {
        return Count;
    }

    public void setCount(int Count) {
        this.Count = Count;
    }

    public java.util.List<ListBean> getList() {
        return List;
    }

    public void setList(ArrayList<ListBean> List) {
        this.List = List;
    }

    public static class ListBean extends Bean {
        /**
         * Key : 1
         * Val : 662998350
         */

        private int Key;
        private int Val;

        public int getKey() {
            return Key;
        }

        public void setKey(int Key) {
            this.Key = Key;
        }

        public int getVal() {
            return Val;
        }

        public void setVal(int Val) {
            this.Val = Val;
        }
    }
}
