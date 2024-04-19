package com.github.gepi.libr.screen.bookbytag;

import com.github.gepi.libr.app.BookTagsExtraColumns;
import com.github.gepi.libr.app.BookTagsService;
import com.github.gepi.libr.app.TagHierarchyService;
import com.github.gepi.libr.app.TagTree;
import com.github.gepi.libr.app.event.AppEventListener;
import com.github.gepi.libr.app.event.AppEventPublisher;
import com.github.gepi.libr.app.event.BookTagsChangeSaveEvent;
import com.github.gepi.libr.app.event.TreeTagNodeSelectedEvent;
import com.github.gepi.libr.entity.*;
import com.github.gepi.libr.screen.tag.TagEditorScreen;
import com.github.gepi.libr.screen.tagscheme.TagSchemeEdit;
import com.vaadin.shared.ui.dnd.DragSourceState;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.TreeGridDragSource;
import com.vaadin.ui.components.grid.TreeGridDropTarget;
import io.jmix.core.DataManager;
import io.jmix.core.Metadata;
import io.jmix.core.common.util.ParamsMap;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.UiEventPublisher;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.*;
import java.util.stream.Collectors;

@UiController("BookTags")
@UiDescriptor("book-tags.xml")
public class BookTags extends Screen implements AppEventListener {
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private CurrentAuthentication currentAuthentication;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CollectionContainer<TagScheme> tagSchemesDc;
    @Autowired
    private CollectionLoader<TagScheme> tagSchemesDl;
    @Autowired
    private EntityComboBox<TagScheme> cbSchemeId;

    private static final Logger log = LoggerFactory.getLogger(BookTags.class);
    @Autowired
    private CollectionContainer<TagTreeDTO> tagTreeDTODc;
    @Autowired
    private TagHierarchyService tagHierarchyService;
    @Autowired
    private Notifications notifications;
    @Autowired
    private TreeDataGrid<TagTreeDTO> tagTreeDataGrid;
    @Autowired
    private BookFragment bookFragment;
    @Autowired
    private UiEventPublisher uiEventPublisher;
    @Autowired
    private Metadata metadata;

    @Autowired
    private BookTagsService bookTagsService;

    private TagTree tagTree;
    private BookTagsExtraColumns bookTagsExtraColumns;

    @Autowired
    private AppEventPublisher bookTagsEntityEventListener;
    private boolean mode;
    @Autowired
    private Button modeBtn;

    @Subscribe
    public void onInit(final InitEvent initEvent) {
        addTreeColumns();
        initDragNDropFacilities();
        subscribe();
    }

    @Subscribe
    public void onAfterDetach(final AfterDetachEvent event) {
        bookTagsEntityEventListener.unSubscribe(this);
    }

    private void subscribe() {
        bookTagsEntityEventListener.subscribe(this);
    }

    @Override
    public void publish(ApplicationEvent event) {
        if (event instanceof BookTagsChangeSaveEvent) {
            recalcTagTree(cbSchemeId.getValue(), false);
        }

    }

    private void addTreeColumns() {
        bookTagsExtraColumns = new BookTagsExtraColumns(bookTagsService);

        var colsGenerator = bookTagsExtraColumns.new GridGenerator();
        colsGenerator.addColumn(tagTreeDataGrid, BookTagsExtraColumns.Enum.BOOKS);
        colsGenerator.addColumn(tagTreeDataGrid, BookTagsExtraColumns.Enum.ALL_CHILDREN_BOOKS);

        tagTreeDataGrid.setCaptionAsHtml(true);
        colsGenerator.setHeader(BookTagsExtraColumns.Enum.BOOKS, tagTreeDataGrid.getDefaultHeaderRow());
        colsGenerator.setHeader(BookTagsExtraColumns.Enum.ALL_CHILDREN_BOOKS, tagTreeDataGrid.getDefaultHeaderRow());
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        tagSchemesDl.setParameter("owner", (User) currentAuthentication.getUser());
        tagSchemesDl.load();
        // выбор схемы по умолчанию
        tagSchemesDc.getItems().stream()
                .filter(TagScheme::getDef)
                .findFirst()
                .ifPresent(
                        scheme -> {
                            cbSchemeId.setValue(scheme);
                            reloadTagTree(scheme);
                        }
                );
        setTagsTreeFilterModeToggle();
        bookFragment.setTagFilterSupplier(this::getSelectedTags);
    }

    private void setTagsTreeFilterModeToggle() {
        mode = !mode;
        if (mode) {
            modeBtn.setIcon("icons/expanded-folder-bg.png");
        } else {
            modeBtn.setIcon("icons/collapsed-folder-bg.png");
        }
    }

    @Subscribe("modeBtn")
    public void onModeBtnClick(final Button.ClickEvent event) {
        setTagsTreeFilterModeToggle();
        uiEventPublisher.publishEvent(new TreeTagNodeSelectedEvent(this));
    }

    private List<Tag> getSelectedTags() {
        List<Tag> tags = tagTreeDataGrid.getSelected().stream().map(TagTreeDTO::getTag).collect(Collectors.toList());
        if (mode && tagTree != null) {
            tags = tagTree.getAllChildren(tags);
        }
        return tags;
    }

    @Subscribe("SchemeEditScreen")
    public void onSchemeEditScreen(final Action.ActionPerformedEvent event) {
        TagScheme scheme = cbSchemeId.getValue();
        if (scheme == null) {
            return;
        }
        Screen schemeScreen = screenBuilders.editor(TagScheme.class, this)
                .editEntity(scheme)
                .build();
        schemeScreen.addAfterCloseListener(afterCloseEvent -> {
            if (afterCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                tagSchemesDl.load();
            }
        });
        schemeScreen.show();
    }

    @Subscribe("SchemeNewScreen")
    public void onSchemeNewScreen(final Action.ActionPerformedEvent event) {
        TagScheme scheme = dataManager.create(TagScheme.class);
        scheme.setOwner((User) currentAuthentication.getUser());
        Screen schemeScreen = screenBuilders.editor(TagScheme.class, this)
                .newEntity(scheme)
                .build();
        schemeScreen.addAfterCloseListener(afterCloseEvent -> {
            if (afterCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                tagSchemesDl.load();
                TagSchemeEdit source = (TagSchemeEdit) afterCloseEvent.getSource();
                cbSchemeId.setValue(tagSchemesDc.getItem(source.getEditedEntity().getId()));
            }
        });

        schemeScreen.show();
    }

    @Subscribe("SchemeDelete")
    public void onSchemeDelete(final Action.ActionPerformedEvent event) {
        TagScheme scheme = cbSchemeId.getValue();
        if (scheme == null) {
            return;
        }
        try {
            dataManager.remove(scheme);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("onSchemeDelete optimistic lock: " + e);
            return;
        }
        cbSchemeId.clear();
        tagSchemesDl.load();
    }

    @Subscribe("cbSchemeId")
    public void onCbSchemeIdValueChange(final HasValue.ValueChangeEvent<TagScheme> event) {
        TagScheme scheme = event.getValue();
        reloadTagTree(scheme);
    }

    @Subscribe("tagTreeDataGrid.addTagAction")
    public void onTagTreeDataGridAddTagAction(final Action.ActionPerformedEvent event) {
        TagTreeDTO selectedNode = getSelectedNode(false);
        Tag parentTag = selectedNode == null ? null : selectedNode.getTag();
        TagScheme scheme = cbSchemeId.getValue();
        if (scheme != null) {
            showTagEditScreen(scheme, null, parentTag);
        }
    }

    @Subscribe("tagTreeDataGrid.editTagAction")
    public void onTagTreeDataGridEditTagAction(final Action.ActionPerformedEvent event) {
        TagTreeDTO selectedNode = getSelectedNode(true);
        if (selectedNode == null) {
            return;
        }
        Tag selectedTag = selectedNode.getTag();
        Tag parentTag = selectedNode.getParent() == null ? null : selectedNode.getParent().getTag();
        TagScheme scheme = cbSchemeId.getValue();
        showTagEditScreen(scheme, selectedTag, parentTag);
    }

    private TagTreeDTO getSelectedNode(boolean obligatory) {
        TagTreeDTO selectedNode = null;

        TagScheme scheme = cbSchemeId.getValue();
        if (scheme == null) {
            notifications.create().withCaption("Выберите схему").show();
            return null;
        }
        selectedNode = tagTreeDataGrid.getSingleSelected();
        if (selectedNode == null && obligatory) {
            notifications.create().withCaption("Выберите тег").show();
            return null;
        }
        return selectedNode;
    }

    private void showTagEditScreen(TagScheme scheme, Tag tag, Tag parentTag) {
        TagEditorScreen editorScreen = screenBuilders.screen(this)
                .withScreenClass(TagEditorScreen.class)
                .withOptions(new MapScreenOptions(ParamsMap.of("scheme", scheme, "tag", tag, "parentTag", parentTag)))
                .withAfterCloseListener(
                        event -> {
                            if (event.closedWith(StandardOutcome.COMMIT)) {
                                reloadTagTree(scheme);
                                setSelectedTag(event.getSource().getTagResult());
                            }
                        })
                .build();
        editorScreen.show();
    }

    private void setSelectedTag(Tag tagResult) {
        tagTreeDataGrid.getItems().getItems()
                .filter(tagTreeDTO -> tagResult.getId().equals(tagTreeDTO.getId()))
                .findFirst()
                .ifPresent(tagTreeDTO -> {
                    expandAllParent(tagTreeDTO);
                    tagTreeDataGrid.setSelected(tagTreeDTO);
                });
    }

    private void reloadTagTree(TagScheme scheme) {
        if (scheme == null) {
            tagTreeDTODc.setItems(new ArrayList<>());
            bookTagsExtraColumns.clear();
            return;
        }
        tagTree = tagHierarchyService.loadTagTree(scheme);
        List<TagTreeDTO> expanded = getExpandedNodes(tagTreeDataGrid);
        bookTagsExtraColumns.fillExtraTreeFieldsMap(scheme, tagTree, true);
        tagTreeDTODc.setItems(tagTree.getTreeDTO());
        setExpandedNodes(tagTreeDataGrid, expanded);
    }

    private void setExpandedNodes(TreeDataGrid<TagTreeDTO> tagTreeDataGrid, List<TagTreeDTO> expanded) {
        expanded.forEach(tagTreeDataGrid::expand);
    }

    private List<TagTreeDTO> getExpandedNodes(TreeDataGrid<TagTreeDTO> tagTreeDataGrid) {
        return tagTreeDataGrid.getItems().getItems().filter(tagTreeDataGrid::isExpanded).toList();
    }


    private void recalcTagTree(TagScheme scheme, boolean joinTransation) {
        if (scheme == null) {
            return;
        }
        List<TagTreeDTO> expanded = getExpandedNodes(tagTreeDataGrid);
        bookTagsExtraColumns.fillExtraTreeFieldsMap(scheme, tagTree, joinTransation);
        tagTreeDataGrid.setItems(tagTreeDataGrid.getItems());
        setExpandedNodes(tagTreeDataGrid, expanded);
    }

    private void expandAllParent(TagTreeDTO selectedNode) {
        List<TagTreeDTO> allParents = new ArrayList<>();
        TagTreeDTO parent = selectedNode;
        while ((parent = parent.getParent()) != null) {
            allParents.add(parent);
        }
        if (!allParents.isEmpty()) {
            tagTreeDataGrid.expand(allParents);
        }
    }

    @Subscribe("tagTreeDataGrid.delTagAction")
    public void onTagTreeDataGridDelTagAction(final Action.ActionPerformedEvent event) {
        TagScheme scheme = cbSchemeId.getValue();
        TagTreeDTO tagTreeDTO = tagTreeDataGrid.getSingleSelected();
        if (scheme == null || tagTreeDTO == null) {
            notifications.create().withCaption("Выберите тег").show();
            return;
        }
        tagHierarchyService.removeTag(tagTreeDTO.getTag(), scheme);
        reloadTagTree(scheme);
    }


    @Subscribe("tagTreeDataGrid")
    public void onTreeTagDataGridSelection(final DataGrid.SelectionEvent<TagTreeDTO> event) {
        uiEventPublisher.publishEvent(new TreeTagNodeSelectedEvent(this));
    }

    private void initDragNDropFacilities() {
        treeNodesRearrangeDnD();
        bookGridItemToTreeDnD();
    }

    @SuppressWarnings("unchecked")
    private void bookGridItemToTreeDnD() {
        final String actionKey = "Book";
        DataGrid<Book> booksTable = (DataGrid<Book>) bookFragment.getFragment().getComponent("booksTable");
        var vaadinGridSource = Objects.requireNonNull(booksTable).unwrap(Grid.class);
        var dragSource = new GridDragSource<Book>(vaadinGridSource);
        dragSource.setEffectAllowed(EffectAllowed.LINK);
        dragSource.setDragDataGenerator(DragSourceState.DATA_TYPE_TEXT_PLAIN,
                item -> String.join(":", actionKey, item.getId().toString()));

        var vaadinTreeGridTarget = tagTreeDataGrid.unwrap(TreeGrid.class);
        var dropTarget = new TreeGridDropTarget<TagTreeDTO>(vaadinTreeGridTarget, DropMode.ON_TOP);

        dropTarget.addTreeGridDropListener(event -> {
            Optional<String> dataTransferData = event.getDataTransferData(DragSourceState.DATA_TYPE_TEXT_PLAIN);
            Optional<TagTreeDTO> dropTargetRow = event.getDropTargetRow();
            if (dropTargetRow.isEmpty() || dataTransferData.isEmpty()) {
                return;
            }
            String[] split = dataTransferData.get().split(":");
            if (split.length != 2 || !split[0].equals(actionKey)) {
                return;
            }
            Long bookId = Long.valueOf(split[1]);
            Book book = Objects.requireNonNull(booksTable.getItems()).getItem(bookId);
            Tag tag = dropTargetRow.get().getTag();
            if (book != null && tag != null) {
                bookTagsService.assignTag(book, tag);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void treeNodesRearrangeDnD() {
        final String actionKey = "TreeNode";
        var vaadinTreeGridSource = tagTreeDataGrid.unwrap(TreeGrid.class);
        var dragSource = new TreeGridDragSource<TagTreeDTO>(vaadinTreeGridSource);
        dragSource.setEffectAllowed(EffectAllowed.LINK);
        dragSource.setDragDataGenerator(DragSourceState.DATA_TYPE_TEXT_PLAIN,
                item -> String.join(":", actionKey, item.getId().toString()));

        var vaadinTreeGridTarget = tagTreeDataGrid.unwrap(TreeGrid.class);
        var dropTarget = new TreeGridDropTarget<TagTreeDTO>(vaadinTreeGridTarget, DropMode.ON_TOP);

        dropTarget.addTreeGridDropListener(event -> {
            TagScheme scheme = cbSchemeId.getValue();
            if (scheme == null) {
                return;
            }
            Optional<String> dataTransferData = event.getDataTransferData(DragSourceState.DATA_TYPE_TEXT_PLAIN);
            if (dataTransferData.isEmpty()) {
                return;
            }
            String[] split = dataTransferData.get().split(":");
            if (split.length != 2 || !split[0].equals(actionKey)) {
                return;
            }
            Long tagId = Long.valueOf(split[1]);
            TagTreeDTO dragItem = tagTreeDataGrid.getItems().getItem(tagId);
            if (dragItem == null) {
                return;
            }
            TagHierarchy hierarchy = metadata.create(TagHierarchy.class);
            hierarchy.setScheme(scheme);
            hierarchy.setTag(dragItem.getTag());

            if (event.getDropTargetRow().isPresent()) {
                hierarchy.setParent(event.getDropTargetRow().get().getTag());
            } else {
                hierarchy.setParent(dragItem.getTag());
            }
            tagHierarchyService.saveHierarchy(hierarchy);
            reloadTagTree(scheme);

        });
    }
}
