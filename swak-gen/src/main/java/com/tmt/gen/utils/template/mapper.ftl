<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.${moduleName}.${subModuleName}.dao.${className}Dao" >
  <#assign columnFields><#list table.columns as c>${c.name?upper_case}<#if c_has_next>${', '}</#if><#if c_index !=0 && c_index%7==0 >${'\n\t\t  '}</#if></#list></#assign>
  <#assign javaFields><#list table.columns as c>${r'#{'}${c.javaField}${r'}'}<#if c_has_next>${', '}</#if><#if c_index !=0 && c_index%7==0 >${'\n\t\t  '}</#if></#list></#assign>
  <#assign bathJavaFields><#list table.columns as c>${r'#{item.'}${c.javaField}${r'}'}<#if c_has_next>${', '}</#if><#if c_index !=0 && c_index%7==0 >${'\n\t\t  '}</#if></#list></#assign>
  <#assign updateFields><#list table.columns as c><#if !c.isInsertField>${c.name?upper_case} = ${r'#{'}${c.javaField}${r'}'}<#if c_has_next>${',\n\t\t  '}</#if></#if></#list></#assign>
  <resultMap id="BaseResult" type="${packageName}.${moduleName}.${subModuleName}.entity.${className}"> 
    <#list table.columns as c>
    <result column="${c.name?upper_case}" property="${c.javaField}" jdbcType="${c.jdbcType?upper_case}"/>
    </#list>
  </resultMap>
  
  <select id="get" resultMap="BaseResult" parameterType="${pk}">
   SELECT ${columnFields}
     FROM ${tableName}
    WHERE ID = ${r'#{id}'}
  </select>
  
  <select id="getAll" resultMap="BaseResult">
   SELECT ${columnFields}
     FROM ${tableName}
  </select>
  
  <#if schemeCategory?contains('treeTable')>
  <select id="findTreeList" resultMap="COMMON.BaseTreeResult">
     SELECT * FROM (
	 SELECT C.ID ID, C.PARENT_ID PARENT, C.PARENT_IDS PARENT_IDS,
	        C.NAME TREE_NAME,
	        '' TREE_CODE,
	        '' TREE_TYPE,
	        C.LEVEL TREE_LEVEL, '' TREE_PATH
	   FROM ${tableName} C
	  WHERE 1 = 1
	  <if test="NAME != null">AND C.NAME = ${r'#{NAME}'}</if> 
	  <if test="IDS != null">AND ${r'#{IDS}'} LIKE CONCAT(CONCAT('%,',C.ID),',%')</if> 
	  )A
  </select>
  <select id="findByCondition" resultMap="BaseResult" parameterType="java.util.Map">
   SELECT ${columnFields}
     FROM ${tableName}
    WHERE LEVEL != 0
    <if test="IDS != null">AND ${r'#{IDS}'} LIKE CONCAT(CONCAT('%,',ID),',%')</if> 
	<if test="NAME != null">AND NAME LIKE CONCAT(CONCAT('%',${r'#{NAME}'}),'%')</if> 
	<if test="PARENT_ID != null">AND PARENT_ID = ${PARENT_ID}</if> 
	<if test="PARENT_IDS != null">AND PARENT_IDS LIKE CONCAT(CONCAT('%,',${r'#{PARENT_IDS}'}), ',%')</if> 
    ORDER BY LEVEL, SORT
  </select>
  <select id="delete${className}Check" parameterType="${packageName}.${moduleName}.${subModuleName}.entity.${className}" resultType="java.lang.Integer">
    SELECT COUNT(1) FROM (
		SELECT 1 FROM SYS_DUAL WHERE EXISTS (SELECT 1 FROM ${tableName} B WHERE B.PARENT_ID=${r'#{id}'}) 
    )A
  </select>
  <#else>
  <select id="findByCondition" resultMap="BaseResult" parameterType="queryCondition">
   SELECT ${columnFields}
     FROM ${tableName}
    <include refid="COMMON.whereClause"/>
    <if test="orderByClause != null">ORDER BY ${r'${'}orderByClause${r'}'}</if>
  </select>
  <select id="findByConditionStat" parameterType="queryCondition" resultType="java.lang.Integer">
    SELECT COUNT(1) C FROM ${tableName}
    <include refid="COMMON.whereClause" />
  </select>
  </#if>
  
  <delete id="delete" parameterType="${packageName}.${moduleName}.${subModuleName}.entity.${className}">
   DELETE FROM ${tableName} WHERE ID = ${r'#{id}'}
  </delete>
  
  <insert id="insert" parameterType="${packageName}.${moduleName}.${subModuleName}.entity.${className}">
   INSERT INTO ${tableName} (${columnFields})
   VALUES (${javaFields})
  </insert>
  
  <insert id="batchInsert" parameterType="java.util.List">
    INSERT INTO ${tableName} (${columnFields})
    VALUES
   <foreach collection="list" item="item" index="index" separator="," >  
    (${bathJavaFields})
   </foreach> 
  </insert>
  
  <update id="update" parameterType="${packageName}.${moduleName}.${subModuleName}.entity.${className}">
   UPDATE ${tableName}
      SET ${updateFields}
    WHERE ID = ${r'#{id}'}
  </update>
  
  <#if table.hasSort>
  <update id="updateSort" parameterType="${packageName}.${moduleName}.${subModuleName}.entity.${className}">
   UPDATE ${tableName}
      SET SORT = ${r'#{sort}'}
    WHERE ID = ${r'#{id}'}
  </update>
  </#if>
  
  <#if table.publishColumn != null>
  <update id="updatePublish" parameterType="${packageName}.${moduleName}.${subModuleName}.entity.${className}">
   UPDATE ${tableName}
      SET ${table.publishColumn.name?upper_case} = ${r'#{'}${table.publishColumn.javaField}${r'}'}
    WHERE ID = ${r'#{id}'}
  </update>
  </#if>
</mapper>