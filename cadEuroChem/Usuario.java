/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadEuroChem;
/**
 *
 * @author phsto
 */
public class Usuario {
    private String Name;
    private String Email;
    private String Password;
    private String Telefone;
    
    public Usuario(){};
    
    public Usuario(String name, String password, String email, String telefone) {
        this.Name = name;
        this.Email = email;
        this.Telefone = telefone;
        this.Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getTelefone() {
        return Telefone;
    }

    public void setTelefone(String Telefone) {
        this.Telefone = Telefone;
    }   
    
    @Override
    public String toString(){
        return "Nome: = " + Name + ", "
                + "Email: = " + Email + ", Telefone: = " +
                Telefone + ", Senha: = " + Password + ']';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Usuario usuario = (Usuario) obj;
        return Name.equals(usuario.Name) && Password.equals(usuario.Password) && 
                Email.equals(usuario.Email) && Telefone.equals(usuario.Telefone);
    }    
}
