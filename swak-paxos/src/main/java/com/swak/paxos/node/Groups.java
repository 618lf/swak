package com.swak.paxos.node;

import java.util.List;

import com.swak.utils.Lists;

/**
 * 分组
 * 
 * @author lifeng
 * @date 2020年12月28日 上午11:02:31
 */
public class Groups {

	/**
	 * 分组
	 */
	private List<Group> groups = Lists.newArrayList();

	/**
	 * 是否在范围内
	 * 
	 * @param group
	 * @return
	 */
	public boolean check(int group) {
		if (group < 0 || group >= groups.size()) {
			return false;
		}

		return true;
	}

	/**
	 * 获得分组
	 * 
	 * @param group
	 * @return
	 */
	public Group getGroup(int group) {
		return groups.get(group);
	}
}