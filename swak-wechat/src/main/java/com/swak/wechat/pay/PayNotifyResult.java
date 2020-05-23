package com.swak.wechat.pay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;

/**
 * 微信支付回调
 *
 * @author: lifeng
 * @date: 2020/4/1 11:30
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class PayNotifyResult extends MchBaseResult {

    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String openid;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String is_subscribe;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String trade_type;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String bank_type;
    @XmlElement
    private Integer total_fee;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String fee_type;
    @XmlElement
    private Integer cash_fee;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String cash_fee_type;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String coupon_fee;
    @XmlElement
    private String coupon_count;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String transaction_id;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String out_trade_no;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String attach;
    @XmlElement
    @XmlJavaTypeAdapter(value = CDataAdapter.class)
    private String time_end;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getIs_subscribe() {
        return is_subscribe;
    }

    public void setIs_subscribe(String is_subscribe) {
        this.is_subscribe = is_subscribe;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getBank_type() {
        return bank_type;
    }

    public void setBank_type(String bank_type) {
        this.bank_type = bank_type;
    }

    public Integer getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(Integer total_fee) {
        this.total_fee = total_fee;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public Integer getCash_fee() {
        return cash_fee;
    }

    public void setCash_fee(Integer cash_fee) {
        this.cash_fee = cash_fee;
    }

    public String getCash_fee_type() {
        return cash_fee_type;
    }

    public void setCash_fee_type(String cash_fee_type) {
        this.cash_fee_type = cash_fee_type;
    }

    public String getCoupon_fee() {
        return coupon_fee;
    }

    public void setCoupon_fee(String coupon_fee) {
        this.coupon_fee = coupon_fee;
    }

    public String getCoupon_count() {
        return coupon_count;
    }

    public void setCoupon_count(String coupon_count) {
        this.coupon_count = coupon_count;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getBankName() {
        if ("ICBC_DEBIT".equals(this.getBank_type())) {
            return "工商银行（借记卡）";
        } else if ("ICBC_DEBIT".equals(this.getBank_type())) {
            return "工商银行（信用卡）";
        } else if ("ABC_DEBIT".equals(this.getBank_type())) {
            return "农业银行（借记卡）";
        } else if ("ABC_CREDIT".equals(this.getBank_type())) {
            return "农业银行 （信用卡）";
        } else if ("PSBC_DEBIT".equals(this.getBank_type())) {
            return "邮政储蓄（借记卡）";
        } else if ("PSBC_CREDIT".equals(this.getBank_type())) {
            return "邮政储蓄 （信用卡）";
        } else if ("CCB_DEBIT".equals(this.getBank_type())) {
            return "建设银行（借记卡）";
        } else if ("CCB_CREDIT".equals(this.getBank_type())) {
            return "建设银行 （信用卡）";
        } else if ("CMB_DEBIT".equals(this.getBank_type())) {
            return "招商银行（借记卡）";
        } else if ("CMB_CREDIT".equals(this.getBank_type())) {
            return "招商银行（信用卡）";
        } else if ("COMM_DEBIT".equals(this.getBank_type())) {
            return "交通银行（借记卡）";
        } else if ("BOC_CREDIT".equals(this.getBank_type())) {
            return "中国银行（信用卡）";
        } else if ("SPDB_DEBIT".equals(this.getBank_type())) {
            return "浦发银行（借记卡）";
        } else if ("SPDB_CREDIT".equals(this.getBank_type())) {
            return "浦发银行（借记卡）";
        } else if ("GDB_DEBIT".equals(this.getBank_type())) {
            return "广发银行（借记卡）";
        } else if ("GDB_CREDIT".equals(this.getBank_type())) {
            return "广发银行（信用卡）";
        } else if ("CMBC_DEBIT".equals(this.getBank_type())) {
            return "民生银行（借记卡）";
        } else if ("CMBC_CREDIT".equals(this.getBank_type())) {
            return "民生银行（信用卡）";
        } else if ("PAB_DEBIT".equals(this.getBank_type())) {
            return "平安银行（借记卡）";
        } else if ("PAB_CREDIT".equals(this.getBank_type())) {
            return "平安银行（信用卡）";
        } else if ("CEB_DEBIT".equals(this.getBank_type())) {
            return "光大银行（借记卡）";
        } else if ("CEB_CREDIT".equals(this.getBank_type())) {
            return "光大银行（信用卡）";
        } else if ("CIB_DEBIT".equals(this.getBank_type())) {
            return "兴业银行（借记卡）";
        } else if ("CIB_CREDIT".equals(this.getBank_type())) {
            return "兴业银行（信用卡）";
        } else if ("CITIC_DEBIT".equals(this.getBank_type())) {
            return "中信银行（借记卡）";
        } else if ("CITIC_CREDIT".equals(this.getBank_type())) {
            return "中信银行（信用卡）";
        } else if ("SDB_CREDIT".equals(this.getBank_type())) {
            return "深发银行（信用卡）";
        } else if ("BOSH_DEBIT".equals(this.getBank_type())) {
            return "上海银行（借记卡）";
        } else if ("BOSH_CREDIT".equals(this.getBank_type())) {
            return "上海银行 （信用卡）";
        } else if ("CRB_DEBIT".equals(this.getBank_type())) {
            return "华润银行（借记卡）";
        } else if ("HZB_DEBIT".equals(this.getBank_type())) {
            return "杭州银行（借记卡）";
        } else if ("HZB_CREDIT".equals(this.getBank_type())) {
            return "杭州银行（信用卡）";
        } else if ("BSB_DEBIT".equals(this.getBank_type())) {
            return "包商银行（借记卡）";
        } else if ("BSB_CREDIT".equals(this.getBank_type())) {
            return "包商银行（信用卡）";
        } else if ("CQB_DEBIT".equals(this.getBank_type())) {
            return "重庆银行（借记卡）";
        } else if ("SDEB_DEBIT".equals(this.getBank_type())) {
            return "顺德农商行（借记卡）";
        } else if ("SZRCB_DEBIT".equals(this.getBank_type())) {
            return "深圳农商银行（借记卡）";
        } else if ("HRBB_DEBIT".equals(this.getBank_type())) {
            return "哈尔滨银行（借记卡）";
        } else if ("BOCD_DEBIT".equals(this.getBank_type())) {
            return "成都银行（借记卡）";
        } else if ("GDNYB_DEBIT".equals(this.getBank_type())) {
            return "南粤银行 （借记卡）";
        } else if ("GDNYB_CREDIT".equals(this.getBank_type())) {
            return "南粤银行 （信用卡）";
        } else if ("GZCB_CREDIT".equals(this.getBank_type())) {
            return "广州银行（信用卡）";
        } else if ("JSB_DEBIT".equals(this.getBank_type())) {
            return "江苏银行（借记卡）";
        } else if ("JSB_CREDIT".equals(this.getBank_type())) {
            return "江苏银行（信用卡）";
        } else if ("NBCB_DEBIT".equals(this.getBank_type())) {
            return "宁波银行（借记卡）";
        } else if ("NBCB_CREDIT".equals(this.getBank_type())) {
            return "宁波银行（信用卡）";
        } else if ("NJCB_DEBIT".equals(this.getBank_type())) {
            return "南京银行（借记卡）";
        } else if ("QDCCB_DEBIT".equals(this.getBank_type())) {
            return "青岛银行（借记卡）";
        } else if ("ZJTLCB_DEBIT".equals(this.getBank_type())) {
            return "浙江泰隆银行（借记卡）";
        } else if ("XAB_DEBIT".equals(this.getBank_type())) {
            return "西安银行（借记卡）";
        } else if ("CSRCB_DEBIT".equals(this.getBank_type())) {
            return "常熟农商银行 （借记卡）";
        } else if ("QLB_DEBIT".equals(this.getBank_type())) {
            return "齐鲁银行（借记卡）";
        } else if ("LJB_DEBIT".equals(this.getBank_type())) {
            return "龙江银行（借记卡）";
        } else if ("HXB_DEBIT".equals(this.getBank_type())) {
            return "华夏银行（借记卡）";
        } else if ("CS_DEBIT".equals(this.getBank_type())) {
            return "测试银行借记卡快捷支付 （借记卡）";
        } else if ("AE_CREDIT".equals(this.getBank_type())) {
            return "AE （信用卡）";
        } else if ("JCB_CREDIT".equals(this.getBank_type())) {
            return "JCB （信用卡）";
        } else if ("MASTERCARD_CREDIT".equals(this.getBank_type())) {
            return "MASTERCARD （信用卡）";
        } else if ("VISA_CREDIT".equals(this.getBank_type())) {
            return "VISA （信用卡）";
        }
        return this.getBank_type();
    }
}
