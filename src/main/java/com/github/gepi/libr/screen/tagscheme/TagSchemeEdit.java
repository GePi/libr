package com.github.gepi.libr.screen.tagscheme;

import com.github.gepi.libr.app.TagSchemeService;
import io.jmix.ui.component.CheckBox;
import io.jmix.ui.screen.*;
import com.github.gepi.libr.entity.TagScheme;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("TagScheme.edit")
@UiDescriptor("tag-scheme-edit.xml")
@EditedEntityContainer("tagSchemeDc")
public class TagSchemeEdit extends StandardEditor<TagScheme> {
    @Autowired
    private TagSchemeService tagSchemeService;
    @Autowired
    private CheckBox defField;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        if (getEditedEntity().getDef()) {
            defField.setEditable(false);
        }
    }

    @Subscribe
    public void onBeforeCommitChanges(final BeforeCommitChangesEvent event) {
        tagSchemeService.saveWithConstraints(getEditedEntity(), true);
    }
}