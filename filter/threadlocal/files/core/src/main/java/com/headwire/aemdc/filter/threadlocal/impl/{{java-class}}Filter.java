package {{ java-package }};

import {{ java-interface-package }}.{{ java-class }}Service;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/*
    The ThreadLocal variable defined in this Http Servlet Filter can now be accessed anywhere in the context of this
    thread. An example use case is Decorating a Sling Resource (via ResourceDecorators) based on some facet of the
    associated HTTP Request (since the HTTP Request is not available to ResourceDecorators).

    If variables need ot be passed along threads IN the context of the HTTP Request, it is better to set them via
    request.setAttribute(..) and retrieve them via request.getAttribute(..).

    In the case of this ThreadLocal variable it can be retrieved form an OSGi Service as follows:

    public ConsumerServiceImpl implements ConsumerService {

        @Reference
        {{ java-class }}Service sampleThreadLocalService;

        public final String getMessage() {
            if (sampleThreadLocalService.get()) {
                return "Welcome to preview mode!"
            } else {
                return "Welcome to normal mode!";
            }
        }

    }
*/

@Component(
        label = "Thread Local Filter and Service",
        description = "This is a OGSi service that is a Sling Filter which sets ThreadLocal state AND an OSGi Service "
        + "that exposes the ThreadLocal state to other OSGi Services."
)
@Properties({
        @Property(
                name = "sling.filter.scope",
                value = "request"
        ),
        @Property(
                name = "sling.filter.order",
                intValue = Integer.MIN_VALUE
        )
})
@Service
public class {{ java-class }}Filter implements Filter, {{ java-class }}Service {
    private static final Logger log = LoggerFactory.getLogger({{ java-class }}Filter.class);

    // Define the ThreadLocal object; the Generic type (in this case Boolean) defines what
    // data type the ThreadLocal object will store
    // Since this is ThreadLocal w dont need to wory about concurrency or other Threads modifying this value.
    private static final ThreadLocal<Boolean> THREAD_LOCAL = new ThreadLocal<Boolean>() {
        @Override protected Boolean initialValue() {
            set(false);
            return get();
        }
    };

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // Usually, do nothing
    }

    @Override
    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        // Since we are in the context of a SlingFilter; the Request is always a SlingHttpServletRequest
        SlingHttpServletRequest slingHttpServletRequest = (SlingHttpServletRequest) request;

        // Set the thread local value based on your use case; in this case the thread local variable is set to "true"
        // if a HTTP Request parameter of "preview" is provided
        if (slingHttpServletRequest.getParameter("preview") != null) {
            THREAD_LOCAL.set(true);
        }

        try {
            // Continue processing the request chain
            chain.doFilter(request, response);
        } finally {
            // Good housekeeping; Clean up after yourself!!!
            THREAD_LOCAL.remove();
        }
    }


    /**
     * This is the method that will expose the ThreadLocal variable to other OSGi Services.
     *
     * @return the thread local value
     */
    @Override
    public final boolean get() {
        return THREAD_LOCAL.get();
    }

    @Override
    public void destroy() {
        // Usually, do nothing
    }
}
