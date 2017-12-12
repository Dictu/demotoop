package nl.minezk.dictu.demotoop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter  {
	
    /**
     * Defines the web based security configuration.
     * 
     * @param   http It allows configuring web based security for specific http requests.
     * @throws  Exception 
     */
    @Override  
    protected void configure(HttpSecurity http) throws Exception {
    	http        
	        .authorizeRequests()
	        .antMatchers("/css/**").permitAll()
	        .antMatchers("/fonts/**").permitAll()
	        .anyRequest().fullyAuthenticated();
        http
        	.formLogin()
				.loginPage("/login")
				.failureUrl("/login?error")
				.permitAll();
        http
        	.logout()
        		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        		.logoutSuccessUrl("/")
                .permitAll();
    }
    
    @Bean
	public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
    	List<UserDetails> users = new ArrayList<>();
    	UserDetails user = User.withUsername("user").password("user").authorities("ROLE_USER").build();
        users.add(user);
        return new InMemoryUserDetailsManager(users);
	}
}
