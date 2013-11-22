package com.plainvanilla.organix.engine.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.plainvanilla.organix.engine.dao.ObjectTypeDAO;
import com.plainvanilla.organix.engine.model.ObjectType;
import com.plainvanilla.organix.engine.model.exception.OrganixModelException;

@Repository
public class HibernateObjectTypeDAO extends AbstractHibernateDAO<ObjectType, Long> implements ObjectTypeDAO {

	@Autowired
	public HibernateObjectTypeDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public boolean containsObjectTypeId(Integer objectId) {
		return (findByTypeId(objectId) != null);
	}

	public ObjectType findByTypeId(Integer objectId) {
		
		ObjectType type = new ObjectType();
		type.setTypeNumber(objectId);
		
		List<ObjectType> types = super.findByExample(type);
		
		if (types.size() > 1) {
			throw new OrganixModelException("More than one Object type with type id : " + objectId + " exists in database!");
		}
		
		if (types.isEmpty()) {
			return null;
		}
		
		return types.get(0);
	}

	public List<ObjectType> findByName(String name) {
		
		ObjectType type = new ObjectType();
		type.setName(name);
			
		return super.findByExample(type);
	}

}
