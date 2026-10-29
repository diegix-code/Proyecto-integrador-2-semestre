package com.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CrudService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Obtiene todos los registros de una entidad usando JPQL dinámico.
     */
    @Transactional(readOnly = true)
    public <T> List<T> findAll(Class<T> entityClass) {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        TypedQuery<T> query = entityManager.createQuery(jpql, entityClass);
        return query.getResultList();
    }

    /**
     * Busca una entidad por su ID.
     */
    @Transactional(readOnly = true)
    public <T> T findById(Class<T> entityClass, Long id) {
        return entityManager.find(entityClass, id);
    }

    /**
     * Guarda o actualiza una entidad (Merge maneja ambos escenarios).
     */
    @Transactional
    public <T> T save(T entity) {
        return entityManager.merge(entity);
    }

    /**
     * Elimina una entidad por su ID.
     */
    @Transactional
    public <T> void delete(Class<T> entityClass, Long id) {
        T entity = findById(entityClass, id);
        if (entity != null) {
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
        }
    }
}
