package utils;

import java.io.FileInputStream;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.perisic.beds.rmiinterface.Question;

/**
 * this class will be used to configure hibernate dynamically,
 * meaning that  the configuration is store in .poperties file
 */
public class HibernateUtil {
	private static SessionFactory sessionFactory;
	private static Properties prop;
	private static StandardServiceRegistry registry;

	
	/**
	 * this method is used to load configuration from file
	 * @param filename: string loca
	 */
	public static void loadConfig(String filename) {
		try (FileInputStream stream = new FileInputStream(filename)) {
			prop = new Properties();
			prop.load(stream);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * this method is used to initialize sessionfactory attribute and get it
	 * @return SessionFactory object (singleton)
	 */
	
	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				// Create registry builder
				StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

				// Apply settings
				registryBuilder.applySettings(prop);

				// Create registry
				registry = registryBuilder.build();

				// Create MetadataSources and adds Question entity
				MetadataSources sources = new MetadataSources(registry).addAnnotatedClass(Question.class);

				//sources.addAnnotatedClass(Answer.class);
				// Create Metadata
				Metadata metadata = sources.getMetadataBuilder().build();

				// Create SessionFactory
				sessionFactory = sources.buildMetadata().buildSessionFactory();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sessionFactory;
	}
	
	/**
	 * shutdown registry 
	 */
	public static void shutdown() {
		if (registry != null) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}

}