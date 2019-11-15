package Lista02;

import javax.swing.*;
import java.sql.SQLException;

public class FormExe {
    public static void main (String args[]) throws SQLException {
        SwingUtilities.invokeLater(() -> new FormCadCli().setVisible(true));







    }

}
