    package org.bgm.productservice.config;

    import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.core.annotation.Order;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.fasterxml.jackson.databind.json.JsonMapper;
    import org.springframework.security.jackson2.CoreJackson2Module;
    import org.springframework.security.jackson2.SecurityJackson2Modules;
    import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
    import org.springframework.security.web.authentication.WebAuthenticationDetails;
    import org.springframework.security.web.debug.DebugFilter;

    @Configuration
    public class SecurityConfig {

        @Bean
        public SecurityDebugFilter debugFilter() {
            return new SecurityDebugFilter();
        }

        @Bean
        public PasswordEncoder getPasswordEncoder(){
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            return  passwordEncoder;
        }

        @Bean
        public ObjectMapper objectMapper() {

            ObjectMapper mapper = JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build();

            mapper.registerModule(new CoreJackson2Module());

            mapper.registerModule(
                    new OAuth2AuthorizationServerJackson2Module()
            );
            mapper.addMixIn(
                    WebAuthenticationDetails.class,
                    WebAuthenticationDetailsMixin.class
            );
            return mapper;
        }


        @Bean
        @Order(1)
        public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http,
                                                                 SecurityDebugFilter debugFilter)
                throws Exception {

            OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                    new OAuth2AuthorizationServerConfigurer();

            http.securityMatcher(
                    authorizationServerConfigurer.getEndpointsMatcher()
            );

            // 1. Enable authorization server features
            http.with(
                    authorizationServerConfigurer,
                    Customizer.withDefaults()
            );

            // 2. FIX: Force authentication on all endpoints matching this chain
            http.authorizeHttpRequests(authorize -> authorize
                    .anyRequest().authenticated()
            );

            // 3. Keep your entry point for redirection
            http.exceptionHandling(ex ->
                    ex.authenticationEntryPoint(
                            new LoginUrlAuthenticationEntryPoint("/login")
                    )
            );

            http.addFilterAfter(
                    debugFilter,
                    org.springframework.security.web.context.SecurityContextHolderFilter.class
            );
            return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                       SecurityDebugFilter debugFilter) throws Exception {
    /*
    Authorize
    /products
    /users
    /orders
     */
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/login",
                                    "/error")
                            .permitAll()
                            .anyRequest()
                            .authenticated())
                    .formLogin(Customizer.withDefaults());
            http.addFilterAfter(
                    debugFilter,
                    org.springframework.security.web.context.SecurityContextHolderFilter.class
            );
            return http.build();
        }
    }
