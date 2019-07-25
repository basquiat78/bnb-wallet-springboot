package io.basquiat.bnb.domain.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 암호화폐 트랜잭션 정보 엔티티
 * created by basquiat
 *
 */
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "block_transactions", catalog = "")
public class BlockTransactions {
	
	/** unique id */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	/** 트랜잭션 해쉬 */
	@Column(name = "tx_hash")
	private String txHash;
	
	/** block number */
	@Column(name = "block_height")
	private long blockHeight;
	
	/** block hash */
	@Column(name = "block_hash")
	private String blockHash;
	
	/** txType */
	@Column(name = "tx_type")
	private String txType;
	
	/** action type */
	@Column(name = "action")
	private String action;
	
	/** to address */
	@Column(name = "to_address")
	private String toAddress;
	
	/** from address */
	@Column(name = "from_address")
	private String fromAddress;
	
	/** 수량 */
	@Column(name = "value")
	private String value;
	
	/** asset type e.g BNB */
	@Column(name = "tx_asset")
	private String txAsset;
	
	/** txAge */
	@Column(name = "tx_age")
	private long txAge;

	/** txFee */
	@Column(name = "tx_fee")
	private String txFee;
	
	/** memo */
	@Column(name = "memo")
	private String memo;
	
	/** block 생성 시간 */
	@Column(name = "time_stamp")
	private Date timeStamp;
	
	/** 등록 시간 */
	@Temporal(TemporalType.DATE)
	@Column(name = "reg_dttm")
	private Date regDttm;
	
	/** insert할때 현재 시간으로 인서트한다. */
    @PrePersist
    protected void setURegDttm() {
    	regDttm = new Date();
    }

}
