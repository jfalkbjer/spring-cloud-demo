package com.example;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@EnableResourceServer
@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	@RestController
	class PrincipalRestController {

		@RequestMapping("/user")
		Map<String, String> principal(Principal principal) {
			OAuth2Authentication p = (OAuth2Authentication) principal;

			Map<String, String> map = new LinkedHashMap<>();
			map.put("name", principal.getName());
			map.put("authorities",
					p.getAuthorities().stream().map(x -> x.getAuthority()).collect(Collectors.joining(":")));
			return map;
		}
	}

	/**
	 * Service that take username and find user in ldap/database or some user
	 * store, to get user details.
	 */
	@Service
	class AccountUserDetailsService implements UserDetailsService {

		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			// @formatter:off
			return new User("jifa", "password", true, true, true, true,
					        AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER")); 
			// @formatter:on
		}
	}

	@Configuration
	@EnableAuthorizationServer
	class OAuthConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private TokenStore tokenStore;

		@Autowired
		private AuthenticationManager authenticationManager;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			clients
			    .inMemory()
			    .withClient("myclient")
			    .secret("mysecret")
			    .scopes("openid")
			    .autoApprove(true)
			    .authorizedGrantTypes("authorization_code", "refresh_token", "password");
			// @formatter:on
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.authenticationManager(this.authenticationManager);
			endpoints.tokenStore(tokenStore);
		}
	}

	@Controller
	public class OAuthController {
		@Autowired
		private TokenStore tokenStore;

		@RequestMapping(value = "/oauth/revoke-token", method = RequestMethod.GET)
		@ResponseStatus(HttpStatus.OK)
		public void logout(HttpServletRequest request) {
			String authHeader = request.getHeader("Authorization");
			if (authHeader != null) {
				String tokenValue = authHeader.replace("Bearer", "").trim();
				OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
				tokenStore.removeAccessToken(accessToken);
			}
		}
	}
}