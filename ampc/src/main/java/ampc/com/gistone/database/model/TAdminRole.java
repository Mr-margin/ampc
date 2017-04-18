package ampc.com.gistone.database.model;

public class TAdminRole {
    private Long adminRoleId;

    private Long adminId;

    private Long roleId;

    private Object adminRoleDoc;

    public Long getAdminRoleId() {
        return adminRoleId;
    }

    public void setAdminRoleId(Long adminRoleId) {
        this.adminRoleId = adminRoleId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Object getAdminRoleDoc() {
        return adminRoleDoc;
    }

    public void setAdminRoleDoc(Object adminRoleDoc) {
        this.adminRoleDoc = adminRoleDoc;
    }
}