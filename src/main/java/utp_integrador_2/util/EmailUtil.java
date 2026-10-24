package utp_integrador_2.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Base64;
import java.util.Properties;

public class EmailUtil {

    private static final String GMAIL_HOST = "smtp.gmail.com";
    private static final String GMAIL_PORT = "587";

    private EmailUtil() {
    }

    // Railway: RESEND
    // Local: Gmail SMTP
    private static boolean usarResend() {
        return "RESEND".equalsIgnoreCase(
                System.getenv("EMAIL_PROVIDER"));
    }

    private static String variable(String nombre)
            throws MessagingException {

        String valor = System.getenv(nombre);

        if (valor == null || valor.isBlank()) {
            throw new MessagingException(
                    "Falta configurar " + nombre);
        }

        return valor;
    }

    // =========================================
    // SMTP PARA NETBEANS
    // =========================================
    private static Session obtenerSesion()
            throws MessagingException {

        String usuario = variable("GMAIL_USER");
        String clave = variable("GMAIL_APP_PASSWORD");

        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.starttls.required", "true");
        p.put("mail.smtp.host", GMAIL_HOST);
        p.put("mail.smtp.port", GMAIL_PORT);
        p.put("mail.smtp.ssl.protocols", "TLSv1.2");
        p.put("mail.smtp.connectiontimeout", "10000");
        p.put("mail.smtp.timeout", "10000");
        p.put("mail.smtp.writetimeout", "10000");

        return Session.getInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication
                    getPasswordAuthentication() {
                return new PasswordAuthentication(
                        usuario, clave);
            }
        });
    }

    private static void enviarSMTP(
            String destino,
            String asunto,
            String contenido,
            boolean html,
            File adjunto)
            throws MessagingException {

        MimeMessage correo
                = new MimeMessage(obtenerSesion());

        try {
            correo.setFrom(new InternetAddress(
                    variable("GMAIL_USER"),
                    "CREINFOR", "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException(
                    "Error en el remitente", e);
        }

        correo.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(destino, true));

        correo.setSubject(asunto, "UTF-8");

        if (adjunto == null) {
            correo.setContent(contenido,
                    html ? "text/html; charset=UTF-8"
                            : "text/plain; charset=UTF-8");
        } else {
            if (!adjunto.isFile()) {
                throw new MessagingException(
                        "No existe el archivo adjunto");
            }

            MimeMultipart multipart = new MimeMultipart();

            MimeBodyPart texto = new MimeBodyPart();
            texto.setContent(contenido,
                    html ? "text/html; charset=UTF-8"
                            : "text/plain; charset=UTF-8");
            multipart.addBodyPart(texto);

            MimeBodyPart archivo = new MimeBodyPart();
            try {
                archivo.attachFile(adjunto);
            } catch (IOException e) {
                throw new MessagingException(
                        "Error al adjuntar archivo", e);
            }

            multipart.addBodyPart(archivo);
            correo.setContent(multipart);
        }

        Transport.send(correo);
    }

    // =========================================
    // JSON SEGURO PARA RESEND
    // =========================================
    private static String json(String valor) {

        if (valor == null) {
            return "\"\"";
        }

        StringBuilder sb = new StringBuilder("\"");

        for (int i = 0; i < valor.length(); i++) {

            char c = valor.charAt(i);

            switch (c) {

                case '"':
                    sb.append("\\\"");
                    break;

                case '\\':
                    sb.append("\\\\");
                    break;

                case '\n':
                    sb.append("\\n");
                    break;

                case '\r':
                    sb.append("\\r");
                    break;

                case '\t':
                    sb.append("\\t");
                    break;

                default:
                    if (c < 32) {
                        sb.append(String.format(
                                "\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }

        return sb.append("\"").toString();
    }

    // =========================================
    // API RESEND PARA RAILWAY
    // =========================================
    private static void enviarResend(
            String destino,
            String asunto,
            String contenido,
            boolean html,
            File adjunto)
            throws MessagingException {

        String apiKey = variable("RESEND_API_KEY");
        String remitente = variable("EMAIL_FROM");

        StringBuilder cuerpo = new StringBuilder();

        cuerpo.append("{")
                .append("\"from\":").append(json(remitente))
                .append(",\"to\":[")
                .append(json(destino)).append("]")
                .append(",\"subject\":")
                .append(json(asunto))
                .append(html ? ",\"html\":" : ",\"text\":")
                .append(json(contenido));

        if (adjunto != null) {
            if (!adjunto.isFile()) {
                throw new MessagingException(
                        "No existe el PDF adjunto");
            }

            try {
                String base64 = Base64.getEncoder()
                        .encodeToString(
                                Files.readAllBytes(
                                        adjunto.toPath()));

                cuerpo.append(",\"attachments\":[{")
                        .append("\"filename\":")
                        .append(json(adjunto.getName()))
                        .append(",\"content\":")
                        .append(json(base64))
                        .append("}]");

            } catch (IOException e) {
                throw new MessagingException(
                        "No se pudo leer el PDF", e);
            }
        }

        cuerpo.append("}");

        try {
            HttpClient cliente = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest solicitud
                    = HttpRequest.newBuilder()
                            .uri(URI.create(
                                    "https://api.resend.com/emails"))
                            .timeout(Duration.ofSeconds(30))
                            .header("Authorization",
                                    "Bearer " + apiKey)
                            .header("Content-Type",
                                    "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(
                                    cuerpo.toString(),
                                    StandardCharsets.UTF_8))
                            .build();

            HttpResponse<String> respuesta
                    = cliente.send(
                            solicitud,
                            HttpResponse.BodyHandlers.ofString());

            int estado = respuesta.statusCode();

            if (estado < 200 || estado >= 300) {
                throw new MessagingException(
                        "Resend HTTP " + estado
                        + ": " + respuesta.body());
            }

            System.out.println(
                    "Correo aceptado por Resend: "
                    + respuesta.body());

        } catch (IOException e) {
            throw new MessagingException(
                    "Error de conexión con Resend", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MessagingException(
                    "Envío de correo interrumpido", e);
        }
    }

    // =========================================
    // MÉTODO CENTRAL
    // =========================================
    private static void enviar(
            String destino,
            String asunto,
            String contenido,
            boolean html,
            File adjunto)
            throws MessagingException {

        if (usarResend()) {
            enviarResend(destino, asunto,
                    contenido, html, adjunto);
        } else {
            enviarSMTP(destino, asunto,
                    contenido, html, adjunto);
        }
    }

    // =========================================
    // CORREO NORMAL
    // =========================================
    public static void sendEmail(
            String toEmail,
            String subject,
            String message)
            throws MessagingException {

        enviar(toEmail, subject, message, false, null);
    }

    // =========================================
    // CORREO CON PDF
    // =========================================
    public static void sendEmailConAdjunto(
            String toEmail,
            String subject,
            String message,
            File archivo)
            throws MessagingException {

        enviar(toEmail, subject, message,
                false, archivo);
    }

    // =========================================
    // CORREO DE ACTIVACIÓN
    // =========================================
    public static void enviarActivacionCuenta(
            String toEmail,
            String nombre,
            String enlaceActivacion)
            throws MessagingException {

        String nombreSeguro = escaparHtml(nombre);
        String enlaceSeguro = escaparHtml(enlaceActivacion);

        String contenido
                = "<div style='font-family:Arial,sans-serif;"
                + "max-width:600px;margin:auto;"
                + "border:1px solid #ddd;"
                + "border-radius:10px;overflow:hidden;'>"
                + "<div style='background:#153b5b;"
                + "color:white;padding:25px;"
                + "text-align:center;'>"
                + "<h1>CREINFOR</h1>"
                + "<p>Activación de cuenta</p></div>"
                + "<div style='padding:30px;'>"
                + "<h2>Hola, " + nombreSeguro + "</h2>"
                + "<p>Se ha creado una cuenta para ti "
                + "en el sistema CREINFOR.</p>"
                + "<p>Para activar tu cuenta y establecer "
                + "tu contraseña personal, utiliza "
                + "el siguiente botón:</p>"
                + "<div style='text-align:center;"
                + "margin:30px 0;'>"
                + "<a href='" + enlaceSeguro + "' "
                + "style='background:#1976d2;"
                + "color:white;padding:14px 28px;"
                + "text-decoration:none;"
                + "border-radius:8px;"
                + "display:inline-block;'>"
                + "ACTIVAR MI CUENTA</a></div>"
                + "<p style='font-size:13px;color:#777;'>"
                + "Este enlace tiene duración limitada "
                + "y solo puede utilizarse una vez.</p>"
                + "<p style='font-size:13px;color:#777;'>"
                + "Si no esperabas este mensaje, "
                + "puedes ignorarlo.</p></div>"
                + "<div style='background:#f5f5f5;"
                + "padding:15px;text-align:center;"
                + "font-size:12px;color:#777;'>"
                + "CREINFOR © 2026</div></div>";

        enviar(toEmail,
                "Activa tu cuenta - CREINFOR",
                contenido, true, null);
    }

    private static String escaparHtml(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
