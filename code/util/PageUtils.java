package com.jdh.fuhsi.portal.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.jdh.common.model.resp.PageResp;
import com.jdh.conversion.MapperTools;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 *
 * @author lym
 * @date 2020/3/7 11:00
 */
public class PageUtils {

    /**
     * 构造目标分页对象
     *
     * @param dataList 数据列表
     */
    public static <T> PageResp<T> newPage(List<T> dataList) {
        PageInfo<T> sourcePageObj = new PageInfo<>(dataList);
        PageResp<T> targetPageObj = new PageResp<>();
        MapperTools.map(sourcePageObj, targetPageObj);
        return targetPageObj;
    }

    /**
     * 构造目标分页对象
     *
     * @param pageNo
     * @param pageSize
     * @param list
     * @return
     */
    public static <T> PageResp<T> newPage(int pageNo, int pageSize, List<T> list) {
        PageResp<T> resp = new PageResp<>();
        long total = CollectionUtils.isEmpty(list) ? 0 : list.size();
        int pages = total % pageSize == 0 ? (int) total / pageSize : (int) total / pageSize + 1;
        int size = total != 0 ? (pages > pageNo ? pageSize : (int) total - (pageSize * (pages - 1))) : 0;
        size = pageNo > pages ? 0 : size;
        List<T> resultList = new ArrayList<>();
        int begin = (pageNo - 1) * pageSize;
        if (size > 0) {
            for (int i = begin; i < begin + size; i++) {
                resultList.add(list.get(i));
            }
        } else {
            resultList = null;
        }
        resp.setList(resultList);
        resp.setTotal(total);
        resp.setPageNo(pageNo);
        resp.setPages(pages);
        resp.setPageSize(size);
        return resp;
    }

    /**
     * 复制除列表外的分页对象信息(用于列表转换)
     *
     * @param sourcePageObj
     * @param <T>
     * @return
     */
    public static <T> PageResp<T> convertPageResp(PageResp sourcePageObj, List<T> targetList) {
        PageResp<T> targetPageObj = new PageResp<>();
        targetPageObj.setPageNo(sourcePageObj.getPageNo());
        targetPageObj.setPageSize(sourcePageObj.getPageSize());
        targetPageObj.setTotal(sourcePageObj.getTotal());
        targetPageObj.setPages(sourcePageObj.getPages());
        targetPageObj.setList(targetList);
        return targetPageObj;
    }

    /**
     * 复制除列表外的分页对象信息(用于列表转换)
     *
     * @param sourcePageObj
     * @param <T>
     * @return
     */
    public static <T> PageResp<T> copyPageInfo(PageInfo<T> sourcePageObj) {
        PageResp<T> targetPageObj = new PageResp<>();
        targetPageObj.setPageNo(sourcePageObj.getPageNum());
        targetPageObj.setPageSize(sourcePageObj.getSize());
        targetPageObj.setTotal(sourcePageObj.getTotal());
        targetPageObj.setPages(sourcePageObj.getPages());
        return targetPageObj;
    }

    /**
     * 将源列表置入PageInfo, 再转化成目标类型的PageResp
     *
     * @param sourceList 源列表
     * @param targetList 目标列表
     * @param <T>        目标列表类型
     * @return
     */
    public static <T> PageResp<T> convertToPageResp(List sourceList, List<T> targetList) {
        PageInfo sourcePageInfo = new PageInfo<>(sourceList);
        PageResp<T> sourcePageResp = copyPageInfo(sourcePageInfo);
        sourcePageResp.setList(targetList);
        return sourcePageResp;
    }

    /**
     * 将IPage 转为 PageResp
     * @param source
     * @param <T>
     * @return
     */
    public static <T> PageResp<T> iPageToPageResp(IPage<T> source) {
        PageResp<T> result = new PageResp<>();
        result.setPageNo((int) source.getCurrent());
        result.setPageSize((int) source.getSize());
        result.setPages((int) source.getPages());
        result.setTotal(source.getTotal());
        result.setList(source.getRecords());
        return result;
    }
}
