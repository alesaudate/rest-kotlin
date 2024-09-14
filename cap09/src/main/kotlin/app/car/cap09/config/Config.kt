package app.car.cap09.config

import app.car.cap09.domain.UserRepository
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.http.HttpMethod.GET
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.sql.DataSource
import app.car.cap09.domain.User as DomainUser


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
class SecurityConfig(
    val datasource: DataSource
) {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(): UserDetailsService {
        return JdbcUserDetailsManager(datasource).also {
            it.usersByUsernameQuery = "select username, password, enabled from users where username=?"
            it.setAuthoritiesByUsernameQuery("select u.username, r.roles from user_roles r, users u where r.user_id = u.id and u.username=?")
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): DefaultSecurityFilterChain {
        http.csrf { it.disable() }
        http.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        http.authorizeHttpRequests {
            it.requestMatchers(GET, "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                .permitAll()
        }
            .authorizeHttpRequests {
                it.anyRequest().authenticated()
            }
        http.httpBasic { }
        http.headers { it.frameOptions { customizer -> customizer.disable() }}
        http.cors {
            it.configurationSource(corsConfigurationSource())
        }
        return http.build()
    }

    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("https://bestcars.com")
            allowedMethods = listOf("GET","POST","PUT","DELETE","PATCH")
            addAllowedHeader("*")
        }
        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
        return source
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


@Configuration
class AppConfig {

    @Bean
    fun messageSource() = ReloadableResourceBundleMessageSource().apply {
        setBasename("classpath:/i18n/messages")
    }
}


@Configuration
class OpenAPIConfig {

    @Bean
    fun openAPIDocumentation(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("C.A.R. API")
                    .description("API do sistema C.A.R., de facilitação de mobilidade urbana")
                    .version("v1.0")
                    .contact(
                        Contact()
                            .name("Alexandre Saudate")
                            .email("alesaudate@gmail.com")
                    )
            )
    }
}
