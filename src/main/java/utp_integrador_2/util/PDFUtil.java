package utp_integrador_2.util;


import utp_integrador_2_model.Solicitud;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFUtil {

    public static File generarConstanciaSolicitud(
            Solicitud solicitud,
            String emailUsuario) throws Exception {

        // Carpeta temporal del sistema
        String tempDir = System.getProperty("java.io.tmpdir");

        String nombreArchivo =
                "Constancia_Solicitud_" +
                solicitud.getIdSolicitud() +
                ".pdf";

        File archivo = new File(tempDir, nombreArchivo);

        Document document = new Document(
                PageSize.A4,
                50,
                50,
                50,
                50
        );

        PdfWriter.getInstance(
                document,
                new FileOutputStream(archivo)
        );

        document.open();

        // ==========================================
        // COLORES
        // ==========================================

        Color azul = new Color(30, 90, 150);
        Color azulClaro = new Color(230, 240, 250);
        Color gris = new Color(100, 100, 100);
        Color verde = new Color(40, 140, 80);

        // ==========================================
        // FUENTES
        // ==========================================

        Font titulo = new Font(
                Font.HELVETICA,
                20,
                Font.BOLD,
                azul
        );

        Font subtitulo = new Font(
                Font.HELVETICA,
                11,
                Font.NORMAL,
                gris
        );

        Font encabezado = new Font(
                Font.HELVETICA,
                11,
                Font.BOLD,
                Color.WHITE
        );

        Font texto = new Font(
                Font.HELVETICA,
                10,
                Font.NORMAL,
                Color.DARK_GRAY
        );

        Font textoNegrita = new Font(
                Font.HELVETICA,
                10,
                Font.BOLD,
                Color.DARK_GRAY
        );

        Font estado = new Font(
                Font.HELVETICA,
                12,
                Font.BOLD,
                verde
        );

        // ==========================================
        // ENCABEZADO
        // ==========================================

        PdfPTable encabezadoTabla = new PdfPTable(2);
        encabezadoTabla.setWidthPercentage(100);
        encabezadoTabla.setWidths(new float[]{70, 30});

        PdfPCell empresa = new PdfPCell();
        empresa.setBorder(Rectangle.NO_BORDER);

        Paragraph nombreEmpresa = new Paragraph(
                "CREINFOR",
                titulo
        );

        empresa.addElement(nombreEmpresa);

        Paragraph sistema = new Paragraph(
                "Sistema de Atención y Soporte",
                subtitulo
        );

        empresa.addElement(sistema);

        encabezadoTabla.addCell(empresa);

        PdfPCell numero = new PdfPCell();
        numero.setBorder(Rectangle.NO_BORDER);
        numero.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph numeroSolicitud = new Paragraph(
                "SOLICITUD\n#" + solicitud.getIdSolicitud(),
                new Font(
                        Font.HELVETICA,
                        14,
                        Font.BOLD,
                        azul
                )
        );

        numeroSolicitud.setAlignment(Element.ALIGN_RIGHT);

        numero.addElement(numeroSolicitud);

        encabezadoTabla.addCell(numero);

        document.add(encabezadoTabla);

        // Línea separadora
        Paragraph linea = new Paragraph(
                "________________________________________________________________________________"
        );

        linea.setFont(
                new Font(
                        Font.HELVETICA,
                        8,
                        Font.NORMAL,
                        azul
                )
        );

        document.add(linea);

        // ==========================================
        // TÍTULO
        // ==========================================

        Paragraph tituloDocumento = new Paragraph(
                "CONSTANCIA DE ATENCIÓN",
                new Font(
                        Font.HELVETICA,
                        18,
                        Font.BOLD,
                        Color.DARK_GRAY
                )
        );

        tituloDocumento.setAlignment(Element.ALIGN_CENTER);
        tituloDocumento.setSpacingBefore(20);
        tituloDocumento.setSpacingAfter(5);

        document.add(tituloDocumento);

        Paragraph descripcion = new Paragraph(
                "Se deja constancia de que la solicitud registrada "
                + "en el sistema Creinfor ha sido atendida y culminada.",
                texto
        );

        descripcion.setAlignment(Element.ALIGN_CENTER);
        descripcion.setSpacingAfter(25);

        document.add(descripcion);

        // ==========================================
        // INFORMACIÓN DE LA SOLICITUD
        // ==========================================

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{30, 70});

        agregarFila(
                tabla,
                "N.º de solicitud",
                String.valueOf(solicitud.getIdSolicitud()),
                azulClaro,
                textoNegrita,
                texto
        );

        agregarFila(
                tabla,
                "Tipo de solicitud",
                solicitud.getTipoSolicitud(),
                Color.WHITE,
                textoNegrita,
                texto
        );

        agregarFila(
                tabla,
                "Correo del usuario",
                emailUsuario,
                azulClaro,
                textoNegrita,
                texto
        );

        String fechaRegistro = "";

        if (solicitud.getFechaRegistro() != null) {
            SimpleDateFormat formato =
                    new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm"
                    );

            fechaRegistro =
                    formato.format(
                            solicitud.getFechaRegistro()
                    );
        }

        agregarFila(
                tabla,
                "Fecha de registro",
                fechaRegistro,
                Color.WHITE,
                textoNegrita,
                texto
        );

        agregarFila(
                tabla,
                "Estado",
                "✓ SOLICITUD FINALIZADA",
                azulClaro,
                textoNegrita,
                estado
        );

        document.add(tabla);

        // ==========================================
        // MOTIVO
        // ==========================================

        Paragraph tituloMotivo = new Paragraph(
                "DETALLE DE LA SOLICITUD",
                new Font(
                        Font.HELVETICA,
                        12,
                        Font.BOLD,
                        azul
                )
        );

        tituloMotivo.setSpacingBefore(25);
        tituloMotivo.setSpacingAfter(8);

        document.add(tituloMotivo);

        PdfPTable motivoTabla = new PdfPTable(1);
        motivoTabla.setWidthPercentage(100);

        PdfPCell motivoCelda = new PdfPCell(
                new Paragraph(
                        solicitud.getMotivo() != null
                                ? solicitud.getMotivo()
                                : "Sin información",
                        texto
                )
        );

        motivoCelda.setPadding(15);
        motivoCelda.setBackgroundColor(
                new Color(248, 248, 248)
        );

        motivoCelda.setBorderColor(
                new Color(220, 220, 220)
        );

        motivoTabla.addCell(motivoCelda);

        document.add(motivoTabla);

        // ==========================================
        // FECHA DE CULMINACIÓN
        // ==========================================

        Paragraph fechaCulminacion = new Paragraph();

        fechaCulminacion.setSpacingBefore(25);

        SimpleDateFormat formatoFecha =
                new SimpleDateFormat(
                        "dd 'de' MMMM 'de' yyyy"
                );

        fechaCulminacion.add(
                new Chunk(
                        "Fecha de culminación: ",
                        textoNegrita
                )
        );

        fechaCulminacion.add(
                new Chunk(
                        formatoFecha.format(new Date()),
                        texto
                )
        );

        document.add(fechaCulminacion);

        // ==========================================
        // MENSAJE FINAL
        // ==========================================

        PdfPTable mensajeTabla =
                new PdfPTable(1);

        mensajeTabla.setWidthPercentage(100);

        PdfPCell mensajeCelda =
                new PdfPCell();

        mensajeCelda.setPadding(15);
        mensajeCelda.setBorderColor(azul);
        mensajeCelda.setBackgroundColor(
                new Color(245, 249, 253)
        );

        Paragraph mensajeFinal =
                new Paragraph(
                        "Su solicitud ha sido atendida "
                        + "satisfactoriamente por el equipo de "
                        + "Soporte Creinfor.",
                        texto
                );

        mensajeFinal.setAlignment(
                Element.ALIGN_CENTER
        );

        mensajeCelda.addElement(mensajeFinal);

        mensajeTabla.addCell(mensajeCelda);

        document.add(mensajeTabla);

        // ==========================================
        // PIE DE PÁGINA
        // ==========================================

        Paragraph espacio = new Paragraph("\n\n");
        document.add(espacio);

        Paragraph pie = new Paragraph(
                "CREINFOR - Área de Soporte\n"
                + "Documento generado automáticamente por el sistema.",
                new Font(
                        Font.HELVETICA,
                        8,
                        Font.NORMAL,
                        gris
                )
        );

        pie.setAlignment(Element.ALIGN_CENTER);

        document.add(pie);

        document.close();

        return archivo;
    }

    private static void agregarFila(
            PdfPTable tabla,
            String etiqueta,
            String valor,
            Color fondo,
            Font fuenteEtiqueta,
            Font fuenteValor) {

        PdfPCell celdaEtiqueta =
                new PdfPCell(
                        new Paragraph(
                                etiqueta,
                                fuenteEtiqueta
                        )
                );

        celdaEtiqueta.setBackgroundColor(fondo);
        celdaEtiqueta.setPadding(10);

        PdfPCell celdaValor =
                new PdfPCell(
                        new Paragraph(
                                valor != null
                                        ? valor
                                        : "",
                                fuenteValor
                        )
                );

        celdaValor.setPadding(10);

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }
}