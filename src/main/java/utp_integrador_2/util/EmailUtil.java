package utp_integrador_2.util;


import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailUtil {

    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";

    private static final String USERNAME ="wilmerfernandezbenavides@gmail.com";

    // REEMPLAZA ESTO POR UNA NUEVA CLAVE DE APLICACIÓN
    private static final String PASSWORD ="uilq dilb xnbn ilct";

    // ==========================================
    // OBTENER SESIÓN DE GMAIL
    // ==========================================

    private static Session obtenerSesion() {

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");

        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", PORT);

        properties.put(
                "mail.smtp.ssl.trust",
                HOST
        );

        properties.put(
                "mail.smtp.ssl.protocols",
                "TLSv1.2"
        );

        properties.put(
                "mail.smtp.connectiontimeout",
                "10000"
        );

        properties.put(
                "mail.smtp.timeout",
                "10000"
        );

        properties.put(
                "mail.smtp.writetimeout",
                "10000"
        );

        return Session.getInstance(
                properties,
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                USERNAME,
                                PASSWORD
                        );
                    }
                }
        );
    }

    // ==========================================
    // CONFIGURAR REMITENTE
    // ==========================================

    private static void configurarRemitente(
            MimeMessage email)
            throws MessagingException {

        try {

            email.setFrom(
                    new InternetAddress(
                            USERNAME,
                            "CREINFOR",
                            "UTF-8"
                    )
            );

        } catch (UnsupportedEncodingException e) {

            throw new MessagingException(
                    "No se pudo configurar el remitente.",
                    e
            );
        }
    }

    // ==========================================
    // CORREO NORMAL
    // ==========================================

    public static void sendEmail(
            String toEmail,
            String subject,
            String message)
            throws MessagingException {

        Session session = obtenerSesion();

        MimeMessage email =
                new MimeMessage(session);

        // Remitente
        configurarRemitente(email);

        // Destinatario
        email.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(toEmail)
        );

        // Asunto
        email.setSubject(
                subject,
                "UTF-8"
        );

        // Mensaje
        email.setText(
                message,
                "UTF-8"
        );

        // Enviar
        Transport.send(email);

        System.out.println(
                "Correo enviado exitosamente a "
                + toEmail
        );
    }

    // ==========================================
    // CORREO CON PDF ADJUNTO
    // ==========================================

    public static void sendEmailConAdjunto(
            String toEmail,
            String subject,
            String message,
            File archivo)
            throws MessagingException {

        Session session = obtenerSesion();

        MimeMessage email =
                new MimeMessage(session);

        // ======================================
        // REMITENTE
        // ======================================

        configurarRemitente(email);

        // ======================================
        // DESTINATARIO
        // ======================================

        email.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(toEmail)
        );

        // ======================================
        // ASUNTO
        // ======================================

        email.setSubject(
                subject,
                "UTF-8"
        );

        // ======================================
        // CONTENIDO MULTIPART
        // ======================================

        Multipart multipart =
                new MimeMultipart();

        // ======================================
        // TEXTO DEL CORREO
        // ======================================

        MimeBodyPart texto =
                new MimeBodyPart();

        texto.setText(
                message,
                "UTF-8"
        );

        multipart.addBodyPart(texto);

        // ======================================
        // ARCHIVO PDF
        // ======================================

        MimeBodyPart adjunto =
                new MimeBodyPart();

        try {

            adjunto.attachFile(archivo);

        } catch (Exception e) {

            throw new MessagingException(
                    "No se pudo adjuntar el PDF.",
                    e
            );
        }

        adjunto.setFileName(
                archivo.getName()
        );

        multipart.addBodyPart(adjunto);

        // ======================================
        // ASIGNAR CONTENIDO
        // ======================================

        email.setContent(multipart);

        // ======================================
        // ENVIAR
        // ======================================

        Transport.send(email);

        System.out.println(
                "Correo con PDF enviado exitosamente a "
                + toEmail
        );
    }
    
    // ==========================================
// CORREO DE ACTIVACIÓN DE CUENTA
// ==========================================

    public static void enviarActivacionCuenta(
            String toEmail,
            String nombre,
            String enlaceActivacion)
            throws MessagingException {

        Session session = obtenerSesion();

        MimeMessage email =
                new MimeMessage(session);

        configurarRemitente(email);

        email.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(toEmail)
        );

        email.setSubject(
                "Activa tu cuenta - CREINFOR",
                "UTF-8"
        );

        String contenido =
                "<div style='font-family:Arial,sans-serif;"
                + "max-width:600px;margin:auto;"
                + "border:1px solid #ddd;"
                + "border-radius:10px;"
                + "overflow:hidden;'>"

                + "<div style='background:#153b5b;"
                + "color:white;"
                + "padding:25px;"
                + "text-align:center;'>"

                + "<h1 style='margin:0;'>CREINFOR</h1>"
                + "<p style='margin:8px 0 0;'>Activación de cuenta</p>"

                + "</div>"

                + "<div style='padding:30px;'>"

                + "<h2 style='color:#263238;'>Hola, "
                + nombre
                + "</h2>"

                + "<p style='color:#455a64;line-height:1.6;'>"
                + "Se ha creado una cuenta para ti en el sistema CREINFOR."
                + "</p>"

                + "<p style='color:#455a64;line-height:1.6;'>"
                + "Para activar tu cuenta y establecer tu contraseña personal, "
                + "haz clic en el siguiente botón:"
                + "</p>"

                + "<div style='text-align:center;margin:30px 0;'>"

                + "<a href='"
                + enlaceActivacion
                + "' style='"
                + "background:#1976d2;"
                + "color:white;"
                + "padding:14px 28px;"
                + "text-decoration:none;"
                + "border-radius:8px;"
                + "font-weight:bold;"
                + "display:inline-block;'>"

                + "ACTIVAR MI CUENTA"

                + "</a>"

                + "</div>"

                + "<p style='font-size:13px;"
                + "color:#777;"
                + "line-height:1.5;'>"
                + "Este enlace tiene una duración limitada "
                + "y solo puede utilizarse una vez."
                + "</p>"

                + "<p style='font-size:13px;"
                + "color:#777;"
                + "line-height:1.5;'>"
                + "Si no esperabas recibir este mensaje, "
                + "puedes ignorarlo."
                + "</p>"

                + "</div>"

                + "<div style='background:#f5f5f5;"
                + "padding:15px;"
                + "text-align:center;"
                + "font-size:12px;"
                + "color:#777;'>"

                + "CREINFOR © 2026"

                + "</div>"

                + "</div>";

        email.setContent(
                contenido,
                "text/html; charset=UTF-8"
        );

        Transport.send(email);

        System.out.println(
                "Correo de activación enviado exitosamente a "
                + toEmail
        );
}
        
}