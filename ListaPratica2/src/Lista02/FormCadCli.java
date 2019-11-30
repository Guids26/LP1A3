package Lista02;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class FormCadCli extends JFrame {
    private JPanel rootPanel;
    private JTextField txtClienteId;
    private JTextField txtClienteNome;
    private JFormattedTextField txtClienteTelResidencial;
    private JFormattedTextField txtClienteTelComercial;
    private JFormattedTextField txtClienteTelCelular;
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
    private JButton btnArquivo;
    private JLabel lblImagem;
    Banco bd = new Banco();
    Boolean inserindo = true;



    public FormCadCli () throws ParseException {

        add(rootPanel);
        setTitle("Cadastro de Clientes");
        /*DEFINE AÇÃO DE FEHCAR APLICAÇÃO AO CLICAR NO X*/
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(360, 0, 800, 600);
        setExtendedState(MAXIMIZED_BOTH);
        DefaultTableModel model = (DefaultTableModel)tblClientesCadastrados.getModel();
        /*ADICIONA COLUNAS NA TABELA*/
        model.addColumn("ID");
        model.addColumn("Nome");
        model.addColumn("Email");
        bd.criaNovaTabela();
        bd.selectAll(model);
        /*ADICIONANDO MASCARA NOS CAMPOS DE TELEFONE*/
        MaskFormatter numMask = new MaskFormatter("(##)#####-####");
        MaskFormatter numMask2 = new MaskFormatter("(##)####-####");
        MaskFormatter numMask3 = new MaskFormatter("(##)#####-####");
        numMask.setPlaceholder("_");
        numMask2.setPlaceholder("_");
        numMask3.setPlaceholder("_");
        numMask.install(txtClienteTelCelular);
        numMask2.install(txtClienteTelResidencial);
        numMask3.install(txtClienteTelComercial);
        tblClientesCadastrados.setDefaultEditor(Object.class, null);


        File[] file = {null};
        lblImagem.setIcon(new ImageIcon("imagens/image-null-batman.jpg"));
        file[0] = new File("imagens/image-null-batman.jpg");



        /*AÇÕES DOS COMPONENTES*/
        btnInserir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HabilitarComponente(true);
                ZerarCampos();
                btnRemover.setEnabled(false);
                btnAlterar.setEnabled(false);
                btnInserir.setEnabled(false);
                int totlinha = tblClientesCadastrados.getRowCount();
                int id = totlinha;
                if (totlinha > 0) {
                     id = (Integer) tblClientesCadastrados.getValueAt(totlinha - 1, 0);
                }
                int proximoId = id + 1 ;
                txtClienteId.setText(Integer.toString(proximoId));
                inserindo = true;
            }
        });

        btnConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(validaEmail() && VerificaCampos()) {
                    btnInserir.setEnabled(true);
                    btnConfirmar.setEnabled(false);
                    btnCancelar.setEnabled(false);
                    btnArquivo.setEnabled(true);
                    HabilitarComponente(false);
                    byte[] img = null;
                    try {
                        img = Files.readAllBytes(file[0].toPath());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(rootPanel, "ERRO AO INSERIR IMAGEM: " + ex.getMessage() );
                    }

                    if(inserindo) {
                        Boolean insert = bd.insereDados(Integer.parseInt(txtClienteId.getText()), txtClienteNome.getText(), txtClienteTelResidencial.getText(), txtClienteTelComercial.getText(), txtClienteTelCelular.getText(), txtClienteEmail.getText(), img);
                    }
                    else {
                        int id = Integer.parseInt(txtClienteId.getText());
                        bd.alteraDados(txtClienteNome.getText(), txtClienteTelResidencial.getText(), txtClienteTelComercial.getText(), txtClienteTelCelular.getText(), txtClienteEmail.getText(), img, id);
                    }
                    bd.selectAll(model);
                    ZerarCampos();

                }
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
                btnAlterar.setEnabled(false);
                btnRemover.setEnabled(false);
                inserindo = false;
            }
        });
        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*PERGUNTA SE DESEJA EXCLUIR E VERIFICA SE A RESPOSTA FOI SIM*/
                int DialogResult = JOptionPane.showConfirmDialog(rootPanel, "Deseja remover?");
                if (DialogResult == JOptionPane.YES_OPTION){
                int linha = tblClientesCadastrados.getSelectedRow();
                /*PEGA O VALOR DO CAMPO ID, CONVERTE PARA INT E CHAMA A FUNÇÃO PRA DELETAR PASSANDO O ID COMO PARAMETRO*/
                int id = (int) model.getValueAt(linha, 0);
                bd.deletaDados(id);
                ZerarCampos();
                btnAlterar.setEnabled(false);
                btnRemover.setEnabled(false);
                btnInserir.setEnabled(true);
                bd.selectAll(model);}
            }
        });

        tblClientesCadastrados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(tblClientesCadastrados.getSelectedRowCount() > 0){
                    HabilitarComponente(false);
                    btnInserir.setEnabled(true);
                    btnAlterar.setEnabled(true);
                    btnRemover.setEnabled(true);
                    /*PEGA VALOR DO ID DA LINHA QUE FOI CLICADA*/
                    int linha = tblClientesCadastrados.getSelectedRow();
                    int id = (Integer) tblClientesCadastrados.getValueAt(linha, 0);
                    try {
                    /*FAZ UMA CONSULTA NO BANCO COM O ID*/
                        String result[] = bd.selectById(id);
                    /*PREENCHE CAMPOS COM O RESULTADO DA CONSULTA*/
                        PreencherCampos(result);
                        File img = bd.selectPhotoById(id);
                        BufferedImage bimg = ImageIO.read(img);
                        bimg.getScaledInstance(lblImagem.getWidth(), lblImagem.getHeight(), Image.SCALE_SMOOTH);
                        lblImagem.setIcon(new ImageIcon(bimg));
                    } catch (SQLException | IOException   ex) {
                        JOptionPane.showMessageDialog(rootPanel, "ERRO NA CONSULTA PARA OS CAMPOS: " + ex.getMessage());
                    } finally {

                        bd.desconectar();
                    }
                }
            }
        });
        btnArquivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Inserir foto");
                fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagem", "jpg","png");
                fileChooser.setFileFilter(filter);
                int retorno = fileChooser.showOpenDialog(rootPanel);

                try {
                    file[0] = fileChooser.getSelectedFile();

                    if (retorno == JFileChooser.APPROVE_OPTION) {
                        lblImagem.setIcon(new ImageIcon(file[0].getPath()));
                        JOptionPane.showMessageDialog(rootPanel,"Imagem inserida");
                    }

                }
                catch (Exception e1){
                    JOptionPane.showMessageDialog(rootPanel,"Imagem não inserida: " + e1.getMessage());
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
        btnArquivo.setEnabled(SimOuNao);
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
        lblImagem.setIcon(new ImageIcon("imagens/image-null-batman.jpg"));


    }

    public void PreencherCampos(String[] result) throws SQLException {
        txtClienteId.setText(result[0]);
        txtClienteNome.setText(result[1]);
        txtClienteTelResidencial.setText(result[2]);
        txtClienteTelComercial.setText(result[3]);
        txtClienteTelCelular.setText(result[4]);
        txtClienteEmail.setText(result[5]);
    }

    public Boolean validaEmail(){
        String email = txtClienteEmail.getText();
        if(email != "" || email != null) {
            int i = 0;
            boolean temarroba = false;
            boolean emailvalido = false;

            for (i = 0; i < email.length(); i++) {
                if (email.charAt(i) == '@') {
                    temarroba = true;
                }
                if (temarroba == true) {
                    if (email.charAt(i) == '.') {
                        return true;
                    }
                }
            }
            JOptionPane.showMessageDialog(rootPanel, "Email inválido! Por favor, confira o endereço inserido!");
        }
            return false;
    }

    public Boolean VerificaCampos (){
        String valores [] = {txtClienteId.getText(), txtClienteNome.getText(), txtClienteTelResidencial.getText(), txtClienteTelComercial.getText(), txtClienteTelCelular.getText(), txtClienteEmail.getText()};
        for (int i = 0; i <5 ; i++){
            if (valores[i].equals("") || valores[i] == null || valores[i].equals("(  )     -    ") || valores[i].equals("(  )    -    ")){
                JOptionPane.showMessageDialog(rootPanel, "Por favor preencha todos os campos obrigatórios!");
                return false;
            }
        }
        return true;
    }
}
