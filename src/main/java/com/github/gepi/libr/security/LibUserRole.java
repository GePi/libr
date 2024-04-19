package com.github.gepi.libr.security;

import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityui.role.annotation.MenuPolicy;
import io.jmix.securityui.role.annotation.ScreenPolicy;

@ResourceRole(name = "LibUser", code = LibUserRole.CODE)
public interface LibUserRole {
    String CODE = "lib-user";

    @EntityPolicy(
            entityName = "*",
            actions = {EntityPolicyAction.ALL})
    @EntityAttributePolicy(entityName = "*", attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    void allLibEntities();
    @ScreenPolicy(screenIds = "*")
    void allLibScreens();
    @MenuPolicy(menuIds = {"BookTags", "Book.browse", "Author.browse"} )
    void allLibMenu();
}
