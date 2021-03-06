package com.kodlama.io.hrms.business.abstracts;

import java.util.List;

import com.kodlama.io.hrms.core.utilities.results.DataResult;
import com.kodlama.io.hrms.core.utilities.results.Result;
import com.kodlama.io.hrms.entities.concretes.JobPosition;
import com.kodlama.io.hrms.entities.dtos.JobPositionForListingDto;

public interface JobPositionService {
	DataResult<List<JobPositionForListingDto>> getAllWithJobAdCount();
	DataResult<List<JobPosition>> getAll();
    DataResult<JobPosition> getByPositionName(String positionName);
    DataResult<JobPosition> getById(int id);
    Result addNew(JobPosition jobPosition);
}
