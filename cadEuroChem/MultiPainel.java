/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cadEuroChem;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import cadEuroChem.MultiPainel;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.AbstractTableModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 *
 * @author phsto
 */
public class MultiPainel extends javax.swing.JFrame {
    private List<Fornecedor> fornecedores = new ArrayList<>();
    private Map<Integer, String> numeroParaNome = new HashMap<>();
    private Map<Integer, String> numeroParaCnpj = new HashMap<>();
    private LoginJFrame loginJFrame;
    private Timer timer;
    private int segundos = 0;
    private DAONotaFiscal daoNotaFiscal = new DAONotaFiscal();
    Workbook workbook = new XSSFWorkbook();
    public MultiPainel(){
        initComponents();
    }
    
    public MultiPainel(LoginJFrame loginFrame) {
        initComponents();
        setExtendedState(MAXIMIZED_BOTH);
        
        // COLOCAR O ÍCONE DO PROGRAMA NO CANTO DA PÁGINA
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/cadEuroChem/img/logoIcon.png")));
        
        this.loginJFrame = loginFrame; // Armazene a referência para uso posterior
        
        DAONotaFiscal daoNotaFiscal = new DAONotaFiscal();
        fornecedores = daoNotaFiscal.obterFornecedores();
        
        // Configurar a tabela
        configurarTabela(fornecedores);
        
        //Preenche automaticamente os meus JComboBoxes
        preencherComboBoxes();
        
        numFornecedorTempo.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarNomeFornecedor();
                atualizarCnpjFornecedor();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarNomeFornecedor();
                atualizarCnpjFornecedor();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarNomeFornecedor();
                atualizarCnpjFornecedor();
            }
        });
        
        buttonCadastrarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Código para abrir a janela de cadastro (SingupJFrame)
                SingupJFrame singupFrame = new SingupJFrame();
                singupFrame.setVisible(true);
            }
        });
        
        timer = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                segundos++;
                atualizarCronometro();
            }
        });
        playTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarCronometro();
            }
        });

        pauseTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pausarCronometro();
            }
        });

        resetTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarCronometro();
            }
        });
    }
    public Fornecedor getCadastroFornecedor(){
        int auxNumFornecedor = Integer.parseInt(campoNumeroFornecedor.getText()); // Obtém o valor do campo de número
        String auxNameFornecedor = campoNameFornecedor.getText(); // Obtém o valor do campo de nome
        String auxCNPJ = campoCnpjFornecedor.getText();
        String auxEmissao = campoDataEmissao.getText();
        String auxRecebimento = campoDataRecebimento.getText();
        return new Fornecedor(auxNumFornecedor, auxNameFornecedor, auxCNPJ, auxEmissao, auxRecebimento);
    }
    private void atualizarNomeFornecedor() {
        String numeroTexto = numFornecedorTempo.getText();
        
        try {
            int numero = Integer.parseInt(numeroTexto);
            if (numeroParaNome.containsKey(numero)) {
                nomeFornecedorTempo.setText(numeroParaNome.get(numero));
            } else {
                nomeFornecedorTempo.setText("Fornecedor não encontrado");
            }
        } catch (NumberFormatException e) {
            nomeFornecedorTempo.setText("Número inválido");
        }
    }
    private void buscarFornecedor() {
        String numeroFornecedorTexto = numFornecedorTempo.getText();

        // Verifica se o campo não está vazio
        if (numeroFornecedorTexto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Digite o número da nota para buscar as informações.");
            return;
        }

        try {
            int numeroFornecedor = Integer.parseInt(numeroFornecedorTexto);

            // Consulta o fornecedor no banco de dados
            DAONotaFiscal daoNotaFiscal = new DAONotaFiscal(); 
           Fornecedor fornecedor = daoNotaFiscal.consultarFornecedor(numeroFornecedor);

            // Exibe os dados nos campos da interface gráfica
            if (fornecedor != null) {
                nomeFornecedorTempo.setText(fornecedor.getNameFornecedor());
                cnpjFornecedorTempo.setText(fornecedor.getNumeroCnpj());
                campoDate.setText(fornecedor.getDataRecebimento());
                
                // Obtém a data de recebimento do fornecedor
                String dataRecebimento = fornecedor.getDataRecebimento();

                // Converte a data de recebimento para um objeto LocalDate
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate dataRecebimentoLocalDate = LocalDate.parse(dataRecebimento, formatter);

                // Calcula a nova data adicionando 2 dias
                LocalDate novaData = dataRecebimentoLocalDate.plusDays(2);

                // Converte a nova data de volta para o formato de string e exibe no campoDate
                campoDate.setText(novaData.format(formatter));
            } else {
                // Caso não encontre o fornecedor
                JOptionPane.showMessageDialog(null, "Fornecedor não encontrado para o número da nota " + numeroFornecedor);
                limparCamposFornecedor();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Número da nota inválido");
            limparCamposFornecedor();
        }
    }
    private void preencherComboBoxes(){
        // Preencher listaNomeFornecedor com os nomes dos fornecedores
        List<String> nomes = fornecedores.stream()
                .map(Fornecedor::getNameFornecedor)
                .collect(Collectors.toList());
        listaNomeFornecedor.setModel(new DefaultComboBoxModel<>(nomes.toArray(new String[0])));
        
        // Preencher listaStatusFornecedor com os prazos
        List<String> prazos = fornecedores.stream()
                .map(Fornecedor::getDataRecebimento) // Aqui você pode ajustar para pegar o prazo correto
                .collect(Collectors.toList());
        listaStatusFornecedor.setModel(new DefaultComboBoxModel<>(prazos.toArray(new String[0])));
    }
    private void limparCamposFornecedor() {
        nomeFornecedorTempo.setText("");
        cnpjFornecedorTempo.setText("");
        campoDate.setText("");
    }

    private void atualizarCnpjFornecedor(){
        String numeroTexto2 = numFornecedorTempo.getText();
        
        try{
            int numero2 = Integer.parseInt(numeroTexto2);
            if(numeroParaCnpj.containsKey(numero2)){
                cnpjFornecedorTempo.setText(numeroParaCnpj.get(numero2));
            }else{
                cnpjFornecedorTempo.setText("CNPJ não encontrado");
            }
        }catch(NumberFormatException e){
            cnpjFornecedorTempo.setText("Número Inválido");
            
        }
    }
    
    private void limparTexto(){
        campoNameFornecedor.setText("");
        campoNumeroFornecedor.setText("");
        campoCnpjFornecedor.setText("");
        campoDataEmissao.setText("");
        campoDataRecebimento.setText("");
    }
    private static final long MAX_TEMPO_SEGUNDOS = 2 * 24 * 60 * 60;
    
    private void atualizarCronometro(){
        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int secs = segundos % 60;
        
        campoTime.setText(String.format("%02d:%02d:%02d", horas, minutos, secs));
        // Verifica se o tempo ultrapassou 2 dias
        if (segundos >= MAX_TEMPO_SEGUNDOS) {
            pausarCronometro(); // Pára o cronômetro
            JOptionPane.showMessageDialog(this, "Tempo máximo de 2 dias atingido!");
        }        
    }
    private void iniciarCronometro(){
        timer.start();
    }
    private void pausarCronometro(){
        timer.stop();
    }
    private void reiniciarCronometro(){
        segundos = 0;
        atualizarCronometro();
    }
    private void configurarTabela(List<Fornecedor> fornecedores) {
        FornecedorTableModel modelo = new FornecedorTableModel(fornecedores);
        tabelaFornecedores.setModel(modelo);
    }
    public class FornecedorTableModel extends AbstractTableModel {
        // Colunas da tabela
        private final List<Fornecedor> fornecedores;
        private final String[] colunas = {"Número", "Nome", "CNPJ", "Data Emissão", "Data Recebimento"};

        public FornecedorTableModel(List<Fornecedor> fornecedores) {
            this.fornecedores = fornecedores;
        }

        @Override
        public int getRowCount() {
            return fornecedores.size();
        }

        @Override
        public int getColumnCount() {
            return colunas.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Fornecedor fornecedor = fornecedores.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return fornecedor.getNumeroFornecedor();
                case 1:
                    return fornecedor.getNameFornecedor();
                case 2:
                    return fornecedor.getNumeroCnpj();
                case 3:
                    return fornecedor.getDateEmissao();
                case 4:
                    return fornecedor.getDataRecebimento();
                default:
                    return null;
            }
        }

        @Override
        public String getColumnName(int column) {
            return colunas[column];
        }  
    }
    private void gerarRelatorioExcel() {
    // Crie um novo livro de trabalho do Excel
    Workbook workbook = new XSSFWorkbook();

    // Crie uma nova planilha
    Sheet sheet = workbook.createSheet("Fornecedores");

    // Adicione os títulos das colunas
    Row headerRow = sheet.createRow(0);
    for (int i = 0; i < tabelaFornecedores.getColumnCount(); i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(tabelaFornecedores.getColumnName(i));
    }

    // Adicione os dados da tabela
    for (int row = 0; row < tabelaFornecedores.getRowCount(); row++) {
        Row excelRow = sheet.createRow(row + 1); // +1 para evitar a linha de cabeçalho
        for (int col = 0; col < tabelaFornecedores.getColumnCount(); col++) {
            Object value = tabelaFornecedores.getValueAt(row, col);
            Cell cell = excelRow.createCell(col);
            if (value != null) {
                cell.setCellValue(value.toString());
            }
        }
    }

    // Salve o arquivo
        try {
            // Escolha o local onde deseja salvar o arquivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar Relatório Excel");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();

                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    workbook.write(outputStream);
                    JOptionPane.showMessageDialog(this, "Relatório Excel gerado com sucesso!");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o relatório Excel: " + e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        buttonSaveFornecedor = new javax.swing.JButton();
        buttonClearFornecedor = new javax.swing.JButton();
        campoNumeroFornecedor = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        campoNameFornecedor = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        listaNomeFornecedor = new javax.swing.JComboBox<>();
        listaStatusFornecedor = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        campoCnpjFornecedor = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        campoDataEmissao = new javax.swing.JFormattedTextField();
        campoDataRecebimento = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        nomeFornecedorTempo = new javax.swing.JTextField();
        numFornecedorTempo = new javax.swing.JTextField();
        campoTime = new javax.swing.JTextField();
        campoDate = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        cnpjFornecedorTempo = new javax.swing.JTextField();
        playTime = new javax.swing.JButton();
        pauseTime = new javax.swing.JButton();
        resetTime = new javax.swing.JButton();
        buttonSalvarTempo = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaFornecedores = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        gerarRelatorio = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        buttonCadastrarUsuario = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        buttonCloseMultiPainel = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PMSYS v0.1");
        setResizable(false);
        setSize(new java.awt.Dimension(1920, 1080));

        jTabbedPane1.setBackground(new java.awt.Color(1, 45, 69));
        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(606, 383));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(606, 383));
        jTabbedPane1.setRequestFocusEnabled(false);

        jPanel1.setBackground(new java.awt.Color(1, 45, 69));
        jPanel1.setToolTipText("");
        jPanel1.setPreferredSize(new java.awt.Dimension(556, 410));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Aveline", 1, 100)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/cadEuroChem/img/logoIcon.png"))); // NOI18N
        jLabel1.setText("PMSYS");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(432, 108, 468, 144));

        jLabel2.setFont(new java.awt.Font("HyperSuperRegular", 1, 50)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("ALPHA v0.1");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(888, 180, 216, 36));

        jTabbedPane1.addTab("HOME", jPanel1);

        jPanel2.setBackground(new java.awt.Color(1, 45, 69));
        jPanel2.setMinimumSize(new java.awt.Dimension(606, 383));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonSaveFornecedor.setBackground(new java.awt.Color(4, 178, 207));
        buttonSaveFornecedor.setFont(new java.awt.Font("HyperSuperRegular", 0, 38)); // NOI18N
        buttonSaveFornecedor.setForeground(new java.awt.Color(255, 255, 255));
        buttonSaveFornecedor.setText("SALVAR");
        buttonSaveFornecedor.setMaximumSize(new java.awt.Dimension(98, 74));
        buttonSaveFornecedor.setMinimumSize(new java.awt.Dimension(98, 74));
        buttonSaveFornecedor.setPreferredSize(new java.awt.Dimension(98, 74));
        buttonSaveFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveFornecedorActionPerformed(evt);
            }
        });
        jPanel2.add(buttonSaveFornecedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 552, 168, 96));

        buttonClearFornecedor.setBackground(new java.awt.Color(242, 202, 82));
        buttonClearFornecedor.setFont(new java.awt.Font("HyperSuperRegular", 0, 38)); // NOI18N
        buttonClearFornecedor.setForeground(new java.awt.Color(255, 255, 255));
        buttonClearFornecedor.setText("LIMPAR");
        buttonClearFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClearFornecedorActionPerformed(evt);
            }
        });
        jPanel2.add(buttonClearFornecedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(552, 552, 168, 96));

        campoNumeroFornecedor.setBackground(new java.awt.Color(255, 255, 255));
        campoNumeroFornecedor.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jPanel2.add(campoNumeroFornecedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 240, 420, 36));

        jLabel6.setFont(new java.awt.Font("HyperSuperRegular", 0, 35)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("NOME:");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 180, 216, 36));

        jLabel7.setFont(new java.awt.Font("HyperSuperRegular", 1, 100)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("DADOS DO FORNECEDOR");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(288, 60, -1, -1));

        jLabel8.setFont(new java.awt.Font("HyperSuperRegular", 0, 35)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("NÚMERO:");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 240, 216, 36));

        campoNameFornecedor.setBackground(new java.awt.Color(255, 255, 255));
        campoNameFornecedor.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        jPanel2.add(campoNameFornecedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 180, 420, 36));

        jLabel10.setFont(new java.awt.Font("HyperSuperRegular", 0, 40)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("FORNECEDORES CADASTRADOS");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(888, 180, -1, -1));

        jLabel11.setFont(new java.awt.Font("HyperSuperRegular", 0, 35)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("NOME");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(821, 240, 216, -1));

        jLabel12.setFont(new java.awt.Font("HyperSuperRegular", 0, 35)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("STATUS");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(1145, 240, 216, -1));

        listaNomeFornecedor.setBackground(new java.awt.Color(255, 255, 255));
        listaNomeFornecedor.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        listaNomeFornecedor.setForeground(new java.awt.Color(1, 45, 69));
        listaNomeFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listaNomeFornecedorActionPerformed(evt);
            }
        });
        jPanel2.add(listaNomeFornecedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(816, 288, 228, 36));

        listaStatusFornecedor.setBackground(new java.awt.Color(255, 255, 255));
        listaStatusFornecedor.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        listaStatusFornecedor.setForeground(new java.awt.Color(1, 45, 69));
        listaStatusFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listaStatusFornecedorActionPerformed(evt);
            }
        });
        jPanel2.add(listaStatusFornecedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 288, 228, 36));

        jLabel21.setFont(new java.awt.Font("HyperSuperRegular", 0, 35)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("CNPJ:");
        jPanel2.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 300, 216, 36));

        campoCnpjFornecedor.setBackground(new java.awt.Color(255, 255, 255));
        campoCnpjFornecedor.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        campoCnpjFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoCnpjFornecedorActionPerformed(evt);
            }
        });
        jPanel2.add(campoCnpjFornecedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 300, 420, 36));

        jLabel22.setFont(new java.awt.Font("HyperSuperRegular", 0, 35)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("DATA EMISSÃO:");
        jPanel2.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 360, 216, 36));

        jLabel23.setFont(new java.awt.Font("HyperSuperRegular", 0, 35)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("RECEBIMENTO:");
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(84, 420, 216, 36));

        campoDataEmissao.setBackground(new java.awt.Color(255, 255, 255));
        campoDataEmissao.setColumns(7);
        try {
            campoDataEmissao.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        campoDataEmissao.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        campoDataEmissao.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        campoDataEmissao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoDataEmissaoActionPerformed(evt);
            }
        });
        jPanel2.add(campoDataEmissao, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 360, 156, 36));

        campoDataRecebimento.setBackground(new java.awt.Color(255, 255, 255));
        campoDataRecebimento.setColumns(7);
        try {
            campoDataRecebimento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        campoDataRecebimento.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        campoDataRecebimento.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        campoDataRecebimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoDataRecebimentoActionPerformed(evt);
            }
        });
        jPanel2.add(campoDataRecebimento, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 420, 156, 36));

        jTabbedPane1.addTab("FORNECEDOR", jPanel2);

        jPanel3.setBackground(new java.awt.Color(1, 45, 69));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("HyperSuperRegular", 0, 80)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("TEMPO:");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 204, 324, 60));

        jLabel5.setFont(new java.awt.Font("HyperSuperRegular", 0, 80)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("PRAZO:");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 288, 324, 60));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("HyperSuperRegular", 0, 80)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("NÚMERO:");
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 120, 324, 60));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("HyperSuperRegular", 0, 80)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("NOME:");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 36, 324, 60));

        nomeFornecedorTempo.setBackground(new java.awt.Color(255, 255, 255));
        nomeFornecedorTempo.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        nomeFornecedorTempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nomeFornecedorTempoActionPerformed(evt);
            }
        });
        jPanel3.add(nomeFornecedorTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(468, 48, 720, 48));

        numFornecedorTempo.setBackground(new java.awt.Color(255, 255, 255));
        numFornecedorTempo.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        numFornecedorTempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numFornecedorTempoActionPerformed(evt);
            }
        });
        jPanel3.add(numFornecedorTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(468, 132, 720, 48));

        campoTime.setBackground(new java.awt.Color(255, 255, 255));
        campoTime.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        campoTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoTimeActionPerformed(evt);
            }
        });
        jPanel3.add(campoTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(468, 216, 720, 48));

        campoDate.setBackground(new java.awt.Color(255, 255, 255));
        campoDate.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        campoDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoDateActionPerformed(evt);
            }
        });
        jPanel3.add(campoDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(468, 300, 720, 48));

        jLabel24.setFont(new java.awt.Font("HyperSuperRegular", 0, 80)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("CNPJ:");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 372, 324, 60));

        cnpjFornecedorTempo.setBackground(new java.awt.Color(255, 255, 255));
        cnpjFornecedorTempo.setFont(new java.awt.Font("Dialog", 0, 20)); // NOI18N
        cnpjFornecedorTempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cnpjFornecedorTempoActionPerformed(evt);
            }
        });
        jPanel3.add(cnpjFornecedorTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(468, 384, 720, 48));

        playTime.setBackground(new java.awt.Color(4, 178, 207));
        playTime.setFont(new java.awt.Font("HyperSuperRegular", 0, 40)); // NOI18N
        playTime.setForeground(new java.awt.Color(255, 255, 255));
        playTime.setText("INICIAR");
        jPanel3.add(playTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 540, 180, 96));

        pauseTime.setBackground(new java.awt.Color(242, 124, 56));
        pauseTime.setFont(new java.awt.Font("HyperSuperRegular", 0, 40)); // NOI18N
        pauseTime.setForeground(new java.awt.Color(255, 255, 255));
        pauseTime.setText("PAUSAR");
        jPanel3.add(pauseTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 540, 180, 96));

        resetTime.setBackground(new java.awt.Color(242, 202, 82));
        resetTime.setFont(new java.awt.Font("HyperSuperRegular", 0, 40)); // NOI18N
        resetTime.setForeground(new java.awt.Color(255, 255, 255));
        resetTime.setText("REINICIAR");
        jPanel3.add(resetTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 540, 180, 96));

        buttonSalvarTempo.setBackground(new java.awt.Color(65, 166, 137));
        buttonSalvarTempo.setFont(new java.awt.Font("HyperSuperRegular", 0, 40)); // NOI18N
        buttonSalvarTempo.setForeground(new java.awt.Color(255, 255, 255));
        buttonSalvarTempo.setText("FINALIZAR");
        buttonSalvarTempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSalvarTempoActionPerformed(evt);
            }
        });
        jPanel3.add(buttonSalvarTempo, new org.netbeans.lib.awtextra.AbsoluteConstraints(804, 540, 180, 96));

        jTabbedPane1.addTab("TEMPO", jPanel3);

        jPanel5.setBackground(new java.awt.Color(1, 45, 69));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("HyperSuperRegular", 0, 110)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("RELATÓRIOS");
        jPanel5.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(167, 35, 1097, 108));

        jScrollPane1.setBackground(new java.awt.Color(62, 74, 89));

        tabelaFornecedores.setAutoCreateRowSorter(true);
        tabelaFornecedores.setBackground(new java.awt.Color(62, 74, 89));
        tabelaFornecedores.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        tabelaFornecedores.setFont(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        tabelaFornecedores.setForeground(new java.awt.Color(242, 242, 242));
        tabelaFornecedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "NOME", "NUMERO", "CNPJ", "RECEBIMENTO", "EMISSÃO"
            }
        ));
        tabelaFornecedores.setGridColor(new java.awt.Color(255, 255, 255));
        tabelaFornecedores.setShowGrid(true);
        jScrollPane1.setViewportView(tabelaFornecedores);

        jPanel5.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(36, 264, 1452, 312));

        jLabel16.setFont(new java.awt.Font("HyperSuperRegular", 0, 60)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("FORNECEDORES");
        jPanel5.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 192, 1452, -1));

        gerarRelatorio.setBackground(new java.awt.Color(4, 178, 207));
        gerarRelatorio.setFont(new java.awt.Font("HyperSuperRegular", 0, 40)); // NOI18N
        gerarRelatorio.setForeground(new java.awt.Color(255, 255, 255));
        gerarRelatorio.setText("GERAR RELATÓRIO");
        gerarRelatorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gerarRelatorioActionPerformed(evt);
            }
        });
        jPanel5.add(gerarRelatorio, new org.netbeans.lib.awtextra.AbsoluteConstraints(552, 612, 348, 84));

        jTabbedPane1.addTab("RELATÓRIOS", jPanel5);

        jPanel6.setBackground(new java.awt.Color(1, 45, 69));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonCadastrarUsuario.setBackground(new java.awt.Color(4, 178, 207));
        buttonCadastrarUsuario.setFont(new java.awt.Font("HyperSuperRegular", 0, 60)); // NOI18N
        buttonCadastrarUsuario.setForeground(new java.awt.Color(255, 255, 255));
        buttonCadastrarUsuario.setText("CADASTRAR");
        buttonCadastrarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCadastrarUsuarioActionPerformed(evt);
            }
        });
        jPanel6.add(buttonCadastrarUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 324, 301, 132));

        jLabel3.setFont(new java.awt.Font("HyperSuperRegular", 0, 110)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("CADASTRAR USUÁRIO");
        jPanel6.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-5, 139, 1536, 122));

        jTabbedPane1.addTab("USUÁRIO", jPanel6);

        jPanel4.setBackground(new java.awt.Color(1, 45, 69));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonCloseMultiPainel.setBackground(new java.awt.Color(242, 202, 82));
        buttonCloseMultiPainel.setFont(new java.awt.Font("HyperSuperRegular", 0, 60)); // NOI18N
        buttonCloseMultiPainel.setForeground(new java.awt.Color(255, 255, 255));
        buttonCloseMultiPainel.setText("SAIR");
        buttonCloseMultiPainel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseMultiPainelActionPerformed(evt);
            }
        });
        jPanel4.add(buttonCloseMultiPainel, new org.netbeans.lib.awtextra.AbsoluteConstraints(612, 360, 330, 120));

        jLabel9.setFont(new java.awt.Font("HyperSuperRegular", 0, 110)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("ATÉ LOGO!");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 144, 1524, 72));

        jTabbedPane1.addTab("SAIR", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1438, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCloseMultiPainelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseMultiPainelActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_buttonCloseMultiPainelActionPerformed
/*
    private void buttonClearFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearFornecedorActionPerformed
    }//GEN-LAST:event_buttonClearFornecedorActionPerformed
*/
    private void buttonCadastrarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCadastrarUsuarioActionPerformed
        // TODO add your handling code here:   
    }//GEN-LAST:event_buttonCadastrarUsuarioActionPerformed
    //>>>>>> PAINEL FORNECEDOR <<<<<<<<<<
    private void buttonSaveFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveFornecedorActionPerformed
        // TODO add your handling code here:
        int auxNumeroFornecedor = Integer.parseInt(campoNumeroFornecedor.getText());
        String auxNameFornecedor = campoNameFornecedor.getText();
        String auxCnpjFornecedor = campoCnpjFornecedor.getText();
        String auxEmissao = campoDataEmissao.getText();
        String auxRecebimento = campoDataRecebimento.getText();

        Fornecedor novoFornecedor = new Fornecedor(auxNumeroFornecedor, auxNameFornecedor, auxCnpjFornecedor, auxEmissao, auxRecebimento);
        DAONotaFiscal daoNotaFiscal = new DAONotaFiscal();

        if (daoNotaFiscal.inserirNota(novoFornecedor, segundos)) {
            JOptionPane.showMessageDialog(null, "Nota fiscal cadastrada com sucesso!");
        } else {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar nota fiscal");
        }

        fornecedores.add(novoFornecedor);
        numeroParaNome.put(novoFornecedor.getNumeroFornecedor(), novoFornecedor.getNameFornecedor());
        limparTexto();

        listaNomeFornecedor.addItem(novoFornecedor.getNameFornecedor());
        listaStatusFornecedor.addItem(novoFornecedor.getDataRecebimento());
    }//GEN-LAST:event_buttonSaveFornecedorActionPerformed

    private void campoCnpjFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoCnpjFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoCnpjFornecedorActionPerformed

    private void listaNomeFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listaNomeFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listaNomeFornecedorActionPerformed

    private void listaStatusFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listaStatusFornecedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listaStatusFornecedorActionPerformed

    private void numFornecedorTempoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numFornecedorTempoActionPerformed
        // TODO add your handling code here:
        buscarFornecedor();
    }//GEN-LAST:event_numFornecedorTempoActionPerformed

    private void nomeFornecedorTempoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nomeFornecedorTempoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nomeFornecedorTempoActionPerformed

    private void campoTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoTimeActionPerformed

    private void campoDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoDateActionPerformed

    private void cnpjFornecedorTempoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cnpjFornecedorTempoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cnpjFornecedorTempoActionPerformed

    private void campoDataEmissaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoDataEmissaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoDataEmissaoActionPerformed

    private void campoDataRecebimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoDataRecebimentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoDataRecebimentoActionPerformed

    private void buttonSalvarTempoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSalvarTempoActionPerformed
        // TODO add your handling code here:
        // Obtém o tempo atual do cronômetro
        int tempoAtual = segundos;

        // Obtém o número do fornecedor da caixa de texto
        String numeroFornecedorTexto = numFornecedorTempo.getText();

        // Converte o número do fornecedor para um inteiro
        try {
            int numeroFornecedor = Integer.parseInt(numeroFornecedorTexto);

            // Atualiza o banco de dados com o tempo atual para o número do fornecedor correspondente
            DAONotaFiscal daoNotaFiscal = new DAONotaFiscal();
            if (daoNotaFiscal.atualizarTempoFornecedor(numeroFornecedor, tempoAtual)) {
                JOptionPane.showMessageDialog(null, "Tempo salvo com sucesso para o fornecedor " + numeroFornecedor);
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao salvar o tempo para o fornecedor " + numeroFornecedor);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Número do fornecedor inválido");
        }
    }//GEN-LAST:event_buttonSalvarTempoActionPerformed

    private void gerarRelatorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gerarRelatorioActionPerformed
        // TODO add your handling code here:
        gerarRelatorioExcel();
    }//GEN-LAST:event_gerarRelatorioActionPerformed
    
    private void buttonClearFornecedorActionPerformed(java.awt.event.ActionEvent evt) {                                                      
        // TODO add your handling code here:
        limparTexto();
    }                 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MultiPainel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MultiPainel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MultiPainel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MultiPainel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MultiPainel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCadastrarUsuario;
    private javax.swing.JButton buttonClearFornecedor;
    private javax.swing.JButton buttonCloseMultiPainel;
    private javax.swing.JButton buttonSalvarTempo;
    private javax.swing.JButton buttonSaveFornecedor;
    private javax.swing.JTextField campoCnpjFornecedor;
    private javax.swing.JFormattedTextField campoDataEmissao;
    private javax.swing.JFormattedTextField campoDataRecebimento;
    private javax.swing.JTextField campoDate;
    private javax.swing.JTextField campoNameFornecedor;
    private javax.swing.JTextField campoNumeroFornecedor;
    private javax.swing.JTextField campoTime;
    private javax.swing.JTextField cnpjFornecedorTempo;
    private javax.swing.JButton gerarRelatorio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox<String> listaNomeFornecedor;
    private javax.swing.JComboBox<String> listaStatusFornecedor;
    private javax.swing.JTextField nomeFornecedorTempo;
    private javax.swing.JTextField numFornecedorTempo;
    private javax.swing.JButton pauseTime;
    private javax.swing.JButton playTime;
    private javax.swing.JButton resetTime;
    private javax.swing.JTable tabelaFornecedores;
    // End of variables declaration//GEN-END:variables
}
