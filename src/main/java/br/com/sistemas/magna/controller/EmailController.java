package br.com.sistemas.magna.controller;

import br.com.sistemas.magna.model.EmailModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/enviar-email")
@MultipartConfig
public class EmailController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String INDEX = "/app";
    private static final Logger LOGGER = Logger.getLogger(EmailController.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            // Parâmetros do formulário
            String destinatario = request.getParameter("destinatario");
            String assunto = request.getParameter("assunto");
            String mensagem = request.getParameter("mensagem");

            // Parte do anexo
            Part arquivoPart = request.getPart("arquivoAnexo");
            processarArquivoAnexo(arquivoPart, destinatario, assunto, mensagem, response);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erro desconhecido", ex);
            throw new ServletException("Erro desconhecido", ex);
        }
    }

    private void processarArquivoAnexo(Part arquivoPart, String destinatario, String assunto, String mensagem, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            String nomeArquivo = EmailModel.getSubmittedFileName(arquivoPart);

            if (nomeArquivo != null && !nomeArquivo.isEmpty()) {
                // Diretório temporário para salvar o arquivo
                String diretorioTemp = System.getProperty("java.io.tmpdir");
                String caminhoAnexo = diretorioTemp + "/" + nomeArquivo;

                // Salvar o arquivo no diretório temporário
                try (InputStream input = arquivoPart.getInputStream()) {
                    Files.copy(input, Paths.get(caminhoAnexo), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Erro ao salvar o arquivo anexo", e);
                    throw new ServletException("Erro ao salvar o arquivo anexo: " + e.getMessage(), e);
                }

                EmailModel.enviarEmail(destinatario, assunto, mensagem, caminhoAnexo);

                // Redirecionamento após o envio
                response.sendRedirect(INDEX);
            } else {
                LOGGER.warning("Nome de arquivo inválido");
                throw new ServletException("Nome de arquivo inválido");
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Erro durante o processamento", ex);
            throw new ServletException("Erro durante o processamento: " + ex.getMessage(), ex);
        }
    }
}
