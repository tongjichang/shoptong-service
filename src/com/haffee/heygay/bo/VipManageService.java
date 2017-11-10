package com.haffee.heygay.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.haffee.heygay.dao.IUserDao;

/**
 * 会员管理 适用于店铺管理员
 * @author jacktong
 * @date 2017年7月4日
 *
 */
@Controller
public class VipManageService {

	@Autowired
	private IUserDao dao;
}
