package io.sunshower.vault.api;

import org.springframework.security.access.prepost.PreAuthorize;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
;

/**
 * Created by haswell on 10/28/16.
 */
@Path("secrets/vault")
@Produces({
        MediaType.APPLICATION_JSON,
        MediaType.APPLICATION_XML
})
@Consumes({
        MediaType.APPLICATION_JSON,
        MediaType.APPLICATION_XML
})
@PreAuthorize("hasAuthority('admin')")
public interface VaultService {

    @POST
    @Path("/")
    Secret save(Secret secret);


    @GET
    @Path("{type}/list")
    <T extends Secret> List<Secret> list(@PathParam("type") Class<T> type);

    @GET
    @Path("/{id}")
    <T extends Secret> T get(Class<T> type, @PathParam("id") UUID id);

    @DELETE
    @Path("/{id}")
    Secret delete(@PathParam("id") UUID id);

}
