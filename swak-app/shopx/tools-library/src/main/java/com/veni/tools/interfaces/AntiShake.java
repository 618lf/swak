package com.veni.tools.interfaces;

import java.util.Calendar;

/**
 * 作者：kkan on 2017/12/22
 * 当前类注释:
 *  防止重复点击
 *  if(antiShake.check(view.getId()))return;
 */

public class AntiShake {
    private  LimitQueue<NoFastClickUtil> queues = new LimitQueue<>(20);
    private int minClickDelayTime = 1000;

    public AntiShake() {
    }
    public AntiShake(int minClickDelayTime) {
        this.minClickDelayTime = minClickDelayTime;
    }

    public boolean check(Object object) {
        String flag = null;
        if (object == null) {
            flag = Thread.currentThread().getStackTrace()[2].getMethodName();
        } else {
            flag = object.toString();
        }
        for (NoFastClickUtil util : queues.getArrayList()) {
            if (util.getMethodName().equals(flag)) {
                return util.check();
            }
        }
        NoFastClickUtil clickUtil = new NoFastClickUtil(flag);
        clickUtil.setMinClickDelayTime(minClickDelayTime);
        queues.offer(clickUtil);
        return clickUtil.check();
    }

    public boolean check() {
        return check(null);
    }


    public class NoFastClickUtil {
        private String methodName;
        private int MinClickDelayTime = 1000;
        private long lastClickTime = 0;

        public void setMinClickDelayTime(int MinClickDelayTime){
            this.MinClickDelayTime=MinClickDelayTime;
        }

        public NoFastClickUtil(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return methodName;
        }

        public boolean check() {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MinClickDelayTime) {
                lastClickTime = currentTime;
                return false;
            } else {
                return true;
            }
        }
    }
}
