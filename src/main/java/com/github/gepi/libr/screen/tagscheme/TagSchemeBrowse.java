package com.github.gepi.libr.screen.tagscheme;

import com.github.gepi.libr.entity.TagScheme;
import com.github.gepi.libr.entity.User;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("TagScheme.browse")
@UiDescriptor("tag-scheme-browse.xml")
@LookupComponent("tagSchemesTable")
public class TagSchemeBrowse extends StandardLookup<TagScheme> {
    @Autowired
    private CollectionLoader<TagScheme> tagSchemesDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Subscribe
    public void onInit(final InitEvent event) {
        tagSchemesDl.setParameter("owner", (User) currentAuthentication.getUser());
    }
}