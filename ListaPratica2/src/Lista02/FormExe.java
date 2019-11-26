package Lista02;

import javax.swing.*;
import java.sql.SQLException;
import java.text.ParseException;

public class FormExe {
    public static void main (String args[]) throws SQLException {
        SwingUtilities.invokeLater(() -> {
            try {
                new FormCadCli().setVisible(true);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "ERRO AO INICIAR APLICAÇÃO: "+ e.getMessage());
            }
        });







    }

}
