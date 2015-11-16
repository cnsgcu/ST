package home.playground.configs

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.ViewResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.thymeleaf.spring4.SpringTemplateEngine
import org.thymeleaf.spring4.view.ThymeleafViewResolver
import org.thymeleaf.templateresolver.ServletContextTemplateResolver

/**
 * Created by cuong on 11/14/15.
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = arrayOf("home.playground.resources"))
open class WebMvcConfig : WebMvcConfigurerAdapter()
{
    companion object {
        private val LOGGER = LoggerFactory.getLogger(WebMvcConfig::class.java)
    }

    override public fun addResourceHandlers(registry: ResourceHandlerRegistry)
    {
        LOGGER.info("Configure static resources.")

        registry.addResourceHandler("/resources/**").addResourceLocations("/WEB-INF/resources/")
    }

    /**
     * Configure view resolver
     *
     * @return an instance of ViewResolver
     */
    @Bean
    open public fun viewResolver(): ViewResolver
    {
        LOGGER.info("Configure Thymeleaf view resolver.");

        val thymeleafViewResolver = ThymeleafViewResolver();

        thymeleafViewResolver.templateEngine = templateEngine();
        thymeleafViewResolver.order = 1;

        return thymeleafViewResolver;
    }

    @Bean
    open public fun templateEngine(): SpringTemplateEngine
    {
        val templateEngine = SpringTemplateEngine();

        templateEngine.setTemplateResolver(templateResolver());

        return templateEngine;
    }

    @Bean
    open public fun templateResolver(): ServletContextTemplateResolver
    {
        val resolver = ServletContextTemplateResolver();

        resolver.prefix = "/WEB-INF/views/";
        resolver.suffix = ".html";
        resolver.templateMode = "HTML5";
        resolver.isCacheable = false;

        return resolver;
    }
}