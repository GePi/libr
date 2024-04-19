package com.github.gepi.libr.screen.tag;

import com.github.gepi.libr.app.SearchMachineByTreeTags;
import com.github.gepi.libr.entity.Tag;
import com.github.gepi.libr.entity.TagTreeDTO;
import io.jmix.core.LoadContext;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.HasValue;
import io.jmix.ui.component.Tree;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@UiController("Tag.browse")
@UiDescriptor("tag-browse.xml")
@LookupComponent("tagsTree")
public class TagBrowse extends StandardLookup<Tag> {
    @Autowired
    private Tree<TagTreeDTO> tagsTree;
    @Autowired
    private SearchMachineByTreeTags searchMachine;
    protected List<TagTreeDTO> tagsTreeDTO = new ArrayList<>();
    protected Tree.SelectionMode selectionMode = Tree.SelectionMode.SINGLE;
    protected String caption = "";

    @Install(to = "tagsDl", target = Target.DATA_LOADER)
    private List<TagTreeDTO> tagsDlLoadDelegate(final LoadContext<TagTreeDTO> loadContext) {
        return tagsTreeDTO;
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        tagsTree.expandUpTo(1);
        tagsTree.setSelectionMode(selectionMode);
        event.getSource().getWindow().setCaption(caption);
    }

    @Subscribe("searchTagField")
    protected void onSearchTagFieldValueChange(HasValue.ValueChangeEvent<String> event) {
        searchMachine.search(tagsTree, event.getValue());
    }

    @Subscribe("gotoNextBtn")
    public void onGotoNextBtnClick(final Button.ClickEvent event) {
        searchMachine.gotoNext(true);
    }

    @Subscribe("gotoPrevBtn")
    public void onGotoPrevBtnClick(final Button.ClickEvent event) {
        searchMachine.gotoNext(false);
    }

    @Subscribe("collapseBtn")
    public void onCollapseBtnClick(final Button.ClickEvent event) {
        tagsTree.collapseTree();
        tagsTree.setSelected(new ArrayList<>());
    }

    public void setTagsTreeDTO(List<TagTreeDTO> tagsTreeDTO) {
        this.tagsTreeDTO = tagsTreeDTO;
    }

    public void setSelectionMode(Tree.SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

}