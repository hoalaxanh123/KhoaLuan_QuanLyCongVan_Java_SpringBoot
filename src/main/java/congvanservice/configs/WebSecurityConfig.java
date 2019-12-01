package congvanservice.configs;

import congvanservice.jwt.JWTAuthenticationFilter;
import congvanservice.jwt.JWTLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    ResourceServerClientConfig resourceServerClientConfig;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("Hieu").password("{noop}e10adc3949ba59abbe56e057f20f883e").roles("Admin");

       // Mình comment phần dưới này vì chúng ta ko sử dụng DB nhé. Nếu các bạn sử dụng, bỏ comment và config query sao cho phù hợp. Các bạn có thể GG để tìm hiểu thêm
//       auth.jdbcAuthentication()
//               .usersByUsernameQuery("select TenTaiKhoan, MatKhau, TrangThai from TaiKhoan where TenTaiKhoan=?")
//               .authoritiesByUsernameQuery("select TenTaiKhoan, PhanQuyen from TaiKhoan where TenTaiKhoan=?");
//                        .passwordEncoder(new BCryptPasswordEncoder(16));

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(resourceServerClientConfig.enabled) {
            http.csrf().disable().authorizeRequests()
                    .antMatchers("/", "/swagger-ui.html").permitAll() // Có nghĩa là request "/" ko cần phải đc xác thực
                    .antMatchers(HttpMethod.POST, "/login").permitAll() // Request dạng POST tới "/login" luôn được phép truy cập dù là đã authenticated hay chưa
                    .antMatchers(HttpMethod.POST, "/api/uploadFile").permitAll()
                    .anyRequest().authenticated() // Các request còn lại đều cần được authenticated
                    .and()
                    // Add các filter vào ứng dụng của chúng ta, thứ mà sẽ hứng các request để xử lý trước khi tới các xử lý trong controllers.
                    // Về thứ tự của các filter, các bạn tham khảo thêm tại http://docs.spring.io/spring-security/site/docs/3.0.x/reference/security-filter-chain.html mục 7.3 Filter Ordering
                    .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        } else {
            http.csrf().disable().authorizeRequests()
                    .anyRequest().permitAll();

        }
    }
}
