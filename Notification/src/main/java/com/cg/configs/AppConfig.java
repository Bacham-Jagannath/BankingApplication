package com.cg.configs;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.account.auth_token}")
    private String authToken;

    @Value("${twilio.account.from}")
    private String from;
    @Bean
    public Handlebars handlebars(){
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates/emails");
        loader.setSuffix(".html");
        return new Handlebars(loader);
    }

    @Bean
    public TwilioRestClient twilioRestClient() {
        Twilio.init(accountSid, authToken);
        return Twilio.getRestClient();
    }
}
