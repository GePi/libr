package com.github.gepi.libr.app;

import com.github.gepi.libr.entity.Tag;
import com.github.gepi.libr.entity.TagScheme;
import com.github.gepi.libr.entity.TagTreeDTO;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;

import java.util.*;
import java.util.stream.Collectors;

public class TagTree {
    private final TagScheme scheme;

    private final Map<Tag, Tag> tagParentMap = new HashMap<>();
    private final Map<Tag, List<Tag>> tagChildrenMap = new HashMap<>();
    private final List<Tag> root = new ArrayList<>();

    private final DataManager dataManager;

    public TagTree(List<KeyValueEntity> tagParentList, TagScheme scheme, DataManager dataManager) {
        this.scheme = scheme;
        this.dataManager = dataManager;
        createTree(tagParentList);
    }

    public List<TagTreeDTO> getTreeDTO() {
        List<TagTreeDTO> tagTreeDTOResult = new ArrayList<>();
        Stack<TagTreeDTO> stack = new Stack<>();

        for (var rootTag : root) {
            var dtoRoot = new TagTreeDTO(rootTag, scheme);
            stack.push(dtoRoot);
            while (!stack.empty()) {
                TagTreeDTO current = stack.pop();
                tagTreeDTOResult.add(current);
                for (var child : tagChildrenMap.get(current.getTag())) {
                    var dto = new TagTreeDTO(child, current, scheme);
                    stack.push(dto);
                }
            }

        }
        return tagTreeDTOResult;
    }

    private void createTree(List<KeyValueEntity> tagParentList) {
        for (var entity : tagParentList) {
            tagParentMap.put(entity.getValue("tag"), entity.getValue("parent"));
            tagChildrenMap.put(entity.getValue("tag"), new ArrayList<>());
        }

        for (var entry : tagParentMap.entrySet()) {
            Tag child = entry.getKey();
            Tag parent = entry.getValue();
            if (!child.equals(parent)) {
                tagChildrenMap.computeIfPresent(parent, (parent1, children) -> {
                    children.add(child);
                    return children;
                });
            }
        }

        for (var entry : tagParentMap.entrySet()) {
            if (entry.getKey().equals(entry.getValue())) {
                root.add(entry.getKey());
            }
        }
    }

    public List<Tag> getRoot() {
        return root;
    }

    public List<Tag> getChildren(Tag tag) {
        return tagChildrenMap.getOrDefault(tag,new ArrayList<>());
    }

    public List<Tag> getAllTags() {
        return tagParentMap.keySet().stream().collect(Collectors.toList());
    }

    public List<Tag> getAllChildren(List<Tag> tags) {
        Set<Tag> allChildren = new HashSet<>();
        List<Tag> children = new ArrayList<>();

        for (var tag : tags) {
            children.addAll(tagChildrenMap.get(tag));
        }
        if (!children.isEmpty()) {
            List<Tag> childrenNextLevels = getAllChildren(children);
            allChildren.addAll(childrenNextLevels);
        }

        allChildren.addAll(tags);
        return allChildren.stream().toList();
    }
}