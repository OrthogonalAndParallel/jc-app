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
 * @description 修复数据 Swing 版本
 * @create:2025-08-13 18:07:06
 */
public class MainFixer {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: java -jar fixer.jar <is_storage> <codes> <domain> <cookie>");
            return;
        }

        String is_storage = args[0];
        String codes = args[1];
        String domain = args[2];
        String cookie = args[3];

        try {
            executeFix(is_storage, codes, domain, cookie);
        } catch (Exception e) {
            System.err.println("执行出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeFix(String is_storage, String codes, String domain, String cookie) throws Exception {
        try {
            System.out.println("开始执行数据修复...");

            String[] codesArr = codes.split(",");
            for (String code : codesArr) {
                System.out.println("开始修复单据【" + code + "】");

                Map<String, String> headers = new HashMap<>();
                headers.put("Cookie", cookie);

                // 列表查询
                String url0 = domain + "/yonbip-mm-qms/bill/list";
                JSONObject parameters = new JSONObject();
                parameters.put("billnum", "qms_incominspectorder_list");
                JSONObject condition = new JSONObject();
                JSONArray commonVOs = new JSONArray();
                JSONObject commonVO = new JSONObject();
                commonVO.put("itemName", "code");
                commonVO.put("value1", code);
                commonVOs.add(commonVO);
                condition.put("commonVOs", commonVOs);
                parameters.put("condition", condition);
                String resultMsg0 = HttpRequestUtil.doPost(url0, headers, JSON.toJSONString(parameters));
                JSONObject resultMsgObj0 = JSONObject.parseObject(resultMsg0);
                JSONArray recordList0 = resultMsgObj0.getJSONObject("data").getJSONArray("recordList");
                if (recordList0.size() <= 0) {
                    System.err.println("查询失败: " + code);
                    continue;
                }
                String id = recordList0.getJSONObject(0).getString("id");

                // 查询检验单
                String url1 = domain + "/yonbip-mm-qms/bill/detail?billnum=qms_incominspectorder_card&id=" + id;
                String resultMsg1 = HttpRequestUtil.doGetByCookie(url1, cookie);
                JSONObject resultMsgObj1 = JSONObject.parseObject(resultMsg1);

                // 查询结果明细
                Set<String> vsourcecodes = new HashSet<>();
                String url5 = domain + "/yonbip-mm-qms/bill/detail?billnum=qms_incominspectorder_detailresult&id=" + id;
                String resultMsg5 = HttpRequestUtil.doGetByCookie(url5, cookie);
                JSONObject resultMsgObj5 = JSONObject.parseObject(resultMsg5);
                JSONArray sourceObjs5 = resultMsgObj5.getJSONObject("data").getJSONArray("qms_qit_incominspectorder_sourcelistList");
                for (int i = 0; i < sourceObjs5.size(); i++) {
                    JSONObject sourceObj5 = sourceObjs5.getJSONObject(i);
                    sourceObj5.put("_status", "Update");
                    vsourcecodes.add(sourceObj5.getString("vsourcecode"));

                    JSONArray detailResultObjs5 = sourceObj5.getJSONArray("qms_qit_incominspectorder_detailresultList");
                    for (int j = 0; j < detailResultObjs5.size(); j++) {
                        JSONObject detailResultObj5 = detailResultObjs5.getJSONObject(j);
                        detailResultObj5.put("_status", "Update");
                        detailResultObj5.put("is_storage", is_storage);
                    }
                }
                // 更新检验单
                resultMsgObj1.remove("code");
                resultMsgObj1.remove("message");
                resultMsgObj1.remove("traceId");
                resultMsgObj1.remove("uploadable");
                resultMsgObj1.put("billnum", "qms_incominspectorder_card");
                JSONArray resultList = resultMsgObj1.getJSONObject("data").getJSONArray("qms_qit_incominspectorder_resultList");
                for (int i = 0; i < resultList.size(); i++) {
                    JSONObject result = resultList.getJSONObject(i);
                    result.put("_status", "Update");
                    result.put("is_storage", is_storage);
                }
                resultMsgObj1.getJSONObject("data").put("qms_qit_incominspectorder_sourcelistList", sourceObjs5);
                String url2 = domain + "/yonbip-mm-qms/bill/save";

                // 查询质检结果
                for (String vsourcecode : vsourcecodes) {
                    String url6 = domain + "/yonbip-mm-qic/bill/list";
                    JSONObject parameters6 = new JSONObject();
                    parameters6.put("billnum", "qms_inspectinfo_list");
                    JSONObject condition6 = new JSONObject();
                    JSONArray commonVOs6 = new JSONArray();
                    JSONObject commonVO6 = new JSONObject();
                    commonVO6.put("itemName", "srcbillcode");
                    commonVO6.put("value1", vsourcecode);
                    commonVOs6.add(commonVO6);
                    condition6.put("commonVOs", commonVOs6);
                    parameters6.put("condition", condition6);
                    String resultMsg6 = HttpRequestUtil.doPost(url6, headers, JSON.toJSONString(parameters6));
                    JSONObject resultMsgObj6 = JSONObject.parseObject(resultMsg6);
                    JSONArray inspectinfos = resultMsgObj6.getJSONObject("data").getJSONArray("recordList");
                    for (int z = 0; z < inspectinfos.size(); z++) {
                        JSONObject inspectinfo = inspectinfos.getJSONObject(z);

                        String id6 = inspectinfo.getString("id");
                        String url3 = domain + "/yonbip-mm-qic/bill/detail?billnum=qms_inspectinfo_card&id=" + id6;
                        String resultMsg3 = HttpRequestUtil.doGetByCookie(url3, cookie);
                        JSONObject resultMsgObj3 = JSONObject.parseObject(resultMsg3);

                        // 更新质检结果
                        String url4 = domain + "/yonbip-mm-qic/bill/save";
                        resultMsgObj3.remove("code");
                        resultMsgObj3.remove("message");
                        resultMsgObj3.remove("traceId");
                        resultMsgObj3.remove("uploadable");
                        resultMsgObj3.put("billnum", "qms_inspectinfo_card");
                        resultMsgObj3.getJSONObject("data").put("_status", "Update");
                        JSONArray detailList = resultMsgObj3.getJSONObject("data").getJSONArray("detailList");
                        for (int i = 0; i < detailList.size(); i++) {
                            JSONObject detail = detailList.getJSONObject(i);
                            detail.put("is_storage", is_storage.equals("1") ? true : false);
                            detail.put("_status", "Update");
                        }
                        // 修复：删除错误的 toJSONString() 调用
                        resultMsgObj3.put("data", resultMsgObj3.getJSONObject("data").toJSONString());
                    }
                }
                System.out.println("单据: 【" + code + "】修复完成");
            }
            System.out.println("数据修复完成!");
        } catch (Exception ex) {
            System.err.println("执行出错: " + ex.getMessage());
            ex.printStackTrace();
            throw ex;
        }
    }
}
