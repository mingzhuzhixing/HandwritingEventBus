package com.v.eventbus;

import java.lang.reflect.Method;

public class SubscribeMethod {

    /**
     * 处理事件的注册方法
     */
    private Method method;

    /**
     * 执行线程的模式
     */
    private ThreadMode threadMode;

    /**
     * 方法的参数类型
     */
    private Class<?> parameterType;

    public SubscribeMethod(Method method, ThreadMode threadMode, Class<?> parameterType) {
        this.method = method;
        this.threadMode = threadMode;
        this.parameterType = parameterType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }
}
