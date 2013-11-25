package com.plainvanilla.organix.engine.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.plainvanilla.organix.engine.dao.ConnectionTypeDAO;
import com.plainvanilla.organix.engine.dao.ObjectTypeDAO;
import com.plainvanilla.organix.engine.model.ConnectionType;
import com.plainvanilla.organix.engine.model.ObjectType;
import com.plainvanilla.organix.engine.model.exception.OrganixIllegalConfigurationException;
import com.plainvanilla.organix.engine.services.DatabaseConfigurationService;


@Service
public class DatabaseConfigurationServiceImpl implements DatabaseConfigurationService {
	
	private ConnectionTypeDAO connectionTypeDao;
	private ObjectTypeDAO objectTypeDao;
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void addObjectType(Integer id, String name) {
		
		if (objectTypeDao.containsObjectTypeId(id)) {
			throw new OrganixIllegalConfigurationException("Object type with id : " + id + " already exists in database");
		}
		
		ObjectType objectType = new ObjectType();
		objectType.setName(name);
		objectType.setTypeNumber(id);

		objectTypeDao.saveOrUpdate(objectType);
	}

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void addConnectionType(Integer id, String sourceRole,
			Integer sourceNodeId, boolean sourceUnique, boolean sourceMandatory,
			String targetRole, Integer targetId, boolean targetUnique,
			boolean targetMandatory) {
		
		if (connectionTypeDao.containsConnectionTypeId(id)) {
			throw new OrganixIllegalConfigurationException("Connection type with id : " + id + " already exists in database");
		}
		
		ConnectionType type = ConnectionType.createInstance(id, sourceRole, sourceNodeId, sourceUnique, sourceMandatory, targetRole, targetId, targetUnique, targetMandatory);

		connectionTypeDao.saveOrUpdate(type);
		
	}
	
	@Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
	public ObjectType getObjectType(Integer id) {
		return objectTypeDao.findByTypeId(id);
	}

	@Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
	public List<ObjectType> getObjectTypeByName(String name) {
		return objectTypeDao.findByName(name);
	}

	@Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
	public ConnectionType getConnectionType(Integer id) {
		return connectionTypeDao.findByTypeId(id);
	}

	@Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
	public List<ConnectionType> getConnectionTypeByName(String name) {
		return connectionTypeDao.findByName(name.toLowerCase());
	}

	@Autowired
	public void setConnectionTypeDao(ConnectionTypeDAO connectionTypeDao) {
		this.connectionTypeDao = connectionTypeDao;
	}

	@Autowired
	public void setObjectTypeDao(ObjectTypeDAO objectTypeDao) {
		this.objectTypeDao = objectTypeDao;
	}
	
	

}
