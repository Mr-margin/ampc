package ampc.com.gistone.database.model;

import java.util.Date;

public class TQueryExcel {
    private Long queryId;

    private Object queryName;

    private String queryEtitle;

    private Object queryShowquery;

    private Object queryValue;

    private Object queryOptiontype;

    private Object queryOption1;

    private Object queryOption2;

    private Object queryOption3;

    private Object queryOption4;

    private Object queryOption5;

    private Object sectorName;

    private Long queryVersion;

    private Object userId;

    private Date addTime;

    private String isEffective;

    private Date deleteTime;

    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }

    public Object getQueryName() {
        return queryName;
    }

    public void setQueryName(Object queryName) {
        this.queryName = queryName;
    }

    public String getQueryEtitle() {
        return queryEtitle;
    }

    public void setQueryEtitle(String queryEtitle) {
        this.queryEtitle = queryEtitle == null ? null : queryEtitle.trim();
    }

    public Object getQueryShowquery() {
        return queryShowquery;
    }

    public void setQueryShowquery(Object queryShowquery) {
        this.queryShowquery = queryShowquery;
    }

    public Object getQueryValue() {
        return queryValue;
    }

    public void setQueryValue(Object queryValue) {
        this.queryValue = queryValue;
    }

    public Object getQueryOptiontype() {
        return queryOptiontype;
    }

    public void setQueryOptiontype(Object queryOptiontype) {
        this.queryOptiontype = queryOptiontype;
    }

    public Object getQueryOption1() {
        return queryOption1;
    }

    public void setQueryOption1(Object queryOption1) {
        this.queryOption1 = queryOption1;
    }

    public Object getQueryOption2() {
        return queryOption2;
    }

    public void setQueryOption2(Object queryOption2) {
        this.queryOption2 = queryOption2;
    }

    public Object getQueryOption3() {
        return queryOption3;
    }

    public void setQueryOption3(Object queryOption3) {
        this.queryOption3 = queryOption3;
    }

    public Object getQueryOption4() {
        return queryOption4;
    }

    public void setQueryOption4(Object queryOption4) {
        this.queryOption4 = queryOption4;
    }

    public Object getQueryOption5() {
        return queryOption5;
    }

    public void setQueryOption5(Object queryOption5) {
        this.queryOption5 = queryOption5;
    }

    public Object getSectorName() {
        return sectorName;
    }

    public void setSectorName(Object sectorName) {
        this.sectorName = sectorName;
    }

    public Long getQueryVersion() {
        return queryVersion;
    }

    public void setQueryVersion(Long queryVersion) {
        this.queryVersion = queryVersion;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getIsEffective() {
        return isEffective;
    }

    public void setIsEffective(String isEffective) {
        this.isEffective = isEffective == null ? null : isEffective.trim();
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }
}