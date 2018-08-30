package com.guazi.ft.dao.consign.model.po;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guazi.ft.Swagger2;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * UserPO
 *
 * @author shichunyang
 */
@ApiModel(value = "user对象", description = "用户信息")
@Data
public class UserPO {

	@ApiModelProperty(name = "id", value = "用户id", required = true, dataType = Swagger2.DATA_TYPE_INT, example = "0")
	private Integer id;

	@ApiModelProperty(name = "name", value = "姓名", required = true, dataType = Swagger2.DATA_TYPE_STRING, example = "春阳")
	private String name;

	private Integer age;

	@JsonProperty("crt_time")
	private String crtTime;
}
