package Lista02;

import javax.swing.*;
import java.awt.Toolkit;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FormCadCli extends JFrame {
    private JPanel rootPanel;
    private JTextField txtClienteId;
    private JTextField txtClienteNome;
    private JTextField txtClienteTelResidencial;
    private JTextField txtClienteTelComercial;
    private JTextField txtClienteTelCelular;
    private JTextField txtClienteEmail;
    private JButton btnInserir;
    private JButton btnRemover;
    private JButton btnAlterar;
    private JButton btnConfirmar;
    private JButton btnCancelar;
    private JButton btnSair;
    private JLabel lblClienteNome;
    private JLabel lblClienteId;
    private JLabel lblClienteTelResidencial;
    private JLabel lblClienteTelComercial;
    private JLabel lblClientetelCelular;
    private JLabel lblClienteEmail;
    private JLabel lblClientesCadastrados;
    private JScrollPane scpClientesCadastrados;
    private JTable tblClientesCadastrados;
    Banco bd = new Banco();



    public FormCadCli (){
        add(rootPanel);
        setTitle("Cadastro de Clientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int largura = Toolkit.getDefaultToolkit().getScreenSize().width/2;
        setBounds(largura, 0, 800, 600);
       // setExtendedState(MAXIMIZED_BOTH);
        DefaultTableModel model = (DefaultTableModel)tblClientesCadastrados.getModel();
        model.addColumn("ID");
        model.addColumn("Nome");
        model.addColumn("Email");
        bd.criaNovaTabela();
        bd.selectAll(model);


        /*AÇÕES DOS COMPONENTES*/
        btnInserir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            HabilitarComponente(true);
            btnInserir.setEnabled(false);
            int totlinha = tblClientesCadastrados.getRowCount();
            txtClienteId.setText(Integer.toString(totlinha+1));
                btnConfirmar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnInserir.setEnabled(true);
                        btnConfirmar.setEnabled(false);
                        btnCancelar.setEnabled(false);
                        HabilitarComponente(false);
                        bd.insereDados(Integer.parseInt(txtClienteId.getText()), txtClienteNome.getText(), txtClienteTelResidencial.getText(), txtClienteTelComercial.getText(), txtClienteTelCelular.getText(), txtClienteEmail.getText());
                        bd.selectAll(model);
                        ZerarCampos();
                    }
                });
            }
        });
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HabilitarComponente(false);
                btnInserir.setEnabled(true);
                ZerarCampos();
            }
        });
        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnAlterar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HabilitarComponente(true);
                btnInserir.setEnabled(false);
                btnConfirmar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        btnInserir.setEnabled(true);
                        btnConfirmar.setEnabled(false);
                        btnCancelar.setEnabled(false);
                        HabilitarComponente(false);
                        ZerarCampos();

                    }
                });

            }
        });
        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(rootPanel, "Deseja remover?");
                int linha = tblClientesCadastrados.getSelectedRow();
                int id = (int) model.getValueAt(linha, 0);
                bd.deletaDados(id);
                bd.selectAll(model);
            }
        });

        tblClientesCadastrados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(tblClientesCadastrados.getSelectedRowCount() > 0){
                    HabilitarComponente(false);
                    btnAlterar.setEnabled(true);
                    btnRemover.setEnabled(true);
                    int linha = tblClientesCadastrados.getSelectedRow();
                    int id = (Integer) tblClientesCadastrados.getValueAt(linha, 0);
                    try {
                        ResultSet rs = bd.selectById(id);
                        PreencherCampos(rs);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(rootPanel, "DEU ERRO NA CONSULTA NOS TXTFIELD FOPDASE");
                    }
                }
            }
        });
    }
/*METODOS*/
    public void HabilitarComponente(boolean SimOuNao){
        txtClienteNome.setEnabled(SimOuNao);
        txtClienteEmail.setEnabled(SimOuNao);
        txtClienteTelResidencial.setEnabled(SimOuNao);
        txtClienteTelComercial.setEnabled(SimOuNao);
        txtClienteTelCelular.setEnabled(SimOuNao);
        btnCancelar.setEnabled(SimOuNao);
        btnConfirmar.setEnabled(SimOuNao);
    }

    public void ZerarCampos() {
        txtClienteId.setText("");
        txtClienteNome.setText("");
        txtClienteTelCelular.setText("");
        txtClienteTelComercial.setText("");
        txtClienteTelResidencial.setText("");
        txtClienteEmail.setText("");
    }

    public void PreencherCampos(ResultSet rs) throws SQLException {
        txtClienteId.setText(Integer.toString(rs.getInt("id")));
        txtClienteNome.setText(rs.getString("nome"));
        txtClienteTelResidencial.setText(rs.getString("telResidencial"));
        txtClienteTelComercial.setText(rs.getString("telComercial"));
        txtClienteTelCelular.setText(rs.getString("telCelular"));
        txtClienteEmail.setText(rs.getString("email"));
    }



}
