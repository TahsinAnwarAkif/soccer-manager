package com.soccermanager.config;

import com.soccermanager.BasicConfig;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.core.rest.ODataRootLocator;
import org.apache.olingo.odata2.core.rest.app.ODataApplication;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

/**
 * @author akif
 * @since 6/18/22
 */
@Component
@ApplicationPath("/odata")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig(BasicConfig serviceFactory, EntityManagerFactory emf) {
        ODataApplication app = new ODataApplication();
        app.getClasses().forEach(c -> {
            if (!ODataRootLocator.class.isAssignableFrom(c)) {
                register(c);
            }
        });

        register(new EntityManagerFilter(emf));
        register(new EntityRootLocator(serviceFactory));
    }

    @Provider
    public static class EntityManagerFilter implements ContainerRequestFilter, ContainerResponseFilter {

        public static final String EM_REQUEST_ATTRIBUTE = EntityManagerFilter.class.getName() + "_ENTITY_MANAGER";
        private final EntityManagerFactory emf;

        @Context
        private HttpServletRequest httpRequest;

        public EntityManagerFilter(EntityManagerFactory emf) {
            this.emf = emf;
        }

        @Override
        public void filter(ContainerRequestContext ctx) {
            EntityManager em = this.emf.createEntityManager();
            httpRequest.setAttribute(EM_REQUEST_ATTRIBUTE, em);

            if (!"GET".equalsIgnoreCase(ctx.getMethod())) {
                em.getTransaction().begin();
            }
        }

        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
            EntityManager em = (EntityManager) httpRequest.getAttribute(EM_REQUEST_ATTRIBUTE);

            if (!"GET".equalsIgnoreCase(requestContext.getMethod())) {
                EntityTransaction t = em.getTransaction();

                if (t.isActive()) {
                    if (!t.getRollbackOnly()) {
                        t.commit();
                    }
                }
            }

            em.close();
        }
    }

    @Path("/")
    private static class EntityRootLocator extends ODataRootLocator {

        private BasicConfig serviceFactory;

        public EntityRootLocator(BasicConfig serviceFactory) {
            this.serviceFactory = serviceFactory;
        }

        @Override
        public ODataServiceFactory getServiceFactory() {
            return this.serviceFactory;
        }
    }
}
