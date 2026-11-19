
package utp_integrador_2.controller;

import java.sql.Connection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.CotizacionFacade;

@WebListener
public class VencimientoCotizacionesListener
        implements ServletContextListener {

    private static final Logger LOGGER =
            Logger.getLogger(VencimientoCotizacionesListener.class.getName());

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread hilo = new Thread(r, "creinfor-vencimiento-cotizaciones");
            hilo.setDaemon(true);
            return hilo;
        });

        scheduler.scheduleWithFixedDelay(() -> {

            try (Connection connection = BDConnection.getConnection()) {

                CotizacionFacade facade = new CotizacionFacade(connection);

                int cantidad = facade.procesarCotizacionesVencidas();

                if (cantidad > 0) {
                    LOGGER.info(
                            "Cotizaciones vencidas procesadas: " + cantidad
                    );
                }

            } catch (Exception e) {
                LOGGER.log(
                        Level.SEVERE,
                        "Error al procesar cotizaciones vencidas",
                        e
                );
            }

        }, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
}
