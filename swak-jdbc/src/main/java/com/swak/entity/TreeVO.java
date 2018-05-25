package com.swak.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swak.utils.Lists;

/**
 * 基础树结构解决方案
 * @author lifeng
 *
 */
public class TreeVO extends BaseTreeEntity<Long> implements Serializable{

	private static final long serialVersionUID = -2495140099509798222L;
	
	private String treeName;
	private String treeCode;
	private String treeType;
	private String treePath;
	private String treeUrl;
	private String treeDesc;
	
	public String getTreeDesc() {
		return treeDesc;
	}
	public void setTreeDesc(String treeDesc) {
		this.treeDesc = treeDesc;
	}
	public String getTreeUrl() {
		return treeUrl;
	}
	public void setTreeUrl(String treeUrl) {
		this.treeUrl = treeUrl;
	}
	public String getTreeName() {
		return treeName;
	}
	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}
	public String getTreeCode() {
		return treeCode;
	}
	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}
	public String getTreeType() {
		return treeType;
	}
	public void setTreeType(String treeType) {
		this.treeType = treeType;
	}
	public String getTreePath() {
		return treePath;
	}
	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}
	//分层次
	public static Map<Integer,List<TreeVO>> classifyByTreeLevel( List<TreeVO> entities ){
		Map<Integer,List<TreeVO>> treeMap = new HashMap<Integer,List<TreeVO>>();
		for(TreeVO treeVO: entities) {
			if(!treeMap.containsKey(treeVO.getLevel())) {
			   List<TreeVO> trees = Lists.newArrayList();
			   treeMap.put(treeVO.getLevel(), trees);
			}
			treeMap.get(treeVO.getLevel()).add(treeVO);
		}
		return treeMap;
	}
	//按子节点排序
	//排序 并放入 copyMenus
	public static List<TreeVO> sort(List<TreeVO> entities) {
		List<TreeVO> copyTrees = Lists.newArrayList();
		Map<Integer,List<TreeVO>> treeMap = classifyByTreeLevel(entities);
		int firstLevel = 1;
		List<TreeVO> treeList = treeMap.get(firstLevel);
		if(treeList != null) {
			for(TreeVO tree: treeList) {
				copyTrees.add(tree);
				List<TreeVO> child = sort(tree,firstLevel+1,treeMap);
				if(child != null && child.size() != 0) {
				   copyTrees.addAll(child);
				   tree.setIsLeaf(Boolean.FALSE);
				}
			}
		}
		return copyTrees;
	}	
	
	public static List<TreeVO> sort(TreeVO parent, int level, Map<Integer,List<TreeVO>> menuMap) {
		List<TreeVO> copyTrees = Lists.newArrayList();
		List<TreeVO> menuList = menuMap.get(level);
		if( menuList != null ) {
			for( TreeVO tree: menuList ) {
				if(tree.getParent().equals(parent.getId())) {
					copyTrees.add(tree);
					 List<TreeVO> child = sort(tree,level+1,menuMap);
					 if( child != null && child.size() != 0 ) {
						 copyTrees.addAll(child);
						 tree.setIsLeaf(Boolean.FALSE);
					 }
				}
			}
		}
		return copyTrees;
	}
}