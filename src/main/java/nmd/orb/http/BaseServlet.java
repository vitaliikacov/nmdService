package nmd.orb.http;

import nmd.orb.http.servlets.ReadsServlet;
import nmd.orb.http.tools.ResponseBody;
import nmd.orb.http.tools.ServletTools;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nmd.orb.http.tools.ServletTools.*;

/**
 * @author : igu
 */
public abstract class BaseServlet extends HttpServlet {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private static final Logger LOGGER = Logger.getLogger(ReadsServlet.class.getName());

    protected final Map<String, Handler> handlers;

    protected BaseServlet() {
        this.handlers = new HashMap<>();
    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) {
        final long startTime = System.currentTimeMillis();

        try {
            final ResponseBody responseBody = createResponseBody(request);

            if (responseBody == null) {
                super.service(request, response);
            } else {
                writeResponseBody(startTime, responseBody, response);
            }
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Unhandled exception", exception);

            writeException(startTime, exception, response);
        }
    }

    private ResponseBody createResponseBody(final HttpServletRequest request) {
        final String method = request.getMethod();
        Handler handler = this.handlers.get(method);

        handler = handler == null ? Handler.EMPTY_HANDLER : handler;

        final String pathInfo = request.getPathInfo();
        final List<String> elements = ServletTools.parse(pathInfo);

        final Map requestParameters = request.getParameterMap();
        final Map<String, String> parameters = convert(requestParameters);

        final String body = ServletTools.readRequestBody(request);

        return handler.handle(elements, parameters, body);
    }

}
