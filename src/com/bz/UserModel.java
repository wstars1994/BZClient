/**
 * 
 * 项目名称:[BZClient]
 * 包:	 [com.bz]
 * 类名称: [UserModel]
 * 类描述: [一句话描述该类的功能]
 * 创建人: [王新晨]
 * 创建时间:[2018年2月7日 下午12:05:41]
 * 修改人: [王新晨]
 * 修改时间:[2018年2月7日 下午12:05:41]
 * 修改备注:[说明本次修改内容]  
 * 版本:	 [v1.0]   
 * 
 */
package com.bz;

import javafx.beans.property.SimpleStringProperty;

public class UserModel {

	private SimpleStringProperty nickName;
	
	public UserModel(String nickName) {
		this.nickName = new SimpleStringProperty(nickName);
	}

	public String getNickName() {
		return nickName.get();
	}
}
