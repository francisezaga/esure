package com.egroupx.esure.services;

import com.egroupx.esure.model.EmailCustomer;
import com.egroupx.esure.model.life.Member;
import com.egroupx.esure.model.medical_aid.MedicalAidMemberDetails;
import com.egroupx.esure.repository.CustomerRepository;
import com.egroupx.esure.repository.LifeInsuranceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${egroupx.email.host}")
    private String emailHost;

    @Value("${egroupx.email.port}")
    private String emailPort;

    @Value("${egroupx.email.sender}")
    private String senderEmail;

    @Value("${egroupx.email.recipient}")
    private String recepientEmail;

    @Value("${egroupx.email.username}")
    private String emailUsername;

    @Value("${egroupx.email.password}")
    private String emailPassword;

    @Value("${egroupx.services.medicalAid.pfisterEmailAddress}")
    private String pfisterEmailAddress;

    @Value("${egroupx.services.medicalAid.esureEmailAddress}")
    private String esureEmailAddress;

    @Value("${egroupx.email.backgroundImage}")
    private String esureEmailBackgroundImg;

    @Value("${egroupx.email.logo}")
    private String esureEmailLogo;

    private final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    private final CustomerRepository customerRepository;

    private final LifeInsuranceRepository lifeInsuranceRepository;

    public EmailService(CustomerRepository customerRepository, LifeInsuranceRepository lifeInsuranceRepository) {
        this.customerRepository = customerRepository;
        this.lifeInsuranceRepository = lifeInsuranceRepository;
    }


    public Mono<String> sendFSPQuotationNotificationEmail(EmailCustomer customer, String subject) {

       Properties props = new Properties();
        props.put("mail.smtp.host", emailHost);
        props.put("mail.smtp.port", emailPort);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPassword);
                    }
                });
        try {
            Transport transport = session.getTransport("smtp");

            MimeMessage message = new MimeMessage(session); // email message

            message.setFrom(new InternetAddress(senderEmail)); // setting header fields

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recepientEmail));

            message.setSubject(subject); // subject line

            String fullName = customer.getFullNames()==null?"":customer.getFullNames();
            Long fspQuotationRef = customer.getFspQuoteRefId()==null?-1L:customer.getFspQuoteRefId();
            String code = customer.getCode()==null?"":customer.getCode();
            String number = customer.getNumber()==null?"":customer.getNumber();
            String emailAddress = customer.getLine_1()==null?"":customer.getLine_1();
            String cellNumber = code+number;
            String body = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>eSure Medical Aid Lead</title>\n" +
                    "    <link href='https://fonts.googleapis.com/css?family=Poppins' rel='stylesheet'>\n"+
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-size: 22px;"+
                    "            padding: 0;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "        }\n" +
                    "        .email-container {\n" +
                    "            margin: 0 auto;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "            background-image: url('"+esureEmailBackgroundImg+"');"+
                    "            background-repeat: no-repeat;"+
                    "            background-position: center right;"+
                    "            background-size: 300px;"+
                    "            padding: 50px;"+
                    "        }\n" +
                    "        .header {\n" +
                    "            font-size: 20px;\n" +
                    "            font-weight: bold;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        .content {\n" +
                    "            font-size: 14px;\n" +
                    "            line-height: 1.6;\n" +
                    "            color: #333;\n" +
                    "        }\n" +
                    "        .content h3 {\n" +
                    "            font-size: 16px;\n" +
                    "            font-weight: bold;\n" +
                    "            margin-top: 10px;\n" +
                    "        }\n" +
                    "        .content p {\n" +
                    "            margin: 0px !important;\n" +
                    "        }\n" +
                    "        .highlight {\n " +
                    "             margin-bottom:15px;" +
                    "        }\n" +
                    "        .footer {\n" +
                    "            margin-top: 30px;\n" +
                    "            font-size: 12px;\n" +
                    "            color: #999;\n" +
                    "        }\n" +
                    "        .esure-logo {\n" +
                    "            color: #b6c634;\n" +
                    "            font-weight: bold;\n" +
                    "        }\n" +
                    "        .p-space, .intro{\n" +
                    "            margin-top: 30px;\n" +
                    "            margin-bottom: 30px;\n" +
                    "        }\n" +
                    "        .p-space-top{\n" +
                    "            margin-top: 20px;\n" +
                    "        }\n" +
                    "        .best-regards {\n" +
                    "            color: #9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-team {\n" +
                    "            color:#9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-logo {\n" +
                    "            color:#9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-moto {\n" +
                    "            font-weight:600;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body style=\"margin:30px;box-shadow: 1px 1px 1px 3px #9B9EA8;\">\n" +
                    "    <div class=\"email-container\">\n" +
                    "        <div class=\"header\">\n" +
                    "    <p>Dear eSure Support Team,</p>\n" +
                    "   </div>\n" +
                    "        <div class=\"content\">\n" +
                    "           <div class=\"intro\">"+
                    "    <p>Please find the details of a customer who requires follow-up support:</p>\n" +
                    "   </div>\n" +
                    "   <p><strong>Name:</strong></p> "+
                    "  <div class=\"highlight\">"+fullName+"</div>\n" +
                    "   <p><strong>Contact Number:</strong></p>\n" +
                    "   <div class=\"highlight\">"+cellNumber+"</div>\n" +
                    "   <p><strong>Email Address:</strong></p>" +
                    " <a href=\"mailto:"+emailAddress+">"+
                    "  <div class=\"highlight\">"+emailAddress+"</div>" +
                    "</a>\n" +
                    "   <div class=\"p-space-top\"><strong>Quote ref:</strong></div> "+
                    "  <div class=\"highlight\">"+fspQuotationRef+"</div>"+
                    "     <div class=\"p-space\">\n" +
                    "    <p>Kindly reach out to the customer directly to assist with their query or concern. Please ensure a prompt response to ensure their needs are met. Thank you for your support and attention to this matter.</p>\n" +
                    "    </div>\n"+
                    "            <div class=\"best-regards\">Best regards,</div>\n" +
                    "            <div class=\"esure-team\">eSure Team</div>\n" +
                    "            <div class=\"esure-moto\">Insuring Your Tomorrow, Today</div>\n" +
                    "            <img src=\""+esureEmailLogo+"\"/>"+
                    "            <div class=\"footer\">\n" +
                    "                <p><i>eSure is a Juristic Representative of Royale Crowns Financial Services, an authorized Financial Services Provider (FSP No.: 52845). All insurance products and services are offered under the regulatory framework of Royale Crowns Financial Services.</i></p>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>\n";
            // actual mail body
            message.setContent(body,"text/html"
            );

            // Send message
            transport.connect();
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
            transport.close();

            LOG.info(MessageFormat.format("Customer onboarding email notification successfully send for user {0}", emailAddress));
            return Mono.just("Email Send");
        }catch (MessagingException mex){
            LOG.error(MessageFormat.format("Customer onboarding email notification failed to send for user {0} error {1}",customer.getLine_1(),mex.getMessage()));
            return Mono.just("Email not send");
        }
    }

    public Mono<String> sendFSPInsuranceWelcomeEmail(EmailCustomer customer, String subject) {

        Properties props = new Properties();
        props.put("mail.smtp.host", emailHost);
        props.put("mail.smtp.port", emailPort);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPassword);
                    }
                });
        try {
            Transport transport = session.getTransport("smtp");

            MimeMessage message = new MimeMessage(session); // email message

            message.setFrom(new InternetAddress(senderEmail)); // setting header fields

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(customer.getLine_1()));

            message.setSubject(subject); // subject line

            String fullName = customer.getFullNames()==null?"":customer.getFullNames();
            Long fspQuotationRef = customer.getFspQuoteRefId()==null?-1L:customer.getFspQuoteRefId();
            String emailAddress = customer.getLine_1()==null?"":customer.getLine_1();
            String emailBody = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>eSure Medical Aid Lead</title>\n" +
                    "    <link href='https://fonts.googleapis.com/css?family=Poppins' rel='stylesheet'>\n"+
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-size: 22px;"+
                    "            padding: 0;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "        }\n" +
                    "        .email-container {\n" +
                    "            margin: 0 auto;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "            background-image: url('"+esureEmailBackgroundImg+"');"+
                    "            background-repeat: no-repeat;"+
                    "            background-position: center right;"+
                    "            background-size: 300px;"+
                    "            padding: 50px;"+
                    "        }\n" +
                    "        .header {\n" +
                    "            font-size: 20px;\n" +
                    "            font-weight: bold;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        .content {\n" +
                    "            font-size: 14px;\n" +
                    "            line-height: 1.6;\n" +
                    "            color: #333;\n" +
                    "        }\n" +
                    "        .content h3 {\n" +
                    "            font-size: 16px;\n" +
                    "            font-weight: bold;\n" +
                    "            margin-top: 10px;\n" +
                    "        }\n" +
                    "        .content p {\n" +
                    "            margin: 0px !important;\n" +
                    "        }\n" +
                    "        .highlight {\n " +
                    "             margin-bottom:0px;" +
                    "        }\n" +
                    "        .footer {\n" +
                    "            margin-top: 30px;\n" +
                    "            font-size: 12px;\n" +
                    "            color: #999;\n" +
                    "        }\n" +
                    "        .esure-logo {\n" +
                    "            color: #b6c634;\n" +
                    "            font-weight: bold;\n" +
                    "        }\n" +
                    "        .p-space, .intro{\n" +
                    "            margin-top: 30px;\n" +
                    "            margin-bottom: 30px;\n" +
                    "        }\n" +
                    "        .p-space-top{\n" +
                    "            margin-top: 20px;\n" +
                    "        }\n" +
                    "        .best-regards {\n" +
                    "            color: #9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-team {\n" +
                    "            color:#9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-logo {\n" +
                    "            color:#9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-moto {\n" +
                    "            font-weight:600;\n" +
                    "        }\n" +
                    "        .section-header{\n" +
                    "            margin-top:20px;\n" +
                    "            font-weight:600;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body style=\"margin:30px;box-shadow: 1px 1px 1px 3px #9B9EA8;\">\n" +
                    "    <div class=\"email-container\">\n" +
                    "        <div class=\"header\">\n" +
                    "    <p>Hi "+fullName+",</p>\n" +
                    "   </div>\n" +
                    "        <div class=\"content\">\n" +
                    "           <div class=\"intro\">"+
                    "    <p>Thank you for submitting your information. Your financial security is our top priority, and we are here to guide you every step of the way.</p>\n" +
                    "      </div>\n" +
                    "    <div class=\"section-header\">Next Steps:</div>\n" +
                    "    <div class=\"p-space-top\">One of our dedicated agents will be in touch soon to discuss your application and ensure you have everything you need.</div>\n" +
                    "\n" +
                    "    <div class=\"section-header\"><div>Your Details:</div></div>\n" +
                    "    <p><strong>Quote Ref:</strong></p>" +
                    "     <div class=\"highlight\">"+fspQuotationRef+"</div>\n" +
                    "\n<div class=\"p-space-top\"" +
                    "    <p><strong>Please contact our customer support centre on:</strong></p>\n" +
                    "</div>\n" +
                    "        <div><strong>Phone:</strong></div>" +
                    "       <div class=\"highlight\"> 010 006 7394</div>\n" +
                    "        <div><strong>Email:</strong></div>" +
                    "        <a href=\"mailto:support@esurecover.co.za\">\n" +
                    "          <div class=\"highlight\">support@esurecover.co.za</div></a\n" +
                    "        >\n" +
                    "        <div><strong>WhatsApp support:</strong></div>" +
                    "         <div class=\"highlight\"> 068 148 8912</div>\n" +
                    "     <div class=\"p-space\">\n" +
                    "    <p>We appreciate your trust in us and look forward to helping you protect what matters most.</p>\n" +
                    "     </div>\n" +
                    "        <p class=\"best-regards\">Best regards,</p>\n" +
                    "        <p class=\"esure-team\">eSure Team</p>\n" +
                    "        <p class=\"esure-moto\">Insuring Your Tomorrow, Today</p>\n" +
                    "            <img src=\""+esureEmailLogo+"\"/>"+
                    "        <div class=\"footer\">\n" +
                    "          <p>\n" +
                    "            <i>\n" +
                    "              eSure is a Juristic Representative of Royale Crowns Financial\n" +
                    "              Services, an authorized Financial Services Provider (FSP No.:\n" +
                    "              52845). All insurance products and services are offered under the\n" +
                    "              regulatory framework of Royale Crowns Financial Services.</i>\n" +
                    "          </p>\n" +
                    "        </div>\n" +
                    "      </div>\n" +
                    "    </div>\n" +
                    "  </body>\n" +
                    "</html>\n";

            // actual mail body
            message.setContent(emailBody,"text/html"
            );

            // Send message
            transport.connect();
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
            transport.close();

            LOG.info(MessageFormat.format("Customer onboarding email notification successfully send for user {0}", emailAddress));
            return Mono.just("Email Send");
        }catch (MessagingException mex){
            LOG.error(MessageFormat.format("Customer onboarding email notification failed to send for user {0} error {1}",customer.getLine_1(),mex.getMessage()));
            return Mono.just("Email not send");
        }
    }

    public Mono<String> sendEmailForFuneralCover(Member member, String subject) {

        Properties props = new Properties();
        props.put("mail.smtp.host", emailHost);
        props.put("mail.smtp.port", emailPort);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPassword);
                    }
                });
        try {
            Transport transport = session.getTransport("smtp");

            MimeMessage message = new MimeMessage(session); // email message

            message.setFrom(new InternetAddress(senderEmail)); // setting header fields

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recepientEmail));

            message.setSubject(subject); // subject line

            String firstName = member.getFirstName()==null?"": member.getFirstName();
            String lastName = member.getSurname()==null?"": member.getSurname();
            String idNumber = member.getIdNumber()==null?"":member.getIdNumber();
            String fullName = firstName.concat(" ").concat(lastName);
            String number = member.getCellNumber()==null?"":member.getCellNumber();
            String emailAddress = member.getEmail()==null?"":member.getEmail();
            String endBody = "<br><h3>Thank you</h3>"+
                    "<br><h3>Regards</h3>"+
                    "<body/>"+
                    "</html>";

            // actual mail body
            message.setContent("<html><body>" +
                    "<head>" +
                    "<style>" +
                    "label{ " +
                    "font-weight:bold;"+
                    "width:300px;"+
                    "color:#212529;"+
                    "}"+
                    "</style>" +
                    "</head>"+
                    "<h2>Good day, there is a "+subject.toLowerCase()+" with below details</h2>"+
                    "<label>Name:</label>" + fullName+"<br>"+
                    "<label>ID Number:</label>" + idNumber+"<br>"+
                    "<label>Contact Number:</label>" + number+"<br>"+
                    "<label>Email:</label>" + emailAddress+"<br>"+
                    endBody
                    ,"text/html"
            );

            // Send message
            transport.connect();
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
            transport.close();

            LOG.info(MessageFormat.format("Customer onboarding email notification successfully send for user {0}", emailAddress));
            return Mono.just("Email Send");
        }catch (MessagingException mex){
            LOG.error(MessageFormat.format("Customer onboarding email notification failed to send for user {0} error {1}",member.getEmail(),mex.getMessage()));
            return Mono.just("Email not send");
        }
    }

    public Mono<String> sendFuneralCoverWelcomeEmail(Member member, String subject) {

        Properties props = new Properties();
        props.put("mail.smtp.host", emailHost);
        props.put("mail.smtp.port", emailPort);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPassword);
                    }
                });
        try {
            Transport transport = session.getTransport("smtp");

            MimeMessage message = new MimeMessage(session); // email message

            message.setFrom(new InternetAddress(senderEmail)); // setting header fields

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(member.getEmail()));

            message.setSubject(subject); // subject line

            String firstName = member.getFirstName()==null?"": member.getFirstName();
            String lastName = member.getSurname()==null?"": member.getSurname();
            String fullName = firstName.concat(" ").concat(lastName);
            String number = member.getCellNumber()==null?"":member.getCellNumber();
            String emailAddress = member.getEmail()==null?"":member.getEmail();
            String endBody = "<br><h3>Thank you</h3>"+
                    "<br><h3>Regards</h3>"+
                    "<body/>"+
                    "</html>";

            // actual mail body
            message.setContent("<html><body>" +
                            "<head>" +
                            "<style>" +
                            "label{ " +
                            "font-weight:bold;"+
                            "width:300px;"+
                            "color:#212529;"+
                            "}"+
                            "</style>" +
                            "</head>"+
                            "<h2>Hi "+fullName+"</h2>"+
                            "<p><h2>Welcome to eSure Life Cover!</h2><br>Thank for you registering with eSure. One of our agencies will contact you regarding your application.<br>"+
                            "<label>Name:</label>" + fullName+"<br>"+
                            "<label>Contact Number:</label>" + number+"<br>"+
                            "<label>Email:</label>" + emailAddress+"<br>"+
                            endBody
                    ,"text/html"
            );

            // Send message
            transport.connect();
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
            transport.close();

            LOG.info(MessageFormat.format("Customer onboarding email notification successfully send for user {0}", emailAddress));
            return Mono.just("Email Send");
        }catch (MessagingException mex){
            LOG.error(MessageFormat.format("Customer onboarding email notification failed to send for user {0} error {1}",member.getEmail(),mex.getMessage()));
            return Mono.just("Email not send");
        }
    }

    public Mono<String> sendQuotationEmailForMedicalAid(MedicalAidMemberDetails memberDetails, String subject,String receiverName,String recepientEmail) {

        Properties props = new Properties();
        props.put("mail.smtp.host", emailHost);
        props.put("mail.smtp.port", emailPort);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPassword);
                    }
                });
        try {
            Transport transport = session.getTransport("smtp");

            MimeMessage message = new MimeMessage(session); // email message

            message.setFrom(new InternetAddress(senderEmail)); // setting header fields
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recepientEmail));

            message.setSubject(subject);

            int adultsCount = memberDetails.getAdultsCount();
            int childrenCount = memberDetails.getChildrenCount();
            String firstName = memberDetails.getFirstName()==null?"":memberDetails.getFirstName();
            String lastName = memberDetails.getLastName()==null?"":memberDetails.getLastName();
            String fullName = firstName.concat(" ").concat(lastName);
            String dateOfBirth =  memberDetails.getDateOfBirth()==null?"":memberDetails.getDateOfBirth().toString();
            String emailAddress = memberDetails.getEmail()==null?"":memberDetails.getEmail();
            String phoneNumber = memberDetails.getPhoneNumber()==null?"":memberDetails.getPhoneNumber();
            String hasMedicalAid = memberDetails.isHasMedicalAid()?"Yes":"No";
            String nameOfMedicalAidProvider = memberDetails.getNameOfMedicalAidProvider()==null?"":memberDetails.getNameOfMedicalAidProvider();
            String isGrossIncomeMoreThan14k = memberDetails.isGrossIncomeMoreThan14K()?"Yes":"No";
            String budgetedAmount = memberDetails.getBudgetedAmount()==null?"":memberDetails.getBudgetedAmount();
            String medicalPriority = memberDetails.getMedicalPriority()==null?"":memberDetails.getMedicalPriority();
            String isNetIncomeMoreThan14k = memberDetails.isNetIncomeMoreThan14k()?"Yes":"No";
            String hasOrDependentHasChronicMedicationRequirements = memberDetails.isMemberOrDependentHasChronicMedRequirements()?"Yes":"No";

            String medicalAidProviderElem = memberDetails.isHasMedicalAid()?"<strong>Medical Aid Provider:</strong><div class=\"highlight\">"+nameOfMedicalAidProvider+"</div>\n":"";

            String body =  "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>eSure Medical Aid Lead</title>\n" +
                    "    <link href='https://fonts.googleapis.com/css?family=Poppins' rel='stylesheet'>\n"+
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-size: 22px;"+
                    "            padding: 0;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "        }\n" +
                    "        .email-container {\n" +
                    "            margin: 0 auto;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "            background-image: url('"+esureEmailBackgroundImg+"');"+
                    "            background-repeat: no-repeat;"+
                    "            background-position: center right;"+
                    "            background-size: 300px;"+
                    "            padding: 50px;"+
                    "        }\n" +
                    "        .header {\n" +
                    "            font-size: 20px;\n" +
                    "            font-weight: bold;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        .content {\n" +
                    "            font-size: 14px;\n" +
                    "            line-height: 1.6;\n" +
                    "            color: #333;\n" +
                    "        }\n" +
                    "        .content h3 {\n" +
                    "            font-size: 16px;\n" +
                    "            font-weight: bold;\n" +
                    "            margin-top: 10px;\n" +
                    "        }\n" +
                    "        .content p {\n" +
                    "            margin: 0px !important;\n" +
                    "        }\n" +
                    "        .highlight {\n " +
                    "             margin-bottom:15px;" +
                    "        }\n" +
                    "        .footer {\n" +
                    "            margin-top: 30px;\n" +
                    "            font-size: 12px;\n" +
                    "            color: #999;\n" +
                    "        }\n" +
                    "        .esure-logo {\n" +
                    "            color: #b6c634;\n" +
                    "            font-weight: bold;\n" +
                    "        }\n" +
                    "        .reach-out, .welcome{\n" +
                    "            margin-top: 30px;\n" +
                    "            margin-bottom: 30px;\n" +
                    "        }\n" +
                    "        .best-regards {\n" +
                    "            color: #9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-team {\n" +
                    "            color:#9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-logo {\n" +
                    "            color:#9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-moto {\n" +
                    "            font-weight:600;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body style=\"margin:30px;box-shadow: 1px 1px 1px 3px #9B9EA8;\">\n" +
                    "\n" +
                    "    <div class=\"email-container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            Dear "+receiverName+",\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "           <div class=\"welcome\"\n>"+
                    "            <p>We have received a new Medical Aid lead from eSure. Please find the customer's details below:</p>\n" +
                    "           </div>\n" +
                    "            <p><strong>Name:</strong></p>\n" +
                    "            <div class=\"highlight\">"+fullName+"</div>\n" +
                    "            <p><strong>Number of adults:</strong>\n" +
                    "            <div class=\"highlight\">"+adultsCount+"</div>\n" +
                    "            <p><strong>Number of children:</strong>\n" +
                    "            <div class=\"highlight\">"+childrenCount+"</div>\n" +
                    "            <p><strong>Date of birth:</strong>\n" +
                    "            <div class=\"highlight\">"+dateOfBirth+"</div>\n" +
                    "            <p><strong>Email Address:</strong>\n" +
                    "            <div class=\"highlight\">"+emailAddress+"</div>\n" +
                    "            <p><strong>Phone Number:</strong>\n" +
                    "            <div class=\"highlight\">"+phoneNumber+"</div>\n" +
                    "            <p><strong>Has Medical Aid:</strong>\n" +
                    "            <div class=\"highlight\">"+hasMedicalAid+"</div>\n" +
                                    medicalAidProviderElem +
                    "            <p><strong>Income Before Deductions > R:14 000.00</strong>\n" +
                    "            <div class=\"highlight\">"+isGrossIncomeMoreThan14k+"</div>\n" +
                    "            <p><strong>Budget for Medical Cover:</strong>\n" +
                    "            <div class=\"highlight\">R"+budgetedAmount+"</div>\n" +
                    "            <p><strong>Main Medical Priority:</strong>\n" +
                    "            <div class=\"highlight\">"+medicalPriority+"</div>\n" +
                    "            <p><strong>Chronic Requirements:</strong>\n" +
                    "            <div class=\"highlight\">"+hasOrDependentHasChronicMedicationRequirements+"</div>\n" +
                    "            <div class=\"reach-out\">" +
                    "            <p>Please reach out to assist the customer with their Medical Aid needs.</p>\n" +
                    "            </div>" +
                    "            <p class=\"best-regards\">Best regards,</p>\n" +
                    "            <p class=\"esure-team\">eSure Team</p>\n" +
                    "            <p class=\"esure-moto\">Insuring Your Tomorrow, Today</p>\n" +
                    "            <img src=\""+esureEmailLogo+"\"/>"+
                    "            <div class=\"footer\">\n" +
                    "                <p><i>eSure is a Juristic Representative of Royale Crowns Financial Services, an authorized Financial Services Provider (FSP No.: 52845). All insurance products and services are offered under the regulatory framework of Royale Crowns Financial Services.</i></p>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>\n";

            // actual mail body
            message.setContent(body,"text/html"
            );

            // Send message
            transport.connect();
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
            transport.close();

            LOG.info(MessageFormat.format("Member medical aid details email notification successfully send for user {0}", emailAddress));
            return Mono.just("Email Send to "+ receiverName);
        }catch (MessagingException mex){
            LOG.error(MessageFormat.format("Member medical aid details email notification failed to send for user {0} error {1}",memberDetails.getEmail(),mex.getMessage()));
            return Mono.just("Email not send to "+ receiverName);
        }
    }

    public Mono<String> sendWelcomeEmailForMedicalAid(MedicalAidMemberDetails memberDetails, String subject) {

        Properties props = new Properties();
        props.put("mail.smtp.host", emailHost);
        props.put("mail.smtp.port", emailPort);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPassword);
                    }
                });
        try {
            Transport transport = session.getTransport("smtp");

            MimeMessage message = new MimeMessage(session); // email message

            message.setFrom(new InternetAddress(senderEmail)); // setting header fields

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(memberDetails.getEmail()));

            message.setSubject(subject);

            String emailAddress = memberDetails.getEmail()==null?"":memberDetails.getEmail();
            String firstName = memberDetails.getFirstName()==null?"":memberDetails.getFirstName();
            String lastName = memberDetails.getLastName()==null?"":memberDetails.getLastName();
            String fullName = firstName.concat(" ").concat(lastName);

            String body = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>eSure Medical Aid Lead</title>\n" +
                    "    <link href='https://fonts.googleapis.com/css?family=Poppins' rel='stylesheet'>\n"+
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-size: 22px;"+
                    "            padding: 0;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "        }\n" +
                    "        .email-container {\n" +
                    "            margin: 0 auto;\n" +
                    "            background-color: #f9f9f9;\n" +
                    "            background-image: url('"+esureEmailBackgroundImg+"');"+
                    "            background-repeat: no-repeat;"+
                    "            background-position: center right;"+
                    "            background-size: 300px;"+
                    "            padding: 50px;"+
                    "        }\n" +
                    "        .header {\n" +
                    "            font-size: 20px;\n" +
                    "            font-weight: bold;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        .content {\n" +
                    "            font-size: 14px;\n" +
                    "            line-height: 1.6;\n" +
                    "            color: #333;\n" +
                    "        }\n" +
                    "        .content h3 {\n" +
                    "            font-size: 16px;\n" +
                    "            font-weight: bold;\n" +
                    "            margin-top: 10px;\n" +
                    "        }\n" +
                    "        .content p {\n" +
                    "            margin: 0px !important;\n" +
                    "        }\n" +
                    "        .highlight {\n " +
                    "             margin-bottom:15px;" +
                    "        }\n" +
                    "        .footer {\n" +
                    "            margin-top: 30px;\n" +
                    "            font-size: 12px;\n" +
                    "            color: #999;\n" +
                    "        }\n" +
                    "        .esure-logo {\n" +
                    "            color: #b6c634;\n" +
                    "            font-weight: bold;\n" +
                    "        }\n" +
                    "        .reach-out, .welcome{\n" +
                    "            margin-top: 30px;\n" +
                    "            margin-bottom: 30px;\n" +
                    "        }\n" +
                    "        .best-regards {\n" +
                    "            color: #9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-team {\n" +
                    "            color:#9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-logo {\n" +
                    "            color:#9B9EA8;\n" +
                    "        }\n" +
                    "        .esure-moto {\n" +
                    "            font-weight:600;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body style=\"margin:30px;box-shadow: 1px 1px 1px 3px #9B9EA8;\">\n" +
                    "\n" +
                    "    <div class=\"email-container\">\n" +
                    "        <div class=\"header\">\n" +
                    "  <p>Hi "+fullName+",</p>\n" +
                    "   </div>"+
                    "  \n" +
                    "        <div class=\"content\">\n" +
                    "           <div class=\"welcome\">"+
                    "  <p><strong>Welcome to eSure Medical Aid!</strong></p>\n" +
                    "   </div>"+
                    "  \n" +
                    "  <p>Thank you for submitting your request with eSure. One of our dedicated agents will be in touch shortly to complete your application.</p>\n" +
                    "  <div class=\"reach-out\">\n" +
                    "  <p>For immediate assistance, feel free to reach out to our support team:</p>\n" +
                    "  </div>\n" +
                    "<p><strong>Phone: </strong></p>\n" +
                    "        <div class=\"highlight\">010 006 7394</div>\n" +
                    "        <p><strong>Email: </strong></p>\n" +
                    "        <a href=\"mailto:support@esurecover.co.za\">\n" +
                    "          <div class=\"highlight\">support@esurecover.co.za</div></a\n" +
                    "        >\n" +
                    "        <p><strong>WhatsApp:</strong></p>\n" +
                    "        <div class=\"highlight\">068 148 8912</div>\n" +
                    "        <p class=\"best-regards\">Best regards,</p>\n" +
                    "        <p class=\"esure-team\">eSure Team</p>\n" +
                    "        <p class=\"esure-moto\">Insuring Your Tomorrow, Today</p>\n" +
                    "            <img src=\""+esureEmailLogo+"\"/>"+
                    "        <div class=\"footer\">\n" +
                    "          <p>\n" +
                    "            <i>\n" +
                    "              eSure is a Juristic Representative of Royale Crowns Financial\n" +
                    "              Services, an authorized Financial Services Provider (FSP No.:\n" +
                    "              52845). All insurance products and services are offered under the\n" +
                    "              regulatory framework of Royale Crowns Financial Services.</i>\n" +
                    "          </p>\n" +
                    "        </div>\n" +
                    "      </div>\n" +
                    "    </div>\n" +
                    "  </body>\n" +
                    "</html>\n";

            // actual mail body
            message.setContent(body,"text/html");

            // Send message
            transport.connect();
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
            transport.close();

            LOG.info(MessageFormat.format("Member medical aid details email notification successfully send for user {0}", emailAddress));
            return Mono.just("Email Send");
        }catch (MessagingException mex){
            LOG.error(MessageFormat.format("Member medical aid details email notification failed to send for user {0} error {1}",memberDetails.getEmail(),mex.getMessage()));
            return Mono.just("Email not send");
        }
    }
}
