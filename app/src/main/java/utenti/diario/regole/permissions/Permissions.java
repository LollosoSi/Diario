package utenti.diario.regole.permissions;

import java.util.ArrayList;
import java.util.Objects;

import utenti.diario.container.Container;

/**
 * Created by SosiForWork on 01/09/2017.
 */

public class Permissions {

    public void RegisterPermission(String parent, String permission) {
        PermissionInternalFragment pif = new PermissionInternalFragment();
        pif.parent = parent;
        pif.permission = permission;
        Container.getInstance().internalFragments.add(pif);
    }


    public void AddUserPermission(String parent, String permission) {

        PermissionInternalFragment pif = new PermissionInternalFragment();
        pif.parent = parent;
        pif.permission = permission;
        Container.getInstance().userFragments.add(pif);

        if (!(getParents(parent).size() > 0)) {
            Container.getInstance().parents.add(parent);
        }
    }

    // Adds all permissions registered to user
    protected void addAllPermissions() {

        ArrayList<PermissionInternalFragment> pf = Container.getInstance().internalFragments;

        for (int i = 0; i < pf.size(); i++) {
            AddUserPermission(pf.get(i).parent, pf.get(i).permission);
        }

    }

    // Adds single permission (user permissions) parses * to all parent
    protected void addPermission(ArrayList<String> perms) {
        if (perms.get(1).equals("*")) {
            addParentPermissions(perms.get(0));
        } else {
            AddUserPermission(perms.get(0), perms.get(1));
        }
    }

    // Adds all permissions of parent (user permissions)
    protected void addParentPermissions(String parent) {

        ArrayList<PermissionInternalFragment> pf = getParentsRegistered(parent);

        for (int i = 0; i < pf.size(); i++) {
            AddUserPermission(pf.get(i).parent, pf.get(i).permission);
        }

    }


    // check if parent and permission is found in list
    protected boolean hasPermission(String parent, String perm) {
        ArrayList<PermissionInternalFragment> pf = getParents(parent);
        boolean found = false;
        for (int i = 0; i < pf.size(); i++) {
            if (Objects.equals(pf.get(i).permission, perm)) {
                found = true;
            }
        }
        return found;
    }

    // Return arraylist with all pif found with requested parent on user perms
    ArrayList<PermissionInternalFragment> getParents(String parent) {

        ArrayList<PermissionInternalFragment> pf = Container.getInstance().userFragments;
        ArrayList<PermissionInternalFragment> ar = new ArrayList<>(); // Array to return

        for (int i = 0; i < pf.size(); i++) {
            if (Objects.equals(pf.get(i).parent, parent)) {
                ar.add(pf.get(i));
            }
        }

        return ar;
    }

    // Return arraylist with all pif found with requested parent
    ArrayList<PermissionInternalFragment> getParentsRegistered(String parent) {

        ArrayList<PermissionInternalFragment> pf = Container.getInstance().internalFragments;
        ArrayList<PermissionInternalFragment> ar = new ArrayList<>(); // Array to return

        for (int i = 0; i < pf.size(); i++) {
            if (Objects.equals(pf.get(i).parent, parent)) {
                ar.add(pf.get(i));
            }
        }

        return ar;
    }


}
