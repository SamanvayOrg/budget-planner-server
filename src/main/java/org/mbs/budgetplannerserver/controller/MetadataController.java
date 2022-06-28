package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.MetadataContract;
import org.mbs.budgetplannerserver.mapper.MetadataContractMapper;
import org.mbs.budgetplannerserver.service.MetaDataService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class MetadataController {
    private final MetaDataService metaDataService;

    public MetadataController(MetaDataService metaDataService) {
        this.metaDataService = metaDataService;
    }


    @RequestMapping(value = "/api/metadata", method = GET)
    @ResponseBody
    public MetadataContract getMetaData() {
        return new MetadataContractMapper().map(metaDataService.getMetadata());
    }
}
