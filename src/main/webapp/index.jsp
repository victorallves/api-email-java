<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Enviar Email</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        .container {
            background-color: rgba(255, 255, 180, 0.9);
            border-radius: 20px;
            padding: 20px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
        }
    </style>
</head>
<body>
    <div class="container mt-5">
        <h1 class="mb-4">Enviar Email</h1>
        <form action="enviar-email" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="destinatario">Destinatário:</label>
                <input type="text" class="form-control" id="destinatario" name="destinatario">
            </div>
            <div class="form-group">
                <label for="assunto">Assunto:</label>
                <input type="text" class="form-control" id="assunto" name="assunto">
            </div>
            <div class="form-group">
                <label for="mensagem">Mensagem:</label>
                <textarea class="form-control" id="mensagem" name="mensagem" rows="5"></textarea>
            </div>
            <div class="form-group">
                <label for="arquivoAnexo">Anexo:</label>
                <input type="file" class="form-control-file" id="arquivoAnexo" name="arquivoAnexo">
            </div>
            <button type="submit" class="btn btn-primary">Enviar</button>
        </form>
    </div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>
