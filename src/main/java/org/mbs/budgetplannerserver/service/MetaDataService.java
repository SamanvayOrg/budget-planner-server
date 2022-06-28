package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.code.Metadata;
import org.mbs.budgetplannerserver.repository.FunctionGroupRepository;
import org.mbs.budgetplannerserver.repository.MajorHeadGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetaDataService {

    private final MajorHeadGroupRepository majorHeadGroupRepository;
    private final FunctionGroupRepository functionGroupRepository;

    @Autowired
    public MetaDataService(MajorHeadGroupRepository majorHeadGroupRepository, FunctionGroupRepository functionGroupRepository) {
        this.majorHeadGroupRepository = majorHeadGroupRepository;
        this.functionGroupRepository = functionGroupRepository;
    }

    public Metadata getMetadata() {
        return new Metadata(functionGroupRepository.findAll(), majorHeadGroupRepository.findAll());
    }
}
