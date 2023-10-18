package br.com.sistemas.magna.model;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.Part;

public class EmailModel {
    private static final Logger LOGGER = Logger.getLogger(EmailModel.class.getName());

    // Construtor privado 
    private EmailModel() {
        throw new AssertionError("A classe EmailModel não deve ser instanciada.");
    }

    public static void enviarEmail(String destinatario, String assunto, String mensagem, String caminhoAnexo) {
        String remetente = "";
        String senha = "";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Pode variar de acordo com o provedor
        props.put("mail.smtp.port", "587"); // Pode variar de acordo com o provedor

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remetente, senha);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remetente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(assunto);

            // Parte de texto da mensagem
            MimeBodyPart textoPart = new MimeBodyPart();
            textoPart.setText(mensagem);

            // Parte do anexo
            MimeBodyPart anexoPart = new MimeBodyPart();
            DataSource source = new FileDataSource(caminhoAnexo);
            anexoPart.setDataHandler(new DataHandler(source));
            anexoPart.setFileName(source.getName());

            // Criando a parte mista (texto e anexo)
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textoPart);
            multipart.addBodyPart(anexoPart);

            // Definindo o conteúdo da mensagem como a parte mista
            message.setContent(multipart);

            // Enviando a mensagem
            Transport.send(message);

            LOGGER.info("E-mail enviado com sucesso!");

        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Erro ao enviar o e-mail", e);
        }
    }

    public static String getSubmittedFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
