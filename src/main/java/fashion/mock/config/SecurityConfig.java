/**
 * Author: Ngô Văn Quốc Thắng 11/05/1996
 */
package fashion.mock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	/**
	 * Author: Ngô Văn Quốc Thắng 11/05/1996
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(
				(requests) -> requests
						.requestMatchers("/register", "/save", "/forgot-password", "/verify-code", "/reset-password",
								"/checkout/**", "/products/**", "/css/**", "/js/**", "/images/**", "/shopping-cart/**",
								"/information/**", "/categories/**", "/shop/**")
						.permitAll().anyRequest().authenticated())
				.formLogin((form) -> form.loginPage("/login").permitAll()).logout((logout) -> logout.permitAll());

		return http.build();
	}

	/**
	 * Author: Ngô Văn Quốc Thắng 11/05/1996
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}