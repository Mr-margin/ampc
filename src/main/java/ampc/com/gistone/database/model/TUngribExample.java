package ampc.com.gistone.database.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TUngribExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TUngribExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andUngribIdIsNull() {
            addCriterion("UNGRIB_ID is null");
            return (Criteria) this;
        }

        public Criteria andUngribIdIsNotNull() {
            addCriterion("UNGRIB_ID is not null");
            return (Criteria) this;
        }

        public Criteria andUngribIdEqualTo(Long value) {
            addCriterion("UNGRIB_ID =", value, "ungribId");
            return (Criteria) this;
        }

        public Criteria andUngribIdNotEqualTo(Long value) {
            addCriterion("UNGRIB_ID <>", value, "ungribId");
            return (Criteria) this;
        }

        public Criteria andUngribIdGreaterThan(Long value) {
            addCriterion("UNGRIB_ID >", value, "ungribId");
            return (Criteria) this;
        }

        public Criteria andUngribIdGreaterThanOrEqualTo(Long value) {
            addCriterion("UNGRIB_ID >=", value, "ungribId");
            return (Criteria) this;
        }

        public Criteria andUngribIdLessThan(Long value) {
            addCriterion("UNGRIB_ID <", value, "ungribId");
            return (Criteria) this;
        }

        public Criteria andUngribIdLessThanOrEqualTo(Long value) {
            addCriterion("UNGRIB_ID <=", value, "ungribId");
            return (Criteria) this;
        }

        public Criteria andUngribIdIn(List<Long> values) {
            addCriterion("UNGRIB_ID in", values, "ungribId");
            return (Criteria) this;
        }

        public Criteria andUngribIdNotIn(List<Long> values) {
            addCriterion("UNGRIB_ID not in", values, "ungribId");
            return (Criteria) this;
        }

        public Criteria andUngribIdBetween(Long value1, Long value2) {
            addCriterion("UNGRIB_ID between", value1, value2, "ungribId");
            return (Criteria) this;
        }

        public Criteria andUngribIdNotBetween(Long value1, Long value2) {
            addCriterion("UNGRIB_ID not between", value1, value2, "ungribId");
            return (Criteria) this;
        }

        public Criteria andAddTimeIsNull() {
            addCriterion("ADD_TIME is null");
            return (Criteria) this;
        }

        public Criteria andAddTimeIsNotNull() {
            addCriterion("ADD_TIME is not null");
            return (Criteria) this;
        }

        public Criteria andAddTimeEqualTo(Date value) {
            addCriterion("ADD_TIME =", value, "addTime");
            return (Criteria) this;
        }

        public Criteria andAddTimeNotEqualTo(Date value) {
            addCriterion("ADD_TIME <>", value, "addTime");
            return (Criteria) this;
        }

        public Criteria andAddTimeGreaterThan(Date value) {
            addCriterion("ADD_TIME >", value, "addTime");
            return (Criteria) this;
        }

        public Criteria andAddTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("ADD_TIME >=", value, "addTime");
            return (Criteria) this;
        }

        public Criteria andAddTimeLessThan(Date value) {
            addCriterion("ADD_TIME <", value, "addTime");
            return (Criteria) this;
        }

        public Criteria andAddTimeLessThanOrEqualTo(Date value) {
            addCriterion("ADD_TIME <=", value, "addTime");
            return (Criteria) this;
        }

        public Criteria andAddTimeIn(List<Date> values) {
            addCriterion("ADD_TIME in", values, "addTime");
            return (Criteria) this;
        }

        public Criteria andAddTimeNotIn(List<Date> values) {
            addCriterion("ADD_TIME not in", values, "addTime");
            return (Criteria) this;
        }

        public Criteria andAddTimeBetween(Date value1, Date value2) {
            addCriterion("ADD_TIME between", value1, value2, "addTime");
            return (Criteria) this;
        }

        public Criteria andAddTimeNotBetween(Date value1, Date value2) {
            addCriterion("ADD_TIME not between", value1, value2, "addTime");
            return (Criteria) this;
        }

        public Criteria andPathDateIsNull() {
            addCriterion("PATH_DATE is null");
            return (Criteria) this;
        }

        public Criteria andPathDateIsNotNull() {
            addCriterion("PATH_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andPathDateEqualTo(Date value) {
            addCriterion("PATH_DATE =", value, "pathDate");
            return (Criteria) this;
        }

        public Criteria andPathDateNotEqualTo(Date value) {
            addCriterion("PATH_DATE <>", value, "pathDate");
            return (Criteria) this;
        }

        public Criteria andPathDateGreaterThan(Date value) {
            addCriterion("PATH_DATE >", value, "pathDate");
            return (Criteria) this;
        }

        public Criteria andPathDateGreaterThanOrEqualTo(Date value) {
            addCriterion("PATH_DATE >=", value, "pathDate");
            return (Criteria) this;
        }

        public Criteria andPathDateLessThan(Date value) {
            addCriterion("PATH_DATE <", value, "pathDate");
            return (Criteria) this;
        }

        public Criteria andPathDateLessThanOrEqualTo(Date value) {
            addCriterion("PATH_DATE <=", value, "pathDate");
            return (Criteria) this;
        }

        public Criteria andPathDateIn(List<Date> values) {
            addCriterion("PATH_DATE in", values, "pathDate");
            return (Criteria) this;
        }

        public Criteria andPathDateNotIn(List<Date> values) {
            addCriterion("PATH_DATE not in", values, "pathDate");
            return (Criteria) this;
        }

        public Criteria andPathDateBetween(Date value1, Date value2) {
            addCriterion("PATH_DATE between", value1, value2, "pathDate");
            return (Criteria) this;
        }

        public Criteria andPathDateNotBetween(Date value1, Date value2) {
            addCriterion("PATH_DATE not between", value1, value2, "pathDate");
            return (Criteria) this;
        }

        public Criteria andFnlStatusIsNull() {
            addCriterion("FNL_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andFnlStatusIsNotNull() {
            addCriterion("FNL_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andFnlStatusEqualTo(Integer value) {
            addCriterion("FNL_STATUS =", value, "fnlStatus");
            return (Criteria) this;
        }

        public Criteria andFnlStatusNotEqualTo(Integer value) {
            addCriterion("FNL_STATUS <>", value, "fnlStatus");
            return (Criteria) this;
        }

        public Criteria andFnlStatusGreaterThan(Integer value) {
            addCriterion("FNL_STATUS >", value, "fnlStatus");
            return (Criteria) this;
        }

        public Criteria andFnlStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("FNL_STATUS >=", value, "fnlStatus");
            return (Criteria) this;
        }

        public Criteria andFnlStatusLessThan(Integer value) {
            addCriterion("FNL_STATUS <", value, "fnlStatus");
            return (Criteria) this;
        }

        public Criteria andFnlStatusLessThanOrEqualTo(Integer value) {
            addCriterion("FNL_STATUS <=", value, "fnlStatus");
            return (Criteria) this;
        }

        public Criteria andFnlStatusIn(List<Integer> values) {
            addCriterion("FNL_STATUS in", values, "fnlStatus");
            return (Criteria) this;
        }

        public Criteria andFnlStatusNotIn(List<Integer> values) {
            addCriterion("FNL_STATUS not in", values, "fnlStatus");
            return (Criteria) this;
        }

        public Criteria andFnlStatusBetween(Integer value1, Integer value2) {
            addCriterion("FNL_STATUS between", value1, value2, "fnlStatus");
            return (Criteria) this;
        }

        public Criteria andFnlStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("FNL_STATUS not between", value1, value2, "fnlStatus");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusIsNull() {
            addCriterion("GFS1_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusIsNotNull() {
            addCriterion("GFS1_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusEqualTo(Integer value) {
            addCriterion("GFS1_STATUS =", value, "gfs1Status");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusNotEqualTo(Integer value) {
            addCriterion("GFS1_STATUS <>", value, "gfs1Status");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusGreaterThan(Integer value) {
            addCriterion("GFS1_STATUS >", value, "gfs1Status");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("GFS1_STATUS >=", value, "gfs1Status");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusLessThan(Integer value) {
            addCriterion("GFS1_STATUS <", value, "gfs1Status");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusLessThanOrEqualTo(Integer value) {
            addCriterion("GFS1_STATUS <=", value, "gfs1Status");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusIn(List<Integer> values) {
            addCriterion("GFS1_STATUS in", values, "gfs1Status");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusNotIn(List<Integer> values) {
            addCriterion("GFS1_STATUS not in", values, "gfs1Status");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusBetween(Integer value1, Integer value2) {
            addCriterion("GFS1_STATUS between", value1, value2, "gfs1Status");
            return (Criteria) this;
        }

        public Criteria andGfs1StatusNotBetween(Integer value1, Integer value2) {
            addCriterion("GFS1_STATUS not between", value1, value2, "gfs1Status");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusIsNull() {
            addCriterion("GFS2_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusIsNotNull() {
            addCriterion("GFS2_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusEqualTo(Integer value) {
            addCriterion("GFS2_STATUS =", value, "gfs2Status");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusNotEqualTo(Integer value) {
            addCriterion("GFS2_STATUS <>", value, "gfs2Status");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusGreaterThan(Integer value) {
            addCriterion("GFS2_STATUS >", value, "gfs2Status");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("GFS2_STATUS >=", value, "gfs2Status");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusLessThan(Integer value) {
            addCriterion("GFS2_STATUS <", value, "gfs2Status");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusLessThanOrEqualTo(Integer value) {
            addCriterion("GFS2_STATUS <=", value, "gfs2Status");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusIn(List<Integer> values) {
            addCriterion("GFS2_STATUS in", values, "gfs2Status");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusNotIn(List<Integer> values) {
            addCriterion("GFS2_STATUS not in", values, "gfs2Status");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusBetween(Integer value1, Integer value2) {
            addCriterion("GFS2_STATUS between", value1, value2, "gfs2Status");
            return (Criteria) this;
        }

        public Criteria andGfs2StatusNotBetween(Integer value1, Integer value2) {
            addCriterion("GFS2_STATUS not between", value1, value2, "gfs2Status");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusIsNull() {
            addCriterion("GFS3_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusIsNotNull() {
            addCriterion("GFS3_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusEqualTo(Integer value) {
            addCriterion("GFS3_STATUS =", value, "gfs3Status");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusNotEqualTo(Integer value) {
            addCriterion("GFS3_STATUS <>", value, "gfs3Status");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusGreaterThan(Integer value) {
            addCriterion("GFS3_STATUS >", value, "gfs3Status");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("GFS3_STATUS >=", value, "gfs3Status");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusLessThan(Integer value) {
            addCriterion("GFS3_STATUS <", value, "gfs3Status");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusLessThanOrEqualTo(Integer value) {
            addCriterion("GFS3_STATUS <=", value, "gfs3Status");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusIn(List<Integer> values) {
            addCriterion("GFS3_STATUS in", values, "gfs3Status");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusNotIn(List<Integer> values) {
            addCriterion("GFS3_STATUS not in", values, "gfs3Status");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusBetween(Integer value1, Integer value2) {
            addCriterion("GFS3_STATUS between", value1, value2, "gfs3Status");
            return (Criteria) this;
        }

        public Criteria andGfs3StatusNotBetween(Integer value1, Integer value2) {
            addCriterion("GFS3_STATUS not between", value1, value2, "gfs3Status");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusIsNull() {
            addCriterion("GFS4_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusIsNotNull() {
            addCriterion("GFS4_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusEqualTo(Integer value) {
            addCriterion("GFS4_STATUS =", value, "gfs4Status");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusNotEqualTo(Integer value) {
            addCriterion("GFS4_STATUS <>", value, "gfs4Status");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusGreaterThan(Integer value) {
            addCriterion("GFS4_STATUS >", value, "gfs4Status");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("GFS4_STATUS >=", value, "gfs4Status");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusLessThan(Integer value) {
            addCriterion("GFS4_STATUS <", value, "gfs4Status");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusLessThanOrEqualTo(Integer value) {
            addCriterion("GFS4_STATUS <=", value, "gfs4Status");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusIn(List<Integer> values) {
            addCriterion("GFS4_STATUS in", values, "gfs4Status");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusNotIn(List<Integer> values) {
            addCriterion("GFS4_STATUS not in", values, "gfs4Status");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusBetween(Integer value1, Integer value2) {
            addCriterion("GFS4_STATUS between", value1, value2, "gfs4Status");
            return (Criteria) this;
        }

        public Criteria andGfs4StatusNotBetween(Integer value1, Integer value2) {
            addCriterion("GFS4_STATUS not between", value1, value2, "gfs4Status");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusIsNull() {
            addCriterion("GFS5_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusIsNotNull() {
            addCriterion("GFS5_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusEqualTo(Integer value) {
            addCriterion("GFS5_STATUS =", value, "gfs5Status");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusNotEqualTo(Integer value) {
            addCriterion("GFS5_STATUS <>", value, "gfs5Status");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusGreaterThan(Integer value) {
            addCriterion("GFS5_STATUS >", value, "gfs5Status");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("GFS5_STATUS >=", value, "gfs5Status");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusLessThan(Integer value) {
            addCriterion("GFS5_STATUS <", value, "gfs5Status");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusLessThanOrEqualTo(Integer value) {
            addCriterion("GFS5_STATUS <=", value, "gfs5Status");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusIn(List<Integer> values) {
            addCriterion("GFS5_STATUS in", values, "gfs5Status");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusNotIn(List<Integer> values) {
            addCriterion("GFS5_STATUS not in", values, "gfs5Status");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusBetween(Integer value1, Integer value2) {
            addCriterion("GFS5_STATUS between", value1, value2, "gfs5Status");
            return (Criteria) this;
        }

        public Criteria andGfs5StatusNotBetween(Integer value1, Integer value2) {
            addCriterion("GFS5_STATUS not between", value1, value2, "gfs5Status");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusIsNull() {
            addCriterion("GFS6_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusIsNotNull() {
            addCriterion("GFS6_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusEqualTo(Integer value) {
            addCriterion("GFS6_STATUS =", value, "gfs6Status");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusNotEqualTo(Integer value) {
            addCriterion("GFS6_STATUS <>", value, "gfs6Status");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusGreaterThan(Integer value) {
            addCriterion("GFS6_STATUS >", value, "gfs6Status");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("GFS6_STATUS >=", value, "gfs6Status");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusLessThan(Integer value) {
            addCriterion("GFS6_STATUS <", value, "gfs6Status");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusLessThanOrEqualTo(Integer value) {
            addCriterion("GFS6_STATUS <=", value, "gfs6Status");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusIn(List<Integer> values) {
            addCriterion("GFS6_STATUS in", values, "gfs6Status");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusNotIn(List<Integer> values) {
            addCriterion("GFS6_STATUS not in", values, "gfs6Status");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusBetween(Integer value1, Integer value2) {
            addCriterion("GFS6_STATUS between", value1, value2, "gfs6Status");
            return (Criteria) this;
        }

        public Criteria andGfs6StatusNotBetween(Integer value1, Integer value2) {
            addCriterion("GFS6_STATUS not between", value1, value2, "gfs6Status");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusIsNull() {
            addCriterion("GFS7_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusIsNotNull() {
            addCriterion("GFS7_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusEqualTo(Integer value) {
            addCriterion("GFS7_STATUS =", value, "gfs7Status");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusNotEqualTo(Integer value) {
            addCriterion("GFS7_STATUS <>", value, "gfs7Status");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusGreaterThan(Integer value) {
            addCriterion("GFS7_STATUS >", value, "gfs7Status");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("GFS7_STATUS >=", value, "gfs7Status");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusLessThan(Integer value) {
            addCriterion("GFS7_STATUS <", value, "gfs7Status");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusLessThanOrEqualTo(Integer value) {
            addCriterion("GFS7_STATUS <=", value, "gfs7Status");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusIn(List<Integer> values) {
            addCriterion("GFS7_STATUS in", values, "gfs7Status");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusNotIn(List<Integer> values) {
            addCriterion("GFS7_STATUS not in", values, "gfs7Status");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusBetween(Integer value1, Integer value2) {
            addCriterion("GFS7_STATUS between", value1, value2, "gfs7Status");
            return (Criteria) this;
        }

        public Criteria andGfs7StatusNotBetween(Integer value1, Integer value2) {
            addCriterion("GFS7_STATUS not between", value1, value2, "gfs7Status");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusIsNull() {
            addCriterion("GFS8_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusIsNotNull() {
            addCriterion("GFS8_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusEqualTo(Integer value) {
            addCriterion("GFS8_STATUS =", value, "gfs8Status");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusNotEqualTo(Integer value) {
            addCriterion("GFS8_STATUS <>", value, "gfs8Status");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusGreaterThan(Integer value) {
            addCriterion("GFS8_STATUS >", value, "gfs8Status");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("GFS8_STATUS >=", value, "gfs8Status");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusLessThan(Integer value) {
            addCriterion("GFS8_STATUS <", value, "gfs8Status");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusLessThanOrEqualTo(Integer value) {
            addCriterion("GFS8_STATUS <=", value, "gfs8Status");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusIn(List<Integer> values) {
            addCriterion("GFS8_STATUS in", values, "gfs8Status");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusNotIn(List<Integer> values) {
            addCriterion("GFS8_STATUS not in", values, "gfs8Status");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusBetween(Integer value1, Integer value2) {
            addCriterion("GFS8_STATUS between", value1, value2, "gfs8Status");
            return (Criteria) this;
        }

        public Criteria andGfs8StatusNotBetween(Integer value1, Integer value2) {
            addCriterion("GFS8_STATUS not between", value1, value2, "gfs8Status");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusIsNull() {
            addCriterion("GFS9_STATUS is null");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusIsNotNull() {
            addCriterion("GFS9_STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusEqualTo(Integer value) {
            addCriterion("GFS9_STATUS =", value, "gfs9Status");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusNotEqualTo(Integer value) {
            addCriterion("GFS9_STATUS <>", value, "gfs9Status");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusGreaterThan(Integer value) {
            addCriterion("GFS9_STATUS >", value, "gfs9Status");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("GFS9_STATUS >=", value, "gfs9Status");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusLessThan(Integer value) {
            addCriterion("GFS9_STATUS <", value, "gfs9Status");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusLessThanOrEqualTo(Integer value) {
            addCriterion("GFS9_STATUS <=", value, "gfs9Status");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusIn(List<Integer> values) {
            addCriterion("GFS9_STATUS in", values, "gfs9Status");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusNotIn(List<Integer> values) {
            addCriterion("GFS9_STATUS not in", values, "gfs9Status");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusBetween(Integer value1, Integer value2) {
            addCriterion("GFS9_STATUS between", value1, value2, "gfs9Status");
            return (Criteria) this;
        }

        public Criteria andGfs9StatusNotBetween(Integer value1, Integer value2) {
            addCriterion("GFS9_STATUS not between", value1, value2, "gfs9Status");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}