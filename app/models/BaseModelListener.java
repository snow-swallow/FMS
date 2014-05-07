package models;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class BaseModelListener {

	@PreUpdate
	public static void preUpdate(BaseModel baseModel) {
		baseModel.updateTime = new Date();
	}

	@PrePersist
	public static void prePersist(BaseModel baseModel) {
		Date now = new Date();
		baseModel.createTime = now;
		baseModel.updateTime = now;
	}
}
