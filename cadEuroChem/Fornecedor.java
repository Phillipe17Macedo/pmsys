/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadEuroChem;

/**
 *
 * @author phsto
 */
public class Fornecedor {
    private int numeroFornecedor;
    private String nameFornecedor;
    private String numeroCnpj;
    private String dateEmissao;
    private String dataRecebimento;
    private long tempoSegundos;
    
    public Fornecedor(){};
    
    public Fornecedor(int numFornecedor, String nomeFornecedor, String numCnpj, String dtEmissao, String dtRecebimento){
        this.numeroFornecedor = numFornecedor;
        this.nameFornecedor = nomeFornecedor;
        this.numeroCnpj = numCnpj;
        this.dateEmissao = dtEmissao;
        this.dataRecebimento = dtRecebimento;
    }

    public String getNameFornecedor() {
        return nameFornecedor;
    }

    public int getNumeroFornecedor() {
        return numeroFornecedor;
    }
    public String getNumeroCnpj() {
        return numeroCnpj;
    }

    public String getDateEmissao() {
        return dateEmissao;
    }

    public String getDataRecebimento() {
        return dataRecebimento;
    }
    public long getTempoSegundos() {
        return tempoSegundos;
    }
    public void setTempoSegundos(long tempoSegundos) {
        this.tempoSegundos = tempoSegundos;
    }
    @Override
    public String toString(){
        return "Numero: = " + numeroFornecedor + ", "
                + "Nome: = " + nameFornecedor + ", CNPJ: = " +
                numeroCnpj + ", EMISSAO: + " + dateEmissao + 
                ", RECEBIMENTO: = " + dataRecebimento + ", TEMPO: = " + tempoSegundos + ']';
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Fornecedor fornecedores = (Fornecedor) obj;
        return numeroFornecedor == fornecedores.numeroFornecedor && nameFornecedor.equals(fornecedores.nameFornecedor) && 
                numeroCnpj == fornecedores.numeroCnpj && dateEmissao == fornecedores.dateEmissao && dataRecebimento == fornecedores.dataRecebimento
                && tempoSegundos == fornecedores.tempoSegundos;
    }    
}
