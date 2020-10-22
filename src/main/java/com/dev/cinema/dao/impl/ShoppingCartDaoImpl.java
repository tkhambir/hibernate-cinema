package com.dev.cinema.dao.impl;

import com.dev.cinema.dao.ShoppingCartDao;
import com.dev.cinema.lib.exceptions.DataProcessingException;
import com.dev.cinema.model.ShoppingCart;
import com.dev.cinema.model.User;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ShoppingCartDaoImpl implements ShoppingCartDao {
    private static final Logger logger = Logger.getLogger(ShoppingCartDaoImpl.class);
    private SessionFactory sessionFactory;

    public ShoppingCartDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(shoppingCart);
            transaction.commit();
            logger.info("Shopping cart: " + shoppingCart + "added to DB");
            return shoppingCart;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert Shopping Cart: "
                    + shoppingCart.toString(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public ShoppingCart getByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<ShoppingCart> query = session.createQuery("FROM ShoppingCart sc "
                    + "JOIN FETCH sc.user u "
                    + "LEFT JOIN FETCH sc.tickets t "
                    + "WHERE sc.user = :user", ShoppingCart.class);
            query.setParameter("user", user);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new DataProcessingException("Can't find shopping cart "
                    + "for user " + user, e);
        }
    }

    @Override
    public void update(ShoppingCart shoppingCart) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.update(shoppingCart);
            transaction.commit();
            logger.info("Shopping cart: " + shoppingCart + "updated in DB");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't update Shopping Cart: "
                    + shoppingCart.toString(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
