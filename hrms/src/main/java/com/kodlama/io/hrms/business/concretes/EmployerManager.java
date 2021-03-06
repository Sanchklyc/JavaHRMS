package com.kodlama.io.hrms.business.concretes;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kodlama.io.hrms.business.abstracts.EmployerService;
import com.kodlama.io.hrms.business.abstracts.UserService;
import com.kodlama.io.hrms.core.utilities.business.BusinessRules;
import com.kodlama.io.hrms.core.utilities.results.DataResult;
import com.kodlama.io.hrms.core.utilities.results.ErrorDataResult;
import com.kodlama.io.hrms.core.utilities.results.ErrorResult;
import com.kodlama.io.hrms.core.utilities.results.Result;
import com.kodlama.io.hrms.core.utilities.results.SuccessDataResult;
import com.kodlama.io.hrms.core.utilities.results.SuccessResult;
import com.kodlama.io.hrms.dataAccess.abstracts.EmployerDao;
import com.kodlama.io.hrms.entities.concretes.Employer;
import com.kodlama.io.hrms.entities.concretes.User;
import com.kodlama.io.hrms.entities.dtos.EmployerForLoginDto;
import com.kodlama.io.hrms.entities.dtos.EmployerForRegisterDto;

@Service
public class EmployerManager implements EmployerService {

	private EmployerDao employerDao;
	private UserService userService;
	@Autowired
	public EmployerManager(EmployerDao employerDao, UserService userService) {
		super();
		this.employerDao = employerDao;
		this.userService = userService;
	}

	@Override
	public DataResult<List<Employer>> getAll() {
		return new SuccessDataResult<List<Employer>>(this.employerDao.findAll());
	}

	@Override
	public Result register(EmployerForRegisterDto employer) {
		Result businessResults = BusinessRules.run(
				isPasswordsSame(employer.getPassword(), employer.getVerifyPassword()),
				isEmailandWebsiteDomainSame(employer.getEmail(), employer.getWebsite()),
				isEmailAlreadyInUse(employer.getEmail())
				);
		if(businessResults !=  null) return businessResults;
		
		
		// Maybe add AUTOMAPPER?
		User userToRegister = new User(employer.getEmail(), employer.getPassword(),false, UUID.randomUUID().toString());
		userService.add(userToRegister);
		
		Employer employerToRegister = new Employer(userToRegister.getId(),employer.getCompanyName(),employer.getPhone(), false, employer.getWebsite());
		this.employerDao.save(employerToRegister);
		
		return new SuccessResult("???? veren ba??ar??yla kay??t oldu. L??tfen e-posta adresinize g??nderilen linke t??klayarak ??yeli??inizi do??rulay??n.");
	}
	@Override
	public DataResult<Employer> getById(int id) {
		Employer employer = employerDao.getOne(id);
		if(employer==null) return new ErrorDataResult<Employer>();
		return new SuccessDataResult<Employer>(employer);
	}

	@Override
	public DataResult<Employer> login(EmployerForLoginDto employer) {
		Employer employerToLogin = employerDao.getByPhoneAndUser_Password(employer.getPhone(),employer.getPassword());
		if(employerToLogin==null) return new ErrorDataResult<Employer>("Giri?? bilgileriniz hatal??. L??tfen kontrol ediniz.");
		
		Result businessRulesResult = BusinessRules.run(
				isEmployerVerified(employerToLogin));
		if(businessRulesResult != null) return new ErrorDataResult<Employer>(businessRulesResult.getMessage());
		
		return new SuccessDataResult<Employer>(employerToLogin,"Giri?? ba??ar??l??");
	}

	
	private Result isPasswordsSame(String password, String passwordConfirm) {
		if(!password.equals(passwordConfirm)) return new ErrorResult("??ifreleriniz uyu??muyor.");
		return null;
	}
	private Result isEmailandWebsiteDomainSame(String email, String website) {
		String[] emailSplit = email.split("@");
		if(!emailSplit[1].equals(website)) return new ErrorResult("E-posta adresinizin domaini web siteniz ile ayn?? olmal??d??r.");
		return null;
	}
	private Result isEmailAlreadyInUse(String email) {
		if(userService.getByEmail(email).getData() != null) return new ErrorResult("Bu e-posta adresiyle kay??tl?? bir kullan??c?? var.");
		return null;
	}
	private Result isEmployerVerified(Employer employer) {
		if(employer.isVerifiedBySystem()) return new SuccessResult();
		return new ErrorResult("??yeli??iniz hen??z aktifle??tirilmemi??tir. L??tfen ekiplerimizin ??yeli??inizi aktifle??tirmesini bekleyin.");
	}

	

	

}
