package com.laioffer.staybooking.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*This class is a CORS filter that ensures appropriate headers are set to allow cross-origin requests.
It's a common practice to include such a filter in Spring applications to handle CORS-related concerns
and enable secure communication between different origins.*/

@Component    // This annotation marks the class as a Spring component, allowing it to be automatically discovered and managed by the Spring container.
@Order(Ordered.HIGHEST_PRECEDENCE)      // 数字越小优先级越高, This annotation sets the priority order of the filter. In this case, the filter has the highest precedence, which means it will be executed before other filters.
public class CorsFilter extends OncePerRequestFilter {   // This OncePerRequestFilter superclass ensures that the doFilterInternal method is invoked only once per request.

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");   // * 也可以换成前端deploy的url，让它更安全
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
    /*doFilterInternal Method:
    This method is the core of the CORS filter and is invoked for each incoming request.
    It sets the following CORS-related headers in the response:
    Access-Control-Allow-Origin: This header specifies which origins are allowed to access the resource.
    In this case, it's set to "*" to allow any origin. You can replace "*" with a specific origin if you
    want to restrict access to a particular domain.

    Access-Control-Allow-Methods: This header lists the HTTP methods that are allowed for the resource.
    In this case, it allows methods like POST, GET, OPTIONS, and DELETE.
    Access-Control-Allow-Headers: This header defines which headers can be included in the request.
    It allows headers like Authorization and Content-Type.

    If the incoming request method is OPTIONS (a pre-flight request sent by the browser to check CORS permissions),
    the response status is set to HttpServletResponse.SC_OK (200 OK) without passing the request to the filter chain. This effectively handles the pre-flight request.
    For non-OPTIONS requests, the filter chain is invoked by calling filterChain.doFilter(httpServletRequest,
    httpServletResponse). This allows the request to proceed to its destination, such as a controller method.
    */
}

