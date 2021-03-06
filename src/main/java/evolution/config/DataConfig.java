package evolution.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "evolution.dao",
        entityManagerFactoryRef = "entityManagerFactoryBean",
        transactionManagerRef = "jpaTransactionManager")
@EnableTransactionManagement(proxyTargetClass = true)
public class DataConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://ec2-176-34-113-15.eu-west-1.compute.amazonaws.com:5432/d1dsfee517idaf?sslmode=require&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory");
        dataSource.setUsername("blnjznffbkytsy");
        dataSource.setPassword("c119924e3525cce4b65884b042b6b83a6256f09e464b4767338d429d5adc6462");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("evolution");
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        entityManagerFactoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);


        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");

        entityManagerFactoryBean.setJpaProperties(properties);
        return entityManagerFactoryBean;
    }

    @Bean
    public JpaTransactionManager jpaTransactionManager(){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
        return transactionManager;
    }
}


//    @Bean(name = "hibernateSessionFactory")
//    public SessionFactory sessionFactory() {
//        return new LocalSessionFactoryBuilder(dataSource())
//
//                .setProperty("hibernate.hbm2ddl.auto", "update")
//                .setProperty("hibernate.show_sql", "true")
//                .setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory")
//                .setProperty("hibernate.cache.use_query_cache", "true")
//                .setProperty("hibernate.cache.use_second_level_cache", "true")
////                .setProperty("net.sf.ehcache.configurationResourceName", "/myehcache.xml")
//                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect")
//                .setProperty("hibernate.enable_lazy_load_no_trans", "true")
//                .buildSessionFactory();
//    }