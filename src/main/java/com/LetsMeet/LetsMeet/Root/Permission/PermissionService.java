package com.LetsMeet.LetsMeet.Root.Permission;

import java.util.UUID;

import com.LetsMeet.LetsMeet.Root.Permission.Model.Permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    @Autowired
    PermissionDAO dao;
    
    public void setPermission(Permission permission) throws IllegalArgumentException{
        try{
            dao.save(permission);
        }
        catch(Exception e){
            throw new IllegalArgumentException("Permission denied");
        }
    }

    public void changePermission(Permission oldPermission, Permission newPermission){
        try{
            dao.update(newPermission);
        }
        catch(Exception e){
            throw new IllegalArgumentException("Permission denied");
        }
    }

    public boolean getPermission(UUID parent, UUID child, String requestType) throws IllegalArgumentException{
        try{
            return (dao.get(parent, child).orElseThrow().getType().code.equals(requestType));
            
        }
        catch (Exception e){
            return false;
        }
    }

    public void clearPermission(Permission oldPermission){
        try{
            dao.delete(oldPermission);
        }
        catch(Exception e){
            throw new IllegalArgumentException("Permission denied");
        }
    }
}
