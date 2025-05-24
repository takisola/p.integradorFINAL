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

@Entity(name = "PagamentoEntity")
@Table(name = "pagamentos")
public class Pagamentos {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_aluno", nullable = false)
    private Aluno aluno;

    @Column(name = "data_pagamento", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataPagamento;

    @Column(nullable = false)
    private Double valor;

   
    public Pagamentos() {}

    public Pagamentos(int id, Date data, double valor, Aluno aluno) {
        this.id = id;
        this.dataPagamento = data;
        this.valor = valor;
        this.aluno = aluno;
    }

  
    public int getId() {
        return id;
    }

    public Date getData() {
        return dataPagamento;
    }

    public double getValor() {
        return valor;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setData(Date data) {
        this.dataPagamento = data;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
}