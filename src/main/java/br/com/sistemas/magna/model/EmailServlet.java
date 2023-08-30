package br.com.sistemas.magna.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

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
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.nio.file.StandardCopyOption;

@WebServlet("/enviar-email")
@MultipartConfig
public class EmailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Parâmetros do formulário
        String destinatario = request.getParameter("destinatario");
        String assunto = request.getParameter("assunto");
        String mensagem = request.getParameter("mensagem");

        // Parte do anexo
        Part arquivoPart = request.getPart("arquivoAnexo");
        String nomeArquivo = getSubmittedFileName(arquivoPart);

        if (nomeArquivo != null && !nomeArquivo.isEmpty()) {
            // Diretório temporário para salvar o arquivo
            String diretorioTemp = System.getProperty("java.io.tmpdir");
            String caminhoAnexo = diretorioTemp + "/" + nomeArquivo;

            // Salvar o arquivo no diretório temporário
            try (InputStream input = arquivoPart.getInputStream()) {
                Files.copy(input, Paths.get(caminhoAnexo), StandardCopyOption.REPLACE_EXISTING);
            }

            // Lógica de envio de email com anexo
            enviarEmail(destinatario, assunto, mensagem, caminhoAnexo);

            // Redirecionamento após o envio
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } else {
            System.out.println("Nome de arquivo inválido");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    private void enviarEmail(String destinatario, String assunto, String mensagem, String caminhoAnexo) {
        String remetente = "viaugustoadr@gmail.com";
        String senha = "ailmhtxtzugafqou";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Pode variar de acordo com o provedor
        props.put("mail.smtp.port", "587"); // Pode variar de acordo com o provedor

        Session session = Session.getInstance(props, new Authenticator() {
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

            System.out.println("E-mail enviado com sucesso!");

        } catch (MessagingException e) {
            System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
        }
    }

    private String getSubmittedFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
