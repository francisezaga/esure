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
import java.time.LocalDate;
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

    private final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    private final CustomerRepository customerRepository;

    private final LifeInsuranceRepository lifeInsuranceRepository;

    public EmailService(CustomerRepository customerRepository, LifeInsuranceRepository lifeInsuranceRepository) {
        this.customerRepository = customerRepository;
        this.lifeInsuranceRepository = lifeInsuranceRepository;
    }


    public Mono<String> sendQuotationNotificationEmail(EmailCustomer customer, String subject) {

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
                    "    <title>Customer Support Request</title>\n" +
                    "</head>\n" +
                    "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">\n" +
                    "    <p>Dear eSure Support Team,</p>\n" +
                    "\n" +
                    "    <p>Please find the details of a customer who requires follow-up support:</p>\n" +
                    "\n" +
                    "<ul>"+
                    "   <li> <p><strong>Name:</strong> "+fullName+"</p></li>\n" +
                    "   <li> <p><strong>Contact Number:</strong> "+cellNumber+"</p></li>\n" +
                    "   <li> <p><strong>Email Address:</strong> <a href=\"mailto:"+emailAddress+"+\">"+emailAddress+"</a></p></li>\n" +
                    "   <li> <p><strong>Quote ref:</strong> "+fspQuotationRef+"</li>"+
                    "</ul>"+
                    "\n" +
                    "    <p>Kindly reach out to the customer directly to assist with their query or concern. Please ensure a prompt response to ensure their needs are met. Thank you for your support and attention to this matter.</p>\n" +
                    "\n" +
                    "    <p>Best regards,<br>\n" +
                    "    eSure Team</p>\n" +
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
                    "    <title>Email</title>\n" +
                    "</head>\n" +
                    "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">\n" +
                    "    <p>Hi "+fullName+",</p>\n" +
                    "\n" +
                    "    <p>Thank you for submitting your information. Your financial security is our top priority, and we’re here to guide you every step of the way.</p>\n" +
                    "\n" +
                    "    <h3>Next Steps:</h3>\n" +
                    "    <p>One of our dedicated agents will be in touch soon to discuss your application and ensure you have everything you need.</p>\n" +
                    "\n" +
                    "    <h3>Your Details:</h3>\n" +
                    "    <p><ul><li><strong>Quote Ref:</strong>"+fspQuotationRef+"</li></ul></p>\n" +
                    "\n" +
                    "    <p>Please contact our customer support centre on:</p>\n" +
                    "\n" +
                    "    <ul>\n" +
                    "        <li>Phone: 010 006 7394</li>\n" +
                    "        <li>Email: <a href=\"mailto:support@esurecover.co.za\">support@esurecover.co.za</a></li>\n" +
                    "        <li>WhatsApp support: 068 148 8912</li>\n" +
                    "    </ul>\n" +
                    "\n" +
                    "    <p>We appreciate your trust in us and look forward to helping you protect what matters most.</p>\n" +
                    "\n" +
                    "    <p>Best regards,<br>\n" +
                    "    The eSure Team<br>\n" +
                    "    <b>Insuring Your Tomorrow, Today</b></p>\n" +
                    "\n" +
                    "    <footer style=\"font-size: 0.9em; color: #777;\">\n" +
                    "        <p><i>eSure is a Juristic Representative of Royale Crowns Financial Services, an authorised Financial Services Provider (FSP No.: 52845). All insurance products and services are offered under the regulatory framework of Royale Crowns Financial Services.</i></p>\n" +
                    "    </footer>\n" +
                    "</body>\n" +
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


    public Mono<String> sendEmailForLifeCover(Member member, String subject) {

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

    public Mono<String> sendLifeCoverWelcomeEmail(Member member, String subject) {

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

            String medicalAidProvider = memberDetails.isHasMedicalAid()?"<p><strong>Name Of Medical Aid Provider:</strong> "+nameOfMedicalAidProvider+"</p>\n":"";

            String body =  "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Customer Support Request</title>\n" +
                    "</head>\n" +
                    "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">\n" +
                    "    <p>Dear "+receiverName+",</p>\n" +
                    "\n" +
                    "    <p>Please find the details of a customer who requires follow-up support:</p>\n" +
                    "\n" +
                    "   <p><strong>Full Name:</strong> "+fullName+"</p>\n" +
                    "   <p><strong>Contact Number:</strong> "+phoneNumber+"</p>\n" +
                    "   <p><strong>Email Address:</strong> <a href=\"mailto:"+emailAddress+"+\">"+emailAddress+"</a></p></li>\n" +
                    "   <p><strong>Date of birth:</strong> "+dateOfBirth+"</p>\n" +
                    "   <p><strong>Number of adults:</strong> "+adultsCount+"</p>\n"+
                    "   <p><strong>Number of children:</strong> "+childrenCount+"</p>\n" +
                    "   <p><strong>Member Has Medical Aid:</strong> "+hasMedicalAid+"</p>\n" +
                        medicalAidProvider+
                    "   <p><strong>Income Before Deductions More than R14 000.00:</strong> "+isGrossIncomeMoreThan14k+"</p>\n" +
                    "   <p><strong>Amount Budgeted For Medical Cover:</strong> R"+budgetedAmount+"</p>\n" +
                    "   <p><strong>Main Medical Priority:</strong> "+medicalPriority+"</p>\n" +
                    "   <p><strong>Member Or Dependent Has Chronic Requirements:</strong> "+hasOrDependentHasChronicMedicationRequirements+"</p>\n" +
                    "    <p>Kindly reach out to the customer directly to assist with their query or concern. Please ensure a prompt response to ensure their needs are met. Thank you for your support and attention to this matter.</p>\n" +
                    "\n" +
                    "    <p>Best regards,<br>\n" +
                    "    eSure Team</p>\n" +
                    "</body>\n" +
                    "</html>\n";;

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
                    "<html>\n" +
                    "<head>\n" +
                    "  <meta charset=\"UTF-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "  <title>Welcome to eSure Medical Aid</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "  <p>Hi "+fullName+",</p>\n" +
                    "  \n" +
                    "  <p>Welcome to eSure Medical Aid!</p>\n" +
                    "  \n" +
                    "  <p>Thank you for submitting your request with eSure. One of our customer support agents will be in touch shortly to complete your application.</p>\n" +
                    "  \n" +
                    "  <p>For immediate assistance, feel free to reach out to our support team:</p>\n" +
                    "  \n" +
                    "  <ul>\n" +
                    "    <li>Phone: 010 006 7394</li>\n" +
                    "    <li>Email: <a href=\"mailto:support@esurecover.co.za\">support@esurecover.co.za</a></li>\n" +
                    "    <li>WhatsApp: 068 148 8912</li>\n" +
                    "  </ul>\n" +
                    "  \n" +
                    "  <p>Thank you,</p>\n" +
                    "  \n" +
                    "  <p>The eSure Team</p>\n" +
                    "  \n" +
                    "  <p><small>eSure is a Juristic Representative of Royale Crowns Financial Services, an authorized Financial Services Provider (FSP No.: 52845). All insurance products are regulated under Royale Crowns Financial Services.</small></p>\n" +
                    "</body>\n" +
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
