package com.swak.mongo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 资源 管理
 * 
 * @author 李锋
 * @date 2019-04-10
 */
public class Resource implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String catalogId; // 分类， 暂时是 storeId ID
	private String name; // 名称
	private String type; // 类型： pdf, mp4, mp3, jpg, png
	private String url; // 临时地址(不会存储真实的地址)
	private String thumbnail;// 缩略图， 暂时将图片的缩略图存储在数据库中，后台才使用
	private String content; // 是一个json字符串，针对oss 和 vod 有不同的处理方式
	private Integer size; // 大小字节
	private Integer sees; // 查看次数
	private Integer state; // -1: 上传失败: 0 上传中：1 上传成功
	private String storeId; // 商户
	private String storeName; // 商户
	private Date createDate;// 创建时间
	
	// 特殊的字段
	private Boolean boo;
	private Double dou;
	private Long lon;
	private BigDecimal bigd;
	private Integer byt;
	
	public Integer getByt() {
		return byt;
	}
	public void setByt(Integer byt) {
		this.byt = byt;
	}
	public BigDecimal getBigd() {
		return bigd;
	}
	public void setBigd(BigDecimal bigd) {
		this.bigd = bigd;
	}
	public Long getLon() {
		return lon;
	}
	public void setLon(Long lon) {
		this.lon = lon;
	}
	public Double getDou() {
		return dou;
	}
	public void setDou(Double dou) {
		this.dou = dou;
	}
	public Boolean getBoo() {
		return boo;
	}
	public void setBoo(Boolean boo) {
		this.boo = boo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Integer getSees() {
		return sees;
	}
	public void setSees(Integer sees) {
		this.sees = sees;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
