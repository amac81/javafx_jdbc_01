package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	private SellerDao sellerDao = DaoFactory.createSellerDao();
	
	public List<Seller> findAll(){	
		return sellerDao.findAll();		
	}
	
}	
