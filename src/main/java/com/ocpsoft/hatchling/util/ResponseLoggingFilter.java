package com.ocpsoft.hatchling.util;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;

import com.ocpsoft.pretty.PrettyContext;

@WebFilter(urlPatterns = { "/faces/index.xhtml", "/faces/admin.xhtml" },
         dispatcherTypes = { DispatcherType.FORWARD, DispatcherType.REQUEST })
public class ResponseLoggingFilter implements Filter
{
   Logger log = Logger.getLogger(ResponseLoggingFilter.class);

   @Override
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException
   {
      Timer timer = Timer.getTimer();
      timer.start();
      chain.doFilter(request, response);
      timer.stop();
      double time = timer.getElapsedMilliseconds();
      PrettyContext context = PrettyContext.getCurrentInstance((HttpServletRequest) request);
      if (context != null)
      {
         this.log.info("Reponse completed in: " + time / 1000.0 + " - " + context.getRequestURL()
                  + context.getRequestQueryString() + ", IPAddress=" + request.getRemoteAddr() + ", RemoteHost"
                  + request.getRemoteHost());
      }
      else
      {
         this.log.info("Reponse completed in: " + time / 1000.0 + " - "
                  + ((HttpServletRequest) request).getRequestURL() + ", IPAddress=" + request.getRemoteAddr()
                  + ", RemoteHost=" + request.getRemoteHost());
      }
   }

   @Override
   public void init(final FilterConfig filterConfig)
   {
   }

   @Override
   public void destroy()
   {
   }
}