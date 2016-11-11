package {{ java-package }};

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.samples.services.SampleService;


@Component(label = "My AEM Service", description = "My AEM Service implementation", metatype = false)
@Service
public class {{ java-class }} implements SampleService {

  private static final Logger LOG = LoggerFactory.getLogger({{ java-class }}.class);

  private String title;

  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  @Override
  public final void doWork() {
    ResourceResolver resourceResolver;
    try {
      resourceResolver = resourceResolverFactory.getServiceResourceResolver(null);

      final Resource resource = resourceResolver.getResource("/content/geometrixx");
      final ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
      title = (String) properties.getOrDefault("jcr:title", "");

    } catch (final LoginException e) {
      LOG.error("Can't get service resource resolver.");
    }
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  @Activate
  protected final void activate(final Map<String, String> properties) throws Exception {
    LOG.info("Service activated");
  }

  @Deactivate
  protected final void deactivate(final Map<String, String> properties) {
    LOG.info("Service deactivated");
  }
}