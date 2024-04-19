package com.github.gepi.libr.app;

import com.github.gepi.libr.entity.*;
import io.jmix.core.*;
import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BookTagsService {

    private final FetchPlans fetchPlans;
    @Autowired
    private DataManager dataManager;

    public BookTagsService(FetchPlans fetchPlans) {
        this.fetchPlans = fetchPlans;
    }

    public List<Tag> getBookTags(Book book, TagScheme scheme) {
        return dataManager.load(Tag.class)
                .query("select t from BookTags bt inner join Tag t on bt.tag = t inner join TagHierarchy th on th.tag = t  where bt.book.id = :bookId and th.scheme.id = :schemeId")
                .parameter("bookId", book.getId())
                .parameter("schemeId", scheme.getId())
                .list();
    }

    public List<BookTags> getBookTags(TagScheme scheme, boolean joinTransaction) {
        FluentLoader.ByQuery<BookTags> bookTagsByQuery = dataManager.load(BookTags.class)
                .query("select bt from BookTags bt inner join Tag t on bt.tag = t inner join TagHierarchy th on th.tag = t  where th.scheme.id = :schemeId")
                .parameter("schemeId", scheme.getId());
        bookTagsByQuery.joinTransaction(joinTransaction);
        return bookTagsByQuery.list();
    }

    public Map<Tag, TagInfo> getBooksAmountByTag(List<Tag> list, boolean joinTransaction) {

        FluentValuesLoader properties = dataManager
                .loadValues("select bt.tag,  count(bt.book) from BookTags bt where bt.tag in :tagList group by bt.tag")
                .parameter("tagList", list)
                .properties("tag", "bookAmount");
        properties.joinTransaction(joinTransaction);
        List<KeyValueEntity> list1 = properties.list();

        return list1.stream()
                .collect(
                        Collectors.toMap(
                                keyValueEntity -> keyValueEntity.getValue("tag"),
                                keyValueEntity -> new TagInfo(keyValueEntity.getValue("bookAmount"))));
    }

    @Transactional
    public void assignTag(Book book, Tag tag) {
        Optional<BookTags> optBookTag = dataManager.load(BookTags.class)
                .query("select bt from BookTags bt where bt.tag.id = :tagId and bt.book.id = :bookId ")
                .parameter("tagId", tag.getId()).parameter("bookId", book.getId())
                .optional();
        if (optBookTag.isEmpty()) {
            BookTags bookTags = dataManager.create(BookTags.class);

          /*BookTagsId id = dataManager.create(BookTagsId.class);
            id.setBookId(book.getId());
            id.setTagId(tag.getId());
            bookTags.setId(id);*/
            // Каким бы образом не создавался Embedded ключ, после сохранения Eclipse генерирует новый,
            // а jmix после сохранения ОДНОЙ сущности, пытается найти её же у себя в кеше по этому ключу и падает.
            // Поэтому сделано сохранение через SaveContext (jmix не пытается найти сущности)
            SaveContext saveContext = new SaveContext();
            bookTags.setBook(book);
            bookTags.setTag(tag);
            saveContext.saving(bookTags);

            dataManager.save(saveContext);
        }
    }

    public Map<TagScheme, List<Tag>> getBookTags(Book book) {
        FetchPlan fp = fetchPlans.builder(Tag.class)
                .addFetchPlan("_base")
                .add("scheme", "_base")
                .build();

        return dataManager.load(Tag.class)
                .query("select t from BookTags bt inner join Tag t on bt.tag = t where bt.book = :book")
                .parameter("book", book)
                .fetchPlan(fp)
                .list().stream().collect(Collectors.groupingBy(Tag::getScheme));
    }

    public static class TagInfo {

        public TagInfo(long bookAmount) {
            this.bookAmount = (int) bookAmount;
        }

        public int getBookAmount() {
            return bookAmount;
        }

        private int bookAmount;
    }
}