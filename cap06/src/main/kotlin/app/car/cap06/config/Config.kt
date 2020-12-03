package app.car.cap06.config

import app.car.cap06.domain.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.annotation.PostConstruct
import javax.sql.DataSource
import app.car.cap06.domain.User as DomainUser

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
class SecurityConfig(
    val datasource: DataSource
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()

        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        /*
        val password = "{noop}password";

        val driver = User.builder()
            .username("driver")
            .password(password)
            .roles("DRIVER")

        val passenger = User.builder()
            .username("passenger")
            .password(password)
            .roles("PASSENGER")

        val admin = User.builder()
            .username("admin")
            .password(password)
            .roles("ADMIN")

        auth.inMemoryAuthentication()
            .withUser(driver)
            .withUser(passenger)
            .withUser(admin)

         */

        val queryUsers = "select username, password, enabled from user where username=?"
        val queryRoles = "select u.username, r.roles from user_roles r, user u where r.user_id = u.id and u.username=?"

        auth.jdbcAuthentication()
            .dataSource(datasource)
            .passwordEncoder(passwordEncoder())
            .usersByUsernameQuery(queryUsers)
            .authoritiesByUsernameQuery(queryRoles)
    }
}

@Configuration
class LoadUserConfig(
    val passwordEncoder: PasswordEncoder,
    val userRepository: UserRepository
) {

    @PostConstruct
    fun init() {
        val admin = DomainUser(
            username = "admin",
            password = passwordEncoder.encode("password"),
            roles = mutableListOf("ROLE_ADMIN")
        )
        userRepository.save(admin)
    }

}