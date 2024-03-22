/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadEuroChem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author phsto
 */
public class DAONotaFiscal {
    private Connection con;
    private PreparedStatement comando;
    
    private void conectar(){
        con = FabricaConexao.conexao();
    }

    private void fechar(){
        try{
            if(comando != null){
                comando.close();
            }
            if(con!= null){
                con.close();
            }
        }catch(SQLException e){
            System.out.println("Erro ao fechar conexão\n" + e.getMessage());
        }
    }

    public boolean inserirNota(Fornecedor fornecedor, int tempoSegundos) {
        conectar();
        String sql = "INSERT INTO NotaFiscal (numeroFornecedor, nameFornecedor, numeroCnpj, dateEmissao, dataRecebimento, tempoSegundos) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            comando = con.prepareStatement(sql);
            comando.setInt(1, fornecedor.getNumeroFornecedor());
            comando.setString(2, fornecedor.getNameFornecedor());
            comando.setString(3, fornecedor.getNumeroCnpj());
            comando.setString(4, fornecedor.getDateEmissao());
            comando.setString(5, fornecedor.getDataRecebimento());
            comando.setLong(6, tempoSegundos);
            comando.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir nota fiscal\n" + e.getMessage());
        } finally {
            fechar();
        }
        return false;
    }

    public Fornecedor consultarFornecedor(int numeroFornecedor) {
        conectar();
        String sql = "SELECT * FROM NotaFiscal WHERE numeroFornecedor = ?";

        try {
            comando = con.prepareStatement(sql);
            comando.setInt(1, numeroFornecedor);
            ResultSet resultado = comando.executeQuery();

            if (resultado.next()) {
                int numero = resultado.getInt("numeroFornecedor");
                String nome = resultado.getString("nameFornecedor");
                String cnpj = resultado.getString("numeroCnpj");
                String dataRecebimento = resultado.getString("dataRecebimento");

                return new Fornecedor(numero, nome, cnpj, null, dataRecebimento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao consultar o fornecedor\n" + e.getMessage());
        } finally {
            fechar();
        }
        return null;
    }

    public boolean atualizarTempoFornecedor(int numeroFornecedor, int tempoSegundos) {
        conectar();
        String sql = "UPDATE NotaFiscal SET tempoSegundos = ? WHERE numeroFornecedor = ?";

        try {
            comando = con.prepareStatement(sql);
            comando.setLong(1, tempoSegundos);
            comando.setInt(2, numeroFornecedor);
            comando.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar o tempo do fornecedor\n" + e.getMessage());
        } finally {
            fechar();
        }
        return false;
    }
    
    public List<Fornecedor> obterFornecedores() {
        conectar();
        List<Fornecedor> fornecedores = new ArrayList<>();
        String sql = "SELECT * FROM NotaFiscal";

        try {
            comando = con.prepareStatement(sql);
            ResultSet resultado = comando.executeQuery();

            while (resultado.next()) {
                int numero = resultado.getInt("numeroFornecedor");
                String nome = resultado.getString("nameFornecedor");
                String cnpj = resultado.getString("numeroCnpj");
                String emissao = resultado.getString("dateEmissao");
                String recebimento = resultado.getString("dataRecebimento");
                long tempoSegundos = resultado.getLong("tempoSegundos");

                Fornecedor fornecedor = new Fornecedor(numero, nome, cnpj, emissao, recebimento);
                fornecedor.setTempoSegundos(tempoSegundos);
                fornecedores.add(fornecedor);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter fornecedores\n" + e.getMessage());
        } finally {
            fechar();
        }

        return fornecedores;
    }
}
