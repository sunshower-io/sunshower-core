package io.sunshower.service.graph.service;

/**
 * Created by haswell on 5/25/17.
 */
public interface GraphServiceResolver {
    <T extends GraphService> T resolve(Class<T> type, String name);
}
