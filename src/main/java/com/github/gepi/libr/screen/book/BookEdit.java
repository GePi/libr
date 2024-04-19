package com.github.gepi.libr.screen.book;

import com.github.gepi.libr.app.*;
import com.github.gepi.libr.app.event.BookTagsEditorTagListChangedEvent;
import com.github.gepi.libr.entity.*;
import com.github.gepi.libr.screen.tag.TagBrowse;
import io.jmix.core.Messages;
import io.jmix.core.Metadata;
import io.jmix.core.ValueLoadContext;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.action.BaseAction;
import io.jmix.ui.component.*;
import io.jmix.ui.download.Downloader;
import io.jmix.ui.model.*;
import io.jmix.ui.screen.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UiController("Book.edit")
@UiDescriptor("book-edit.xml")
@EditedEntityContainer("bookDc")
public class BookEdit extends StandardEditor<Book> {
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private CollectionContainer<BookAuthors> bookAuthorDc;
    @Autowired
    private Metadata metadata;
    @Autowired
    private Table<BookAuthors> authorsTable;
    @Autowired
    private FileUploadField fileRu;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private FileUploadField fileEn;
    private boolean deleteFileRuFlag;
    private boolean deleteFileEnFlag;
    @Autowired
    private LinkButton downloadFileRu;
    @Autowired
    private LinkButton downloadFileEn;
    @Autowired
    private Downloader downloader;
    @Autowired
    private CollectionLoader<TagScheme> schemeLoader;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private EntitySuggestionField<TagScheme> schemeField;
    @Autowired
    private CollectionContainer<TagScheme> schemeDc;
    @Autowired
    private CollectionContainer<Tag> tagDc;
    @Autowired
    private TagHierarchyService tagHierarchyService;
    @Autowired
    private TagPicker<Tag> tagsPicker;
    @Autowired
    private DataContext dataContext;
    @Autowired
    private CollectionContainer<BookTags> bookTagsDc;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private KeyValueCollectionLoader infoSchemeTagsLoader;
    @Autowired
    private Table<Object> infoSchemeTags;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Messages messages;

    @Autowired
    private BookTagsEditorHelperFactory bookTagsEditorHelperFactory;
    private BookTagsEditorHelper bookTagsEditorHelper;

    @Subscribe
    public void onInit(final InitEvent event) {
        setGenerator4TagListColumn();
    }

    private void createTagEditorHelper() {
        bookTagsEditorHelper = bookTagsEditorHelperFactory.create(getEditedEntity());
        bookTagsEditorHelper.loadTagsFromAllAssignedSchemas();
    }

    private void setGenerator4TagListColumn() {
        Table.Column<Object> tagList = infoSchemeTags.getColumn("tagList");
        tagList.setColumnGenerator(o ->
                {
                    if (o instanceof KeyValueEntity keyValueEntity) {
                        return fillSchemeWithTagInfoField(keyValueEntity);
                    } else {
                        return dummySchemeWithTagInfoField();
                    }
                }
        );
    }

    private Component dummySchemeWithTagInfoField() {
        ParameterizedTypeReference<Label<String>> parameterizedTypeReference =
                new ParameterizedTypeReference<>() {
                };
        Label<String> label = uiComponents.create(parameterizedTypeReference);
        label.setValue(
                messages.getMessage(getClass(), "dummySchemeWithTagInfoField"));
        return label;
    }

    private Component fillSchemeWithTagInfoField(KeyValueEntity keyValueEntity) {

        TagScheme scheme = keyValueEntity.getValue("scheme");
        List<Tag> tags = keyValueEntity.getValue("tagList");

        VBoxLayout vBoxLayout = uiComponents.create(VBoxLayout.class);

        LinkButton linkButton = uiComponents.create(LinkButton.class);
        linkButton.setCaption(scheme.getTitle());
        linkButton.addClickListener(event -> setScheme(scheme));
        linkButton.setWidthAuto();
        vBoxLayout.add(linkButton);

        var htmlBox = uiComponents.create(HtmlBoxLayout.class);
        String htmlTemplate = "<div style='margin-top: 3px; margin-left: 3px; margin-down: 5px; word-wrap: break-word; white-space: normal;'>" +
                "[body]</div>";
        htmlBox.setFrame(vBoxLayout.getFrame());
        htmlBox.setTemplateContents(
                htmlTemplate.replace("[body]",
                        tags.stream().map(Tag::getTitle).collect(Collectors.joining(", "))
                ));
        htmlBox.setWidthAuto();
        htmlBox.setHeightAuto();

        vBoxLayout.add(htmlBox);
        vBoxLayout.setWidth("320px");
        return vBoxLayout;
    }

    private void prepareSchemeField() {
        schemeLoader.setParameter("owner1", (User) currentAuthentication.getUser());
        schemeLoader.load();
        schemeField.setSearchExecutor((searchString, searchParamsMap) ->
                schemeDc.getItems().stream().filter(scheme ->
                        StringUtils.containsIgnoreCase(scheme.getTitle(), searchString)).toList());
        schemeDc.getItems().stream().filter(TagScheme::getDef).findFirst().ifPresent(this::setScheme);
    }

    @Install(to = "infoSchemeTagsLoader", target = Target.DATA_LOADER)
    private List<KeyValueEntity> infoSchemeTagsLoaderLoadDelegate(final ValueLoadContext valueLoadContext) {
        return bookTagsEditorHelper2infoSchemeTags();
    }

    private List<KeyValueEntity> bookTagsEditorHelper2infoSchemeTags() {
        List<KeyValueEntity> keyValueEntities = new ArrayList<>();
        Map<TagScheme, BookTagsEditorHelper.TagsType> schemeTags = bookTagsEditorHelper.getTags(true);

        for (var scheme : schemeTags.entrySet()) {
            KeyValueEntity keyValueEntity = new KeyValueEntity();
            keyValueEntity.setValue("scheme", scheme.getKey());
            BookTagsEditorHelper.TagsType value = scheme.getValue();
            keyValueEntity.setValue("tagList", value.selectedTags());
            keyValueEntities.add(keyValueEntity);
        }
        return keyValueEntities;
    }

    private void setScheme(TagScheme scheme) {
        schemeField.setValue(scheme);
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        createTagEditorHelper();
        prepareSchemeField();
        prepareFileFields();
    }

    private void prepareFileFields() {
        deleteFileEnFlag = false;
        deleteFileRuFlag = false;
        if (getEditedEntity().getFileNameRu() == null || getEditedEntity().getFileNameRu().isEmpty()) {
            downloadFileRu.setEnabled(false);
        } else {
            downloadFileRu.setCaption("Скачать файл " + formShortFileName(getEditedEntity().getFileNameRu()));

            final Long entityId = getEditedEntity().getId();
            final String fileNameRu = getEditedEntity().getFileNameRu();
            downloadFileRu.setAction(new BaseAction("downloadRu") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    downloadFileForEntity(entityId, fileNameRu, BookLang.RU);
                }
            });
        }
        if (getEditedEntity().getFileNameEn() == null || getEditedEntity().getFileNameEn().isEmpty()) {
            downloadFileEn.setEnabled(false);
        } else {
            final Long entityId = getEditedEntity().getId();
            final String fileNameEn = getEditedEntity().getFileNameEn();
            downloadFileEn.setAction(new BaseAction("downloadEn") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    downloadFileForEntity(entityId, fileNameEn, BookLang.EN);
                }
            });

            downloadFileEn.setCaption("Скачать файл " + formShortFileName(getEditedEntity().getFileNameEn()));
        }
    }

    private String formShortFileName(String fileName) {
        return fileName.substring(0, Math.min(fileName.length(), 80));
    }

    @Subscribe("fileRu")
    public void onFileRuFileUploadSucceed(final SingleFileUploadField.FileUploadSucceedEvent event) {
        getEditedEntity().setFileNameRu(event.getFileName());
    }

    @Subscribe("fileEn")
    public void onFileEnFileUploadSucceed(final SingleFileUploadField.FileUploadSucceedEvent event) {
        getEditedEntity().setFileNameEn(event.getFileName());
    }

    @Subscribe
    public void onBeforeCommitChanges(final BeforeCommitChangesEvent event) {
        getEditedEntity().setOwner(getEditedEntity().getOwner() != null ? getEditedEntity().getOwner() : (User) currentAuthentication.getUser());
        processTagAssignment();
    }

    private void processTagAssignment() {
        List<BookTags> bookTagsOriginal = bookTagsDc.getMutableItems();
        List<Tag> selectedTags = bookTagsEditorHelper.getTags();

        List<BookTags> removingBookTags = bookTagsOriginal.stream()
                .filter(bookTags -> !selectedTags.removeIf(tag -> tag.equals(bookTags.getTag())))
                .toList();
        bookTagsOriginal.removeAll(removingBookTags);
        removingBookTags.forEach(bookTags -> dataContext.remove(bookTags));

        for (var selectedTag : selectedTags) {
            BookTags bookTags = dataContext.create(BookTags.class);
            bookTags.setBook(getEditedEntity());
            bookTags.setTag(selectedTag);
        }
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    public void onPreCommit(final DataContext.PreCommitEvent event) {

    }

    @Subscribe
    public void onAfterCommitChanges(final AfterCommitChangesEvent event) {
        if (!fileRu.isEmpty()) {
            fileStorageService.save(fileRu.getValue(), fileRu.getFileName(), getEditedEntity().getId().toString(), BookLang.RU);
        } else {
            if (deleteFileRuFlag) {
                fileStorageService.delete(getEditedEntity().getId().toString(), BookLang.RU);
            }
        }
        if (!fileEn.isEmpty()) {
            fileStorageService.save(fileEn.getValue(), fileEn.getFileName(), getEditedEntity().getId().toString(), BookLang.EN);
        } else {
            if (deleteFileEnFlag) {
                fileStorageService.delete(getEditedEntity().getId().toString(), BookLang.EN);
            }
        }
    }

    @Subscribe("authorsTable.addLine")
    public void onAuthorsTableAddLine(final Action.ActionPerformedEvent event) {
        screenBuilders.lookup(Author.class, this)
                .withSelectHandler(authors -> {
                    authors.forEach(author -> {
                        if (bookAuthorDc.getItems().stream().map(BookAuthors::getAuthor).anyMatch(author::equals)) {
                            return;
                        }
                        BookAuthors bookAuthors = metadata.create(BookAuthors.class);
                        BookAuthorsId bookAuthorsId = new BookAuthorsId();
                        bookAuthorsId.setAuthorId(author.getId());
                        bookAuthorsId.setBookId(getEditedEntity().getId());
                        bookAuthors.setBookAuthorsId(bookAuthorsId);
                        bookAuthors.setAuthor(author);
                        bookAuthors.setBook(getEditedEntity());

                        bookAuthors = dataContext.merge(bookAuthors);
                        bookAuthorDc.getMutableItems().add(bookAuthors);
                    });
                }).build()
                .show();
    }

    @Subscribe("authorsTable.removeLine")
    public void onAuthorsTableRemoveLine(final Action.ActionPerformedEvent event) {
        BookAuthors bookAuthors = authorsTable.getSingleSelected();
        if (bookAuthors == null) {
            return;
        }
        bookAuthorDc.getMutableItems().remove(bookAuthors);
        dataContext.remove(bookAuthors);
    }

    @Subscribe("deleteFileEn")
    public void onDeleteFileEnClick(final Button.ClickEvent event) {
        deleteFileEnFlag = true;
        fileEn.clear();
        getEditedEntity().setFileNameEn("");
    }

    @Subscribe("deleteFileRu")
    public void onDeleteFileRuClick(final Button.ClickEvent event) {
        deleteFileRuFlag = true;
        fileRu.clear();
        getEditedEntity().setFileNameRu("");
    }

    private void downloadFileForEntity(Long id, String fileName, BookLang bookLang) {
        try (InputStream loaded = fileStorageService.load(id.toString(), bookLang)) {
            downloader.download(loaded.readAllBytes(), fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe(id = "schemeDc", target = Target.DATA_CONTAINER)
    public void onSchemeDcItemChange(final InstanceContainer.ItemChangeEvent<TagScheme> event) {
        setTagsOnScheme(event.getItem());
    }

    @Subscribe(id = "schemeDc", target = Target.DATA_CONTAINER)
    public void onSchemeDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<TagScheme> event) {
        setTagsOnScheme(event.getItem());
    }

    @Subscribe("schemeField")
    public void onSchemeFieldValueChange(final HasValue.ValueChangeEvent<TagScheme> event) {
        setTagsOnScheme(event.getValue());
    }

    @Subscribe("tagsPicker")
    public void onTagsPickerValueChange(final HasValue.ValueChangeEvent<Collection<Tag>> event) {
        saveTagsTmpDepot(schemeField.getValue());
    }

    private void setTagsOnScheme(TagScheme scheme) {
        if (scheme == null) {
            return;
        }
        BookTagsEditorHelper.TagsType tags = bookTagsEditorHelper.getTags(scheme);
        tagDc.setItems(tags.allTags());
        tagsPicker.setValue(tags.selectedTags());
    }

    private void saveTagsTmpDepot(TagScheme scheme) {
        if (scheme == null) {
            return;
        }
        bookTagsEditorHelper.setSelectedTags(scheme, tagsPicker.getValue().stream().toList());
    }

    @Install(to = "tagsPicker.lookup", subject = "screenConfigurer")
    private void tagsPickerLookupScreenConfigurer(final Screen screen) {
        if (screen instanceof TagBrowse tagBrowseScreen && schemeField.getValue() != null) {
            tagBrowseScreen.setTagsTreeDTO(tagHierarchyService.loadTagTree(schemeField.getValue()).getTreeDTO());
            tagBrowseScreen.setSelectionMode(Tree.SelectionMode.MULTI);
            tagBrowseScreen.setCaption(messageBundle.getMessage("bookEdit.tagSelectionScreenCaption"));

        }
    }

    @Install(to = "tagsPicker.lookup", subject = "transformation")
    private Collection<Tag> tagsPickerLookupTransformation(final Collection<TagTreeDTO> tagTreeDTOs) {
        return tagTreeDTOs.stream().map(TagTreeDTO::getTag).collect(Collectors.toList());
    }

    @EventListener
    public void bookTagsEditorTagListChanged(BookTagsEditorTagListChangedEvent event) {
        infoSchemeTagsLoader.load();
    }
}