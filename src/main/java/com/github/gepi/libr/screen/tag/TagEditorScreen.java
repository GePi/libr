package com.github.gepi.libr.screen.tag;

import com.github.gepi.libr.app.TagHierarchyService;
import com.github.gepi.libr.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.EntityPicker;
import io.jmix.ui.component.Field;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.model.InstanceLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@UiController("TagEditorScreen")
@UiDescriptor("tag-editor-screen.xml")
public class TagEditorScreen extends Screen {
    @Autowired
    private InstanceContainer<Tag> tagDc;
    @Autowired
    private InstanceContainer<TagHierarchy> tagHierarchyDc;

    @Autowired
    private DataManager dataManager;
    @Autowired
    private TagHierarchyService tagHierarchyService;
    @Autowired
    private EntityPicker<Tag> tagParent;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private InstanceLoader<Tag> tagDl;
    @Autowired
    private InstanceLoader<TagHierarchy> tagHierarchyDl;

    private Map<String, Object> params;
    @Autowired
    private ScreenBuilders screenBuilders;

    private Tag tagResult;
    private TagHierarchy hierarchyResult;
    @Autowired
    private MessageBundle messageBundle;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        TagScheme scheme = (TagScheme) params.get("scheme");
        Tag tag = (Tag) params.get("tag");

        if (tag == null) {
            tag = dataManager.create(Tag.class);
            tag.setScheme(scheme);
            tagDc.setItem(tag);

            TagHierarchy hierarchy = dataManager.create(TagHierarchy.class);
            hierarchy.setTag(tag);
            hierarchy.setScheme(scheme);
            hierarchy.setParent((Tag) params.get("parentTag"));
            tagHierarchyDc.setItem(hierarchy);

        } else {
            tagDl.setEntityId(tag.getId());
            tagDl.load();
            tagHierarchyDl.setParameter("tag1", tag);
            tagHierarchyDl.load();
        }
    }

    @Subscribe
    public void onInit(final InitEvent event) {
        ScreenOptions options = event.getOptions();
        if (options instanceof MapScreenOptions) {
            params = ((MapScreenOptions) options).getParams();
        }
    }

    @Subscribe("windowCommitAndClose")
    public void onWindowCommitAndClose(final Action.ActionPerformedEvent event) {
        tagResult = tagDc.getItem();
        hierarchyResult = tagHierarchyDc.getItem();
        tagHierarchyService.saveTagAndHierarchy(tagResult, hierarchyResult);
        this.close(StandardOutcome.COMMIT);
    }

    @Subscribe("windowClose")
    public void onWindowClose(final Action.ActionPerformedEvent event) {
        this.close(StandardOutcome.CLOSE);
    }

    @Subscribe("tagParent.treeLookup")
    public void onTagParentTreeLookup(final Action.ActionPerformedEvent event) {

        TagBrowse lookupScreen = screenBuilders.screen(this)
                .withScreenClass(TagBrowse.class)
                .build();


        lookupScreen.setSelectHandler(items->handleSelection(items,tagParent));

        lookupScreen.setTagsTreeDTO(tagHierarchyService.loadTagTree((TagScheme) params.get("scheme")).getTreeDTO());
        lookupScreen.setCaption(messageBundle.getMessage("tagEditorScreen.selectParentTag"));
        lookupScreen.show();
    }

    @SuppressWarnings("unchecked")
    private <E> void handleSelection(Collection<E> items, Field<Tag> field) {
        Collection<Tag> tags = handleSelectionTagTreeDTOTransformation((Collection<TagTreeDTO>) items);
        tags.stream().findFirst().ifPresent(field::setValue);
    }

    private Collection<Tag> handleSelectionTagTreeDTOTransformation(Collection<TagTreeDTO> items) {
        return items.stream().map(TagTreeDTO::getTag).collect(Collectors.toList());
    }

    public void setP(Collection<Object> tagNeTagADTO) {
        for (var dto : tagNeTagADTO) {
            System.out.println(((TagTreeDTO) dto).getTag().getTitle());
        }
    }

    public Tag getTagResult() {
        return tagResult;
    }

    public TagHierarchy getHierarchyResult() {
        return hierarchyResult;
    }

    @Subscribe("tagParent.rootLink")
    public void onTagParentRootLink(final Action.ActionPerformedEvent event) {
        tagParent.setValue(null);
    }
}
