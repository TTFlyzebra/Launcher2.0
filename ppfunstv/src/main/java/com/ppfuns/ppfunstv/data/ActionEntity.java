package com.ppfuns.ppfunstv.data;


/**
 * 命令解析
 */

public  class ActionEntity{
        private String url;
        private String play;
        private String intent;
        private String appendAttr;
        private String packageName;
        private String className;
        private String action;
        private String data;//json 格式
        private String downIntent;
        private String downPara;
        private String cmd;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPlay() {
            return play;
        }

        public void setPlay(String play) {
            this.play = play;
        }

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public String getAppendAttr() {
            return appendAttr;
        }

        public void setAppendAttr(String appendAttr) {
            this.appendAttr = appendAttr;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getDownIntent() {
            return downIntent;
        }

        public void setDownIntent(String downIntent) {
            this.downIntent = downIntent;
        }

        public String getDownPara() {
            return downPara;
        }

        public void setDownPara(String downPara) {
            this.downPara = downPara;
        }

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

    @Override
    public String toString() {
        return "ActionEntity{" +
                "url='" + url + '\'' +
                ", play='" + play + '\'' +
                ", intent='" + intent + '\'' +
                ", appendAttr='" + appendAttr + '\'' +
                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", action='" + action + '\'' +
                ", data='" + data + '\'' +
                ", downIntent='" + downIntent + '\'' +
                ", downPara='" + downPara + '\'' +
                ", cmd='" + cmd + '\'' +
                '}';
    }
}