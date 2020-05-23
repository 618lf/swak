package com.swak.entity;

import com.swak.utils.Lists;
import com.swak.utils.Maps;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基础树结构解决方案
 *
 * @author: lifeng
 * @date: 2020/3/29 11:22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TreeVO extends BaseTreeEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String treeName;
    private String treeCode;
    private String treeType;
    private String treePath;
    private String treeUrl;
    private String treeDesc;

    public static Map<Integer, List<TreeVO>> classifyByTreeLevel(List<TreeVO> entities) {
        Map<Integer, List<TreeVO>> treeMap = Maps.newHashMap();
        for (TreeVO treeVO : entities) {
            if (!treeMap.containsKey(treeVO.getLevel())) {
                List<TreeVO> trees = Lists.newArrayList();
                treeMap.put(treeVO.getLevel(), trees);
            }
            treeMap.get(treeVO.getLevel()).add(treeVO);
        }
        return treeMap;
    }

    public static List<TreeVO> sort(List<TreeVO> entities) {
        List<TreeVO> copyTrees = Lists.newArrayList();
        Map<Integer, List<TreeVO>> treeMap = classifyByTreeLevel(entities);
        int firstLevel = 1;
        List<TreeVO> treeList = treeMap.get(firstLevel);
        if (treeList != null) {
            for (TreeVO tree : treeList) {
                copyTrees.add(tree);
                List<TreeVO> child = sort(tree, firstLevel + 1, treeMap);
                if (child.size() != 0) {
                    copyTrees.addAll(child);
                    tree.setIsLeaf(Boolean.FALSE);
                }
            }
        }
        return copyTrees;
    }

    public static List<TreeVO> sort(TreeVO parent, int level, Map<Integer, List<TreeVO>> menuMap) {
        List<TreeVO> copyTrees = Lists.newArrayList();
        List<TreeVO> menuList = menuMap.get(level);
        if (menuList != null) {
            for (TreeVO tree : menuList) {
                if (tree.getParent().equals(parent.getId())) {
                    copyTrees.add(tree);
                    List<TreeVO> child = sort(tree, level + 1, menuMap);
                    if (child.size() != 0) {
                        copyTrees.addAll(child);
                        tree.setIsLeaf(Boolean.FALSE);
                    }
                }
            }
        }
        return copyTrees;
    }
}