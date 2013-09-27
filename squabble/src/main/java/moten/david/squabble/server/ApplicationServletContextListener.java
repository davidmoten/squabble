package moten.david.squabble.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationServletContextListener implements
        ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        ApplicationServiceImpl.keepGoing = false;
    }

}
