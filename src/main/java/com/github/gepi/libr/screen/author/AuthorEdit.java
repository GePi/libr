package com.github.gepi.libr.screen.author;

import io.jmix.ui.screen.*;
import com.github.gepi.libr.entity.Author;

@UiController("Author.edit")
@UiDescriptor("author-edit.xml")
@EditedEntityContainer("authorDc")
public class AuthorEdit extends StandardEditor<Author> {

}