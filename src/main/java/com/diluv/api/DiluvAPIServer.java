package com.diluv.api;

import java.io.File;
import java.nio.file.Paths;

import javax.ws.rs.core.Application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import com.diluv.api.v1.APIV1;
import io.undertow.Undertow;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;

public class DiluvAPIServer {

    public static final Logger LOGGER = LogManager.getLogger("API");

    private final UndertowJaxrsServer server;

    public DiluvAPIServer () {

        this.server = new UndertowJaxrsServer();
    }

    public void start (String host, int port) {

        this.deploy("API V1", "/v1", APIV1.class);

        ResourceHandler resourceHandler = new ResourceHandler(new PathResourceManager(
            Paths.get(new File("public/").getAbsolutePath()), 100))
            .setDirectoryListingEnabled(true)
            .addWelcomeFiles("index.html");
        this.server.addResourcePrefixPath("/public", resourceHandler);
        this.server.start(Undertow.builder().addHttpListener(port, host));
        LOGGER.info("Server started on {}:{}", host, port);
    }

    private void deploy (String name, String path, Class<? extends Application> application) {

        this.deploy(name, "/", path, application);
    }

    /**
     * Creates and deploys a new deployment to the core undertow server.
     *
     * @param name The name for the deployment.
     * @param prefix The prefix to map the deployment to.
     * @param path The path used to access this deployment.
     * @param application An application class containing all the classes to load into this
     *     deployment.
     */
    private void deploy (String name, String prefix, String path, Class<? extends Application> application) {

        final ResteasyDeployment deployment = new ResteasyDeploymentImpl();
        deployment.setApplicationClass(application.getName());
        deployment.setInjectorFactoryClass("org.jboss.resteasy.cdi.CdiInjectorFactory");

        final DeploymentInfo info = this.server.undertowDeployment(deployment, prefix);
        info.setClassLoader(this.getClass().getClassLoader());
        info.setDeploymentName(name);
        info.setContextPath(path);

        info.addListener(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class));

        this.server.deploy(info);
    }
}