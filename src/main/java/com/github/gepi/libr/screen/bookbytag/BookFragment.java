package com.github.gepi.libr.screen.bookbytag;

import com.github.gepi.libr.app.FileStorageService;
import com.github.gepi.libr.app.event.TreeTagNodeSelectedEvent;
import com.github.gepi.libr.entity.Book;
import com.github.gepi.libr.entity.BookLang;
import com.github.gepi.libr.entity.Tag;
import io.jmix.core.DataManager;
import io.jmix.ui.Actions;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.component.renderer.ComponentRendererImpl;
import io.jmix.ui.download.Downloader;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;


@UiController("BookFragment")
@UiDescriptor("book-fragment.xml")
public class BookFragment extends ScreenFragment {

    @Autowired
    private Filter bookFilter;
    @Autowired
    private CollectionLoader<Book> booksDl;
    @Autowired
    private UiComponents uiComponents;

    private Supplier<List<Tag>> tagFilterSupplier;
    @Autowired
    private DataManager dataManager;

    private JpqlFilter<Book> tagsJpqlFilter;
    @Autowired
    private Actions actions;
    @Autowired
    private Button editBtn;
    @Autowired
    private DataGrid<Book> booksTable;
    @Autowired
    private Notifications notifications;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private Downloader downloader;


    @Subscribe
    public void onInit(final InitEvent event) {
        createTagFilter();
        addTagFilterToCurrentConfiguration();
        createLinksFieldGenerator();
    }

    @EventListener
    public void refreshWithFilter(TreeTagNodeSelectedEvent event) {
        List<Tag> tags;
        if (tagFilterSupplier != null) {
            tags = tagFilterSupplier.get();
            addTagFilterToCurrentConfiguration();
            applyFilter(tags);
        }
    }

    private void createLinksFieldGenerator() {
        booksTable.getColumn("downloadLinks").setColumnGenerator(
                bookColumnGeneratorEvent -> {
                    Book book = bookColumnGeneratorEvent.getItem();

                    HBoxLayout layout = uiComponents.create(HBoxLayout.class);
                    layout.setSpacing(true);
                    layout.setHeightFull();
                    layout.setAlignment(Component.Alignment.MIDDLE_LEFT);

                    if (book.getFileNameRu() != null && !book.getFileNameRu().isEmpty()) {
                        LinkButton linkButtonRu = uiComponents.create(LinkButton.class);
                        linkButtonRu.setCaption("Скачать Ru");
                        linkButtonRu.setDescription(book.getFileNameRu());
                        linkButtonRu.setWidthFull();
                        linkButtonRu.setHeightFull();
                        linkButtonRu.addClickListener(clickEvent -> {
                            notifications.create(Notifications.NotificationType.HUMANIZED).withCaption("Скачивается " + book.getFileNameRu()).show();
                            try (InputStream loaded = fileStorageService.load(book.getId().toString(), BookLang.RU)) {
                                downloader.download(loaded.readAllBytes(), book.getFileNameRu());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        layout.add(linkButtonRu);
                    }

                    if (book.getFileNameEn() != null && !book.getFileNameEn().isEmpty()) {

                        LinkButton linkButtonEn = uiComponents.create(LinkButton.class);
                        linkButtonEn.setCaption("Скачать En");
                        linkButtonEn.setDescription(book.getFileNameEn());
                        linkButtonEn.setHeightFull();
                        linkButtonEn.addClickListener(clickEvent -> {
                            notifications.create(Notifications.NotificationType.HUMANIZED).withCaption("Скачивается " + book.getFileNameEn()).show();
                            try (InputStream loaded = fileStorageService.load(book.getId().toString(), BookLang.EN)) {
                                downloader.download(loaded.readAllBytes(), book.getFileNameEn());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        layout.add(linkButtonEn);
                    }
                    return layout;
                }
        );
        ComponentRendererImpl<Object> renderer = new ComponentRendererImpl<>();

        booksTable.getColumn("downloadLinks").setRenderer(renderer);
    }


    private void applyFilter(List<Tag> tags) {
        Map<String, Object> parameterValuesMap = tagsJpqlFilter.getQueryCondition().getParameterValuesMap();
        parameterValuesMap.put("tags", tags);
        tagsJpqlFilter.apply();
    }

    private void createTagFilter() {
        tagsJpqlFilter = uiComponents.create(JpqlFilter.NAME);
        tagsJpqlFilter.setId("tagFilter");
        tagsJpqlFilter.setDataLoader(bookFilter.getDataLoader());
        tagsJpqlFilter.setCondition("bt.tag.id IN ?", "inner join BookTags bt on bt.book = e");
        tagsJpqlFilter.setParameterClass(Tag.class);
        tagsJpqlFilter.setParameterName("tags");
        tagsJpqlFilter.setHasInExpression(true);
        tagsJpqlFilter.setVisible(false);
    }

    private void addTagFilterToCurrentConfiguration() {
        Filter.Configuration currentConfiguration = bookFilter.getCurrentConfiguration();
        LogicalFilterComponent rootLogicalFilterComponent = currentConfiguration.getRootLogicalFilterComponent();

        for (var filterComponent : rootLogicalFilterComponent.getOwnFilterComponents()) {
            if (Objects.equals(filterComponent.getId(), "tagFilter")) {
                return;
            }
        }
        currentConfiguration.getRootLogicalFilterComponent().add(tagsJpqlFilter);
        bookFilter.setCurrentConfiguration(currentConfiguration);
    }

    private void delTagFilterFromCurrentConfiguration() {
        Filter.Configuration currentConfiguration = bookFilter.getCurrentConfiguration();
        LogicalFilterComponent rootLogicalFilterComponent = currentConfiguration.getRootLogicalFilterComponent();
        rootLogicalFilterComponent.getOwnFilterComponents().stream()
                .filter(filterComponent -> "tagFilter".equals(filterComponent.getId()))
                .findFirst()
                .ifPresent(filterComponent -> {
                    rootLogicalFilterComponent.remove(filterComponent);
                    bookFilter.setCurrentConfiguration(currentConfiguration);
                    bookFilter.apply();
                });
    }

    public void setTagFilterSupplier(Supplier<List<Tag>> supplier) {
        tagFilterSupplier = supplier;
    }

    @Subscribe("bookFilter.resetFilterBtn")
    public void onBookFilterResetFilterBtn(final Action.ActionPerformedEvent event) {
        delTagFilterFromCurrentConfiguration();
        bookFilter.refreshCurrentConfigurationLayout();
    }

    @Subscribe("allBook")
    public void onAllBookClick(final Button.ClickEvent event) {
        delTagFilterFromCurrentConfiguration();
        bookFilter.refreshCurrentConfigurationLayout();
    }
}