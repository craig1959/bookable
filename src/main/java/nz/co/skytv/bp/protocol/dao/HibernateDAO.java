package nz.co.skytv.bp.protocol.dao;

import java.io.Serializable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Basic DAO operations dependent with Hibernate's specific classes
 * @see SessionFactory
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class HibernateDAO implements Serializable {

  private static final long serialVersionUID = 1L;

  private SessionFactory sessionFactory;


  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  protected Session currentSession() {
    return sessionFactory.getCurrentSession();
  }


}
