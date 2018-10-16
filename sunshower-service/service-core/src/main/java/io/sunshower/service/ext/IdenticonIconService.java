package io.sunshower.service.ext;

import io.sunshower.common.Identifier;
import io.sunshower.common.crypto.Hashes;
import io.sunshower.common.crypto.Multihash;
import io.sunshower.model.core.Image;
import io.sunshower.model.core.ImageAware;
import io.sunshower.model.core.auth.ImageType;
import io.sunshower.service.security.PermissionsService;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import jdenticon.Jdenticon;
import lombok.val;
import org.apache.commons.codec.binary.Hex;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.transaction.annotation.Transactional;

public class IdenticonIconService implements IconService {
  static final Hashes.HashFunction hash = Hashes.create(Multihash.Type.SHA_2_256);

  @PersistenceContext private EntityManager entityManager;
  @Inject private PermissionsService<Permission> permissionsService;

  @Override
  public Image iconFor(Object o, int w, int h) {
    val hash = IdenticonIconService.hash.hash(o);
    val hex = new String(Hex.encodeHex(hash.getBytes()));
    val svg = Jdenticon.Companion.toSvg(hex, w, 0f);
    val img = new Image();
    img.setData(svg.getBytes());
    img.setType(ImageType.SVG);
    return img;
  }

  @Override
  public Image iconDirect(String string, int i, int i1) {
    final byte[] digest =
        io.sunshower.common.Hashes.hashCode(io.sunshower.common.Hashes.Algorithm.SHA1)
            .digest(string.getBytes());
    val svg = Jdenticon.Companion.toSvg(Hex.encodeHexString(digest), i, 0f);
    val img = new Image();
    img.setData(svg.getBytes());
    img.setType(ImageType.SVG);
    return img;
  }

  @Override
  @Transactional
  public <T extends ImageAware> Image setIcon(Class<T> type, Identifier id, Image image) {
    final T t = doLoad(type, id, false);
    if (t.getImage() != null) {
      entityManager.remove(t.getImage());
    }
    t.setImage(image);
    entityManager.flush();
    return image;
  }

  @Override
  public <T extends ImageAware> Image getIcon(Class<T> type, Identifier id, boolean skipAuth) {
    final T t = doLoad(type, id, skipAuth);
    return t.getImage();
  }

  @Override
  public <T extends ImageAware> Image getIcon(Class<T> type, Identifier id) {
    final T loaded = doLoad(type, id, false);
    return loaded.getImage();
  }

  private <T extends ImageAware> T doLoad(Class<T> type, Identifier id, boolean skipAuth) {
    final T t = entityManager.find(type, id);
    if (t == null) {
      throw new EntityNotFoundException("No imageaware with that id");
    }
    if (!skipAuth) {
      permissionsService.checkPermission(t, BasePermission.WRITE);
    }
    return t;
  }
}
