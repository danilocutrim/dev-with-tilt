package br.com.sample.sba.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@AllArgsConstructor
@Configuration
public class SecuritySecureConfig extends WebSecurityConfigurerAdapter {

    private final AdminServerProperties adminServer;
    private final SecurityProperties security;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.debug("SBA context path {}", adminServer.getContextPath());
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminServer.path("/"));

        http.authorizeRequests(
                (authorizeRequests) -> authorizeRequests.antMatchers(adminServer.path("/assets/**")).permitAll()
                        .antMatchers("/actuator/info").permitAll()
                        .antMatchers("/actuator/health").permitAll()
                        .antMatchers(adminServer.path("/login")).permitAll().anyRequest().authenticated()
        ).formLogin(
                (formLogin) -> formLogin.loginPage(adminServer.path("/login")).successHandler(successHandler).and()
        ).logout((logout) -> logout.logoutUrl(adminServer.path("/logout"))).httpBasic(Customizer.withDefaults())
                .csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher(adminServer.path("/instances"),
                                        HttpMethod.POST.toString()),
                                new AntPathRequestMatcher(adminServer.path("/instances/*"),
                                        HttpMethod.DELETE.toString()),
                                new AntPathRequestMatcher("/actuator/**")
                        ))
                .rememberMe((rememberMe) -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600));
    }

    // Required to provide UserDetailsService for "remember functionality"
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.debug("SBA: user {} password {} configured.", security.getUser().getName(), security.getUser().getPassword());
        auth.inMemoryAuthentication().withUser(security.getUser().getName())
                .password("{noop}" + security.getUser().getPassword()).roles("USER");
    }

}
