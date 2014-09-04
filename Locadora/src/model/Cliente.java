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
import javax.persistence.ManyToMany;
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
@Table(name = "cliente")
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "Cliente.findAll", query = "SELECT c FROM Cliente c"),
    @NamedQuery(name = "Cliente.findByIdPessoa", query = "SELECT c FROM Cliente c WHERE c.idPessoa = :idPessoa"),
    @NamedQuery(name = "Cliente.findByNomePessoa", query = "SELECT c FROM Cliente c WHERE c.nomePessoa = :nomePessoa"),
    @NamedQuery(name = "Cliente.findByTelefonePessoa", query = "SELECT c FROM Cliente c WHERE c.telefonePessoa = :telefonePessoa")
})
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPessoa")
    private Integer idPessoa;
    @Basic(optional = false)
    @Column(name = "nomePessoa")
    private String nomePessoa;
    @Basic(optional = false)
    @Column(name = "telefonePessoa")
    private String telefonePessoa;
    @ManyToMany(mappedBy = "clienteList")
    private List<Filme> filmeList;

    public Cliente() {
    }

    public Cliente(Integer idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Cliente(Integer idPessoa, String nomePessoa, String telefonePessoa) {
        this.idPessoa = idPessoa;
        this.nomePessoa = nomePessoa;
        this.telefonePessoa = telefonePessoa;
    }

    public Integer getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Integer idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String getTelefonePessoa() {
        return telefonePessoa;
    }

    public void setTelefonePessoa(String telefonePessoa) {
        this.telefonePessoa = telefonePessoa;
    }

    @XmlTransient
    public List<Filme> getFilmeList() {
        return filmeList;
    }

    public void setFilmeList(List<Filme> filmeList) {
        this.filmeList = filmeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPessoa != null ? idPessoa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cliente))
        {
            return false;
        }
        Cliente other = (Cliente) object;
        if ((this.idPessoa == null && other.idPessoa != null) || (this.idPessoa != null && !this.idPessoa.equals(other.idPessoa)))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "model.Cliente[ idPessoa=" + idPessoa + " ]";
    }
    
}
