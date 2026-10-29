package utp_integrador_2.util;

import utp_integrador_2_model.Cotizacion;
import utp_integrador_2_model.DetalleCotizacion;
import java.util.List;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
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

        String nombreArchivo
                = "Constancia_Solicitud_"
                + solicitud.getIdSolicitud()
                + ".pdf";

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
            SimpleDateFormat formato
                    = new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm"
                    );

            fechaRegistro
                    = formato.format(
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

        SimpleDateFormat formatoFecha
                = new SimpleDateFormat(
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
        PdfPTable mensajeTabla
                = new PdfPTable(1);

        mensajeTabla.setWidthPercentage(100);

        PdfPCell mensajeCelda
                = new PdfPCell();

        mensajeCelda.setPadding(15);
        mensajeCelda.setBorderColor(azul);
        mensajeCelda.setBackgroundColor(
                new Color(245, 249, 253)
        );

        Paragraph mensajeFinal
                = new Paragraph(
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

        PdfPCell celdaEtiqueta
                = new PdfPCell(
                        new Paragraph(
                                etiqueta,
                                fuenteEtiqueta
                        )
                );

        celdaEtiqueta.setBackgroundColor(fondo);
        celdaEtiqueta.setPadding(10);

        PdfPCell celdaValor
                = new PdfPCell(
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

    public static File generarPDFCotizacion(
            Cotizacion cotizacion,
            List<DetalleCotizacion> detalles,
            String emailCliente) throws Exception {

        File archivo = File.createTempFile(
                "Cotizacion_" + cotizacion.getIdCotizacion() + "_",
                ".pdf"
        );

        Document document = new Document(PageSize.A4, 45, 45, 45, 45);

        Color azul = new Color(23, 59, 80);
        Color teal = new Color(24, 138, 141);
        Color fondo = new Color(239, 246, 248);

        Font titulo = new Font(Font.HELVETICA, 22, Font.BOLD, azul);
        Font subtitulo = new Font(Font.HELVETICA, 15, Font.BOLD, azul);
        Font texto = new Font(Font.HELVETICA, 10, Font.NORMAL);
        Font negrita = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font blanco = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);

        NumberFormat moneda = NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("es-PE")
        );

        DateTimeFormatter fecha
                = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            PdfWriter.getInstance(document, new FileOutputStream(archivo));
            document.open();

            // ENCABEZADO
            Paragraph empresa = new Paragraph("CREINFOR", titulo);
            empresa.setAlignment(Element.ALIGN_CENTER);
            document.add(empresa);

            Paragraph sistema = new Paragraph(
                    "Sistema de Gestión de Soporte Técnico", texto);
            sistema.setAlignment(Element.ALIGN_CENTER);
            sistema.setSpacingAfter(20);
            document.add(sistema);

            Paragraph encabezado = new Paragraph(
                    "COTIZACIÓN DE SERVICIOS", subtitulo);
            encabezado.setAlignment(Element.ALIGN_CENTER);
            encabezado.setSpacingAfter(20);
            document.add(encabezado);

            // INFORMACIÓN GENERAL
            PdfPTable datos = new PdfPTable(2);
            datos.setWidthPercentage(100);
            datos.setWidths(new float[]{35, 65});

            agregarFila(datos, "N.º de cotización",
                    String.valueOf(cotizacion.getIdCotizacion()),
                    fondo, negrita, texto);

            agregarFila(datos, "Solicitud",
                    "#" + cotizacion.getIdSolicitud(),
                    Color.WHITE, negrita, texto);

            agregarFila(datos, "Correo del cliente",
                    emailCliente, fondo, negrita, texto);

            agregarFila(datos, "Válida hasta",
                    cotizacion.getFechaVencimiento().format(fecha),
                    Color.WHITE, negrita, texto);

            agregarFila(datos, "Estado",
                    "PROPUESTA ECONÓMICA",
                    fondo, negrita, texto);

            document.add(datos);

            Paragraph conceptoTitulo = new Paragraph(
                    "DETALLE DE LOS SERVICIOS", subtitulo);
            conceptoTitulo.setSpacingBefore(25);
            conceptoTitulo.setSpacingAfter(12);
            document.add(conceptoTitulo);

            // TABLA DE CONCEPTOS
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{45, 15, 20, 20});

            String[] columnas = {
                "Descripción", "Cantidad", "P. unitario", "Subtotal"
            };

            for (String columna : columnas) {
                PdfPCell celda = new PdfPCell(
                        new Phrase(columna, blanco));
                celda.setBackgroundColor(teal);
                celda.setPadding(10);
                tabla.addCell(celda);
            }

            for (DetalleCotizacion d : detalles) {
                tabla.addCell(new Phrase(d.getDescripcion(), texto));
                tabla.addCell(new Phrase(
                        d.getCantidad().toPlainString(), texto));
                tabla.addCell(new Phrase(
                        moneda.format(d.getPrecioUnitario()), texto));
                tabla.addCell(new Phrase(
                        moneda.format(d.getSubtotal()), texto));
            }

            document.add(tabla);

            // TOTAL
            Paragraph total = new Paragraph(
                    "TOTAL COTIZADO: "
                    + moneda.format(cotizacion.getTotal()),
                    new Font(Font.HELVETICA, 16, Font.BOLD, teal));

            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(20);
            document.add(total);

            // OBSERVACIONES
            if (cotizacion.getObservaciones() != null
                    && !cotizacion.getObservaciones().trim().isEmpty()) {

                Paragraph observaciones = new Paragraph(
                        "OBSERVACIONES Y CONDICIONES",
                        subtitulo);

                observaciones.setSpacingBefore(25);
                observaciones.setSpacingAfter(10);
                document.add(observaciones);

                document.add(new Paragraph(
                        cotizacion.getObservaciones(), texto));
            }

            // MENSAJE FINAL
            Paragraph mensaje = new Paragraph(
                    "Esta propuesta está sujeta a la aprobación "
                    + "del cliente. La ejecución del servicio "
                    + "se realizará de acuerdo con las condiciones "
                    + "que se acuerden.",
                    texto);

            mensaje.setSpacingBefore(30);
            document.add(mensaje);

            Paragraph pie = new Paragraph(
                    "CREINFOR S.A.C.\n"
                    + "Área de Soporte Técnico\n"
                    + "Documento generado por el sistema CREINFOR.",
                    texto);

            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(35);
            document.add(pie);

        } finally {
            document.close();
        }

        return archivo;
    }

    public static File generarInformeTecnicoPDF(
            Connection connection,
            Solicitud solicitud,
            String emailCliente) throws Exception {

        int idSolicitud = solicitud.getIdSolicitud();

        File archivo = File.createTempFile(
                "Informe_Tecnico_" + idSolicitud + "_", ".pdf");

        Color verde = new Color(22, 75, 58);
        Color fondo = new Color(234, 244, 239);

        Font titulo = new Font(Font.HELVETICA, 21, Font.BOLD, verde);
        Font seccion = new Font(Font.HELVETICA, 12, Font.BOLD, verde);
        Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL);
        Font negrita = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font blanco = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);

        NumberFormat moneda = NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("es-PE"));

        Document document = new Document(
                PageSize.A4, 42, 42, 45, 45);

        boolean abierto = false;

        try {
            PdfWriter.getInstance(
                    document, new FileOutputStream(archivo));

            document.open();
            abierto = true;

            Paragraph marca = new Paragraph("CREINFOR", titulo);
            marca.setAlignment(Element.ALIGN_CENTER);
            document.add(marca);

            Paragraph subtitulo = new Paragraph(
                    "SISTEMA DE GESTIÓN DE SOPORTE TÉCNICO",
                    normal);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            document.add(subtitulo);

            Paragraph nombre = new Paragraph(
                    "INFORME TÉCNICO FINAL", titulo);
            nombre.setAlignment(Element.ALIGN_CENTER);
            nombre.setSpacingAfter(22);
            document.add(nombre);

            PdfPTable datos = new PdfPTable(2);
            datos.setWidthPercentage(100);
            datos.setWidths(new float[]{35, 65});

            agregarFila(datos, "Solicitud",
                    "#" + idSolicitud, fondo, negrita, normal);
            agregarFila(datos, "Tipo de servicio",
                    solicitud.getTipoSolicitud(),
                    Color.WHITE, negrita, normal);
            agregarFila(datos, "Correo del cliente",
                    emailCliente, fondo, negrita, normal);
            agregarFila(datos, "Estado",
                    "FINALIZADA",
                    Color.WHITE, negrita, normal);

            document.add(datos);

            agregarSeccionInforme(
                    document, "MOTIVO DE LA SOLICITUD",
                    solicitud.getMotivo(), seccion, normal);

            // INFORME GUARDADO POR EL COORDINADOR
            String sqlInforme
                    = "SELECT diagnostico, solucion_aplicada, "
                    + "resultados, recomendaciones, fecha_elaboracion "
                    + "FROM informes_tecnicos WHERE id_solicitud = ?";

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlInforme)) {

                ps.setInt(1, idSolicitud);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new IllegalStateException(
                                "No existe informe técnico para esta solicitud.");
                    }

                    agregarSeccionInforme(document,
                            "1. DIAGNÓSTICO",
                            rs.getString("diagnostico"), seccion, normal);

                    agregarSeccionInforme(document,
                            "2. SOLUCIÓN APLICADA",
                            rs.getString("solucion_aplicada"), seccion, normal);

                    agregarSeccionInforme(document,
                            "3. RESULTADOS OBTENIDOS",
                            rs.getString("resultados"), seccion, normal);

                    agregarSeccionInforme(document,
                            "4. RECOMENDACIONES",
                            rs.getString("recomendaciones"), seccion, normal);
                }
            }

            // COTIZACIÓN APROBADA
            String sqlCotizacion
                    = "SELECT id_cotizacion, total, moneda "
                    + "FROM cotizaciones "
                    + "WHERE id_solicitud = ? AND estado = 'aprobada' "
                    + "ORDER BY id_cotizacion DESC LIMIT 1";

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlCotizacion)) {

                ps.setInt(1, idSolicitud);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        Paragraph p = new Paragraph(
                                "COTIZACIÓN APROBADA", seccion);
                        p.setSpacingBefore(20);
                        p.setSpacingAfter(8);
                        document.add(p);

                        document.add(new Paragraph(
                                "Cotización #" + rs.getInt("id_cotizacion")
                                + " - Importe aceptado: "
                                + moneda.format(rs.getBigDecimal("total")),
                                normal));
                    }
                }
            }

            // COLABORADORES ASIGNADOS
            Paragraph equipo = new Paragraph(
                    "EQUIPO RESPONSABLE", seccion);
            equipo.setSpacingBefore(22);
            equipo.setSpacingAfter(10);
            document.add(equipo);

            String sqlEquipo
                    = "SELECT c.nombres, c.apellidos, c.rol, "
                    + "a.es_coordinador "
                    + "FROM asignaciones a "
                    + "JOIN colaboradores c "
                    + "ON c.id_colaborador = a.id_colaborador "
                    + "WHERE a.id_solicitud = ? "
                    + "ORDER BY a.es_coordinador DESC, c.nombres";

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlEquipo)) {

                ps.setInt(1, idSolicitud);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        String persona
                                = rs.getString("nombres") + " "
                                + rs.getString("apellidos") + " - "
                                + rs.getString("rol");

                        if (rs.getBoolean("es_coordinador")) {
                            persona += " (Coordinador)";
                        }

                        document.add(new Paragraph(persona, normal));
                    }
                }
            }

            // ACTIVIDADES REGISTRADAS
            Paragraph encabezadoActividades = new Paragraph(
                    "ACTIVIDADES REALIZADAS", seccion);
            encabezadoActividades.setSpacingBefore(24);
            encabezadoActividades.setSpacingAfter(10);
            document.add(encabezadoActividades);

            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{20, 60, 20});

            for (String encabezado : new String[]{
                "Fecha", "Descripción", "Horas"}) {

                PdfPCell celda = new PdfPCell(
                        new Phrase(encabezado, blanco));
                celda.setBackgroundColor(verde);
                celda.setPadding(9);
                tabla.addCell(celda);
            }

            String sqlActividades
                    = "SELECT ac.fecha, ac.descripcion, ac.tiempo_horas "
                    + "FROM actividades ac "
                    + "JOIN asignaciones a "
                    + "ON a.id_asignacion = ac.id_asignacion "
                    + "WHERE a.id_solicitud = ? "
                    + "ORDER BY ac.fecha, ac.id_actividad";

            BigDecimal totalHoras = BigDecimal.ZERO;
            int cantidad = 0;

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlActividades)) {

                ps.setInt(1, idSolicitud);

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        cantidad++;

                        java.sql.Date fecha = rs.getDate("fecha");
                        BigDecimal horas
                                = rs.getBigDecimal("tiempo_horas");

                        if (horas == null) {
                            horas = BigDecimal.ZERO;
                        }

                        totalHoras = totalHoras.add(horas);

                        tabla.addCell(new Phrase(
                                fecha != null ? fecha.toString() : "",
                                normal));

                        tabla.addCell(new Phrase(
                                rs.getString("descripcion"), normal));

                        tabla.addCell(new Phrase(
                                horas.toPlainString(), normal));
                    }
                }
            }

            if (cantidad == 0) {
                tabla.addCell(new Phrase("-", normal));
                tabla.addCell(new Phrase(
                        "Sin actividades registradas", normal));
                tabla.addCell(new Phrase("0", normal));
            }

            document.add(tabla);

            Paragraph resumen = new Paragraph(
                    "Actividades registradas: " + cantidad
                    + "     Total de horas: "
                    + totalHoras.toPlainString() + " h",
                    negrita);
            resumen.setSpacingBefore(12);
            document.add(resumen);

            Paragraph pie = new Paragraph(
                    "CREINFOR S.A.C.\n"
                    + "Informe técnico generado a partir "
                    + "de los registros del sistema.",
                    normal);
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(35);
            document.add(pie);

        } finally {
            if (abierto) {
                document.close();
            }
        }

        return archivo;
    }

    private static void agregarSeccionInforme(
            Document document,
            String encabezado,
            String contenido,
            Font fuenteTitulo,
            Font fuenteTexto) throws DocumentException {

        Paragraph tituloSeccion = new Paragraph(
                encabezado, fuenteTitulo);
        tituloSeccion.setSpacingBefore(20);
        tituloSeccion.setSpacingAfter(8);
        document.add(tituloSeccion);

        Paragraph textoSeccion = new Paragraph(
                contenido != null ? contenido : "",
                fuenteTexto);
        textoSeccion.setLeading(16);
        document.add(textoSeccion);
    }

}
