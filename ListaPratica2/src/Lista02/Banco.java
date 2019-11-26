package Lista02;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.*;

public class Banco {
   private final String CAMINHODB = "jdbc:sqlite:Banco/banco.db";
   private Connection conexao;


   public Banco ( ){

   }

    /*Conecta no bando e cria se o banco não existir*/
   public boolean conectar(){
       try{
           this.conexao = DriverManager.getConnection(CAMINHODB);

       }catch (SQLException e) {
           JOptionPane.showMessageDialog(null, " ERRO AO CONECTAR AO BANCO: " + e.getMessage());
           return false;
       }
       return true;
   }
   public boolean desconectar(){
       try{
           if(this.conexao.isClosed() == false){
               this.conexao.close();
               System.out.println("desconectado");
           }
       }catch (SQLException e){
           JOptionPane.showMessageDialog(null, "ERRO AO DESCONECTAR DO BANCO: " + e.getMessage());
            return false;
       }
       return true;
   }

    public void criaNovaTabela(){
        conectar();
        String tabelaPessoa = "CREATE TABLE IF NOT EXISTS Clientes(\n"+
                "id integer PRIMARY KEY UNIQUE,\n" +
                "nome varchar(30) not null,\n" +
                "telResidencial varchar(14) ,\n" +
                "telComercial varchar(14) ,\n" +
                "telCelular varchar(14) ,\n" +
                "email varchar(100) not null,\n"
                + " foto BLOB );";
        try{
            Statement stmt = conexao.createStatement();
            stmt.execute(tabelaPessoa);
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERRO AO INSERIR DADOS NO BANCO" + e.getMessage());
        }
        desconectar();
    }

    public void insereDados(int id, String nome, String telResidencial, String telComercial, String telCelular, String email, byte[] img){
        conectar();
        String insereSQL = "INSERT INTO Clientes(id, nome, telResidencial, telComercial, telCelular, email, foto) VALUES(?,?,?,?,?,?,?)";
        try(Connection conn = this.getConexao();
            PreparedStatement pstmt = conn.prepareStatement(insereSQL)){
            pstmt.setInt(1,id);
            pstmt.setString(2,nome);
            pstmt.setString(3, telResidencial);
            pstmt.setString(4, telComercial);
            pstmt.setString(5, telCelular);
            pstmt.setString(6, email);
            pstmt.setBytes(7, img);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Inserido com sucesso!");
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null,"ERRO AO INSERIR NO BANCO: " + e.getMessage());
        }
        desconectar();
    }

   public void alteraDados(String nome, String telResidencial, String telComercial, String telCelular, String email, byte[] img, int id){
       conectar();
       String alteraSQL = "UPDATE Clientes" +
                            " SET nome = ?, telResidencial = ?, telComercial = ?, telCelular = ?, email = ?, foto = ? " +
                            "WHERE id = ?";
       try(Connection conn = this.getConexao();
           PreparedStatement pstmt = conn.prepareStatement(alteraSQL)){
           pstmt.setString(1, nome);
           pstmt.setString(2, telResidencial);
           pstmt.setString(3, telComercial);
           pstmt.setString(4, telCelular);
           pstmt.setString(5, email);
           pstmt.setBytes(6, img);
           pstmt.setInt(7,id);
           pstmt.executeUpdate();
           JOptionPane.showMessageDialog(null, "Atualizado com sucesso!");
       }catch (SQLException e){
           JOptionPane.showMessageDialog(null, "ERRO AO ATUALIZAR DADOS NO BANCO: " + e.getMessage());
       }
       desconectar();
   }

   public void deletaDados(int id){
       conectar();
       String deletaSQL = "DELETE " +
                          "FROM Clientes " +
                          "WHERE id = ?";
       try(Connection conn =  this.getConexao();
           PreparedStatement pstmt = conn.prepareStatement(deletaSQL)){
               pstmt.setInt(1, id);
               pstmt.executeUpdate();
               JOptionPane.showMessageDialog(null, "DELETADO COM SUCESSO");
       }
       catch (SQLException e){
           JOptionPane.showMessageDialog(null, "ERRO AO DELETAR NO BANCO: " + e.getMessage());
       }
       desconectar();
   }

   public void selectAll (DefaultTableModel model){
       conectar();
       String selectAllSQL = "SELECT * FROM Clientes";
       try(Connection conn = this.getConexao();
            Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(selectAllSQL)) {
           model.setNumRows(0);
           while (rs.next()) {
               model.addRow(new Object[]{
                       rs.getInt("id"),
                       rs.getString("nome"),
                       rs.getString("email")
               });
           }
       }
       catch (SQLException e) {
           JOptionPane.showMessageDialog(null, "ERRO AO CONSULTAR BANCO DE DADOS " + e.getMessage());
       }
       desconectar();
   }

    public String[] selectById(int id){
        String selectByIdSQL = "SELECT * FROM Clientes WHERE id = " + id;
        conectar();
        try(Connection conn = this.getConexao()){
            PreparedStatement stmt = conn.prepareStatement(selectByIdSQL);
            ResultSet rs = stmt.executeQuery();
            String result[] = {Integer.toString(rs.getInt("id")),rs.getString("nome"),rs.getString("telResidencial"),
                    rs.getString("telComercial"),rs.getString("telCelular"),rs.getString("email")};
            return result;
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "ERRO AO CONSULTAR CLIENTE NO BANCO: " + e.getMessage());
        }
        return new String[0];
    }

    public File selectPhotoById (int id) throws IOException {
    String selectPhotoByIdSQL = "SELECT foto FROM Clientes WHERE id = " + id;
    conectar();
        File imgPerfilBanco = new File (String.valueOf(Files.createTempFile("fotoFromDb"+id, ".jpeg")));
        FileOutputStream output = new FileOutputStream(imgPerfilBanco);
    try(Connection conn = this.getConexao()){
        PreparedStatement stmt = conn.prepareStatement(selectPhotoByIdSQL);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()){
            InputStream input = rs.getBinaryStream("foto");
            byte[] buffer = new byte[4096];
            while(input.read(buffer) > 0){
                output.write(buffer);
            }
        }
    }catch (SQLException | IOException e){
        JOptionPane.showMessageDialog(null, "ERRO AO CONSULTAR FOTO NO BANCO: " + e.getMessage());
    }
    finally {
        return imgPerfilBanco;
    }
    }

    public String selectMaxId (){
       String selectMaxIdSQL = "SELECT MAX(id) FROM Clientes;";
       String Result;
         conectar();
        try(Connection conn = this.getConexao();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectMaxIdSQL)) {
            Result = rs.getString("id");
            return Result;
            }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERRO AO CONSULTAR ID NO BANCO DE DADOS " + e.getMessage());
            Result = "0";
        }
        desconectar();
        return Result;
   }

   public Connection getConexao (){
       return this.conexao;
   }

}
