package spring.schedule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author thinh
 *
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

	/**
	 * 「/login」というURLからloginForm.htmlを呼び出す
	 */
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("loginForm");
	}
}