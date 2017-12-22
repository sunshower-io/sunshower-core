package io.sunshower.service.task.exec;

import io.sunshower.common.Identifier;
import io.sunshower.service.task.Context;
import io.sunshower.service.task.ElementDescriptor;
import io.sunshower.service.task.Run;
import io.sunshower.service.task.TaskContext;
import org.apache.commons.lang.StringUtils;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.*;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Created by haswell on 3/27/17.
 */
class InjectionContext {

    private final TinkerGraph graph;
    private final TaskContext context;
    private final ElementDescriptor descriptor;
    private final ApplicationContext applicationContext;
    private final Map<String, InjectionSite> namedSites;
    private final Map<Class<?>, InjectionSite> typedSites;

    InjectionContext(
            TaskContext context,
            ElementDescriptor descriptor,
            TinkerGraph graph,
            ApplicationContext applicationContext
    ) {
        this.graph = graph;
        this.context = context;
        this.descriptor = descriptor;
        this.namedSites = new HashMap<>();
        this.typedSites = new HashMap<>();
        this.applicationContext = applicationContext;
    }


    public Object invoke() {
        try {
            final Class<?> type = descriptor.getType();
            AutowireCapableBeanFactory beanFactory = applicationContext
                    .getAutowireCapableBeanFactory();

            Object instance = resolveTarget(beanFactory.createBean(
                    type,
                    AutowireCapableBeanFactory.AUTOWIRE_NO,
                    true
            ));


            final Set<InjectionSite> dependencies = new HashSet<>();

            for (Class<?> ct = type; ct != null; ct = ct.getSuperclass()) {
                for (Field field : ct.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Context.class)) {
                        String fieldName = field.getName();
                        String name = resolveName(field, field.getAnnotation(Context.class));

                        Field actualField = ct.getDeclaredField(fieldName);


                        final InjectionSite site = new InjectionSite(
                                context,
                                fieldName,
                                name,
                                actualField,
                                instance,
                                graph,
                                descriptor,
                                this
                        );
                        dependencies.add(site);

                    }
                }
            }

            resolve(dependencies);


            return instance;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Object resolveTarget(Object bean) throws Exception {
        if(AopUtils.isAopProxy(bean) && bean instanceof Advised)  {
            return ((Advised) bean).getTargetSource().getTarget();

        }
        return bean;
    }

    private void resolve(Set<InjectionSite> dependencies) {
        final Iterator<InjectionSite> deps = dependencies.iterator();
        while (deps.hasNext()) {
            final InjectionSite next = deps.next();
            if (next.resolveAndInject()) {
                deps.remove();
            }
        }

        searchAndResolve(dependencies);
    }

    private void searchAndResolve(Set<InjectionSite> dependencies) {
        final Map<Class<?>, Object> resolved = new HashMap<>();
        resolveDependencies(descriptor.getId(), resolved);

        for (InjectionSite site : dependencies) {
            site.inject(resolved);
        }
    }

    private void resolveDependencies(Identifier id, Map<Class<?>, Object> resolved) {
        graph.traversal().V(id)
                .inE().forEachRemaining(e -> {
            Vertex vertex = e.outVertex();
            VertexProperty<Object> result = vertex.property("result");
            if (result.isPresent()) {
                Object value = result.value();
                if(value != null) {
                    Class<?> vtype = value.getClass();
                    resolved.put(vtype, result.value());
                }
            }
            resolveDependencies((Identifier) vertex.id(), resolved);
        });

    }

    private String resolveName(Field field, Context annotation) {
        if (StringUtils.isEmpty(annotation.name())) {
            return field.getName();
        }
        return annotation.name();
    }


}
