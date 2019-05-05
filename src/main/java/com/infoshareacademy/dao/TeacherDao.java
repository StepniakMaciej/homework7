package com.infoshareacademy.dao;

import com.infoshareacademy.model.Teacher;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TeacherDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Long save(Teacher teacher) {
        entityManager.persist(teacher);
        return teacher.getPesel();
    }

    public Teacher update(Teacher teacher) {
        return entityManager.merge(teacher);
    }

    public void delete(Long pesel) {
        final Teacher teacher = entityManager.find(Teacher.class, pesel);
        if (teacher != null) {
            entityManager.remove(teacher);
        }
    }

    public Teacher findById(Long pesel) {
        return entityManager.find(Teacher.class, pesel);
    }

    public List<Teacher> findAll() {
        final Query query = entityManager.createQuery("SELECT t FROM Teacher t");
        return query.getResultList();
    }

}

