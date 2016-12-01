package nz.co.skytv.spring.util;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;


/**
 * If a factory produces a class that is not managed by the Spring context.<br>
 * Spring annotations such as @Value don't work.<br>
 * This helper enhances the class to allow Spring Annotations.
 */
@Service
public class FactoryHelper implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public <T> T enhance(T bean) {
    applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
    return bean;
  }

}
