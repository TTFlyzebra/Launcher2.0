package com.ppfuns.ppfunstv.data;

import java.util.List;

/**
 * Created by 李宗源 on 2016/8/1.
 * E-mail:lizy@ppfuns.com
 */
public class TemperatureEntity {

    /**
     * yesterday : {"date":"16日星期五","high":"高温 26℃","fx":"北风","low":"低温 20℃","fl":"微风","type":"多云"}
     * city : 长沙
     * aqi : 48
     * forecast : [{"date":"17日星期六","high":"高温 29℃","fengli":"微风级","low":"低温 21℃",
     * "fengxiang":"北风","type":"晴"},{"date":"18日星期天","high":"高温 30℃","fengli":"微风级","low":"低温
     * 23℃","fengxiang":"东南风","type":"多云"},{"date":"19日星期一","high":"高温 29℃","fengli":"微风级",
     * "low":"低温 24℃","fengxiang":"南风","type":"阵雨"},{"date":"20日星期二","high":"高温 30℃",
     * "fengli":"微风级","low":"低温 24℃","fengxiang":"南风","type":"阵雨"},{"date":"21日星期三","high":"高温
     * 28℃","fengli":"微风级","low":"低温 24℃","fengxiang":"南风","type":"中雨"}]
     * ganmao : 各项气象条件适宜，无明显降温过程，发生感冒机率较低。
     * wendu : 28
     */

    private DataBean data;
    /**
     * data : {"yesterday":{"date":"16日星期五","high":"高温 26℃","fx":"北风","low":"低温 20℃","fl":"微风",
     * "type":"多云"},"city":"长沙","aqi":"48","forecast":[{"date":"17日星期六","high":"高温 29℃",
     * "fengli":"微风级","low":"低温 21℃","fengxiang":"北风","type":"晴"},{"date":"18日星期天","high":"高温
     * 30℃","fengli":"微风级","low":"低温 23℃","fengxiang":"东南风","type":"多云"},{"date":"19日星期一",
     * "high":"高温 29℃","fengli":"微风级","low":"低温 24℃","fengxiang":"南风","type":"阵雨"},
     * {"date":"20日星期二","high":"高温 30℃","fengli":"微风级","low":"低温 24℃","fengxiang":"南风",
     * "type":"阵雨"},{"date":"21日星期三","high":"高温 28℃","fengli":"微风级","low":"低温 24℃",
     * "fengxiang":"南风","type":"中雨"}],"ganmao":"各项气象条件适宜，无明显降温过程，发生感冒机率较低。","wendu":"28"}
     * status : 1000
     * desc : OK
     */

    private int status;
    private String desc;

    public DataBean getData(){
        return data;
    }

    public void setData(DataBean data){
        this.data = data;
    }

    public int getStatus(){
        return status;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public String getDesc(){
        return desc;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public static class DataBean{
        /**
         * date : 16日星期五
         * high : 高温 26℃
         * fx : 北风
         * low : 低温 20℃
         * fl : 微风
         * type : 多云
         */

        private YesterdayBean yesterday;
        private String city;
        private String aqi;
        private String ganmao;
        private String wendu;
        /**
         * date : 17日星期六
         * high : 高温 29℃
         * fengli : 微风级
         * low : 低温 21℃
         * fengxiang : 北风
         * type : 晴
         */

        private List<ForecastBean> forecast;

        public YesterdayBean getYesterday(){
            return yesterday;
        }

        public void setYesterday(YesterdayBean yesterday){
            this.yesterday = yesterday;
        }

        public String getCity(){
            return city;
        }

        public void setCity(String city){
            this.city = city;
        }

        public String getAqi(){
            return aqi;
        }

        public void setAqi(String aqi){
            this.aqi = aqi;
        }

        public String getGanmao(){
            return ganmao;
        }

        public void setGanmao(String ganmao){
            this.ganmao = ganmao;
        }

        public String getWendu(){
            return wendu;
        }

        public void setWendu(String wendu){
            this.wendu = wendu;
        }

        public List<ForecastBean> getForecast(){
            return forecast;
        }

        public void setForecast(List<ForecastBean> forecast){
            this.forecast = forecast;
        }

        public static class YesterdayBean{
            private String date;
            private String high;
            private String fx;
            private String low;
            private String fl;
            private String type;

            public String getDate(){
                return date;
            }

            public void setDate(String date){
                this.date = date;
            }

            public String getHigh(){
                return high;
            }

            public void setHigh(String high){
                this.high = high;
            }

            public String getFx(){
                return fx;
            }

            public void setFx(String fx){
                this.fx = fx;
            }

            public String getLow(){
                return low;
            }

            public void setLow(String low){
                this.low = low;
            }

            public String getFl(){
                return fl;
            }

            public void setFl(String fl){
                this.fl = fl;
            }

            public String getType(){
                return type;
            }

            public void setType(String type){
                this.type = type;
            }
        }

        public static class ForecastBean{
            private String date;
            private String high;
            private String fengli;
            private String low;
            private String fengxiang;
            private String type;

            public String getDate(){
                return date;
            }

            public void setDate(String date){
                this.date = date;
            }

            public String getHigh(){
                return high;
            }

            public void setHigh(String high){
                this.high = high;
            }

            public String getFengli(){
                return fengli;
            }

            public void setFengli(String fengli){
                this.fengli = fengli;
            }

            public String getLow(){
                return low;
            }

            public void setLow(String low){
                this.low = low;
            }

            public String getFengxiang(){
                return fengxiang;
            }

            public void setFengxiang(String fengxiang){
                this.fengxiang = fengxiang;
            }

            public String getType(){
                return type;
            }

            public void setType(String type){
                this.type = type;
            }
        }
    }
}
