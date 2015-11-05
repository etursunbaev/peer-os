package io.subutai.core.identity.api.dao;


import java.util.List;

import io.subutai.core.identity.api.model.Permission;
import io.subutai.core.identity.api.model.Role;
import io.subutai.core.identity.api.model.Session;
import io.subutai.core.identity.api.model.User;
import io.subutai.core.identity.api.model.UserToken;


/**
 *
 */
public interface IdentityDataService
{

    /* ******User *************************************
     *
     */
    User getUserByUsername( String userName );


    /* *************************************************
     *
     */
    User getUser( long userId );

    /* *************************************************
     *
     */
    void assignUserRole( long userId, Role role );


    /* ******User *************************************
     *
     */
    List<User> getAllUsers();


    /* *************************************************
     *
     */
    void persistUser( User item );


    /* *************************************************
     *
     */
    void removeUser( long id );


    /* *************************************************
     *
     */
    void updateUser( User item );


    /* ***********Roles ********************************
     */
    Role getRole( long roleId );

    /* *************************************************
     *
     */
    List<Role> getAllRoles();


    /* *************************************************
     *
     */
    void persistRole( Role item );


    /* *************************************************
     *
     */
    void removeRole( long id );


    /* *************************************************
     *
     */
    void updateRole( Role item );


    /* *************************************************
     *
     */
    void assignRolePermission( long roleId, Permission permission );


    /*
     * ******Permission*********************************
     */
    Permission getPermission( long permissionId );


    /* *************************************************
     *
     */
    List<Permission> getAllPermissions();


    /* *************************************************
     *
     */
    void persistPermission( Permission item );


    /* *************************************************
     *
     */
    void removePermission( long id );


    /* *************************************************
     *
     */
    void updatePermission( Permission item );


    /* ******Session************************
     *
     */
    List<Session> getAllSessions();


    /* *************************************************
     */
    Session getSession( long sessionId );


    /* *************************************************
     */
    Session getSessionByUserId( long userId );


    /* *************************************************
     *
     */
    void persistSession( Session item );


    /* *************************************************
     *
     */
    void removeSession( long id );


    /* *************************************************
     *
     */
    void updateSession( Session item );

    /* ******UserToken *********************************
    *
    */
    List<UserToken> getAllUserTokens();


    /* *************************************************
     *
     */
    UserToken getUserToken( String token );


    /* *************************************************
     *
     */
    UserToken getValidUserToken( String token );

    /* *************************************************
         *
         */
    UserToken getUserToken( long userId );

    /* *************************************************
     *
     */
    UserToken getValidUserToken( long userId );

    /* *************************************************
         *
         */
    void persistUserToken( UserToken item );


    /* *************************************************
     *
     */
    void removeUserToken( String token );


    /* *************************************************
     *
     */
    void updateUserToken( UserToken item );
}