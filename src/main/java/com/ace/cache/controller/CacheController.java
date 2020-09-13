package com.ace.cache.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wanghaobin
 * @create 2020/9/13.
 */
@Controller
public class CacheController {
    /**
     * 测试重定向，与验证restful
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/cache")
    public void hello(HttpServletResponse response) throws IOException {
        response.sendRedirect("/static/cache/index.html");
    }
}
