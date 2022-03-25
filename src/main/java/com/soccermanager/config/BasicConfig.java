package com.soccermanager;

import com.google.common.base.Predicates;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.soccermanager.config.JerseyConfig.EntityManagerFilter.EM_REQUEST_ATTRIBUTE;
import static org.apache.olingo.odata2.api.processor.ODataContext.HTTP_SERVLET_REQUEST_OBJECT;

/**
 * @author akif
 * @since 3/17/22
 */
@Configuration
@EnableSwagger2
@EnableWebMvc
@EnableCaching
public class BasicConfig extends ODataJPAServiceFactory implements WebMvcConfigurer {

	@Bean
	public MessageSourceAccessor getMessageSourceAccessor() {
		return new MessageSourceAccessor(messageSource());
	}

	@Bean
	public MessageSource messageSource() {
		final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");

		return messageSource;
	}

	@Bean
	@Override
	public Validator getValidator() {
		final LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
		factoryBean.setValidationMessageSource(this.messageSource());

		return factoryBean;
	}

	@Bean
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(Objects.requireNonNull(cacheMangerFactory().getObject()));
	}

	@Bean
	public EhCacheManagerFactoryBean cacheMangerFactory() {
		EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
		bean.setConfigLocation(new ClassPathResource("ehcache-config.xml"));
		bean.setShared(true);

		return bean;
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.any())
			.paths(Predicates.not(PathSelectors.regex("/error")))
			.build()
			.apiInfo(metadata())
			.useDefaultResponseMessages(false)
			.securitySchemes(Collections.singletonList(apiKey()))
			.securityContexts(Collections.singletonList(securityContext()))
			.genericModelSubstitutes(Optional.class);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/v2/api-docs", "/v2/api-docs");
		registry.addRedirectViewController("/swagger-resources/configuration/ui", "/swagger-resources/configuration/ui");
		registry.addRedirectViewController("/swagger-resources/configuration/security", "/swagger-resources/configuration/security");
		registry.addRedirectViewController("/swagger-resources", "/swagger-resources");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Override
	public ODataJPAContext initializeODataJPAContext() throws ODataJPARuntimeException {
		ODataJPAContext ctx = getODataJPAContext();
		ODataContext octx = ctx.getODataContext();
		HttpServletRequest request = (HttpServletRequest) octx.getParameter(HTTP_SERVLET_REQUEST_OBJECT);
		EntityManager em = (EntityManager) request.getAttribute(EM_REQUEST_ATTRIBUTE);

		ctx.setEntityManager(em);
		ctx.setPersistenceUnitName("default");
		ctx.setContainerManaged(true);
		return ctx;
	}

	private ApiInfo metadata() {
		return new ApiInfoBuilder()
			.title("Online Soccer Manager")
			.description("An online **Soccer Manager** game where users can create fantasy teams and will be able to sell/buy players among them.")
			.version("1.0.0")
			.contact(new Contact("M. Tahsin Anwar", null, "tahsinanwar42@gmail.com"))
			.build();
	}

	private ApiKey apiKey() {
		return new ApiKey("Authorization", "Authorization", "header");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
			.securityReferences(defaultAuth())
			.forPaths(PathSelectors.any())
			.build();
	}

	private List<SecurityReference> defaultAuth() {
		final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		final AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;

		return Collections.singletonList(new SecurityReference("Authorization", authorizationScopes));
	}
}
