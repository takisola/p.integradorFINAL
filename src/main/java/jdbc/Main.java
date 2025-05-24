package jdbc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

public class Main extends JFrame {
    private EntityManagerFactory emf;
    private boolean isAdmin;

    public Main(boolean isAdmin) {
        this.isAdmin = isAdmin;
        emf = Persistence.createEntityManagerFactory("academia");
        initUI();
    }

    private void initUI() {
        setTitle("Sistema Academia - " + (isAdmin ? "Administrador" : "Recepcionista"));
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnCadastrar = new JButton("Cadastrar Aluno");
        JButton btnPresenca = new JButton("Registrar Presença");
        JButton btnPagamento = new JButton("Registrar Pagamento");
        JButton btnConsulta = new JButton("Consultas");
        JButton btnSair = new JButton("Sair");

        
        btnCadastrar.setEnabled(isAdmin);

        btnCadastrar.addActionListener(e -> mostrarCadastroAluno());
        btnPresenca.addActionListener(e -> mostrarPresenca());
        btnPagamento.addActionListener(e -> mostrarPagamento());
        btnConsulta.addActionListener(e -> mostrarConsultas());
        btnSair.addActionListener(e -> System.exit(0));

        panel.add(btnCadastrar);
        panel.add(btnPresenca);
        panel.add(btnPagamento);
        panel.add(btnConsulta);
        panel.add(btnSair);

        add(panel);
    }

    private void mostrarCadastroAluno() {
        JDialog dialog = new JDialog(this, "Cadastro de Aluno", true);
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));

        JTextField txtNome = new JTextField();
        JTextField txtDataNasc = new JTextField();
        JTextField txtTelefone = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPlano = new JTextField();

        panel.add(new JLabel("Nome:")); panel.add(txtNome);
        panel.add(new JLabel("Data Nasc. (dd/mm/aaaa):")); panel.add(txtDataNasc);
        panel.add(new JLabel("Telefone:")); panel.add(txtTelefone);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Plano:")); panel.add(txtPlano);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            EntityManager em = emf.createEntityManager();
            try {
                Aluno aluno = new Aluno();
                aluno.setNome(txtNome.getText());
                aluno.setDataNascimento(parseDate(txtDataNasc.getText()));
                aluno.setTelefone(txtTelefone.getText());
                aluno.setEmail(txtEmail.getText());
                aluno.setPlano(txtPlano.getText());

                em.getTransaction().begin();
                em.persist(aluno);
                em.getTransaction().commit();

                JOptionPane.showMessageDialog(dialog, "Aluno cadastrado com ID: " + aluno.getId());
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                em.close();
            }
        });

        panel.add(btnSalvar);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void mostrarPresenca() {
        JDialog dialog = new JDialog(this, "Registrar Presença", true);
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField txtId = new JTextField();
        panel.add(new JLabel("ID do Aluno:")); panel.add(txtId);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> {
            EntityManager em = emf.createEntityManager();
            try {
                Aluno aluno = em.find(Aluno.class, Integer.parseInt(txtId.getText()));
                if (aluno != null) {
                    Presenca pres = new Presenca();
                    pres.setAluno(aluno);
                    pres.setData(new Date());

                    em.getTransaction().begin();
                    em.persist(pres);
                    em.getTransaction().commit();

                    JOptionPane.showMessageDialog(dialog, "Presença registrada!");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Aluno não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } finally {
                em.close();
            }
        });

        panel.add(btnRegistrar);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void mostrarPagamento() {
        JDialog dialog = new JDialog(this, "Registrar Pagamento", true);
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField txtId = new JTextField(), txtValor = new JTextField();

        panel.add(new JLabel("ID do Aluno:")); panel.add(txtId);
        panel.add(new JLabel("Valor:")); panel.add(txtValor);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.addActionListener(e -> {
            EntityManager em = emf.createEntityManager();
            try {
                Aluno aluno = em.find(Aluno.class, Integer.parseInt(txtId.getText()));
                if (aluno != null) {

                    aluno.realizarPagamento();

                    JOptionPane.showMessageDialog(dialog, "Pagamento registrado!");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Aluno não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } finally {
                em.close();
            }
        });

        panel.add(btnRegistrar);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void mostrarConsultas() {
        JDialog dialog = new JDialog(this, "Consultas", true);
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Alunos", criarPainelAlunos());
        tabs.addTab("Presenças", criarPainelPresencas());
        tabs.addTab("Pagamentos", criarPainelPagamentos());

        dialog.add(tabs);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JScrollPane criarPainelAlunos() {
        String[] colunas = {"ID", "Nome", "Data Nasc.", "Telefone", "Email", "Plano"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        EntityManager em = emf.createEntityManager();
        List<Aluno> lista = em.createQuery("SELECT a FROM Aluno a", Aluno.class).getResultList();
        em.close();

        for (Aluno a : lista) {
            model.addRow(new Object[]{a.getId(), a.getNome(), a.getDataNascimento(), a.getTelefone(), a.getEmail(), a.getPlano()});
        }
        return new JScrollPane(new JTable(model));
    }

    private JScrollPane criarPainelPresencas() {
        String[] cols = {"ID", "Aluno ID", "Data Presença"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        EntityManager em = emf.createEntityManager();
        List<Presenca> lista = em.createQuery("SELECT p FROM PresencaEntity p", Presenca.class).getResultList();
        em.close();

        for (Presenca p : lista) {
            model.addRow(new Object[]{p.getId(), p.getAluno().getId(), p.getData()});
        }
        return new JScrollPane(new JTable(model));
    }

    private JScrollPane criarPainelPagamentos() {
        String[] cols = {"ID", "Aluno ID", "Data Pagamento", "Valor"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        EntityManager em = emf.createEntityManager();
        List<Pagamentos> lista = em.createQuery("SELECT p FROM PagamentoEntity p", Pagamentos.class).getResultList();
        em.close();

        for (Pagamentos p : lista) {
            model.addRow(new Object[]{p.getId(), p.getAluno().getId(), p.getData(), p.getValor()});
        }
        return new JScrollPane(new JTable(model));
    }

    private Date parseDate(String data) {
        String[] partes = data.split("/");
        return new Date(
            Integer.parseInt(partes[2]) - 1900,
            Integer.parseInt(partes[1]) - 1,
            Integer.parseInt(partes[0])
        );
    }

    public static void main(String[] args) {
       
        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);
            if (login.isAuthenticated()) {
                boolean isAdmin = login.getRole().equals("Administrador");
                new Main(isAdmin).setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}

class LoginDialog extends JDialog {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private boolean authenticated = false;
    private String role;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtUser = new JTextField();
        txtPass = new JPasswordField();
        btnLogin = new JButton("Entrar");

        panel.add(new JLabel("Usuário:")); panel.add(txtUser);
        panel.add(new JLabel("Senha:")); panel.add(txtPass);
        panel.add(new JLabel()); panel.add(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUser.getText();
                String pass = new String(txtPass.getPassword());
                if ("admin".equals(user) && "admin".equals(pass)) {
                    authenticated = true;
                    role = "Administrador";
                } else if ("recep".equals(user) && "recep".equals(pass)) {
                    authenticated = true;
                    role = "Recepcionista";
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "Credenciais inválidas!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                dispose();
            }
        });

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getRole() {
        return role;
    }
}
