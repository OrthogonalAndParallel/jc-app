package com.yonyou;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author jinchenj
 * @description 修复数据
 * @create:2025-08-13 18:07:06
 */
public class MainFixer {

    private static final String QMS_INCOMINSPECTORDER_LIST = "qms_incominspectorder_list";
    private static final String QMS_INCOMINSPECTORDER_CARD = "qms_incominspectorder_card";
    private static final String QMS_INSPECTINFO_LIST = "qms_inspectinfo_list";
    private static final String QMS_INSPECTINFO_CARD = "qms_inspectinfo_card";

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: java -jar fixer.jar <is_storage> <codes> <domain> <cookie>");
            return;
        }

        try {
            executeFix(args[0], args[1], args[2], args[3]);
        } catch (Exception e) {
            System.err.println("执行出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void executeFix(String isStorage, String codes, String domain, String cookie) throws Exception {
        try {
            System.out.println("开始执行数据修复...");

            String[] codesArr = codes.split(",");
            for (String code : codesArr) {
                try {
                    fixDocument(code, isStorage, domain, cookie);
                } catch (Exception e) {
                    System.err.println("修复单据【" + code + "】时出错: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("数据修复完成!");
        } catch (Exception ex) {
            System.err.println("执行出错: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }

    private static void fixDocument(String code, String isStorage, String domain, String cookie) throws Exception {
        System.out.println("开始修复单据【" + code + "】");

        // 查询检验单ID
        String id = findInspectOrderId(code, domain, cookie);
        if (id == null) {
            System.err.println("查询失败: " + code);
            return;
        }

        // 获取检验单详情和结果明细
        JSONObject orderDetail = getInspectOrderDetail(id, domain, cookie);
        Set<String> vsourcecodes = updateOrderResultDetails(id, isStorage, domain, cookie);

        // 更新检验单
        updateInspectOrder(orderDetail, isStorage, vsourcecodes, domain, cookie);

        // 更新质检结果
        updateInspectResults(vsourcecodes, isStorage, domain, cookie);

        System.out.println("单据: 【" + code + "】修复完成");
    }

    private static String findInspectOrderId(String code, String domain, String cookie) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        String url = domain + "/yonbip-mm-qms/bill/list";
        JSONObject parameters = new JSONObject();
        parameters.put("billnum", QMS_INCOMINSPECTORDER_LIST);

        JSONObject condition = new JSONObject();
        JSONArray commonVOs = new JSONArray();
        JSONObject commonVO = new JSONObject();
        commonVO.put("itemName", "code");
        commonVO.put("value1", code);
        commonVOs.add(commonVO);
        condition.put("commonVOs", commonVOs);
        parameters.put("condition", condition);

        String resultMsg = HttpRequestUtil.doPost(url, headers, JSON.toJSONString(parameters));
        JSONObject resultObj = JSONObject.parseObject(resultMsg);
        JSONArray recordList = resultObj.getJSONObject("data").getJSONArray("recordList");

        return recordList.size() > 0 ? recordList.getJSONObject(0).getString("id") : null;
    }

    private static JSONObject getInspectOrderDetail(String id, String domain, String cookie) throws Exception {
        String url = domain + "/yonbip-mm-qms/bill/detail?billnum=" + QMS_INCOMINSPECTORDER_CARD + "&id=" + id;
        String resultMsg = HttpRequestUtil.doGetByCookie(url, cookie);
        return JSONObject.parseObject(resultMsg);
    }

    private static Set<String> updateOrderResultDetails(String id, String isStorage, String domain, String cookie) throws Exception {
        Set<String> vsourcecodes = new HashSet<>();

        String url = domain + "/yonbip-mm-qms/bill/detail?billnum=qms_incominspectorder_detailresult&id=" + id;
        String resultMsg = HttpRequestUtil.doGetByCookie(url, cookie);
        JSONObject resultObj = JSONObject.parseObject(resultMsg);

        JSONArray sourceObjs = resultObj.getJSONObject("data").getJSONArray("qms_qit_incominspectorder_sourcelistList");
        for (int i = 0; i < sourceObjs.size(); i++) {
            JSONObject sourceObj = sourceObjs.getJSONObject(i);
            sourceObj.put("_status", "Update");
            vsourcecodes.add(sourceObj.getString("vsourcecode"));

            JSONArray detailResultObjs = sourceObj.getJSONArray("qms_qit_incominspectorder_detailresultList");
            for (int j = 0; j < detailResultObjs.size(); j++) {
                JSONObject detailResultObj = detailResultObjs.getJSONObject(j);
                detailResultObj.put("_status", "Update");
                detailResultObj.put("is_storage", isStorage);
            }
        }

        return vsourcecodes;
    }

    private static void updateInspectOrder(JSONObject orderDetail, String isStorage, Set<String> vsourcecodes,
                                           String domain, String cookie) throws Exception {
        // 清理不需要的字段
        orderDetail.remove("code");
        orderDetail.remove("message");
        orderDetail.remove("traceId");
        orderDetail.remove("uploadable");
        orderDetail.put("billnum", QMS_INCOMINSPECTORDER_CARD);

        // 更新检验结果
        JSONArray resultList = orderDetail.getJSONObject("data").getJSONArray("qms_qit_incominspectorder_resultList");
        for (int i = 0; i < resultList.size(); i++) {
            JSONObject result = resultList.getJSONObject(i);
            result.put("_status", "Update");
            result.put("is_storage", isStorage);
        }

        // 保存更新
        String url = domain + "/yonbip-mm-qms/bill/save";
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);
        // 这里应该调用保存方法，但原代码中未实现
    }

    private static void updateInspectResults(Set<String> vsourcecodes, String isStorage, String domain, String cookie) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookie);

        for (String vsourcecode : vsourcecodes) {
            // 查询质检结果
            String listUrl = domain + "/yonbip-mm-qic/bill/list";
            JSONObject parameters = new JSONObject();
            parameters.put("billnum", QMS_INSPECTINFO_LIST);

            JSONObject condition = new JSONObject();
            JSONArray commonVOs = new JSONArray();
            JSONObject commonVO = new JSONObject();
            commonVO.put("itemName", "srcbillcode");
            commonVO.put("value1", vsourcecode);
            commonVOs.add(commonVO);
            condition.put("commonVOs", commonVOs);
            parameters.put("condition", condition);

            String resultMsg = HttpRequestUtil.doPost(listUrl, headers, JSON.toJSONString(parameters));
            JSONObject resultObj = JSONObject.parseObject(resultMsg);
            JSONArray inspectinfos = resultObj.getJSONObject("data").getJSONArray("recordList");

            // 更新每个质检结果
            for (int z = 0; z < inspectinfos.size(); z++) {
                JSONObject inspectinfo = inspectinfos.getJSONObject(z);
                String id = inspectinfo.getString("id");

                // 获取质检详情
                String detailUrl = domain + "/yonbip-mm-qic/bill/detail?billnum=" + QMS_INSPECTINFO_CARD + "&id=" + id;
                String detailResultMsg = HttpRequestUtil.doGetByCookie(detailUrl, cookie);
                JSONObject detailResultObj = JSONObject.parseObject(detailResultMsg);

                // 更新质检详情
                detailResultObj.remove("code");
                detailResultObj.remove("message");
                detailResultObj.remove("traceId");
                detailResultObj.remove("uploadable");
                detailResultObj.put("billnum", QMS_INSPECTINFO_CARD);
                detailResultObj.getJSONObject("data").put("_status", "Update");

                JSONArray detailList = detailResultObj.getJSONObject("data").getJSONArray("detailList");
                for (int i = 0; i < detailList.size(); i++) {
                    JSONObject detail = detailList.getJSONObject(i);
                    detail.put("is_storage", "1".equals(isStorage));
                    detail.put("_status", "Update");
                }

                // 保存更新
                String saveUrl = domain + "/yonbip-mm-qic/bill/save";
                // 这里应该调用保存方法，但原代码中未实现
            }
        }
    }

}
