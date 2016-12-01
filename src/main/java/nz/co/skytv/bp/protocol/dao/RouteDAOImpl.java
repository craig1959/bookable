package nz.co.skytv.bp.protocol.dao;

import java.util.List;
import nz.co.skytv.bp.protocol.model.Route;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;


@Repository
public class RouteDAOImpl extends HibernateDAO implements RouteDAO {


  private static final long serialVersionUID = 1L;

  @Override
  public List<Route> getRoutes(String source, String channel) {

    Criteria criteria = currentSession().createCriteria(Route.class);
    criteria.add(Restrictions.eq("is_active", 1));
    criteria.createAlias("location", "loc");//foreign key link between route table and location table
    criteria.add(Restrictions.eq("loc.source", source));
    criteria.add(Restrictions.eq("loc.channel", channel));
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);


    @SuppressWarnings("unchecked") List<Route> routes = (List<Route>) criteria.list();

    return routes;
  }

  @Override
  public List<String> getResetLocation(String channelName) {

    Criteria criteria = currentSession().createCriteria(Route.class);
    criteria.add(Restrictions.eq("is_active", 1));
    criteria.add(Restrictions.eq("channel_name", channelName));
    criteria.setProjection(Projections.distinct(Projections.property("route_location")));
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

    @SuppressWarnings("unchecked") List<String> routes = (List<String>) criteria.list();
    return routes;
  }

}
