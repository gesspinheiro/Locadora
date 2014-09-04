/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Dan
 */
@Entity
@Table(name = "filme")
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "Filme.findAll", query = "SELECT f FROM Filme f"),
    @NamedQuery(name = "Filme.findByIdFilme", query = "SELECT f FROM Filme f WHERE f.idFilme = :idFilme"),
    @NamedQuery(name = "Filme.findByNomeFilme", query = "SELECT f FROM Filme f WHERE f.nomeFilme = :nomeFilme"),
    @NamedQuery(name = "Filme.findByValorFilme", query = "SELECT f FROM Filme f WHERE f.valorFilme = :valorFilme"),
    @NamedQuery(name = "Filme.findByQtdDisponivelFilme", query = "SELECT f FROM Filme f WHERE f.qtdDisponivelFilme = :qtdDisponivelFilme")
})
public class Filme implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idFilme")
    private Integer idFilme;
    @Basic(optional = false)
    @Column(name = "nomeFilme")
    private String nomeFilme;
    @Basic(optional = false)
    @Column(name = "valorFilme")
    private float valorFilme;
    @Basic(optional = false)
    @Column(name = "qtdDisponivelFilme")
    private int qtdDisponivelFilme;
    @JoinTable(name = "locar", joinColumns =
    {
        @JoinColumn(name = "Filme_idFilme", referencedColumnName = "idFilme")
    }, inverseJoinColumns =
    {
        @JoinColumn(name = "Cliente_idPessoa", referencedColumnName = "idPessoa")
    })
    @ManyToMany
    private List<Cliente> clienteList;
    @JoinColumn(name = "idCategoria", referencedColumnName = "idCategoria")
    @ManyToOne(optional = false)
    private Categoria idCategoria;

    public Filme() {
    }

    public Filme(Integer idFilme) {
        this.idFilme = idFilme;
    }

    public Filme(Integer idFilme, String nomeFilme, float valorFilme, int qtdDisponivelFilme) {
        this.idFilme = idFilme;
        this.nomeFilme = nomeFilme;
        this.valorFilme = valorFilme;
        this.qtdDisponivelFilme = qtdDisponivelFilme;
    }

    public Integer getIdFilme() {
        return idFilme;
    }

    public void setIdFilme(Integer idFilme) {
        this.idFilme = idFilme;
    }

    public String getNomeFilme() {
        return nomeFilme;
    }

    public void setNomeFilme(String nomeFilme) {
        this.nomeFilme = nomeFilme;
    }

    public float getValorFilme() {
        return valorFilme;
    }

    public void setValorFilme(float valorFilme) {
        this.valorFilme = valorFilme;
    }

    public int getQtdDisponivelFilme() {
        return qtdDisponivelFilme;
    }

    public void setQtdDisponivelFilme(int qtdDisponivelFilme) {
        this.qtdDisponivelFilme = qtdDisponivelFilme;
    }

    @XmlTransient
    public List<Cliente> getClienteList() {
        return clienteList;
    }

    public void setClienteList(List<Cliente> clienteList) {
        this.clienteList = clienteList;
    }

    public Categoria getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Categoria idCategoria) {
        this.idCategoria = idCategoria;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idFilme != null ? idFilme.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Filme))
        {
            return false;
        }
        Filme other = (Filme) object;
        if ((this.idFilme == null && other.idFilme != null) || (this.idFilme != null && !this.idFilme.equals(other.idFilme)))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "model.Filme[ idFilme=" + idFilme + " ]";
    }
    
}
