package com.swak.vertx.transport;

import com.swak.utils.Sets;

import java.io.Serializable;
import java.util.Set;

/**
 * 得到相关的权限
 *
 * @author: lifeng
 * @date: 2020/3/29 21:14
 */
public class AuthorizationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<String> permissions = Sets.newHashSet();
    private Set<String> roles = Sets.newHashSet();

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void addPermission(String permission) {
        this.permissions.add(permission);
    }

    public void addRole(String role) {
        this.roles.add(role);
    }
}