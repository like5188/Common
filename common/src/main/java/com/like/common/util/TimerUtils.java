package com.like.common.util;

import android.os.Handler;

/**
 * 倒计时工具类
 */
public class TimerUtils {
    private long startTime;
    private long endTime;
    private long remainderTime;
    private long step = 1000;// 默认步长1000毫秒
    private OnTickListener mTickListener;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTickListener != null) {
                long curTime = System.currentTimeMillis();
                if (curTime < startTime) {
                    long time2Start = startTime - curTime;
                    mTickListener.onNotStart(time2Start);
                    mHandler.postDelayed(mRunnable, time2Start);
                } else if (curTime > endTime) {
                    mTickListener.onFinished();
                    end();
                } else {
                    remainderTime = endTime - curTime;
                    int returnType = mTickListener.getReturnType();
                    if (returnType == OnTickListener.TYPE_REMAINDER_TIME) {
                        returnRemainderTime();
                    } else if (returnType == OnTickListener.TYPE_D_H_M_S) {
                        returnDHMS();
                    } else if (returnType == OnTickListener.TYPE_H_M_S) {
                        returnHMS();
                    } else {
                        returnRemainderTime();
                        returnDHMS();
                        returnHMS();
                    }
                    mHandler.postDelayed(mRunnable, step);
                }
            }
        }
    };

    private void returnRemainderTime() {
        mTickListener.onTicked(remainderTime);
    }

    private void returnDHMS() {
        long timeSec = remainderTime / 1000;
        long day = timeSec / (24 * 3600);
        timeSec = timeSec % (24 * 3600);
        long hour = timeSec / 3600;
        timeSec = timeSec % 3600;
        long minutes = timeSec / 60;
        long second = timeSec % 60;
        mTickListener.onTicked(day, hour, minutes, second);
    }

    private void returnHMS() {
        long timeSec = remainderTime / 1000;
        long hour = timeSec / 3600;
        timeSec = timeSec % 3600;
        long minutes = timeSec / 60;
        long second = timeSec % 60;
        mTickListener.onTicked(hour, minutes, second);
    }

    public TimerUtils(long startTime, long endTime, OnTickListener listener) {
        this(startTime, endTime, 1000, listener);
    }

    public TimerUtils(long startTime, long endTime, long step, OnTickListener listener) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.step = step;
        mTickListener = listener;
        remainderTime = endTime - startTime;
    }

    /**
     * 开始倒计时
     */
    public void start() {
        mHandler.post(mRunnable);
    }

    /**
     * 结束倒计时
     */
    public void end() {
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 倒计时监听接口
     */
    public interface OnTickListener {
        public static final int TYPE_REMAINDER_TIME = 1;
        public static final int TYPE_D_H_M_S = 2;
        public static final int TYPE_H_M_S = 3;

        /**
         * 返回类型，避免计算多种onTicked方法，造成浪费
         *
         * @return
         */
        int getReturnType();

        /**
         * 正在倒计时
         *
         * @param remainderTime 剩余时间
         */
        void onTicked(long remainderTime);

        /**
         * 正在倒计时
         *
         * @param day     天
         * @param hour    小时
         * @param minutes 分
         * @param second  秒
         */
        void onTicked(long day, long hour, long minutes, long second);

        /**
         * 正在倒计时
         *
         * @param hour    小时
         * @param minutes 分
         * @param second  秒
         */
        void onTicked(long hour, long minutes, long second);

        /**
         * 倒计时结束
         */
        void onFinished();

        /**
         * 倒计时尚未开始
         *
         * @param time2Start 距离开始的时间
         */
        void onNotStart(long time2Start);
    }

}
