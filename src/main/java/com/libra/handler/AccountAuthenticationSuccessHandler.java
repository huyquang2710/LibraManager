package com.libra.handler;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class AccountAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		boolean hasUserRole = false;
		boolean hasAdminRole = false;
		
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for(GrantedAuthority grantedAuthority : authorities ) {
			if(grantedAuthority.getAuthority().equals("USER")) {
				try {
					hasUserRole = true;
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(grantedAuthority.getAuthority().equals("ADMIN")) {
				try {
					hasAdminRole = true;
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(hasUserRole) {
			redirectStrategy.sendRedirect(request, response, "/");
		} else if(hasAdminRole) {
			redirectStrategy.sendRedirect(request, response, "/admin");
		} else {
			throw new IllegalStateException();
		}
		
	}
	
}
