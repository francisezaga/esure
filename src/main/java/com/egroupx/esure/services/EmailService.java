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

    @Value("${egroupx.medicalAid.emailAddress}")
    private String medicalAidRecipientEmail;

    private final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    private final CustomerRepository customerRepository;

    private final LifeInsuranceRepository lifeInsuranceRepository;

    public EmailService(CustomerRepository customerRepository, LifeInsuranceRepository lifeInsuranceRepository) {
        this.customerRepository = customerRepository;
        this.lifeInsuranceRepository = lifeInsuranceRepository;
    }


    public Mono<String> sendEmail(EmailCustomer customer, String subject) {

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
            String idNumber = customer.getIdNumber()==null?"":customer.getIdNumber();
            Long fspQuotationRef = customer.getFspQuoteRefId()==null?-1L:customer.getFspQuoteRefId();
            Long fspPolicyRef = customer.getFspPolicyId()==null?0L:customer.getFspPolicyId();
            String code = customer.getCode()==null?"":customer.getCode();
            String number = customer.getNumber()==null?"":customer.getNumber();
            String emailAddress = customer.getLine_1()==null?"":customer.getLine_1();
            String endBody = "<br><h3>Thank you</h3>"+
                    "<br><h3>Regards</h3>"+
                    "<body/>"+
                    "</html>";
            String optionalEndBody = customer.getInstructions()==null?endBody:"<label>Additional Info:</label>" + customer.getInstructions()+endBody;

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
                    "<label>FSP Quote Ref:</label>" + fspQuotationRef+"<br>"+
                    "<label>FSP Policy Ref:</label>" + fspPolicyRef+"<br>"+
                    "<label>Contact Number:</label>" + code+number+"<br>"+
                    "<label>Email:</label>" + emailAddress+"<br>"+
                    optionalEndBody
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
            LOG.error(MessageFormat.format("Customer onboarding email notification failed to send for user {0} error {1}",customer.getLine_1(),mex.getMessage()));
            return Mono.just("Email not send");
        }
    }


    public Mono<String> sendInsuranceWelcomeEmail(EmailCustomer customer, String subject) {

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
            String idNumber = customer.getIdNumber()==null?"":customer.getIdNumber();
            Long fspQuotationRef = customer.getFspQuoteRefId()==null?-1L:customer.getFspQuoteRefId();
            Long fspPolicyRef = customer.getFspPolicyId()==null?0L:customer.getFspPolicyId();
            String code = customer.getCode()==null?"":customer.getCode();
            String number = customer.getNumber()==null?"":customer.getNumber();
            String emailAddress = customer.getLine_1()==null?"":customer.getLine_1();
            String endBody = "<br><h3>Thank you</h3>"+
                    "<br><h3>Regards</h3>"+
                    "<body/>"+
                    "</html>";
            String optionalEndBody = customer.getInstructions()==null?endBody:"<label>Additional Info:</label>" + customer.getInstructions()+endBody;

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
                            "<p>Welcome to eSure Life Cover!<br>Thank for registering with eSure. One of our agencies will contact you regarding your application.</h2>"+
                            "<label>Quote Ref:</label>" + fspQuotationRef+"<br>"+
                            "<label>Policy Ref:</label>" + fspPolicyRef+"<br>"+
                            "<label>Contact Number:</label>" + code+number+"<br>"+
                            "<label>Email:</label>" + emailAddress+"<br>"+
                            optionalEndBody
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
                            "<h2>Hi "+fullName+"</h2>"+
                            "<p>Welcome to eSure Life Cover!<br>Thank for registering with eSure. One of our agencies will contact you regarding your application.</h2>"+
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

    public Mono<String> sendEmailForMedicalAid(MedicalAidMemberDetails memberDetails, String subject) {

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

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(medicalAidRecipientEmail));

            message.setSubject(subject);

            String emailAddress = memberDetails.getEmail()==null?"":memberDetails.getEmail();
            int adultsCount = memberDetails.getAdultsCount();
            int childrenCount = memberDetails.getChildrenCount();
            String fullName = memberDetails.getFullName()==null?"":memberDetails.getFullName();
            String phoneNumber = memberDetails.getPhoneNumber()==null?"":memberDetails.getPhoneNumber();
            boolean hasMedicalAid = memberDetails.isHasMedicalAid();
            String incomeCategory = memberDetails.getIncomeCategory()==null?"":memberDetails.getIncomeCategory();
            String hospitalChoice = memberDetails.getHospitalChoice()==null?"":memberDetails.getHospitalChoice();
            String hospitalRates = memberDetails.getHospitalRates()==null?"":memberDetails.getHospitalRates();
            String dayToDayCoverLevel = memberDetails.getDayToDayCoverLevel()==null?"":memberDetails.getDayToDayCoverLevel();
            String doctorChoice = memberDetails.getDoctorChoice()==null?"":memberDetails.getDoctorChoice();
            boolean hasChronicMedicationRequirements = memberDetails.isHasChronicMedicationRequirements();
            String hospitalExclusions = memberDetails.getHospitalExclusions()==null?"":memberDetails.getHospitalExclusions();

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
                            "<label>Email:</label>" + emailAddress+"<br>"+
                            "<label>Contact number:</label>" + phoneNumber+"<br>"+
                            "<label>Adult Count:</label>" + adultsCount+"<br>"+
                            "<label>Children Count:</label>" + childrenCount+"<br>"+
                            "<label>Does member has medical aid?:</label>" + hasMedicalAid+"<br>"+
                            "<label>Income Category:</label>" + incomeCategory+"<br>"+
                            "<label>Hospital Choice:</label>" + hospitalChoice+"<br>"+
                            "<label>Hospital rates:</label>" + hospitalRates+"<br>"+
                            "<label>Day to day cover level:</label>" + dayToDayCoverLevel+"<br>"+
                            "<label>Doctor choice:</label>" + doctorChoice+"<br>"+
                            "<label>Member has chronic medical requirements:</label>" + hasChronicMedicationRequirements+"<br>"+
                            "<label>Hospital Exclusions:</label" + hospitalExclusions+"<br>"+
                            endBody
                    ,"text/html"
            );

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
