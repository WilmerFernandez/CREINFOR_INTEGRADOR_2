package utp_integrador_2.facade;

import utp_integrador_2.DAO.ColaboradorDAO;
import utp_integrador_2_model.Colaborador;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ColaboradorFacade {

    private ColaboradorDAO colaboradorDAO;

    public ColaboradorFacade(Connection connection) {
        this.colaboradorDAO = new ColaboradorDAO(connection);
    }

    public List<Colaborador> listarTodos() throws SQLException {
        return colaboradorDAO.listarTodos();
    }

    public void registrarColaborador(Colaborador c) throws SQLException {
        if (colaboradorDAO.existeEmail(c.getEmail())) {
            throw new SQLException("Ya existe un colaborador registrado con ese correo.");
        }

        colaboradorDAO.insertar(c);
    }

    public Colaborador buscarPorId(int idColaborador) throws SQLException {
        return colaboradorDAO.buscarPorId(idColaborador);
    }

    public void actualizarColaborador(Colaborador c) throws SQLException {
        if (colaboradorDAO.existeEmailEnOtroColaborador(
                c.getEmail(),
                c.getIdColaborador())) {

            throw new SQLException(
                    "Ese correo ya pertenece a otro colaborador."
            );
        }

        colaboradorDAO.actualizar(c);
    }
}