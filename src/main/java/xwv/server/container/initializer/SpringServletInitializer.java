package xwv.server.container.initializer;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import xwv.server.container.config.RootConfig;
import xwv.server.container.config.WebConfig;


public class SpringServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @NonNull
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Nullable
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootConfig.class};
    }

    @Nullable
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }


}
