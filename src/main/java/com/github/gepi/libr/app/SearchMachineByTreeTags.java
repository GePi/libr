package com.github.gepi.libr.app;

import com.github.gepi.libr.entity.TagTreeDTO;
import io.jmix.ui.component.Tree;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
public class SearchMachineByTreeTags {

    private String searchPattern;
    List<TagTreeDTO> foundNodes;
    private int foundNodesIndex;

    Tree<TagTreeDTO> tagsTree;
    private boolean found = false;

    public void search(Tree<TagTreeDTO> tagsTree, String pattern) {
        found = false;
        if (pattern == null) {
            return;
        }

        searchPattern = pattern;
        this.tagsTree = tagsTree;
        foundNodesIndex = 0;
        foundNodes = tagsTree.getItems().getItems()
                .filter(tagTreeDTO -> StringUtils.containsIgnoreCase(tagTreeDTO.getTag().getTitle(), searchPattern))
                .toList();

        if (!foundNodes.isEmpty()) {
            found = true;
            showFoundNode();
        }
    }

    public void gotoNext(boolean up) {
        if (!found) {
            return;
        }
        foundNodesIndex = Math.floorMod(foundNodesIndex + (up ? -1 : 1), foundNodes.size());
        showFoundNode();
    }

    private void showFoundNode() {
        expandPath(tagsTree, foundNodes.get(foundNodesIndex));
        tagsTree.setSelected(foundNodes.get(foundNodesIndex));
    }

    private void expandPath(Tree<TagTreeDTO> tagsTree, TagTreeDTO node) {
        tagsTree.expand(node);
        if (node.getParent() == null || node.getParent().equals(node.getTag())) {
            return;
        }
        expandPath(tagsTree, node.getParent());
    }
}
