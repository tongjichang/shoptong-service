package com.haffee.heygay.dao;


import java.util.List;

import com.haffee.heygay.po.User;

public interface IUserDao {
	
	/**
	 * 根据用户名查询用户
	 * @param u
	 * @return
	 */
	public User getUserByUsername(User u);
	

	/**
	 * 获取总页数
	 * @param hql
	 * @return
	 */
	public int getPageCount(int pagesize,String hql);
	
	/**
	 * 查询一页内容
	 * @return
	 */
	public List<Object> getOnePageResult(int pagesize,int pagenow,String hql);
	
	/**
	 * 查询一个实体
	 * @param hql
	 * @return
	 */
	public Object getOneObject(String hql);
	
	/**
	 * 删除一个实体
	 * @param object
	 * @return
	 */
	public boolean doDeleteObject(Object object);
	
	/**
	 * 添加一个实体
	 * @param object
	 * @return
	 */
	public boolean doSaveObject(Object object);
	
	/**
	 * 查询一共多少条记录
	 * @param hql
	 * @return
	 */
	public int getAllNumber(String hql);
	/**
	 * 修改一个实体
	 * @param object
	 * @return
	 */
	public boolean doUpdateObject(Object object);
	
	/**
	 * 查询所有实体
	 * @param hql
	 * @return
	 */
	public List<Object> getAllObject(String hql);
	
	/**
	 * 根据sql返回实体
	 * @param sql
	 * @param obj
	 * @return
	 */
	public List<Object> findBySql(String sql,Object... obj);
	
	/**
	 * 根据sql返回实体
	 * @param sql
	 * @param obj
	 * @return
	 */
	public List<User> findBySqlnew(int pagesize, int pagenow,String sql, Object[] params);
	/**
	 * 根据sql返回实体
	 * @param sql
	 * @param obj
	 * @return
	 */
	public List<Object> findBySqlnew2(int pagesize, int pagenow,String sql, Object[] params);

}
