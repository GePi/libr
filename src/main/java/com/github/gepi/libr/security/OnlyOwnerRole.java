package com.github.gepi.libr.security;

import com.github.gepi.libr.entity.Book;
import com.github.gepi.libr.entity.Tag;
import com.github.gepi.libr.entity.TagScheme;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

@RowLevelRole(name = "OnlyOwnerRole", code = OnlyOwnerRole.CODE)
public interface OnlyOwnerRole {
    String CODE = "only-owner-role";

    @JpqlRowLevelPolicy(
            entityClass = Book.class,
            where = "{E}.owner.id = :current_user_id"
    )
    void bookOwner();
    @JpqlRowLevelPolicy(
            entityClass = TagScheme.class,
            where = "{E}.owner.id = :current_user_id"
    )
    void schemeOwner();
    @JpqlRowLevelPolicy(
            entityClass = Tag.class,
            where = "{E}.owner.id = :current_user_id"
    )
    void tagOwner();
}