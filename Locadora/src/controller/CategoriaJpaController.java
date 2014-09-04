/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Filme;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Categoria;

/**
 *
 * @author Dan
 */
public class CategoriaJpaController implements Serializable {

    public CategoriaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categoria categoria) {
        if (categoria.getFilmeList() == null)
        {
            categoria.setFilmeList(new ArrayList<Filme>());
        }
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Filme> attachedFilmeList = new ArrayList<Filme>();
            for (Filme filmeListFilmeToAttach : categoria.getFilmeList())
            {
                filmeListFilmeToAttach = em.getReference(filmeListFilmeToAttach.getClass(), filmeListFilmeToAttach.getIdFilme());
                attachedFilmeList.add(filmeListFilmeToAttach);
            }
            categoria.setFilmeList(attachedFilmeList);
            em.persist(categoria);
            for (Filme filmeListFilme : categoria.getFilmeList())
            {
                Categoria oldIdCategoriaOfFilmeListFilme = filmeListFilme.getIdCategoria();
                filmeListFilme.setIdCategoria(categoria);
                filmeListFilme = em.merge(filmeListFilme);
                if (oldIdCategoriaOfFilmeListFilme != null)
                {
                    oldIdCategoriaOfFilmeListFilme.getFilmeList().remove(filmeListFilme);
                    oldIdCategoriaOfFilmeListFilme = em.merge(oldIdCategoriaOfFilmeListFilme);
                }
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

    public void edit(Categoria categoria) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria persistentCategoria = em.find(Categoria.class, categoria.getIdCategoria());
            List<Filme> filmeListOld = persistentCategoria.getFilmeList();
            List<Filme> filmeListNew = categoria.getFilmeList();
            List<String> illegalOrphanMessages = null;
            for (Filme filmeListOldFilme : filmeListOld)
            {
                if (!filmeListNew.contains(filmeListOldFilme))
                {
                    if (illegalOrphanMessages == null)
                    {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Filme " + filmeListOldFilme + " since its idCategoria field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null)
            {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Filme> attachedFilmeListNew = new ArrayList<Filme>();
            for (Filme filmeListNewFilmeToAttach : filmeListNew)
            {
                filmeListNewFilmeToAttach = em.getReference(filmeListNewFilmeToAttach.getClass(), filmeListNewFilmeToAttach.getIdFilme());
                attachedFilmeListNew.add(filmeListNewFilmeToAttach);
            }
            filmeListNew = attachedFilmeListNew;
            categoria.setFilmeList(filmeListNew);
            categoria = em.merge(categoria);
            for (Filme filmeListNewFilme : filmeListNew)
            {
                if (!filmeListOld.contains(filmeListNewFilme))
                {
                    Categoria oldIdCategoriaOfFilmeListNewFilme = filmeListNewFilme.getIdCategoria();
                    filmeListNewFilme.setIdCategoria(categoria);
                    filmeListNewFilme = em.merge(filmeListNewFilme);
                    if (oldIdCategoriaOfFilmeListNewFilme != null && !oldIdCategoriaOfFilmeListNewFilme.equals(categoria))
                    {
                        oldIdCategoriaOfFilmeListNewFilme.getFilmeList().remove(filmeListNewFilme);
                        oldIdCategoriaOfFilmeListNewFilme = em.merge(oldIdCategoriaOfFilmeListNewFilme);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex)
        {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0)
            {
                Integer id = categoria.getIdCategoria();
                if (findCategoria(id) == null)
                {
                    throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.");
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try
        {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria categoria;
            try
            {
                categoria = em.getReference(Categoria.class, id);
                categoria.getIdCategoria();
            } catch (EntityNotFoundException enfe)
            {
                throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Filme> filmeListOrphanCheck = categoria.getFilmeList();
            for (Filme filmeListOrphanCheckFilme : filmeListOrphanCheck)
            {
                if (illegalOrphanMessages == null)
                {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Categoria (" + categoria + ") cannot be destroyed since the Filme " + filmeListOrphanCheckFilme + " in its filmeList field has a non-nullable idCategoria field.");
            }
            if (illegalOrphanMessages != null)
            {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(categoria);
            em.getTransaction().commit();
        } finally
        {
            if (em != null)
            {
                em.close();
            }
        }
    }

    public List<Categoria> findCategoriaEntities() {
        return findCategoriaEntities(true, -1, -1);
    }

    public List<Categoria> findCategoriaEntities(int maxResults, int firstResult) {
        return findCategoriaEntities(false, maxResults, firstResult);
    }

    private List<Categoria> findCategoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try
        {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categoria.class));
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

    public Categoria findCategoria(Integer id) {
        EntityManager em = getEntityManager();
        try
        {
            return em.find(Categoria.class, id);
        } finally
        {
            em.close();
        }
    }

    public int getCategoriaCount() {
        EntityManager em = getEntityManager();
        try
        {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categoria> rt = cq.from(Categoria.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally
        {
            em.close();
        }
    }
    
}
