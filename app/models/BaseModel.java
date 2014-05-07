package models;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import play.db.jpa.Model;

@EntityListeners(BaseModelListener.class)
@MappedSuperclass
public class BaseModel extends Model {
	@Version
	public long version;
	public boolean isDeleted = false;
	@Temporal(TemporalType.TIMESTAMP)
	public Date createTime;
	@Temporal(TemporalType.TIMESTAMP)
	public Date updateTime;
}
