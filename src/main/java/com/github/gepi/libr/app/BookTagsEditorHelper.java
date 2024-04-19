package com.github.gepi.libr.app;

import com.github.gepi.libr.app.event.BookTagsEditorTagListChangedEvent;
import com.github.gepi.libr.entity.Book;
import com.github.gepi.libr.entity.Tag;
import com.github.gepi.libr.entity.TagScheme;
import com.github.gepi.libr.entity.TagTreeDTO;
import io.jmix.ui.UiEventPublisher;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class BookTagsEditorHelper {
    private final UiEventPublisher uiEventPublisher;
    private final TagHierarchyService tagHierarchyService;
    private final BookTagsService bookTagsService;
    private final Map<TagScheme, TagsType> tagsMap = new HashMap<>();
    private Book book;

    public BookTagsEditorHelper(UiEventPublisher uiEventPublisher, TagHierarchyService tagHierarchyService, BookTagsService bookTagsService) {
        this.uiEventPublisher = uiEventPublisher;
        this.tagHierarchyService = tagHierarchyService;
        this.bookTagsService = bookTagsService;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public record TagsType(List<Tag> allTags, List<Tag> selectedTags) {
    }

    public void loadTagsFromAllAssignedSchemas() {
        tagsMap.clear();
        Map<TagScheme, List<Tag>> bookTags4Scheme = bookTagsService.getBookTags(book);

        for (var entry : bookTags4Scheme.entrySet()) {
            TagScheme scheme = entry.getKey();
            List<Tag> bookTags = entry.getValue();
            List<Tag> schemeAllTags = selectSchemeTags(scheme);
            setTags(scheme, schemeAllTags, bookTags);
        }
    }

    private void loadTags(TagScheme scheme) {
        List<Tag> bookTags = bookTagsService.getBookTags(book, scheme);
        List<Tag> schemeAllTags = selectSchemeTags(scheme);
        setTags(scheme, schemeAllTags, bookTags);
    }

    @NotNull
    private List<Tag> selectSchemeTags(TagScheme scheme) {
        List<TagTreeDTO> tagTree = tagHierarchyService.loadTagTree(scheme).getTreeDTO();
        return tagTree.stream().map(TagTreeDTO::getTag).toList();
    }

    public void setTags(TagScheme scheme, List<Tag> allTags, List<Tag> selectedTags) {
        tagsMap.put(scheme, new TagsType(allTags, selectedTags));
    }

    public TagsType getTags(TagScheme scheme) {
        if (!tagsMap.containsKey(scheme)) {
            loadTags(scheme);
        }
        return tagsMap.get(scheme);
    }

    public Map<TagScheme, TagsType> getTags(boolean allSchemes) {
        return tagsMap;
    }

    public List<Tag> getTags() {
        return tagsMap.values().stream()
                .flatMap(tagsType -> tagsType.selectedTags.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void setSelectedTags(TagScheme scheme, List<Tag> selectedTags) {
        tagsMap.computeIfPresent(scheme, (scheme1, TagsType) -> new TagsType(TagsType.allTags, selectedTags));
        uiEventPublisher.publishEvent(new BookTagsEditorTagListChangedEvent(this));
    }
}