package com.cg.services;

import com.cg.configs.ProviderConfiguration;
import com.cg.dto.LoanDto;
import com.github.jknack.handlebars.Template;
import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import com.twilio.type.PhoneNumber;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import com.github.jknack.handlebars.Handlebars;

import java.util.Map;

@Service
@Slf4j
public class NotificationService {

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;
    @Value("${twilio.account.auth_token}")
    private String AUTH_TOKEN;
    @Value("${twilio.account.from}")
    private String FROM;

    @Value("${default.notification.type:SMS}")
    private String defaultNotificationType;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private Handlebars handlebars;

    @Autowired
    private TwilioRestClient twilioRestClient;

    @Autowired
    private ProviderConfiguration providerConfiguration;


    private String smsBody = "Hi %s,\n Your loan number: %d has been updated with status is: %s";
    private String smsBodyIfApproved = "Hey! \nHi %s,\n Your loan number: %d has been %s. We Initiated NFFT amount will be credited with in 3 working days";

    private String smsBodyIfCancelled = "Hey we are sorry %s,\n Your loan number: %d and type: %s has been %s for some reason. Please call back us for any further assistance";

    public void sendNotification(LoanDto loanDto){
        if(!ObjectUtils.isEmpty(loanDto)){
            if(defaultNotificationType.equalsIgnoreCase("EMAIL")){
                Map<String, Object> templateData = new HashMap<>();
                templateData.put("name", loanDto.getCustomerDto().getFirstname());
                templateData.put("status", loanDto.getStatus());
                templateData.put("loanAmount",loanDto.getLoanAmount());

                Template template = null;
                try {
                    if(loanDto.getStatus().equalsIgnoreCase("CANCELLED")){
                        template = handlebars.compile("cancelled-email");
                    }else{
                        template = handlebars.compile("email");
                    }
                    String templateString = template.apply(templateData);
                    boolean isSent = sendEmail(new String[]{loanDto.getCustomerDto().getEmail()}, templateString,
                            providerConfiguration.getUsername(), providerConfiguration.getFromName()
                            , "Loan Application Status");
                    log.info("email status {}", isSent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else{
                String body = "";
                if(loanDto.getStatus().equalsIgnoreCase("APPROVED")){
                    body = smsBodyIfApproved.formatted(loanDto.getCustomerDto().getFirstname(), loanDto.getLoanId(), loanDto.getStatus());
                }else if(loanDto.getStatus().equalsIgnoreCase("CANCELLED")){
                    body = smsBodyIfCancelled.formatted(loanDto.getCustomerDto().getFirstname(), loanDto.getLoanId(),
                            loanDto.getLoanType(), loanDto.getStatus());
                }else{
                    body = smsBody.formatted(loanDto.getCustomerDto().getFirstname(), loanDto.getLoanId(), loanDto.getStatus());
                }
                sendSMS(String.valueOf(loanDto.getCustomerDto().getMobileNum()), body);
            }
        }
    }
    public boolean sendEmail(String[] to, String body, String from, String fromName, String subject) throws Exception {

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(from, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setSentDate(new Date());

        boolean isSent = false;
        try {
            emailSender.send(mimeMessage);
            isSent = true;
        } catch (Exception ex) {
            log.error("Sending e-mail error: {}", ex.getMessage());
        }
        return isSent;
    }

    public boolean sendSMS(String toPhnNumber, String body){

        boolean isSent = false;
        try{
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message message = Message.creator(
                    new PhoneNumber("+91"+toPhnNumber),
                    new PhoneNumber(FROM),
                    body
            ).create(twilioRestClient);
            log.info("response {}", message.getStatus().name());
            isSent = true;
        }catch (Exception ignored){
            log.info(String.valueOf(ignored));
        }
        return isSent;
    }
}
