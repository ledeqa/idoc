package com.idoc.web.out;


import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.idoc.model.login.SearchInterfaceInProduct;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.idoc.model.Interface;

import com.idoc.service.docManage.InterfaceServiceImpl;

import com.idoc.service.login.LoginIndexServiceImpl;

import com.idoc.web.BaseController;


@Controller
@RequestMapping("/out")
public class OutInterfaceController extends BaseController {

    @Autowired
    private InterfaceServiceImpl interfaceServiceImpl;

    @Autowired
    private LoginIndexServiceImpl loginIndexServiceImpl;

    /**
     * 根据产品id、接口名称、接口url查询接口
     *
     * @param productId
     * @param interfaceUrl
     * @return
     * @throws IOException
     */
    @RequestMapping("getInterfaceByUrl.html")
    @ResponseBody
    public Map<String, Object> getInterfaceByUrl(
            @RequestParam(value = "productId", required = true) String productId,
            @RequestParam(value = "interfaceName", required = false) String interfaceName,
            @RequestParam(value = "interfaceUrl", required = false) String interfaceUrl
    ) throws IOException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (StringUtils.isBlank(productId)) {
            retMap.put("retCode", 401);
            retMap.put("retDesc", "参数缺失");
            return retMap;
        }
        List<SearchInterfaceInProduct> list = loginIndexServiceImpl.searchInterfaceId(productId, interfaceName, interfaceUrl);
        if (list == null || list.size() <= 0) {
            retMap.put("retCode", -1);
            retMap.put("retDesc", "没有查询到满足条件的接口！productId=" + productId + ",interfaceName=" + interfaceName + ",interfaceUrl=" + interfaceUrl);
        } else {
            if (list.size() > 1) {
                retMap.put("retCode", 200);
                retMap.put("retDesc", "查询成功！有多个接口");
            } else {
                SearchInterfaceInProduct interInPro = list.get(0);
                Long id = Long.valueOf(interInPro.getInterfaceId());
                Interface inter = interfaceServiceImpl.getInterfaceInfo(id);
                if (inter == null) {
                    retMap.put("retCode", -1);
                    retMap.put("retDesc", "没有查询到满足条件的接口！interfaceId=" + id);
                } else {
                    retMap.put("retCode", 200);
                    retMap.put("retDesc", "查询成功！");
                    retMap.put("inter", inter);
                }
            }

        }

        return retMap;
    }
}