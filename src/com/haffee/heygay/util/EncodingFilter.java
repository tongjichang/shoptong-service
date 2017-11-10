package com.haffee.heygay.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class EncodingFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {}

    public void destroy() {}

    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
    	
    	

        request.setCharacterEncoding("utf-8");
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,key,id");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, PUT, DELETE, POST");
        //httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        chain.doFilter(request, response);
       
    }

}


