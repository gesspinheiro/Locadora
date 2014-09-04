/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Categoria;
import model.Cliente;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Filme;

/**
 *
 * @author Dan
 */
public class FilmeJpaController implements Serializable {

    public FilmeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Filme filme) {
        if (filme.getClienteList() == null)
        {
            filme.setClienteList(new ArrayList<Cliente>());
        }
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria idCategoria = filme.getIdCategoria();
            if (idCategoria != null)
            {
                idCategoria = em.getReference(idCategoria.getClass(), idCategoria.getIdCategoria());
                filme.setIdCategoria(idCategoria);
            }
            List<Cliente> attachedClienteList = new ArrayList<Cliente>();
            for (Cliente clienteListClienteToAttach : filme.getClienteList())
            {
                clienteListClienteToAttach = em.getReference(clienteListClienteToAttach.getClass(), clienteListClienteToAttach.getIdPessoa());
                attachedClienteList.add(clienteListClienteToAttach);
            }
            filme.setClienteList(attachedClienteList);
            em.persist(filme);
            if (idCategoria != null)
            {
                idCategoria.getFilmeList().add(filme);
                idCategoria = em.merge(idCategoria);
            }
            for (Cliente clienteListCliente : filme.getClienteList())
            {
                clienteListCliente.getFilmeList().add(filme);
                clienteListCliente = em.merge(clienteListCliente);
            }
            em.getTransaction().commit();
        } finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public void edit(Filme filme) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            Filme persistentFilme = em.find(Filme.class, filme.getIdFilme());
            Categoria idCategoriaOld = persistentFilme.getIdCategoria();
            Categoria idCategoriaNew = filme.getIdCategoria();
            List<Cliente> clienteListOld = persistentFilme.getClienteList();
            List<Cliente> clienteListNew = filme.getClienteList();
            if (idCategoriaNew != null)
            {
                idCategoriaNew = em.getReference(idCategoriaNew.getClass(), idCategoriaNew.getIdCategoria());
                filme.setIdCategoria(idCategoriaNew);
            }
            List<Cliente> attachedClienteListNew = new ArrayList<Cliente>();
            for (Cliente clienteListNewClienteToAttach : clienteListNew)
            {
                clienteListNewClienteToAttach = em.getReference(clienteListNewClienteToAttach.getClass(), clienteListNewClienteToAttach.getIdPessoa());
                attachedClienteListNew.add(clienteListNewClienteToAttach);
            }
            clienteListNew = attachedClienteListNew;
            filme.setClienteList(clienteListNew);
            filme = em.merge(filme);
            if (idCategoriaOld != null && !idCategoriaOld.equals(idCategoriaNew))
            {
                idCategoriaOld.getFilmeList().remove(filme);
                idCategoriaOld = em.merge(idCategoriaOld);
            }
            if (idCategoriaNew != null && !idCategoriaNew.equals(idCategoriaOld))
            {
                idCategoriaNew.getFilmeList().add(filme);
                idCategoriaNew = em.merge(idCategoriaNew);
            }
            for (Cliente clienteListOldCliente : clienteListOld)
            {
                if (!clienteListNew.contains(clienteListOldCliente))
                {
                    clienteListOldCliente.getFilmeList().remove(filme);
                    clienteListOldCliente = em.merge(clienteListOldCliente);
                }
            }
            for (Cliente clienteListNewCliente : clienteListNew)
            {
                if (!clienteListOld.contains(clienteListNewCliente))
                {
                    clienteListNewCliente.getFilmeList().add(filme);
                    clienteListNewCliente = em.merge(clienteListNewCliente);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex)
        {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0)
            {
                Integer id = filme.getIdFilme();
                if (findFilme(id) == null)
                {
                    throw new NonexistentEntityException("The filme with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            Filme filme;
            try
            {
                filme = em.getReference(Filme.class, id);
                filme.getIdFilme();
            } catch (EntityNotFoundException enfe)
            {
                throw new NonexistentEntityException("The filme with id " + id + " no longer exists.", enfe);
            }
            Categoria idCategoria = filme.getIdCategoria();
            if (idCategoria != null)
            {
                idCategoria.getFilmeList().remove(filme);
                idCategoria = em.merge(idCategoria);
            }
            List<Cliente> clienteList = filme.getClienteList();
            for (Cliente clienteListCliente : clienteList)
            {
                clienteListCliente.getFilmeList().remove(filme);
                clienteListCliente = em.merge(clienteListCliente);
            }
            em.remove(filme);
            em.getTransaction().commit();
        } finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public List<Filme> findFilmeEntities() {
        return findFilmeEntities(true, -1, -1);
    }

    public List<Filme> findFilmeEntities(int maxResults, int firstResult) {
        return findFilmeEntities(false, maxResults, firstResult);
    }

    private List<Filme> findFilmeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try
        {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Filme.class));
            Query q = em.createQuery(cq);
            if (!all)
            {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally
        {
            em.close();
        }
    }

    public Filme findFilme(Integer id) {
        EntityManager em = getEntityManager();
        try
        {
            return em.find(Filme.class, id);
        } finally
        {
            em.close();
        }
    }

    public int getFilmeCount() {
        EntityManager em = getEntityManager();
        try
        {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Filme> rt = cq.from(Filme.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally
        {
            em.close();
        }
    }
    
}
