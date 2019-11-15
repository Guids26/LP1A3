package Lista02;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import Lista02.FormCadCli;

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
                             "email varchar(100) );";
       try{
           Statement stmt = conexao.createStatement();
           stmt.execute(tabelaPessoa);
       }catch (SQLException e) {
           JOptionPane.showMessageDialog(null, "ERRO AO INSERIR DADOS NO BANCO" + e.getMessage());
       }
       desconectar();
   }

   public void insereDados(int id, String nome, String telResidencial, String telComercial, String telCelular, String email){
       conectar();
       String insereSQL = "INSERT INTO Clientes(id, nome, telResidencial, telComercial, telCelular, email) VALUES(?,?,?,?,?,?)";
       try(Connection conn = this.getConexao();
           PreparedStatement pstmt = conn.prepareStatement(insereSQL)){
           pstmt.setInt(1,id);
           pstmt.setString(2,nome);
           pstmt.setString(3, telResidencial);
           pstmt.setString(4, telComercial);
           pstmt.setString(5, telCelular);
           pstmt.setString(6, email);
           pstmt.executeUpdate();
           JOptionPane.showMessageDialog(null, "Inserido com sucesso!");
       }catch (SQLException e){
           JOptionPane.showMessageDialog(null,"ERRO AO INSERIR NO BANCO: " + e.getMessage());
       }
       desconectar();
   }

   public void alteraDados(String nome, String telResidencial, String telComercial, String telCelular, String email, int id){
       conectar();
       String alteraSQL = "UPDATE Clientes" +
                            " SET nome = ?, telResidencial = ?, telComercial = ?, telCelular = ?, email = ? " +
                            "WHERE id = ?";
       try(Connection conn = this.getConexao();
           PreparedStatement pstmt = conn.prepareStatement(alteraSQL)){
           pstmt.setString(1, nome);
           pstmt.setString(2, telResidencial);
           pstmt.setString(3, telComercial);
           pstmt.setString(4, telCelular);
           pstmt.setString(5, email);
           pstmt.setInt(6,id);
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

   public ResultSet selectById(int id){
       String selectByIdSQL = "SELECT * FROM Clientes WHERE id = " + id;
       conectar();
       try(Connection conn = this.getConexao()){
           Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(selectByIdSQL);
           return rs;
       }catch(SQLException e){
           JOptionPane.showMessageDialog(null, "ERRO AO CONSULTAR CLIENTE NO BANCO: " + e.getMessage());
       }
       desconectar();
       return null;

   }

   public Connection getConexao (){
       return this.conexao;
   }

   public ResultSet getResult(ResultSet rs){
       return rs;
   }

}
