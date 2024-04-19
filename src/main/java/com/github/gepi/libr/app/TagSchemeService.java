package com.github.gepi.libr.app;

import com.github.gepi.libr.entity.TagScheme;
import com.github.gepi.libr.entity.User;
import io.jmix.core.security.CurrentAuthentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class TagSchemeService {
    private final CurrentAuthentication currentAuthentication;

    @PersistenceContext
    private EntityManager entityManager;

    public TagSchemeService(CurrentAuthentication currentAuthentication) {
        this.currentAuthentication = currentAuthentication;
    }

    @Transactional
    public void saveWithConstraints(TagScheme schemeSaving, Boolean onlyApplyConstraints) {
        if (schemeSaving == null) {
            return;
        }
        if (schemeSaving.getDef()) {
            entityManager.createQuery("update TagScheme ts set ts.def = :def where ts.owner = :owner and ts.id <> :schemeId")
                    .setParameter("owner", (User) currentAuthentication.getUser())
                    .setParameter("def", Boolean.FALSE)
                    .setParameter("schemeId", schemeSaving.getId())
                    .executeUpdate();
        }
        if (!onlyApplyConstraints) {
            entityManager.persist(schemeSaving);
        }
    }

}