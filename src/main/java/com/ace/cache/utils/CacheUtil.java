package com.ace.cache.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
@Slf4j
public class CacheUtil {
    /**
     * 是否强制刷新缓存
     */
    private static ThreadLocal<Boolean> FORCE_REFRESH = new ThreadLocal<>();

    /**
     * 锁死当前堆栈
     */
    private static ThreadLocal<String> LOCK_STACK = new ThreadLocal<>();

    /**
     * 强制刷新本线程缓存
     */
    public static void setForceRefreshCache(){
        FORCE_REFRESH.set(true);
    }

    public static boolean isForceRefreshCache(){
        Boolean isForceRefresh = FORCE_REFRESH.get();
        boolean result = null != isForceRefresh && isForceRefresh;
        if (result && StringUtils.isBlank(LOCK_STACK.get()))
        {
            String stack = getInvokeStackStr(2);
            LOCK_STACK.set(stack);
        }
        return result;
    }

    public static void clear(){
        clear(false);
    }

    public static void clear(boolean isForce){
        if (isForce){
            FORCE_REFRESH.remove();
            LOCK_STACK.remove();
        }
        else{
            String thisLockStack = LOCK_STACK.get();
            if (StringUtils.isBlank(thisLockStack) ) {
                FORCE_REFRESH.remove();
                LOCK_STACK.remove();
            }
            else {
                String stack = getInvokeStackStr(2);
                if(thisLockStack.equals(stack)){
                    FORCE_REFRESH.remove();
                    LOCK_STACK.remove();
                }
                else{
                    return;
                }
            }
        }
    }

    /////////////////

    public static void main(String[] args) {
        CacheUtil.setForceRefreshCache();
        CacheUtil.setForceRefreshCache();

        System.out.println(CacheUtil.isForceRefreshCache());
        test2();
        System.out.println(CacheUtil.isForceRefreshCache());
        CacheUtil.clear(false);
        System.out.println(CacheUtil.isForceRefreshCache());
        CacheUtil.clear(true);
        System.out.println(CacheUtil.isForceRefreshCache());
    }

    private static void test(){
        System.out.println(getInvokeStackStr(0));
    }
    private static void test2(){
        CacheUtil.clear(false);
    }

    private static String getInvokeStackStr(int before){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StringBuilder sb= new StringBuilder();
        if (stackTraceElements.length> before +1) {
            for (int i = 1 + before; i < stackTraceElements.length; i++) {
                sb.append(stackTraceElements[i].getClassName() + "." + stackTraceElements[i].getMethodName() + "<-");
            }
        }
        return sb.toString();
    }

}
