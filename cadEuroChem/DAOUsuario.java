package cadEuroChem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DAOUsuario {
    private Connection con;
    private PreparedStatement comando;
    
    private void conectar() {
        con = FabricaConexao.conexao();
    }
    
    private void fechar() {
        try {
            if (comando != null) {
                comando.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão\n" + e.getMessage());
        }
    }
    
    public boolean inserirUsuario(Usuario usuario) {
        conectar();
        String sql = "INSERT INTO Usuario (NomeUsuario, Email, Senha, Telefone) VALUES (?, ?, ?, ?)";
        
        try {
            comando = con.prepareStatement(sql);
            comando.setString(1, usuario.getName());
            comando.setString(2, usuario.getEmail());
            comando.setString(3, usuario.getPassword());
            comando.setString(4, usuario.getTelefone());
            comando.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir usuário\n" + e.getMessage());
        } finally {
            fechar();
        }
        return false;
    }
    public boolean verificarLogin(String name, String senha) {
        conectar();
        String sql = "SELECT * FROM Usuario WHERE NomeUsuario = ? AND Senha = ?";

        try {
            comando = con.prepareStatement(sql);
            comando.setString(1, name);
            comando.setString(2, senha);
            ResultSet resultSet = comando.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Erro ao verificar o login\n" + e.getMessage());
        } finally {
            fechar();
        }
        return false;
    }

}
