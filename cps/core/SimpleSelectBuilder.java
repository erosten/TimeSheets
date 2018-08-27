
package cps.core;

import cps.core.db.frame.BaseEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

public final class SimpleSelectBuilder<T extends BaseEntity> {

  private final EntityManager entityManager;
  private final CriteriaBuilder criteriaBuilder;
  private final CriteriaQuery<T> criteriaQuery;
  private final Root<T> root;
  private final Collection<Predicate> predicates;

  private Integer first = null;
  private Integer max = null;
  private LockModeType lockModeType = null;

  public SimpleSelectBuilder(final EntityManager entityManager, final Class<T> entityClazz) {
    this.entityManager = entityManager;
    this.criteriaBuilder = entityManager.getCriteriaBuilder();
    this.criteriaQuery = this.criteriaBuilder.createQuery(entityClazz);
    this.root = criteriaQuery.from(entityClazz);
    this.predicates = new Vector<>();
  }

  public SimpleSelectBuilder<T> and(final Attribute attribute, final Object value) {
    final Expression<T> expression = this.getExpression(attribute, root);
    this.predicates.add(criteriaBuilder.equal(expression, value));
    return this;
  }

  public SimpleSelectBuilder<T> andNotIn(final Attribute attribute,
      final Collection<Object> values) {
    final Expression expression = this.getExpression(attribute, root);
    this.predicates.add(criteriaBuilder.not(expression.in(values)));
    return this;
  }

  public SimpleSelectBuilder<T> andIn(final Attribute attribute, final Collection<Object> values) {
    final Expression expression = this.getExpression(attribute, root);
    this.predicates.add(expression.in(values));
    return this;
  }

  public SimpleSelectBuilder<T> andContains(final Attribute attribute, final Object value) {

    final Expression expression = this.getExpression(attribute, root);
    this.predicates.add(criteriaBuilder.isMember(value, expression));
    return this;
  }

  public SimpleSelectBuilder<T> orderByAsc(final Attribute attribute) {
    final List<Order> orders = new ArrayList<>();
    if (this.criteriaQuery.getOrderList() != null) {
      orders.addAll(this.criteriaQuery.getOrderList());
    }
    orders.add(criteriaBuilder.asc(this.getExpression(attribute, root)));
    this.criteriaQuery.orderBy(orders.toArray(new Order[orders.size()]));
    return this;
  }

  public SimpleSelectBuilder<T> orderByDesc(final Attribute attribute) {
    List<Order> orders = this.criteriaQuery.getOrderList();
    if (orders == null) {
      orders = new ArrayList<>();
    }
    orders.add(criteriaBuilder.desc(this.getExpression(attribute, root)));
    this.criteriaQuery.orderBy(orders.toArray(new Order[orders.size()]));
    return this;
  }

  public SimpleSelectBuilder<T> setFirst(Integer first) {
    this.first = first;
    return this;
  }

  public SimpleSelectBuilder<T> setMax(Integer max) {
    this.max = max;
    return this;
  }

  public SimpleSelectBuilder<T> setLockModeType(LockModeType lockModeType) {
    this.lockModeType = lockModeType;
    return this;
  }

  public List<T> getResultList() {
    final TypedQuery<T> query = this.prepareQuery();

    if (lockModeType != null) {
      query.setLockMode(lockModeType);
    }

    if (first != null) {
      query.setFirstResult(first);
    }

    if (max != null) {
      query.setMaxResults(max);
    }

    return query.getResultList();
  }

  // public List<T> getCacheableResultList() {
  // final TypedQuery<T> query = this.prepareQuery();
  // if (lockModeType != null) {
  // query.setLockMode(lockModeType);
  // }
  //
  // if (first != null) {
  // query.setFirstResult(first);
  // }
  //
  // if (max != null) {
  // query.setMaxResults(max);
  // }
  //
  // query.setHint("org.hibernate.cacheable", true);
  // query.setHint("org.hibernate.cacheMode", "NORMAL");
  // return query.getResultList();
  // }

  public T getSingleResult() {
    final TypedQuery<T> query = this.prepareQuery();

    if (lockModeType != null) {
      query.setLockMode(lockModeType);
    }

    return query.getSingleResult();
  }

  // public T getCacheableSingleResult() {
  // final TypedQuery<T> query = this.prepareQuery();
  //
  // if (lockModeType != null) {
  // query.setLockMode(lockModeType);
  // }
  //
  // query.setHint("org.hibernate.cacheable", true);
  // query.setHint("org.hibernate.cacheMode", "NORMAL");
  // return query.getSingleResult();
  // }

  private TypedQuery<T> prepareQuery() {
    this.criteriaQuery.where(this.predicates.toArray(new Predicate[this.predicates.size()]));
    return this.entityManager.createQuery(criteriaQuery);
  }

  private <E> Expression<E> getExpression(final Attribute attribute, final From<T, E> from) {
    if (attribute instanceof SingularAttribute) {
      SingularAttribute singularAttribute = (SingularAttribute) attribute;
      return from.get(singularAttribute);
    } else if (attribute instanceof PluralAttribute) {
      PluralAttribute pluralAttribute = (PluralAttribute) attribute;
      return from.get(pluralAttribute);
    } else {
      throw new PersistenceException(
          "Attribute type of '" + attribute
              + "' must be one of [SingularAttribute, PluralAttribute].");
    }
  }

  private <E> Join<T, E> getJoinExpression(final Attribute attribute, final From<T, E> from) {
    if (attribute instanceof SingularAttribute) {
      final SingularAttribute singularAttribute = (SingularAttribute) attribute;
      return from.join(singularAttribute);
    } else if (attribute instanceof CollectionAttribute) {
      final CollectionAttribute collectionAttribute = (CollectionAttribute) attribute;
      return from.join(collectionAttribute);
    } else {
      throw new PersistenceException(
          "Attribute type of '" + attribute
              + "' must be one of [SingularAttribute, PluralAttribute].");
    }
  }

  public SimpleSelectBuilder<T> joinAnd(final Attribute attribute, final Object value,
      final Attribute... joinOn) {
    Join tableJoin = null;
    for (final Attribute join : joinOn) {
      if (tableJoin == null) {
        tableJoin = this.getJoinExpression(join, root);
      } else {
        tableJoin = this.getJoinExpression(join, tableJoin);
      }

    }

    if (tableJoin == null) {
      throw new PersistenceException("SelectBuilder cannot construct your join statement");
    }

    final Expression expression = this.getExpression(attribute, tableJoin);
    this.predicates.add(criteriaBuilder.equal(expression, value));
    return this;
  }
}
