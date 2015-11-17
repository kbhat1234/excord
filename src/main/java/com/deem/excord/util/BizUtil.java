package com.deem.excord.util;

import com.deem.excord.domain.EcTestplan;
import com.deem.excord.vo.TestPlanMetricVo;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum BizUtil {

    INSTANCE;
    private static final Logger logger = LoggerFactory.getLogger(BizUtil.class);

    public List<TestPlanMetricVo> flattenTestPlanMetrics(List<Object[]> tmLst) {
        Map<Long, TestPlanMetricVo> tmMap = new HashMap<Long, TestPlanMetricVo>();
        for (Object[] result : tmLst) {
            Long folderId = ((BigInteger) result[0]).longValue();
            String status = (String) result[2];
            if (status == null) {
                status = Constants.STATUS_NOT_RUN;
            }
            TestPlanMetricVo tpm = tmMap.get(folderId);
            if (tpm == null) {
                tpm = new TestPlanMetricVo();
                tpm.setFolderId(folderId);
                tpm.setFolderName((String) result[1]);
                tpm.setPassCount(0);
                tpm.setFailCount(0);
                tpm.setNaCount(0);
                tpm.setBlockedCount(0);
                tpm.setFutureCount(0);
                tpm.setNotcompleteCount(0);
                tpm.setNotrunCount(0);
                tpm.setTotal(0);
            }

            Integer count = 0;
            if (result[3] != null) {
                count = ((BigInteger) result[3]).intValue();
            }
            tpm.setTotal(tpm.getTotal() + count);
            switch (status) {
                case Constants.STATUS_PASSED:
                    tpm.setPassCount(count);
                    break;
                case Constants.STATUS_FAILED:
                    tpm.setFailCount(count);
                    break;
                case Constants.STATUS_BLOCKED:
                    tpm.setBlockedCount(count);
                    break;
                case Constants.STATUS_NOT_COMPLETED:
                    tpm.setNotcompleteCount(count);
                    break;
                default:
                    tpm.setNotrunCount(tpm.getNotrunCount() + count);
                    break;
            }
            tmMap.put(folderId, tpm);

        }

        List<TestPlanMetricVo> finalMetricLst = new ArrayList<TestPlanMetricVo>();
        for (Map.Entry<Long, TestPlanMetricVo> entry : tmMap.entrySet()) {
            TestPlanMetricVo value = entry.getValue();
            value.setPassRate(Math.round((value.getPassCount() * 100.0) / value.getTotal()));
            value.setProgressRate(Math.round(((value.getTotal() - value.getNotrunCount()) * 100.0) / value.getTotal()));
            finalMetricLst.add(value);
        }
        return finalMetricLst;
    }

    public List<TestPlanMetricVo> gererateTestplanMetrics(EcTestplan tp, List<Object[]> metricsLst) {
        List<TestPlanMetricVo> resultLst = new ArrayList<TestPlanMetricVo>();
        TestPlanMetricVo tpm = new TestPlanMetricVo();
        tpm.setTestPlanId(tp.getId());
        tpm.setPassCount(0);
        tpm.setProgressCount(0);
        tpm.setTotal(0);
        for (Object[] result : metricsLst) {
            Long tpId = ((BigInteger) result[0]).longValue();
            String tpStatus = (String) (result[1]);
            Integer tpCount = ((BigInteger) (result[2])).intValue();
            if (tpId.equals(tp.getId())) {
                switch (tpStatus) {
                    case Constants.STATUS_PASSED:
                        tpm.setPassCount(tpCount);
                        break;
                    case "TOTAL":
                        tpm.setTotal(tpCount);
                        break;
                    case "RUN":
                        tpm.setProgressCount(tpCount);
                        break;
                }
            }
        }
        logger.debug("PlanId:{},Pass:{},Total:{},Progress:{}", tp.getId(), tpm.getPassCount(), tpm.getTotal(), tpm.getProgressCount());
        tpm.setPassRate(Math.round((tpm.getPassCount() * 100.0) / tpm.getTotal()));
        tpm.setProgressRate(Math.round((tpm.getProgressCount() * 100.0) / tpm.getTotal()));
        resultLst.add(tpm);
        return resultLst;
    }

    public List<TestPlanMetricVo> gererateAllTestPlanMetrics(List<EcTestplan> testPlanLst, List<Object[]> metricsLst) {
        List<TestPlanMetricVo> resultLst = new ArrayList<TestPlanMetricVo>();
        Map<Long, TestPlanMetricVo> mapObj = new HashMap<Long, TestPlanMetricVo>();
        for (EcTestplan tp : testPlanLst) {
            TestPlanMetricVo tpm = new TestPlanMetricVo();
            tpm.setTestPlanId(tp.getId());
            tpm.setPassCount(0);
            tpm.setProgressCount(0);
            tpm.setTotal(0);
            mapObj.put(tp.getId(), tpm);
        }

        for (Object[] result : metricsLst) {
            Long tpId = ((BigInteger) result[0]).longValue();
            String tpStatus = (String) (result[1]);
            Integer tpCount = ((BigInteger) (result[2])).intValue();
            TestPlanMetricVo tpm = mapObj.get(tpId);
            if (tpm != null) {
                switch (tpStatus) {
                    case Constants.STATUS_PASSED:
                        tpm.setPassCount(tpCount);
                        break;
                    case "TOTAL":
                        tpm.setTotal(tpCount);
                        break;
                    case "RUN":
                        tpm.setProgressCount(tpCount);
                        break;
                }
            }
        }
        for (Map.Entry<Long, TestPlanMetricVo> entry : mapObj.entrySet()) {
            TestPlanMetricVo tpm = entry.getValue();
            tpm.setPassRate(Math.round((tpm.getPassCount() * 100.0) / tpm.getTotal()));
            tpm.setProgressRate(Math.round((tpm.getProgressCount() * 100.0) / tpm.getTotal()));
            resultLst.add(tpm);
        }
        return resultLst;
    }

    public Boolean checkStatus(String tstatus) {
        switch (tstatus) {
            case Constants.STATUS_PASSED:
            case Constants.STATUS_BLOCKED:
            case Constants.STATUS_FAILED:
            case Constants.STATUS_NOT_COMPLETED:
            case Constants.STATUS_NOT_RUN:
                return true;
            default:
                return false;
        }

    }

    public Boolean checkEnv(String tenv) {
        switch (tenv) {
            
           
            case Constants.STATUS_PASSED:
            case Constants.STATUS_BLOCKED:
            case Constants.STATUS_FAILED:
            case Constants.STATUS_NOT_COMPLETED:
            case Constants.STATUS_NOT_RUN:
                return true;
            default:
                return false;
        }

    }

}
