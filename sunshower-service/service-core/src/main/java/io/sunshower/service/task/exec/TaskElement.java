package io.sunshower.service.task.exec;

import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * Created by haswell on 2/15/17.
 */
class TaskElement {
    final Vertex vertex;

    TaskElement(Vertex vertex) {
        this.vertex = vertex;
    }
}
