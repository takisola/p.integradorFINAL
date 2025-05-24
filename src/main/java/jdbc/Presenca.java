package jdbc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity(name = "PresencaEntity")
@Table(name = "presencas")
public class Presenca {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_aluno", nullable = false)
    private Aluno aluno;

    @Column(name = "data_presenca", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataPresenca;

   
    public Presenca() {}

    public Presenca(int id, Date data, Aluno aluno) {
        this.id = id;
        this.dataPresenca = data;
        this.aluno = aluno;
    }

  
    public int getId() {
        return id;
    }

    public Date getData() {
        return dataPresenca;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setData(Date data) {
        this.dataPresenca = data;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
}