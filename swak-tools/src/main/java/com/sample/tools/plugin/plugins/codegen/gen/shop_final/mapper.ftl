<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.dao.${className}Dao" >
  <#assign columnFields><#list table.columns as c>${c.name?upper_case}<#if c_has_next>${', '}</#if><#if c_index !=0 && c_index%7==0 >${'\n\t\t  '}</#if></#list></#assign>
  <#assign javaFields><#list table.columns as c>${r'#{'}${c.javaField}${r'}'}<#if c_has_next>${', '}</#if><#if c_index !=0 && c_index%7==0 >${'\n\t\t  '}</#if></#list></#assign>
  <#assign bathJavaFields><#list table.columns as c>${r'#{item.'}${c.javaField}${r'}'}<#if c_has_next>${', '}</#if><#if c_index !=0 && c_index%7==0 >${'\n\t\t  '}</#if></#list></#assign>
  <#assign updateFields><#list table.columns as c><#if !c.isInsertField>${c.name?upper_case} = ${r'#{'}${c.javaField}${r'}'}<#if c_has_next>${',\n\t\t  '}</#if></#if></#list></#assign>
  <resultMap id="BaseResult" type="${packageName}.entity.${className}"> 
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
  
  <select id="findByCondition" resultMap="BaseResult" parameterType="queryCondition">
   SELECT ${columnFields}
     FROM ${tableName}
    <include refid="COMMON.whereClause"/>
    <if test="orderByClause!= null">ORDER BY ${r'${'}orderByClause${r'}'}</if>
  </select>
  <select id="findByConditionStat" parameterType="queryCondition" resultType="java.lang.Integer">
    SELECT COUNT(1) C FROM ${tableName}
    <include refid="COMMON.whereClause" />
  </select>
  
  <delete id="delete" parameterType="${packageName}.entity.${className}">
   DELETE FROM ${tableName} WHERE ID = ${r'#{id}'}
  </delete>
  
  <insert id="insert" parameterType="${packageName}.entity.${className}">
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
  
  <update id="update" parameterType="${packageName}.entity.${className}">
   UPDATE ${tableName}
      SET ${updateFields}
    WHERE ID = ${r'#{id}'}
  </update>

</mapper>