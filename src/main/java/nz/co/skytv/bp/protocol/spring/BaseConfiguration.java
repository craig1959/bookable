package nz.co.skytv.bp.protocol.spring;

import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@ComponentScan(basePackages = "nz.co.skytv")
@Configuration
@PropertySource("${bookablepromo.properties.location:classpath:/bookablepromo.properties}")
public class BaseConfiguration {


  @Autowired
  Environment environment;

  @Bean
  public static PropertySourcesPlaceholderConfigurer properties() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public AnnotationSessionFactoryBean sessionFactory() {
    AnnotationSessionFactoryBean sessionFactory = new AnnotationSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());
    sessionFactory.setPackagesToScan(new String[] { "nz.co.skytv.bp.protocol.model" });
    sessionFactory.setHibernateProperties(hibernateProperties());

    return sessionFactory;
  }


  @Bean
  public DataSource dataSource()
  {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName(environment.getProperty("jdbc.driverClassName"));
    dataSource.setUrl(environment.getProperty("jdbc.databaseurl"));
    dataSource.setUsername(environment.getProperty("jdbc.username"));
    dataSource.setPassword(environment.getProperty("jdbc.password"));
    return dataSource;
  }

  @Bean
  @Autowired
  public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
    return new HibernateTransactionManager(sessionFactory().getObject());
  }

  Properties hibernateProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.hbm2ddl.auto", "validate");
    properties.setProperty("hibernate.dialect", environment.getProperty("jdbc.dialect"));
    properties.setProperty("hibernate.show_sql", "true");
    return properties;
  }
}
