package com.haffee.heygay.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.haffee.heygay.po.User;

public class IUserDaoImpl extends HibernateDaoSupport implements IUserDao{

	/**
	 * �����û�����ѯ�û�
	 * @param u
	 * @return
	 */
	public User getUserByUsername(User user) {
		List<User> list = null;
		try {
			list = this.getHibernateTemplate().find("from User u where u.user_name='"+user.getUser_name()+"'");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list.size()!=0)
			return list.get(0);
		else
			return null;
	}
	

	/**
	 * ��ȡ��ҳ��
	 * @param hql
	 * @return
	 */
	public int getPageCount(int pagesize,String hql) {
		int pageCount = 0;
		Query q = this.getSession().createQuery(hql);
		int count = q.list().size();
		 if (count % pagesize == 0) {
	            pageCount = count / pagesize;
	        } else {
	            pageCount = count / pagesize + 1;
	        }

		return pageCount;
	}

	/**
	 * ��ѯһҳ����
	 * @return
	 */
	public List<Object> getOnePageResult(int pagesize, int pagenow, String hql) {
		List<Object> list = new ArrayList<Object>();
		Query q = this.getSession().createQuery(hql);
		q.setFirstResult(pagesize * (pagenow - 1));
        q.setMaxResults(pagesize);
        list = q.list();
		return list;
	}


	/**
	 * ��ѯһ��ʵ��
	 * @param hql
	 * @return
	 */
	public Object getOneObject(String hql) {
		List<Object> list = this.getHibernateTemplate().find(hql);
		if(list.size()!=0)
			return list.get(0);
		else
			return null;
	}



	/**
	 * ɾ��һ��ʵ��
	 * @param object
	 * @return
	 */
	public boolean doDeleteObject(Object object) {
		boolean isSuccess = false;
		try {
			this.getHibernateTemplate().delete(object);
			isSuccess = true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}


	/**
	 * ���һ��ʵ��
	 * @param object
	 * @return
	 */
	public boolean doSaveObject(Object object) {
		boolean isSuccess = false;
		try {
			this.getHibernateTemplate().save(object);
			isSuccess = true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}

	/**
	 * �޸�һ��ʵ��
	 * @param object
	 * @return
	 */
	public boolean doUpdateObject(Object object) {
		boolean isSuccess = false;
		try {
			this.getHibernateTemplate().update(object);
			isSuccess = true;
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}
	

	/**
	 * ��ѯһ����������¼
	 * @param hql
	 * @return
	 */
	public int getAllNumber(String hql) {
		List<Object> list = this.getHibernateTemplate().find(hql);
		return list.size();
	}


	/**
	 * ��ѯ����ʵ��
	 * @param hql
	 * @return
	 */
	public List<Object> getAllObject(String hql) {
		List<Object> list = this.getHibernateTemplate().find(hql);
		return list;
	}


	/**
	 * ����sql��ѯʵ��
	 */
	@Override
	public List<Object> findBySql(String sql, Object... obj) {
		return (List<Object>)this.getHibernateTemplate().find(sql,obj);
	}
	/**
	 * ����sql��ѯʵ��
	 */
	@Override
	public List<User> findBySqlnew(final int pagesize, final int pagenow,final String sql, final Object[] params) {
		    return this.getHibernateTemplate().execute(new HibernateCallback<List<User>>() {  
		        @SuppressWarnings("unchecked")  
		        @Override  
		        public List<User> doInHibernate(Session session)  
		            throws HibernateException, SQLException {  
		            SQLQuery query = session.createSQLQuery(sql);  
//		            query.addScalar("user_name", Hibernate.STRING);   //����ֵ����  
		    		for(int i=0; i<params.length; i++){
		    			query.setParameter(i, params[i]);
			     	}
		    		query.setFirstResult(pagesize * (pagenow - 1));
		    		query.setMaxResults(pagesize);
		            query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		            return (List<User>)query.list();  
		        }  
		    }); 
//		SQLQuery dataQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(sql);
//		for(int i=0; i<params.length; i++){
//			dataQuery.setParameter(i, params[i]);
//		}
//		dataQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//		return (List<Object>)dataQuery.list();
	}

	/**
	 * ����sql��ѯʵ��
	 */
	@Override
	public List<Object> findBySqlnew2(final int pagesize, final int pagenow,final String sql, final Object[] params) {
		    return this.getHibernateTemplate().execute(new HibernateCallback<List<Object>>() {  
		        @SuppressWarnings("unchecked")  
		        @Override  
		        public List<Object> doInHibernate(Session session)  
		            throws HibernateException, SQLException {  
		            SQLQuery query = session.createSQLQuery(sql);  
//		            query.addScalar("user_name", Hibernate.STRING);   //����ֵ����  
		    		for(int i=0; i<params.length; i++){
		    			query.setParameter(i, params[i]);
			     	}
		    		query.setFirstResult(pagesize * (pagenow - 1));
		    		query.setMaxResults(pagesize);
		            query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		            return (List<Object>)query.list();  
		        }  
		    }); 
	}
}
