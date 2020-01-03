package com.ace.cache.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName WebUtil
 * @Author: Bill_Ho
 * @Date: Create in 2019/5/14 11:23 AM
 * @Description: TODO
 **/
public class WebUtil {
        public static String getCookieValue(HttpServletRequest request, String cookieName) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                Cookie[] var3 = cookies;
                int var4 = cookies.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Cookie cookie = var3[var5];
                    if (cookie.getName().equals(cookieName)) {
                        return cookie.getValue();
                    }
                }
            }

            return null;
        }
}
