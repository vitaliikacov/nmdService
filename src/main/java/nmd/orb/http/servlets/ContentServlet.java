package nmd.orb.http.servlets;

import nmd.orb.gae.GaeWrappers;
import nmd.orb.http.BaseServlet;
import nmd.orb.http.servlets.cron.CronServletGetRequestHandler;

/**
 * @author : igu
 */
public class ContentServlet extends BaseServlet {

    public ContentServlet() {
        super();

        this.handlers.put(GET, new CronServletGetRequestHandler(GaeWrappers.INSTANCE.getCronServiceWrapper()));
    }

}
