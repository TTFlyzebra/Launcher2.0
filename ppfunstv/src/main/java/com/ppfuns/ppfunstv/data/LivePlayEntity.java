package com.ppfuns.ppfunstv.data;

/**
 * Created by lizongyuan on 2017/1/12.
 * E-mail:lizy@ppfuns.com
 */

public class LivePlayEntity {


        /**
         * play : dvb://0.0.0
         * intent : com.ppfuns.live.action.ACTION_ENTRY_LIVE
         * data : dvb://0.0.0?bounds=0.0.0.1920,1080&group=HD
         * cmd :
         */
        public String play;
        public String intent;
        public String data;
        public String cmd;

        @Override
        public String toString() {
            return "LivePlayActionEntity{" +
                    "play='" + play + '\'' +
                    ", intent='" + intent + '\'' +
                    ", data='" + data + '\'' +
                    ", cmd='" + cmd + '\'' +
                    '}';
        }

}
