package io.sunshower.core.security;

import io.sunshower.model.core.auth.Role;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/** Created by haswell on 10/26/16. */
@Path("roles")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public interface RoleService {

  @PUT
  @Path("{name}")
  Role findOrCreate(Role role);
}
