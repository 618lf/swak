package com.tmt.matcher;

import java.util.Collections;
import java.util.List;

import org.springframework.util.PathMatcher;

import com.swak.common.utils.Lists;
import com.swak.common.utils.Sets;
import com.swak.http.PathMatcherHelper;
import com.swak.mvc.annotation.RequestMethod;
import com.swak.mvc.method.Match;

public class TestMatcher {

	public static void main(String[] args) {
      List<Match> matches = Lists.newArrayList();
      String lookupPath = "/shop/goods/1223";
      matches.add(new Match(lookupPath, Sets.newHashSet("/shop/goods/{id}"), RequestMethod.GET));
      matches.add(new Match(lookupPath, Sets.newHashSet("/shop/goods/{id}"), RequestMethod.ALL));
      matches.add(new Match(lookupPath, Sets.newHashSet("/shop/goods/{id}"), RequestMethod.GET));
      
      Collections.sort(matches);
      System.out.println(matches);
      
      PathMatcher pathMatcher = PathMatcherHelper.getMatcher();
      System.out.println(pathMatcher.match("/shop/{module}/{id}", lookupPath));
	}
}