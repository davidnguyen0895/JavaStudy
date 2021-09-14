package spring.schedule.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.DefaultHttpFirewall;

import spring.schedule.service.UserService;

/**
 * SpringSecurityを利用するための設定クラス ログイン処理でのパラメータ、画面遷移や認証処理でのデータアクセス先を設定する
 * 
 * @author thinh
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserService userDetailsService;

	// フォームの値と比較するDBから取得したパスワードは暗号化されているのでフォームの値も暗号化するために利用
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 認可設定を無視するリクエストを設定 静的リソース(images,css)を認可処理の対象から除外する
	 * 
	 * @param web
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		// org.springframework.security.web.firewall.RequestRejectedException:
		// The request was rejected because the URL contained a potentially malicious
		// String ";"
		// というエラーログがコンソールに出力されるため、下記を追加
		DefaultHttpFirewall firewall = new DefaultHttpFirewall();
		web.httpFirewall(firewall);
		web.ignoring().antMatchers("/images/**", "/css/**");
	}

	/**
	 * 認証時に利用するデータソースを定義する設定メソッド
	 * ここではDBから取得したユーザ情報をuserDetailsServiceへセットすることで認証時の比較情報としている
	 * 
	 * @param auth
	 * @throws Exception
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		// ユーザー名「user」、パスワード「pass」が入力されたらログイン可能とする
		// パスワードエンコーダーを利用しないようにするため、パスワードの先頭に{noop}を
		// 指定している
		auth.inMemoryAuthentication().withUser("username").password("{noop}password").roles("USER");
	}

	/**
	 * SpringSecurityによる認証を設定
	 * 
	 * @param http HttpSecurityオブジェクト
	 * @throws Exception 例外
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().authenticated().and().formLogin().loginPage("/login") // ログインページはコントローラを経由しないのでViewNameとの紐付けが必要
				.loginProcessingUrl("/sign_in") // フォームのSubmitURL、このURLへリクエストが送られると認証処理が実行される
				.usernameParameter("username") // リクエストパラメータのusername属性を明示
				.passwordParameter("pass")// リクエストパラメータのpassword属性を明示
				.successForwardUrl("/index").failureUrl("/login?error").permitAll().and().exceptionHandling()
				.accessDeniedPage("/error").and().logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout")
				.permitAll();
	}
}
