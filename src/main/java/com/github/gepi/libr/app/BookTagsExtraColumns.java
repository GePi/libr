package com.github.gepi.libr.app;

import com.github.gepi.libr.entity.Book;
import com.github.gepi.libr.entity.Tag;
import com.github.gepi.libr.entity.TagScheme;
import com.github.gepi.libr.entity.TagTreeDTO;
import io.jmix.ui.component.DataGrid;
import io.jmix.ui.component.TreeDataGrid;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BookTagsExtraColumns {
    public enum Enum {
        BOOKS("amountBooks", 0), ALL_CHILDREN_BOOKS("amountBooksAllChildren", 1);

        public final String id;
        public final int num;

        Enum(String id, int num) {
            this.id = id;
            this.num = num;
        }
    }

    private TagTree tagTree;
    private final BookTagsService bookTagsService;
    private final Map<Tag, Object[]> extraTreeFieldsMap = new HashMap<>();

    public BookTagsExtraColumns(BookTagsService bookTagsService) {
        this.bookTagsService = bookTagsService;
    }

    public void fillExtraTreeFieldsMap(TagScheme scheme, TagTree tagTree, boolean joinTransaction) {
        if (tagTree == null) {
            throw new RuntimeException("Не установлено tagTree");
        }
        this.tagTree = tagTree;

        extraTreeFieldsMap.clear();
        for (var tag : tagTree.getAllTags()) {
            Object[] val = new Object[2];
            Arrays.fill(val, 0);
            extraTreeFieldsMap.put(tag, val);
        }

        Map<Tag, BookTagsService.TagInfo> booksAmountByTag = bookTagsService.getBooksAmountByTag(tagTree.getAllTags(), joinTransaction);
        BookTagsService.TagInfo tagInfoDefault = new BookTagsService.TagInfo(0);

        for (var tag : tagTree.getAllTags()) {
            extraTreeFieldsMap.computeIfPresent(tag, (tag1, objects) -> {
                objects[0] = booksAmountByTag.getOrDefault(tag, tagInfoDefault).getBookAmount();
                return objects;
            });
        }

        for (var tag : tagTree.getRoot()) {
            sumChildrenAmountBooks(tag, bookTagsService.getBookTags(scheme, joinTransaction));
        }
    }

    public void clear() {
        extraTreeFieldsMap.clear();
    }

    private Set<Book> sumChildrenAmountBooks(Tag tag, List<com.github.gepi.libr.entity.BookTags> bookTags) {
        Set<Book> childrenBook = new HashSet<>();
        for (var child : tagTree.getChildren(tag)) {
            childrenBook.addAll(sumChildrenAmountBooks(child, bookTags));
        }
        Set<Book> selfBook = bookTags.stream().filter(bookTags1 -> bookTags1.getTag().equals(tag)).map(com.github.gepi.libr.entity.BookTags::getBook).collect(Collectors.toSet());
        selfBook.addAll(childrenBook);
        extraTreeFieldsMap.get(tag)[1] = selfBook.size();
        return selfBook;
    }

    public class GridGenerator {
        private static final Map<Enum, String> htmlHeaderTemplateMap =
                new EnumMap<>(Map.of(
                        Enum.BOOKS, "<img class='my-imgicon' src='VAADIN/themes/silence/icons/collapsed-folder-bg.png'/> Книг",
                        Enum.ALL_CHILDREN_BOOKS, "<img class='my-imgicon' src='VAADIN/themes/silence/icons/expanded-folder-bg.png'/> Книг всего"));

        public void setHeader(@NotNull Enum column, @NotNull DataGrid.HeaderRow headerRow) {
            if (headerRow == null) return;
            if (headerRow.getCell(column.id) == null) return;
            headerRow.getCell(column.id).setHtml(htmlHeaderTemplateMap.get(column));
        }

        public void addColumn(@NotNull TreeDataGrid<TagTreeDTO> tagTreeDataGrid, @NotNull Enum column) {
            tagTreeDataGrid.addGeneratedColumn(column.id, getValue(column.num));
        }

        private Function<DataGrid.ColumnGeneratorEvent<TagTreeDTO>, ?> getValue(int colNum) {
            return event -> {
                if (extraTreeFieldsMap.get(event.getItem().getTag()) == null) return 0;
                return Integer.toString((Integer) extraTreeFieldsMap.get(event.getItem().getTag())[colNum]);
            };
        }

    }
}

