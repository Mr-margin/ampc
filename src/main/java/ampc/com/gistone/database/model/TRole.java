package ampc.com.gistone.database.model;

public class TRole {
    private Long roleId;

    private Object roleName;

    private Object roleDoc;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Object getRoleName() {
        return roleName;
    }

    public void setRoleName(Object roleName) {
        this.roleName = roleName;
    }

    public Object getRoleDoc() {
        return roleDoc;
    }

    public void setRoleDoc(Object roleDoc) {
        this.roleDoc = roleDoc;
    }
}