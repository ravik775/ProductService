package org.bgm.productservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bgm.productservice.dtos.CategoryDTO;
import org.bgm.productservice.dtos.LoginRequest;
import org.bgm.productservice.dtos.ProductDTO;
import org.bgm.productservice.model.Category;
import org.bgm.productservice.repository.CategoryRepository;
import org.bgm.productservice.security.models.User;
import org.bgm.productservice.security.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityIntegrationTest {

    private static final String ADMIN_USERNAME = "admin@test.com";
    private static final String ADMIN_PASSWORD = "Admin1234";
    private static final String USER_USERNAME = "user@test.com";
    private static final String USER_PASSWORD = "User1234";
    private static final String OAUTH_CLIENT_ID = "test-client";
    private static final String OAUTH_CLIENT_SECRET = "secret";

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        userRepository.deleteAll();
        seedUser(ADMIN_USERNAME, ADMIN_PASSWORD, "Admin");
        seedUser(USER_USERNAME, USER_PASSWORD, "USER");
        seedCategory("Electronics");
        seedOAuthClient();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void getProducts_withoutJwt_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/products/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCatalog_withoutJwt_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/catalog/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withValidCredentials_returnsJwt() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest(ADMIN_USERNAME, ADMIN_PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    void getProducts_withLoginJwt_returnsOk() throws Exception {
        String token = obtainTokenViaLogin(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/products/")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void getCatalog_withLoginJwt_returnsOk() throws Exception {
        String token = obtainTokenViaLogin(USER_USERNAME, USER_PASSWORD);

        mockMvc.perform(get("/catalog/")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void createProduct_withoutAdminAuthority_returnsForbidden() throws Exception {
        String token = obtainTokenViaLogin(USER_USERNAME, USER_PASSWORD);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("Test Product");
        productDTO.setPrice(100);
        productDTO.setDescription("Test product");
        productDTO.setCategory("Electronics");

        mockMvc.perform(post("/product")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_withAdminJwt_returnsOk() throws Exception {
        String token = obtainTokenViaLogin(ADMIN_USERNAME, ADMIN_PASSWORD);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("Admin Product");
        productDTO.setPrice(200);
        productDTO.setDescription("Test product");
        productDTO.setCategory("Electronics");

        mockMvc.perform(post("/product")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Admin Product"));
    }

    @Test
    void oauth2ClientCredentials_withJwt_canAccessProducts() throws Exception {
        String token = obtainTokenViaOAuth2ClientCredentials();

        mockMvc.perform(get("/products/")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void oauth2ClientCredentials_withJwt_canAccessCatalog() throws Exception {
        String token = obtainTokenViaOAuth2ClientCredentials();

        mockMvc.perform(get("/catalog/")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void oauth2ClientCredentials_withoutAdmin_cannotCreateProduct() throws Exception {
        String token = obtainTokenViaOAuth2ClientCredentials();

        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("OAuth Product");
        productDTO.setPrice(50);
        productDTO.setDescription("Test product");
        productDTO.setCategory("Electronics");

        mockMvc.perform(post("/product")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCategory_withAdminJwt_returnsOk() throws Exception {
        String token = obtainTokenViaLogin(ADMIN_USERNAME, ADMIN_PASSWORD);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Books");
        categoryDTO.setDescription("Book catalog");

        mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Books"))
                .andExpect(jsonPath("$.description").value("Book catalog"));
    }

    @Test
    void createCategory_withoutAdmin_returnsForbidden() throws Exception {
        String token = obtainTokenViaLogin(USER_USERNAME, USER_PASSWORD);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Sports");
        categoryDTO.setDescription("Sports catalog");

        mockMvc.perform(post("/catalog")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateCategory_withAdminJwt_returnsOk() throws Exception {
        String token = obtainTokenViaLogin(ADMIN_USERNAME, ADMIN_PASSWORD);
        long categoryId = categoryRepository.findCategoryByNameIgnoreCase("Electronics").orElseThrow().getId();

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Electronics");
        categoryDTO.setDescription("Updated electronics catalog");

        mockMvc.perform(put("/catalog/" + categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Updated electronics catalog"));
    }

    @Test
    void updateCategory_withoutAdmin_returnsForbidden() throws Exception {
        String token = obtainTokenViaLogin(USER_USERNAME, USER_PASSWORD);
        long categoryId = categoryRepository.findCategoryByNameIgnoreCase("Electronics").orElseThrow().getId();

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Electronics");
        categoryDTO.setDescription("Should not update");

        mockMvc.perform(put("/category/" + categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isForbidden());
    }

    private String obtainTokenViaLogin(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(username, password))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("accessToken").asText();
    }

    private String obtainTokenViaOAuth2ClientCredentials() throws Exception {
        MvcResult result = mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic(OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("grant_type=client_credentials&scope=api.read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("access_token").asText();
    }

    private void seedUser(String username, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setAccountExpired(false);
        user.setCredentialsExpired(false);
        user.setAccountLocked(false);
        user.setCreatedBy("test");
        user.setUpdatedBy("test");
        user.setRoles(new String[]{role});
        userRepository.save(user);
    }

    private void seedCategory(String name) {
        if (categoryRepository.findCategoryByNameIgnoreCase(name).isPresent()) {
            return;
        }
        Category category = new Category();
        category.setName(name);
        category.setDescription("Test category");
        category.setCreatedBy("test");
        category.setUpdatedBy("test");
        categoryRepository.save(category);
    }

    private void seedOAuthClient() {
        RegisteredClient existing = registeredClientRepository.findByClientId(OAUTH_CLIENT_ID);
        if (existing != null) {
            return;
        }

        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(OAUTH_CLIENT_ID)
                .clientSecret(passwordEncoder.encode(OAUTH_CLIENT_SECRET))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("api.read")
                .build();

        registeredClientRepository.save(client);
    }
}
