package nmd.rss.collector.rest.servlets;

import nmd.rss.collector.rest.BaseServlet;

import static nmd.rss.collector.rest.servlets.categories.CategoriesServletDeleteRequestHandler.CATEGORIES_SERVLET_DELETE_REQUEST_HANDLER;
import static nmd.rss.collector.rest.servlets.categories.CategoriesServletGetRequestHandler.CATEGORIES_SERVLET_GET_REQUEST_HANDLER;
import static nmd.rss.collector.rest.servlets.categories.CategoriesServletPostRequestHandler.CATEGORIES_SERVLET_POST_REQUEST_HANDLER;
import static nmd.rss.collector.rest.servlets.categories.CategoriesServletPutRequestHandler.CATEGORIES_SERVLET_PUT_REQUEST_HANDLER;

/**
 * Author : Igor Usenko ( igors48@gmail.com )
 * Date : 22.03.2014
 */
public class CategoriesServlet extends BaseServlet {

    public CategoriesServlet() {
        super();

        this.handlers.put(GET, CATEGORIES_SERVLET_GET_REQUEST_HANDLER);
        this.handlers.put(POST, CATEGORIES_SERVLET_POST_REQUEST_HANDLER);
        this.handlers.put(PUT, CATEGORIES_SERVLET_PUT_REQUEST_HANDLER);
        this.handlers.put(DELETE, CATEGORIES_SERVLET_DELETE_REQUEST_HANDLER);
    }

}
