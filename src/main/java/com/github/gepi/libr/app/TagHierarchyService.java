package com.github.gepi.libr.app;

import com.github.gepi.libr.app.exception.TagTreeMovementTagException;
import com.github.gepi.libr.entity.Tag;
import com.github.gepi.libr.entity.TagHierarchy;
import com.github.gepi.libr.entity.TagScheme;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
import io.jmix.core.Metadata;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TagHierarchyService {
    private final FetchPlans fetchPlans;
    private final Metadata metadata;
    @Autowired
    private DataManager dataManager;

    public TagHierarchyService(FetchPlans fetchPlans, Metadata metadata) {
        this.fetchPlans = fetchPlans;
        this.metadata = metadata;
    }
    public TagTree loadTagTree(TagScheme scheme) {
        List<KeyValueEntity> list = dataManager.loadValues("select t.tag, t.parent " +
                        "from TagHierarchy t where t.scheme = :scheme")
                .parameter("scheme", scheme)
                .properties("tag", "parent")
                .list();
        TagTree tagTree = new TagTree(list, scheme, dataManager);
        return tagTree;
    }

    @Transactional
    public void removeTag(Tag tag, TagScheme scheme) {
        removeTagHierarchy(tag, scheme);
        dataManager.remove(tag);
    }

    @Transactional
    public void removeTagHierarchy(Tag tag, TagScheme scheme) {
        List<TagHierarchy> subHierarchyList = new ArrayList<>();
        dataManager.load(TagHierarchy.class).query("select t from TagHierarchy t " +
                        "where t.tag = :tag1 " +
                        "and t.scheme = :scheme1")
                .parameter("tag1", tag)
                .parameter("scheme1", scheme)
                .optional().ifPresent(tagHierarchy -> {
                    loadHierarchyRecursively(tagHierarchy, subHierarchyList);
                });
        if (subHierarchyList.isEmpty()) {
            return;
        }
        dataManager.remove(subHierarchyList);
    }

    private void loadHierarchyRecursively(TagHierarchy tagHierarchy, List<TagHierarchy> subHierarchyList) {
        subHierarchyList.add(tagHierarchy);
        dataManager.load(TagHierarchy.class).query("select t from TagHierarchy t " +
                        "where t.scheme = :scheme1 " +
                        "and t.parent = :parent1")
                .parameter("parent1", tagHierarchy.getTag())
                .parameter("scheme1", tagHierarchy.getScheme())
                .list().stream()
                .filter(tagHierarchy1 -> !tagHierarchy1.equals(tagHierarchy))
                .forEach(tagHierarchy1 -> loadHierarchyRecursively(tagHierarchy1, subHierarchyList));
    }

    @Transactional
    public void saveTagAndHierarchy(Tag tag, TagHierarchy hierarchy) {
        dataManager.save(tag);
        saveHierarchy(hierarchy);
    }

    @Transactional
    public void saveHierarchy(TagHierarchy hierarchyRow) throws RuntimeException {
        if (hierarchyRow == null) {
            return;
        }
        if (hierarchyRow.getParent() == null) {
            hierarchyRow.setParent(hierarchyRow.getTag());
        }

        // получаем всю иерархию
        FetchPlan fp = fetchPlans.builder(TagHierarchy.class)
                .add("scheme")
                .add("tag")
                .add("parent")
                .build();

        Map<Tag, TagHierarchy> schemeHierarchyMap =
                dataManager.load(TagHierarchy.class)
                        .query("select t from TagHierarchy t where t.scheme = :scheme1")
                        .parameter("scheme1", hierarchyRow.getScheme())
                        .fetchPlan(fp)
                        .list()
                        .stream()
                        .collect(Collectors.toMap(TagHierarchy::getTag, Function.identity()));

        TagHierarchy oldRow = schemeHierarchyMap.get(hierarchyRow.getTag());
        if (oldRow != null) { // для существующего тега
            // проверяем не перемещается ли узел в одного из своих потомков
            TagHierarchy current = hierarchyRow;
            while (!current.getTag().equals(current.getParent())) {
                current = schemeHierarchyMap.get(current.getParent());
                if (current.getTag().equals(hierarchyRow.getTag())) {
                    throw new TagTreeMovementTagException("Нельзя перемещать узел в своего потомка");
                }
            }
            dataManager.remove(oldRow);
            TagHierarchy hierarchy = metadata.create(TagHierarchy.class);
            hierarchy.setScheme(hierarchyRow.getScheme());
            hierarchy.setTag(hierarchyRow.getTag());
            hierarchy.setParent(hierarchyRow.getParent());
            hierarchyRow = hierarchy;
        }
        dataManager.save(hierarchyRow);
    }
}