package kostka.moviecatalog.configuration;

import kostka.moviecatalog.service.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(final AuthenticationManagerBuilder builder) throws Exception {
        builder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();

        http.csrf().disable()
                .authorizeRequests().antMatchers("/h2-console/**").permitAll()
                .and()
                .authorizeRequests().antMatchers("/admin/**").hasRole(ADMIN_ROLE)
                .and()
                .authorizeRequests().antMatchers("/successfulLogin").hasAnyRole(USER_ROLE, ADMIN_ROLE)
                .and()
                .authorizeRequests().antMatchers("/**").hasAnyRole(USER_ROLE, ADMIN_ROLE)
                .and()
                .formLogin().defaultSuccessUrl("/successfulLogin", true).permitAll()
                .and()
                .logout().permitAll();
    }
}
