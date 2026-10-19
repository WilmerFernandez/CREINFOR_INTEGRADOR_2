package utp_integrador_2.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException; // Importar SQLException
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DTO.SolicitudDetalleDTO;
import utp_integrador_2.facade.SolicitudFacade;

@WebServlet("/solicitudes") // CAMBIADO a /admin/solicitudes para coincidir con el JSP
public class AdminSolicitudesServlet extends HttpServlet {

    // El facade ya no se inicializa en init() porque necesita la conexión del request
    // private SolicitudFacade solicitudFacade; 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection connection = null; // Declara la conexión fuera del try para asegurarte de cerrarla
        try {
            // 1. Obtener la conexión a la base de datos
            connection = BDConnection.getConnection(); // Obtiene la conexión

            // 2. Crear una instancia del Facade, pasándole la conexión
            // El Facade, a su vez, pasará esta conexión al DAO.
            SolicitudFacade solicitudFacade = new SolicitudFacade(connection);

            // Obtener parámetros de búsqueda (pueden ser null o vacíos si no se buscaron)
            String tipo_solicitud = request.getParameter("tipo_solicitud");
            String colaborador = request.getParameter("colaborador");
            String estado = request.getParameter("estado");
            
            // 3. Obtener la lista de solicitudes con detalles para el administrador
            // Pasa los parámetros al facade.
            List<SolicitudDetalleDTO> solicitudes = solicitudFacade.obtenerTodasLasSolicitudesParaAdmin(tipo_solicitud, colaborador, estado);
            
            // 4. Establecer la lista de solicitudes como un atributo de la solicitud
            // para que esté disponible en el JSP
            request.setAttribute("solicitudes", solicitudes);

            // 5. Reenviar la solicitud y la respuesta a la página JSP
            // Ruta completa y correcta para el JSP dentro de WEB-INF
            request.getRequestDispatcher("listaSolicitudes.jsp").forward(request, response);
            
        } catch (SQLException e) {
            // Manejo de errores de SQL: por ejemplo, problemas de conexión o consulta
            e.printStackTrace(); // En un entorno real, usarías un sistema de logging
            request.setAttribute("errorMessage", "Error de base de datos al cargar las solicitudes. " + e.getMessage());
            // Puedes redirigir a una página de error genérica
            request.getRequestDispatcher("errorPage.jsp").forward(request, response); // Ruta completa
        } catch (RuntimeException e) {
            // Manejo de errores de lógica de negocio (lanzados por el Facade)
            e.printStackTrace(); // En un entorno real, usarías un sistema de logging
            request.setAttribute("errorMessage", "Error al procesar la solicitud: " + e.getMessage());
            request.getRequestDispatcher("errorPage.jsp").forward(request, response); // Ruta completa
        } finally {
            // Asegurarse de cerrar la conexión a la base de datos
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace(); // Error al cerrar la conexión
                }
            }
        }
    }
}