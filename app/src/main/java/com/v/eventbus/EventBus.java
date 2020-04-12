package com.v.eventbus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBus {
    private static EventBus eventBus = new EventBus();

    /**
     * 存储注册的类中的订阅方法
     */
    private Map<Object, List<SubscribeMethod>> cacheMap;

    /**
     * 线程切换到主线程
     */
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 线程池
     */
    private ExecutorService executorService;

    /**
     * 单利
     */
    public static EventBus getDefault() {
        return eventBus;
    }

    /**
     * 构成函数
     */
    private EventBus() {
        cacheMap = new HashMap<>();
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * 注册
     */
    public void register(Object subscribe) {
        //判断当前是否已经注册了
        List<SubscribeMethod> subscribeMethods = cacheMap.get(subscribe);
        if (subscribeMethods == null) {
            List<SubscribeMethod> subscribeMethod = getSubscribeMethod(subscribe);
            cacheMap.put(subscribe, subscribeMethod);
        }
    }

    /**
     * 获取注册类中的订阅方法
     *
     * @param subscribe 注册的类
     */
    private List<SubscribeMethod> getSubscribeMethod(Object subscribe) {
        List<SubscribeMethod> list = new ArrayList<>();

        //获取类class
        Class<?> aClass = subscribe.getClass();
        while (aClass != null) {
            //获取包名
            String name = aClass.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android") || name.startsWith("androidx.")) {
                break;
            }

            //获取注册类中的所有方法
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                //获取方法的注解
                OnSubscribe annotation = method.getAnnotation(OnSubscribe.class);
                if (annotation == null) {
                    continue;
                }

                //获取方法的参数类型
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException("Eventbus parameter only one");
                }

                //线程模式
                ThreadMode threadMode = annotation.threadMode();
                SubscribeMethod subscribeMethod = new SubscribeMethod(method, threadMode, parameterTypes[0]);
                list.add(subscribeMethod);
            }
            aClass = aClass.getSuperclass();
        }
        return list;
    }

    /**
     * 取消注册
     */
    public void unregisger(Object subscribe) {
        List<SubscribeMethod> list = cacheMap.get(subscribe);
        if (list != null) {
            cacheMap.remove(subscribe);
        }
    }

    /**
     * 发送消息
     */
    public void postMessage(final Object message) {
        Set<Object> keySet = cacheMap.keySet();
        Iterator<Object> iterator = keySet.iterator();

        while (iterator.hasNext()) {
            //获取注册的类
            final Object registerObject = iterator.next();

            //获取注册类中的所有添加注解的方法
            List<SubscribeMethod> subscribeMethods = cacheMap.get(registerObject);
            if (subscribeMethods == null) {
                continue;
            }
            for (final SubscribeMethod subscribeMethod : subscribeMethods) {
                if (subscribeMethod.getParameterType().isAssignableFrom(message.getClass())) {

                    switch (subscribeMethod.getThreadMode()) {
                        case MAIN:
                            //判断当前线程是否在主线程
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                invoke(subscribeMethod, registerObject, message);
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribeMethod, registerObject, message);
                                    }
                                });
                            }
                            break;

                        //接收方法在子线程种情况
                        case ASYNC:
                            //post方法执行在主线程中
                            if (Looper.myLooper() == Looper.getMainLooper()) {
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribeMethod, registerObject, message);
                                    }
                                });
                            } else {
                                invoke(subscribeMethod, registerObject, message);
                            }
                            break;

                        case POSTING:
                            invoke(subscribeMethod, registerObject, message);
                            break;
                    }

                    invoke(subscribeMethod, registerObject, message);
                }
            }
        }
    }

    private void invoke(SubscribeMethod subscribeMethod, Object registerObject, Object message) {
        Method method = subscribeMethod.getMethod();
        try {
            method.invoke(registerObject, message);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
