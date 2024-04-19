package com.github.gepi.libr.screen.author;

import com.github.gepi.libr.entity.Author;
import com.github.gepi.libr.repository.BookAuthorsRepository;
import io.jmix.core.DataManager;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("Author.browse")
@UiDescriptor("author-browse.xml")
@LookupComponent("authorsTable")
public class AuthorBrowse extends StandardLookup<Author> {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private GroupTable<Author> authorsTable;
    @Autowired
    private BookAuthorsRepository bookAuthorsRepository;

    @Install(to = "authorsTable.remove", subject = "enabledRule")
    private boolean authorsTableRemoveEnabledRule() {
        if (authorsTable.getSingleSelected() == null) {
            return false;
        }
        if (bookAuthorsRepository.findByAuthor(authorsTable.getSingleSelected()).isEmpty()) {
            return true;
        }
        return false;
    }
}