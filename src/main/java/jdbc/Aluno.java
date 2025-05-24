package jdbc;
import java.util.Date;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;

@Entity
public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "data_nascimento")
    private Date dataNascimento;
    private String telefone;
    private String email;
    private String plano;

    public Aluno(int id, String nome, Date dataNascimento, String telefone, String email, String plano) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("academia");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            TypedQuery<Aluno> query = em.createQuery(
                "SELECT a FROM Aluno a WHERE a.email = :email", Aluno.class
            );
            query.setParameter("email", email);
            List<Aluno> resultados = query.getResultList();

            if (!resultados.isEmpty()) {
                
                Aluno existente = resultados.get(0);
                this.id = existente.getId();
                this.nome = existente.getNome();
                this.dataNascimento = existente.getDataNascimento();
                this.telefone = existente.getTelefone();
                this.email = existente.getEmail();
                this.plano = existente.getPlano();

            } else {
                
                Aluno novo = new Aluno();
                novo.setNome(nome);
                novo.setDataNascimento(dataNascimento);
                novo.setTelefone(telefone);
                novo.setEmail(email);
                novo.setPlano(plano);
                em.persist(novo);
                em.flush(); 
                this.id = novo.getId();
                this.nome = novo.getNome();
                this.dataNascimento = novo.getDataNascimento();
                this.telefone = novo.getTelefone();
                this.email = novo.getEmail();
                this.plano = novo.getPlano();
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao criar ou buscar aluno: " + e.getMessage(), e);
        } finally {
            em.close();
            emf.close();
        }
    }

    public Aluno() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Date getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(Date dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPlano() { return plano; }
    public void setPlano(String plano) { this.plano = plano; }
    
    
public void registrarPresenca() {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("academia");
    EntityManager em = emf.createEntityManager();
    
    try {
        em.getTransaction().begin();
        
      
        Query query = em.createNativeQuery(
            "INSERT INTO presencas (id_aluno, data_presenca) VALUES (?, CURRENT_DATE())"
        );
        query.setParameter(1, this.id);
        query.executeUpdate();
        
        em.getTransaction().commit();
    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        e.printStackTrace();
    } finally {
        em.close();
        emf.close();
    }
}


public void realizarPagamento() {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("academia");
    EntityManager em = emf.createEntityManager();
    
    try {
        em.getTransaction().begin();
        
     
        Query query = em.createNativeQuery(
            "INSERT INTO pagamentos (id_aluno, data_pagamento, valor) VALUES (?, CURRENT_DATE(), ?)"
        );
        query.setParameter(1, this.id);
        query.setParameter(2, this.plano); 
        query.executeUpdate();
        
        em.getTransaction().commit();
    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        e.printStackTrace();
    } finally {
        em.close();
        emf.close();
    }
}
}