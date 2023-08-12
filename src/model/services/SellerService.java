package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	private SellerDao SellerDao = DaoFactory.createSellerDao();
	
	public List<Seller> findAll(){	
		return SellerDao.findAll();		
	}
	
	public void saveOrUpdate(Seller Seller) {
		if(Seller.getId() == null) //new Seller
		{
			SellerDao.insert(Seller);
		}
		else
		{
			SellerDao.update(Seller);
		}
	}
	
	public void remove(Seller Seller) {
		SellerDao.deleteById(Seller.getId());
	}
}	
